package de.jgaertig.sulfurFun.commands;

import de.jgaertig.sulfurFun.SulfurFun;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jspecify.annotations.NonNull;

public class GameCommand implements CommandExecutor {

    protected Plugin plugin;
    protected SulfurFun.LanguageManager languageManager;

    public GameCommand(Plugin plugin, SulfurFun.LanguageManager languageManager) {
        this.plugin = plugin;
        this.languageManager = languageManager;
    }

    @Override
    public boolean onCommand(@NonNull CommandSender sender, @NonNull Command cmd, @NonNull String label, String[] args){
        if(!(sender instanceof Player player)){
            languageManager.send(sender, "for_players_only");
            return false;
        }

        return command(player, args);
    }

    public boolean command(Player player, String[] args){
        // to override!
        return false;
    }
}
