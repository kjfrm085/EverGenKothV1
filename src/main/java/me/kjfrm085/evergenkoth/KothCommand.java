package me.kjfrm085.evergenkoth;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.session.SessionManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public class KothCommand implements CommandExecutor {
    private final EverGenKothPlugin plugin;

    public KothCommand(EverGenKothPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("evergenkoth.admin")) {
            sender.sendMessage(plugin.getConfig().getString("messages.not_admin"));
            return true;
        }

        // /evergenkoth setregion <arena>
        if (args.length >= 1 && args[0].equalsIgnoreCase("setregion")) {
            if (!(sender instanceof Player)) {
                sender.sendMessage("Only players can use this command.");
                return true;
            }
            if (args.length < 2) {
                sender.sendMessage("Usage: /evergenkoth setregion <arena>");
                return true;
            }
            Player player = (Player) sender;
            String arena = args[1];
            WorldEditPlugin worldEdit = (WorldEditPlugin) Bukkit.getPluginManager().getPlugin("WorldEdit");
            if (worldEdit == null) {
                player.sendMessage("WorldEdit plugin not found!");
                return true;
            }
            SessionManager sessionManager = worldEdit.getSessionManager();
            com.sk89q.worldedit.session.LocalSession session = sessionManager.get(BukkitAdapter.adapt(player));
            Region selection;
            try {
                selection = session.getSelection(session.getSelectionWorld());
            } catch (Exception e) {
                player.sendMessage("Please make a WorldEdit selection first (pos1 and pos2).");
                return true;
            }
            org.bukkit.World bukkitWorld = Bukkit.getWorld(selection.getWorld().getName());
            org.bukkit.Location min = new org.bukkit.Location(bukkitWorld, selection.getMinimumPoint().getBlockX(), selection.getMinimumPoint().getBlockY(), selection.getMinimumPoint().getBlockZ());
            org.bukkit.Location max = new org.bukkit.Location(bukkitWorld, selection.getMaximumPoint().getBlockX(), selection.getMaximumPoint().getBlockY(), selection.getMaximumPoint().getBlockZ());
            plugin.getConfig().set("arenas." + arena + ".world", selection.getWorld().getName());
            plugin.getConfig().set("arenas." + arena + ".min.x", min.getBlockX());
            plugin.getConfig().set("arenas." + arena + ".min.y", min.getBlockY());
            plugin.getConfig().set("arenas." + arena + ".min.z", min.getBlockZ());
            plugin.getConfig().set("arenas." + arena + ".max.x", max.getBlockX());
            plugin.getConfig().set("arenas." + arena + ".max.y", max.getBlockY());
            plugin.getConfig().set("arenas." + arena + ".max.z", max.getBlockZ());
            plugin.saveConfig();
            player.sendMessage("Saved region for arena '" + arena + "' using your WorldEdit selection.");
            return true;
        }

        // /evergenkoth loot set <name>
        if (args.length >= 3 && args[0].equalsIgnoreCase("loot") && args[1].equalsIgnoreCase("set")) {
            if (!(sender instanceof Player)) {
                sender.sendMessage("Only players can use this command.");
                return true;
            }
            Player player = (Player) sender;
            String lootName = args[2];
            ItemStack item = player.getInventory().getItemInMainHand();
            if (item == null || item.getType() == Material.AIR) {
                player.sendMessage("Hold an item in your hand to set as loot.");
                return true;
            }
            plugin.getConfig().set("loot." + lootName, item);
            plugin.saveConfig();
            player.sendMessage("Added loot: " + lootName);
            return true;
        }

        // /evergenkoth loot remove <name>
        if (args.length >= 3 && args[0].equalsIgnoreCase("loot") && args[1].equalsIgnoreCase("remove")) {
            String lootName = args[2];
            if (!plugin.getConfig().contains("loot." + lootName)) {
                sender.sendMessage("No loot by that name.");
                return true;
            }
            plugin.getConfig().set("loot." + lootName, null);
            plugin.saveConfig();
            sender.sendMessage("Removed loot: " + lootName);
            return true;
        }

        // /evergenkoth schedule <interval>
        if (args.length >= 2 && args[0].equalsIgnoreCase("schedule")) {
            String intervalString = args[1];
            int seconds = parseTime(intervalString);
            if (seconds < 60) {
                sender.sendMessage("Please provide an interval of at least 1 minute.");
                return true;
            }
            plugin.getConfig().set("schedule.interval", intervalString);
            plugin.saveConfig();
            plugin.startKothScheduler(seconds);
            sender.sendMessage("KOTH will now run every " + intervalString + ".");
            return true;
        }

        return false;
    }

    private int parseTime(String input) {
        input = input.toLowerCase();
        if (input.endsWith("m")) {
            return Integer.parseInt(input.replace("m", "")) * 60;
        }
        if (input.endsWith("h")) {
            return Integer.parseInt(input.replace("h", "")) * 3600;
        }
        return Integer.parseInt(input); // fallback: assume seconds
    }
}
