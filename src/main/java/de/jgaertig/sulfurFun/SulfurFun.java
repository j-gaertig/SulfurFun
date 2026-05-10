package de.jgaertig.sulfurFun;

import de.jgaertig.sulfurFun.commands.DeleteGame;
import de.jgaertig.sulfurFun.commands.JoinGame;
import de.jgaertig.sulfurFun.commands.LeaveGame;
import de.jgaertig.sulfurFun.commands.NewGame;
import de.jgaertig.sulfurFun.listeners.SetupListener;
import de.jgaertig.sulfurFun.models.ArenaManager;
import de.jgaertig.sulfurFun.models.GameManager;
import de.jgaertig.sulfurFun.tasks.ActionbarTask;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class SulfurFun extends JavaPlugin {

    private File arenaFile;
    private FileConfiguration arenaConfig;
    private LanguageManager languageManager;
    private ArenaManager arenaManager;
    private GameManager gameManager;

    @Override
    public void onEnable() {
        setupConfiguration();
        // Manager zuerst initialisieren!
        this.languageManager = new LanguageManager(this);
        setupManagers();
        sendEnableMessage();
    }

    public LanguageManager getLanguageManager() {
        return languageManager;
    }

    @Override
    public void onDisable() {}

    private void setupConfiguration() {

        saveDefaultConfig();

        if (!getDataFolder().exists()) {
            getDataFolder().mkdirs();
        }
        arenaFile = new File(getDataFolder(), "arenas.yml");
        if (!arenaFile.exists()) {
            saveResource("arenas.yml", false);
        }
        arenaConfig = YamlConfiguration.loadConfiguration(arenaFile);
    }

    private void setupManagers() {

        this.arenaManager = new ArenaManager();
        this.gameManager = new GameManager(this, arenaManager);

        if (arenaConfig != null) {
            for (String arenaName : arenaConfig.getKeys(false)) {
                int max = arenaConfig.getInt(arenaName + ".maxplayer");
                arenaManager.loadArena(arenaName, max);
            }
        }
        // 1. Listener mit Manager erstellen
        SetupListener setupListener = new SetupListener(this.languageManager);
        ActionbarTask actionbarTask = new ActionbarTask(this, this.arenaManager, this.gameManager);

        // 2. Commands mit Manager erstellen
        NewGame newGameCommand = new NewGame(this, setupListener, this.languageManager);
        DeleteGame deleteGameCommand = new DeleteGame(this, setupListener, this.languageManager);
        JoinGame joinGameCommand = new JoinGame(this, this.languageManager, this.arenaManager, this.gameManager);
        LeaveGame leaveGame = new LeaveGame(this.arenaManager, this.languageManager);

        new ActionbarTask(this, this.arenaManager, this.gameManager).runTaskTimer(this, 20L, 20L);

        // 3. Verknüpfung setzen
        setupListener.setNewGameCommand(newGameCommand);

        // 4. Registrierung
        getCommand("newgame").setExecutor(newGameCommand);
        getCommand("newgame").setTabCompleter(newGameCommand);
        getCommand("deletegame").setExecutor(deleteGameCommand);
        getCommand("deletegame").setTabCompleter(deleteGameCommand);
        getCommand("joingame").setExecutor(joinGameCommand);
        getCommand("leavegame").setExecutor(leaveGame);

        getServer().getPluginManager().registerEvents(setupListener, this);
    }

    public FileConfiguration getArenaConfig() {
        return arenaConfig;
    }

    public void saveArenaConfig() {
        try {
            arenaConfig.save(arenaFile);
        } catch (IOException e) {
            getLogger().severe("Could not save arenas.yml!");
        }
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

    public class LanguageManager {
        private final JavaPlugin plugin;
        private final Map<String, FileConfiguration> configs = new HashMap<>();

        public LanguageManager(JavaPlugin plugin) {
            this.plugin = plugin;
            loadLanguages();
        }

        public void loadLanguages() {
            List<String> languages = Arrays.asList(
                    "en_us.yml", // Englisch (USA)
                    "de_de.yml", // Deutsch
                    "es_es.yml", // Spanisch (Spanien)
                    "fr_fr.yml", // Französisch (Frankreich)
                    "ru_ru.yml", // Russisch
                    "pt_br.yml", // Portugiesisch (Brasilien)
                    "zh_cn.yml", // Chinesisch (Vereinfacht)
                    "it_it.yml", // Italienisch
                    "pl_pl.yml", // Polnisch
                    "nl_nl.yml", // Niederländisch
                    "tr_tr.yml", // Türkisch
                    "ja_jp.yml"  // Japanisch
            );
            for (String lang : languages) {
                File file = new File(plugin.getDataFolder(), "languages/" + lang);
                if (!file.exists()) {
                    plugin.saveResource("languages/" + lang, false);
                }

                // Hier kommt die Änderung:
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