package de.timing.commands;

import de.timing.Timing;
import de.timing.timer.RestartTimerManager;
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
 * Command handler for /restarttimer.
 */
public class RestartTimerCommand implements CommandExecutor, TabCompleter {

    private final Timing plugin;

    public RestartTimerCommand(Timing plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, 
                            @NotNull String label, @NotNull String[] args) {
        
        if (!sender.hasPermission("timing.restarttimer")) {
            sender.sendMessage(MessageUtil.error("You don't have permission to use this command!"));
            return true;
        }

        if (args.length == 0) {
            sendUsage(sender);
            return true;
        }

        RestartTimerManager manager = plugin.getRestartTimerManager();
        String subCommand = args[0].toLowerCase();

        switch (subCommand) {
            case "start" -> {
                if (args.length < 2) {
                    sender.sendMessage(MessageUtil.error("Usage: /restarttimer start <seconds>"));
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
                sender.sendMessage(MessageUtil.success("Restart timer started for <yellow>" + 
                    MessageUtil.formatTime(seconds) + "</yellow>!"));
            }
            
            case "stop" -> {
                if (!manager.isRunning()) {
                    sender.sendMessage(MessageUtil.error("Restart timer is not running!"));
                    return true;
                }
                
                manager.stop();
                sender.sendMessage(MessageUtil.success("Restart timer stopped!"));
            }
            
            case "status" -> {
                if (manager.isRunning()) {
                    sender.sendMessage(MessageUtil.info("Restart timer: <green>Running</green>"));
                    sender.sendMessage(MessageUtil.info("Remaining: <yellow>" + 
                        MessageUtil.formatTime(manager.getRemainingSeconds()) + "</yellow>"));
                } else {
                    sender.sendMessage(MessageUtil.info("Restart timer: <red>Stopped</red>"));
                }
            }
            
            default -> sendUsage(sender);
        }

        return true;
    }

    private void sendUsage(CommandSender sender) {
        sender.sendMessage(MessageUtil.info("Restart Timer Commands:"));
        sender.sendMessage(MessageUtil.parse("<gray>/restarttimer start <seconds></gray> <white>- Start countdown</white>"));
        sender.sendMessage(MessageUtil.parse("<gray>/restarttimer stop</gray> <white>- Stop countdown</white>"));
        sender.sendMessage(MessageUtil.parse("<gray>/restarttimer status</gray> <white>- Show status</white>"));
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, 
                                                 @NotNull String alias, @NotNull String[] args) {
        if (args.length == 1) {
            return Arrays.asList("start", "stop", "status");
        }
        if (args.length == 2 && args[0].equalsIgnoreCase("start")) {
            return Arrays.asList("30", "60", "120", "300", "600");
        }
        return List.of();
    }
}
