package de.raidcraft.rctravel.api.station;

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

    double getPrice(Location start);

    void travel(Player player, Location from, Location to);

    void travelFrom(Player player, Station sourceStation);

    void travelTo(Player player, Station targetStation);

    void travelTo(Player player);

    boolean equals(Object obj);

    int hashCode();
}
