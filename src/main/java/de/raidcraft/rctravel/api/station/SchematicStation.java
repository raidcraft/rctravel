package de.raidcraft.rctravel.api.station;

import de.raidcraft.api.RaidCraftException;
import org.bukkit.Location;

/**
 * @author Philip Urban
 */
public interface SchematicStation extends Station {

    public Location getMinPoint();

    public Location getMaxPoint();

    public void changeSchematic(boolean locked);

    public String getLockedSchematicName();

    public String getUnlockedSchematicName();

    public void createSchematic(boolean locked) throws RaidCraftException;
}
