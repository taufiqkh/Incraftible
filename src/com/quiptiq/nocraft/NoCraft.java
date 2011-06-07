package com.quiptiq.nocraft;

import static com.quiptiq.nocraft.Message.LOG_WARN_NO_BUKKITCONTRIB;
import static com.quiptiq.nocraft.Message.LOG_WARN_NO_CONFIG;

import java.util.logging.Logger;

import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event.Type;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Plugin for intercepting crafting events.
 *
 * @author Taufiq Hoven
 *
 */
public class NoCraft extends JavaPlugin {

    private Logger log;

    private NoCraftConfig config;

    @Override
    public final void onLoad() {
        log = getServer().getLogger();
        this.config = new NoCraftConfig(this.getConfiguration(), log);
        super.onLoad();
    }

    @Override
    public void onDisable() {
        // Nothing to do
    }

    @Override
    public final void onEnable() {
        PluginManager pluginManager = getServer().getPluginManager();
        if (config == null) {
            // Error in loading?
            log.warning(LOG_WARN_NO_CONFIG);
        } else if (pluginManager.getPlugin("BukkitContrib") == null) {
            log.warning(LOG_WARN_NO_BUKKITCONTRIB);
        } else {
            pluginManager.registerEvent(
                    Type.CUSTOM_EVENT, new CraftEventListener(new CraftEventHandler(this.config)), Priority.Normal,
                    this);
        }
    }
}
