package de.jgaertig.sulfurFun.commands;

import de.jgaertig.sulfurFun.SulfurFun;
import de.jgaertig.sulfurFun.arena.setup.SetupManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class DeleteGameCommand implements CommandExecutor, TabCompleter {

    private final SulfurFun plugin;
    private final SulfurFun.LanguageManager languageManager;
    private final SetupManager setupManager;

    public DeleteGameCommand(SulfurFun plugin, SulfurFun.LanguageManager languageManager, SetupManager setupManager) {
        this.plugin = plugin;
        this.languageManager = languageManager;
        this.setupManager = setupManager;
    }

    @Override
    public boolean onCommand(org.bukkit.command.@NonNull CommandSender sender, org.bukkit.command.@NonNull Command command, @NonNull String label, String[] args) {

        if (!((SulfurFun) plugin).hasCommandPermission(sender, "deletearena")) {
            languageManager.send(sender, "no_permission");
            return true;
        }

        if (!(args.length == 1)) {
            languageManager.send(sender, "deletearena_usage");
            return true;
        }

        File file = new File(plugin.getDataFolder(), "arenas.yml");
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);

        String arena_id = args[0];

        if (!(config.contains(arena_id))) {
            languageManager.send(sender, "arena_not_found");
            return true;
        } else {
            config.set(arena_id, null);
            try {
                config.save(file);
                plugin.getArenaManager().reload();
                languageManager.send(sender, "arena_deleted");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

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
