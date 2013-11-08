package de.raidcraft.rctravel.comparator;

import de.raidcraft.rctravel.api.station.Station;

import java.util.Comparator;

/**
 * @author Philip Urban
 */
public class AlphabeticComparator implements Comparator<Station> {

    @Override
    public int compare(Station o1, Station o2) {

        return o1.getName().compareTo(o2.getName());
    }
}
