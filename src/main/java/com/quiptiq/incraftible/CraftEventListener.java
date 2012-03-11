package com.quiptiq.incraftible;

import static com.quiptiq.incraftible.message.FixedMessage.LOG_ITEM_CRAFT_ATTEMPT;

import java.util.logging.Logger;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.Recipe;

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
     * @param config
     *            Configuration for the plugin behaviour.
     */
    public CraftEventListener(IncraftibleConfig config) {
        this.config = config;
    }

    /**
     * Called when inventory is clicked. Due to the bug documented in issue
     * BUKKIT-1112, this is not captured as a CraftItemEvent and must be checked
     * as an inventory click.
     * 
     * @param clickEvent
     *            Event containing the click information.
     */
    @EventHandler(priority = EventPriority.NORMAL)
    public void onInventoryClick(InventoryClickEvent event) {
        Inventory inv = event.getInventory();
        if (inv != null && inv instanceof CraftingInventory && SlotType.RESULT.equals(event.getSlotType())
                && !event.isCancelled()) {
            Recipe recipe = ((CraftingInventory) inv).getRecipe();
            if (recipe != null && recipe.getResult() != null
                    && (InventoryType.CRAFTING.equals(inv.getType()) || InventoryType.WORKBENCH.equals(inv.getType()))) {
                handleCraft(event, recipe, recipe.getResult().getType());
            }
        }
    }

    /**
     * Called on attempted crafting within the inventory.
     * 
     * @param event
     *            Event containing crafting information.
     */
    @EventHandler(priority = EventPriority.NORMAL)
    public void onInventoryCraft(CraftItemEvent event) {
        log.info("onInventoryCraft");
        if (!SlotType.RESULT.equals(event.getSlotType())
                || event.isCancelled()
                || event.getRecipe() == null
                || event.getRecipe().getResult() == null
                || !(InventoryType.CRAFTING.equals(event.getInventory().getType()) || InventoryType.WORKBENCH
                        .equals(event.getInventory().getType()))) {
            log.info("event.isCancelled(): " + event.isCancelled());
            log.info("Slot type: " + event.getSlotType());
            log.info("recipe: " + event.getRecipe());
            log.info("Inventory type: " + event.getInventory().getType());
            return;
        }
    }

    /**
     * Handles crafting of an object. If the item is not allowable and the
     * following option is set to true, the event result is set to null:
     * 
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
    public boolean handleCraft(InventoryClickEvent event, Recipe recipe, Material craftable) {
        if (Player.class.isAssignableFrom(event.getWhoClicked().getClass())) {
            Player player = (Player) event.getWhoClicked();
            if (!config.isItemAllowed(craftable, recipe.getResult(), player)) {
                player.sendMessage(Message.PLAYER_MESSAGE_DISALLOWED.prepareMessage(craftable));
                event.setCancelled(true);
                // Certain
                if (config.isEventReturnValueMadeNull()) {
                    event.setResult(null);
                }
                log.log(config.getLogLevel(), String.format(LOG_ITEM_CRAFT_ATTEMPT, player.getName(), craftable));
                return true;
            }
        }
        return false;
    }
}
