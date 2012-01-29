package com.quiptiq.incraftible;

import java.util.HashMap;

/**
 * Encapsulates the strategy for determining permissions on a given item.
 */
public enum PermissionsStrategy {
    /**
     * When set in the {@link #CONFIG_CRAFT_DEFAULT} config item, denotes that
     * all crafting as per default minecraft is allowed.
     */
    STANDARD("standard"),

    /**
     * When set in the {@link #CONFIG_CRAFT_DEFAULT} config item, denotes that
     * all crafting, including non-standard items, is allowed by default. That
     * is, any items for which permissions are not set are assumed to be
     * allowed.
     */
    ALL("all"),

    /**
     * When set in the {@link #CONFIG_CRAFT_DEFAULT} config item, denotes that
     * no crafting is allowed by default. Items must be whitelisted to be
     * crafted.
     */
    NONE("none");

    private static final HashMap<String, PermissionsStrategy> configToPermissionStrategy;

    static {
        HashMap<String, PermissionsStrategy> strategies = new HashMap<String, PermissionsStrategy>();
        for (PermissionsStrategy strategy : values()) {
            strategies.put(strategy.getConfigString(), strategy);
        }
        configToPermissionStrategy = strategies;
    }

    private final String configString;

    private PermissionsStrategy(String configString) {
        this.configString = configString;
    }

    public String getConfigString() {
        return configString;
    }

    public static PermissionsStrategy strategyForConfig(String config) {
        return configToPermissionStrategy.get(config);
    }
}
