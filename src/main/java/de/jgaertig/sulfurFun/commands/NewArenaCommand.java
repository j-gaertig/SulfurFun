package de.jgaertig.sulfurFun.commands;

import de.jgaertig.sulfurFun.Mode;
import de.jgaertig.sulfurFun.SulfurFun;
import de.jgaertig.sulfurFun.arena.setup.SetupManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jspecify.annotations.NonNull;

import java.io.File;
import java.util.List;

public class NewArenaCommand implements CommandExecutor, TabCompleter {

    private final JavaPlugin plugin;
    private final SulfurFun.LanguageManager languageManager;
    private final SetupManager setupManager;

    public NewArenaCommand(JavaPlugin plugin, SulfurFun.LanguageManager languageManager, SetupManager setupManager) {
        this.plugin = plugin;
        this.languageManager = languageManager;
        this.setupManager = setupManager;
    }

    @Override
    public boolean onCommand(@NonNull CommandSender sender, @NonNull Command command, @NonNull String label, String[] args) {

        if (!((SulfurFun) plugin).hasCommandPermission(sender, "newarena")) {
            languageManager.send(sender, "no_permission");
            return true;
        }

        if (!(sender instanceof Player)) {
            languageManager.send(sender, "for_players_only");
            return true;
        }

        if (!(args.length == 2)) {
            languageManager.send(sender, "newarena_usage");
            return true;
        }

        String gamemode = args[0];
        String arena_id = args[1];

        Mode mode;
        try {
            mode = Mode.valueOf(gamemode.toUpperCase());
        } catch (IllegalArgumentException e) {
            languageManager.send(sender, "gamemode_not_found");
            return true;
        }

        File arenasFile = new File(plugin.getDataFolder(), "arenas.yml");
        YamlConfiguration arenasConfig = YamlConfiguration.loadConfiguration(arenasFile);

        if (arenasConfig.contains(arena_id)) {
            languageManager.send(sender, "arena_already_exists");
            return true;
        }

        Player player = (Player) sender;

        setupManager.startSetup(player, mode, arena_id);

        return true;
    }

    @Override
    public List<String> onTabComplete(@NonNull CommandSender sender, @NonNull Command command, @NonNull String label, String[] args) {

        if (args.length == 1) {
            List<String> gamemodes = new java.util.ArrayList<>();
            for (Mode mode : Mode.values()) {
                gamemodes.add(mode.name().toLowerCase());
            }
            return gamemodes;
        }

        if (args.length == 2) {
            return List.of("<arena_id>");
        }

        return List.of();
    }

}