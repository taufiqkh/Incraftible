package com.quiptiq.incraftible.message;

import static com.quiptiq.incraftible.message.FixedMessage.LOG_PREFIX;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.logging.Logger;

import org.bukkit.Material;

/**
 * Provides human-friendly names for materials. Where possible, these are
 * retrieved from the resource bundle, so as to provide locale-specific names.
 * In its absence, names are derived from the value of the
 * {@link org.bukkit.Material} enum.
 *
 * This class is thread-safe.
 *
 * @author Taufiq Hoven
 */
public class MaterialNamer {
    private static final String DEFAULT_RESOURCE_BUNDLE = "incraftible_messages";

    private static final String RESOURCE_ITEM_PREFIX = "item.";

    private static final String RESOURCE_SUFFIX = ".name";

    private static final String LOG_MATERIAL_NOT_FOUND = LOG_PREFIX
            + "Couldn't find resource for material after checking: %s";

    private static final String LOG_UNEXPECTED_CLASS = LOG_PREFIX + "Object class for key is not string: %s";

    private static final String LOG_NO_ITEM_NAME_RESOURCE_BUNDLE = LOG_PREFIX
            + "No resource bundle found for item names.";

    private static final Logger log = Logger.getLogger("Minecraft");

    private static final MaterialNamer namer = new MaterialNamer();

    /**
     * Map of materials to their names.
     */
    private final Map<Material, String> materialNames;

    /**
     * Returns an instance of a material namer, which provides human-friendly
     * names for materials.
     *
     * @return Instance of a material namer.
     */
    public static final MaterialNamer getInstance() {
        return namer;
    }

    private MaterialNamer() {
        ResourceBundle bundle = null;
        try {
            bundle = ResourceBundle.getBundle(DEFAULT_RESOURCE_BUNDLE);
        } catch (MissingResourceException e) {
            log.warning(LOG_NO_ITEM_NAME_RESOURCE_BUNDLE);
        }
        HashMap<Material, String> names = new HashMap<Material, String>();

        if (bundle != null) {
            for (Material material : Material.values()) {
                String itemKey = RESOURCE_ITEM_PREFIX + material.name().toLowerCase() + RESOURCE_SUFFIX;
                if (bundle.containsKey(itemKey)) {
                    try {
                        String name = bundle.getString(itemKey);
                        names.put(material, name);
                        continue;
                    } catch (MissingResourceException e) {
                        log.warning(String.format(LOG_MATERIAL_NOT_FOUND, material.name()));
                    } catch (ClassCastException e) {
                        log.warning(String.format(LOG_UNEXPECTED_CLASS, material.name()));
                    }
                }
                // Derive the name from the enum.
                names.put(material, deriveMaterialName(material));
            }
        } else {
            for (Material material : Material.values()) {
                names.put(material, deriveMaterialName(material));
            }
        }
        materialNames = Collections.unmodifiableMap(names);
    }

    /**
     * Gets the name for the specified material, as determined by the message
     * resource bundle or, failing that, the Material enum.
     *
     * @param material
     *            Material for which a name is to be retrieved.
     * @return Name for the specified material.
     */
    public String getName(Material material) {
        return materialNames.get(material);
    }

    /**
     * Derives a basic name from the Material enum.
     *
     * @param material
     *            Material from which a name is derived.
     * @return Name for the specified material.
     */
    private static String deriveMaterialName(Material material) {
        String rawMaterialName = material.name();
        return rawMaterialName.toLowerCase().replaceAll("_", " ");
    }
}
