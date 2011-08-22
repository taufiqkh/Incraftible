package com.quiptiq.incraftible;

import static com.quiptiq.incraftible.message.FixedMessage.LOG_PREFIX;

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
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.util.config.Configuration;

import com.quiptiq.incraftible.message.Message;

/**
 * Configuration for Incraftible.
 *
 * <p>
 * NOTE:
 * <p>
 * Overengineered at the moment, while exploring how Bukkit config works.
 *
 * @author Taufiq Hoven
 */
public class IncraftibleConfig {

    private static final String CONFIG_FILENAME = "config.yml";

    private static final String LINE_SEPARATOR = System.getProperty("line.separator");

    private static final String LOG_DEFAULT_CONFIG = LOG_PREFIX + "No config file found in %s, generating default.";

    private static final String LOG_WARN_FAILED_CREATING_CONFIG_DIRECTORY = LOG_PREFIX
            + "Couldn't create config directory %s";

    private static final String LOG_WARN_CLOSE_JAR_ENTRY = LOG_PREFIX + "Couldn't close jar entry reader";

    private static final String LOG_WARN_CLOSE_FILE_WRITER = LOG_PREFIX + "Couldn't close file writer";

    /**
     * Root node for all incraftible permissions.
     */
    public static final String PERMISSION_ROOT = "incraftible";

    /**
     * Prefix for Incraftible crafting permissions.
     */
    private static final String PERMISSION_PREFIX = PERMISSION_ROOT + ".craft.";

    /**
     * Standard permissions as per vanilla Minecraft.
     */
    public static final String PERMISSION_STANDARD = PERMISSION_PREFIX + "standard";

    /**
     * Special case for dye, which for various reasons is represented by the
     * INK_SACK Material.
     */
    private static final String PERMISSION_DYES = "dye.*";

    /**
     * Map from each material to the name-based permission that controls its
     * crafting.
     */
    private static final Map<Material, String> PERMISSION_NAMES;

    static {
        HashMap<Material, String> permissionNames = new HashMap<Material, String>();
        for (Material material : Material.values()) {
            if (material.equals(Material.INK_SACK)) {
                permissionNames.put(material, PERMISSION_PREFIX + PERMISSION_DYES);
            } else {
                permissionNames.put(material, PERMISSION_PREFIX + material.toString().toLowerCase());
            }
        }
        PERMISSION_NAMES = Collections.unmodifiableMap(permissionNames);
    }

    private static Logger log = Logger.getLogger(Incraftible.DEFAULT_LOGGER);

    private Configuration config;

    private final HashSet<Material> disallowedItems = new HashSet<Material>();

    /**
     * Creates a new IncraftibleConfig based on the specified Bukkit
     * configuration.
     *
     * @param newConfig
     *            Bukkit config on which properties are retrieved.
     */
    public IncraftibleConfig(Incraftible plugin, File pluginFile) {
        loadConfig(plugin, pluginFile);
    }

    /**
     * Loads configuration from the specified Bukkit configuration, discarding
     * any current settings.
     *
     * @param newConfig
     *            Bukkit config on which properties are retrieved.
     */
    public void loadConfig(Incraftible plugin, File pluginFile) {
        // Check for existing config file
        File configFile = new File(plugin.getDataFolder(), CONFIG_FILENAME);
        if (!configFile.exists()) {
            log.info(String.format(LOG_DEFAULT_CONFIG, configFile.getPath()));
            config = loadDefaultConfig(plugin, pluginFile, configFile);
        } else {
            config = plugin.getConfiguration();
        }
        // Override configurable messages
        for (Message message : Message.values()) {
            String configuredMessage = config.getString(message.getConfigNode());
            if (configuredMessage != null) {
                message.overrideMessage(configuredMessage);
            }
        }
    }

    /**
     * Finds the standard parent permission in a list of permissions and
     * generates a list of default material name permissions based on the
     * children of that parent. The child nodes must be by material name, not
     * id.
     *
     * @param parentPermissions
     *            List of default permissions.
     * @return List of default material permissions.
     */
    public List<Permission> createDefaultMaterialPermissions(List<Permission> parentPermissions) {
    // Set up material permissions
        ArrayList<Permission> materialPermissions = new ArrayList<Permission>();
        for (Permission permission : parentPermissions) {
            if (PERMISSION_STANDARD.equals(permission.getName())) {
                // Standard permission found, now iterate and set material
                // permissions
                for (String childName : permission.getChildren().keySet()) {
                    if (!childName.startsWith(PERMISSION_PREFIX)) {
                        log.warning(LOG_PREFIX + "Unrecognised permission while creating defaults: " + childName);
                        continue;
                    }
                    String materialName = childName.substring(PERMISSION_PREFIX.length());
                    Material material;
                    if (PERMISSION_DYES.equals(materialName)) {
                        material = Material.INK_SACK;
                    } else {
                        material = Material.matchMaterial(materialName);
                        if (material== null) {
                            log.warning(LOG_PREFIX + "Can't match material with name: " + materialName);
                            continue;
                        }
                    }
                    materialPermissions.add(new Permission(childName, PermissionDefault.TRUE));
                }
                break;
            }
        }
        return materialPermissions;
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
    private Configuration loadDefaultConfig(Incraftible plugin, File pluginFile, File configFile) {
        // Use the default that is packaged in the Incraftible jar
        String nextLine = null;
        FileWriter writer = null;
        BufferedReader reader = null;
        if (configFile.getParent() != null) {
            // Create the parent directory if it doesn't exist already
            File parentDir = new File(configFile.getParent());
            if (!parentDir.exists() && !(parentDir.mkdirs())) {
                log.warning(String.format(LOG_WARN_FAILED_CREATING_CONFIG_DIRECTORY, configFile.getPath()));
                return null;
            }
        }

        // Read the default config file from the jar
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
     * TODO: Implement or remove this
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
     * TODO: Implement or remove this
     *
     * @return True if the save was successful, otherwise false.
     */
    private boolean saveDisallowedItems() {
        List<Integer> itemIds = new ArrayList<Integer>();
        for (Material item : disallowedItems) {
            itemIds.add(item.getId());
        }
        // config.setProperty(ConfigType.DISALLOWED_ITEM.key, itemIds);
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
        String permissionName = PERMISSION_NAMES.get(item);
        if (permissionName == null) {
            log.info("No permission defined for " + item.toString());
            return false;
        }
        log.fine(LOG_PREFIX + PERMISSION_NAMES.get(item) + ":" + player.hasPermission(PERMISSION_NAMES.get(item)));
        return item != null
                && (player.hasPermission(PERMISSION_NAMES.get(item)));
    }

    /**
     * Returns a set of all currently disallowed items.
     *
     * TODO: Implement or remove this.
     *
     * @return Set of all disallowed items.
     */
    public Set<Material> getDisallowedItems() {
        return new HashSet<Material>(disallowedItems);
    }
}
