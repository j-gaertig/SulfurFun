package de.jgaertig.sulfurFun.arena;

import de.jgaertig.sulfurFun.Mode;

public class Arena {
    String name;
    Mode mode;
    ArenaState state;
    int minPLayers;
    int maxPLayers;

    public Arena(String name, Mode mode, int minPLayers, int maxPLayers) {
        this.name = name;
        this.mode = mode;
        this.minPLayers = minPLayers;
        this.maxPLayers = maxPLayers;
        this.state = ArenaState.WAITING;
    }
}
