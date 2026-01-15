package de.timing.gui;

import de.timing.Timing;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

/**
 * Handles chat input for GUI text entry.
 */
public class ChatInputListener implements Listener {

    private static final Map<UUID, InputSession> pendingInputs = new HashMap<>();

    private final Timing plugin;

    public ChatInputListener(Timing plugin) {
        this.plugin = plugin;
    }

    /**
     * Start listening for chat input from a player.
     */
    public static void startInput(Player player, String field, Consumer<String> callback) {
        pendingInputs.put(player.getUniqueId(), new InputSession(field, callback));
    }

    /**
     * Cancel pending input for a player.
     */
    public static void cancelInput(Player player) {
        pendingInputs.remove(player.getUniqueId());
    }

    /**
     * Check if a player is currently in input mode.
     */
    public static boolean isWaitingForInput(Player player) {
        return pendingInputs.containsKey(player.getUniqueId());
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        InputSession session = pendingInputs.remove(player.getUniqueId());

        if (session == null) {
            return;
        }

        event.setCancelled(true);
        String message = event.getMessage();

        if (message.equalsIgnoreCase("cancel")) {
            player.getScheduler().run(plugin, task -> {
                de.timing.util.MessageUtil.info("Input cancelled.");
            }, null);
            return;
        }

        session.callback().accept(message);
    }

    private record InputSession(String field, Consumer<String> callback) {
    }
}
