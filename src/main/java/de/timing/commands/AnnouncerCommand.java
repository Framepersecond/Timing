package de.timing.commands;

import de.timing.Timing;
import de.timing.gui.AnnouncerGUI;
import de.timing.util.MessageUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Command handler for /announcer.
 */
public class AnnouncerCommand implements CommandExecutor, TabCompleter {

    private final Timing plugin;

    public AnnouncerCommand(Timing plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, 
                            @NotNull String label, @NotNull String[] args) {
        
        if (!sender.hasPermission("timing.announcer")) {
            sender.sendMessage(MessageUtil.error("You don't have permission to use this command!"));
            return true;
        }

        // No args - open GUI
        if (args.length == 0) {
            if (!(sender instanceof Player player)) {
                sender.sendMessage(MessageUtil.error("This command can only be used by players!"));
                return true;
            }
            
            plugin.getGuiManager().openGUI(player, new AnnouncerGUI(plugin));
            return true;
        }

        String subCommand = args[0].toLowerCase();

        switch (subCommand) {
            case "send" -> {
                if (args.length < 2) {
                    sender.sendMessage(MessageUtil.error("Usage: /announcer send <name>"));
                    return true;
                }
                
                String name = args[1];
                if (plugin.getAnnouncerManager().broadcast(name)) {
                    sender.sendMessage(MessageUtil.success("Announcement <yellow>" + name + "</yellow> sent!"));
                } else {
                    sender.sendMessage(MessageUtil.error("Announcement not found: " + name));
                }
            }
            
            case "reload" -> {
                plugin.getConfigManager().reload();
                plugin.getAnnouncerManager().loadAnnouncements();
                sender.sendMessage(MessageUtil.success("Configuration reloaded!"));
            }
            
            case "list" -> {
                var announcements = plugin.getAnnouncerManager().getAnnouncements();
                if (announcements.isEmpty()) {
                    sender.sendMessage(MessageUtil.info("No announcements found."));
                } else {
                    sender.sendMessage(MessageUtil.info("Announcements:"));
                    for (String name : announcements.keySet()) {
                        var ann = announcements.get(name);
                        String status = ann.isEnabled() ? "<green>Enabled</green>" : "<red>Disabled</red>";
                        sender.sendMessage(MessageUtil.parse("  <gray>-</gray> <yellow>" + name + "</yellow> <gray>(" + ann.getType() + ")</gray> " + status));
                    }
                }
            }
            
            default -> sendUsage(sender);
        }

        return true;
    }

    private void sendUsage(CommandSender sender) {
        sender.sendMessage(MessageUtil.info("Announcer Commands:"));
        sender.sendMessage(MessageUtil.parse("<gray>/announcer</gray> <white>- Open GUI</white>"));
        sender.sendMessage(MessageUtil.parse("<gray>/announcer send <name></gray> <white>- Send announcement</white>"));
        sender.sendMessage(MessageUtil.parse("<gray>/announcer list</gray> <white>- List announcements</white>"));
        sender.sendMessage(MessageUtil.parse("<gray>/announcer reload</gray> <white>- Reload config</white>"));
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, 
                                                 @NotNull String alias, @NotNull String[] args) {
        if (args.length == 1) {
            return Arrays.asList("send", "list", "reload");
        }
        if (args.length == 2 && args[0].equalsIgnoreCase("send")) {
            return new ArrayList<>(plugin.getAnnouncerManager().getAnnouncements().keySet());
        }
        return List.of();
    }
}
