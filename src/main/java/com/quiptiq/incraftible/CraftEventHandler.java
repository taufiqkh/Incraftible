package com.quiptiq.incraftible;

import static com.quiptiq.incraftible.message.FixedMessage.LOG_ITEM_CRAFT_ATTEMPT;

import java.util.logging.Logger;

import org.bukkit.Material;
import org.getspout.spoutapi.event.inventory.InventoryCraftEvent;

import com.quiptiq.incraftible.message.Message;

/**
 * Handles crafting events as configured.
 *
 * @author Taufiq Hoven
 */
public class CraftEventHandler {
    private final Logger log = Logger.getLogger(Incraftible.DEFAULT_LOGGER);

    private final IncraftibleConfig config;

    /**
     * Creates a crafting event handler based on the given config.
     *
     * @param newConfig
     *            Configuration that determines how events are handled.
     */
    public CraftEventHandler(IncraftibleConfig newConfig) {
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
        if (!config.isItemAllowed(craftable, event.getPlayer())) {
            event.getPlayer().sendMessage(Message.PLAYER_MESSAGE_DISALLOWED.prepareMessage(craftable));
            event.setCancelled(true);
            log.info(String.format(LOG_ITEM_CRAFT_ATTEMPT, event.getPlayer().getName(), craftable));
            return true;
        }
        return false;
    }
}
