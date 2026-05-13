package de.jgaertig.sulfurFun.commands;

import de.jgaertig.sulfurFun.SulfurFun;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class GameCommand implements CommandExecutor {

    private Plugin plugin;
    private SulfurFun.LanguageManager languageManager;

    public GameCommand(Plugin plugin, SulfurFun.LanguageManager languageManager) {
        this.plugin = plugin;
        this.languageManager = languageManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
        if(!(sender instanceof Player)){
            languageManager.send(sender, "for_players_only");
            return false;
        }
        Player player = (Player) sender;
        return command(player, args);
    }

    public boolean command(Player player, String[] args){
        // to override!
        return false;
    }
}
