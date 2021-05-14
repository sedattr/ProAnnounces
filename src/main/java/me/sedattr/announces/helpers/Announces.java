package me.sedattr.announces.helpers;

import me.clip.placeholderapi.PlaceholderAPI;
import me.sedattr.announces.ProAnnounces;
import org.bukkit.Bukkit;
import org.bukkit.Statistic;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitScheduler;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Announces implements Listener {
    private static BukkitScheduler scheduler = null;
    public static List<String> announces = new ArrayList<>();
    public static List<Player> toggledPlayers = new ArrayList<>();
    public static int announceNumber;
    public static int announceCount;

    public static void reloadAnnounces() {
        ProAnnounces.settingsSection = ProAnnounces.getInstance().getConfig().getConfigurationSection("settings");
        ProAnnounces.announcesSection = ProAnnounces.getInstance().getConfig().getConfigurationSection("announces");
        ProAnnounces.messagesSection = ProAnnounces.getInstance().getConfig().getConfigurationSection("messages");

        announces = new ArrayList<>(ProAnnounces.announcesSection.getKeys(false));
        announceCount = announces.size();
        announceNumber = 0;

        startAnnounces();
    }

    public static void startAnnounces() {
        if (scheduler != null) Bukkit.getScheduler().cancelTasks(ProAnnounces.getInstance());

        scheduler = Bukkit.getServer().getScheduler();
        scheduler.scheduleSyncRepeatingTask(ProAnnounces.getInstance(), () -> {
            if (Bukkit.getOnlinePlayers().size() >= ProAnnounces.settingsSection.getInt("minimumPlayers")) {
                if (ProAnnounces.settingsSection.getBoolean("random")) announceNumber = new Random().nextInt(announceCount);
                else if (announceNumber >= announceCount) announceNumber = 0;

                for (String announce : ProAnnounces.announcesSection.getStringList(announces.get(announceNumber))) {
                    announce = Utils.colorizeRGB(announce
                            .replace("%header%", Utils.colorize(ProAnnounces.messagesSection.getString("header")))
                            .replace("%prefix%", Utils.colorize(ProAnnounces.messagesSection.getString("prefix")))
                            .replace("%footer%", Utils.colorize(ProAnnounces.messagesSection.getString("footer"))));

                    for (Player player : Bukkit.getOnlinePlayers()) {
                        if (toggledPlayers.contains(player)) continue;

                        if (ProAnnounces.settingsSection.getBoolean("worlds.enabled")) {
                            if (!ProAnnounces.settingsSection.getStringList("worlds." + player.getWorld().getName())
                                    .contains(announces.get(announceNumber))) continue;
                        }

                        Object entityPlayer;
                        int ping = 0;
                        if (announce.contains("%ping%")) {
                            try {
                                entityPlayer = player.getClass().getMethod("getHandle").invoke(player);
                                ping = (int) entityPlayer.getClass().getField("ping").get(entityPlayer);
                            } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException | NoSuchFieldException ignored) {
                            }
                        }

                        announce = announce
                                .replace("%world%", player.getWorld().getName())
                                .replace("%location_x%", String.valueOf(player.getLocation().getX()))
                                .replace("%location_y%", String.valueOf(player.getLocation().getY()))
                                .replace("%location_z%", String.valueOf(player.getLocation().getZ()))
                                .replace("%xp%", String.valueOf(player.getExp()))
                                .replace("%hunger%", String.valueOf(player.getFoodLevel()))
                                .replace("%health%", String.valueOf(player.getHealth()))
                                .replace("%online%", String.valueOf(Bukkit.getOnlinePlayers().size()))
                                .replace("%name%", player.getName())
                                .replace("%displayname%", player.getDisplayName())
                                .replace("%ping%", String.valueOf(ping))
                                .replace("%deaths%", String.valueOf(player.getStatistic(Statistic.DEATHS)));

                        // If PlaceholderAPI is enabled and working, use PlaceholderAPI for placeholders.
                        if (ProAnnounces.placeholderAPI) {
                            if (announce.contains("%center")) Utils.sendCenteredMessage(player, PlaceholderAPI.setPlaceholders(player, announce.replace("%center%", "")));
                            else player.sendMessage(PlaceholderAPI.setPlaceholders(player, Utils.colorize(announce)));
                        }

                        // Else if PlaceholderAPI is not working, use normal message style.
                        else {
                            if (announce.contains("%center")) Utils.sendCenteredMessage(player, announce.replace("%center%", ""));
                            else player.sendMessage(Utils.colorize(announce));
                        }
                    }
                }

                Utils.broadcastSound();
                announceNumber++;
            }
        }, ProAnnounces.settingsSection.getInt("reload") * 20L, ProAnnounces.settingsSection.getInt("reload") * 20L);
    }
}
