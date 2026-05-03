package de.jgaertig.sulfurFun.tasks;

import de.jgaertig.sulfurFun.SulfurFun;
import de.jgaertig.sulfurFun.models.ArenaManager;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class ActionbarTask extends BukkitRunnable {

    private final SulfurFun plugin;
    private final ArenaManager arenaManager;

    public ActionbarTask(SulfurFun plugin, ArenaManager arenaManager) {
        this.plugin = plugin;
        this.arenaManager = arenaManager;
    }

    @Override
    public void run() {
        // Prüfen, ob die Actionbar in der Config überhaupt aktiviert ist
        if (!plugin.getConfig().getBoolean("actionBar.enabled")) return;

        String configMessage = plugin.getConfig().getString("actionBar.message");

        for (Player player : Bukkit.getOnlinePlayers()) {
            String arenaName = arenaManager.getPlayerArena(player.getUniqueId());

            // Nur Spieler in einer Arena erhalten die Actionbar
            if (arenaName != null) {
                String formattedMessage = formatMessage(player, arenaName, configMessage);
                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(formattedMessage));
            }
        }
    }

    private String formatMessage(Player player, String arenaName, String message) {
        // 1. Daten holen
        int pos = arenaManager.getPosition(player.getUniqueId(), arenaName);
        int size = arenaManager.getQueueSize(arenaName);
        int max = arenaManager.getMaxPlayers(arenaName) * 2;

        // Typ aus der arenas.yml holen (da der Manager ihn nicht im RAM hat)
        String type = plugin.getArenaConfig().getString(arenaName + ".type", "Game");

        String smartStatus;
        String posDisplay;

        // 3. Prüfung auf Error 1
        if (pos == -1) {
            smartStatus = "§c§oError 1";
            posDisplay = "§c§oError 1";
        } else {
            // 4. Normale Smart-Status Logik
            if (pos <= max) {
                smartStatus = size + "/" + max;
                posDisplay = String.valueOf(pos);
            } else {
                // Spieler ist weiter hinten (z.B. Pos 5 bei max 4 -> Warteplatz 1)
                smartStatus = "Warteplatz: " + (pos - max);
                posDisplay = String.valueOf(pos);
            }
        }

        // 3. Ersetzen
        return message
                .replace("%type%", type)
                .replace("%name%", arenaName)
                .replace("%position%", posDisplay)
                .replace("%maxplayer%", String.valueOf(max))
                .replace("%players%", String.valueOf(size))
                .replace("%smartstatus%", smartStatus)
                .replace("&", "§"); // Farben aktivieren!
    }
}