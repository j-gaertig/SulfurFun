package de.jgaertig.sulfurFun.commands;

import de.jgaertig.sulfurFun.SulfurFun;
import de.jgaertig.sulfurFun.listeners.SetupListener;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class DeleteGame implements CommandExecutor, TabCompleter {

    private final SulfurFun plugin;
    private final SetupListener setupListener;

    public DeleteGame(SulfurFun plugin, SetupListener setupListener) {
        this.plugin = plugin; // Hier speichern wir das Plugin-Objekt
        this.setupListener = setupListener;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(args.length == 1)) {
            sender.sendMessage(ChatColor.YELLOW + "Usage: /deletegame <name_of_existing_arena>");
            return false;
        }

        String arenaName = args[0];

        if (plugin.getArenaConfig().contains(arenaName)) {
            // Hier löschen wir den gesamten Abschnitt der Arena
            plugin.getArenaConfig().set(arenaName, null);

            setupListener.stopSessionsForArena(arenaName);

            // Und speichern...
            plugin.saveArenaConfig();
            sender.sendMessage(ChatColor.GREEN + "Arena " + arenaName + " is now deleted!");            }
        else {
            sender.sendMessage(ChatColor.RED + "This arena doesn't exist.");
        }



        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 1) {
            return List.of(plugin.getArenaConfig().getKeys(false).toArray(new String[0]));
        }

        return List.of();
    }
}
