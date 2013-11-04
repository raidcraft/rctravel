package de.raidcraft.rctravel.api.station;

import org.bukkit.Location;

/**
 * @author Philip Urban
 */
public abstract class AbstractStation implements Station {

    private String name;
    private Location location;

    protected AbstractStation(String name, Location location) {

        this.name = name;
        this.location = location;
    }

    @Override
    public String getName() {

        return name;
    }

    @Override
    public Location getLocation() {

        return location;
    }
}
