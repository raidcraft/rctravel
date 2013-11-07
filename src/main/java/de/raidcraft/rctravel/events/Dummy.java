package de.raidcraft.rctravel.events;

import de.raidcraft.rctravel.GroupedStation;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * @author Philip Urban
 */
public class Dummy extends Event {

    private GroupedStation groupedStation;
    private boolean newLockState;

    public Dummy(GroupedStation groupedStation, boolean newLockState) {

        this.groupedStation = groupedStation;
        this.newLockState = newLockState;
    }

    public GroupedStation getGroupedStation() {

        return groupedStation;
    }

    public boolean getNewLockState() {

        return newLockState;
    }

    // event handler stuff

    private static final HandlerList handlers = new HandlerList();

    public HandlerList getHandlers() {

        return handlers;
    }

    public static HandlerList getHandlerList() {

        return handlers;
    }
}
