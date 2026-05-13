package de.jgaertig.sulfurFun.commands;

import de.jgaertig.sulfurFun.SulfurFun;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class JoinGameCommand implements CommandExecutor {

    private Plugin plugin;
    private SulfurFun.LanguageManager languageManager;

    public JoinGameCommand(Plugin plugin, SulfurFun.LanguageManager languageManager) {
        this.plugin = plugin;
        this.languageManager = languageManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
        if(!(sender instanceof Player)){
            languageManager.send(sender, "messages.forplayersonly");
            return false;
        }

        Player player = (Player) sender;
        if (args.length == 0){
            join(player);
            return true;
        }else{

        }
        return true;
    }

    public void join(Player player){}
}
