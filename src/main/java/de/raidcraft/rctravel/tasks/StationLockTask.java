package de.raidcraft.rctravel.tasks;

import de.raidcraft.RaidCraft;
import de.raidcraft.rctravel.GroupedStation;
import de.raidcraft.rctravel.RCTravelPlugin;
import de.raidcraft.rctravel.api.station.Station;
import de.raidcraft.rctravel.events.StationLockStateChangeEvent;

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

    @Override
    public void run() {

        List<GroupedStation> groupedStations = plugin.getStationManager().getGroupedStations();
        // add all stations time shifted to map (preventing laggs)
        if(index < groupedStations.size()) {
            GroupedStation groupedStation = groupedStations.get(index);
            if(!remainingCooldowns.containsKey(groupedStation.getStation())) {
                remainingCooldowns.put(groupedStation.getStation(), new Cooldown(groupedStation));
            }
            index++;
        }

        for(GroupedStation gs : groupedStations) {
            Cooldown cooldown = remainingCooldowns.get(gs.getStation());
            if(cooldown == null) continue;

            cooldown.process();
            if(!cooldown.isLocked()) {
                // travel queued players
                RaidCraft.getComponent(RCTravelPlugin.class).getTravelManager().startTravel(cooldown.getGroupedStation().getStation());
            }
        }

    }

    public class Cooldown {

        private boolean locked;
        private long time;
        private GroupedStation groupedStation;

        public Cooldown(GroupedStation groupedStation) {

            this.groupedStation = groupedStation;
            locked = false;
            time = System.currentTimeMillis();
        }

        public GroupedStation getGroupedStation() {

            return groupedStation;
        }

        public boolean isLocked() {

            return locked;
        }

        public int getRemainingTime(int total) {

            return (int)(total - ((System.currentTimeMillis() - time) / 1000));
        }

        public void process() {

            if(locked) {
                if(getRemainingTime(groupedStation.getGroup().getLockTime()) < 0) {
                    locked = false;
                    time = System.currentTimeMillis();
                    RaidCraft.callEvent(new StationLockStateChangeEvent(groupedStation, false));
                }
            }
            else {
                if(getRemainingTime(groupedStation.getGroup().getUnlockTime()) < 0) {
                    locked = true;
                    time = System.currentTimeMillis();
                    RaidCraft.callEvent(new StationLockStateChangeEvent(groupedStation, true));
                }
            }
        }
    }
}
