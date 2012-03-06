package com.quiptiq.incraftible;

import static com.quiptiq.incraftible.message.FixedMessage.LOG_ITEM_CRAFT_ATTEMPT;

import java.util.logging.Logger;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.getspout.spoutapi.event.inventory.InventoryCraftEvent;

import com.quiptiq.incraftible.message.Message;

/**
 * Simple listener for inventory crafting events.
 *
 * @author Taufiq Hoven
 */
public class CraftEventListener implements Listener {
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

    /**
     * Called on attempted crafting within the inventory.
     *
     * @param event Event containing crafting information.
     */
    @EventHandler(priority = EventPriority.NORMAL)
    public void onInventoryCraft(InventoryCraftEvent event) {
        if (event.isCancelled() || event.getResult() == null) {
            return;
        }
        handleCraft(event, event.getResult().getType());
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
            log.log(config.getLogLevel(), String.format(LOG_ITEM_CRAFT_ATTEMPT, event.getPlayer().getName(), craftable));
            return true;
        }
        return false;
    }}
