package de.raidcraft.rctravel;

import de.raidcraft.rctravel.api.group.StationGroup;
import de.raidcraft.rctravel.api.station.Station;

/**
 * User: Philip Urban
 * Date: 05.11.13
 * Time: 10:02
 */
public class GroupedStation {

    private Station station;
    private StationGroup stationGroup;

    public GroupedStation(Station station, StationGroup stationGroup) {

        this.station = station;
        this.stationGroup = stationGroup;
    }

    public Station getStation() {

        return station;
    }

    public StationGroup getStationGroup() {

        return stationGroup;
    }
}
