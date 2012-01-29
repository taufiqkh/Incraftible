package com.quiptiq.incraftible;

import static com.quiptiq.incraftible.message.FixedMessage.LOG_PREFIX;

import java.util.*;
import java.util.logging.Logger;

import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.material.Dye;
import org.bukkit.material.MaterialData;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;

/**
 * Reference object for in-built permissions.

 * @author Taufiq Hoven
 */
public final class PermissionsReference {

    /**
     * Root node for all incraftible permissions.
     */
    public static final String PERMISSION_ROOT = "incraftible";

    /**
     * Node name for crafting permissions.
     */
    public static final String PERMISSION_CRAFT_NODE = "craft";

    /**
     * Prefix for Incraftible crafting permissions.
     */
    public static final String PERMISSION_CRAFT_PREFIX = PERMISSION_ROOT + ".craft.";

    /**
     * Standard permissions as per vanilla Minecraft.
     */
    public static final String PERMISSION_STANDARD = PERMISSION_CRAFT_PREFIX + "standard";

    private static final String PERMISSION_SEPARATOR = ".";

    private static final String PERMISSION_WILDCARD = "*";

    /**
     * Special case for dye, which for various reasons is represented by the
     * INK_SACK Material.
     */
    public static final String PERMISSION_DYE = "dye";

    private static final Map<Material, List<? extends Enum<?>>> MATERIAL_DATA_VALUES;

    /**
     * Generate a map of materials to the data enums that contain their values.
     */
    static {
        HashMap<Material, List<? extends Enum<?>>> dataEnums = new HashMap<Material, List<? extends Enum<?>>>();
        dataEnums.put(Material.INK_SACK, Arrays.asList(DyeColor.values()));
        dataEnums.put(Material.WOOL, Arrays.asList(DyeColor.values()));
        MATERIAL_DATA_VALUES = Collections.unmodifiableMap(dataEnums);
    }

    /**
     * Map from each material to the name-based permission that controls its
     * crafting.
     */
    private static final Map<Material, String> PERMISSION_NAMES;

    private static final Map<Material, Map<Byte, String>> PERMISSION_DATA_NAMES;

    /**
     * Generate a mapping from materials to their permissions.
     */
    static {
        HashMap<Material, String> permissionNames = new HashMap<Material, String>();
        HashMap<Material, Map<Byte, String>> permissionDataNames = new HashMap<Material, Map<Byte, String>>();
        for (Material material : Material.values()) {
            if (MATERIAL_DATA_VALUES.containsKey(material)) {
                String materialName = material.equals(Material.INK_SACK) ?
                        PERMISSION_DYE : material.toString().toLowerCase();
                Map<Byte, String> dataNames = createMaterialDataNames(material, materialName);
                permissionDataNames.put(material, Collections.unmodifiableMap(dataNames));
            } else {
                permissionNames.put(material, PERMISSION_CRAFT_PREFIX + material.toString().toLowerCase());
            }
        }
        PERMISSION_NAMES = Collections.unmodifiableMap(permissionNames);
        PERMISSION_DATA_NAMES = Collections.unmodifiableMap(permissionDataNames);
    }

    /**
     * Hides ugliness of data value assignment. Can't call common method so have
     * to check individual cases. This is the main place where the generic
     * methods won't work.
     *
     * @param material
     *            Material for which data names will be added.
     * @param materialName
     *            Name of the material.
     * @return Map of material data names.
     */
    @SuppressWarnings("unchecked")
    private static Map<Byte, String> createMaterialDataNames(Material material, String materialName) {
        HashMap<Byte, String> dataNames = new HashMap<Byte, String>();
        if (material.equals(Material.INK_SACK) || material.equals(Material.WOOL)) {
            Dye dyeDataGenerator = new Dye();
            for (DyeColor color : (List<DyeColor>) MATERIAL_DATA_VALUES.get(material)) {
                // Numeric value of dye color may not be the same as the data
                // value of a dye
                dyeDataGenerator.setColor(color);
                dataNames.put(dyeDataGenerator.getData(),
                        dataNodePermissionName(color, materialName));
            }
        }
        return dataNames;
    }

    private static final PermissionsReference manager = new PermissionsReference();

    /**
     * @return  Instance of PermissionsReference.
     */
    public static PermissionsReference getInstance() {
        return manager;
    }

    private PermissionsReference() {
        ;
    }


    private static final Logger log = Logger.getLogger(Incraftible.DEFAULT_LOGGER);

    /**
     * Creates a data node permission name for the specified node and parent.
     *
     * @param node
     *            Node from which the data name is derived.
     * @param parentName
     *            Name of the parent material.
     * @return Permission name for the specified data and parent.
     */
    public static String dataNodePermissionName(Enum<?> node, String parentName) {
        return buildPermissionName(PERMISSION_CRAFT_NODE, parentName, node.toString().toLowerCase());
    }

    /**
     * Builds a permission name from the specified hierarchy of nodes, where
     * the first node is the topmost parent and subsequent nodes descend.
     *
     * @param nodeNames
     *            Names of the nodes from which a permission hierarchy is
     *            formed.
     * @return Name of the permission for the specified nodes.
     */
    public static String buildPermissionName(String ... nodeNames) {
        return extendPermissionName(PERMISSION_ROOT, nodeNames);
    }

    /**
     * Extends the specified permission name with the specified hierarchy of
     * nodes.
     *
     * @param permissionName
     *            Permission to extend.
     * @param hierarchy
     *            Hierarchy of nodes, with the root node first.
     * @return Permission name for the specified base permission and hierarchy
     *         of descendants.
     */
    public static String extendPermissionName(String permissionName, String ... hierarchy) {
        StringBuilder permission = new StringBuilder(permissionName);
        for (int i = 0; i < hierarchy.length; i++) {
            permission.append(PERMISSION_SEPARATOR).append(hierarchy[i]);
        }
        return permission.toString();
    }

    private static String extendAsWildcard(String permissionName) {
        return permissionName + PERMISSION_SEPARATOR + PERMISSION_WILDCARD;
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
                return createDefaultMaterialPermissions(permission.getChildren().keySet(),
                        permission.getDefault());
            }
        }
        return new ArrayList<Permission>();
    }

    private List<Permission> createDefaultMaterialPermissions(Set<String> childNames, PermissionDefault val) {
        ArrayList<Permission> materialPermissions = new ArrayList<Permission>();
        for (String childName : childNames) {
            if (!childName.startsWith(PERMISSION_CRAFT_PREFIX)) {
                log.warning(LOG_PREFIX + "Unrecognised permission while creating defaults: " + childName);
                continue;
            }
            String materialName = childName.substring(PERMISSION_CRAFT_PREFIX.length());
            if (materialName.endsWith(PERMISSION_SEPARATOR + PERMISSION_WILDCARD)) {
                materialName = materialName.substring(0,
                        materialName.length() - PERMISSION_SEPARATOR.length() - PERMISSION_WILDCARD.length());
            }
            Material material;
            if (PERMISSION_DYE.equals(materialName)) {
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
            if (dataClass != null && MATERIAL_DATA_VALUES.containsKey(material)) {
                addMaterialDataPermissions(
                        materialPermissions, materialName, dataClass, MATERIAL_DATA_VALUES.get(material),
                        val);
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
            Class<? extends MaterialData> dataClass, List<? extends Enum<?>> dataEnums, PermissionDefault val) {
        HashMap<String, Boolean> dataPermissions = new HashMap<String, Boolean>();
        for (Enum<?> dataEnum : dataEnums) {
            String dataPermissionName = dataNodePermissionName(dataEnum, nodeName);
            dataPermissions.put(dataPermissionName, PermissionDefault.FALSE.equals(val) ? false : true);
            materialPermissions.add(new Permission(dataPermissionName, val));
        }
        materialPermissions.add(new Permission(extendAsWildcard(nodeName), val, dataPermissions));
    }

    /**
     * Whether or not there is a base (ie. non-data) permission for the
     * specified material.
     *
     * @param item
     *            Item to check.
     * @return True if there is a permission for the specified item, otherwise
     *         false.
     */
    public boolean hasBasePermission(Material item) {
        return PERMISSION_NAMES.containsKey(item);
    }

    /**
     * Gets the name of the base (ie. not-data) permission for the specified
     * item, if it exists.
     *
     * @param item
     *            Item for which a permission is retrieved.
     * @return Permission name for the specified item, or null if none exists.
     */
    public String getBasePermissionName(Material item) {
        return PERMISSION_NAMES.get(item);
    }

    /**
     * Whether or not there is a data permission for the specified material.
     *
     * @param item
     *            Material to check.
     * @return True if there is at least one data permission for the specified
     *         material, otherwise false.
     */
    public boolean hasDataPermission(Material item) {
        return PERMISSION_DATA_NAMES.containsKey(item);
    }

    /**
     * Gets the name of the permission of the data item with the specified
     * material and data value.
     *
     * @param item
     *            Material for which data exists.
     * @param data
     *            Data for the specified material.
     * @return Name of the permission for the specified material and data value.
     */
    public String getDataPermissionName(Material item, byte data) {
        return PERMISSION_DATA_NAMES.get(item).get(data);
    }
}
