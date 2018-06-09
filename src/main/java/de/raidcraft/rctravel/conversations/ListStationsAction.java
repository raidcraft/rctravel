package de.raidcraft.rctravel.conversations;

import de.raidcraft.RaidCraft;
import de.raidcraft.api.action.action.Action;
import de.raidcraft.api.conversations.Conversations;
import de.raidcraft.api.conversations.builder.StageBuilder;
import de.raidcraft.api.conversations.conversation.Conversation;
import de.raidcraft.api.conversations.stage.StageTemplate;
import de.raidcraft.rctravel.RCTravelPlugin;
import de.raidcraft.rctravel.api.group.Group;
import de.raidcraft.rctravel.api.station.Chargeable;
import de.raidcraft.rctravel.api.station.Station;
import de.raidcraft.rctravel.manager.GroupManager;
import de.raidcraft.rctravel.manager.StationManager;
import de.raidcraft.util.ConfigUtil;
import de.raidcraft.util.EnumUtils;
import de.raidcraft.util.fanciful.FancyMessage;
import org.bukkit.ChatColor;
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

    private final RCTravelPlugin plugin = RaidCraft.getComponent(RCTravelPlugin.class);
    private final GroupManager groupManager = plugin.getGroupManager();
    private final StationManager stationManager = plugin.getStationManager();

    private static boolean isFreeStation(Station station) {
        double price = 0;
        if (station instanceof Chargeable) {
            price = ((Chargeable) station).getPrice();
        }

        return price == 0;
    }

    @Override
    @Information(
            value = "stations.list",
            desc = "Lists all target stations in a conversation.",
            type = Conversation.class,
            conf = {
                    "group: travel scope",
                    "stationName: the station that should list its targets",
                    "useNearestStation: [true/->false] - if true station name can be blank",
                    "searchRadius: radius to search for nearest station",
                    "allowTravelToUndiscovered: [true/->false] - if true the player can only choose discovered stations",
                    "order: ->ALPHABETIC_ASC | ALPHABETIC_DESC | DISTANCE_ASC | DISTANCE_DESC | PRICE_ASC | PRICE_DESC",
                    "filter: FREE | ->DISCOVERED | ALL"
            }
    )
    public void accept(Conversation conversation, ConfigurationSection config) {

        Optional<Group> group = groupManager.getGroup(config.getString("group"));
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

        EnumSet<StationListFilter> filters = EnumUtils.getEnumSetFromString(StationListFilter.class, config.getString("filter", "DISCOVERED"));
        StationListOrder order = EnumUtils.getEnumFromString(StationListOrder.class, config.getString("order", "ALPHABETIC_ASC"));

        List<Station> stations = new ArrayList<>();

        if (filters.contains(StationListFilter.ALL)) {
            stations = stationManager.getAllStations(group.get().getName());
        } else if (filters.contains(StationListFilter.DISCOVERED)) {
            stations = stationManager.getDiscoveredStations(group.get(), conversation.getOwner().getUniqueId());
        }

        if (filters.contains(StationListFilter.FREE)) {
            stations = stations.stream().filter(ListStationsAction::isFreeStation).collect(Collectors.toList());
        }

        stations = stations.stream()
                .filter(station -> !station.equals(currentStation))
                .sorted()
                .collect(Collectors.toList());

        conversation.changeToStage(buildStationList(conversation, stations));
    }

    private StageTemplate buildStationList(Conversation conversation, List<Station> stations) {

        StageBuilder stage = Conversations.buildStage("list-stations");

        if (stations.size() < 1) {
            return stage.withText("Tut mir leid, du kennst keine passenden Stationen.")
                    .withAction(Action.changeToPreviousStage())
                    .build();
        }

        stage.withText("Du kennst folgende Stationen:");

        stations.forEach(station -> {
            int distance = (int) station.getDistance(conversation.getLocation());
            double price = station instanceof Chargeable ? ((Chargeable) station).getPrice(distance) : 0;
            stage.withAnswer(
                    new FancyMessage("[").color(ChatColor.BLUE)
                            .text(station.getDisplayName()).color(ChatColor.YELLOW)
                            .formattedTooltip(
                                    new FancyMessage(station.getDisplayName()).color(ChatColor.YELLOW),
                                    new FancyMessage("Distanz: ").color(ChatColor.YELLOW)
                                            .text(station.getDistance(conversation.getLocation()) + "m").color(ChatColor.AQUA),
                                    price > 0 ? new FancyMessage("Preis: ").color(ChatColor.YELLOW)
                                            .text(RaidCraft.getEconomy().getFormattedAmount(price))
                                            : new FancyMessage("Kostenlos").color(ChatColor.GREEN)
                                            .text("]").color(ChatColor.BLUE)
                            ),
                    answer -> answer.withAction(Action.of(TravelToStationConversationAction.class), action -> action.withConfig("station", station.getName()))
            );
        });

        return stage.build();
    }

    public enum StationListOrder {

        ALPHABETIC_ASC("Alphabetisch von A-Z sortiert."),
        ALPHABETIC_DESC("Alphabetisch von Z-A sortiert."),
        DISTANCE_ASC("Nach Entfernung (nah bis fern) sortiert."),
        DISTANCE_DESC("Nach Entfernung (fern bis nah) sortiert."),
        PRICE_ASC("Nach Preis (günstig bis teuer) sortiert."),
        PRICE_DESC("Nach Preis (teuer bis günstig) sortiert.");

        private String infoText;

        StationListOrder(String infoText) {

            this.infoText = infoText;
        }

        public String getInfoText() {

            return infoText;
        }
    }

    public enum StationListFilter {

        ALL("Es werden alle Stationen angezeigt."),
        FREE("Es werden nur kostenlose Stationen angezeigt."),
        DISCOVERED("Es werden nur entdeckte Stationen angezeigt.");

        private String infoText;

        StationListFilter(String infoText) {

            this.infoText = infoText;
        }

        public String getInfoText() {

            return infoText;
        }
    }
}
