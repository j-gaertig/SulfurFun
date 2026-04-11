package de.jgaertig.sulfurFun;

import de.jgaertig.sulfurFun.commands.DeleteGame;
import de.jgaertig.sulfurFun.commands.NewGame;
import de.jgaertig.sulfurFun.listeners.SetupListener;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import java.io.File;
import java.io.IOException;

public final class SulfurFun extends JavaPlugin {

    private File arenaFile;
    private FileConfiguration arenaConfig;

    @Override
    public void onEnable() {
        File folder = getDataFolder();
        if (!folder.exists()) folder.mkdirs();

        // 1. Datei laden
        arenaFile = new File(getDataFolder(), "arenas.yml");
        if (!arenaFile.exists()) {
            saveResource("arenas.yml", false); // Falls du eine Standard-Datei im Jar hast
        }
        arenaConfig = YamlConfiguration.loadConfiguration(arenaFile);

        NewGame newGameCommand = new NewGame(this, null);

        SetupListener setupListener = new SetupListener(newGameCommand);

        newGameCommand.setSetupListener(setupListener);

        getCommand("newgame").setExecutor(newGameCommand);
        getCommand("newgame").setTabCompleter(newGameCommand);

        DeleteGame deleteGameCommand = new DeleteGame(this, setupListener);

        getCommand("deletegame").setExecutor(deleteGameCommand);
        getCommand("deletegame").setTabCompleter(deleteGameCommand);

        getServer().getPluginManager().registerEvents(setupListener, this);


        String gold = ChatColor.GOLD.toString();
        String yellow = ChatColor.YELLOW.toString();
        String green = ChatColor.GREEN.toString();
        String gray = ChatColor.GRAY.toString();

        Bukkit.getConsoleSender().sendMessage("");
        Bukkit.getConsoleSender().sendMessage(gold + ChatColor.BOLD + "      Sulfur");
        Bukkit.getConsoleSender().sendMessage(gold + "  .-----------.");
        Bukkit.getConsoleSender().sendMessage(gold + "  |    " + yellow + "FUN" + gold + "    |");
        Bukkit.getConsoleSender().sendMessage(gold + "  |           |");
        Bukkit.getConsoleSender().sendMessage(gold + "  |           |");
        Bukkit.getConsoleSender().sendMessage(gold + "  |  " + yellow + "☐     ☐" + gold + "  |");
        Bukkit.getConsoleSender().sendMessage(gold + "  '-----------'");
        Bukkit.getConsoleSender().sendMessage("");
        Bukkit.getConsoleSender().sendMessage(green + "Plugin loaded ...");
        Bukkit.getConsoleSender().sendMessage(gray + "Version: " + getDescription().getVersion());
        Bukkit.getConsoleSender().sendMessage("");
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    // Damit andere Klassen die Config lesen können 📖
    public FileConfiguration getArenaConfig() {
        return arenaConfig;
    }

    // Damit andere Klassen Änderungen festschreiben können 💾
    public void saveArenaConfig() {
        try {
            arenaConfig.save(arenaFile);
        } catch (IOException e) {
            getLogger().severe("Could not save arenas.yml!");
        }
    }
}