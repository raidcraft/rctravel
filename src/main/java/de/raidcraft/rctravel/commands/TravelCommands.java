package de.raidcraft.rctravel.commands;

import com.sk89q.minecraft.util.commands.*;
import de.raidcraft.api.RaidCraftException;
import de.raidcraft.rctravel.RCTravelPlugin;
import de.raidcraft.rctravel.api.group.Group;
import de.raidcraft.rctravel.api.station.SchematicStation;
import de.raidcraft.rctravel.api.station.Station;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * @author Philip Urban
 */
public class TravelCommands {

    private RCTravelPlugin plugin;

    public TravelCommands(RCTravelPlugin plugin) {

        this.plugin = plugin;
    }

    @Command(
            aliases = {"travel", "rctravel", "rct"},
            desc = "Travel commands"
    )
    @NestedCommand(value = NestedCommands.class)
    public void travel(CommandContext args, CommandSender sender) throws CommandException {
    }

    public static class NestedCommands {

        private final RCTravelPlugin plugin;

        public NestedCommands(RCTravelPlugin plugin) {

            this.plugin = plugin;
        }

        @Command(
                aliases = {"reload"},
                desc = "Reload travel plugin"
        )
        @CommandPermissions("rctravel.cmd.reload")
        public void reload(CommandContext args, CommandSender sender) throws CommandException {

            plugin.reload();
            sender.sendMessage(ChatColor.GREEN + "RCTravel wurde neugeladen!");
        }

        @Command(
                aliases = {"create"},
                desc = "Create travel station",
                min = 2,
                usage = "<group> <name>"
        )
        @CommandPermissions("rctravel.cmd.create")
        public void create(CommandContext args, CommandSender sender) throws CommandException {

            if(!(sender instanceof Player)) throw new CommandException("Player required!");
            Player player = (Player)sender;

            // check if group exists
            Group group = plugin.getGroupManager().getGroup(args.getString(0));
            if(group == null) {
                throw new CommandException("Es gibt keine Gruppe mit dem namen '" + args.getString(0) + "'!");
            }

            try {
                plugin.getStationManager().createStation(args.getString(1), player, group);
            } catch (RaidCraftException e) {
                throw new CommandException(e.getMessage());
            }

            sender.sendMessage(ChatColor.GREEN + "Die Station '" + args.getString(1) + "' wurde erfolgreich erstellt!");
        }

        @Command(
                aliases = {"delete"},
                desc = "Delete travel station",
                min = 2,
                usage = "<group> <name>"
        )
        @CommandPermissions("rctravel.cmd.create")
        public void delete(CommandContext args, CommandSender sender) throws CommandException {

            if(!(sender instanceof Player)) throw new CommandException("Player required!");
            Player player = (Player)sender;

            // check if group exists
            Group group = plugin.getGroupManager().getGroup(args.getString(0));
            if(group == null) {
                throw new CommandException("Es gibt keine Gruppe mit dem namen '" + args.getString(0) + "'!");
            }

            // check if station exists
            Station station = plugin.getStationManager().getStation(group, args.getString(1));
            if(station == null) {
                throw new CommandException("Es gibt keine Station mit dem namen '" + args.getString(1) + "'!");
            }

            try {
                plugin.getStationManager().deleteStation(group, station);
            } catch (RaidCraftException e) {
                throw new CommandException(e.getMessage());
            }

            sender.sendMessage(ChatColor.GREEN + "Die Station '" + args.getString(1) + "' wurde erfolgreich gelöscht!");
        }

        @Command(
                aliases = {"schematic", "sch"},
                desc = "Recreate station schematic",
                min = 2,
                usage = "<Group> <Station> -l (locked/unlocked)"
        )
        @CommandPermissions("rctravel.cmd.reload")
        public void schematic(CommandContext args, CommandSender sender) throws CommandException {

            if(!(sender instanceof Player)) throw new CommandException("Player required!");
            Player player = (Player)sender;

            // check if group exists
            Group group = plugin.getGroupManager().getGroup(args.getString(0));
            if(group == null) {
                throw new CommandException("Es gibt keine Gruppe mit dem namen '" + args.getString(0) + "'!");
            }

            Station station = plugin.getStationManager().getStation(group, args.getString(1));
            boolean locked = args.hasFlag('l');

            if(!(station instanceof SchematicStation)) {
                throw new CommandException("Diese Station unterstüzt keine Schematics!");
            }

            try {
                ((SchematicStation) station).createSchematic(locked);
            } catch (RaidCraftException e) {
                throw new CommandException(e.getMessage());
            }
        }
    }
}
