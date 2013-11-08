package de.raidcraft.rctravel;

import de.raidcraft.rctravel.api.station.Station;

/**
 * @author Philip Urban
 */
public class Journey {

    private Station station;
    private Station target;

    public Journey(Station station, Station target) {

        this.station = station;
        this.target = target;
    }

    public Station getStation() {

        return station;
    }

    public Station getTarget() {

        return target;
    }
}
