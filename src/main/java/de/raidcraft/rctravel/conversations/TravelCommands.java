package de.raidcraft.rctravel.conversations;

import com.sk89q.minecraft.util.commands.*;
import de.raidcraft.rctravel.RCTravelPlugin;
import org.bukkit.command.CommandSender;

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
            plugin.getStationManager().loadStations();
            plugin.getGroupManager().loadGroups();
        }

        @Command(
                aliases = {"create"},
                desc = "Create travel station",
                min = 2,
                usage = "<group> <name>"
        )
        @CommandPermissions("rctravel.cmd.create")
        public void create(CommandContext args, CommandSender sender) throws CommandException {


        }
    }
}
