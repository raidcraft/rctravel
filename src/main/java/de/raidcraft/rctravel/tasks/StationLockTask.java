package de.raidcraft.rctravel.tasks;

import de.raidcraft.RaidCraft;
import de.raidcraft.rctravel.GroupedStation;
import de.raidcraft.rctravel.RCTravelPlugin;
import de.raidcraft.rctravel.api.station.Station;
import de.raidcraft.rctravel.events.Dummy;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User: Philip Urban
 * Date: 05.11.13
 * Time: 09:38
 */
public class StationLockTask implements Runnable {

    private RCTravelPlugin plugin;
    private Map<Station, Cooldown> remainingCooldowns = new HashMap<>();
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

        return (remainingCooldowns.containsKey(station) && remainingCooldowns.get(station).isLocked());
    }

    public int getRemainingCooldown(Station station) {

        if(!isLocked(station)) return 0;
        return remainingCooldowns.get(station).getRemainingCooldown();
    }

    @Override
    public void run() {

        List<GroupedStation> groupedStations = plugin.getStationManager().getGroupedStations();
        if(index >= groupedStations.size()) index = 0;
        GroupedStation groupedStation = groupedStations.get(index);
        if(!remainingCooldowns.containsKey(groupedStation.getStation())) {
            remainingCooldowns.put(groupedStation.getStation(), new Cooldown(groupedStation));
        }

        remainingCooldowns.get(groupedStation.getStation()).process();

        index++;
    }

    public class Cooldown {

        // unlock time is negativ, lock time positive
        private int cooldown;
        private GroupedStation groupedStation;

        public Cooldown(GroupedStation groupedStation) {

            this.groupedStation = groupedStation;
            cooldown = -groupedStation.getGroup().getUnlockTime() - 1;
        }

        public boolean isLocked() {

            return (cooldown > 0);
        }

        public int getRemainingCooldown() {

            if(cooldown < 0) return 0;
            return cooldown;
        }

        public void process() {

            boolean wasLocked = isLocked();
            if(wasLocked) {
                cooldown--;
            }
            else if(cooldown < 0) {
                cooldown++;
            }

            // unlock
            if(wasLocked && !isLocked()) {
                cooldown = -groupedStation.getGroup().getUnlockTime();
                RaidCraft.callEvent(new Dummy(groupedStation, false));
            }

            // lock
            if(!wasLocked && cooldown == 0) {
                cooldown = groupedStation.getGroup().getLockTime();
                RaidCraft.callEvent(new Dummy(groupedStation, false));
            }
        }
    }
}
