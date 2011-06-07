package com.quiptiq.nocraft;

import static com.quiptiq.nocraft.Message.LOG_WARN_INVALID_DISALLOWED_ITEM_ID;
import static com.quiptiq.nocraft.Message.LOG_WARN_INVALID_KEY;
import static com.quiptiq.nocraft.Message.LOG_WARN_NULL_DISALLOWED_ITEM_ID;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import org.bukkit.Material;
import org.bukkit.util.config.Configuration;

/**
 * Configuration for NoCraft.
 *
 * <p>NOTE:
 * <p>Overengineered at the moment, while exploring how Bukkit config works.
 *
 * @author Taufiq Hoven
 */
public class NoCraftConfig {
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
        public static boolean isKeyUnderstood(String key) {
            return key != null && CONFIG_TYPE_MAP.containsKey(key.toLowerCase());
        }
    }

    private final ArrayList<Material> disallowedItems = new ArrayList<Material>();

    /**
     * Creates a new NoCraftConfig based on the specified Bukkit configuration.
     *
     * @param config
     *            Bukkit config on which properties are retrieved.
     * @param log
     *            Logger for this plugin.
     */
    public NoCraftConfig(Configuration config, Logger log) {
        Set<String> keys = config.getNodes("").keySet();

        for (String key : keys) {
            if (!ConfigType.isKeyUnderstood(key)) {
                log.warning(String.format(LOG_WARN_INVALID_KEY, key));
            }
        }
        if (keys.contains(ConfigType.DISALLOWED_ITEM.key)) {
            List<Integer> disallowedItemIds = config.getIntList(
                    ConfigType.DISALLOWED_ITEM.key, new ArrayList<Integer>());
            for (Integer disallowedItemId : disallowedItemIds) {
                // Check for null
                if (disallowedItemId == null) {
                    log.warning(LOG_WARN_NULL_DISALLOWED_ITEM_ID);
                } else {
                    Material material = Material.getMaterial(disallowedItemId);
                    // Check for bad item id
                    if (material == null) {
                        log.warning(String.format(LOG_WARN_INVALID_DISALLOWED_ITEM_ID, disallowedItemId));
                    } else {
                        disallowedItems.add(material);
                    }
                }
            }
        }
    }
}
