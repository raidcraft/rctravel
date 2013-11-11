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
import org.bukkit.Chunk;
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

        Bukkit.getScheduler().runTaskLater(RaidCraft.getComponent(RCTravelPlugin.class), new TravelMasterChecker(event.getChunk()), 3*20);
    }

    public class TravelMasterChecker implements Runnable {

        private Chunk chunk;

        public TravelMasterChecker(Chunk chunk) {

            this.chunk = chunk;
        }

        @Override
        public void run() {

            Set<GroupedStation> stations = RaidCraft.getComponent(RCTravelPlugin.class).getStationManager().getGroupedStationsByChunk(chunk);

            // if there are stations without npcs -> create new

            for(GroupedStation groupedStation : new HashSet<>(stations)) {

                // check a second time
                Set<ChunkLocation> affectedChunks = NPCRegistry.INST.getAffectedChunkLocations(chunk);
                boolean found = false;
                for(ChunkLocation cl : affectedChunks) {
                    for(Entity entity : chunk.getWorld().getChunkAt(cl.getX(), cl.getZ()).getEntities()) {
                        if(!(entity instanceof LivingEntity)) continue;
                        if(entity.getLocation().distance(groupedStation.getStation().getLocation()) <= 5) {
                            NPC npc = RaidCraft.getComponent(RCConversationsPlugin.class).getCitizens().getNPCRegistry().getNPC(entity);
                            if(npc == null) continue;
                            ConversationsTrait trait = npc.getTrait(ConversationsTrait.class);
                            if(!trait.getConversationName().equalsIgnoreCase(groupedStation.getGroup().getConversationName())) continue;
                            stations.remove(groupedStation);
                            if(found) {
                                NPCRegistry.INST.unregisterNPC(npc);
                                npc.destroy();
                            }
                            else {
                                found = true;
                            }
                        }
                    }
                }
            }

            for(GroupedStation groupedStation : stations) {
                RaidCraft.LOGGER.info("Create Travel NPC for station: '" + groupedStation.getStation().getName() + "'!");
                RaidCraft.getComponent(RCTravelPlugin.class).getNpcManager().createNPC(groupedStation);
            }
        }
    }
}
