package com.quiptiq.incraftible;

import static com.quiptiq.incraftible.message.FixedMessage.LOG_ITEM_CRAFT_ATTEMPT;

import java.util.logging.Logger;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.getspout.spoutapi.event.inventory.InventoryCraftEvent;
import org.getspout.spoutapi.event.inventory.InventoryListener;

import com.quiptiq.incraftible.message.Message;

/**
 * Simple listener for inventory crafting events.
 *
 * @author Taufiq Hoven
 */
public class CraftEventListener extends InventoryListener {
    private final Logger log = Logger.getLogger(Incraftible.DEFAULT_LOGGER);

    /**
     * Configuration for the behaviour of the crafting checks.
     */
    private final IncraftibleConfig config;

    /**
     * Create a new listener for the specified plugin config.
     *
     * @param config    Configuration for the plugin behaviour.
     */
    public CraftEventListener(IncraftibleConfig config) {
        this.config = config;
    }

    /*
     * (non-Javadoc)
     * Called on attempted crafting within the inventory.
     *
     * @see org.getspout.spoutapi.event.inventory.InventoryListener#onInventoryCraft(org.getspout.spoutapi.event.inventory.InventoryCraftEvent)
     */
    @Override
    @EventHandler(priority = EventPriority.NORMAL)
    public void onInventoryCraft(InventoryCraftEvent event) {
        if (event.isCancelled() || event.getResult() == null) {
            return;
        }
        if (!handleCraft(event, event.getResult().getType())) {
            super.onInventoryCraft(event);
        }
    }

    /**
     * Handles crafting of an object. If the item is not allowable and the
     * following option is set to true, the event result is set to null:
     * <pre>
     * event.craft.returnvalue.null
     * </pre>
     *
     * @param event
     *            Crafting event.
     * @param craftable
     *            Object created by the crafting.
     * @return True if the event is handled, false if it was not.
     */
    public boolean handleCraft(InventoryCraftEvent event, Material craftable) {
        if (!config.isItemAllowed(craftable, event.getResult(), event.getPlayer())) {
            event.getPlayer().sendMessage(Message.PLAYER_MESSAGE_DISALLOWED.prepareMessage(craftable));
            event.setCancelled(true);
            // Certain
            if (config.isEventReturnValueMadeNull()) {
                event.setResult(null);
            }
            log.info(String.format(LOG_ITEM_CRAFT_ATTEMPT, event.getPlayer().getName(), craftable));
            return true;
        }
        return false;
    }}
