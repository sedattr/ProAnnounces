package me.sedattr.announces;

import java.io.IOException;
import me.sedattr.announces.helpers.Announces;
import org.bstats.bukkit.MetricsLite;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;

public class ProAnnounces extends JavaPlugin {
    private static ProAnnounces instance;
    public static boolean placeholderAPI = false;
    public static ConfigurationSection announcesSection;
    public static ConfigurationSection settingsSection;
    public static ConfigurationSection messagesSection;

    public static ProAnnounces getInstance() {
        return instance;
    }

    public void onEnable() {
        saveDefaultConfig();
        getCommand("proannounces").setExecutor(new Commands());
        instance = this;

        placeholderAPI = Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null;
        Bukkit.getConsoleSender().sendMessage("§8[§bProAnnounces§8] §eLoading announces...");
        Bukkit.getConsoleSender().sendMessage("§8[§bProAnnounces§8] §eStarting announces...");
        Announces.reloadAnnounces();

        new MetricsLite(this, 9940);
        try {
            new UpdateChecker();
        } catch (IOException ignored) {
        }
    }
}