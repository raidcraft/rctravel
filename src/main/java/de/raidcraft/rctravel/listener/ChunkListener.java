package de.raidcraft.rctravel.listener;

import de.raidcraft.RaidCraft;
import de.raidcraft.rcconversations.RCConversationsPlugin;
import de.raidcraft.rcconversations.npc.ConversationsTrait;
import de.raidcraft.rcconversations.npc.NPCRegistry;
import de.raidcraft.rcconversations.util.ChunkLocation;
import de.raidcraft.rctravel.GroupedStation;
import de.raidcraft.rctravel.RCTravelPlugin;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkLoadEvent;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Philip Urban
 */
public class ChunkListener implements Listener {

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onChunkLoad(ChunkLoadEvent event) {
        // TODO: performance
        // TODO: NPC rework
        Bukkit.getScheduler().runTaskLater(RaidCraft.getComponent(RCTravelPlugin.class), new TravelMasterChecker(event.getWorld(), new ChunkLocation(event.getChunk())), 10);
    }

    public class TravelMasterChecker implements Runnable {

        private ChunkLocation chunkLocation;
        private World world;

        public TravelMasterChecker(World world, ChunkLocation chunkLocation) {

            this.chunkLocation = chunkLocation;
            this.world = world;
        }

        @Override
        public void run() {

            Set<GroupedStation> stations = RaidCraft.getComponent(RCTravelPlugin.class).getStationManager().getGroupedStationsByChunk(chunkLocation);

            // if there are stations without npcs -> create new

            for (GroupedStation groupedStation : new HashSet<>(stations)) {

                // check a second time
                Set<ChunkLocation> affectedChunks = NPCRegistry.INST.getAffectedChunkLocations(chunkLocation);
                boolean found = false;
                for (ChunkLocation cl : affectedChunks) {
                    for (Entity entity : chunkLocation.getChunk(world).getEntities()) {
                        if (!(entity instanceof LivingEntity)) continue;
                        if (entity.getLocation().distance(groupedStation.getStation().getLocation()) <= 5) {
                            NPC npc = RaidCraft.getComponent(RCConversationsPlugin.class).getCitizens().getNPCRegistry().getNPC(entity);
                            if (npc == null) continue;
                            ConversationsTrait trait = npc.getTrait(ConversationsTrait.class);
                            if (trait == null || trait.getConversationName() == null) {
                                npc.destroy();
                                continue;
                            }
                            if (!trait.getConversationName().equalsIgnoreCase(groupedStation.getGroup().getConversationName())) continue;
                            stations.remove(groupedStation);
                            if (found) {
                                NPCRegistry.INST.unregisterNPC(npc);
                                npc.destroy();
                            } else {
                                found = true;
                            }
                        }
                    }
                }
            }

            for (GroupedStation groupedStation : stations) {
                RaidCraft.LOGGER.info("Create Travel NPC for station: '" + groupedStation.getStation().getDisplayName() + "'!");
                RaidCraft.getComponent(RCTravelPlugin.class).getNpcManager().createNPC(groupedStation);
            }
        }
    }
}
