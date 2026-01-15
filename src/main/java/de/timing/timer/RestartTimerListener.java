package de.timing.timer;

import de.timing.Timing;
import com.destroystokyo.paper.event.server.PaperServerListPingEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import net.kyori.adventure.text.Component;

/**
 * Listener for Restart Timer events.
 * Handles MOTD modification and player kick during countdown.
 */
public class RestartTimerListener implements Listener {

    private final Timing plugin;

    public RestartTimerListener(Timing plugin) {
        this.plugin = plugin;
    }

    /**
     * Modify MOTD during countdown.
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onServerListPing(PaperServerListPingEvent event) {
        if (!plugin.getRestartTimerManager().isRunning()) {
            return;
        }

        Component motd = plugin.getRestartTimerManager().getMotd();
        if (motd != null) {
            event.motd(motd);
        }
    }

    /**
     * Kick players trying to connect during restart countdown.
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerPreLogin(AsyncPlayerPreLoginEvent event) {
        if (!plugin.getRestartTimerManager().isRunning()) {
            return;
        }

        Component kickMessage = plugin.getRestartTimerManager().getKickMessage();
        if (kickMessage != null) {
            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, kickMessage);
        }
    }
}
