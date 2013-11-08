package de.raidcraft.rctravel.api.station;

/**
 * @author Philip Urban
 */
public interface Chargeable {

    public double getPrice();

    public double getPrice(int distance);
}
