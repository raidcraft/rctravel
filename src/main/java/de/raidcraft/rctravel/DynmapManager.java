package de.raidcraft.rctravel;

import de.raidcraft.RaidCraft;
import de.raidcraft.rctravel.api.group.Group;
import de.raidcraft.rctravel.api.station.Station;
import de.raidcraft.util.StringUtils;
import org.bukkit.Bukkit;
import org.dynmap.DynmapAPI;
import org.dynmap.markers.Marker;
import org.dynmap.markers.MarkerAPI;
import org.dynmap.markers.MarkerSet;

/**
 * Author: Philip
 * Date: 12.12.12 - 22:16
 * Description:
 */
public class DynmapManager {

    private RCTravelPlugin plugin;

    private DynmapAPI api;
    private MarkerAPI markerAPI = null;

    public DynmapManager(RCTravelPlugin plugin) {

        this.plugin = plugin;
        api = (DynmapAPI) Bukkit.getServer().getPluginManager().getPlugin("dynmap");
        if(api == null) {
            return;
        }
        markerAPI = api.getMarkerAPI();
    }

    private MarkerSet getMarkerSet(Group group) {

        MarkerSet markerSet = markerAPI.getMarkerSet(StringUtils.formatName(group.getName()));
        if(markerSet == null) {
            markerSet = markerAPI.createMarkerSet(StringUtils.formatName(group.getName()), group.getName(), null, true);
        }
        return markerSet;
    }

    public void addStationMarker(Station station, Group group) {


        if (markerAPI == null) {
            RaidCraft.LOGGER.warning("Dynmap not installed!");
            return;
        }

        MarkerSet markerSet = getMarkerSet(group);

        removeMarker(station, group);

        markerSet.createMarker(StringUtils.formatName(station.getName())
                , station.getName()
                , station.getLocation().getWorld().getName()
                , station.getLocation().getBlockX()
                , station.getLocation().getBlockY()
                , station.getLocation().getBlockZ()
                , markerAPI.getMarkerIcon(group.getIconName())
                , true);
    }

    public void removeMarker(Station station, Group group) {

        MarkerSet markerSet = getMarkerSet(group);

        for (Marker marker : markerSet.getMarkers()) {
            if (marker.getLabel().equalsIgnoreCase(station.getName()) || marker.getLabel().equalsIgnoreCase(StringUtils.formatName(station.getName()))) {
                marker.deleteMarker();
            }
        }
    }
}
