package de.jgaertig.sulfurFun.arena;

import de.jgaertig.sulfurFun.Mode;
import de.jgaertig.sulfurFun.SulfurFun;
import de.jgaertig.sulfurFun.game.GameManager;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class ArenaManager {

    private final JavaPlugin javaPlugin;
    private final Plugin plugin;
    private final SulfurFun.LanguageManager languageManager;
    private File arenasFile;
    private YamlConfiguration arenasConfig;

    private List<Arena> arenas;
    private GameManager gameManager;

    public ArenaManager(JavaPlugin plugin, SulfurFun.LanguageManager languageManager, GameManager gameManager) {
        this.javaPlugin = plugin;
        this.gameManager = gameManager;
        this.plugin = plugin;
        this.languageManager = languageManager;
        arenas = new ArrayList<>();
        loadArenasFromConfig();
    }

    public boolean autoJoin(Mode mode, Player player){

        Arena bestArena = null;
        Arena currentArena = null;

        for (Arena a : arenas){
            if (a.mode.equals(mode)&& a.state == ArenaState.WAITING){
                if  (bestArena == null){
                    bestArena = a;
                }else{
                    int player_number = a.players.size();
                    if (a.state == ArenaState.WAITING && player_number < a.maxPlayers && player_number > bestArena.players.size()){
                        bestArena = a;
                    }
                }
            }
            if (a.players.contains(player)){
                if (a.state == ArenaState.RUNNING){
                    languageManager.send(player, "already_in_game");
                    return false;
                }else{
                    currentArena = a;
                }
            }
        }

        if (bestArena == null){
            languageManager.send(player, "no_arena_available");
            return false;
        }

        if (bestArena.players.contains(player)){
            return false;
        }

        if (currentArena != null){
            currentArena.players.remove(player);
        }

        bestArena.players.add(player);
        if (bestArena.state != ArenaState.STARTING) checkStart(bestArena);
        return true;
    }

    private void checkStart(Arena arena) {
        if (arena.players.size() >= arena.minPlayers) {
            arena.state = ArenaState.STARTING;
            arena.startTimer();
        }
    }



    private void loadArenasFromConfig(){
        arenasFile = new File(javaPlugin.getDataFolder(), "arenas.yml");
        if (!arenasFile.exists()) {
            javaPlugin.saveResource("arenas.yml", false);
        }
        try (java.io.InputStreamReader reader = new java.io.InputStreamReader(
                new java.io.FileInputStream(arenasFile), java.nio.charset.StandardCharsets.UTF_8)) {

            arenasConfig = YamlConfiguration.loadConfiguration(reader);

        } catch (java.io.IOException e){
        }

        if (arenasConfig == null) return;
        for (String key : arenasConfig.getKeys(false)) {

            String modeString = arenasConfig.getString(key + ".mode");
            Mode mode = SulfurFun.modeMap.get(modeString);

            switch (mode) {
                case FOOTBALL:
                    // name
                    String name = arenasConfig.getString(key + ".name");
                    // players
                    int minPlayers = arenasConfig.getInt(key + ".minPlayers");
                    int maxPlayers = arenasConfig.getInt(key + ".maxPlayers");
                    // goals
                    Goal goal1 = readGoal(key + ".goal1");
                    Goal goal2 = readGoal(key + ".goal2");

                    // player + ball spawn points
                    SpawnPoint spawnPoint1 = readSpawnPoint(key + ".spawnPoint1");
                    SpawnPoint spawnPoint2 = readSpawnPoint(key + ".spawnPoint2");
                    SpawnPoint spawnPointBall = readSpawnPoint(key + ".spawnPointBall");


                    BallArena arena = new BallArena(
                            name,
                            mode,
                            minPlayers,
                            maxPlayers,
                            goal1,
                            goal2,
                            spawnPoint1,
                            spawnPoint2,
                            spawnPointBall,
                            gameManager,
                            plugin,
                            languageManager
                    );

                    arenas.add(arena);

                case HOCKEY:
                    // hockey
            }
        }
    }

    private SpawnPoint readSpawnPoint(String path) {
        double x = arenasConfig.getDouble(path + ".x");
        double y = arenasConfig.getDouble(path + ".y");
        double z = arenasConfig.getDouble(path + ".z");

        return new SpawnPoint(x,y,z);
    }
    private Goal readGoal(String path) {
        double c1x = arenasConfig.getDouble(path + ".corner_1_x");
        double c1y = arenasConfig.getDouble(path + ".corner_1_y");
        double c1z = arenasConfig.getDouble(path + ".corner_1_z");

        double c2x = arenasConfig.getDouble(path + ".corner_2_x");
        double c2y = arenasConfig.getDouble(path + ".corner_2_y");
        double c2z = arenasConfig.getDouble(path + ".corner_2_z");

        return new Goal(c1x, c1y, c1z, c2x, c2y, c2z);
    }
}
