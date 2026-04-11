package de.jgaertig.sulfurFun.listeners;

import de.jgaertig.sulfurFun.commands.NewGame;
import de.jgaertig.sulfurFun.models.SetupSession;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SetupListener implements Listener {

    // Die Map verknüpft die UUID des Spielers mit seiner Session
    private final Map<UUID, SetupSession> sessions = new HashMap<>();

    // Diese Methode rufen wir später aus dem Command auf
    public void addPlayer(UUID uuid, String arenaName) {
        sessions.put(uuid, new SetupSession(arenaName));
    }

    private NewGame newGameCommand;

    // Konstruktor: Hier bekommt der Listener den Command zugewiesen
    public SetupListener(NewGame newGameCommand) {
        this.newGameCommand = newGameCommand;
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        // Prüfen, ob es ein Rechtsklick auf einen Block war
        if (event.getHand() != org.bukkit.inventory.EquipmentSlot.HAND) return;
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;

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
        // Wir schauen uns jeden Eintrag in der Map an
        // Wir nutzen "removeIf", um alle zu entfernen, die zur Arena passen
        sessions.entrySet().removeIf(entry -> {
            boolean match = entry.getValue().getArenaName().equalsIgnoreCase(arenaName);
            if (match) {
                // Optional: Dem Spieler eine Nachricht schicken, dass abgebrochen wurde
                org.bukkit.Bukkit.getPlayer(entry.getKey()).sendMessage(
                        org.bukkit.ChatColor.RED + "The setup was cancelled because the arena was deleted."
                );
            }
            return match;
        });
    }

}
