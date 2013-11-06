package de.raidcraft.rctravel.api.station;

import de.raidcraft.api.RaidCraftException;
import org.bukkit.entity.Player;

/**
 * @author Philip Urban
 */
public interface SchematicStation {

    public void changeSchematic(boolean locked);

    public String getLockedSchematicName();

    public String getUnlockedSchematicName();

    public void createSchematic(Player player, boolean locked) throws RaidCraftException;
}
