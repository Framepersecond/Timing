package de.timing.announcer;

import de.timing.Timing;
import de.timing.util.MessageUtil;
import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import org.bukkit.entity.Player;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

/**
 * Manages announcements and their scheduled broadcasts.
 */
public class AnnouncerManager {

    private final Timing plugin;
    private final Map<String, Announcement> announcements;
    private final Map<String, ScheduledTask> scheduledTasks;

    public AnnouncerManager(Timing plugin) {
        this.plugin = plugin;
        this.announcements = new HashMap<>();
        this.scheduledTasks = new HashMap<>();
        loadAnnouncements();
    }

    /**
     * Load all announcements from config.
     */
    public void loadAnnouncements() {
        stopAll();
        announcements.clear();
        announcements.putAll(plugin.getConfigManager().loadAnnouncements());
        
        // Start scheduled announcements
        for (Announcement announcement : announcements.values()) {
            if (announcement.isEnabled() && announcement.getInterval() > 0) {
                scheduleAnnouncement(announcement);
            }
        }
        
        plugin.getLogger().info("Loaded " + announcements.size() + " announcements");
    }

    /**
     * Schedule an announcement for recurring broadcast.
     */
    private void scheduleAnnouncement(Announcement announcement) {
        if (scheduledTasks.containsKey(announcement.getName())) {
            scheduledTasks.get(announcement.getName()).cancel();
        }

        long intervalTicks = announcement.getInterval() * 20L;
        
        ScheduledTask task = plugin.getServer().getGlobalRegionScheduler().runAtFixedRate(
            plugin,
            t -> broadcast(announcement),
            intervalTicks,
            intervalTicks
        );
        
        scheduledTasks.put(announcement.getName(), task);
    }

    /**
     * Stop all scheduled announcements.
     */
    public void stopAll() {
        for (ScheduledTask task : scheduledTasks.values()) {
            task.cancel();
        }
        scheduledTasks.clear();
    }

    /**
     * Broadcast an announcement to all players.
     */
    public void broadcast(Announcement announcement) {
        if (announcement == null || !announcement.isEnabled()) {
            return;
        }

        Component message = MessageUtil.parse(announcement.getMessage());

        for (Player player : plugin.getServer().getOnlinePlayers()) {
            sendToPlayer(player, announcement, message);
        }
    }

    /**
     * Broadcast an announcement by name.
     */
    public boolean broadcast(String name) {
        Announcement announcement = announcements.get(name);
        if (announcement == null) {
            return false;
        }
        broadcast(announcement);
        return true;
    }

    /**
     * Send announcement to a specific player.
     */
    public void sendToPlayer(Player player, Announcement announcement, Component message) {
        switch (announcement.getType()) {
            case ACTION_BAR:
                player.sendActionBar(message);
                break;
            case TITLE:
                Component subtitle = MessageUtil.parse(announcement.getSubtitle());
                Title.Times times = Title.Times.times(
                    Duration.ofMillis(announcement.getFadeIn() * 50L),
                    Duration.ofMillis(announcement.getStay() * 50L),
                    Duration.ofMillis(announcement.getFadeOut() * 50L)
                );
                player.showTitle(Title.title(message, subtitle, times));
                break;
            case SUBTITLE:
                Component emptyTitle = Component.empty();
                Title.Times subTimes = Title.Times.times(
                    Duration.ofMillis(announcement.getFadeIn() * 50L),
                    Duration.ofMillis(announcement.getStay() * 50L),
                    Duration.ofMillis(announcement.getFadeOut() * 50L)
                );
                player.showTitle(Title.title(emptyTitle, message, subTimes));
                break;
        }
    }

    /**
     * Preview an announcement to a specific player.
     */
    public void preview(Player player, Announcement announcement) {
        Component message = MessageUtil.parse(announcement.getMessage());
        sendToPlayer(player, announcement, message);
    }

    /**
     * Save an announcement.
     */
    public void saveAnnouncement(Announcement announcement) {
        announcements.put(announcement.getName(), announcement);
        plugin.getConfigManager().saveAnnouncement(announcement);
        
        // Reschedule if needed
        if (scheduledTasks.containsKey(announcement.getName())) {
            scheduledTasks.get(announcement.getName()).cancel();
            scheduledTasks.remove(announcement.getName());
        }
        
        if (announcement.isEnabled() && announcement.getInterval() > 0) {
            scheduleAnnouncement(announcement);
        }
    }

    /**
     * Delete an announcement.
     */
    public void deleteAnnouncement(String name) {
        announcements.remove(name);
        plugin.getConfigManager().deleteAnnouncement(name);
        
        if (scheduledTasks.containsKey(name)) {
            scheduledTasks.get(name).cancel();
            scheduledTasks.remove(name);
        }
    }

    /**
     * Get an announcement by name.
     */
    public Announcement getAnnouncement(String name) {
        return announcements.get(name);
    }

    /**
     * Get all announcements.
     */
    public Map<String, Announcement> getAnnouncements() {
        return new HashMap<>(announcements);
    }

    /**
     * Create a new announcement with default values.
     */
    public Announcement createNew(String name) {
        return new Announcement(name, "<yellow>New announcement</yellow>", AnnouncementType.ACTION_BAR);
    }
}
