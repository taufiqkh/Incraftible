package com.quiptiq.incraftible;

import static com.quiptiq.incraftible.message.FixedMessage.COMMAND_PREFIX;
import static com.quiptiq.incraftible.message.FixedMessage.COMMAND_USAGE;
import static com.quiptiq.incraftible.message.FixedMessage.PLAYER_MESSAGE_LOGPERMS_USAGE;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

/**
 * Handles Incraftible commands.
 *
 * @author Taufiq Hoven
 */
public class CommandHandler implements CommandExecutor {
    private static final String SUBCOMMAND_LOGPERMS = "logperms";

    /**
     * Parent permission for Incraftible commands.
     */
    private static final String PERMISSION_COMMAND_PARENT = IncraftibleConfig.PERMISSION_ROOT + ".command";

    /**
     * Permission for all Incraftible commands.
     */
    private static final String PERMISSION_COMMAND_ALL = PERMISSION_COMMAND_PARENT + ".*";

    /**
     * Map of commands to the permission that allows it.
     */
    private static final Map<String, String> COMMAND_PERMISSIONS;

    /**
     * Initialisation for command permissions.
     */
    static {
        HashMap<String, String> permissions = new HashMap<String, String>();
        String[] subCommands = {SUBCOMMAND_LOGPERMS};
        for (String subCommand : subCommands) {
            permissions.put(subCommand, PERMISSION_COMMAND_PARENT + "." + subCommand);
        }
        COMMAND_PERMISSIONS = Collections.unmodifiableMap(permissions);
    }

    private final IncraftibleConfig config;

    /**
     * Creates a new command handler with the specified configuration.
     *
     * @param newConfig
     *            Config to use.
     */
    public CommandHandler(IncraftibleConfig newConfig) {
        config = newConfig;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!COMMAND_PREFIX.equalsIgnoreCase(command.getName())) {
            return false;
        }

        if (args.length == 0) {
            // If sender has permission for a command, spell out the usage
            for (String permissionName : COMMAND_PERMISSIONS.keySet()) {
                if (sender.hasPermission(permissionName)) {
                    for (String message : COMMAND_USAGE) {
                        sender.sendMessage(message);
                    }
                    return true;
                }
                // No permissions, silently pass along
                return false;
            }
        } else if (isSubCommandToExecute(SUBCOMMAND_LOGPERMS, args[0], sender)) {
            if (args.length != 2) {
                sender.sendMessage(PLAYER_MESSAGE_LOGPERMS_USAGE);
                return true;
            }
            config.logCraftPermissions(sender.getServer().getPlayer(args[1]));
        } else {
            return false;
        }
        return true;
    }

    /**
     * Given the specified argument and sender, determines whether the subcommand is to be executed.
     * @param subCommand
     * @param argument
     * @param sender
     * @return
     */
    private boolean isSubCommandToExecute(String subCommand, String argument, CommandSender sender) {
        return subCommand.equalsIgnoreCase(argument) && (
                sender.hasPermission(COMMAND_PERMISSIONS.get(subCommand))
                || sender.hasPermission(PERMISSION_COMMAND_ALL));
    }
}
