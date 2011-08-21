package com.quiptiq.incraftible;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.*;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.InvalidDescriptionException;
import org.bukkit.plugin.PluginDescriptionFile;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.quiptiq.incraftible.message.Message;

/**
 * Unit tests for the IncraftibleConfig class.
 *
 * @author Taufiq Hoven
 */
public class IncraftibleConfigTest {
    private static final String TEST_CONFIG_DIR = "testConfigDir/";

    private static final String TEST_CONFIG_FILE = "config.yml";

    private static final String TEST_PLUGIN_FILE = "plugin.yml";

    private static final String TEST_JAR_PATH = "target/testpluginconfig.jar";

    private static final String NEWLINE = System.getProperty("line.separator");

    private static final String DISALLOW_TEST_MESSAGE = "Foobity doobity doo";

    /**
     * Stored plugin FileReader, to close after the PluginFileDescription has
     * finished with it.
     */
    private FileReader pluginFileReader = null;

    @Before
    public void setUp() {
        File testDir = new File(TEST_CONFIG_DIR);
        if (!testDir.exists()) {
            testDir.mkdirs();
        }
    }

    @After
    public void tearDown() throws IOException {
        pluginFileReader.close();
        String[] fileNames = { TEST_CONFIG_FILE, TEST_PLUGIN_FILE };
        for (String filePath : fileNames) {
            File testFile = new File(TEST_CONFIG_DIR + filePath);
            if (testFile.exists()) {
                testFile.delete();
            }
        }
        File testConfigDir = new File(TEST_CONFIG_DIR);
        if (testConfigDir.exists()) {
            testConfigDir.delete();
        }
    }

    /**
     * When a config file exists, that config should be loaded.
     *
     * @throws IOException
     * @throws InvalidDescriptionException
     */
    @Test
    public void testLoadExistingConfig() throws IOException, InvalidDescriptionException {
        createConfigFile();
        TestIncraftible testIncraftible = new TestIncraftible();
        File testConfigFile = new File(TEST_CONFIG_DIR + TEST_CONFIG_FILE);
        assertTrue("Test plugin file should have been created", testConfigFile.exists());
        new IncraftibleConfig(testIncraftible, null);
        assertEquals(
                "Message should match test configuration", DISALLOW_TEST_MESSAGE,
                Message.PLAYER_MESSAGE_DISALLOWED.getMessage());
    }

    /**
     * When no config file exists, the default config should be loaded and
     * created in the config directory.
     *
     * @throws InvalidDescriptionException
     * @throws IOException
     */
    @Test
    public void testGenerateConfig() throws IOException, InvalidDescriptionException {
        File generatedConfigFile = new File(TEST_CONFIG_DIR + TEST_CONFIG_FILE);
        assertFalse("Config file should not exist before the test is run.", generatedConfigFile.exists());
        TestIncraftible testIncraftible = new TestIncraftible();
        new IncraftibleConfig(testIncraftible, new File(TEST_JAR_PATH));
        assertTrue("Default config file should be generated when config is created.", generatedConfigFile.exists());
    }

    /**
     * When an item by default is configured to be allowed, config should return
     * it as allowed.
     *
     * @throws IOException
     * @throws InvalidDescriptionException
     */
    @Test
    public void testIsAllowedDefault() throws IOException, InvalidDescriptionException {
        IncraftibleConfig config = new IncraftibleConfig(new TestIncraftible(), new File(TEST_JAR_PATH));
        Player mockPlayer = mock(Player.class);
        Material testMaterial = Material.CLAY;
        when(mockPlayer.hasPermission("incraftible.craft.clay")).thenReturn(true);
        assertTrue(
                "Default items configured as allowed should be returned as such",
                config.isItemAllowed(testMaterial, mockPlayer));
    }

    /**
     * When an item by default is configured to be disallowed, config should
     * return it as disallowed.
     *
     * @throws IOException
     * @throws InvalidDescriptionException
     */
    @Test
    public void testIsDisallowedDefault() throws IOException, InvalidDescriptionException {
        IncraftibleConfig config = new IncraftibleConfig(new TestIncraftible(), new File(TEST_JAR_PATH));
        Player mockPlayer = mock(Player.class);
        Material testMaterial = Material.APPLE;
        when(mockPlayer.hasPermission("incraftible.craft.apple")).thenReturn(false);
        assertFalse(
                "Default items configured as disallowed should be returned as such",
                config.isItemAllowed(testMaterial, mockPlayer));
    }

    @Test
    public void testEmptyDefaultPermissions() throws IOException, InvalidDescriptionException {
        IncraftibleConfig config = new IncraftibleConfig(new TestIncraftible(), new File(TEST_JAR_PATH));
        ArrayList<Permission> permissions = new ArrayList<Permission>();
        assertEquals("Empty default permissions list should result in empty material permissions",
                0, config.createDefaultMaterialPermissions(permissions).size());
    }

    @Test
    public void testNoMatchingDefaultPermissions() throws IOException, InvalidDescriptionException {
        IncraftibleConfig config = new IncraftibleConfig(new TestIncraftible(), new File(TEST_JAR_PATH));
        ArrayList<Permission> permissions = new ArrayList<Permission>();
        HashMap<String, Boolean> children = new HashMap<String, Boolean>();
        children.put("incraftible.dummychild", true);
        permissions.add(mockPermission("incraftible.dummy", children));
        assertEquals("Empty default permissions list should result in empty material permissions",
                0, config.createDefaultMaterialPermissions(permissions).size());
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

    /**
     * Create test config file.
     *
     * @return Newly created config file
     * @throws IOException
     *             if any problems occur while creating the file.
     */
    private File createConfigFile() throws IOException {
        File testConfigFile = new File(TEST_CONFIG_DIR + TEST_CONFIG_FILE);
        testConfigFile.createNewFile();
        FileWriter writer = new FileWriter(testConfigFile);
        writer.write("messages:" + NEWLINE);
        writer.write("    disallowed: \"" + DISALLOW_TEST_MESSAGE + "\"");
        writer.close();
        return testConfigFile;
    }

    /**
     * Creates a test plugin file, with minimal values.
     *
     * @return Plugin file
     * @throws IOException
     *             if any problems occur while creating the file.
     */
    private File createPluginFile() throws IOException {
        File testPluginFile = new File(TEST_CONFIG_DIR + TEST_PLUGIN_FILE);
        testPluginFile.createNewFile();
        FileWriter writer = new FileWriter(testPluginFile);
        writer.write("name: Incraftible" + NEWLINE);
        writer.write("version: 42" + NEWLINE);
        writer.write("main: com.quiptiq.Incraftible.Main" + NEWLINE);
        writer.close();
        return testPluginFile;
    }

    private class TestIncraftible extends Incraftible {
        public TestIncraftible() throws IOException, InvalidDescriptionException {
            File testPluginFile = createPluginFile();
            pluginFileReader = new FileReader(testPluginFile);
            PluginDescriptionFile description = new PluginDescriptionFile(pluginFileReader);
            initialize(
                    null, null, description, new File(TEST_CONFIG_DIR), new File(TEST_CONFIG_DIR + TEST_JAR_PATH),
                    getClassLoader());
        }
    }
}
