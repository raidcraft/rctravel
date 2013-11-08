package de.raidcraft.rctravel.api.station;

import de.raidcraft.api.RaidCraftException;
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
    public void travel(Player player, Station station) throws RaidCraftException {

        player.teleport(station.getLocation());
    }
}
