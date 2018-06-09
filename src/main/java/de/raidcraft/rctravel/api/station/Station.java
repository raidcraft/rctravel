package de.raidcraft.rctravel.api.station;

import de.raidcraft.api.RaidCraftException;
import org.bukkit.Location;
import org.bukkit.entity.Player;

/**
 * @author Philip Urban
 */
public interface Station extends Comparable<Station> {

    String getDisplayName();

    String getName();

    Location getLocation();

    double getDistance(Location location);

    void travel(Player player, Station station) throws RaidCraftException;

    boolean equals(Object obj);

    int hashCode();
}
