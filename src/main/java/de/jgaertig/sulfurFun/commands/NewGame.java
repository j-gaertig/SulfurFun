package de.jgaertig.sulfurFun.commands;

import de.jgaertig.sulfurFun.SulfurFun;
import de.jgaertig.sulfurFun.listeners.SetupListener;
import de.jgaertig.sulfurFun.models.FootballSetup;
import de.jgaertig.sulfurFun.models.GameSetup;
import de.jgaertig.sulfurFun.models.SetupSession;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class NewGame implements CommandExecutor, TabCompleter {

    private final SulfurFun plugin;
    private final Map<String, GameSetup> availableGames = new HashMap<>();
    private SetupListener setupListener;
    // Der Manager für die Sprachen 🌍
    private final SulfurFun.LanguageManager languageManager;

    // Konstruktor angepasst
    public NewGame(SulfurFun plugin, SetupListener setupListener, SulfurFun.LanguageManager languageManager) {
        this.plugin = plugin;
        this.setupListener = setupListener;
        this.languageManager = languageManager;

        availableGames.put("football", new FootballSetup());
    }

    public SulfurFun getPlugin() {
        return this.plugin;
    }

    public void setSetupListener(SetupListener setupListener) {
        this.setupListener = setupListener;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            // Konsole bekommt eine direkte Nachricht, da sie keine Locale hat
            languageManager.send(sender, "messages.newgame.forplayersonly");
            return false;
        }

        Player player = (Player) sender;

        if (args.length != 2) {
            languageManager.send(player, "messages.newgame.usage");
            return false;
        }

        String type = args[0].toLowerCase();
        String name = args[1];

        if (plugin.getArenaConfig().contains(name)) {
            // Nutzt Platzhalter für den Namen
            languageManager.send(player, "messages.newgame.alreadyexists", "%name%", name);
            return true;
        }

        if (availableGames.containsKey(type)) {
            GameSetup selectedSetup = availableGames.get(type);

            setupListener.addPlayer(player.getUniqueId(), name, selectedSetup);
            plugin.getArenaConfig().set(name + ".type", type);

            SetupSession session = setupListener.getSession(player.getUniqueId());
            askNextStep(player, session);

            plugin.saveArenaConfig();
        } else {
            // Nutzt Platzhalter für den Typ
            languageManager.send(player, "messages.newgame.invalidtype", "%type%", type);
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 1) {
            return List.copyOf(availableGames.keySet());
        }

        if (args.length == 2) {
            // Tab-Vorschlag wird hier nicht übersetzt, da es ein technischer Hinweis ist
            return List.of("<name_of_arena>");
        }

        return List.of();
    }

    public void handleSetupStep(Player player, Location loc) {
        saveToConfig(player, loc);
        finishStep(player);
    }

    public void handleSetupStep(Player player, Object value) {
        saveToConfig(player, value);
        finishStep(player);
    }

    private void askNextStep(Player player, SetupSession session) {
        int index = session.getStep() - 1;
        GameSetup setup = session.getGameSetup();

        if (index < setup.getMessages().length) {
            String stepPath = setup.getMessages()[index];
            // Wir kombinieren den "Next Step" Präfix mit der eigentlichen Anweisung
            String nextStepText = languageManager.getMessage(player, "messages.newgame.nextstep");
            String instruction = languageManager.getMessage(player, stepPath);

            player.sendMessage(ChatColor.BLUE + nextStepText + ChatColor.WHITE + instruction);
        } else {
            languageManager.send(player, "messages.newgame.finished");
            setupListener.removePlayer(player.getUniqueId());
        }
    }

    private void saveToConfig(Player player, Object value) {
        SetupSession session = setupListener.getSession(player.getUniqueId());
        String stepName = session.getGameSetup().getSteps()[session.getStep() - 1];
        String path = session.getArenaName() + "." + stepName;

        if (value instanceof Location) {
            Location loc = (Location) value;
            plugin.getArenaConfig().set(path + ".world", loc.getWorld().getName());
            plugin.getArenaConfig().set(path + ".x", loc.getBlockX());
            plugin.getArenaConfig().set(path + ".y", loc.getBlockY());
            plugin.getArenaConfig().set(path + ".z", loc.getBlockZ());
        } else {
            plugin.getArenaConfig().set(path, value);
        }

        plugin.saveArenaConfig();
    }

    private void finishStep(Player player) {
        SetupSession session = setupListener.getSession(player.getUniqueId());
        session.nextStep();
        askNextStep(player, session);
    }
}