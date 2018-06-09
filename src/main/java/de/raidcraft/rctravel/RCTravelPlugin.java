package de.raidcraft.rctravel;

import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import de.raidcraft.api.BasePlugin;
import de.raidcraft.api.action.ActionAPI;
import de.raidcraft.api.config.ConfigurationBase;
import de.raidcraft.api.conversations.conversation.Conversation;
import de.raidcraft.api.npc.NPC_Manager;
import de.raidcraft.api.npc.RC_Traits;
import de.raidcraft.rctravel.commands.TravelCommands;
import de.raidcraft.rctravel.conversations.ListStationsAction;
import de.raidcraft.rctravel.conversations.TravelToStationAction;
import de.raidcraft.rctravel.listener.StationListener;
import de.raidcraft.rctravel.manager.GroupManager;
import de.raidcraft.rctravel.manager.StationManager;
import de.raidcraft.rctravel.manager.TravelManager;
import de.raidcraft.rctravel.manager.TravelMasterNPCManager;
import de.raidcraft.rctravel.npc.StationTrait;
import de.raidcraft.rctravel.tables.TTravelStation;
import de.raidcraft.rctravel.tasks.StationLockTask;
import de.raidcraft.rctravel.util.DynmapManager;
import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Philip Urban
 */
public class RCTravelPlugin extends BasePlugin {

    private LocalConfiguration config;
    private StationManager stationManager;
    private GroupManager groupManager;
    private DynmapManager dynmapManager;
    private StationLockTask stationLockTask;
    private WorldEditPlugin worldEdit;
    private WorldGuardPlugin worldGuard;
    private TravelManager travelManager;

    @Override
    public void enable() {
        // register Station trait
        NPC_Manager.getInstance().registerTrait(StationTrait.class, RC_Traits.STATION);

        registerCommands(TravelCommands.class);
        registerEvents(new StationListener());

        registerActionApi();

        config = new LocalConfiguration(this);

        //XXX order is important!
        groupManager = new GroupManager(this);
        stationManager = new StationManager(this);
        dynmapManager = new DynmapManager(this);
        stationLockTask = new StationLockTask(this);
        worldEdit = (WorldEditPlugin) Bukkit.getServer().getPluginManager().getPlugin("WorldEdit");
        worldGuard = (WorldGuardPlugin) Bukkit.getServer().getPluginManager().getPlugin("WorldGuard");
        travelManager = new TravelManager(this);

        TravelMasterNPCManager.spawnAllDragonGuardNPCs(stationManager);

        // startStage station schedule calculation
        // every 5 seconds one station will be checked
        Bukkit.getScheduler().runTaskTimer(this, stationLockTask, 0, 5 * 20);
    }

    @Override
    public void disable() {

    }

    @Override
    public void reload() {

        config = new LocalConfiguration(this);

        //XXX order is important!
        getGroupManager().reload();
        getStationManager().reload();
        getStationLockTask().reload();
        getTravelManager().reload();
    }

    public List<Class<?>> getDatabaseClasses() {

        List<Class<?>> databases = new ArrayList<>();
        databases.add(TTravelStation.class);
        return databases;
    }

    public class LocalConfiguration extends ConfigurationBase<RCTravelPlugin> {

        public LocalConfiguration(RCTravelPlugin plugin) {

            super(plugin, "config.yml");
        }
    }

    public void registerActionApi() {

        ActionAPI.register(this)
                .action(new TravelToStationAction())
                .action(new ListStationsAction(), Conversation.class);
    }

    public void registerConversations() {
        
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

    public WorldEditPlugin getWorldEdit() {

        return worldEdit;
    }

    public WorldGuardPlugin getWorldGuard() {

        return worldGuard;
    }

    public TravelManager getTravelManager() {

        return travelManager;
    }
}
