package de.timing.timer;

import de.timing.Timing;
import com.destroystokyo.paper.event.server.PaperServerListPingEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import net.kyori.adventure.text.Component;

/**
 * Listener for End Timer events.
 * Handles MOTD modification during End dimension countdown (only when server is
 * started).
 */
public class EndTimerListener implements Listener {

    private final Timing plugin;

    public EndTimerListener(Timing plugin) {
        this.plugin = plugin;
    }

    /**
     * Modify MOTD during End countdown (only when server is started).
     */
    @EventHandler(priority = EventPriority.HIGH)
    public void onServerListPing(PaperServerListPingEvent event) {
        if (!plugin.isServerStarted() || !plugin.getEndTimerManager().isRunning()) {
            return;
        }

        if (plugin.getBeginningTimerManager().isRunning() || plugin.getRestartTimerManager().isRunning()) {
            return;
        }

        Component motd = plugin.getEndTimerManager().getMotd();
        if (motd != null) {
            event.motd(motd);
        }
    }
}
