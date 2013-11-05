package de.raidcraft.rctravel.commands;

import com.sk89q.minecraft.util.commands.*;
import de.raidcraft.api.RaidCraftException;
import de.raidcraft.rctravel.RCTravelPlugin;
import de.raidcraft.rctravel.api.group.Group;
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
    }
}
