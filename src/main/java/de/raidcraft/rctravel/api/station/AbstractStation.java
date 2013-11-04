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

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AbstractStation that = (AbstractStation) o;

        return name.equals(that.name);

    }

    @Override
    public int hashCode() {

        return name.hashCode();
    }
}
