package de.raidcraft.rctravel.api.station;

import org.bukkit.Location;
import org.bukkit.entity.Player;

/**
 * @author Philip Urban
 */
public interface Station {

    public String getName();

    public String getPlainName();

    public Location getLocation();

    public void travel(Player player);
}
