package de.timing.gui;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

/**
 * Interface for GUI implementations.
 */
public interface GUI {

    /**
     * Get the inventory for this GUI.
     */
    Inventory getInventory();

    /**
     * Handle a click in the GUI.
     */
    void onClick(Player player, int slot, ItemStack item, boolean shiftClick, boolean rightClick);

    /**
     * Handle the GUI being closed.
     */
    default void onClose(Player player) {
        // Default: do nothing
    }
}
