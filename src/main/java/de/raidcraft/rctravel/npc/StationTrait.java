package de.raidcraft.rctravel.npc;

import de.raidcraft.api.npc.RC_Traits;
import net.citizensnpcs.api.persistence.Persist;
import net.citizensnpcs.api.trait.Trait;

/**
 * @author Dragonfire
 */
public class StationTrait extends Trait {
    @Persist
    public String stationName;

    public StationTrait() {
        super(RC_Traits.STATION);
    }

    public void setStationName(String stationName) {
        this.stationName = stationName;
    }

    public String getStationName() {
        return stationName;
    }
}
