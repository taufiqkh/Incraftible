package com.quiptiq.incraftible;

import static com.quiptiq.incraftible.message.FixedMessage.LOG_DISABLED;
import static com.quiptiq.incraftible.message.FixedMessage.LOG_ENABLED;
import static com.quiptiq.incraftible.message.FixedMessage.LOG_WARN_NO_CONFIG;
import static com.quiptiq.incraftible.message.FixedMessage.LOG_WARN_NO_SPOUT;

import java.util.List;
import java.util.logging.Logger;

import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event.Type;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.quiptiq.incraftible.message.FixedMessage;

/**
 * Plugin for intercepting crafting events.
 *
 * @author Taufiq Hoven
 */
public class Incraftible extends JavaPlugin {

    /**
     * Name of the default logger.
     */
    public static final String DEFAULT_LOGGER = "Incraftible";

    private Logger log;

    private IncraftibleConfig config;

    @Override
    public final void onLoad() {
        log = Logger.getLogger(DEFAULT_LOGGER);
        super.onLoad();
    }

    @Override
    public void onDisable() {
        log.info(LOG_DISABLED);
    }

    @Override
    public final void onEnable() {
        PluginManager pluginManager = getServer().getPluginManager();

        this.config = new IncraftibleConfig(this, this.getFile());
        List<Permission> materialPermissions = config.createDefaultMaterialPermissions(this.getDescription().getPermissions());
        for (Permission permission : materialPermissions) {
            pluginManager.addPermission(permission);
        }
        if (config == null) {
            // Error in loading?
            log.warning(LOG_WARN_NO_CONFIG);
        } else {
            if (pluginManager.getPlugin("Spout") == null) {
                log.warning(LOG_WARN_NO_SPOUT);
            }
            getCommand(FixedMessage.COMMAND_PREFIX).setExecutor(new CommandHandler(config));
            pluginManager.registerEvent(
                    Type.CUSTOM_EVENT, new CraftEventListener(new CraftEventHandler(this.config)), Priority.Normal,
                    this);
        }
        log.info(LOG_ENABLED);
    }
}
