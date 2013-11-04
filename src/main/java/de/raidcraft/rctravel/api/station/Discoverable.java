package de.raidcraft.rctravel.api.station;

/**
 * @author Philip Urban
 */
public interface Discoverable {

    public boolean hasDiscovered(String player);

    public void setDiscovered(String player, boolean discovered);
}
