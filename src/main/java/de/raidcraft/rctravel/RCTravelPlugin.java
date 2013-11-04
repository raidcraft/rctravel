package de.raidcraft.rctravel;

import de.raidcraft.api.BasePlugin;
import de.raidcraft.rctravel.conversations.TravelCommands;
import de.raidcraft.rctravel.tables.TTravelStation;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Philip Urban
 */
public class RCTravelPlugin extends BasePlugin {

    private StationManager stationManager;
    private GroupManager groupManager;
    private DynmapManager dynmapManager;

    @Override
    public void enable() {

        registerCommands(TravelCommands.class);

        stationManager = new StationManager(this);
        groupManager = new GroupManager(this);
        dynmapManager = new DynmapManager(this);
    }

    @Override
    public void disable() {
    }

    @Override
    public List<Class<?>> getDatabaseClasses() {

        List<Class<?>> databases = new ArrayList<>();
        databases.add(TTravelStation.class);
        return databases;
    }

    public StationManager getStationManager() {

        return stationManager;
    }

    public GroupManager getGroupManager() {

        return groupManager;
    }

    public DynmapManager getDynmapManager() {

        return dynmapManager;
    }
}
