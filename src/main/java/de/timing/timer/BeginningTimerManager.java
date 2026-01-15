package de.timing.timer;

import de.timing.Timing;
import de.timing.util.MessageUtil;
import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

/**
 * Manages the server beginning countdown timer.
 * During the countdown, modifies MOTD and kicks connecting players.
 * When countdown ends, disables whitelist and sets server as started.
 */
public class BeginningTimerManager {

    private final Timing plugin;
    private ScheduledTask timerTask;
    private int remainingSeconds;
    private boolean running;

    public BeginningTimerManager(Timing plugin) {
        this.plugin = plugin;
        this.running = false;
        this.remainingSeconds = 0;
    }

    /**
     * Start the countdown timer.
     * 
     * @param seconds Duration in seconds
     */
    public void start(int seconds) {
        if (running) {
            stop();
        }

        this.remainingSeconds = seconds;
        this.running = true;

        timerTask = plugin.getServer().getGlobalRegionScheduler().runAtFixedRate(plugin, task -> {
            if (remainingSeconds <= 0) {
                onTimerEnd();
                task.cancel();
                return;
            }

            if (shouldBroadcast(remainingSeconds)) {
                broadcastCountdown();
            }

            remainingSeconds--;
        }, 1, 20);

        plugin.getLogger().info("Beginning timer started with " + seconds + " seconds");
    }

    /**
     * Stop the countdown timer.
     */
    public void stop() {
        if (timerTask != null) {
            timerTask.cancel();
            timerTask = null;
        }
        running = false;
        remainingSeconds = 0;
        plugin.getLogger().info("Beginning timer stopped");
    }

    /**
     * Called when the countdown reaches zero.
     */
    private void onTimerEnd() {
        running = false;
        remainingSeconds = 0;

        if (plugin.getConfigManager().isDisableWhitelistOnEnd()) {
            plugin.getServer().setWhitelist(false);
            plugin.getLogger().info("Whitelist disabled - server is now open!");
        }

        plugin.setServerStarted(true);

        Component message = MessageUtil.success("Server is now open!");
        for (Player player : plugin.getServer().getOnlinePlayers()) {
            player.sendMessage(message);
        }

        plugin.getLogger().info("Beginning timer completed - server is now open!");
    }

    /**
     * Broadcast countdown message to online players.
     */
    private void broadcastCountdown() {
        String timeFormatted = MessageUtil.formatTime(remainingSeconds);
        Component message = MessageUtil.info("Server starting in <white>" + timeFormatted + "</white>");

        for (Player player : plugin.getServer().getOnlinePlayers()) {
            player.sendMessage(message);
        }
    }

    /**
     * Determine if we should broadcast at this time.
     */
    private boolean shouldBroadcast(int seconds) {
        return seconds == 60 || seconds == 30 || seconds == 15 ||
                seconds == 10 || seconds <= 5;
    }

    /**
     * Get the current MOTD for server list ping.
     */
    public Component getMotd() {
        if (!running) {
            return null;
        }
        String format = plugin.getConfigManager().getBeginningTimerMotdFormat();
        return MessageUtil.parseWithTime(format, remainingSeconds);
    }

    /**
     * Get the kick message for connecting players.
     */
    public Component getKickMessage() {
        if (!running) {
            return null;
        }
        String format = plugin.getConfigManager().getBeginningTimerKickMessage();
        return MessageUtil.parseWithTime(format, remainingSeconds);
    }

    public boolean isRunning() {
        return running;
    }

    public int getRemainingSeconds() {
        return remainingSeconds;
    }
}
