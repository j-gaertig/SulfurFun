package de.jgaertig.sulfurFun.arena;

import de.jgaertig.sulfurFun.Mode;
import de.jgaertig.sulfurFun.SulfurFun;
import de.jgaertig.sulfurFun.game.GameManager;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public class Arena {
    String name;
    Mode mode;
    ArenaState state;
    int minPlayers;
    int maxPlayers;
    List<Player> players;
    GameManager gameManager;
    BukkitRunnable countdownTask;
    int countdownSeconds;
    Plugin plugin;
    SulfurFun.LanguageManager languageManager;

    public Arena(String name, Mode mode, int minPLayers, int maxPLayers, GameManager gameManager, Plugin plugin, SulfurFun.LanguageManager languageManager) {
        this.name = name;
        this.mode = mode;
        this.minPlayers = minPLayers;
        this.maxPlayers = maxPLayers;
        this.players = new ArrayList<>();
        this.state = ArenaState.WAITING;
        this.gameManager = gameManager;
        countdownSeconds = plugin.getConfig().getInt("countdownSeconds");
        this.plugin = plugin;
        this.languageManager = languageManager;
    }

    public void startTimer() {
        if (state == ArenaState.STARTING || state == ArenaState.RUNNING) {
            return;
        }

        state = ArenaState.STARTING;

        countdownTask = new BukkitRunnable() {
            @Override
            public void run() {
                if (players.size() < minPlayers) {
                    cancelTimer();
                    state = ArenaState.WAITING;
                    for (Player p  : players) {
                        languageManager.send(p, "not_enough_players");
                    }
                    return;
                }

                if (countdownSeconds <= 0) {
                    cancel();
                    countdownTask = null;
                    state = ArenaState.RUNNING;
                    gameManager.startGame(Arena.this);
                    return;
                }
                for (Player p  : players) {
                    languageManager.send(p, "game_starting_in", "%seconds%",String.valueOf(countdownSeconds));
                }
                countdownSeconds--;
            }
        };

        countdownTask.runTaskTimer(plugin, 0L, 20L);
    }

    public void cancelTimer() {
        if (countdownTask != null) {
            countdownTask.cancel();
            countdownTask = null;
        }
    }

    private void broadcast(String message) {
        for (Player player : players) {
            player.sendMessage(message);
        }
    }
}
