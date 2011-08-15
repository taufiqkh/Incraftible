package com.quiptiq.nocraft;

import static com.quiptiq.nocraft.Message.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

/**
 * Handles NoCraft commands.
 *
 * @author Taufiq Hoven
 */
public class CommandHandler implements CommandExecutor {
    /**
     * String for NoCraft commands.
     */
    public static final String COMMAND = "nc";

    private static final String SUBCOMMAND_ALLOW = "allow";

    private static final String SUBCOMMAND_DISALLOW = "disallow";

    private static final String SUBCOMMAND_LIST = "list";

    private static final int MAX_ITEMS_PER_LINE = 5;

    /**
     * Parent permission for NoCraft commands.
     */
    private static final String PERMISSION_COMMAND_PARENT = "nocraft.command";

    /**
     *
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
        String[] subCommands = {SUBCOMMAND_ALLOW, SUBCOMMAND_DISALLOW, SUBCOMMAND_LIST};
        for (String subCommand : subCommands) {
            permissions.put(subCommand, PERMISSION_COMMAND_PARENT + "." + subCommand);
        }
        COMMAND_PERMISSIONS = Collections.unmodifiableMap(permissions);
    }

    private final NoCraftConfig config;

    private final Logger log = Logger.getLogger(NoCraft.DEFAULT_LOGGER);

    /**
     * Creates a new command handler with the specified configuration.
     *
     * @param newConfig
     *            Config to use.
     */
    public CommandHandler(NoCraftConfig newConfig) {
        config = newConfig;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!COMMAND.equalsIgnoreCase(command.getName())) {
            return false;
        }

        if (args.length == 0) {
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
        } else if (isSubCommandToExecute(SUBCOMMAND_DISALLOW, args[0], sender)) {
            if (args.length != 2) {
                sender.sendMessage(PLAYER_MESSAGE_DISALLOW_USAGE);
                return true;
            }

            Material item = validateItemId(sender, args[1]);
            if (item != null) {
                // Item corresponds to a valid material
                if (config.disallowItem(item)) {
                    sender.sendMessage(String.format(PLAYER_MESSAGE_DISALLOW_SUCCESS, item));
                } else {
                    sender.sendMessage(PLAYER_MESSAGE_COMMAND_FAIL);
                }
            }
        } else if (isSubCommandToExecute(SUBCOMMAND_ALLOW, args[0], sender)) {
            if (args.length != 2) {
                sender.sendMessage(PLAYER_MESSAGE_ALLOW_USAGE);
                return true;
            }

            Material item = validateItemId(sender, args[1]);
            if (item != null) {
                // Item corresponds to a valid material.
                if (config.allowItem(item)) {
                    sender.sendMessage(String.format(PLAYER_MESSAGE_ALLOW_SUCCESS, item));
                } else {
                    sender.sendMessage(PLAYER_MESSAGE_COMMAND_FAIL);
                }
            }
        } else if (isSubCommandToExecute(SUBCOMMAND_LIST, args[0], sender)) {
            Set<Material> disallowedItems = config.getDisallowedItems();
            if (disallowedItems.size() == 0) {
                sender.sendMessage(PLAYER_MESSAGE_LIST_NONE);
                return true;
            }
            // Iterate through, 5 items per line
            StringBuilder itemList = new StringBuilder();
            String delimiter = " ";
            int itemsInRow = 0;
            sender.sendMessage(PLAYER_MESSAGE_LIST_HEADER);
            for (Material item : disallowedItems) {
                // could be tighter, but it works
                if (itemsInRow > 0) {
                    itemList.append(delimiter);
                }
                itemList.append(item.toString());
                itemsInRow++;
                if (itemsInRow >= MAX_ITEMS_PER_LINE) {
                    sender.sendMessage(itemList.toString());
                    itemList.setLength(0);
                    itemsInRow = 0;
                }
            }
            if (itemsInRow > 0) {
                sender.sendMessage(itemList.toString());
            }
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

    /**
     * Validates the specified item id and if it is a valid number that matches
     * a material id, returns the material for that id. If not, informs the
     * sender and returns null.
     *
     * @param sender
     *            Sender of the id
     * @param idToValidate
     *            Id to be validated.
     * @return A material for the specified id if it is valid, otherwise null.
     */
    private Material validateItemId(CommandSender sender, String idToValidate) {
        int itemId;
        try {
            itemId = Integer.parseInt(idToValidate);
        } catch (NumberFormatException e) {
            sender.sendMessage(String.format(PLAYER_MESSAGE_BAD_ITEM_ID, idToValidate));
            return null;
        }
        Material material = Material.getMaterial(itemId);
        if (material == null) {
            log.warning(String.format(LOG_WARN_INVALID_DISALLOWED_ITEM_ID, itemId));
        }
        return material;
    }
}
