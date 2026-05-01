package de.jgaertig.sulfurFun.listeners;

import de.jgaertig.sulfurFun.SulfurFun;
import de.jgaertig.sulfurFun.commands.NewGame;
import de.jgaertig.sulfurFun.models.GameSetup;
import de.jgaertig.sulfurFun.models.SetupSession;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SetupListener implements Listener {

    private NewGame newGameCommand;
    private final Map<UUID, SetupSession> sessions = new HashMap<>();
    // Der Manager für die Sprachen 🌍
    private final SulfurFun.LanguageManager languageManager;

    // Konstruktor mit LanguageManager
    public SetupListener(SulfurFun.LanguageManager languageManager) {
        this.languageManager = languageManager;
    }

    public void setNewGameCommand(NewGame newGameCommand) {
        this.newGameCommand = newGameCommand;
    }

    public void addPlayer(UUID uuid, String arenaName, GameSetup gameSetup) {
        sessions.put(uuid, new SetupSession(arenaName, gameSetup));
    }

    public void removePlayer(UUID uuid) {
        sessions.remove(uuid);
    }

    public SetupSession getSession(UUID uuid) {
        return sessions.get(uuid);
    }

    public void stopSessionsForArena(String arenaName) {
        sessions.entrySet().removeIf(entry -> {
            boolean match = entry.getValue().getArenaName().equalsIgnoreCase(arenaName);
            if (match) {
                Player p = Bukkit.getPlayer(entry.getKey());
                if (p != null) {
                    // Übersetzung nutzen 💬
                    languageManager.send(p, "messages.deletegame.cancelsetup");
                }
            }
            return match;
        });
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (event.getHand() != org.bukkit.inventory.EquipmentSlot.HAND) return;
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;

        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();

        if (!sessions.containsKey(uuid)) return;

        event.setCancelled(true);
        SetupSession session = sessions.get(uuid);
        int currentIndex = session.getStep() - 1;
        GameSetup.StepType currentType = session.getGameSetup().getStepTypes()[currentIndex];

        if (currentType == GameSetup.StepType.CLICK) {
            Block block = event.getClickedBlock();
            if (block != null) {
                newGameCommand.handleSetupStep(player, block.getLocation());
            }
        } else {
            // Übersetzung nutzen 💬
            languageManager.send(player, "messages.newgame.chatinsteadclicking");
        }
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();

        if (!sessions.containsKey(uuid)) return;

        SetupSession session = sessions.get(uuid);
        int currentIndex = session.getStep() - 1;
        GameSetup.StepType currentType = session.getGameSetup().getStepTypes()[currentIndex];

        if (currentType == GameSetup.StepType.CHAT) {
            event.setCancelled(true);
            String message = event.getMessage();
            String pattern = session.getGameSetup().getInputPatterns()[currentIndex];

            if (pattern == null || message.matches(pattern)) {
                Object finalValue;
                if (pattern != null && pattern.contains("[0-9]")) {
                    finalValue = Integer.parseInt(message);
                } else {
                    finalValue = message;
                }

                Bukkit.getScheduler().runTask(newGameCommand.getPlugin(), () -> {
                    newGameCommand.handleSetupStep(player, finalValue);
                });
            } else {
                // Übersetzung nutzen 💬
                languageManager.send(player, "messages.newgame.invalidinput");
            }
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        removePlayer(event.getPlayer().getUniqueId());
    }
}