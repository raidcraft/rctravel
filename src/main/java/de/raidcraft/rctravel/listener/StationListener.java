package de.raidcraft.rctravel.listener;

import de.raidcraft.RaidCraft;
import de.raidcraft.rctravel.GroupedStation;
import de.raidcraft.rctravel.RCTravelPlugin;
import de.raidcraft.rctravel.api.station.SchematicStation;
import de.raidcraft.rctravel.events.StationLockStateChangeEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

/**
 * @author Philip Urban
 */
public class StationListener implements Listener {

    @EventHandler
    public void onStationLockstateChange(StationLockStateChangeEvent event) {

        // lock
        if(event.getNewLockState()) {
            if(event.getGroupedStation().getStation() instanceof SchematicStation) {
                clearSchematicPlace((SchematicStation) event.getGroupedStation().getStation());
                ((SchematicStation) event.getGroupedStation().getStation()).changeSchematic(true);
            }
            announceDeparture(event.getGroupedStation());
        }
        // unlock
        else {
            // change schematic
            if(event.getGroupedStation().getStation() instanceof SchematicStation) {
                clearSchematicPlace((SchematicStation) event.getGroupedStation().getStation());
                ((SchematicStation) event.getGroupedStation().getStation()).changeSchematic(false);
            }
            announceArrival(event.getGroupedStation());
        }
    }

    private void clearSchematicPlace(SchematicStation schematicStation) {

        for(Player player : Bukkit.getOnlinePlayers()) {
            if(RaidCraft.getComponent(RCTravelPlugin.class).getWorldGuardManager().isInsideRegion(player, schematicStation.getMinPoint(), schematicStation.getMaxPoint())) {
                player.teleport(schematicStation.getLocation());
            }
        }
    }

    private void announceArrival(GroupedStation groupedStation) {

        for(Player player : Bukkit.getOnlinePlayers()) {
            if(!player.getWorld().equals(groupedStation.getStation().getLocation().getWorld())) continue;
            if(player.getLocation().distance(groupedStation.getStation().getLocation()) < 250) {
                player.sendMessage(ChatColor.GOLD + "*" + ChatColor.DARK_GREEN + " Das " + groupedStation.getGroup().getVehicleName()
                        + " von " + ChatColor.GREEN + groupedStation.getStation().getName() + ChatColor.DARK_GREEN + " ist eingetroffen!");
            }
        }
    }

    private void announceDeparture(GroupedStation groupedStation) {

        for(Player player : Bukkit.getOnlinePlayers()) {
            if(!player.getWorld().equals(groupedStation.getStation().getLocation().getWorld())) continue;
            if(player.getLocation().distance(groupedStation.getStation().getLocation()) < 250) {
                player.sendMessage(ChatColor.GOLD + "*" + ChatColor.DARK_RED + " Das " + groupedStation.getGroup().getVehicleName()
                        + " von " + ChatColor.RED + groupedStation.getStation().getName() + ChatColor.DARK_RED + " ist abgereist!");
            }
        }
    }
}
