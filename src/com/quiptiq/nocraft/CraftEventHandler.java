package com.quiptiq.nocraft;

import java.util.HashSet;

import org.bukkit.Material;
import org.bukkitcontrib.event.inventory.InventoryCraftEvent;

/**
 * Handles crafting events as configured.
 *
 * @author Taufiq Hoven
 */
public class CraftEventHandler {
    private HashSet<Material> disallowedCraftables;

    /**
     * Creates a crafting event handler based on the given config.
     *
     * @param config
     *            Configuration that determines how events are handled.
     */
    public CraftEventHandler(NoCraftConfig config) {
    }

    /**
     * Handles crafting of an object.
     *
     * @param event
     *            Crafting event.
     * @param craftable
     *            Object created by the crafting.
     * @return True if the event is handled, false if it was not.
     */
    public boolean handleCraft(InventoryCraftEvent event, Material craftable) {
        if (disallowedCraftables.contains(craftable)) {
            event.getPlayer().chat("That item is not allowed!");
            event.setCancelled(true);
            return true;
        }
        return false;
    }
}
