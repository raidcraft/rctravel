package de.raidcraft.rctravel.api.station;

/**
 * @author Philip Urban
 */
public interface Chargeable {

    public default boolean isFree() {

        return getPrice() <= 0;
    }

    public double getPrice();

    public double getPrice(int distance);
}
