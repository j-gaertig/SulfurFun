package de.jgaertig.sulfurFun.arena;

import de.jgaertig.sulfurFun.Mode;
import de.jgaertig.sulfurFun.SulfurFun;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.List;


public class ArenaManager {

    private final JavaPlugin plugin;
    private final SulfurFun.LanguageManager languageManager;
    private File arenasFile;
    private YamlConfiguration arenasConfig;

    private List<Arena> arenas;

    public ArenaManager(JavaPlugin plugin, SulfurFun.LanguageManager languageManager){
        this.plugin = plugin;
        this.languageManager = languageManager;
        loadArenasFromConfig();
    }

    public void joinArena(Mode mode, Player player){
    }

    private void loadArenasFromConfig(){
        arenasFile = new File(plugin.getDataFolder(), "arenas.yml");
        if (!arenasFile.exists()) {
            plugin.saveResource("arenas.yml", false);
        }
        try (java.io.InputStreamReader reader = new java.io.InputStreamReader(
                new java.io.FileInputStream(arenasFile), java.nio.charset.StandardCharsets.UTF_8)) {

            arenasConfig = YamlConfiguration.loadConfiguration(reader);

        } catch (java.io.IOException e){
        }
    }
}
