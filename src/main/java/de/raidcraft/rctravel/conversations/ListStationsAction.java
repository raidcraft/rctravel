package de.raidcraft.rctravel.conversations;

import de.raidcraft.RaidCraft;
import de.raidcraft.api.action.action.Action;
import de.raidcraft.api.conversations.Conversations;
import de.raidcraft.api.conversations.conversation.Conversation;
import de.raidcraft.rctravel.RCTravelPlugin;
import de.raidcraft.rctravel.api.group.StationGroup;
import de.raidcraft.rctravel.api.station.Chargeable;
import de.raidcraft.rctravel.api.station.Station;
import de.raidcraft.rctravel.manager.GroupManager;
import de.raidcraft.rctravel.manager.StationManager;
import de.raidcraft.rctravel.util.StationConversationUtil;
import de.raidcraft.util.ConfigUtil;
import de.raidcraft.util.EnumUtils;
import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author Philip
 */
public class ListStationsAction implements Action<Conversation> {


    private static boolean isFreeStation(Station station) {
        double price = 0;
        if (station instanceof Chargeable) {
            price = ((Chargeable) station).getPrice();
        }

        return price == 0;
    }

    @Override
    @Information(
            value = "station.conv.list",
            desc = "Lists all target stations in a conversation.",
            type = Conversation.class,
            conf = {
                    "group: travelTo scope",
                    "stationName: the station that should list its targets",
                    "useNearestStation: [true/->false] - if true station displayName can be blank",
                    "searchRadius: radius to search for nearest station",
                    "allowTravelToUndiscovered: [true/->false] - if true the player can only choose discovered stations",
                    "order: ->ALPHABETIC_ASC | ALPHABETIC_DESC | DISTANCE_ASC | DISTANCE_DESC | PRICE_ASC | PRICE_DESC",
                    "filter: FREE | ->DISCOVERED | ALL"
            }
    )
    public void accept(Conversation conversation, ConfigurationSection config) {

        RCTravelPlugin plugin = RaidCraft.getComponent(RCTravelPlugin.class);
        GroupManager groupManager = plugin.getGroupManager();
        StationManager stationManager = plugin.getStationManager();


        Optional<StationGroup> group = groupManager.getGroup(config.getString("group"));
        if (!group.isPresent()) {
            Conversations.error(conversation, "Invalid group in action " + getIdentifier() + " and config: " + ConfigUtil.getFileName(config));
            return;
        }

        Optional<Station> currentStation = stationManager.getStation(config.getString("stationName"));
        if (config.getBoolean("useNearestStation", false)) {
            currentStation.orElseGet(() -> stationManager.getNearbyStation(conversation.getLocation(), config.getInt("searchRadius", 10)));
        }
        if (!currentStation.isPresent()) {
            Conversations.error(conversation, "Invalid station in action " + getIdentifier() + " and config: " + ConfigUtil.getFileName(config));
            return;
        }

        EnumSet<StationConversationUtil.StationListFilter> filters = EnumUtils.getEnumSetFromString(StationConversationUtil.StationListFilter.class, config.getString("filter", "DISCOVERED"));

        List<Station> stations = new ArrayList<>();

        if (filters.contains(StationConversationUtil.StationListFilter.ALL)) {
            stations = stationManager.getAllStations(group.get().getName());
        } else if (filters.contains(StationConversationUtil.StationListFilter.DISCOVERED)) {
            stations = stationManager.getDiscoveredStations(group.get(), conversation.getOwner().getUniqueId());
        }

        if (filters.contains(StationConversationUtil.StationListFilter.FREE)) {
            stations = stations.stream().filter(ListStationsAction::isFreeStation).collect(Collectors.toList());
        }

        stations = stations.stream()
                .filter(station -> !station.equals(currentStation))
                .sorted()
                .collect(Collectors.toList());

        conversation.changeToStage(StationConversationUtil.buildStationList(conversation, stations, TravelToStationAction.class, (station, actionBuilder) -> {
            actionBuilder.withConfig("target", station.getName());
            actionBuilder.withConfig("start", currentStation.get().getName());
            actionBuilder.withConfig("confirm", true);
            actionBuilder.withConfig("pay", true);
        }));
    }
}
