package me.sedattr.announces;

import org.bukkit.Bukkit;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

public class UpdateChecker {
    public UpdateChecker() throws IOException {
        int projectID = 60796;

        URLConnection con = new URL("https://api.spigotmc.org/legacy/update.php?resource=" + projectID).openConnection();
        String oldVersion = ProAnnounces.getInstance().getDescription().getVersion();
        String newVersion = new BufferedReader(new InputStreamReader(con.getInputStream())).readLine();
        if (!oldVersion.equals(newVersion))
            Bukkit.getConsoleSender().sendMessage("§8[§bProAnnounces§8] §cNew version found! " + oldVersion + "/" + newVersion);
        else
            Bukkit.getConsoleSender().sendMessage("§8[§bProAnnounces§8] §aPlugin is up to date!");
    }
}