package de.jgaertig.sulfurFun;

import de.jgaertig.sulfurFun.arena.ArenaManager;
import de.jgaertig.sulfurFun.commands.JoinFootballCommand;
import de.jgaertig.sulfurFun.commands.NewArenaCommand;
import de.jgaertig.sulfurFun.game.GameManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class SulfurFun extends JavaPlugin {

    LanguageManager languageManager;
    GameManager gameManager;
    ArenaManager arenaManager;

    @Override
    public void onEnable() {
        setupConfiguration();
        this.languageManager = new LanguageManager(this);
        setupGameSystem();
        registerCommandsAndListeners();
        sendEnableMessage();
    }

    public static Map<String, Mode> modeMap = new HashMap<>(Map.of(
            "football",Mode.FOOTBALL,
            "hockey",Mode.HOCKEY
    ));

    @Override
    public void onDisable() {}

    private void setupConfiguration() {

        saveDefaultConfig();

        if (!getDataFolder().exists()) {
            getDataFolder().mkdirs();
        }
    }

    private void setupGameSystem() {
        this.arenaManager = new ArenaManager(this, languageManager);
        this.gameManager = new GameManager(this, languageManager);
    }

    public void registerCommandsAndListeners(){
        getCommand("football").setExecutor(new JoinFootballCommand(this, languageManager, arenaManager));
        getCommand("newgame").setExecutor(new NewArenaCommand(this, languageManager));
    }

    private void sendEnableMessage() {
        String gold = ChatColor.GOLD.toString();
        String yellow = ChatColor.YELLOW.toString();
        String green = ChatColor.GREEN.toString();
        String gray = ChatColor.GRAY.toString();

        Bukkit.getConsoleSender().sendMessage("");
        Bukkit.getConsoleSender().sendMessage(gold + ChatColor.BOLD + "           SulfurFun");
        Bukkit.getConsoleSender().sendMessage("");
        Bukkit.getConsoleSender().sendMessage(gold + "      ████████████████████      ");
        Bukkit.getConsoleSender().sendMessage(gold + "      ██                ██      ");
        Bukkit.getConsoleSender().sendMessage(gold + "      ██                ██      ");
        Bukkit.getConsoleSender().sendMessage(gold + "      ██                ██      ");
        Bukkit.getConsoleSender().sendMessage(gold + "      ██                ██      ");
        Bukkit.getConsoleSender().sendMessage(gold + "      ██                ██      ");
        Bukkit.getConsoleSender().sendMessage("");
        Bukkit.getConsoleSender().sendMessage(yellow + "    ██████            ██████    ");
        Bukkit.getConsoleSender().sendMessage(yellow + "    ██████            ██████    ");
        Bukkit.getConsoleSender().sendMessage(yellow + "    ██████            ██████    ");
        Bukkit.getConsoleSender().sendMessage("");
        Bukkit.getConsoleSender().sendMessage(yellow + "              ████              ");
        Bukkit.getConsoleSender().sendMessage("");
        Bukkit.getConsoleSender().sendMessage(gray + "Version: " + getDescription().getVersion());
        Bukkit.getConsoleSender().sendMessage(green + "Plugin loaded ...");
        Bukkit.getConsoleSender().sendMessage("");
    }

    public LanguageManager getLanguageManager() {
        return languageManager;
    }

    public static class LanguageManager {
        private final JavaPlugin plugin;
        private final Map<String, FileConfiguration> configs = new HashMap<>();

        public LanguageManager(JavaPlugin plugin) {
            this.plugin = plugin;
            loadLanguages();
        }

        public void loadLanguages() {
            List<String> languages = Arrays.asList(
                    "en_us.yml",
                    "de_de.yml"
            );
            for (String lang : languages) {
                File file = new File(plugin.getDataFolder(), "languages/" + lang);
                if (!file.exists()) {
                    plugin.saveResource("languages/" + lang, false);
                }

                try (java.io.InputStreamReader reader = new java.io.InputStreamReader(
                        new java.io.FileInputStream(file), java.nio.charset.StandardCharsets.UTF_8)) {

                    YamlConfiguration config = YamlConfiguration.loadConfiguration(reader);
                    configs.put(lang.replace(".yml", ""), config);

                } catch (java.io.IOException e) {
                    plugin.getLogger().severe(": " + lang);
                }
            }
        }

        public String getMessage(Player player, String path) {
            String locale = player.getLocale().toLowerCase();
            FileConfiguration config = configs.getOrDefault(locale, configs.get("en_us"));
            String message = config.getString(path);
            if (message == null) return "Missing: " + path;
            return ChatColor.translateAlternateColorCodes('&', message);
        }

        public void send(CommandSender sender, String path, String... replacements) {
            if (!(sender instanceof Player player)) {
                sender.sendMessage(path);
                return;
            }
            String message = getMessage(player, path);
            for (int i = 0; i < replacements.length; i += 2) {
                if (i + 1 < replacements.length) {
                    message = message.replace(replacements[i], replacements[i + 1]);
                }
            }
            player.sendMessage(message);
        }
    }
}