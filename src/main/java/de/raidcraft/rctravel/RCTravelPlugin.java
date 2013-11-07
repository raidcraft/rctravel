package de.raidcraft.rctravel;

import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import de.raidcraft.api.BasePlugin;
import de.raidcraft.rctravel.commands.TravelCommands;
import de.raidcraft.rctravel.tables.TTravelStation;
import de.raidcraft.rctravel.tasks.StationLockTask;
import de.raidcraft.rctravel.util.DynmapManager;
import de.raidcraft.rctravel.util.SchematicManager;
import de.raidcraft.rctravel.util.WorldGuardManager;
import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Philip Urban
 */
public class RCTravelPlugin extends BasePlugin {

    private StationManager stationManager;
    private GroupManager groupManager;
    private DynmapManager dynmapManager;
    private StationLockTask stationLockTask;
    private SchematicManager schematicManager;
    private WorldGuardManager worldGuardManager;
    private WorldEditPlugin worldEdit;
    private WorldGuardPlugin worldGuard;
    private TravelManager travelManager;

    @Override
    public void enable() {

        registerCommands(TravelCommands.class);

        //XXX order is important!
        stationManager = new StationManager(this);
        groupManager = new GroupManager(this);
        dynmapManager = new DynmapManager(this);
        stationLockTask = new StationLockTask(this);
        schematicManager = new SchematicManager(this);
        worldEdit = (WorldEditPlugin)Bukkit.getServer().getPluginManager().getPlugin("WorldEdit");
        worldGuard = (WorldGuardPlugin)Bukkit.getServer().getPluginManager().getPlugin("WorldGuard");
        worldGuardManager = new WorldGuardManager(this, worldGuard);
        travelManager = new TravelManager(this);

        // start station schedule calculation
        // every 5 seconds one station will be checked
        Bukkit.getScheduler().runTaskTimer(this, stationLockTask, 0, 5 * 20);
    }

    @Override
    public void disable() {
    }

    @Override
    public void reload() {

        //XXX order is important!
        getGroupManager().reload();
        getStationManager().reload();
        getStationLockTask().reload();
        getTravelManager().reload();
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

    public StationLockTask getStationLockTask() {

        return stationLockTask;
    }

    public SchematicManager getSchematicManager() {

        return schematicManager;
    }

    public WorldEditPlugin getWorldEdit() {

        return worldEdit;
    }

    public WorldGuardPlugin getWorldGuard() {

        return worldGuard;
    }

    public WorldGuardManager getWorldGuardManager() {

        return worldGuardManager;
    }

    public TravelManager getTravelManager() {

        return travelManager;
    }
}
