package de.raidcraft.rctravel.api.station;

import org.bukkit.Location;
import org.bukkit.entity.Player;

/**
 * @author Philip Urban
 */
public class SimpleStation extends AbstractStation {

    public SimpleStation(String name, Location location) {

        super(name, location);
    }

    @Override
    public void travel(Player player, Location from, Location to) {
        player.teleport(to);
    }
}
