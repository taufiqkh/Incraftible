package com.quiptiq.incraftible;

import static com.quiptiq.incraftible.message.FixedMessage.LOG_DISABLED;
import static com.quiptiq.incraftible.message.FixedMessage.LOG_ENABLED;
import static com.quiptiq.incraftible.message.FixedMessage.LOG_STANDARD_STRATEGY;
import static com.quiptiq.incraftible.message.FixedMessage.LOG_WARN_NO_CONFIG;

import java.util.List;
import java.util.logging.Logger;

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

    private final PermissionsReference perms = PermissionsReference.getInstance();

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

        config = new IncraftibleConfig(this, this.getFile());
        if (config == null) {
            // Error in loading?
            log.warning(LOG_WARN_NO_CONFIG);
        } else {
            if (PermissionsStrategy.STANDARD.equals(config.getPermissionsStrategy())) {
                List<Permission> materialPermissions =
                        perms.createDefaultMaterialPermissions(this.getDescription().getPermissions());
                log.info(LOG_STANDARD_STRATEGY);
                for (Permission permission : materialPermissions) {
                    pluginManager.addPermission(permission);
                }
            }
            getCommand(FixedMessage.COMMAND_PREFIX).setExecutor(new CommandHandler(config));

            pluginManager.registerEvents(new CraftEventListener(config), this);
        }
        log.info(LOG_ENABLED);
    }
}
