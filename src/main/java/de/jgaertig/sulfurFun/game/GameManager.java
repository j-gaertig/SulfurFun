package de.jgaertig.sulfurFun.game;

import de.jgaertig.sulfurFun.SulfurFun;
import org.bukkit.plugin.Plugin;

public class GameManager {

    private Plugin plugin;
    private SulfurFun.LanguageManager languageManager;

    public GameManager(Plugin plugin, SulfurFun.LanguageManager languageManager){
        this.plugin = plugin;
        this.languageManager = languageManager;
    }
}
