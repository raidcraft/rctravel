package de.raidcraft.rctravel;

import com.sk89q.worldedit.bukkit.selections.Selection;
import de.raidcraft.RaidCraft;
import de.raidcraft.api.RaidCraftException;
import de.raidcraft.rcconversations.npc.ConversationsTrait;
import de.raidcraft.rctravel.api.group.Group;
import de.raidcraft.rctravel.api.station.Discoverable;
import de.raidcraft.rctravel.api.station.SchematicStation;
import de.raidcraft.rctravel.api.station.Station;
import de.raidcraft.rctravel.tables.TTravelStation;
import de.raidcraft.util.CaseInsensitiveMap;
import de.raidcraft.util.StringUtils;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Philip Urban
 */
public class StationManager {

    private RCTravelPlugin plugin;
    // map: key -> group name | value -> list of stations
    private Map<String, List<Station>> cachedStations = new CaseInsensitiveMap<>();
    List<GroupedStation> groupedStations = new ArrayList<>();

    public StationManager(RCTravelPlugin plugin) {

        this.plugin = plugin;
        reload();
    }

    public void loadStations() {

        cachedStations.clear();

        List<TTravelStation> tTravelStations = RaidCraft.getDatabase(RCTravelPlugin.class).find(TTravelStation.class).findList();
        for(TTravelStation tTravelStation : tTravelStations) {
            Location location = tTravelStation.getBukkitLocation();
            if(location == null) continue;

            Group group = plugin.getGroupManager().getGroup(tTravelStation.getGroupName());
            if(group == null) continue;
            double price = group.getDefaultPrice();
            if(tTravelStation.getPrice() != 0) {
                price = tTravelStation.getPrice() / 100D;
            }
            TeleportTravelStation station =
                    new TeleportTravelStation(tTravelStation.getName(), location, price, tTravelStation.getBukkitMinPoint(), tTravelStation.getBukkitMaxPoint());
            addToCache(station, group);
        }
    }

    public Station getStation(String stationName) {

        Station station = null;
        for(List<Station> stList : cachedStations.values()) {
            if(station != null) break;
            for(Station st : stList) {
                if(st.getName().equalsIgnoreCase(stationName)) {
                    station = st;
                    break;
                }
            }
        }
        if(station == null) {
            for(List<Station> stList : cachedStations.values()) {
                if(station != null) break;
                for(Station st : stList) {
                    if(st.getName().toLowerCase().startsWith(stationName.toLowerCase())) {
                        station = st;
                        break;
                    }
                }
            }
        }
        return station;
    }

    public GroupedStation getGroupedStation(Station station) {

        for(GroupedStation groupedStation : groupedStations) {

            if(groupedStation.getStation().equals(station)) {
                return groupedStation;
            }
        }
        return null;
    }

    public List<Station> getDiscoveredStations(Group group, String player) {

        List<Station> stations = new ArrayList<>();
        List<Station> groupStations = cachedStations.get(group.getPlainName());
        if(groupStations == null) return stations;

        for(Station station : groupStations) {
            if(station instanceof Discoverable) {
                if(!((Discoverable) station).hasDiscovered(player)) continue;
            }
            stations.add(station);
        }
        return stations;
    }

    public List<Station> getGroupStations(String group) {

        return cachedStations.get(StringUtils.formatName(group));
    }

    public Station getNearbyStation(Location location, int radius) {

        for(GroupedStation groupedStation : groupedStations) {
            if(!groupedStation.getStation().getLocation().getWorld().getName().equalsIgnoreCase(location.getWorld().getName())) continue;
            if(groupedStation.getStation().getLocation().distance(location) <= radius) {
                return groupedStation.getStation();
            }
        }
        return null;
    }

    public void buildGroupedStations() {

        groupedStations.clear();
        for(Map.Entry<String, List<Station>> entry : cachedStations.entrySet()) {
            Group group = plugin.getGroupManager().getGroup(entry.getKey());
            if(group == null) continue;
            for(Station station : entry.getValue()) {
                GroupedStation groupedStation = new GroupedStation(station, group);
                groupedStations.add(groupedStation);
            }
        }
    }

    public List<GroupedStation> getGroupedStations() {

        return groupedStations;
    }

    public void createStation(String stationName, Player player, Group group) throws RaidCraftException {

        // check if station with same name already exists
        TTravelStation tTravelStation = RaidCraft.getDatabase(RCTravelPlugin.class)
                .find(TTravelStation.class).where().ieq("name", stationName).findUnique();
        if(tTravelStation != null) {
            throw new RaidCraftException("Es existiert bereits eine Station mit diesem Namen!");
        }

        Selection selection = plugin.getWorldEdit().getSelection(player);
        if(selection == null || selection.getMinimumPoint() == null || selection.getMaximumPoint() == null) {
            throw new RaidCraftException("Es muss das Transportmittel mit WorldEdit selektiert sein!");
        }

        TeleportTravelStation station = new TeleportTravelStation(stationName, player.getLocation(), group.getDefaultPrice(), selection.getMinimumPoint(), selection.getMaximumPoint());
        plugin.getDynmapManager().addStationMarker(station, group);
        station.createSchematic(false);
        ConversationsTrait.create(station.getLocation(), group.getConversationName(), "Reiseleiter", false);
        saveStation(station, group);
        reload();
    }

    public void deleteStation(Station station) throws RaidCraftException {

        GroupedStation groupedStation = null;
        for(GroupedStation gs : groupedStations) {
            if(gs.getStation().equals(station)) {
                groupedStation = gs;
                break;
            }
        }

        // delete from database
        TTravelStation tTravelStation = RaidCraft.getDatabase(RCTravelPlugin.class)
                .find(TTravelStation.class).where().ieq("name", station.getName()).findUnique();
        if(tTravelStation != null) {
            RaidCraft.getDatabase(RCTravelPlugin.class).delete(tTravelStation);
        }

        reload();

        // delete schematics if schematic station
        if(station instanceof SchematicStation) {
            SchematicStation schematicStation = (SchematicStation)station;
            plugin.getSchematicManager().deleteSchematic(station.getLocation().getWorld(), schematicStation.getUnlockedSchematicName());
            plugin.getSchematicManager().deleteSchematic(station.getLocation().getWorld(), schematicStation.getLockedSchematicName());
        }

        // delete dynmap marker
        if(groupedStation != null) {
            plugin.getDynmapManager().removeMarker(groupedStation.getStation(), groupedStation.getGroup());
        }
        plugin.reload();
    }

    private void addToCache(Station station, Group group) {

        if(!cachedStations.containsKey(group.getPlainName())) {
            cachedStations.put(group.getPlainName(), new ArrayList<Station>());
        }
        cachedStations.get(group.getPlainName()).add(station);
    }

    private void saveStation(SchematicStation station, Group group) {

        TTravelStation tTravelStation = new TTravelStation();
        tTravelStation.setName(station.getName());
        tTravelStation.setGroupName(group.getPlainName());
        tTravelStation.setWorld(station.getLocation().getWorld().getName());
        tTravelStation.setX((int)(station.getLocation().getX() * 100D));
        tTravelStation.setY((int)(station.getLocation().getY() * 100D));
        tTravelStation.setZ((int)(station.getLocation().getZ() * 100D));
        tTravelStation.setYaw((int)(station.getLocation().getYaw() * 100F));
        tTravelStation.setPitch((int)(station.getLocation().getPitch() * 100F));
        tTravelStation.setPrice(0);
        tTravelStation.setXMin(station.getMinPoint().getBlockX());
        tTravelStation.setYMin(station.getMinPoint().getBlockY());
        tTravelStation.setZMin(station.getMinPoint().getBlockZ());
        tTravelStation.setXMax(station.getMaxPoint().getBlockX());
        tTravelStation.setYMax(station.getMaxPoint().getBlockY());
        tTravelStation.setZMax(station.getMaxPoint().getBlockZ());

        RaidCraft.getDatabase(RCTravelPlugin.class).save(tTravelStation);
    }

    public void reload() {

        loadStations();
        buildGroupedStations();
    }
}
