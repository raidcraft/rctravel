package de.raidcraft.rctravel.api.station;

/**
 * @author Philip Urban
 */
public interface Chargeable {

    default boolean isFree() {

        return getPrice() <= 0;
    }

    double getPrice();

    double getPrice(int distance);
}
