package de.raidcraft.rctravel;

import de.raidcraft.rctravel.api.group.Group;
import de.raidcraft.rctravel.api.station.Station;

/**
 * User: Philip Urban
 * Date: 05.11.13
 * Time: 10:02
 */
public class GroupedStation {

    private Station station;
    private Group group;

    public GroupedStation(Station station, Group group) {
        this.station = station;
        this.group = group;
    }

    public Station getStation() {
        return station;
    }

    public Group getGroup() {
        return group;
    }
}
