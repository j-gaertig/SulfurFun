package de.jgaertig.sulfurFun.arena.setup;

import de.jgaertig.sulfurFun.Mode;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static de.jgaertig.sulfurFun.Mode.FOOTBALL;
import static de.jgaertig.sulfurFun.Mode.HOCKEY;

public class SetupManager {
    private final Map<UUID, ArenaSetup> activeSetups = new HashMap<>();

    public ArenaSetup getActiveSetup(UUID uuid) {
        return activeSetups.get(uuid);
    }

    public void removeSetup(UUID uuid) {
        activeSetups.remove(uuid);
    }

    public void startSetup(Player player, Mode mode, String arenaId) {
        ArenaSetup setup = switch(mode) {
            case FOOTBALL -> new FootballSetup(player, arenaId);
            case HOCKEY -> null; // Später hinzufügen
        };

        if(setup != null) {
            activeSetups.put(player.getUniqueId(), setup);
            player.sendMessage("§e[Setup] " + setup.getSetupMessages().get(0));
        }
    }
}
