package de.raidcraft.rctravel.manager;

import de.raidcraft.RaidCraft;
import de.raidcraft.api.npc.NPC_Manager;
import de.raidcraft.rcconversations.npc.NPC_Conservations_Manager;
import de.raidcraft.rctravel.GroupedStation;
import de.raidcraft.rctravel.RCTravelPlugin;
import de.raidcraft.rctravel.npc.StationTrait;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Location;

import java.util.List;

/**
 * @author Philip Urban
 */
public class TravelMasterNPCManager {

    public static void spawnTravelMasterNPC(GroupedStation station) {

        Location improvedLocation = station.getStation().getLocation().clone();
        improvedLocation.setY(improvedLocation.getY() + 1.5);
        RCTravelPlugin plugin = RaidCraft.getComponent(RCTravelPlugin.class);
        NPC npc = NPC_Conservations_Manager.getInstance().spawnNonPersistNpcConservations(improvedLocation, "Reiseleiter", plugin.getName(), station.getGroup().getConversationName());
        npc.addTrait(StationTrait.class);
        npc.getTrait(StationTrait.class).setStationName(station.getStation().getName());
    }

    public static void spawnAllDragonGuardNPCs(StationManager stationManager) {

        List<GroupedStation> stationList = stationManager.getGroupedStations();
        RaidCraft.LOGGER.info("[RCTravel] Spawn " + stationList.size() + " Travel Masters...");
        for(GroupedStation station : stationList) {
            spawnTravelMasterNPC(station);
        }
    }

    public static void removeAllDragonGuards() {
        RCTravelPlugin plugin = RaidCraft.getComponent(RCTravelPlugin.class);
        NPC_Manager.getInstance().removeAllNPCs(plugin.getName());
    }

}
