package de.timing.gui;

import de.timing.Timing;
import de.timing.motd.MotdConfig;
import de.timing.util.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

/**
 * GUI for editing the server MOTD with formatting options.
 */
public class MotdEditorGUI implements GUI {

    private final Timing plugin;
    private final Inventory inventory;
    private MotdConfig config;
    
    // Gradient color tracking
    private String gradientColor1 = null;
    private String gradientColor2 = null;
    
    // Which line we're editing (1 or 2)
    private int editingLine = 0;

    // Slot positions
    private static final int LINE1_SLOT = 11;
    private static final int LINE2_SLOT = 13;
    private static final int TOGGLE_SLOT = 15;
    
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
    private static final int SAVE_SLOT = 42;
    private static final int CANCEL_SLOT = 44;

    public MotdEditorGUI(Timing plugin) {
        this.plugin = plugin;
        this.config = plugin.getMotdManager().getConfig().copy();
        this.inventory = Bukkit.createInventory(null, 54, 
            MessageUtil.parse("<gradient:gold:yellow>MOTD Editor</gradient>"));
        setupInventory();
    }

    private void setupInventory() {
        inventory.clear();

        // Line 1 edit button
        String line1Indicator = (editingLine == 1) ? "<green>✓ EDITING</green>" : "";
        inventory.setItem(LINE1_SLOT, GUIManager.createItem(
            Material.WRITABLE_BOOK,
            "<yellow>Line 1 (Top)</yellow>",
            line1Indicator.isEmpty() ? null : line1Indicator,
            "<gray>Current:</gray>",
            "<white>" + truncate(config.getLine1(), 25) + "</white>",
            "",
            "<green>Left-click</green> <gray>to type in chat</gray>",
            "<yellow>Right-click</yellow> <gray>to select for formatting</gray>"
        ));

        // Line 2 edit button
        String line2Indicator = (editingLine == 2) ? "<green>✓ EDITING</green>" : "";
        inventory.setItem(LINE2_SLOT, GUIManager.createItem(
            Material.BOOK,
            "<yellow>Line 2 (Bottom)</yellow>",
            line2Indicator.isEmpty() ? null : line2Indicator,
            "<gray>Current:</gray>",
            "<white>" + truncate(config.getLine2(), 25) + "</white>",
            "",
            "<green>Left-click</green> <gray>to type in chat</gray>",
            "<yellow>Right-click</yellow> <gray>to select for formatting</gray>"
        ));

        // Enable/Disable toggle
        Material toggleMaterial = config.isEnabled() ? Material.LIME_DYE : Material.GRAY_DYE;
        String toggleStatus = config.isEnabled() ? "<green>Enabled</green>" : "<red>Disabled</red>";
        inventory.setItem(TOGGLE_SLOT, GUIManager.createItem(
            toggleMaterial,
            "<yellow>Custom MOTD</yellow>",
            "<gray>Status: " + toggleStatus + "</gray>",
            "",
            "<green>Click</green> <gray>to toggle</gray>"
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

        // Format options - show which line is selected
        String lineInfo = editingLine == 0 ? "<red>Select a line first (right-click)</red>" : 
                         "<green>Editing Line " + editingLine + "</green>";

        inventory.setItem(BOLD_SLOT, GUIManager.createItem(
            Material.ENCHANTED_BOOK,
            "<bold>Bold</bold>",
            lineInfo,
            "<gray>Click to wrap line in bold</gray>"
        ));

        inventory.setItem(ITALIC_SLOT, GUIManager.createItem(
            Material.FEATHER,
            "<italic>Italic</italic>",
            lineInfo,
            "<gray>Click to wrap line in italic</gray>"
        ));

        inventory.setItem(UNDERLINE_SLOT, GUIManager.createItem(
            Material.IRON_INGOT,
            "<underlined>Underline</underlined>",
            lineInfo,
            "<gray>Click to wrap line in underline</gray>"
        ));

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

        inventory.setItem(GRADIENT_SLOT, GUIManager.createItem(
            Material.PRISMARINE_SHARD,
            "<gradient:gold:red>Apply Gradient</gradient>",
            lineInfo,
            gradientStatus,
            "<gray>Select colors above, then click</gray>"
        ));

        inventory.setItem(RAINBOW_SLOT, GUIManager.createItem(
            Material.NETHER_STAR,
            "<rainbow>Rainbow</rainbow>",
            lineInfo,
            "<gray>Click to wrap line in rainbow</gray>"
        ));

        // Preview button
        inventory.setItem(PREVIEW_SLOT, GUIManager.createItem(
            Material.ENDER_EYE,
            "<light_purple>Preview</light_purple>",
            "<gray>Shows MOTD in chat</gray>"
        ));

        // Save button
        inventory.setItem(SAVE_SLOT, GUIManager.createItem(
            Material.LIME_WOOL,
            "<green>Save</green>",
            "<gray>Save MOTD settings</gray>"
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

        // Line 1
        if (slot == LINE1_SLOT) {
            if (rightClick) {
                editingLine = 1;
                player.sendMessage(MessageUtil.info("Now editing <yellow>Line 1</yellow>. Use formatting buttons!"));
                setupInventory();
            } else {
                ChatInputListener.startInput(player, "motd_line1", input -> {
                    config.setLine1(input);
                    player.getScheduler().run(plugin, task -> {
                        plugin.getGuiManager().openGUI(player, new MotdEditorGUI(plugin, config, editingLine, gradientColor1, gradientColor2));
                    }, null);
                });
                player.closeInventory();
                player.sendMessage(MessageUtil.info("Enter Line 1 (MiniMessage format):"));
            }
            return;
        }

        // Line 2
        if (slot == LINE2_SLOT) {
            if (rightClick) {
                editingLine = 2;
                player.sendMessage(MessageUtil.info("Now editing <yellow>Line 2</yellow>. Use formatting buttons!"));
                setupInventory();
            } else {
                ChatInputListener.startInput(player, "motd_line2", input -> {
                    config.setLine2(input);
                    player.getScheduler().run(plugin, task -> {
                        plugin.getGuiManager().openGUI(player, new MotdEditorGUI(plugin, config, editingLine, gradientColor1, gradientColor2));
                    }, null);
                });
                player.closeInventory();
                player.sendMessage(MessageUtil.info("Enter Line 2 (MiniMessage format):"));
            }
            return;
        }

        // Toggle enabled
        if (slot == TOGGLE_SLOT) {
            config.setEnabled(!config.isEnabled());
            setupInventory();
            return;
        }

        // Color selection for gradient
        for (int i = 0; i < COLOR_SLOTS.length; i++) {
            if (slot == COLOR_SLOTS[i]) {
                String color = COLORS[i];
                if (rightClick) {
                    gradientColor2 = color;
                    player.sendMessage(MessageUtil.info("Gradient end: <" + color + ">" + color + "</" + color + ">"));
                } else {
                    gradientColor1 = color;
                    player.sendMessage(MessageUtil.info("Gradient start: <" + color + ">" + color + "</" + color + ">"));
                }
                setupInventory();
                return;
            }
        }

        // Formatting - requires line selection
        if (editingLine == 0) {
            if (slot == BOLD_SLOT || slot == ITALIC_SLOT || slot == UNDERLINE_SLOT || 
                slot == GRADIENT_SLOT || slot == RAINBOW_SLOT) {
                player.sendMessage(MessageUtil.error("Right-click a line first to select it for formatting!"));
                return;
            }
        }

        // Apply bold
        if (slot == BOLD_SLOT && editingLine > 0) {
            applyFormat("<bold>", "</bold>");
            player.sendMessage(MessageUtil.success("Applied bold to Line " + editingLine));
            setupInventory();
            return;
        }

        // Apply italic
        if (slot == ITALIC_SLOT && editingLine > 0) {
            applyFormat("<italic>", "</italic>");
            player.sendMessage(MessageUtil.success("Applied italic to Line " + editingLine));
            setupInventory();
            return;
        }

        // Apply underline
        if (slot == UNDERLINE_SLOT && editingLine > 0) {
            applyFormat("<underlined>", "</underlined>");
            player.sendMessage(MessageUtil.success("Applied underline to Line " + editingLine));
            setupInventory();
            return;
        }

        // Apply gradient
        if (slot == GRADIENT_SLOT && editingLine > 0) {
            if (gradientColor1 == null || gradientColor2 == null) {
                player.sendMessage(MessageUtil.error("Select both gradient colors first!"));
                return;
            }
            applyFormat("<gradient:" + gradientColor1 + ":" + gradientColor2 + ">", "</gradient>");
            player.sendMessage(MessageUtil.success("Applied gradient to Line " + editingLine));
            gradientColor1 = null;
            gradientColor2 = null;
            setupInventory();
            return;
        }

        // Apply rainbow
        if (slot == RAINBOW_SLOT && editingLine > 0) {
            applyFormat("<rainbow>", "</rainbow>");
            player.sendMessage(MessageUtil.success("Applied rainbow to Line " + editingLine));
            setupInventory();
            return;
        }

        // Preview
        if (slot == PREVIEW_SLOT) {
            player.sendMessage(MessageUtil.info("MOTD Preview:"));
            player.sendMessage(MessageUtil.parse(config.getLine1()));
            player.sendMessage(MessageUtil.parse(config.getLine2()));
            return;
        }

        // Save
        if (slot == SAVE_SLOT) {
            plugin.getMotdManager().setConfig(config);
            plugin.getMotdManager().save();
            player.sendMessage(MessageUtil.success("MOTD saved!"));
            player.closeInventory();
            return;
        }

        // Cancel
        if (slot == CANCEL_SLOT) {
            player.sendMessage(MessageUtil.info("Changes discarded."));
            player.closeInventory();
        }
    }

    private void applyFormat(String prefix, String suffix) {
        if (editingLine == 1) {
            config.setLine1(prefix + config.getLine1() + suffix);
        } else if (editingLine == 2) {
            config.setLine2(prefix + config.getLine2() + suffix);
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

    // Constructor for restoring state after chat input
    public MotdEditorGUI(Timing plugin, MotdConfig config, int editingLine, String gradientColor1, String gradientColor2) {
        this.plugin = plugin;
        this.config = config;
        this.editingLine = editingLine;
        this.gradientColor1 = gradientColor1;
        this.gradientColor2 = gradientColor2;
        this.inventory = Bukkit.createInventory(null, 54, 
            MessageUtil.parse("<gradient:gold:yellow>MOTD Editor</gradient>"));
        setupInventory();
    }
}
