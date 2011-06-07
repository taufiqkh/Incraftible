package com.quiptiq.nocraft;

/**
 * Dumping ground for message constants.
 */
public final class Message {
    /**
     * Private constructor.
     */
    private Message() {
    }

    /**
     * {@value}.
     */
    public static final String LOG_PREFIX = "[NoCraft] ";

    /**
     * {@value}.
     */
    public static final String LOG_WARN_NO_BUKKITCONTRIB = LOG_PREFIX
            + "BukkitContrib not found. Can't intercept crafting events.";

    /**
     * {@value}.
     */
    public static final String LOG_WARN_NO_CONFIG = LOG_PREFIX
            + "Config has not been loaded - plugin did not load correctly.";

    /**
     * {@value}.
     */
    public static final String LOG_WARN_INVALID_KEY = LOG_PREFIX + "Did not understand config key \"%s\"";

    /**
     * {@value}.
     */
    public static final String LOG_WARN_NULL_DISALLOWED_ITEM_ID = LOG_PREFIX + "Null disallowed item id found";

    /**
     * {@value}.
     */
    public static final String LOG_WARN_INVALID_DISALLOWED_ITEM_ID = LOG_PREFIX + "Invalid disallowed item id %i";

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
     * {@value}.
     */
    public static final String LOG_DEFAULT_CONFIG = LOG_PREFIX + "No config file detected, loading defaults.";
}
