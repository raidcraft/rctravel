package de.raidcraft.rctravel.util;

import de.raidcraft.rcconversations.npc.ConversationsTrait;
import de.raidcraft.rcconversations.npc.NPCRegistry;
import de.raidcraft.rctravel.GroupedStation;
import de.raidcraft.rctravel.RCTravelPlugin;
import net.citizensnpcs.api.npc.NPC;

/**
 * @author Philip Urban
 */
public class NPCManager {

    private RCTravelPlugin plugin;

    public NPCManager(RCTravelPlugin plugin) {

        this.plugin = plugin;
    }

    public void createNPC(GroupedStation groupedStation) {

        ConversationsTrait.create(groupedStation.getStation().getLocation(), groupedStation.getGroup().getConversationName(), "Reiseleiter");
    }

    public void removeNPC(GroupedStation groupedStation) {

        for(NPC npc : NPCRegistry.INST.getSpawnedNPCs(groupedStation.getStation().getLocation().getChunk())) {
            ConversationsTrait trait = npc.getTrait(ConversationsTrait.class);
            if(!trait.getConversationName().equalsIgnoreCase(groupedStation.getGroup().getConversationName())) {
                continue;
            }

            if(npc.getBukkitEntity().getLocation().distance(groupedStation.getStation().getLocation()) <= 5) {
                NPCRegistry.INST.unregisterNPC(npc);
                npc.destroy();
            }
        }
    }

}
