package me.kjfrm085.evergenkoth;

import org.bukkit.plugin.java.JavaPlugin;

public class EverGenKothPlugin extends JavaPlugin {
    private static EverGenKothPlugin instance;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
        // Register command executor
        getCommand("evergenkoth").setExecutor(new KothCommand(this));
        getLogger().info("EverGenKoth enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().info("EverGenKoth disabled!");
    }

    public static EverGenKothPlugin getInstance() {
        return instance;
    }
}
