package de.jgaertig.sulfurFun.commands;

import de.jgaertig.sulfurFun.Mode;
import de.jgaertig.sulfurFun.SulfurFun;
import de.jgaertig.sulfurFun.arena.ArenaManager;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class JoinFootballCommand extends JoinGameCommand{
    ArenaManager arenaManager;
    public JoinFootballCommand(Plugin plugin, SulfurFun.LanguageManager languageManager, ArenaManager arenaManager){
        super(plugin, languageManager);
        this.arenaManager = arenaManager;
    }
    public void join(Player player){
        arenaManager.joinArena(Mode.FOOTBALL, player);
    }
}
