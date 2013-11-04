package de.raidcraft.rctravel;

import de.raidcraft.RaidCraft;
import de.raidcraft.rctravel.api.station.Station;
import de.raidcraft.rctravel.tables.TTravelStation;
import de.raidcraft.util.CaseInsensitiveMap;
import de.raidcraft.util.StringUtils;
import org.bukkit.Location;

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

    public StationManager(RCTravelPlugin plugin) {

        this.plugin = plugin;
        loadStations();
    }

    public void loadStations() {

        cachedStations.clear();

        List<TTravelStation> tTravelStations = RaidCraft.getDatabase(RCTravelPlugin.class).find(TTravelStation.class).findList();
        for(TTravelStation tTravelStation : tTravelStations) {
            Location location = tTravelStation.getBukkitLocation();
            if(location == null) continue;

            TeleportTravelStation station = new TeleportTravelStation(tTravelStation.getName(), location, tTravelStation.getPrice() / 100D);
            String group = StringUtils.formatName(tTravelStation.getGroup());
            if(!cachedStations.containsKey(group)) {
                cachedStations.put(group, new ArrayList<Station>());
            }
            cachedStations.get(group).add(station);
        }
    }

    public List<Station> getGroupStations(String group) {

        return cachedStations.get(StringUtils.formatName(group));
    }
}
