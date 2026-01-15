package de.timing.timer;

import de.timing.Timing;
import de.timing.util.MessageUtil;
import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import net.kyori.adventure.text.Component;
import org.bukkit.World;
import org.bukkit.entity.Player;

/**
 * Manages the End dimension countdown timer.
 * During the countdown, modifies MOTD (when server is started) and shows
 * announcements.
 * When countdown ends, can trigger end dimension events.
 */
public class EndTimerManager {

    private final Timing plugin;
    private ScheduledTask timerTask;
    private int remainingSeconds;
    private boolean running;

    public EndTimerManager(Timing plugin) {
        this.plugin = plugin;
        this.running = false;
        this.remainingSeconds = 0;
    }

    /**
     * Start the End dimension countdown timer.
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

        plugin.getLogger().info("End dimension timer started with " + seconds + " seconds");
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
        plugin.getLogger().info("End dimension timer stopped");
    }

    /**
     * Called when the countdown reaches zero.
     */
    private void onTimerEnd() {
        running = false;
        remainingSeconds = 0;

        Component message = MessageUtil.success("<light_purple>The End</light_purple> is now open!");
        for (Player player : plugin.getServer().getOnlinePlayers()) {
            player.sendMessage(message);
        }

        plugin.getLogger().info("End dimension timer completed - The End is now open!");
    }

    /**
     * Broadcast countdown message to online players.
     */
    private void broadcastCountdown() {
        String timeFormatted = MessageUtil.formatTime(remainingSeconds);
        Component message = MessageUtil
                .prefixed("<light_purple>The End</light_purple> opens in <white>" + timeFormatted + "</white>");

        for (Player player : plugin.getServer().getOnlinePlayers()) {
            player.sendMessage(message);
        }
    }

    /**
     * Determine if we should broadcast at this time.
     */
    private boolean shouldBroadcast(int seconds) {
        return seconds == 600 || seconds == 300 || seconds == 180 || seconds == 120 ||
                seconds == 60 || seconds == 30 || seconds == 15 ||
                seconds == 10 || seconds <= 5;
    }

    /**
     * Get the current MOTD for server list ping (only when server is started).
     */
    public Component getMotd() {
        if (!running || !plugin.isServerStarted()) {
            return null;
        }
        String format = plugin.getConfigManager().getEndTimerMotdFormat();
        return MessageUtil.parseWithTime(format, remainingSeconds);
    }

    public boolean isRunning() {
        return running;
    }

    public int getRemainingSeconds() {
        return remainingSeconds;
    }
}
