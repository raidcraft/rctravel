package de.raidcraft.rctravel.listener;

import de.raidcraft.RaidCraft;
import de.raidcraft.rctravel.RCTravelPlugin;
import de.raidcraft.rctravel.api.station.SchematicStation;
import de.raidcraft.rctravel.events.StationLockStateChangeEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

/**
 * @author Philip Urban
 */
public class StationListener implements Listener {

    @EventHandler
    public void onStationLockstateChange(StationLockStateChangeEvent event) {

        // lock
        if(event.getNewLockState()) {
            if(event.getGroupedStation().getStation() instanceof SchematicStation) {
                ((SchematicStation) event.getGroupedStation()).changeSchematic(true);
            }
        }
        // unlock
        else {
            // change schematic
            if(event.getGroupedStation().getStation() instanceof SchematicStation) {
                ((SchematicStation) event.getGroupedStation()).changeSchematic(false);
            }
            // travel queued players
            RaidCraft.getComponent(RCTravelPlugin.class).getTravelManager().startTravel(event.getGroupedStation().getStation());
        }
    }
}
