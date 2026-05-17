package de.jgaertig.sulfurFun.arena.setup;

import de.jgaertig.sulfurFun.SulfurFun;
import de.jgaertig.sulfurFun.arena.ArenaManager;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class FootballSetup extends ArenaSetup {
    private Location goal1_1, goal1_2, goal2_1, goal2_2, spawn1, spawn2, ballSpawn; // usw.

    private String arenaName;
    private int minPlayers;
    private int maxPlayers;

    public FootballSetup(Player player, String arenaId) {
        super(player, arenaId);
    }

    @Override
    public List<String> getSetupMessages() {
        return List.of(
                "Gib den Anzeigenamen der Arena ein (Chat)",
                "Gib die minimale Spieleranzahl ein (Chat)",
                "Gib die maximale Spieleranzahl ein (Chat)",
                "Klicke auf die erste Ecke von Tor 1",
                "Klicke auf die diagole Ecke von Tor 1",
                "Klicke auf die erste Ecke von Tor 2",
                "Klicke auf die diagole Ecke von Tor 2",
                "Klicke auf den Spawnpoint für Team 1",
                "Klicke auf den Spawnpoint für Team 2",
                "Klicke auf den Ball-Spawnpoint"
        );
    }

    @Override
    public void setStepValue(int step, Object value) {
        switch (step) {
            case 0 -> arenaName = (String) value;
            case 1 -> minPlayers = Integer.parseInt((String) value);
            case 2 -> maxPlayers = Integer.parseInt((String) value);
            case 3 -> goal1_1 = (Location) value;
            case 4 -> goal1_2 = (Location) value;
            case 5 -> goal2_1 = (Location) value;
            case 6 -> goal2_2 = (Location) value;
            case 7 -> spawn1 = (Location) value;
            case 8 -> spawn2 = (Location) value;
            case 9 -> ballSpawn = (Location) value;
        }
    }

    @Override
    public void save(JavaPlugin plugin) {
        File file = new File(plugin.getDataFolder(), "arenas.yml");
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);

        String path = arenaId + ".";
        config.set(path + "mode", "football");
        config.set(path + "name", arenaName);
        config.set(path + "minPlayers", minPlayers);
        config.set(path + "maxPlayers", maxPlayers);

        saveLocation(config, path + "goal1.corner_1", goal1_1);
        saveLocation(config, path + "goal1.corner_2", goal1_2);
        saveLocation(config, path + "goal2.corner_1", goal2_1);
        saveLocation(config, path + "goal2.corner_2", goal2_2);
        saveLocation(config, path + "spawnPoint1", spawn1);
        saveLocation(config, path + "spawnPoint2", spawn2);
        saveLocation(config, path + "spawnPointBall", ballSpawn);

        try {
            config.save(file);

            if (plugin instanceof SulfurFun main) {
                main.getArenaManager().reload();
                player.sendMessage("§aArena erfolgreich geladen!");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveLocation(YamlConfiguration config, String path, Location loc) {
        config.set(path + "_x", loc.getX());
        config.set(path + "_y", loc.getY());
        config.set(path + "_z", loc.getZ());
    }
}
