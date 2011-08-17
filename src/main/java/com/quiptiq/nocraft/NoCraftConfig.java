package com.quiptiq.nocraft;

import static com.quiptiq.nocraft.Message.LOG_PREFIX;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.jar.JarFile;
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

    private static final String CONFIG_FILENAME = "config.yml";

    private static final String LINE_SEPARATOR = System.getProperty("line.separator");

    private static final String LOG_DEFAULT_CONFIG = LOG_PREFIX + "No config file found in %s, generating default.";

    private static final String LOG_WARN_FAILED_CREATING_CONFIG_DIRECTORY = LOG_PREFIX +
            "Couldn't create config directory %s";

    private static final String LOG_WARN_CLOSE_JAR_ENTRY = LOG_PREFIX + "Couldn't close jar entry reader";

    private static final String LOG_WARN_CLOSE_FILE_WRITER = LOG_PREFIX + "Couldn't close file writer";

    /**
     * Prefix for NoCraft crafting permissions.
     */
    private static final String PERMISSION_PREFIX = "nocraft.craft.";

    /**
     * Map from each material to the permission that controls its crafting.
     */
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
    public NoCraftConfig(NoCraft plugin, File pluginFile) {
        loadConfig(plugin, pluginFile);
    }

    /**
     * Loads configuration from the specified Bukkit configuration, discarding
     * any current settings.
     *
     * @param newConfig
     *            Bukkit config on which properties are retrieved.
     */
    public void loadConfig(NoCraft plugin, File pluginFile) {
        // Check for existing config file
        File configFile = new File(plugin.getDataFolder(), CONFIG_FILENAME);
        if (!configFile.exists()) {
            log.info(String.format(LOG_DEFAULT_CONFIG, configFile.getPath()));
            config = loadDefaultConfig(plugin, pluginFile, configFile);
        } else {
            config = plugin.getConfiguration();
        }
    }

    /**
     * Loads the default configuration for the specified plugin, using the given
     * plugin file and writes if out to the specified config file.
     *
     * @param plugin
     *            Plugin to be configured.
     * @param pluginFile
     *            Jar file containing the plugin.
     * @param configFile
     *            File that will contain the config.
     * @return
     */
    private Configuration loadDefaultConfig(NoCraft plugin, File pluginFile, File configFile) {
        // Use the default that is packaged in the NoCraft jar
        String nextLine = null;
        FileWriter writer = null;
        BufferedReader reader = null;
        if (configFile.getParent() != null && !(new File(configFile.getParent()).mkdirs())) {
            log.warning(String.format(LOG_WARN_FAILED_CREATING_CONFIG_DIRECTORY, configFile.getPath()));
            return null;
        }
        try {
            JarFile jar = new JarFile(pluginFile);
            reader = new BufferedReader(new InputStreamReader(jar.getInputStream(jar.getJarEntry(CONFIG_FILENAME))));
            writer = new FileWriter(configFile);
            while ((nextLine = reader.readLine()) != null) {
                writer.write(nextLine);
                writer.append(LINE_SEPARATOR);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    log.warning(LOG_WARN_CLOSE_JAR_ENTRY);
                }
            }
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    log.warning(LOG_WARN_CLOSE_FILE_WRITER);
                }
            }
        }
        Configuration config = plugin.getConfiguration();
        config.load();
        return config;
    }

    /**
     * Disallows the specified item and saves the config. If the item is already
     * disallowed, does nothing.
     *
     * @param material
     *            Material of item to disallow.
     * @return True if the disallow was successful, false if the item was null.
     */
    public boolean disallowItem(Material material) {
        if (material == null) {
            // log.warning(String.format(LOG_WARN_INVALID_DISALLOWED_ITEM_ID,
            // material));
            return false;
        }
        disallowedItems.add(material);
        return saveDisallowedItems();
    }

    /**
     * Allows the specified item and saves the config. If the item is already
     * allowed, does nothing.
     *
     * @param material
     *            Id of the item to allow.
     * @return True if the allow was successful, false if the item id was
     *         invalid.
     */
    public boolean allowItem(Material material) {
        if (material == null) {
            // log.warning(String.format(LOG_WARN_INVALID_DISALLOWED_ITEM_ID,
            // material));
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
        //config.setProperty(ConfigType.DISALLOWED_ITEM.key, itemIds);
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
     * @return Set of all disallowed items.
     */
    public Set<Material> getDisallowedItems() {
        return new HashSet<Material>(disallowedItems);
    }
}
