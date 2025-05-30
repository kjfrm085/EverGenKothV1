package me.kjfrm085.evergenkoth;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class EverGenKothPlugin extends JavaPlugin {
    private static EverGenKothPlugin instance;
    private KothScoreboard kothScoreboard;
    private BukkitRunnable kothScheduler;
    private int kothDurationSeconds = 300; // default: 5 minutes

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
        getCommand("evergenkoth").setExecutor(new KothCommand(this));
        getLogger().info("EverGenKoth enabled!");
        // Start scheduler if config has interval
        String interval = getConfig().getString("schedule.interval");
        if (interval != null) startKothScheduler(new KothCommand(this).parseTime(interval));
    }

    @Override
    public void onDisable() {
        if (kothScheduler != null) kothScheduler.cancel();
        getLogger().info("EverGenKoth disabled!");
    }

    public static EverGenKothPlugin getInstance() {
        return instance;
    }

    // Start the KOTH scheduler with the given interval in seconds
    public void startKothScheduler(int intervalSeconds) {
        if (kothScheduler != null) kothScheduler.cancel();
        kothScheduler = new BukkitRunnable() {
            @Override
            public void run() {
                startKoth();
            }
        };
        kothScheduler.runTaskTimer(this, 0L, intervalSeconds * 20L);
    }

    // Start a KOTH event
    public void startKoth() {
        Bukkit.broadcastMessage("§cKOTH has started! Capture the hill!");
        if (kothScoreboard != null) kothScoreboard.stop();
        kothScoreboard = new KothScoreboard(this, kothDurationSeconds);
        kothScoreboard.start();
        new BukkitRunnable() {
            @Override
            public void run() {
                endKoth();
            }
        }.runTaskLater(this, kothDurationSeconds * 20L);
    }

    // End a KOTH event (for demo, gives all loot to a random online player)
    public void endKoth() {
        if (kothScoreboard != null) kothScoreboard.stop();
        Player winner = getRandomOnlinePlayer();
        if (winner != null) {
            List<ItemStack> allLoot = getAllLoot();
            for (ItemStack item : allLoot) {
                winner.getInventory().addItem(item.clone());
            }
            Bukkit.broadcastMessage("§aKOTH ended! Winner: " + winner.getName() + " received all loot.");
        } else {
            Bukkit.broadcastMessage("§cKOTH ended! No players online to receive loot.");
        }
    }

    // Helper: Get a random online player
    private Player getRandomOnlinePlayer() {
        List<? extends Player> players = new ArrayList<>(Bukkit.getOnlinePlayers());
        if (players.isEmpty()) return null;
        Collections.shuffle(players);
        return players.get(0);
    }

    // Helper: Get all loot items from all loot pools
    public List<ItemStack> getAllLoot() {
        List<ItemStack> loot = new ArrayList<>();
        ConfigurationSection lootSection = getConfig().getConfigurationSection("loot");
        if (lootSection != null) {
            for (String key : lootSection.getKeys(false)) {
                ItemStack item = getConfig().getItemStack("loot." + key);
                if (item != null) loot.add(item);
            }
        }
        return loot;
    }
}
