package com.quiptiq.nocraft;

import static com.quiptiq.nocraft.Message.LOG_ITEM_CRAFT_ATTEMPT;
import static com.quiptiq.nocraft.Message.PLAYER_MESSAGE_NOT_ALLOWED;

import java.util.logging.Logger;

import org.bukkit.Material;
import org.getspout.spoutapi.event.inventory.InventoryCraftEvent;

/**
 * Handles crafting events as configured.
 *
 * @author Taufiq Hoven
 */
public class CraftEventHandler {
    private final Logger log = Logger.getLogger(NoCraft.DEFAULT_LOGGER);

    private final NoCraftConfig config;

    /**
     * Creates a crafting event handler based on the given config.
     *
     * @param newConfig
     *            Configuration that determines how events are handled.
     */
    public CraftEventHandler(NoCraftConfig newConfig) {
        config = newConfig;
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
        if (config.isItemAllowed(craftable, event.getPlayer())) {
            event.getPlayer().sendMessage(PLAYER_MESSAGE_NOT_ALLOWED);
            event.setCancelled(true);
            log.info(String.format(LOG_ITEM_CRAFT_ATTEMPT, event.getPlayer().getName(), craftable));
            return true;
        }
        return false;
    }
}
