package de.jgaertig.sulfurFun.commands;

import de.jgaertig.sulfurFun.Mode;
import de.jgaertig.sulfurFun.SulfurFun;
import de.jgaertig.sulfurFun.arena.ArenaManager;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class FootballCommand extends GameCommand {
    ArenaManager arenaManager;
    public FootballCommand(Plugin plugin, SulfurFun.LanguageManager languageManager, ArenaManager arenaManager){
        super(plugin, languageManager);
        this.arenaManager = arenaManager;
    }
    public boolean command(Player player, String[] args){

        if (args.length == 0){
            return arenaManager.autoJoin(Mode.FOOTBALL, player);
        }else{

        }
        return false;
    }
}
