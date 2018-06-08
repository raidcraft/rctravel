package de.raidcraft.rctravel.conversations;

import de.raidcraft.RaidCraft;
import de.raidcraft.api.action.action.Action;
import de.raidcraft.rctravel.RCTravelPlugin;
import de.raidcraft.rctravel.api.station.SimpleStation;
import de.raidcraft.rctravel.api.station.Station;
import de.raidcraft.rctravel.tasks.TakeoffDelayedTask;
import de.raidcraft.util.ConfigUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

public class TravelToStationAction implements Action<Player> {

    @Override
    @Information(
            value = "travel-to-station",
            desc = "Travels to the given station. Uses the current player position or given station as start.",
            aliases = {"travel"},
            conf = {
                    "start: [station name]",
                    "target: <station name>",
                    "delay: [in sec]"
            }
    )
    public void accept(Player player, ConfigurationSection config) {

        String startName = config.getString("start", null);
        String targetName = config.getString("target", null);
        int delay = config.getInt("delay", 0);

        RCTravelPlugin plugin = RaidCraft.getComponent(RCTravelPlugin.class);
        Station targetStation = plugin.getStationManager().getStation(targetName);

        if (targetStation == null) {
            player.sendMessage(ChatColor.RED + "Invalid target station in action " + getIdentifier() + " and config: " + ConfigUtil.getFileName(config));
            return;
        }

        Station startStation;
        if (startName == null) {
            startStation = new SimpleStation(getIdentifier(), player.getLocation().clone());
        } else {
            startStation = plugin.getStationManager().getStation(startName);
        }
        if (startStation == null) {
            player.sendMessage(ChatColor.RED + "Invalid start station in action " + getIdentifier() + " and config: " + ConfigUtil.getFileName(config));
            return;
        }

        Bukkit.getScheduler().runTaskLater(plugin, new TakeoffDelayedTask(startStation, targetStation, player), delay);
    }
}
