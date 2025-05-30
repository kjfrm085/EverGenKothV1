package me.kjfrm085.evergenkoth;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldedit.bukkit.BukkitPlayer;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.session.SessionManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

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

            // Get WorldEdit selection
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

            // Save selection to config
            com.sk89q.worldedit.util.Location min = selection.getMinimumPoint().toVector().toLocation(Bukkit.getWorld(selection.getWorld().getName()));
            com.sk89q.worldedit.util.Location max = selection.getMaximumPoint().toVector().toLocation(Bukkit.getWorld(selection.getWorld().getName()));

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

        // ... other command logic

        return false;
    }
}
