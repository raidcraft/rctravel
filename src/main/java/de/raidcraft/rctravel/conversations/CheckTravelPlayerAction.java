package de.raidcraft.rctravel.conversations;

import de.raidcraft.RaidCraft;
import de.raidcraft.api.economy.Economy;
import de.raidcraft.rcconversations.api.action.AbstractAction;
import de.raidcraft.rcconversations.api.action.ActionArgumentException;
import de.raidcraft.rcconversations.api.action.ActionArgumentList;
import de.raidcraft.rcconversations.api.action.ActionInformation;
import de.raidcraft.rcconversations.api.conversation.Conversation;
import de.raidcraft.rcconversations.util.ParseString;
import de.raidcraft.rctravel.RCTravelPlugin;
import de.raidcraft.rctravel.api.station.Chargeable;
import de.raidcraft.rctravel.api.station.Discoverable;
import de.raidcraft.rctravel.api.station.Station;

/**
 * @author Philip
 */
@ActionInformation(name = "TRAVEL_CHECK_PLAYER")
public class CheckTravelPlayerAction extends AbstractAction {

    @Override
    public void run(Conversation conversation, ActionArgumentList args) throws ActionArgumentException {

        String startName = args.getString("start", null);
        startName = ParseString.INST.parse(conversation, startName);
        String targetName = args.getString("target", null);
        targetName = ParseString.INST.parse(conversation, targetName);
        String success = args.getString("onsuccess", null);
        String failure = args.getString("onfailure", null);
        boolean checkPrice = args.getBoolean("price", false);
        boolean checkFamiliarity = args.getBoolean("familiarity", false);

        RCTravelPlugin plugin = RaidCraft.getComponent(RCTravelPlugin.class);
        Station startStation = plugin.getStationManager().getStation(startName);
        Station targetStation = plugin.getStationManager().getStation(startName);

        if(startStation == null) {
            setErrorMsg(conversation, "Es ist ein Fehler aufgetreten! Bitte informiere das Raid-Craft Team!");
            changeStage(conversation, failure);
            return;
        }

        if(targetStation == null) {
            setErrorMsg(conversation, "Die angegebene Station existiert nicht!");
            changeStage(conversation, failure);
            return;
        }

        if(startStation.equals(targetStation)) {
            setErrorMsg(conversation, "Du befindest dich bereits an dieser Station!");
            changeStage(conversation, failure);
            return;
        }

        conversation.set("rct_target_name", targetStation.getPlainName());
        conversation.set("rct_target_friendlyname", targetStation.getName());
        conversation.set("rct_target_distance", startStation.getLocation().distance(targetStation.getLocation()));

        if(targetStation instanceof Chargeable) {
            Economy economy = RaidCraft.getEconomy();
            double price = ((Chargeable) targetStation).getPrice((int)startStation.getLocation().distance(targetStation.getLocation()));
            conversation.set("rct_target_price", price);
            conversation.set("rct_target_price_formatted", economy.getFormattedAmount(price));
            if(checkPrice && !economy.hasEnough(conversation.getPlayer().getName(), price)) {
                setErrorMsg(conversation, "Du brauchst " + economy.getFormattedAmount(price) + " um dorthin zu reisen!");
                changeStage(conversation, failure);
                return;
            }
        }

        if(targetStation instanceof Discoverable && checkFamiliarity) {
            if(!((Discoverable) targetStation).hasDiscovered(conversation.getPlayer().getName())) {
                setErrorMsg(conversation, "Du musst dieses Reiseziel erst noch kennen lernen!");
                changeStage(conversation, failure);
                return;
            }
        }

        changeStage(conversation, success);
    }

    private void setErrorMsg(Conversation conversation, String msg) {

        conversation.set("rct_target_error", msg);
    }

    private void changeStage(Conversation conversation, String failureStage) {

        if(failureStage != null) {
            conversation.setCurrentStage(failureStage);
            conversation.triggerCurrentStage();
        }
    }
}
