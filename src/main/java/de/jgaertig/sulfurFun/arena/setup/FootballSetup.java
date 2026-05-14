package de.jgaertig.sulfurFun.arena.setup;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public class FootballSetup extends ArenaSetup {
    private Location goal1_1, goal1_2, goal2_1, goal2_2, spawn1, spawn2, ballSpawn; // usw.

    public FootballSetup(Player player, String arenaId) {
        super(player, arenaId);
    }

    @Override
    public List<String> getSetupMessages() {
        return List.of(
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
        String name;
        int minPlayers;
        int maxPlayers;
        switch (step) {
            case 0 -> name = (String) value;
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
    }
}
