package de.jgaertig.sulfurFun.commands;

import de.jgaertig.sulfurFun.SulfurFun;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class NewArenaCommand implements CommandExecutor {
    private Plugin plugin;
    private SulfurFun.LanguageManager languageManager;

    public NewArenaCommand(Plugin plugin, SulfurFun.LanguageManager languageManager) {
        this.plugin = plugin;
        this.languageManager = languageManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player)) {
            languageManager.send(sender, "messages.forplayersonly");
            return false;
        }

        return false;
    }
}
