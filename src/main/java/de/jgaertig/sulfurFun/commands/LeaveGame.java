package de.jgaertig.sulfurFun.commands;

import de.jgaertig.sulfurFun.SulfurFun;
import de.jgaertig.sulfurFun.models.ArenaManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class LeaveGame implements CommandExecutor {
    private final ArenaManager arenaManager;
    private final SulfurFun.LanguageManager languageManager;

    public LeaveGame(ArenaManager arenaManager, SulfurFun.LanguageManager languageManager) {
        this.arenaManager = arenaManager;
        this.languageManager = languageManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) return false;

        if (arenaManager.isAlreadyInGame(player.getUniqueId())) {
            arenaManager.removePlayer(player.getUniqueId());
            languageManager.send(player, "messages.leavegame.success");
        } else {
            languageManager.send(player, "messages.leavegame.notinqueue");
        }
        return true;
    }
}
