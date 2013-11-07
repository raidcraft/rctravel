package de.raidcraft.rctravel;

import de.raidcraft.RaidCraft;
import de.raidcraft.api.RaidCraftException;
import de.raidcraft.rctravel.api.station.AbstractChargeableStation;
import de.raidcraft.rctravel.api.station.SchematicStation;
import org.bukkit.Location;
import org.bukkit.entity.Player;

/**
 * @author Philip Urban
 */
public class TeleportTravelStation extends AbstractChargeableStation implements SchematicStation {

    private final static String SCHEMATIC_PREFIX = "tp_station_";
    private Location minPoint;
    private Location maxPoint;

    public TeleportTravelStation(String name, Location location, double price, Location minPoint, Location maxPoint) {

        super(name, location, price);
        this.minPoint = minPoint;
        this.maxPoint = maxPoint;
    }

    @Override
    public void travel(Player player) {

        player.teleport(getLocation());
    }

    @Override
    public Location getMinPoint() {

        return minPoint;
    }

    @Override
    public Location getMaxPoint() {

        return maxPoint;
    }

    public boolean isLocked() {

        return RaidCraft.getComponent(RCTravelPlugin.class).getStationLockTask().isLocked(this);
    }

    public void changeSchematic(boolean locked) {

        String schematicName;
        if(locked) {
            schematicName = getLockedSchematicName();
        }
        else {
            schematicName = getUnlockedSchematicName();
        }

        try {
            RaidCraft.getComponent(RCTravelPlugin.class).getSchematicManager().pasteSchematic(getLocation().getWorld(), schematicName);
        } catch (RaidCraftException e) {
            RaidCraft.LOGGER.warning("[RCTravel] " + e.getMessage());
        }
    }

    public void createSchematic(boolean locked) throws RaidCraftException {

        String schematicName;
        if(locked) {
            schematicName = getLockedSchematicName();
        }
        else {
            schematicName = getUnlockedSchematicName();
        }

        RCTravelPlugin plugin = RaidCraft.getComponent(RCTravelPlugin.class);
        plugin.getSchematicManager().createSchematic(getLocation().getWorld(), minPoint, maxPoint, schematicName);
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
