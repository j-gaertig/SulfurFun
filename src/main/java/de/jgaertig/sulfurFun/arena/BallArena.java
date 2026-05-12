package de.jgaertig.sulfurFun.arena;

import de.jgaertig.sulfurFun.Mode;

public class BallArena extends Arena {

    Goal goal1;
    Goal goal2;
    SpawnPoint spawnPoint1;
    SpawnPoint spawnPoint2;
    SpawnPoint spawnPointBall;

    public BallArena(String name, Mode mode, int minPLayers, int maxPLayers,Goal goal1, Goal goal2, SpawnPoint spawnPoint1, SpawnPoint spawnPoint2,  SpawnPoint spawnPointBall) {
        super(name, mode, minPLayers, maxPLayers);
        this.goal1 = goal1;
        this.goal2 = goal2;

        this.spawnPoint1 = spawnPoint1;
        this.spawnPoint2 = spawnPoint2;
        this.spawnPointBall = spawnPointBall;
    }

}
