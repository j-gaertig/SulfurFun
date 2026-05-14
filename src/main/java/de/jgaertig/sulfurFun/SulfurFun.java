package de.jgaertig.sulfurFun;

import de.jgaertig.sulfurFun.arena.ArenaManager;
import de.jgaertig.sulfurFun.arena.setup.SetupListener;
import de.jgaertig.sulfurFun.arena.setup.SetupManager;
import de.jgaertig.sulfurFun.commands.FootballCommand;
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
import java.util.*;

public final class SulfurFun extends JavaPlugin {

    LanguageManager languageManager;
    GameManager gameManager;
    ArenaManager arenaManager;
    SetupManager setupManager;


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
        this.gameManager = new GameManager(this, languageManager);
        this.arenaManager = new ArenaManager(this, languageManager, gameManager);
        this.setupManager = new SetupManager();

    }

    public void registerCommandsAndListeners(){
        Objects.requireNonNull(getCommand("football")).setExecutor(new FootballCommand(this, languageManager, arenaManager));
        Objects.requireNonNull(getCommand("newarena")).setExecutor(new NewArenaCommand(this, languageManager, setupManager));

        getServer().getPluginManager().registerEvents(new SetupListener(setupManager), this);
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

    public GameManager getGameManager() {
        return gameManager;
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

    public boolean hasCommandPermission(CommandSender sender, String command) {
        if (!(sender instanceof Player)) {
            return true;
        }

        String perm = getConfig().getString("permissions_for_commands." + command, "op");

        if (perm.equalsIgnoreCase("op")) return sender.isOp();
        if (perm.equalsIgnoreCase("true")) return true;
        if (perm.equalsIgnoreCase("false")) return false;

        return sender.hasPermission(perm);
    }
}