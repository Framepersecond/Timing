package de.timing;

import de.timing.announcer.AnnouncerManager;
import de.timing.commands.AnnouncerCommand;
import de.timing.commands.BeginningTimerCommand;
import de.timing.commands.EndTimerCommand;
import de.timing.commands.MotdCommand;
import de.timing.commands.RestartTimerCommand;
import de.timing.config.ConfigManager;
import de.timing.gui.ChatInputListener;
import de.timing.gui.GUIManager;
import de.timing.motd.MotdListener;
import de.timing.motd.MotdManager;
import de.timing.timer.BeginningTimerListener;
import de.timing.timer.BeginningTimerManager;
import de.timing.timer.EndTimerListener;
import de.timing.timer.EndTimerManager;
import de.timing.timer.RestartTimerListener;
import de.timing.timer.RestartTimerManager;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Timing - A Folia plugin for server timing management.
 * Features: Beginning/Restart countdown timers with MOTD/kick, End dimension
 * timer,
 * customizable MOTD, GUI-based announcements.
 * Timer states are persisted across server restarts.
 */
public class Timing extends JavaPlugin {

    private static Timing instance;

    private ConfigManager configManager;
    private BeginningTimerManager beginningTimerManager;
    private RestartTimerManager restartTimerManager;
    private EndTimerManager endTimerManager;
    private AnnouncerManager announcerManager;
    private MotdManager motdManager;
    private GUIManager guiManager;

    private boolean serverStarted = false;

    @Override
    public void onEnable() {
        instance = this;

        saveDefaultConfig();
        configManager = new ConfigManager(this);

        serverStarted = configManager.isServerStartedState();

        beginningTimerManager = new BeginningTimerManager(this);
        restartTimerManager = new RestartTimerManager(this);
        endTimerManager = new EndTimerManager(this);
        announcerManager = new AnnouncerManager(this);
        motdManager = new MotdManager(this);
        guiManager = new GUIManager(this);

        getServer().getPluginManager().registerEvents(new MotdListener(this), this);
        getServer().getPluginManager().registerEvents(new BeginningTimerListener(this), this);
        getServer().getPluginManager().registerEvents(new RestartTimerListener(this), this);
        getServer().getPluginManager().registerEvents(new EndTimerListener(this), this);
        getServer().getPluginManager().registerEvents(guiManager, this);
        getServer().getPluginManager().registerEvents(new ChatInputListener(this), this);

        getCommand("beginningtimer").setExecutor(new BeginningTimerCommand(this));
        getCommand("restarttimer").setExecutor(new RestartTimerCommand(this));
        getCommand("endtimer").setExecutor(new EndTimerCommand(this));
        getCommand("announcer").setExecutor(new AnnouncerCommand(this));
        getCommand("motd").setExecutor(new MotdCommand(this));

        getServer().getGlobalRegionScheduler().runDelayed(this, task -> {
            resumeSavedTimers();
        }, 20);

        getLogger().info("Timing plugin enabled! Server started: " + serverStarted);
    }

    /**
     * Resume any saved timers from config.
     */
    private void resumeSavedTimers() {
        int beginningSeconds = configManager.getSavedBeginningTimerSeconds();
        if (beginningSeconds > 0) {
            getLogger().info("Resuming Beginning Timer with " + beginningSeconds + " seconds remaining");
            beginningTimerManager.start(beginningSeconds);
            configManager.saveBeginningTimerState(0);
        }

        int endSeconds = configManager.getSavedEndTimerSeconds();
        if (endSeconds > 0) {
            getLogger().info("Resuming End Timer with " + endSeconds + " seconds remaining");
            endTimerManager.start(endSeconds);
            configManager.saveEndTimerState(0);
        }
    }

    @Override
    public void onDisable() {
        if (beginningTimerManager != null && beginningTimerManager.isRunning()) {
            int remaining = beginningTimerManager.getRemainingSeconds();
            configManager.saveBeginningTimerState(remaining);
            getLogger().info("Saved Beginning Timer state: " + remaining + " seconds remaining");
            beginningTimerManager.stop();
        } else if (beginningTimerManager != null) {
            beginningTimerManager.stop();
        }

        if (endTimerManager != null && endTimerManager.isRunning()) {
            int remaining = endTimerManager.getRemainingSeconds();
            configManager.saveEndTimerState(remaining);
            getLogger().info("Saved End Timer state: " + remaining + " seconds remaining");
            endTimerManager.stop();
        } else if (endTimerManager != null) {
            endTimerManager.stop();
        }

        if (restartTimerManager != null) {
            restartTimerManager.stop();
        }

        if (announcerManager != null) {
            announcerManager.stopAll();
        }

        if (configManager != null) {
            configManager.setServerStartedState(serverStarted);
        }

        getLogger().info("Timing plugin disabled!");
    }

    public boolean isServerStarted() {
        return serverStarted;
    }

    public void setServerStarted(boolean started) {
        this.serverStarted = started;
        configManager.setServerStartedState(started);
        getLogger().info("Server started state changed to: " + started);
    }

    public static Timing getInstance() {
        return instance;
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public BeginningTimerManager getBeginningTimerManager() {
        return beginningTimerManager;
    }

    public RestartTimerManager getRestartTimerManager() {
        return restartTimerManager;
    }

    public EndTimerManager getEndTimerManager() {
        return endTimerManager;
    }

    public AnnouncerManager getAnnouncerManager() {
        return announcerManager;
    }

    public MotdManager getMotdManager() {
        return motdManager;
    }

    public GUIManager getGuiManager() {
        return guiManager;
    }
}
