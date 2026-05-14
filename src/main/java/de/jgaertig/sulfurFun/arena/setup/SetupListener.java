package de.jgaertig.sulfurFun.arena.setup;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.block.Action;

public class SetupListener implements Listener {
    private final SetupManager setupManager;

    public SetupListener(SetupManager setupManager) {
        this.setupManager = setupManager;
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        ArenaSetup setup = setupManager.getActiveSetup(event.getPlayer().getUniqueId());
        if (setup == null) return;

        if (event.getAction() == Action.RIGHT_CLICK_BLOCK && event.getClickedBlock() != null) {
            event.setCancelled(true);
            setup.setStepValue(setup.currentStep, event.getClickedBlock().getLocation());

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

        setup.setStepValue(setup.currentStep, event.getMessage());

        if (!setup.next()) {
            setupManager.removeSetup(event.getPlayer().getUniqueId());
        }
    }
}