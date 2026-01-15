package de.timing.gui;

import de.timing.Timing;
import de.timing.announcer.Announcement;
import de.timing.util.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Main announcer GUI showing list of announcements.
 */
public class AnnouncerGUI implements GUI {

    private final Timing plugin;
    private final Inventory inventory;
    private final List<String> announcementNames;

    private static final int CREATE_SLOT = 49; // Bottom center

    public AnnouncerGUI(Timing plugin) {
        this.plugin = plugin;
        this.inventory = Bukkit.createInventory(null, 54, MessageUtil.parse("<gradient:gold:yellow>Announcer</gradient>"));
        this.announcementNames = new ArrayList<>();
        setupInventory();
    }

    private void setupInventory() {
        inventory.clear();
        announcementNames.clear();

        // Load announcements
        Map<String, Announcement> announcements = plugin.getAnnouncerManager().getAnnouncements();
        
        int slot = 0;
        for (Map.Entry<String, Announcement> entry : announcements.entrySet()) {
            if (slot >= 45) break; // Leave bottom row for controls
            
            Announcement announcement = entry.getValue();
            announcementNames.add(entry.getKey());

            Material material = announcement.isEnabled() ? Material.PAPER : Material.GRAY_DYE;
            String status = announcement.isEnabled() ? "<green>Enabled</green>" : "<red>Disabled</red>";
            
            ItemStack item = GUIManager.createItem(
                material,
                "<yellow>" + announcement.getName() + "</yellow>",
                "<gray>Type: <white>" + announcement.getType().name() + "</white></gray>",
                "<gray>Status: " + status + "</gray>",
                "",
                "<green>Left-click</green> <gray>to edit</gray>",
                "<yellow>Right-click</yellow> <gray>to toggle</gray>",
                "<red>Shift-click</red> <gray>to delete</gray>"
            );
            
            inventory.setItem(slot, item);
            slot++;
        }

        // Create new button
        ItemStack createItem = GUIManager.createItem(
            Material.LIME_DYE,
            "<green>Create New Announcement</green>",
            "<gray>Click to create a new announcement</gray>"
        );
        inventory.setItem(CREATE_SLOT, createItem);

        // Fill empty slots on bottom row
        ItemStack filler = GUIManager.createFiller();
        for (int i = 45; i < 54; i++) {
            if (inventory.getItem(i) == null) {
                inventory.setItem(i, filler);
            }
        }
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }

    @Override
    public void onClick(Player player, int slot, ItemStack item, boolean shiftClick, boolean rightClick) {
        if (item == null || item.getType() == Material.GRAY_STAINED_GLASS_PANE) {
            return;
        }

        // Create new announcement
        if (slot == CREATE_SLOT) {
            // Start chat input for name
            ChatInputListener.startInput(player, "announcement_name", input -> {
                String name = input.replace(" ", "_").toLowerCase();
                Announcement newAnnouncement = plugin.getAnnouncerManager().createNew(name);
                
                // Open editor for the new announcement
                player.getScheduler().run(plugin, task -> {
                    plugin.getGuiManager().openGUI(player, new AnnouncementEditorGUI(plugin, newAnnouncement, true));
                }, null);
            });
            
            player.closeInventory();
            player.sendMessage(MessageUtil.info("Enter a name for the new announcement:"));
            return;
        }

        // Handle announcement slot
        if (slot < announcementNames.size()) {
            String name = announcementNames.get(slot);
            Announcement announcement = plugin.getAnnouncerManager().getAnnouncement(name);
            
            if (announcement == null) {
                return;
            }

            if (shiftClick) {
                // Delete
                plugin.getAnnouncerManager().deleteAnnouncement(name);
                player.sendMessage(MessageUtil.success("Announcement <yellow>" + name + "</yellow> deleted!"));
                setupInventory(); // Refresh
            } else if (rightClick) {
                // Toggle enabled
                announcement.setEnabled(!announcement.isEnabled());
                plugin.getAnnouncerManager().saveAnnouncement(announcement);
                player.sendMessage(MessageUtil.success("Announcement <yellow>" + name + "</yellow> " + 
                    (announcement.isEnabled() ? "<green>enabled</green>" : "<red>disabled</red>") + "!"));
                setupInventory(); // Refresh
            } else {
                // Edit
                plugin.getGuiManager().openGUI(player, new AnnouncementEditorGUI(plugin, announcement.copy(), false));
            }
        }
    }
}
