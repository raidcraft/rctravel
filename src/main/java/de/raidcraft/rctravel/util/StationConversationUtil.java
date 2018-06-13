package de.raidcraft.rctravel.util;

import de.raidcraft.RaidCraft;
import de.raidcraft.api.action.action.Action;
import de.raidcraft.api.action.action.ActionBuilder;
import de.raidcraft.api.conversations.Conversations;
import de.raidcraft.api.conversations.builder.StageBuilder;
import de.raidcraft.api.conversations.conversation.Conversation;
import de.raidcraft.api.conversations.stage.StageTemplate;
import de.raidcraft.rctravel.api.station.Chargeable;
import de.raidcraft.rctravel.api.station.Station;
import de.raidcraft.util.fanciful.FancyMessage;
import org.bukkit.ChatColor;

import java.util.List;

public class StationConversationUtil {

    public static StageTemplate buildStationList(Conversation conversation, List<Station> stations, Class<? extends Action> actionClass, StationAction stationAction) {

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
                    answer -> answer.withAction(Action.of(actionClass), action -> {
                        stationAction.apply(station, action);
                    })
            );
        });

        return stage.build();
    }

    @FunctionalInterface
    public interface StationAction {

        void apply(Station station, ActionBuilder actionBuilder);
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
