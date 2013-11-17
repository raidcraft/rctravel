package de.raidcraft.rctravel.conversations;

import de.raidcraft.RaidCraft;
import de.raidcraft.rcconversations.actions.common.StageAction;
import de.raidcraft.rcconversations.actions.variables.SetVariableAction;
import de.raidcraft.rcconversations.api.action.*;
import de.raidcraft.rcconversations.api.answer.Answer;
import de.raidcraft.rcconversations.api.answer.SimpleAnswer;
import de.raidcraft.rcconversations.api.conversation.Conversation;
import de.raidcraft.rcconversations.api.stage.SimpleStage;
import de.raidcraft.rcconversations.api.stage.Stage;
import de.raidcraft.rcconversations.util.ParseString;
import de.raidcraft.rctravel.RCTravelPlugin;
import de.raidcraft.rctravel.api.group.Group;
import de.raidcraft.rctravel.api.station.Chargeable;
import de.raidcraft.rctravel.api.station.Station;
import de.raidcraft.rctravel.comparator.AlphabeticComparator;
import de.raidcraft.rctravel.comparator.DistanceComparator;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.*;

/**
 * @author Philip
 */
@ActionInformation(name = "LIST_TRAVEL_STATIONS")
public class ListStationsAction extends AbstractAction {

    @Override
    public void run(Conversation conversation, ActionArgumentList args) throws ActionArgumentException {

        String groupName = args.getString("group");
        groupName = ParseString.INST.parse(conversation, groupName);
        String typeName = args.getString("type");
        ListType type = ListType.valueOf(typeName);
        if(type == null) {
            throw new WrongArgumentValueException("Wrong argument value in action '" + getName() + "': Type '" + typeName + "' does not exists!");
        }
        if(groupName == null) {
            throw new WrongArgumentValueException("Wrong argument value in action '" + getName() + "': GroupName is not configured!");
        }
        RCTravelPlugin plugin = RaidCraft.getComponent(RCTravelPlugin.class);
        Group group = plugin.getGroupManager().getGroup(groupName);
        if(group == null) {
            throw new WrongArgumentValueException("Wrong argument value in action '" + getName() + "': Group '" + typeName + "' does not exists!");
        }
        Station currentStation = plugin.getStationManager().getStation(conversation.getString("rct_station_name"));

        if(currentStation == null) {
            throw new WrongArgumentValueException("Wrong argument value in action '" + getName() + "': Station '" + conversation.getString("rct_station_name") + "' does not exists!");
        }

        String confirmStage = args.getString("confirmstage");
        String returnStage = args.getString("returnstage");
        int pageSize = args.getInt("pagesize", 4);

        if(confirmStage == null || returnStage == null) {
            throw new MissingArgumentException("Missing argument in action '" + getName() + "': Confirmstage or Returnstage is missing!");
        }

        String entranceStage = "rct_stationslist";

        List<Station> stations = plugin.getStationManager().getDiscoveredStations(group, conversation.getPlayer().getName());

        if(type == ListType.ALPHABETIC) {
            Collections.sort(stations, new AlphabeticComparator());
        }
        if(type == ListType.DISTANCE) {
            Collections.sort(stations, new DistanceComparator(currentStation));
        }

        if(type == ListType.FREE) {
            List<Station> freeStations = new ArrayList<>();
            double price;
            for(Station s : stations) {
                price = 0;
                if(s instanceof Chargeable) {
                    price = ((Chargeable) s).getPrice();
                }

                if(price == 0) {
                    freeStations.add(s);
                }
            }
            stations = freeStations;
        }

        for(Station station : stations) {
            if(station.equals(currentStation)) {
                stations.remove(station);
                break;
            }
        }

        if(stations.size() == 0) {
            List<Answer> answers = new ArrayList<>();
            answers.add(new SimpleAnswer("1", "Ok zurück", new ActionArgumentList("A", StageAction.class, "stage", returnStage)));
            conversation.addStage(new SimpleStage(entranceStage, "Du kennst keine passende Stationen!", answers));
        }

        int pages = (int) (((double) stations.size() / (double) pageSize) + 0.5);
        if(pages == 0) pages = 1;
        for (int i = 0; i < pages; i++) {

            Stage stage;
            List<Answer> answers = new ArrayList<>();
            String text;

            text = "Du kennst folgende Stationen (" + (i+1) + "/" + pages + "):|&7(" + type.getInfoText() + ")";
            int a;

            for (a = 0; a < pageSize; a++) {
                if (stations.size() <= a + (i * pageSize)) break;
                answers.add(createStationAnswer(conversation.getPlayer(), a, currentStation, stations.get(i*pageSize + a), confirmStage));
            }
            a++;

            String nextStage;
            if (pages - 1 == i) {
                nextStage = entranceStage;
            }
            else {
                nextStage = entranceStage + "_" + (i + 1);
            }
            String thisStage;
            if(i == 0) {
                thisStage = entranceStage;
            }
            else {
                thisStage = entranceStage + "_" + i;
            }

            if(pages > 1) {
                answers.add(new SimpleAnswer(String.valueOf(a), "&7Nächste Seite", new ActionArgumentList(String.valueOf(a), StageAction.class, "stage", nextStage)));
            }
            stage = new SimpleStage(thisStage, text, answers);

            conversation.addStage(stage);
        }

        conversation.setCurrentStage(entranceStage);
        conversation.triggerCurrentStage();
    }

    private Answer createStationAnswer(Player player, int number, Station start, Station target, String confirmStage) {

        List<ActionArgumentList> actions = new ArrayList<>();
        int i = 0;
        Map<String, Object> data = new HashMap<>();
        data.put("variable", "rct_target_name");
        data.put("local", true);
        data.put("value", target.getName());
        actions.add(new ActionArgumentList(String.valueOf(i++), SetVariableAction.class, data));
        actions.add(new ActionArgumentList(String.valueOf(i++), StageAction.class, "stage", confirmStage));

        StringBuilder builder = new StringBuilder();
        double price  = 0;
        if(target instanceof Chargeable) {
            price = ((Chargeable) target).getPrice((int)start.getLocation().distance(target.getLocation()));
            if(!RaidCraft.getEconomy().hasEnough(player.getName(), price)) {
                builder.append(ChatColor.DARK_GRAY);
            }
        }

        builder.append(target.getName());
        builder.append(" ").append(RaidCraft.getEconomy().getFormattedAmount(price));

        int distance = (int)start.getLocation().distance(target.getLocation());
        if(distance < 1000) {
            builder.append(ChatColor.GRAY).append(" (").append(distance).append("m)");
        }
        else {
            builder.append(ChatColor.GRAY).append(" (").append(((double)distance)/1000.).append("km)");
        }

        return new SimpleAnswer(String.valueOf(number + 1), builder.toString(), actions);
    }

    public enum ListType {

        ALPHABETIC("Alphabetisch sortiert"),
        DISTANCE("Nach Entfernung sortiert"),
        FREE("Es werden nur kostenlose angezeigt");

        private String infoText;

        private ListType(String infoText) {

            this.infoText = infoText;
        }

        public String getInfoText() {

            return infoText;
        }
    }
}
