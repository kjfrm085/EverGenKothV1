package me.kjfrm085.evergenkoth;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class KothCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("evergenkoth.admin")) {
            sender.sendMessage(EverGenKothPlugin.getInstance().getConfig().getString("messages.not_admin"));
            return true;
        }
        if (args.length == 0) {
            sender.sendMessage("Usage: /evergenkoth <create|remove|start|stop|list|setreward>");
            return true;
        }
        // TODO: Add subcommand logic
        sender.sendMessage("Command processing coming soon!");
        return true;
    }
}
