package com.quiptiq.incraftible;

import static com.quiptiq.incraftible.message.FixedMessage.LOG_DISABLED;
import static com.quiptiq.incraftible.message.FixedMessage.LOG_ENABLED;
import static com.quiptiq.incraftible.message.FixedMessage.LOG_WARN_NO_CONFIG;
import static com.quiptiq.incraftible.message.FixedMessage.LOG_WARN_NO_SPOUT;

import java.util.logging.Logger;

import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event.Type;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Plugin for intercepting crafting events.
 *
 * @author Taufiq Hoven
 * @version 0.1
 */
public class Incraftible extends JavaPlugin {

    /**
     * Name of the default logger.
     */
    public static final String DEFAULT_LOGGER = "Minecraft";

    private Logger log;

    private IncraftibleConfig config;

    @Override
    public final void onLoad() {
        log = getServer().getLogger();
        this.config = new IncraftibleConfig(this, this.getFile());
        super.onLoad();
    }

    @Override
    public void onDisable() {
        log.info(LOG_DISABLED);
    }

    @Override
    public final void onEnable() {
        PluginManager pluginManager = getServer().getPluginManager();
        if (config == null) {
            // Error in loading?
            log.warning(LOG_WARN_NO_CONFIG);
        } else {
            if (pluginManager.getPlugin("Spout") == null) {
                log.warning(LOG_WARN_NO_SPOUT);
            }
            getCommand(CommandHandler.COMMAND).setExecutor(new CommandHandler(config));
            pluginManager.registerEvent(
                    Type.CUSTOM_EVENT, new CraftEventListener(new CraftEventHandler(this.config)), Priority.Normal,
                    this);
        }
        log.info(LOG_ENABLED);
    }
}
