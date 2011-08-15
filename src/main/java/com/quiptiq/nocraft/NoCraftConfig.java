package com.quiptiq.nocraft;

import static com.quiptiq.nocraft.Message.LOG_WARN_NO_BUKKIT_CONFIG;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.util.config.Configuration;

/**
 * Configuration for NoCraft.
 *
 * <p>
 * NOTE:
 * <p>
 * Overengineered at the moment, while exploring how Bukkit config works.
 *
 * @author Taufiq Hoven
 */
public class NoCraftConfig {

    private static final String PERMISSION_PREFIX = "nocraft.craft.";

    private static final Map<Material, String> PERMISSION_NAMES;

    static {
        HashMap<Material, String> permissionNames = new HashMap<Material, String>();
        for (Material material : Material.values()) {
            permissionNames.put(material, PERMISSION_PREFIX + material.toString().toLowerCase());
        }
        PERMISSION_NAMES = Collections.unmodifiableMap(permissionNames);
    }

    private final Logger log = Logger.getLogger(NoCraft.DEFAULT_LOGGER);

    private Configuration config;

    private final HashSet<Material> disallowedItems = new HashSet<Material>();

    /**
     * Creates a new NoCraftConfig based on the specified Bukkit configuration.
     *
     * @param newConfig
     *            Bukkit config on which properties are retrieved.
     */
    public NoCraftConfig(Configuration newConfig) {
        loadConfig(newConfig);
    }

    /**
     * Loads configuration from the specified Bukkit configuration, discarding
     * any current settings.
     *
     * @param newConfig
     *            Bukkit config on which properties are retrieved.
     */
    public void loadConfig(Configuration newConfig) {
        config = newConfig;
        if (config == null) {
            log.warning(LOG_WARN_NO_BUKKIT_CONFIG);
            return;
        }

    }

    /**
     * Disallows the specified item and saves the config. If the item is already disallowed, does nothing.
     *
     * @param material
     *            Material of item to disallow.
     * @return True if the disallow was successful, false if the item was null.
     */
    public boolean disallowItem(Material material) {
        if (material == null) {
//            log.warning(String.format(LOG_WARN_INVALID_DISALLOWED_ITEM_ID, material));
            return false;
        }
        disallowedItems.add(material);
        return saveDisallowedItems();
    }

    /**
     * Allows the specified item and saves the config. If the item is already allowed, does nothing.
     *
     * @param material
     *            Id of the item to allow.
     * @return True if the allow was successful, false if the item id was
     *         invalid.
     */
    public boolean allowItem(Material material) {
        if (material == null) {
//            log.warning(String.format(LOG_WARN_INVALID_DISALLOWED_ITEM_ID, material));
            return false;
        }
        disallowedItems.remove(material);
        return saveDisallowedItems();
    }

    /**
     * Saves currently disallowed items.
     *
     * @return True if the save was successful, otherwise false.
     */
    private boolean saveDisallowedItems() {
        List<Integer> itemIds = new ArrayList<Integer>();
        for (Material item : disallowedItems) {
            itemIds.add(item.getId());
        }
        config.setProperty(ConfigType.DISALLOWED_ITEM.key, itemIds);
        return config.save();
    }

    /**
     * Whether or not the specified item is allowed.
     *
     * @param item
     *            Item to be checked. If null, returns false.
     * @return True if the item is allowed, otherwise false.
     */
    public boolean isItemAllowed(Material item, Player player) {
        return item != null && player.hasPermission(PERMISSION_NAMES.get(item));
    }

    /**
     * Returns a set of all currently disallowed items.
     *
     * @return  Set of all disallowed items.
     */
    public Set<Material> getDisallowedItems() {
        return new HashSet<Material>(disallowedItems);
    }

    /**
     * Configuration types. Only lower case for this implementation.
     */
    private enum ConfigType {
        /**
         * List of disallowed item(s).
         */
        DISALLOWED_ITEM("disallowed");

        /**
         * Reverse mapping from string to config type.
         */
        private static final Map<String, ConfigType> CONFIG_TYPE_MAP;

        static {
            // Build the mapping from string to config type.
            HashMap<String, ConfigType> typeMap = new HashMap<String, ConfigType>();
            for (ConfigType type : EnumSet.allOf(ConfigType.class)) {
                typeMap.put(type.key, type);
            }
            CONFIG_TYPE_MAP = Collections.unmodifiableMap(typeMap);
        }

        /**
         * Key of the config item in the file.
         */
        private final String key;

        /**
         * Create a new ConfigType with the specified key.
         *
         * @param configKey
         *            Key to match in config files.
         */
        private ConfigType(String configKey) {
            this.key = configKey;
        }

        /**
         * Whether or not the specified key is understood.
         *
         * @param key
         *            Key to check
         * @return True if the key is used and understood by the config,
         *         otherwise false.
         */
        @SuppressWarnings("unused")
        public static boolean isKeyUnderstood(String key) {
            return key != null && CONFIG_TYPE_MAP.containsKey(key.toLowerCase());
        }
    }
}
