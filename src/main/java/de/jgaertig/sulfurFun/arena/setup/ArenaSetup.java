package de.jgaertig.sulfurFun.arena.setup;

import de.jgaertig.sulfurFun.SulfurFun;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public abstract class ArenaSetup {
    protected final Player player;
    protected final String arenaId;
    protected int currentStep = 0;

    public ArenaSetup(Player player, String arenaId) {
        this.player = player;
        this.arenaId = arenaId;
    }

    public abstract List<String> getSetupMessages();
    public abstract void setStepValue(int step, Object value);
    public abstract void save(JavaPlugin plugin);

    public boolean next() {
        currentStep++;
        if (currentStep < getSetupMessages().size()) {
            player.sendMessage("§e[Setup] " + getSetupMessages().get(currentStep));
            return true; // Es geht weiter
        } else {
            player.sendMessage("§a[Setup] Fertig! Speichere Arena...");
            save(JavaPlugin.getPlugin(SulfurFun.class));
            return false; // Setup beendet
        }
    }

    public int getCurrentStep() {
        return currentStep;
    }

}
