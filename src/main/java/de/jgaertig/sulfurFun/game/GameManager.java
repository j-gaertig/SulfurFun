package de.jgaertig.sulfurFun.game;

import de.jgaertig.sulfurFun.arena.Arena;
import org.bukkit.plugin.Plugin;

import static de.jgaertig.sulfurFun.SulfurFun.*;

public class GameManager {

    private final Plugin plugin;
    private final LanguageManager languageManager;

    public GameManager(Plugin plugin, LanguageManager languageManager){
        this.plugin = plugin;
        this.languageManager = languageManager;
    }

    public void startGame(Arena arena) {
        // Gamelogik

    }
}
