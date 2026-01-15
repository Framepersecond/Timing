package de.timing.timer;

import de.timing.Timing;
import com.destroystokyo.paper.event.server.PaperServerListPingEvent;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import net.kyori.adventure.text.Component;

import java.util.UUID;

/**
 * Listener for Beginning Timer events.
 * Handles MOTD modification and player kick during countdown.
 * OPs and whitelisted players can bypass the kick.
 */
public class BeginningTimerListener implements Listener {

    private final Timing plugin;

    public BeginningTimerListener(Timing plugin) {
        this.plugin = plugin;
    }

    /**
     * Modify MOTD during countdown.
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onServerListPing(PaperServerListPingEvent event) {
        if (!plugin.getBeginningTimerManager().isRunning()) {
            return;
        }

        Component motd = plugin.getBeginningTimerManager().getMotd();
        if (motd != null) {
            event.motd(motd);
        }
    }

    /**
     * Kick players trying to connect during countdown.
     * OPs and whitelisted players can bypass.
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerPreLogin(AsyncPlayerPreLoginEvent event) {
        if (!plugin.getBeginningTimerManager().isRunning()) {
            return;
        }

        UUID playerUUID = event.getUniqueId();

        OfflinePlayer offlinePlayer = plugin.getServer().getOfflinePlayer(playerUUID);
        if (offlinePlayer.isOp()) {
            return;
        }

        if (offlinePlayer.isWhitelisted()) {
            return;
        }

        Component kickMessage = plugin.getBeginningTimerManager().getKickMessage();
        if (kickMessage != null) {
            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, kickMessage);
        }
    }
}
