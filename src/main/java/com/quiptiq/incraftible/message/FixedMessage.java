package com.quiptiq.incraftible.message;

/**
 * Dumping ground for message constants.
 */
public final class FixedMessage {
    /**
     * Private constructor.
     */
    private FixedMessage() {
    }

    /**
     * Version of Incraftible: {@value}
     */
    public static final String VERSION = "0.6";

    public static final String PLUGIN_NAME = "Incraftible";

    /**
     * {@value}.
     */
    public static final String LOG_PREFIX = "[" + PLUGIN_NAME + "] ";

    /**
     * {@value}.
     */
    public static final String LOG_WARN_NO_SPOUT = LOG_PREFIX
            + "Spout not found. Can't intercept crafting events.";

    /**
     * {@value}.
     */
    public static final String LOG_WARN_NO_CONFIG = LOG_PREFIX
            + "Config has not been loaded - plugin did not load correctly.";

    /**
     * {@value}.
     */
    public static final String LOG_WARN_NO_BUKKIT_CONFIG = LOG_PREFIX
            + "Error loading file using Bukkit config.";

    /**
     * {@value}.
     */
    public static final String LOG_WARN_INVALID_KEY = LOG_PREFIX + "Did not understand config key \"%s\"";

    /**
     * {@value}.
     */
    public static final String LOG_WARN_INVALID_DISALLOWED_ITEM_ID = LOG_PREFIX + "Invalid disallowed item id %s";

    /**
     * {@value}.
     */
    public static final String USAGE = LOG_PREFIX + "Plugin should be placed in the plugins directory of the Minecraft"
            + " server.";

    /**
     * {@value}.
     */
    public static final String ENTER_TO_CONTINUE = LOG_PREFIX + "Please press Enter to continue.";

    /**
     * @{value}.
     */
    public static final String LOG_ITEM_DISALLOW_LIST = LOG_PREFIX + "Config file loaded, disallowed items are: %s";

    /**
     * @{value}.
     */
    public static final String LOG_ITEM_DISALLOW_NONE = LOG_PREFIX + "Config file loaded, no items are disallowed.";

    /**
     * @{value}.
     */
    public static final String LOG_ITEM_CRAFT_ATTEMPT = "Prevented player %s from crafting %s";

    /**
     * @{value}.
     */
    public static final String LOG_ENABLED = LOG_PREFIX + PLUGIN_NAME + " " + VERSION + " enabled.";

    /**
     * @{value}.
     */
    public static final String LOG_DISABLED = LOG_PREFIX + "Plugin disabled";

    /**
     * @{value}.
     */
    public static final String[] COMMAND_USAGE = {
            PLUGIN_NAME + " " + VERSION + " by Taufiq Hoven",
            "/nc disallow <item number> Disallows crafting an item",
            "/nc allow <item number>    Allows crafting an item",
            "/nc list                   Lists all disallowed items"
    };

    /**
     * @{value}.
     */
    public static final String PLAYER_MESSAGE_ALLOW_USAGE = "Usage: /ic allow <itemNumber>";

    /**
     * @{value}.
     */
    public static final String PLAYER_MESSAGE_DISALLOW_USAGE = "Usage: /ic disallow <itemNumber>";

    /**
     * @{value}.
     */
    public static final String PLAYER_MESSAGE_ALLOW_SUCCESS = "Crafting of %s is now allowed.";

    /**
     * @{value}.
     */
    public static final String PLAYER_MESSAGE_DISALLOW_SUCCESS = "Crafting of %s is now disallowed.";

    /**
     * @{value}.
     */
    public static final String PLAYER_MESSAGE_COMMAND_FAIL = "Command failed! Possibly an error saving config.";

    /**
     * @{value}.
     */
    public static final String PLAYER_MESSAGE_LIST_NONE = "No items are currently disallowed from crafting.";

    /**
     * @{value}.
     */
    public static final String PLAYER_MESSAGE_LIST_HEADER = "The following items are disallowed:";
}
