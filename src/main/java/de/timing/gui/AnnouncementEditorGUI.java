package de.timing.gui;

import de.timing.Timing;
import de.timing.announcer.Announcement;
import de.timing.announcer.AnnouncementType;
import de.timing.util.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

/**
 * GUI for editing an announcement with formatting options.
 */
public class AnnouncementEditorGUI implements GUI {

    private final Timing plugin;
    private final Inventory inventory;
    private Announcement announcement;
    private final boolean isNew;
    
    // Gradient color tracking
    private String gradientColor1 = null;
    private String gradientColor2 = null;

    // Slot positions
    private static final int MESSAGE_SLOT = 11;
    private static final int SUBTITLE_SLOT = 13;
    private static final int TYPE_SLOT = 15;
    
    // Color slots (row 2)
    private static final int[] COLOR_SLOTS = {19, 20, 21, 22, 23, 24, 25};
    private static final String[] COLORS = {"red", "gold", "yellow", "green", "aqua", "blue", "light_purple"};
    private static final Material[] COLOR_MATERIALS = {
        Material.RED_DYE, Material.ORANGE_DYE, Material.YELLOW_DYE, 
        Material.LIME_DYE, Material.CYAN_DYE, Material.BLUE_DYE, Material.PURPLE_DYE
    };

    // Format slots (row 3)
    private static final int BOLD_SLOT = 29;
    private static final int ITALIC_SLOT = 30;
    private static final int UNDERLINE_SLOT = 31;
    private static final int GRADIENT_SLOT = 32;
    private static final int RAINBOW_SLOT = 33;

    // Control slots (row 4)
    private static final int PREVIEW_SLOT = 38;
    private static final int INTERVAL_SLOT = 40;
    private static final int SAVE_SLOT = 42;
    private static final int CANCEL_SLOT = 44;

    public AnnouncementEditorGUI(Timing plugin, Announcement announcement, boolean isNew) {
        this.plugin = plugin;
        this.announcement = announcement;
        this.isNew = isNew;
        this.inventory = Bukkit.createInventory(null, 54, 
            MessageUtil.parse("<gradient:gold:yellow>Edit: " + announcement.getName() + "</gradient>"));
        setupInventory();
    }

    private void setupInventory() {
        inventory.clear();

        // Message edit button
        inventory.setItem(MESSAGE_SLOT, GUIManager.createItem(
            Material.WRITABLE_BOOK,
            "<yellow>Set Message</yellow>",
            "<gray>Current:</gray>",
            "<white>" + truncate(announcement.getMessage(), 30) + "</white>",
            "",
            "<green>Click</green> <gray>to type in chat</gray>"
        ));

        // Subtitle edit button (only for TITLE type)
        if (announcement.getType() == AnnouncementType.TITLE) {
            inventory.setItem(SUBTITLE_SLOT, GUIManager.createItem(
                Material.BOOK,
                "<yellow>Set Subtitle</yellow>",
                "<gray>Current:</gray>",
                "<white>" + truncate(announcement.getSubtitle(), 30) + "</white>",
                "",
                "<green>Click</green> <gray>to type in chat</gray>"
            ));
        } else {
            inventory.setItem(SUBTITLE_SLOT, GUIManager.createItem(
                Material.BARRIER,
                "<gray>Subtitle</gray>",
                "<red>Only available for TITLE type</red>"
            ));
        }

        // Type selector
        Material typeMaterial = switch (announcement.getType()) {
            case ACTION_BAR -> Material.EXPERIENCE_BOTTLE;
            case TITLE -> Material.NAME_TAG;
            case SUBTITLE -> Material.PAPER;
        };
        inventory.setItem(TYPE_SLOT, GUIManager.createItem(
            typeMaterial,
            "<yellow>Display Type</yellow>",
            "<gray>Current: <white>" + announcement.getType().name() + "</white></gray>",
            "",
            "<green>Click</green> <gray>to cycle</gray>"
        ));

        // Color dyes with gradient instructions
        for (int i = 0; i < COLOR_SLOTS.length; i++) {
            String color = COLORS[i];
            String selected1 = (color.equals(gradientColor1)) ? "<green>✓ Gradient Start</green>" : "";
            String selected2 = (color.equals(gradientColor2)) ? "<green>✓ Gradient End</green>" : "";
            
            inventory.setItem(COLOR_SLOTS[i], GUIManager.createItem(
                COLOR_MATERIALS[i],
                "<" + color + ">" + capitalize(color) + "</" + color + ">",
                selected1.isEmpty() ? null : selected1,
                selected2.isEmpty() ? null : selected2,
                "",
                "<green>Left-click</green> <gray>= Gradient 1st color</gray>",
                "<yellow>Right-click</yellow> <gray>= Gradient 2nd color</gray>"
            ));
        }

        // Gradient status display
        String gradientStatus = "<gray>Colors: </gray>";
        if (gradientColor1 != null) {
            gradientStatus += "<" + gradientColor1 + ">" + gradientColor1 + "</" + gradientColor1 + ">";
        } else {
            gradientStatus += "<dark_gray>none</dark_gray>";
        }
        gradientStatus += " <gray>→</gray> ";
        if (gradientColor2 != null) {
            gradientStatus += "<" + gradientColor2 + ">" + gradientColor2 + "</" + gradientColor2 + ">";
        } else {
            gradientStatus += "<dark_gray>none</dark_gray>";
        }

        // Format options
        inventory.setItem(BOLD_SLOT, GUIManager.createItem(
            Material.ENCHANTED_BOOK,
            "<bold>Bold</bold>",
            "<gray>Click to wrap message in bold</gray>"
        ));

        inventory.setItem(ITALIC_SLOT, GUIManager.createItem(
            Material.FEATHER,
            "<italic>Italic</italic>",
            "<gray>Click to wrap message in italic</gray>"
        ));

        inventory.setItem(UNDERLINE_SLOT, GUIManager.createItem(
            Material.IRON_INGOT,
            "<underlined>Underline</underlined>",
            "<gray>Click to wrap message in underline</gray>"
        ));

        inventory.setItem(GRADIENT_SLOT, GUIManager.createItem(
            Material.PRISMARINE_SHARD,
            "<gradient:gold:red>Apply Gradient</gradient>",
            gradientStatus,
            "",
            "<gray>Select colors above, then click to apply</gray>"
        ));

        inventory.setItem(RAINBOW_SLOT, GUIManager.createItem(
            Material.NETHER_STAR,
            "<rainbow>Rainbow</rainbow>",
            "<gray>Click to wrap message in rainbow</gray>"
        ));

        // Preview button
        inventory.setItem(PREVIEW_SLOT, GUIManager.createItem(
            Material.ENDER_EYE,
            "<light_purple>Preview</light_purple>",
            "<gray>Click to preview this announcement</gray>"
        ));

        // Interval setting
        inventory.setItem(INTERVAL_SLOT, GUIManager.createItem(
            Material.CLOCK,
            "<yellow>Broadcast Interval</yellow>",
            "<gray>Current: <white>" + (announcement.getInterval() == 0 ? "Manual only" : announcement.getInterval() + "s") + "</white></gray>",
            "",
            "<green>Left-click</green> <gray>to increase</gray>",
            "<red>Right-click</red> <gray>to decrease</gray>",
            "<yellow>Shift-click</yellow> <gray>for larger steps</gray>"
        ));

        // Save button
        inventory.setItem(SAVE_SLOT, GUIManager.createItem(
            Material.LIME_WOOL,
            "<green>Save</green>",
            "<gray>Save this announcement</gray>"
        ));

        // Cancel button
        inventory.setItem(CANCEL_SLOT, GUIManager.createItem(
            Material.RED_WOOL,
            "<red>Cancel</red>",
            "<gray>Discard changes</gray>"
        ));

        // Fill empty slots
        GUIManager.fillEmpty(inventory);
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

        // Set message
        if (slot == MESSAGE_SLOT) {
            ChatInputListener.startInput(player, "message", input -> {
                announcement.setMessage(input);
                player.getScheduler().run(plugin, task -> {
                    plugin.getGuiManager().openGUI(player, new AnnouncementEditorGUI(plugin, announcement, isNew));
                }, null);
            });
            player.closeInventory();
            player.sendMessage(MessageUtil.info("Enter the message (MiniMessage format supported):"));
            player.sendMessage(MessageUtil.parse("<gray>Example: <yellow><bold>Hello</bold> <rainbow>World</rainbow></yellow></gray>"));
            return;
        }

        // Set subtitle
        if (slot == SUBTITLE_SLOT && announcement.getType() == AnnouncementType.TITLE) {
            ChatInputListener.startInput(player, "subtitle", input -> {
                announcement.setSubtitle(input);
                player.getScheduler().run(plugin, task -> {
                    plugin.getGuiManager().openGUI(player, new AnnouncementEditorGUI(plugin, announcement, isNew));
                }, null);
            });
            player.closeInventory();
            player.sendMessage(MessageUtil.info("Enter the subtitle:"));
            return;
        }

        // Cycle type
        if (slot == TYPE_SLOT) {
            AnnouncementType[] types = AnnouncementType.values();
            int currentIndex = announcement.getType().ordinal();
            int nextIndex = (currentIndex + 1) % types.length;
            announcement.setType(types[nextIndex]);
            setupInventory();
            return;
        }

        // Color selection for gradient
        for (int i = 0; i < COLOR_SLOTS.length; i++) {
            if (slot == COLOR_SLOTS[i]) {
                String color = COLORS[i];
                
                if (rightClick) {
                    // Right-click = second gradient color
                    gradientColor2 = color;
                    player.sendMessage(MessageUtil.info("Gradient end color: <" + color + ">" + color + "</" + color + ">"));
                } else {
                    // Left-click = first gradient color
                    gradientColor1 = color;
                    player.sendMessage(MessageUtil.info("Gradient start color: <" + color + ">" + color + "</" + color + ">"));
                }
                
                setupInventory();
                return;
            }
        }

        // Apply bold
        if (slot == BOLD_SLOT) {
            String current = announcement.getMessage();
            announcement.setMessage("<bold>" + current + "</bold>");
            player.sendMessage(MessageUtil.success("Applied <bold>bold</bold> to message!"));
            setupInventory();
            return;
        }

        // Apply italic
        if (slot == ITALIC_SLOT) {
            String current = announcement.getMessage();
            announcement.setMessage("<italic>" + current + "</italic>");
            player.sendMessage(MessageUtil.success("Applied <italic>italic</italic> to message!"));
            setupInventory();
            return;
        }

        // Apply underline
        if (slot == UNDERLINE_SLOT) {
            String current = announcement.getMessage();
            announcement.setMessage("<underlined>" + current + "</underlined>");
            player.sendMessage(MessageUtil.success("Applied <underlined>underline</underlined> to message!"));
            setupInventory();
            return;
        }

        // Apply gradient
        if (slot == GRADIENT_SLOT) {
            if (gradientColor1 == null || gradientColor2 == null) {
                player.sendMessage(MessageUtil.error("Select both gradient colors first! Left-click = start, Right-click = end"));
                return;
            }
            
            String current = announcement.getMessage();
            announcement.setMessage("<gradient:" + gradientColor1 + ":" + gradientColor2 + ">" + current + "</gradient>");
            player.sendMessage(MessageUtil.success("Applied <gradient:" + gradientColor1 + ":" + gradientColor2 + ">gradient</gradient> to message!"));
            
            // Reset gradient selection
            gradientColor1 = null;
            gradientColor2 = null;
            setupInventory();
            return;
        }

        // Apply rainbow
        if (slot == RAINBOW_SLOT) {
            String current = announcement.getMessage();
            announcement.setMessage("<rainbow>" + current + "</rainbow>");
            player.sendMessage(MessageUtil.success("Applied <rainbow>rainbow</rainbow> to message!"));
            setupInventory();
            return;
        }

        // Preview
        if (slot == PREVIEW_SLOT) {
            plugin.getAnnouncerManager().preview(player, announcement);
            return;
        }

        // Interval
        if (slot == INTERVAL_SLOT) {
            int delta = shiftClick ? 60 : 10;
            if (rightClick) delta = -delta;
            
            int newInterval = Math.max(0, announcement.getInterval() + delta);
            announcement.setInterval(newInterval);
            setupInventory();
            return;
        }

        // Save
        if (slot == SAVE_SLOT) {
            plugin.getAnnouncerManager().saveAnnouncement(announcement);
            player.sendMessage(MessageUtil.success("Announcement <yellow>" + announcement.getName() + "</yellow> saved!"));
            plugin.getGuiManager().openGUI(player, new AnnouncerGUI(plugin));
            return;
        }

        // Cancel
        if (slot == CANCEL_SLOT) {
            if (isNew) {
                player.sendMessage(MessageUtil.info("Announcement creation cancelled."));
            } else {
                player.sendMessage(MessageUtil.info("Changes discarded."));
            }
            plugin.getGuiManager().openGUI(player, new AnnouncerGUI(plugin));
        }
    }

    private String truncate(String text, int maxLength) {
        if (text == null) return "";
        if (text.length() <= maxLength) return text;
        return text.substring(0, maxLength) + "...";
    }

    private String capitalize(String text) {
        if (text == null || text.isEmpty()) return text;
        return text.substring(0, 1).toUpperCase() + text.substring(1).replace("_", " ");
    }
}
