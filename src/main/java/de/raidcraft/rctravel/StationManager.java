package de.raidcraft.rctravel;

import de.raidcraft.RaidCraft;
import de.raidcraft.api.RaidCraftException;
import de.raidcraft.rctravel.api.group.Group;
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

            Group group = plugin.getGroupManager().getGroup(tTravelStation.getGroup());
            if(group == null) continue;
            double price = group.getDefaultPrice();
            if(tTravelStation.getPrice() == 0) {
                price = tTravelStation.getPrice() / 100D;
            }
            TeleportTravelStation station = new TeleportTravelStation(tTravelStation.getName(), location, price);
            addToCache(station, group);
        }
    }

    public Station getStation(Group group, String stationName) {

        Station station = null;
        for(Station st : getGroupStations(group.getPlainName())) {
            if(station.getName().equalsIgnoreCase(stationName)) station = st;
        }
        if(station == null) {
            for(Station st : getGroupStations(group.getPlainName())) {
                if(station.getName().toLowerCase().startsWith(stationName.toLowerCase())) station = st;
            }
        }
        return station;
    }

    public List<Station> getGroupStations(String group) {

        return cachedStations.get(StringUtils.formatName(group));
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
                .find(TTravelStation.class).where().ieq("group", group.getPlainName()).ieq("name", stationName).findUnique();
        if(tTravelStation != null) {
            throw new RaidCraftException("Es existiert bereits eine Station mit diesem Namen!");
        }

        TeleportTravelStation station = new TeleportTravelStation(stationName, player.getLocation(), group.getDefaultPrice());
        plugin.getDynmapManager().addStationMarker(station, group);
        station.createSchematic(player, false);
        addToCache(station, group);
        saveStation(station, group);
    }

    private void addToCache(Station station, Group group) {

        if(!cachedStations.containsKey(group.getPlainName())) {
            cachedStations.put(group.getPlainName(), new ArrayList<Station>());
        }
        cachedStations.get(group.getPlainName()).add(station);
    }

    private void saveStation(Station station, Group group) {

        TTravelStation tTravelStation = new TTravelStation();
        tTravelStation.setName(station.getName());
        tTravelStation.setGroup(group.getPlainName());
        tTravelStation.setWorld(station.getLocation().getWorld().getName());
        tTravelStation.setX((int)(station.getLocation().getX() * 100D));
        tTravelStation.setY((int)(station.getLocation().getY() * 100D));
        tTravelStation.setZ((int)(station.getLocation().getZ() * 100D));
        tTravelStation.setYaw((int)(station.getLocation().getYaw() * 100F));
        tTravelStation.setPitch((int)(station.getLocation().getPitch() * 100F));
        tTravelStation.setPrice(0);

        RaidCraft.getDatabase(RCTravelPlugin.class).save(tTravelStation);
    }

    public void reload() {

        loadStations();
        buildGroupedStations();
    }
}
