package de.raidcraft.rctravel.conversations;

import de.raidcraft.RaidCraft;
import de.raidcraft.rcconversations.RCConversationsPlugin;
import de.raidcraft.rcconversations.api.action.AbstractAction;
import de.raidcraft.rcconversations.api.action.ActionArgumentException;
import de.raidcraft.rcconversations.api.action.ActionArgumentList;
import de.raidcraft.rcconversations.api.action.ActionInformation;
import de.raidcraft.rcconversations.api.action.WrongArgumentValueException;
import de.raidcraft.rcconversations.api.conversation.Conversation;
import de.raidcraft.rcconversations.conversations.EndReason;
import de.raidcraft.rcconversations.util.ParseString;
import de.raidcraft.rctravel.Journey;
import de.raidcraft.rctravel.RCTravelPlugin;
import de.raidcraft.rctravel.TeleportTravelStation;
import de.raidcraft.rctravel.api.station.SimpleStation;
import de.raidcraft.rctravel.api.station.Station;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

/**
 * @author Philip
 */
@ActionInformation(name = "TRAVEL_TO_STATION")
public class TravelToStationAction extends AbstractAction {

    @Override
    public void run(Conversation conversation, ActionArgumentList args) throws ActionArgumentException {

        String startName = args.getString("start", null);
        String targetName = args.getString("target", null);
        targetName = ParseString.INST.parse(conversation, targetName);
        startName = ParseString.INST.parse(conversation, startName);
        int delay = args.getInt("delay", 0);

        RCTravelPlugin plugin = RaidCraft.getComponent(RCTravelPlugin.class);
        Station targetStation = plugin.getStationManager().getStation(targetName);
        if (targetStation == null) {
            throw new WrongArgumentValueException("Wrong argument value in action '" + getName() + "': Station '" + targetName + "' does not exists!");
        }

        Station startStation;
        if (startName == null) {
            startStation = new SimpleStation(conversation.getName(), conversation.getPlayer().getLocation().clone());
        } else {
            startStation = plugin.getStationManager().getStation(startName);
        }
        if (startStation == null) {
            throw new WrongArgumentValueException("Wrong argument value in action '" + getName() + "': Station '" + targetName + "' does not exists!");
        }

        Bukkit.getScheduler().runTaskLater(plugin, new TakeoffDelayedTask(startStation, targetStation, conversation.getPlayer()), delay);
    }

    public class TakeoffDelayedTask implements Runnable {

        Station start;
        Station target;
        Player player;

        public TakeoffDelayedTask(Station start, Station target, Player player) {

            this.start = start;
            this.target = target;
            this.player = player;
        }

        @Override
        public void run() {

            if (start instanceof TeleportTravelStation) {
                RaidCraft.getComponent(RCTravelPlugin.class).getTravelManager().queuePlayer(player, new Journey(start, target));
            }

        }
    }
}
