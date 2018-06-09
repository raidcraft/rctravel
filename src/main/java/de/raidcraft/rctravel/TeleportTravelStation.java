package de.raidcraft.rctravel;

import de.raidcraft.RaidCraft;
import de.raidcraft.rctravel.api.station.AbstractStation;
import de.raidcraft.rctravel.api.station.Chargeable;
import de.raidcraft.rctravel.api.station.RegionStation;
import de.raidcraft.rctravel.api.station.Station;
import de.raidcraft.rctravel.util.RegionUtil;
import de.raidcraft.tables.RcLogLevel;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

/**
 * @author Philip Urban
 */
public class TeleportTravelStation extends AbstractStation implements Chargeable, RegionStation {

    private final static String SCHEMATIC_PREFIX = "tp_station_";
    private Location minPoint;
    private Location maxPoint;
    private double price;

    public TeleportTravelStation(String name, Location location, double price, Location minPoint, Location maxPoint) {

        super(name, location);
        this.minPoint = minPoint;
        this.maxPoint = maxPoint;
        this.price = price;
    }

    @Override
    public void travelFrom(Player player, Station sourceStation) {

        if (sourceStation instanceof TeleportTravelStation) {
            sourceStation.travelTo(player, this);
            return;
        } else {
            travel(player, sourceStation.getLocation(), getLocation());
        }

        RaidCraft.log("Der Spieler befindet sich nicht am Abfahrtsort!", "TeleportTravelStation", RcLogLevel.WARNING);
    }

    @Override
    public void travelTo(Player player, Station targetStation) {

        // check if player is inside of transport region
        if (RegionUtil.isInsideRegion(player, minPoint, maxPoint)) {
            player.teleport(targetStation.getLocation());
            player.sendMessage(ChatColor.GREEN + "Du bist an deinem Reiseziel angekommen.");
            return;
        }

        RaidCraft.log("Der Spieler befindet sich nicht am Abfahrtsort!", "TeleportTravelStation", RcLogLevel.WARNING);
    }

    @Override
    public void travel(Player player, Location from, Location to) {

    }

    public boolean isLocked() {

        return RaidCraft.getComponent(RCTravelPlugin.class).getStationLockTask().isLocked(this);
    }

    @Override
    public Location getMinPoint() {
        return minPoint;
    }

    @Override
    public Location getMaxPoint() {
        return maxPoint;
    }

    @Override
    public double getPrice() {

        return price;
    }

    @Override
    public double getPrice(int distance) {

        return price;
    }
}
