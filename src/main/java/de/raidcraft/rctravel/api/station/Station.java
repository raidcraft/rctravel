package de.raidcraft.rctravel.api.station;

import de.raidcraft.api.RaidCraftException;
import org.bukkit.Location;
import org.bukkit.entity.Player;

/**
 * @author Philip Urban
 */
public interface Station {

    public String getDisplayName();

    public String getName();

    public Location getLocation();

    public void travel(Player player, Station station) throws RaidCraftException;

    public boolean equals(Object obj);

    public int hashCode();
}
