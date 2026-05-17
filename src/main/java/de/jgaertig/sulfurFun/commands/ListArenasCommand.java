package de.jgaertig.sulfurFun.commands;

import de.jgaertig.sulfurFun.SulfurFun;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.io.File;
import java.util.List;

public class ListArenasCommand implements CommandExecutor, TabCompleter {

    private final SulfurFun plugin;
    private final SulfurFun.LanguageManager languageManager;

    public ListArenasCommand(SulfurFun plugin, SulfurFun.LanguageManager languageManager) {
        this.plugin = plugin;
        this.languageManager = languageManager;
    }

    @Override
    public boolean onCommand(@NonNull CommandSender sender, @NonNull Command command, @NonNull String label, @NonNull String[] args) {

        if (!((SulfurFun) plugin).hasCommandPermission(sender, "listarenas")) {
            languageManager.send(sender, "no_permission");
            return true;
        }

        java.io.File file = new java.io.File(plugin.getDataFolder(), "arenas.yml");
        org.bukkit.configuration.file.YamlConfiguration config = org.bukkit.configuration.file.YamlConfiguration.loadConfiguration(file);
        java.util.Set<String> keys = config.getKeys(false);

        if (args.length == 0) {
            if (keys.isEmpty()) {
                sender.sendMessage("§cEs wurden noch keine Arenen erstellt.");
                return true;
            }

            String list = String.join(", ", keys);
            sender.sendMessage("§6Verfügbare Arenen: §e" + list);
            sender.sendMessage("" + list);
            sender.sendMessage("Type '/listarenas <arena_id>' to get more information about this arena.");
            return true;
        }

        if (args.length == 1) {
            String arenaId = args[0];

            if (!config.contains(arenaId)) {
                languageManager.send(sender, "arena_not_found");
                return true;
            }

            sender.sendMessage("§7--- §6Arena Details: §e" + arenaId + " §7---");
            sender.sendMessage("§bModus: §f" + config.getString(arenaId + ".mode"));
            sender.sendMessage("§bName: §f" + config.getString(arenaId + ".name"));
            sender.sendMessage("§bSpieler: §f" + config.getInt(arenaId + ".minPlayers") + " - " + config.getInt(arenaId + ".maxPlayers"));

            sender.sendMessage("§7-----------------------");
            return true;
        }

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NonNull CommandSender sender, @NonNull Command command, @NonNull String label, @NonNull String[] args) {

        if (args.length == 1) {
            File file = new File(plugin.getDataFolder(), "arenas.yml");
            YamlConfiguration config = YamlConfiguration.loadConfiguration(file);

            return new java.util.ArrayList<>(config.getKeys(false));
        }

        return List.of();
    }
}
