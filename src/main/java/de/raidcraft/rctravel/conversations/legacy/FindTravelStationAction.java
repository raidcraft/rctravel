package de.raidcraft.rctravel.conversations.legacy;

import de.raidcraft.RaidCraft;
import de.raidcraft.rcconversations.api.action.AbstractAction;
import de.raidcraft.rcconversations.api.action.ActionArgumentException;
import de.raidcraft.rcconversations.api.action.ActionArgumentList;
import de.raidcraft.rcconversations.api.action.ActionInformation;
import de.raidcraft.rcconversations.api.conversation.Conversation;
import de.raidcraft.rctravel.GroupedStation;
import de.raidcraft.rctravel.RCTravelPlugin;
import de.raidcraft.rctravel.api.station.Discoverable;
import de.raidcraft.rctravel.api.station.Station;
import de.raidcraft.util.DateUtil;
import org.bukkit.ChatColor;

/**
 * @author Philip
 */
@ActionInformation(name = "FIND_TRAVEL_STATION")
public class FindTravelStationAction extends AbstractAction {

    @Override
    public void run(Conversation conversation, ActionArgumentList args) throws ActionArgumentException {

        int radius = args.getInt("radius");
        String success = args.getString("onsuccess", null);
        String failure = args.getString("onfailure", null);

        RCTravelPlugin plugin = RaidCraft.getComponent(RCTravelPlugin.class);
        Station station = plugin.getStationManager().getNearbyStation(conversation.getHost().getLocation(), radius);

        if (station == null) {
            if (failure != null) {
                conversation.setCurrentStage(failure);
                conversation.triggerCurrentStage();
            }
            return;
        }

        if (station instanceof Discoverable) {
            if (!((Discoverable) station).hasDiscovered(conversation.getPlayer().getUniqueId())) {
                ((Discoverable) station).setDiscovered(conversation.getPlayer().getUniqueId(), true);
                conversation.getPlayer().sendMessage(ChatColor.GREEN + "Du besucht diese Reisestation zum ersten mal!");
            }
        }

        GroupedStation groupedStation = plugin.getStationManager().getGroupedStation(station);
        conversation.set("rct_station_name", station.getName());
        conversation.set("rct_station_group", groupedStation.getGroup().getPlainName());
        conversation.set("rct_station_friendlyname", station.getDisplayName());
        conversation.set("rct_station_vehicle", groupedStation.getGroup().getVehicleName());
        conversation.set("rct_station_cooldown", DateUtil.formatSeconds(plugin.getStationLockTask().getRemainingTime(station)));
        conversation.set("rct_station_friendlystate", (plugin.getStationLockTask().isLocked(station)) ? "auf Reise" : "Abfahrt bereit");
        if (success != null) {
            conversation.setCurrentStage(success);
            conversation.triggerCurrentStage();
        }
    }
}
