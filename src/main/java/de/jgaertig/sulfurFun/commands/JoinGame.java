package de.jgaertig.sulfurFun.commands;

import de.jgaertig.sulfurFun.SulfurFun;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.List;

public class JoinGame implements CommandExecutor {

     private final SulfurFun plugin;
     // Der Manager für die Sprachen 🌍
     private final SulfurFun.LanguageManager languageManager;

     // Konstruktor angepasst
     public JoinGame(SulfurFun plugin, SulfurFun.LanguageManager languageManager) {
          this.plugin = plugin;
          this.languageManager = languageManager;
     }

     @Override
     public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

          if (!(sender instanceof Player)) {
               languageManager.send(sender, "messages.joingame.forplayersonly");
               return false;
          }

          Player player = (Player) sender;

          if (args.length < 1 || args.length > 2) {
               languageManager.send(player, "messages.joingame.usage");
               return false;
          }

          String type = args[0].toLowerCase();
          String foundArena = null;
          List<String> candidates = new ArrayList<>();

          if (args.length == 2) {
               // Fall A: Name wurde mitgegeben
               String name = args[1];
               if (plugin.getArenaConfig().contains(name)) {
                    String arenaType = plugin.getArenaConfig().getString(name + ".type");
                    if (type.equalsIgnoreCase(arenaType)) {
                         foundArena = name;
                    }
               } else {
                    languageManager.send(player, "messages.joingame.notexist");
                    return true;
               }
          } else {
               // Fall B: Nur Typ wurde mitgegeben (Deine Schleife!)
               for (String arenaName : plugin.getArenaConfig().getKeys(false)) {
                    String currentType = plugin.getArenaConfig().getString(arenaName + ".type");
                    if (currentType != null && currentType.equalsIgnoreCase(type)) {
                         candidates.add(arenaName);
                    }
               }

               if (candidates.isEmpty()) {
                    languageManager.send(player, "messages.joingame.notfound");
               } else if (candidates.size() == 1) {
                    foundArena = candidates.get(0);
               } else {
                    // Hier muss jetzt das auswahl verfahren kommen
               }
          }



          return true;
     }
}
