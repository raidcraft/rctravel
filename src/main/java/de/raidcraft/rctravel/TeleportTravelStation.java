package de.raidcraft.rctravel;

import de.raidcraft.RaidCraft;
import de.raidcraft.rctravel.api.station.AbstractChargeableStation;
import org.bukkit.Location;
import org.bukkit.entity.Player;

/**
 * @author Philip Urban
 */
public class TeleportTravelStation extends AbstractChargeableStation {

    public TeleportTravelStation(String name, Location location, double price) {

        super(name, location, price);
    }

    @Override
    public void travel(Player player) {

        player.teleport(getLocation());
    }

    public boolean isLocked() {

        return RaidCraft.getComponent(RCTravelPlugin.class).getStationLockTask().isLocked(this);
    }

    /**
     * Change schematic
     * @param locked
     */
    public void setLocked(boolean locked) {

        //TODO
    }
}
