package de.jgaertig.sulfurFun.listeners;

import de.jgaertig.sulfurFun.SulfurFun;
import de.jgaertig.sulfurFun.commands.NewGame;
import de.jgaertig.sulfurFun.models.SetupSession;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SetupListener implements Listener {

    private NewGame newGameCommand;

    public SetupListener() {}

    public void setNewGameCommand(NewGame newGameCommand) {
        this.newGameCommand = newGameCommand;
    }

    // Die Map verknüpft die UUID des Spielers mit seiner Session
    private final Map<UUID, SetupSession> sessions = new HashMap<>();

    // Diese Methode rufen wir später aus dem Command auf
    public void addPlayer(UUID uuid, String arenaName) {
        sessions.put(uuid, new SetupSession(arenaName));
    }


    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        // Prüfen, ob es ein Rechtsklick auf einen Block war
        if (event.getHand() != org.bukkit.inventory.EquipmentSlot.HAND) return;
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;

        event.setCancelled(true);

        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();

        // Hat der Spieler gerade ein Setup offen?
        if (!sessions.containsKey(uuid)) return;

        // Wenn ja, holen wir die Position des Blocks
        Block block = event.getClickedBlock();
        Location loc = block.getLocation();

        // Wir schicken die Daten an die Command-Klasse zurück
        // (Dafür muss der Listener die NewGame-Instanz kennen)
        newGameCommand.handleSetupClick(player, loc);


    }

    public SetupSession getSession(UUID uuid) {
        return sessions.get(uuid);
    }

    public void removePlayer(UUID uuid) {
        sessions.remove(uuid);
    }

    public void stopSessionsForArena(String arenaName) {
        // Wir gehen durch alle Einträge (sessions) durch
        sessions.entrySet().removeIf(entry -> {
            // 1. Prüfen, ob die Session zu der gelöschten Arena gehört
            boolean match = entry.getValue().getArenaName().equalsIgnoreCase(arenaName);

            if (match) {
                // 2. NUR wenn es ein Match ist, holen wir den Spieler
                Player p = org.bukkit.Bukkit.getPlayer(entry.getKey());
                if (p != null) {
                    p.sendMessage(org.bukkit.ChatColor.RED + "The setup was cancelled because the arena was deleted.");
                }
            }
            // 3. Wenn match 'true' ist, wird der Eintrag aus der Map gelöscht
            return match;
        });
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();

        // 1. Hat der Spieler eine aktive Session?
        if (!sessions.containsKey(uuid)) return;

        SetupSession session = sessions.get(uuid);

        // 2. Sind wir bei Schritt 8 (Index 7)?
        if (session.getStep() == 8) {
            // Wir brechen das Senden der Nachricht ab, damit sie nicht im globalen Chat erscheint
            event.setCancelled(true);

            String message = event.getMessage(); // Das ist der Text vom Spieler

            try {
                // Versuche, den Text in eine ganze Zahl (int) umzuwandeln
                int amount = Integer.parseInt(message);

                // WICHTIG: Da Chat-Events "Async" laufen, müssen wir für Bukkit-Aktionen
                // zurück auf den Haupt-Thread (wie ein Gleiswechsel beim Zug 🚂).
                Bukkit.getScheduler().runTask(newGameCommand.getPlugin(), () -> {
                    newGameCommand.handleMaxPlayersInput(player, amount);
                });

            } catch (NumberFormatException e) {
                // Wenn der Spieler keine Zahl eingegeben hat (z.B. "fünf" statt "5")
                player.sendMessage(ChatColor.RED + "Please enter a valid number!");
            }
        }
    }

    @EventHandler
    public void onPlayerQuit(org.bukkit.event.player.PlayerQuitEvent event) {
        // Wenn ein Spieler den Server verlässt, entfernen wir seine Session (falls vorhanden)
        removePlayer(event.getPlayer().getUniqueId());
    }

}
