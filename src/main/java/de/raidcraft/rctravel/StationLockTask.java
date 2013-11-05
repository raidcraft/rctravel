package de.raidcraft.rctravel;

import de.raidcraft.rctravel.api.station.Station;

import java.util.*;

/**
 * User: Philip Urban
 * Date: 05.11.13
 * Time: 09:38
 */
public class StationLockTask implements Runnable {

    private RCTravelPlugin plugin;
    // cooldown in minutes
    // if cooldown is < 0 then station is unlocked
    private Map<Station, Integer> remainingCooldowns = new HashMap<>();
    private int index = 0;

    public StationLockTask(RCTravelPlugin plugin) {

        this.plugin = plugin;
        reload();
    }

    public void reload() {

        index = 0;
        remainingCooldowns.clear();
    }

    public boolean isLocked(Station station) {

        return !(!remainingCooldowns.containsKey(station) || remainingCooldowns.get(station) < 0);
    }

    public int getRemainingCooldown(Station station) {

        if(!isLocked(station)) return 0;
        return remainingCooldowns.get(station);
    }

    @Override
    public void run() {

        //TODO
    }
}
