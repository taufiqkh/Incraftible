package com.quiptiq.nocraft;

import static com.quiptiq.nocraft.Message.*;

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

        if (!sender.isOp()) {
            sender.sendMessage("You don't have access to that command.");
        } else if (args.length == 0) {
            for (String message : COMMAND_USAGE) {
                sender.sendMessage(message);
            }
        } else if (SUBCOMMAND_DISALLOW.equalsIgnoreCase(args[0])) {
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
        } else if (SUBCOMMAND_ALLOW.equalsIgnoreCase(args[0])) {
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
        }
        return true;
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
