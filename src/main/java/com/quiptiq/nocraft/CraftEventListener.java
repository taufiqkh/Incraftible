package com.quiptiq.nocraft;

import org.getspout.spoutapi.event.inventory.InventoryCraftEvent;
import org.getspout.spoutapi.event.inventory.InventoryListener;

/**
 * Simple listener for inventory crafting events.
 *
 * @author Taufiq Hoven
 */
public class CraftEventListener extends InventoryListener {
    /**
     * Handles any non-cancelled crafting events.
     */
    private final CraftEventHandler handler;

    /**
     * Create a new listener, using the specified handler to handle intercepted
     * events.
     *
     * @param newHandler
     *            Handles intercepted events.
     */
    public CraftEventListener(CraftEventHandler newHandler) {
        this.handler = newHandler;
    }

    @Override
    public void onInventoryCraft(InventoryCraftEvent event) {
        if (event.isCancelled()) {
            return;
        }
        if (!handler.handleCraft(event, event.getResult().getType())) {
            super.onInventoryCraft(event);
        }
    }
}
