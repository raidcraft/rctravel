package de.raidcraft.rctravel.commands;

import com.sk89q.minecraft.util.commands.*;
import de.raidcraft.api.RaidCraftException;
import de.raidcraft.rctravel.GroupedStation;
import de.raidcraft.rctravel.RCTravelPlugin;
import de.raidcraft.rctravel.api.group.Group;
import de.raidcraft.rctravel.api.station.Station;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Optional;

/**
 * @author Philip Urban
 */
public class TravelCommands {

    private RCTravelPlugin plugin;

    public TravelCommands(RCTravelPlugin plugin) {

        this.plugin = plugin;
    }

    @Command(
            aliases = {"travelTo", "rctravel"},
            desc = "Travel commands"
    )
    @NestedCommand(value = NestedCommands.class)
    public void travel(CommandContext args, CommandSender sender) {

    }

    public static class NestedCommands {

        private final RCTravelPlugin plugin;

        public NestedCommands(RCTravelPlugin plugin) {

            this.plugin = plugin;
        }

        @Command(
                aliases = {"reload"},
                desc = "Reload travelTo plugin"
        )
        @CommandPermissions("rctravel.cmd.reload")
        public void reload(CommandContext args, CommandSender sender) {

            plugin.reload();

            // update dynmap marker
            for (GroupedStation groupedStation : plugin.getStationManager().getGroupedStations()) {
                plugin.getDynmapManager().addStationMarker(groupedStation.getStation(), groupedStation.getGroup());
            }
            sender.sendMessage(ChatColor.GREEN + "RCTravel wurde neugeladen!");
        }

        @Command(
                aliases = {"create"},
                desc = "Create travelTo station",
                min = 2,
                usage = "<group> <name>"
        )
        @CommandPermissions("rctravel.cmd.create")
        public void create(CommandContext args, CommandSender sender) throws CommandException {

            if (!(sender instanceof Player)) throw new CommandException("Player required!");
            Player player = (Player) sender;

            // check if group exists
            Optional<Group> group = plugin.getGroupManager().getGroup(args.getString(0));
            if (!group.isPresent()) {
                throw new CommandException("Es gibt keine Gruppe mit dem namen '" + args.getString(0) + "'!");
            }

            try {
                Station station = plugin.getStationManager().createStation(args.getJoinedStrings(1).replace(" ", "_"), player, group.get());
                sender.sendMessage(ChatColor.GREEN + "Die Station '" + station.getDisplayName() + "' wurde erfolgreich erstellt!");
            } catch (RaidCraftException e) {
                throw new CommandException(e.getMessage());
            }

        }

        @Command(
                aliases = {"delete", "remove"},
                desc = "Delete travelTo station",
                min = 1,
                usage = "<name>"
        )
        @CommandPermissions("rctravel.cmd.delete")
        public void delete(CommandContext args, CommandSender sender) throws CommandException {

            if (!(sender instanceof Player)) throw new CommandException("Player required!");
            Player player = (Player) sender;

            // check if station exists
            Optional<Station> station = plugin.getStationManager().getStation(args.getJoinedStrings(0));
            if (!station.isPresent()) {
                throw new CommandException("Es gibt keine Station mit dem namen '" + args.getJoinedStrings(0) + "'!");
            }

            GroupedStation groupedStation = plugin.getStationManager().getGroupedStation(station.get());

            plugin.getStationManager().deleteStation(station.get());

            sender.sendMessage(ChatColor.GREEN + "Die Station '" + station.get().getDisplayName() + "' wurde erfolgreich gelöscht!");
        }

        @Command(
                aliases = {"tp", "warp", "teleport"},
                desc = "Warp to station",
                min = 1,
                usage = "<Station>"
        )
        @CommandPermissions("rctravel.cmd.tp")
        public void warp(CommandContext args, CommandSender sender) throws CommandException {

            if (!(sender instanceof Player)) throw new CommandException("Player required!");
            Player player = (Player) sender;

            // check if station exists
            Optional<Station> station = plugin.getStationManager().getStation(args.getJoinedStrings(0));
            if (!station.isPresent()) {
                throw new CommandException("Es gibt keine Station mit dem namen '" + args.getJoinedStrings(0) + "'!");
            }

            player.teleport(station.get().getLocation());

            sender.sendMessage(ChatColor.GREEN + "Du wurdest zur Station '" + station.get().getDisplayName() + "' teleportiert!");
        }

        @Command(
                aliases = {"arrive", "come"},
                desc = "Forced station to get unlocked",
                min = 1,
                usage = "<Station>"
        )
        @CommandPermissions("rctravel.cmd.tp")
        public void arrive(CommandContext args, CommandSender sender) throws CommandException {

            if (!(sender instanceof Player)) throw new CommandException("Player required!");
            Player player = (Player) sender;

            // check if station exists
            Optional<Station> station = plugin.getStationManager().getStation(args.getJoinedStrings(0));
            if (!station.isPresent()) {
                throw new CommandException("Es gibt keine Station mit dem namen '" + args.getJoinedStrings(0) + "'!");
            }

            plugin.getStationLockTask().setLocked(station.get(), false);
        }
    }
}
