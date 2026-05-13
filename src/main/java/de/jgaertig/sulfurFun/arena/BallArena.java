package de.jgaertig.sulfurFun.arena;

import de.jgaertig.sulfurFun.Mode;
import de.jgaertig.sulfurFun.SulfurFun;
import de.jgaertig.sulfurFun.game.GameManager;
import org.bukkit.plugin.Plugin;

public class BallArena extends Arena {

    Goal goal1;
    Goal goal2;
    SpawnPoint spawnPoint1;
    SpawnPoint spawnPoint2;
    SpawnPoint spawnPointBall;

    public BallArena(String name, Mode mode, int minPLayers, int maxPLayers, Goal goal1, Goal goal2, SpawnPoint spawnPoint1, SpawnPoint spawnPoint2, SpawnPoint spawnPointBall, GameManager gameManager, Plugin plugin, SulfurFun.LanguageManager languageManager) {
        super(name, mode, minPLayers, maxPLayers, gameManager, plugin, languageManager);
        this.goal1 = goal1;
        this.goal2 = goal2;

        this.spawnPoint1 = spawnPoint1;
        this.spawnPoint2 = spawnPoint2;
        this.spawnPointBall = spawnPointBall;
    }

}
