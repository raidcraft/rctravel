package de.raidcraft.rctravel.manager;

import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.regions.Region;
import de.raidcraft.RaidCraft;
import de.raidcraft.api.Component;
import de.raidcraft.api.RaidCraftException;
import de.raidcraft.rctravel.GroupedStation;
import de.raidcraft.rctravel.RCTravelPlugin;
import de.raidcraft.rctravel.TeleportTravelStation;
import de.raidcraft.rctravel.api.group.StationGroup;
import de.raidcraft.rctravel.api.station.Discoverable;
import de.raidcraft.rctravel.api.station.RegionStation;
import de.raidcraft.rctravel.api.station.Station;
import de.raidcraft.rctravel.tables.TTravelStation;
import de.raidcraft.util.CaseInsensitiveMap;
import de.raidcraft.util.ChunkLocation;
import de.raidcraft.util.LocationUtil;
import de.raidcraft.util.StringUtils;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.*;

/**
 * @author Philip Urban
 */
public class StationManager implements Component {

    private RCTravelPlugin plugin;
    // map: key -> group displayName | value -> list of stations
    private Map<String, List<Station>> cachedStations = new CaseInsensitiveMap<>();
    List<GroupedStation> groupedStations = new ArrayList<>();

    public StationManager(RCTravelPlugin plugin) {

        this.plugin = plugin;
        RaidCraft.registerComponent(StationManager.class, this);
        load();
    }

    private void load() {

        loadStations();
        buildGroupedStations();
    }

    public void reload() {

        cachedStations.clear();
        groupedStations.clear();
        load();

        TravelMasterNPCManager.removeAllDragonGuards();
        TravelMasterNPCManager.spawnAllDragonGuardNPCs(this);
    }

    public void loadStations() {

        List<TTravelStation> tTravelStations = RaidCraft.getDatabase(RCTravelPlugin.class).find(TTravelStation.class).findList();
        for (TTravelStation tTravelStation : tTravelStations) {
            Location location = tTravelStation.getBukkitLocation();
            if (location == null) continue;

            Optional<StationGroup> group = plugin.getGroupManager().getGroup(tTravelStation.getGroupName());
            if (!group.isPresent()) continue;
            double price = group.get().getDefaultPrice();
            if (tTravelStation.getPrice() != 0) {
                price = tTravelStation.getPrice() / 100D;
            }
            TeleportTravelStation station =
                    new TeleportTravelStation(tTravelStation.getName(), location,
                            price, tTravelStation.getBukkitMinPoint(), tTravelStation.getBukkitMaxPoint());
            addToCache(station, group.get());
        }
    }

    public Optional<Station> getStation(String stationName) {

        if (stationName == null) return Optional.empty();

        Station station = null;
        for (List<Station> stList : cachedStations.values()) {
            if (station != null) break;
            for (Station st : stList) {
                if (st.getDisplayName().equalsIgnoreCase(stationName)) {
                    station = st;
                    break;
                }
            }
        }
        if (station == null) {
            for (List<Station> stList : cachedStations.values()) {
                if (station != null) break;
                for (Station st : stList) {
                    if (st.getName().startsWith(StringUtils.formatName(stationName))) {
                        station = st;
                        break;
                    }
                }
            }
        }
        return Optional.ofNullable(station);
    }

    public GroupedStation getGroupedStation(Station station) {

        for (GroupedStation groupedStation : groupedStations) {

            if (groupedStation.getStation().equals(station)) {
                return groupedStation;
            }
        }
        return null;
    }

    public Set<GroupedStation> getGroupedStationsByChunk(ChunkLocation chunkLocation) {

        Set<GroupedStation> gps = new HashSet<>();
        for (GroupedStation groupedStation : groupedStations) {
            ChunkLocation stationChunkLocation = new ChunkLocation(groupedStation.getStation().getLocation());
            if (chunkLocation.equals(stationChunkLocation)) {
                gps.add(groupedStation);
            }
        }
        return gps;
    }

    public List<Station> getDiscoveredStations(StationGroup stationGroup, UUID player) {

        List<Station> stations = new ArrayList<>();
        List<Station> groupStations = cachedStations.get(stationGroup.getPlainName());
        if (groupStations == null) return stations;

        for (Station station : groupStations) {
            if (station instanceof Discoverable) {
                if (!((Discoverable) station).hasDiscovered(player)) continue;
            }
            stations.add(station);
        }
        return stations;
    }

    public List<Station> getAllStations(String group) {

        return cachedStations.get(StringUtils.formatName(group));
    }

    public Station getNearbyStation(Location location, int radius) {

        for (GroupedStation groupedStation : groupedStations) {
            if (!groupedStation.getStation().getLocation().getWorld().getName().equalsIgnoreCase(location.getWorld().getName())) continue;
            if (groupedStation.getStation().getLocation().distance(location) <= radius) {
                return groupedStation.getStation();
            }
        }
        return null;
    }

    public void buildGroupedStations() {

        groupedStations.clear();
        for (Map.Entry<String, List<Station>> entry : cachedStations.entrySet()) {
            Optional<StationGroup> group = plugin.getGroupManager().getGroup(entry.getKey());
            if (!group.isPresent()) continue;
            for (Station station : entry.getValue()) {
                GroupedStation groupedStation = new GroupedStation(station, group.get());
                groupedStations.add(groupedStation);
            }
        }
    }

    public List<GroupedStation> getGroupedStations() {

        return groupedStations;
    }

    public Station createStation(String stationName, Player player, StationGroup stationGroup) throws RaidCraftException {

        // check if station with same displayName already exists
        TTravelStation tTravelStation = RaidCraft.getDatabase(RCTravelPlugin.class)
                .find(TTravelStation.class).where().ieq("name", stationName).findOne();
        if (tTravelStation != null) {
            throw new RaidCraftException("Es existiert bereits eine Station mit diesem Namen!");
        }

        Region selection;
        try {
            selection = plugin.getWorldEdit().getSession(player).getSelection(new BukkitWorld(player.getWorld()));
        } catch (IncompleteRegionException e) {
            throw new RaidCraftException(e.getMessage());
        }
        if (selection == null || selection.getMinimumPoint() == null || selection.getMaximumPoint() == null) {
            throw new RaidCraftException("Es muss das Transportmittel mit WorldEdit selektiert sein!");
        }
        TeleportTravelStation station = new TeleportTravelStation
                (stationName, player.getLocation(),
                        stationGroup.getDefaultPrice(),
                        new Location(player.getWorld(), selection.getMinimumPoint().getBlockX(), selection.getMinimumPoint().getBlockY(), selection.getMinimumPoint().getBlockZ()),
                        new Location(player.getWorld(), selection.getMaximumPoint().getBlockX(), selection.getMaximumPoint().getBlockY(), selection.getMaximumPoint().getBlockZ()));
        plugin.getDynmapManager().addStationMarker(station, stationGroup);

        saveStation(station, stationGroup);
        reload();
        plugin.getStationLockTask().reload();
        return station;
    }

    public void deleteStation(Station station) {

        GroupedStation groupedStation = null;
        for (GroupedStation gs : groupedStations) {
            if (gs.getStation().equals(station)) {
                groupedStation = gs;
                break;
            }
        }

        // delete from database
        TTravelStation tTravelStation = RaidCraft.getDatabase(RCTravelPlugin.class)
                .find(TTravelStation.class).where().ieq("name", station.getDisplayName()).findOne();
        if (tTravelStation != null) {
            RaidCraft.getDatabase(RCTravelPlugin.class).delete(tTravelStation);
        }

        reload();

        // delete dynmap marker
        if (groupedStation != null) {
            plugin.getDynmapManager().removeMarker(groupedStation.getStation(), groupedStation.getStationGroup());
        }
        plugin.reload();
    }

    private void addToCache(Station station, StationGroup stationGroup) {

        if (!cachedStations.containsKey(stationGroup.getPlainName())) {
            cachedStations.put(stationGroup.getPlainName(), new ArrayList<Station>());
        }
        cachedStations.get(stationGroup.getPlainName()).add(station);
    }

    private void saveStation(RegionStation station, StationGroup stationGroup) {

        TTravelStation tTravelStation = new TTravelStation();
        tTravelStation.setName(station.getDisplayName());
        tTravelStation.setGroupName(stationGroup.getPlainName());
        tTravelStation.setWorld(station.getLocation().getWorld().getName());
        tTravelStation.setX((int) (station.getLocation().getX() * 100D));
        tTravelStation.setY((int) (station.getLocation().getY() * 100D));
        tTravelStation.setZ((int) (station.getLocation().getZ() * 100D));
        tTravelStation.setYaw((int) (station.getLocation().getYaw() * 100F));
        tTravelStation.setPitch((int) (station.getLocation().getPitch() * 100F));
        tTravelStation.setPrice(0);
        tTravelStation.setXMin(station.getMinPoint().getBlockX());
        tTravelStation.setYMin(station.getMinPoint().getBlockY());
        tTravelStation.setZMin(station.getMinPoint().getBlockZ());
        tTravelStation.setXMax(station.getMaxPoint().getBlockX());
        tTravelStation.setYMax(station.getMaxPoint().getBlockY());
        tTravelStation.setZMax(station.getMaxPoint().getBlockZ());

        RaidCraft.getDatabase(RCTravelPlugin.class).save(tTravelStation);
    }
}
