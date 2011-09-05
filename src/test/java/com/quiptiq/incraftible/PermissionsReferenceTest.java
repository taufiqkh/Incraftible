package com.quiptiq.incraftible;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.*;

import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.Server;
import org.bukkit.permissions.Permissible;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.PluginManager;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Tests the PermissionsReference class.
 */
public class PermissionsReferenceTest {
    @BeforeClass
    public static void setUpClass() {
        PluginManager pluginManager = mock(PluginManager.class);
        when(pluginManager.getPermissionSubscriptions(null)).thenReturn(new HashSet<Permissible>());
        Server server = mock(Server.class);
        when(server.getPluginManager()).thenReturn(pluginManager);
        Bukkit.setServer(server);
    }

    /**
     * Empty default permissions configuration should result in empty default
     * permissions being created.
     */
    @Test
    public void testEmptyDefaultPermissions() {
        PermissionsReference reference = PermissionsReference.getInstance();
        ArrayList<Permission> permissions = new ArrayList<Permission>();
        assertEquals("Empty default permissions list should result in empty material permissions",
                0, reference.createDefaultMaterialPermissions(permissions).size());
    }

    /**
     * Permission configuration where there is no match for the standard set
     * should result in no default material permissions.
     */
    @Test
    public void testNoMatchingDefaultPermissions() {
        PermissionsReference reference = PermissionsReference.getInstance();
        ArrayList<Permission> permissions = new ArrayList<Permission>();
        HashMap<String, Boolean> children = new HashMap<String, Boolean>();
        children.put("incraftible.dummychild", true);
        permissions.add(mockPermission("incraftible.dummy", children));
        assertEquals("Empty default permissions list should result in empty material permissions",
                0, reference.createDefaultMaterialPermissions(permissions).size());
    }

    /**
     * Standard permissions should create dye data nodes.
     */
    @Test
    public void testDyeDataValues() {
        PermissionsReference reference = PermissionsReference.getInstance();
        ArrayList<Permission> permissions = new ArrayList<Permission>();
        HashMap<String, Boolean> children = new HashMap<String, Boolean>();
        children.put("incraftible.craft.dye.*", true);
        permissions.add(mockPermission("incraftible.craft.standard", children));
        List<Permission> defaultPermissions = reference.createDefaultMaterialPermissions(permissions);
        // Number of permissions = Number of dyes + 1 (for parent)
        assertEquals("Number of permissions = Number of dyes + 1 (for parent)",
                DyeColor.values().length + 1, defaultPermissions.size());
        boolean foundSample = false;
        for (Permission permission : defaultPermissions) {
            if ("incraftible.craft.dye.green".equals(permission.getName())) {
                foundSample = true;
                break;
            }
        }
        assertEquals("Default permissions should contain sample node.", true, foundSample);
    }

    /**
     * Standard permissions should create wool data nodes.
     */
    @Test
    public void testWoolDataValues() {
        PermissionsReference reference = PermissionsReference.getInstance();
        ArrayList<Permission> permissions = new ArrayList<Permission>();
        HashMap<String, Boolean> children = new HashMap<String, Boolean>();
        children.put("incraftible.craft.wool.*", true);
        permissions.add(mockPermission("incraftible.craft.standard", children));
        List<Permission> defaultPermissions = reference.createDefaultMaterialPermissions(permissions);
        assertEquals("Number of permissions = Number of colours + 1 (for parent)",
                DyeColor.values().length + 1, defaultPermissions.size());
        boolean foundSample = false;
        for (Permission permission : defaultPermissions) {
            if ("incraftible.craft.wool.green".equals(permission.getName())) {
                foundSample = true;
                break;
            }
        }
        assertEquals("Default permissions should contain sample node.", true, foundSample);
    }

    /**
     * Creates a mock permission. Needed as permission creation is not well
     * designed for testing as it has too many side-effects.
     *
     * @return  Mock permission with the specified name and children.
     */
    public Permission mockPermission(String name, HashMap<String, Boolean> children) {
        Permission permission = mock(Permission.class);
        when(permission.getName()).thenReturn(name);
        when(permission.getChildren()).thenReturn(children);
        return permission;
    }
}
