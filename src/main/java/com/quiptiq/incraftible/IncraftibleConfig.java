package com.quiptiq.incraftible;

import static com.quiptiq.incraftible.message.FixedMessage.LOG_PREFIX;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.jar.JarFile;
import java.util.logging.Logger;

import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.TreeType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Dye;
import org.bukkit.material.MaterialData;
import org.bukkit.material.Tree;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.util.config.Configuration;

import com.quiptiq.incraftible.message.Message;

/**
 * Configuration for Incraftible.
 *
 * TODO: Refactor the permissions logic into a dedicated class.
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

    public static final String PERMISSION_SEPARATOR = ".";

    public static final String PERMISSION_WILDCARD = "*";

    /**
     * Special case for dye, which for various reasons is represented by the
     * INK_SACK Material.
     */
    private static final String PERMISSION_DYE = "dye";

    private static final String PERMISSION_DYE_ALL = PERMISSION_DYE + ".*";

    /**
     * Map from each material to the name-based permission that controls its
     * crafting.
     */
    private static final Map<Material, String> PERMISSION_NAMES;

    private static final Map<Material, Map<Byte, String>> PERMISSION_DATA_NAMES;

    static {
        HashMap<Material, String> permissionNames = new HashMap<Material, String>();
        HashMap<Material, Map<Byte, String>> permissionDataNames = new HashMap<Material, Map<Byte, String>>();
        for (Material material : Material.values()) {
            if (material.equals(Material.INK_SACK)) {
                HashMap<Byte, String> dataNames = new HashMap<Byte, String>();
                for (DyeColor color : DyeColor.values()) {
                    dataNames.put(color.getData(),
                            PERMISSION_PREFIX + PERMISSION_DYE + PERMISSION_SEPARATOR +
                            color.toString().toLowerCase());
                }
                permissionDataNames.put(material, Collections.unmodifiableMap(dataNames));
            } else {
                permissionNames.put(material, PERMISSION_PREFIX + material.toString().toLowerCase());
            }
        }
        PERMISSION_NAMES = Collections.unmodifiableMap(permissionNames);
        PERMISSION_DATA_NAMES = Collections.unmodifiableMap(permissionDataNames);
    }

    private static final Logger log = Logger.getLogger(Incraftible.DEFAULT_LOGGER);

    private Configuration config;

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
        for (Permission permission : parentPermissions) {
            if (PERMISSION_STANDARD.equals(permission.getName())) {
                // Standard permission found, now iterate and set material
                // permissions
                return createDefaultMaterialPermissions(permission.getChildren().keySet());
            }
        }
        return new ArrayList<Permission>();
    }

    private List<Permission> createDefaultMaterialPermissions(Set<String> childNames) {
        ArrayList<Permission> materialPermissions = new ArrayList<Permission>();
        for (String childName : childNames) {
            if (!childName.startsWith(PERMISSION_PREFIX)) {
                log.warning(LOG_PREFIX + "Unrecognised permission while creating defaults: " + childName);
                continue;
            }
            String materialName = childName.substring(PERMISSION_PREFIX.length());
            Material material;
            if (PERMISSION_DYE_ALL.equals(materialName)) {
                material = Material.INK_SACK;
            } else {
                material = Material.matchMaterial(materialName);
                if (material== null) {
                    log.warning(LOG_PREFIX + "Can't match material with name: " + materialName);
                    continue;
                }
            }

            Class<? extends MaterialData> dataClass = material.getData();
            // For supported materials with data, add the data permissions
            if (dataClass != null && dataClass.equals(Dye.class)) {
                addMaterialDataPermissions(
                        materialPermissions, childName, Dye.class, Arrays.asList(DyeColor.values()));
            } else if (dataClass != null && dataClass.equals(Tree.class)) {
                addMaterialDataPermissions(
                        materialPermissions, childName, Tree.class, Arrays.asList(TreeType.values()));
            } else {
                materialPermissions.add(new Permission(childName, PermissionDefault.TRUE));
            }
        }
        return materialPermissions;
    }

    /**
     * Adds material data permissions as leaf nodes defaulting to true, then add
     * the material as a wildcard defaulting to false.
     *
     * @param materialPermissions
     *            List of permissions to which nodes will be added.
     * @param nodeName
     *            Name of the permission name with the material.
     * @param dataClass
     *            Class of the material data.
     * @param dataEnums
     *            Values of the named enumerator.
     */
    private void addMaterialDataPermissions(List<Permission> materialPermissions, String nodeName,
            Class<? extends MaterialData> dataClass, List<? extends Enum<?>> dataEnums) {
        HashMap<String, Boolean> dataPermissions = new HashMap<String, Boolean>();
        for (Enum<?> dataEnum : dataEnums) {
            String dataPermissionName = dataEnum.toString().toLowerCase();
            dataPermissions.put(dataPermissionName, true);
            materialPermissions.add(new Permission(dataPermissionName, PermissionDefault.TRUE));
        }
        materialPermissions.add(new Permission(
                nodeName + PERMISSION_SEPARATOR + PERMISSION_WILDCARD,
                PermissionDefault.FALSE, dataPermissions));
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
     * Whether or not the specified item is allowed.
     *
     * @param item
     *            Item to be checked. If null, returns false.
     * @return True if the item is allowed, otherwise false.
     */
    public boolean isItemAllowed(Material item, ItemStack stack, Player player) {
        String permissionName;
        if (item == null) {
            return false;
        }
        if (PERMISSION_NAMES.containsKey(item)) {
            permissionName = PERMISSION_NAMES.get(item);
        } else if (PERMISSION_DATA_NAMES.containsKey(item)) {
            if (stack == null) {
                log.warning(LOG_PREFIX + "Invalid stack " + item.toString());
                return false;
            }
            MaterialData materialData = stack.getData();
            if (materialData == null) {
                log.warning(LOG_PREFIX + "No material data for expected item material" + item.toString());
                return false;
            }
            permissionName = PERMISSION_DATA_NAMES.get(item).get(materialData.getData());
            if (permissionName == null) {
                log.warning(LOG_PREFIX + "No permission name stored for " + item.toString() + ":" + materialData.getData());
                return false;
            }
        } else {
            log.warning(LOG_PREFIX + "No permission stored for material " + item.toString());
            return false;
        }
        return player.hasPermission(permissionName);
    }

    /**
     * Logs all incraftible permissions for the specified player.
     *
     * @param player
     *            Player for which permissions are logged.
     */
    public void logCraftPermissions(Player player) {
        if (player == null) {
            log.warning(LOG_PREFIX + "Can't log permissions for null player");
        }
        TreeSet<String> sortedLogEntries = new TreeSet<String>();
        for (PermissionAttachmentInfo info : player.getEffectivePermissions()) {
            String permissionName = info.getPermission();
            if (!permissionName.startsWith(PERMISSION_PREFIX)) {
                continue;
            }
            sortedLogEntries.add(permissionName + ":" + info.getValue());
        }
        for (String logEntry : sortedLogEntries) {
            log.info(logEntry);
        }
    }
}
