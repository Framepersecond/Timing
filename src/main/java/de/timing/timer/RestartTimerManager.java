package de.timing.timer;

import de.timing.Timing;
import de.timing.util.MessageUtil;
import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

/**
 * Manages the server restart/stop countdown timer.
 * During the countdown, modifies MOTD and kicks connecting players.
 * When countdown ends, kicks all players.
 */
public class RestartTimerManager {

    private final Timing plugin;
    private ScheduledTask timerTask;
    private int remainingSeconds;
    private boolean running;

    public RestartTimerManager(Timing plugin) {
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

        plugin.getLogger().info("Restart timer started with " + seconds + " seconds");
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
        plugin.getLogger().info("Restart timer stopped");
    }

    /**
     * Called when the countdown reaches zero.
     */
    private void onTimerEnd() {
        running = false;
        remainingSeconds = 0;

        if (plugin.getConfigManager().isKickAllOnRestart()) {
            String kickFormat = plugin.getConfigManager().getRestartFinalKickMessage();
            Component kickMessage = MessageUtil.parse(kickFormat);

            for (Player player : plugin.getServer().getOnlinePlayers()) {
                player.getScheduler().run(plugin, task -> {
                    player.kick(kickMessage);
                }, null);
            }

            plugin.getLogger().info("All players kicked - server stopping!");
        }

        plugin.setServerStarted(false);

        plugin.getLogger().info("Restart timer completed - stopping server!");

        plugin.getServer().getGlobalRegionScheduler().runDelayed(plugin, task -> {
            plugin.getServer().shutdown();
        }, 40);
    }

    /**
     * Broadcast countdown message to online players.
     */
    private void broadcastCountdown() {
        String timeFormatted = MessageUtil.formatTime(remainingSeconds);
        Component message = MessageUtil.info("Server restarting in <white>" + timeFormatted + "</white>");

        for (Player player : plugin.getServer().getOnlinePlayers()) {
            player.sendMessage(message);
        }
    }

    /**
     * Determine if we should broadcast at this time.
     */
    private boolean shouldBroadcast(int seconds) {
        return seconds == 300 || seconds == 180 || seconds == 120 ||
                seconds == 60 || seconds == 30 || seconds == 15 ||
                seconds == 10 || seconds <= 5;
    }

    /**
     * Get the current MOTD for server list ping.
     */
    public Component getMotd() {
        if (!running) {
            return null;
        }
        String format = plugin.getConfigManager().getRestartTimerMotdFormat();
        return MessageUtil.parseWithTime(format, remainingSeconds);
    }

    /**
     * Get the kick message for connecting players.
     */
    public Component getKickMessage() {
        if (!running) {
            return null;
        }
        String format = plugin.getConfigManager().getRestartTimerKickMessage();
        return MessageUtil.parseWithTime(format, remainingSeconds);
    }

    public boolean isRunning() {
        return running;
    }

    public int getRemainingSeconds() {
        return remainingSeconds;
    }
}
