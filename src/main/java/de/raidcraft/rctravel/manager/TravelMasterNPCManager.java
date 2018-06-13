package de.raidcraft.rctravel.manager;

import de.raidcraft.RaidCraft;
import de.raidcraft.api.conversations.Conversations;
import de.raidcraft.api.conversations.host.ConversationHost;
import de.raidcraft.api.npc.NPC_Manager;
import de.raidcraft.rctravel.GroupedStation;
import de.raidcraft.rctravel.RCTravelPlugin;
import de.raidcraft.rctravel.npc.StationTrait;
import org.bukkit.Location;

import java.util.List;
import java.util.Optional;

/**
 * @author Philip Urban
 */
public class TravelMasterNPCManager {

    public static void spawnTravelMasterNPC(GroupedStation station) {

        Location improvedLocation = station.getStation().getLocation().clone();
        improvedLocation.setY(improvedLocation.getY() + 1.5);
        RCTravelPlugin plugin = RaidCraft.getComponent(RCTravelPlugin.class);
        Optional<ConversationHost<?>> host = Conversations.spawnConversationHost(plugin.getName(), "Reiseleiter", station.getStationGroup().getConversationName(), improvedLocation);

        host.ifPresent(h -> h.addTrait(StationTrait.class));
        host.ifPresent(h -> h.getTrait(StationTrait.class).ifPresent(trait -> trait.setStationName(station.getStation().getName())));
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
