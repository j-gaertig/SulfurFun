package de.jgaertig.sulfurFun.commands;

import de.jgaertig.sulfurFun.SulfurFun;
import de.jgaertig.sulfurFun.models.ArenaManager;
import de.jgaertig.sulfurFun.models.GameManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class JoinGame implements CommandExecutor {

     private final SulfurFun plugin;
     // Der Manager für die Sprachen 🌍
     private final SulfurFun.LanguageManager languageManager;
     private final ArenaManager arenaManager;
     private final GameManager gameManager;

     // Konstruktor angepasst
     public JoinGame(SulfurFun plugin, SulfurFun.LanguageManager languageManager, ArenaManager arenaManager, GameManager gameManager) {
          this.plugin = plugin;
          this.languageManager = languageManager;
          this.arenaManager = arenaManager;
          this.gameManager = gameManager;
     }

     @Override
     public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

          if (!(sender instanceof Player)) {
               languageManager.send(sender, "messages.joingame.forplayersonly");
               return true;
          }

          Player player = (Player) sender;
          UUID uuid = player.getUniqueId();

          if (arenaManager.isAlreadyInGame(uuid) || gameManager.isPlayerInGame(uuid)) {
               languageManager.send(player, "messages.joingame.alreadyplaying");
               return true;
          }

          if (args.length < 1 || args.length > 2) {
               languageManager.send(player, "messages.joingame.usage");
               return true;
          }

          String type = args[0].toLowerCase();
          String foundArena = null;
          List<String> candidates = new ArrayList<>();
          List<String> emptycandidates = new ArrayList<>();
          List<String> randomcandidates = new ArrayList<>();

          if (args.length == 2) {
               // Fall A: Name wurde mitgegeben
               String name = args[1];
               if (plugin.getArenaConfig().contains(name)) {
                    String arenaType = plugin.getArenaConfig().getString(name + ".type");
                    if (type.equalsIgnoreCase(arenaType)) {
                         foundArena = name;
                         arenaManager.addToQueue(foundArena, uuid);
                         gameManager.tryToStartGame(foundArena);
                    }

               } else {
                    languageManager.send(player, "messages.joingame.notexist");
                    return true;
               }
          } else {

               if (!plugin.getConfig().getBoolean("randomJoinGameCommandEnabled")) return true;

               for (String arenaName : plugin.getArenaConfig().getKeys(false)) {
                    String currentType = plugin.getArenaConfig().getString(arenaName + ".type");
                    if (currentType != null && currentType.equalsIgnoreCase(type)) {
                         candidates.add(arenaName);
                    }
               }

               String mode = plugin.getConfig().getString("random-join-mode", "SMART");

               if (mode.equalsIgnoreCase("SMART")) {
                    if (candidates.isEmpty()) {
                         languageManager.send(player, "messages.joingame.notfound");
                    } else if (candidates.size() == 1) {
                         foundArena = candidates.get(0);
                         arenaManager.addToQueue(foundArena, uuid);
                         gameManager.tryToStartGame(foundArena);
                    } else {
                         for (String arenaName : candidates) {
                              if (arenaManager.getQueueSize(arenaName) < arenaManager.getMaxPlayers(arenaName) * 2 && arenaManager.getQueueSize(arenaName) > 0) {
                                   foundArena = arenaName;
                                   arenaManager.addToQueue(foundArena, uuid);
                                   gameManager.tryToStartGame(foundArena);
                                   return true;
                              } else if (arenaManager.getQueueSize(arenaName) == 0) {
                                   emptycandidates.add(arenaName);
                              }
                         }
                         if (foundArena == null && !emptycandidates.isEmpty()) {
                              Random random = new Random();
                              int randomarena = random.nextInt(emptycandidates.size());
                              foundArena = emptycandidates.get(randomarena);
                              arenaManager.addToQueue(foundArena, uuid);
                              gameManager.tryToStartGame(foundArena);
                              return true;

                         } else if (foundArena == null) {
                              Random random = new Random();
                              int randomarena = random.nextInt(candidates.size());
                              foundArena = candidates.get(randomarena);
                              arenaManager.addToQueue(foundArena, uuid);
                              gameManager.tryToStartGame(foundArena);
                              return true;

                         }
                    }
               } else {
                    if (candidates.isEmpty()) {
                         languageManager.send(player, "messages.joingame.notfound");
                    } else {
                         // REINER ZUFALL
                         Random random = new Random();
                         foundArena = candidates.get(random.nextInt(candidates.size()));
                         arenaManager.addToQueue(foundArena, uuid);
                         gameManager.tryToStartGame(foundArena);
                    }
               }
          }
          return true;
     }
}
