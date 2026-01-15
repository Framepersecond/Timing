package de.timing.motd;

import de.timing.Timing;
import de.timing.util.MessageUtil;
import net.kyori.adventure.text.Component;

/**
 * Manages the server MOTD with support for customization and timer overrides.
 */
public class MotdManager {

    private final Timing plugin;
    private MotdConfig config;

    public MotdManager(Timing plugin) {
        this.plugin = plugin;
        this.config = loadConfig();
    }

    /**
     * Load MOTD config from file.
     */
    private MotdConfig loadConfig() {
        String line1 = plugin.getConfigManager().getMotdLine1();
        String line2 = plugin.getConfigManager().getMotdLine2();
        boolean enabled = plugin.getConfigManager().isMotdEnabled();
        return new MotdConfig(line1, line2, enabled);
    }

    /**
     * Reload config from file.
     */
    public void reload() {
        this.config = loadConfig();
    }

    /**
     * Save current config to file.
     */
    public void save() {
        plugin.getConfigManager().saveMotdConfig(config);
    }

    /**
     * Get the current MOTD to display.
     * Returns null if a timer is overriding, or if MOTD is disabled.
     */
    public Component getMotd() {
        if (plugin.getBeginningTimerManager().isRunning()) {
            return null;
        }
        if (plugin.getRestartTimerManager().isRunning()) {
            return null;
        }
        if (plugin.getEndTimerManager().isRunning() && plugin.isServerStarted()) {
            return null;
        }

        if (!config.isEnabled()) {
            return null;
        }

        return MessageUtil.parse(config.getFullMotd());
    }

    /**
     * Check if the default MOTD should be shown (no timer overriding).
     */
    public boolean shouldShowDefaultMotd() {
        if (!config.isEnabled()) {
            return false;
        }
        if (plugin.getBeginningTimerManager().isRunning()) {
            return false;
        }
        if (plugin.getRestartTimerManager().isRunning()) {
            return false;
        }
        if (plugin.getEndTimerManager().isRunning() && plugin.isServerStarted()) {
            return false;
        }
        return true;
    }

    public MotdConfig getConfig() {
        return config;
    }

    public void setConfig(MotdConfig config) {
        this.config = config;
    }
}
