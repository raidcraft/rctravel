package de.raidcraft.rctravel.api.group;

/**
 * A group clusters travelTo stations together.
 * A player can only travelTo between stations in the same group.
 * A group also defines the vehicle and base price for all contained stations.
 *
 * @author Philip Urban
 */
public interface StationGroup {

    String getName();

    String getPlainName();

    String getIconName();

    String getVehicleName();

    String getConversationName();

    double getDefaultPrice();

    int getLockTime();

    int getUnlockTime();
}
