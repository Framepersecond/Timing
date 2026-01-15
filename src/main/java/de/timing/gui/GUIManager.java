package de.timing.gui;

import de.timing.Timing;
import de.timing.util.MessageUtil;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Base GUI manager handling inventory interactions.
 */
public class GUIManager implements Listener {

    private final Timing plugin;
    private final Map<UUID, GUI> openGuis;

    public GUIManager(Timing plugin) {
        this.plugin = plugin;
        this.openGuis = new HashMap<>();
    }

    /**
     * Open a GUI for a player.
     */
    public void openGUI(Player player, GUI gui) {
        openGuis.put(player.getUniqueId(), gui);
        player.openInventory(gui.getInventory());
    }

    /**
     * Close GUI for a player.
     */
    public void closeGUI(Player player) {
        openGuis.remove(player.getUniqueId());
    }

    /**
     * Get the GUI a player has open.
     */
    public GUI getOpenGUI(Player player) {
        return openGuis.get(player.getUniqueId());
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) {
            return;
        }

        GUI gui = openGuis.get(player.getUniqueId());
        if (gui == null) {
            return;
        }

        if (event.getClickedInventory() != gui.getInventory()) {
            event.setCancelled(true);
            return;
        }

        event.setCancelled(true);
        gui.onClick(player, event.getSlot(), event.getCurrentItem(), event.isShiftClick(), event.isRightClick());
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (!(event.getPlayer() instanceof Player player)) {
            return;
        }

        GUI gui = openGuis.get(player.getUniqueId());
        if (gui == null) {
            return;
        }

        if (event.getInventory() == gui.getInventory()) {
            gui.onClose(player);
            openGuis.remove(player.getUniqueId());
        }
    }

    /**
     * Create a basic item with name and lore.
     */
    public static ItemStack createItem(Material material, String name, String... lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.displayName(MessageUtil.parse(name));
            if (lore.length > 0) {
                List<Component> loreComponents = Arrays.stream(lore)
                        .filter(line -> line != null)
                        .map(MessageUtil::parse)
                        .toList();
                if (!loreComponents.isEmpty()) {
                    meta.lore(loreComponents);
                }
            }
            item.setItemMeta(meta);
        }
        return item;
    }

    /**
     * Create a filler item (glass pane).
     */
    public static ItemStack createFiller() {
        return createItem(Material.GRAY_STAINED_GLASS_PANE, " ");
    }

    /**
     * Fill empty slots with filler items.
     */
    public static void fillEmpty(Inventory inventory) {
        ItemStack filler = createFiller();
        for (int i = 0; i < inventory.getSize(); i++) {
            if (inventory.getItem(i) == null) {
                inventory.setItem(i, filler);
            }
        }
    }

    public Timing getPlugin() {
        return plugin;
    }
}
