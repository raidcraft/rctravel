package de.raidcraft.rctravel;

import de.raidcraft.RaidCraft;
import de.raidcraft.rctravel.api.station.AbstractChargeableStation;
import de.raidcraft.rctravel.api.station.SchematicStation;
import org.bukkit.Location;
import org.bukkit.entity.Player;

/**
 * @author Philip Urban
 */
public class TeleportTravelStation extends AbstractChargeableStation implements SchematicStation {

    private final static String SCHEMATIC_PREFIX = "tp_station_";

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

    public void changeSchematic(boolean locked) {

        //TODO
    }

    @Override
    public String getLockedSchematicName() {

        return SCHEMATIC_PREFIX + getPlainName() + "_" + "locked";
    }

    @Override
    public String getUnlockedSchematicName() {

        return SCHEMATIC_PREFIX + getPlainName() + "_" + "unlocked";
    }
}
