package de.raidcraft.rctravel.api.station;

import org.bukkit.Location;

/**
 * @author Philip Urban
 */
public abstract class AbstractChargeableStation extends AbstractStation implements Chargeable {

    private double price;

    protected AbstractChargeableStation(String name, Location location, double price) {

        super(name, location);
        this.price = price;
    }

    @Override
    public double getPrice() {

        return price;
    }
}
