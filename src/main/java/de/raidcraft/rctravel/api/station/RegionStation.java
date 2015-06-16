package de.raidcraft.rctravel.api.station;

import de.raidcraft.api.RaidCraftException;
import org.bukkit.Location;

/**
 * @author Philip Urban
 */
public interface RegionStation extends Station {

    public Location getMinPoint();

    public Location getMaxPoint();
}
