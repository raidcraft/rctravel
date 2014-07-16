package de.raidcraft.rctravel.npc;

import de.raidcraft.api.npc.RC_Traits;
import net.citizensnpcs.api.persistence.Persist;
import net.citizensnpcs.api.trait.Trait;

/**
 * Created with IntelliJ IDEA.
 * User: Sebastian
 * Date: 17.07.14
 * Time: 01:37
 * To change this template use File | Settings | File Templates.
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
