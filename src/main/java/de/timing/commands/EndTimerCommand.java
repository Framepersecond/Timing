package de.timing.commands;

import de.timing.Timing;
import de.timing.timer.EndTimerManager;
import de.timing.util.MessageUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;

/**
 * Command handler for /endtimer (End dimension timer).
 */
public class EndTimerCommand implements CommandExecutor, TabCompleter {

    private final Timing plugin;

    public EndTimerCommand(Timing plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, 
                            @NotNull String label, @NotNull String[] args) {
        
        if (!sender.hasPermission("timing.endtimer")) {
            sender.sendMessage(MessageUtil.error("You don't have permission to use this command!"));
            return true;
        }

        if (args.length == 0) {
            sendUsage(sender);
            return true;
        }

        EndTimerManager manager = plugin.getEndTimerManager();
        String subCommand = args[0].toLowerCase();

        switch (subCommand) {
            case "start" -> {
                if (args.length < 2) {
                    sender.sendMessage(MessageUtil.error("Usage: /endtimer start <seconds>"));
                    return true;
                }
                
                int seconds;
                try {
                    seconds = Integer.parseInt(args[1]);
                } catch (NumberFormatException e) {
                    sender.sendMessage(MessageUtil.error("Invalid number: " + args[1]));
                    return true;
                }
                
                if (seconds <= 0) {
                    sender.sendMessage(MessageUtil.error("Duration must be greater than 0!"));
                    return true;
                }
                
                manager.start(seconds);
                sender.sendMessage(MessageUtil.success("<light_purple>End</light_purple> timer started for <yellow>" + 
                    MessageUtil.formatTime(seconds) + "</yellow>!"));
            }
            
            case "stop" -> {
                if (!manager.isRunning()) {
                    sender.sendMessage(MessageUtil.error("End timer is not running!"));
                    return true;
                }
                
                manager.stop();
                sender.sendMessage(MessageUtil.success("<light_purple>End</light_purple> timer stopped!"));
            }
            
            case "status" -> {
                if (manager.isRunning()) {
                    sender.sendMessage(MessageUtil.info("<light_purple>End</light_purple> timer: <green>Running</green>"));
                    sender.sendMessage(MessageUtil.info("Remaining: <yellow>" + 
                        MessageUtil.formatTime(manager.getRemainingSeconds()) + "</yellow>"));
                } else {
                    sender.sendMessage(MessageUtil.info("<light_purple>End</light_purple> timer: <red>Stopped</red>"));
                }
                sender.sendMessage(MessageUtil.info("Server started: " + 
                    (plugin.isServerStarted() ? "<green>Yes</green>" : "<red>No</red>")));
            }
            
            default -> sendUsage(sender);
        }

        return true;
    }

    private void sendUsage(CommandSender sender) {
        sender.sendMessage(MessageUtil.info("<light_purple>End</light_purple> Timer Commands:"));
        sender.sendMessage(MessageUtil.parse("<gray>/endtimer start <seconds></gray> <white>- Start End dimension countdown</white>"));
        sender.sendMessage(MessageUtil.parse("<gray>/endtimer stop</gray> <white>- Stop countdown</white>"));
        sender.sendMessage(MessageUtil.parse("<gray>/endtimer status</gray> <white>- Show status</white>"));
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, 
                                                 @NotNull String alias, @NotNull String[] args) {
        if (args.length == 1) {
            return Arrays.asList("start", "stop", "status");
        }
        if (args.length == 2 && args[0].equalsIgnoreCase("start")) {
            return Arrays.asList("300", "600", "900", "1800", "3600");
        }
        return List.of();
    }
}
