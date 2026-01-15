package de.timing.config;

import de.timing.Timing;
import de.timing.announcer.Announcement;
import de.timing.announcer.AnnouncementType;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.HashMap;
import java.util.Map;

/**
 * Manages plugin configuration and provides access to config values.
 */
public class ConfigManager {

    private final Timing plugin;

    public ConfigManager(Timing plugin) {
        this.plugin = plugin;
    }

    public void reload() {
        plugin.reloadConfig();
    }

    public FileConfiguration getConfig() {
        return plugin.getConfig();
    }

    // ========== Server State ==========

    public boolean isServerStartedState() {
        return getConfig().getBoolean("server-state.started", false);
    }

    public void setServerStartedState(boolean started) {
        getConfig().set("server-state.started", started);
        plugin.saveConfig();
    }

    // ========== Timer State Persistence ==========

    public int getSavedBeginningTimerSeconds() {
        return getConfig().getInt("server-state.beginning-timer-remaining", 0);
    }

    public void saveBeginningTimerState(int seconds) {
        getConfig().set("server-state.beginning-timer-remaining", seconds);
        plugin.saveConfig();
    }

    public int getSavedEndTimerSeconds() {
        return getConfig().getInt("server-state.end-timer-remaining", 0);
    }

    public void saveEndTimerState(int seconds) {
        getConfig().set("server-state.end-timer-remaining", seconds);
        plugin.saveConfig();
    }

    // ========== Beginning Timer Config ==========

    public String getBeginningTimerMotdFormat() {
        return getConfig().getString("beginning-timer.motd-format", 
            "<red><bold>Server Starting</bold></red>\n<yellow>Starting in: <white>{time}</white></yellow>");
    }

    public String getBeginningTimerKickMessage() {
        return getConfig().getString("beginning-timer.kick-message",
            "<red><bold>Server is Starting!</bold></red>\n\n<yellow>The server will open in <white>{time}</white></yellow>");
    }

    public boolean isDisableWhitelistOnEnd() {
        return getConfig().getBoolean("beginning-timer.disable-whitelist-on-end", true);
    }

    // ========== Restart Timer Config ==========

    public String getRestartTimerMotdFormat() {
        return getConfig().getString("restart-timer.motd-format",
            "<red><bold>Server Restarting</bold></red>\n<yellow>Stopping in: <white>{time}</white></yellow>");
    }

    public String getRestartTimerKickMessage() {
        return getConfig().getString("restart-timer.kick-message",
            "<red><bold>Server is Stopping!</bold></red>\n\n<yellow>The server will stop in <white>{time}</white></yellow>");
    }

    public boolean isKickAllOnRestart() {
        return getConfig().getBoolean("restart-timer.kick-all-on-end", true);
    }

    public String getRestartFinalKickMessage() {
        return getConfig().getString("restart-timer.final-kick-message",
            "<red><bold>Server Stopped</bold></red>\n\n<gray>Please reconnect shortly!</gray>");
    }

    // ========== End Timer Config (End Dimension) ==========

    public String getEndTimerMotdFormat() {
        return getConfig().getString("end-timer.motd-format",
            "<light_purple><bold>The End</bold></light_purple>\n<yellow>Opens in: <white>{time}</white></yellow>");
    }

    // ========== MOTD Config ==========

    public String getMotdLine1() {
        return getConfig().getString("motd.line1", "<gradient:gold:yellow><bold>My Server</bold></gradient>");
    }

    public String getMotdLine2() {
        return getConfig().getString("motd.line2", "<gray>Welcome to the server!</gray>");
    }

    public boolean isMotdEnabled() {
        return getConfig().getBoolean("motd.enabled", true);
    }

    public void saveMotdConfig(de.timing.motd.MotdConfig motdConfig) {
        getConfig().set("motd.line1", motdConfig.getLine1());
        getConfig().set("motd.line2", motdConfig.getLine2());
        getConfig().set("motd.enabled", motdConfig.isEnabled());
        plugin.saveConfig();
    }

    // ========== Announcements Config ==========

    public Map<String, Announcement> loadAnnouncements() {
        Map<String, Announcement> announcements = new HashMap<>();
        ConfigurationSection section = getConfig().getConfigurationSection("announcements");
        
        if (section == null) {
            return announcements;
        }

        for (String key : section.getKeys(false)) {
            ConfigurationSection announcementSection = section.getConfigurationSection(key);
            if (announcementSection == null) continue;

            String message = announcementSection.getString("message", "");
            String typeStr = announcementSection.getString("type", "ACTION_BAR");
            AnnouncementType type;
            try {
                type = AnnouncementType.valueOf(typeStr.toUpperCase());
            } catch (IllegalArgumentException e) {
                type = AnnouncementType.ACTION_BAR;
            }

            Announcement announcement = new Announcement(key, message, type);
            announcement.setSubtitle(announcementSection.getString("subtitle", ""));
            announcement.setFadeIn(announcementSection.getInt("fade-in", 10));
            announcement.setStay(announcementSection.getInt("stay", 70));
            announcement.setFadeOut(announcementSection.getInt("fade-out", 20));
            announcement.setInterval(announcementSection.getInt("interval", 0));
            announcement.setEnabled(announcementSection.getBoolean("enabled", true));

            announcements.put(key, announcement);
        }

        return announcements;
    }

    public void saveAnnouncement(Announcement announcement) {
        String path = "announcements." + announcement.getName();
        FileConfiguration config = getConfig();

        config.set(path + ".message", announcement.getMessage());
        config.set(path + ".type", announcement.getType().name());
        config.set(path + ".subtitle", announcement.getSubtitle());
        config.set(path + ".fade-in", announcement.getFadeIn());
        config.set(path + ".stay", announcement.getStay());
        config.set(path + ".fade-out", announcement.getFadeOut());
        config.set(path + ".interval", announcement.getInterval());
        config.set(path + ".enabled", announcement.isEnabled());

        plugin.saveConfig();
    }

    public void deleteAnnouncement(String name) {
        getConfig().set("announcements." + name, null);
        plugin.saveConfig();
    }
}
