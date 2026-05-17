package de.jgaertig.sulfurFun.arena.setup;

import de.jgaertig.sulfurFun.SulfurFun;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.block.Action;
import org.bukkit.plugin.java.JavaPlugin;

public class SetupListener implements Listener {
    private final SetupManager setupManager;

    public SetupListener(SetupManager setupManager) {
        this.setupManager = setupManager;
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        ArenaSetup setup = setupManager.getActiveSetup(event.getPlayer().getUniqueId());
        if (setup == null) return;

        if (event.getHand() != org.bukkit.inventory.EquipmentSlot.HAND) return;

        if (event.getAction() == Action.RIGHT_CLICK_BLOCK && event.getClickedBlock() != null) {
            event.setCancelled(true);
            setup.setStepValue(setup.getCurrentStep(), event.getClickedBlock().getLocation());

            if (!setup.next()) {
                setupManager.removeSetup(event.getPlayer().getUniqueId());
            }
        }
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        ArenaSetup setup = setupManager.getActiveSetup(event.getPlayer().getUniqueId());
        if (setup == null) return;

        event.setCancelled(true);

        Bukkit.getScheduler().runTask(JavaPlugin.getPlugin(SulfurFun.class), () -> {
            setup.setStepValue(setup.getCurrentStep(), event.getMessage());
            if (!setup.next()) {
                setupManager.removeSetup(event.getPlayer().getUniqueId());
            }
        });
    }
}