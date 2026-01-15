package de.timing.commands;

import de.timing.Timing;
import de.timing.gui.MotdEditorGUI;
import de.timing.util.MessageUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;

/**
 * Command handler for /motd.
 */
public class MotdCommand implements CommandExecutor, TabCompleter {

    private final Timing plugin;

    public MotdCommand(Timing plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, 
                            @NotNull String label, @NotNull String[] args) {
        
        if (!sender.hasPermission("timing.motd")) {
            sender.sendMessage(MessageUtil.error("You don't have permission to use this command!"));
            return true;
        }

        // No args - open GUI (player only)
        if (args.length == 0) {
            if (!(sender instanceof Player player)) {
                sender.sendMessage(MessageUtil.error("Use /motd preview, /motd enable, or /motd disable from console."));
                return true;
            }
            
            plugin.getGuiManager().openGUI(player, new MotdEditorGUI(plugin));
            return true;
        }

        String subCommand = args[0].toLowerCase();

        switch (subCommand) {
            case "preview" -> {
                sender.sendMessage(MessageUtil.info("Current MOTD:"));
                sender.sendMessage(MessageUtil.parse(plugin.getMotdManager().getConfig().getLine1()));
                sender.sendMessage(MessageUtil.parse(plugin.getMotdManager().getConfig().getLine2()));
                sender.sendMessage(MessageUtil.info("Enabled: " + 
                    (plugin.getMotdManager().getConfig().isEnabled() ? "<green>Yes</green>" : "<red>No</red>")));
            }
            
            case "enable" -> {
                plugin.getMotdManager().getConfig().setEnabled(true);
                plugin.getMotdManager().save();
                sender.sendMessage(MessageUtil.success("Custom MOTD enabled!"));
            }
            
            case "disable" -> {
                plugin.getMotdManager().getConfig().setEnabled(false);
                plugin.getMotdManager().save();
                sender.sendMessage(MessageUtil.success("Custom MOTD disabled!"));
            }
            
            case "reload" -> {
                plugin.getMotdManager().reload();
                sender.sendMessage(MessageUtil.success("MOTD reloaded from config!"));
            }
            
            default -> sendUsage(sender);
        }

        return true;
    }

    private void sendUsage(CommandSender sender) {
        sender.sendMessage(MessageUtil.info("MOTD Commands:"));
        sender.sendMessage(MessageUtil.parse("<gray>/motd</gray> <white>- Open editor GUI</white>"));
        sender.sendMessage(MessageUtil.parse("<gray>/motd preview</gray> <white>- Show current MOTD</white>"));
        sender.sendMessage(MessageUtil.parse("<gray>/motd enable</gray> <white>- Enable custom MOTD</white>"));
        sender.sendMessage(MessageUtil.parse("<gray>/motd disable</gray> <white>- Disable custom MOTD</white>"));
        sender.sendMessage(MessageUtil.parse("<gray>/motd reload</gray> <white>- Reload from config</white>"));
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, 
                                                 @NotNull String alias, @NotNull String[] args) {
        if (args.length == 1) {
            return Arrays.asList("preview", "enable", "disable", "reload");
        }
        return List.of();
    }
}
