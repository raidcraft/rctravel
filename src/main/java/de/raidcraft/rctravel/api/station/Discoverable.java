package de.raidcraft.rctravel.api.station;

import java.util.UUID;

/**
 * @author Philip Urban
 */
public interface Discoverable {

    public boolean hasDiscovered(UUID player);

    public void setDiscovered(UUID player, boolean discovered);
}
