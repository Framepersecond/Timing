package de.timing.motd;

import de.timing.Timing;
import com.destroystokyo.paper.event.server.PaperServerListPingEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import net.kyori.adventure.text.Component;

/**
 * Listener for MOTD events.
 * Shows default MOTD when no timer is overriding.
 */
public class MotdListener implements Listener {

    private final Timing plugin;

    public MotdListener(Timing plugin) {
        this.plugin = plugin;
    }

    /**
     * Set default MOTD (lowest priority - timers override this).
     */
    @EventHandler(priority = EventPriority.LOW)
    public void onServerListPing(PaperServerListPingEvent event) {
        if (!plugin.getMotdManager().shouldShowDefaultMotd()) {
            return;
        }

        Component motd = plugin.getMotdManager().getMotd();
        if (motd != null) {
            event.motd(motd);
        }
    }
}
