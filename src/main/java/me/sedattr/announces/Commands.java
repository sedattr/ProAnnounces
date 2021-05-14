package me.sedattr.announces;

import me.sedattr.announces.helpers.Announces;
import me.sedattr.announces.helpers.Utils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Commands implements CommandExecutor, TabCompleter, Listener {
    public boolean noPermission(CommandSender player, String text) {
        String permission = ProAnnounces.settingsSection.getString("permissions." + text);
        if (permission == null || permission.equals("")) return false;

        return !player.hasPermission(permission);
    }

    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] args) {
        if (noPermission(commandSender, "command")) return null;

        ArrayList<String> complete = new ArrayList<>();
        if (!noPermission(commandSender, "reload")) complete.add("reload");
        if (!noPermission(commandSender, "info")) complete.add("info");
        if (!noPermission(commandSender, "toggle")) complete.add("toggle");

        if (args.length == 1) return complete;
        return null;
    }

    public boolean onCommand(CommandSender commandSender, Command command, String label, String[] args) {
        if (noPermission(commandSender, "command")) {
            commandSender.sendMessage(Utils.colorize(ProAnnounces.messagesSection.getString("noPermission")));
            return false;
        }

        if (args.length > 0) {
            switch (args[0].toLowerCase()) {
                case "reload":
                if (noPermission(commandSender, "reload")) {
                    commandSender.sendMessage(Utils.colorize(ProAnnounces.messagesSection.getString("noPermission")));
                    return false;
                }

                Announces.toggledPlayers = new ArrayList<>();
                ProAnnounces.getInstance().reloadConfig();
                Announces.reloadAnnounces();

                commandSender.sendMessage(Utils.colorize(ProAnnounces.messagesSection.getString("reloaded")));
                return true;

                case "info":
                for (String message : ProAnnounces.messagesSection.getStringList("info")) {
                    commandSender.sendMessage(Utils.colorize(message
                            .replace("%reload%", String.valueOf(ProAnnounces.settingsSection.getInt("reload")))
                            .replace("%minimum_players%", String.valueOf(ProAnnounces.settingsSection.getInt("minimumPlayers")))
                            .replace("%random%", String.valueOf(ProAnnounces.settingsSection.getBoolean("random")).replace("f", "F").replace("t", "T"))
                            .replace("%sound%", String.valueOf(ProAnnounces.settingsSection.getBoolean("sound.enabled")).replace("t", "T").replace("f", "F"))
                            .replace("%current_announce%", Announces.announces.get(Announces.announceNumber))
                            .replace("%announce_count%", String.valueOf(Announces.announceCount))));
                }
                return true;

                case "toggle":
                if (!(commandSender instanceof Player)) {
                    commandSender.sendMessage(Utils.colorize(ProAnnounces.messagesSection.getString("notPlayer")));
                    return false;
                }

                if (noPermission(commandSender, "toggle")) {
                    commandSender.sendMessage(Utils.colorize(ProAnnounces.messagesSection.getString("noPermission")));
                    return false;
                }

                boolean disabled = Announces.toggledPlayers.remove(commandSender);
                if (disabled)
                    commandSender.sendMessage(Utils.colorize(ProAnnounces.messagesSection.getString("announcesEnabled")));
                else {
                    commandSender.sendMessage(Utils.colorize(ProAnnounces.messagesSection.getString("announcesDisabled")));
                    Announces.toggledPlayers.add((Player) commandSender);
                }
                return true;
            }
        }

        for (String message : ProAnnounces.messagesSection.getStringList("usage"))
            commandSender.sendMessage(Utils.colorize(message));
        return false;
    }
}
