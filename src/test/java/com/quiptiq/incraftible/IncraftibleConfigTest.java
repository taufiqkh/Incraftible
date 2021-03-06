package com.quiptiq.incraftible;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.*;

import org.bukkit.Material;
import org.bukkit.entity.Player;
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
        String allowedPermission = "incraftible.craft.clay";
        Player mockPlayer = mock(Player.class);
        Material testMaterial = Material.CLAY;
        when(mockPlayer.isPermissionSet(allowedPermission)).thenReturn(true);
        when(mockPlayer.hasPermission(allowedPermission)).thenReturn(true);
        assertTrue(
                "Default items configured as allowed should be returned as such",
                config.isItemAllowed(testMaterial, null, mockPlayer));
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
        String disallowedPermission = "incraftible.craft.apple";
        Player mockPlayer = mock(Player.class);
        Material testMaterial = Material.APPLE;
        when(mockPlayer.isPermissionSet(disallowedPermission)).thenReturn(true);
        when(mockPlayer.hasPermission(disallowedPermission)).thenReturn(false);
        assertFalse(
                "Default items configured as disallowed should be returned as such",
                config.isItemAllowed(testMaterial, null, mockPlayer));
    }

    /**
     * Create test config file.
     *
     * @return Newly created config file
     * @throws IOException
     *             if any problems occur while creating the file.
     */
    private File createConfigFile() throws IOException {
        return createConfigFile(PermissionsStrategy.ALL);
    }

    /**
     * Create a test config file containing the specified permissions strategy.
     *
     * @param strategy
     *            Permissions strategy for use when loaded by Incraftible.
     * @return Newly created config file.
     * @throws IOException
     *             if any problems occur while creating the file.
     */
    private File createConfigFile(PermissionsStrategy strategy) throws IOException {
        File testConfigFile = new File(TEST_CONFIG_DIR + TEST_CONFIG_FILE);
        testConfigFile.createNewFile();
        FileWriter writer = new FileWriter(testConfigFile);
        writer.write("messages:" + NEWLINE);
        writer.write("    disallowed: \"" + DISALLOW_TEST_MESSAGE + "\"" + NEWLINE);
        writer.write("craft.default:  " + strategy.getConfigString());
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
                    IncraftibleConfigTest.class.getClassLoader());
        }
    }
}
