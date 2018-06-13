package de.raidcraft.rctravel.util;

import de.raidcraft.RaidCraft;
import de.raidcraft.rctravel.RCTravelPlugin;
import de.raidcraft.rctravel.api.group.StationGroup;
import de.raidcraft.rctravel.api.station.Station;
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
        if (api == null) {
            return;
        }
        markerAPI = api.getMarkerAPI();
    }

    private MarkerSet getMarkerSet(StationGroup stationGroup) {

        MarkerSet markerSet = markerAPI.getMarkerSet(stationGroup.getPlainName());
        if (markerSet == null) {
            markerSet = markerAPI.createMarkerSet(stationGroup.getPlainName(), stationGroup.getName(), null, true);
        }
        return markerSet;
    }

    public void addStationMarker(Station station, StationGroup stationGroup) {


        if (markerAPI == null) {
            RaidCraft.LOGGER.warning("Dynmap not installed!");
            return;
        }

        MarkerSet markerSet = getMarkerSet(stationGroup);

        removeMarker(station, stationGroup);

        markerSet.createMarker(station.getName()
                , station.getDisplayName()
                , station.getLocation().getWorld().getName()
                , station.getLocation().getBlockX()
                , station.getLocation().getBlockY()
                , station.getLocation().getBlockZ()
                , markerAPI.getMarkerIcon(stationGroup.getIconName())
                , true);
    }

    public void removeMarker(Station station, StationGroup stationGroup) {

        MarkerSet markerSet = getMarkerSet(stationGroup);

        for (Marker marker : markerSet.getMarkers()) {
            if (marker.getLabel().equalsIgnoreCase(station.getDisplayName()) || marker.getLabel().equalsIgnoreCase(station.getName())) {
                marker.deleteMarker();
            }
        }
    }
}
