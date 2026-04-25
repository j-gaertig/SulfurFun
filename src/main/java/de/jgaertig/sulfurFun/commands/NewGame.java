package de.jgaertig.sulfurFun.commands;

import de.jgaertig.sulfurFun.SulfurFun;
import de.jgaertig.sulfurFun.listeners.SetupListener;
import de.jgaertig.sulfurFun.models.SetupSession;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class NewGame implements CommandExecutor, TabCompleter {

    private final SulfurFun plugin;

    public SulfurFun getPlugin() {
        return this.plugin;
    }

    // Die Reihenfolge, in der der Spieler die Punkte abläuft
    private final String[] steps = {
            "bluegoal1", "bluegoal2", "blueplayerspawn",
            "redgoal1", "redgoal2", "redplayerspawn",
            "ballspawn", "maxplayer"
    };

    // Nachrichten, die beim Fußball Arena Erstellen gebraucht werden
    private final String[] stepMessages = {
            "Right-click on the corner of the outer edge of the blue team's goal.", // bluegoal1
            "Right-click on the diagonally opposite corner of the blue team's goal.", // bluegoal2
            "Right-click on the spawn point where the blue team's players spawn.", // blueplayerspawn
            "Right-click on the corner of the outer edge of the red team's goal.", // redgoal1
            "Right-click on the diagonally opposite corner of the red team's goal.", // redgoal2
            "Right-click on the spawn point where the red team's players spawn.", // redplayerspawn
            "Right-click where the ball should spawn.", // ballspawn
            "Type the maximum amount of players into the chat." // maxplayers
    };

    private SetupListener setupListener;

    public NewGame(SulfurFun plugin, SetupListener setupListener) {
        this.plugin = plugin;
        this.setupListener = setupListener;
    }

    public void setSetupListener(SetupListener setupListener) {
        this.setupListener = setupListener;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // Prüfen ob Sender ein Spieler ist
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.YELLOW + "Warning: This Command is for players only!");
            return false;
        }
        // Prüfen, ob genügend Argumente im Command sind
        if (!(args.length == 2)) {
            sender.sendMessage(ChatColor.YELLOW + "Usage: /newgame <type_of_minigame> <name_of_your_arena>");
            return false;
        }

        // Schöne Strings und Variablen erstellen
        Player player = (Player) sender;
        String type = args[0];
        String name = args[1];
        String playerperteam = "";
        String playerdamage = "";

        if (plugin.getArenaConfig().contains(name)) {
            player.sendMessage(ChatColor.RED + "Warning: '" + name + "' already exists!");
            return true;
        }

        // welcher type -> dementsprechend eine Aktion
        if (type.equalsIgnoreCase("football")) {
            plugin.getArenaConfig().set(name + ".type", type);

            setupListener.addPlayer(player.getUniqueId(), name);

            SetupSession session = setupListener.getSession(player.getUniqueId());
            askNextStep(player, session);


            // Datei speichern
            plugin.saveArenaConfig();


        } else {
            player.sendMessage(ChatColor.YELLOW + "Unknown type of minigame");
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 1) {
            return List.of("football");
        }
        if (args.length == 2) {
            return List.of("<name_of_arena>");
        }
        return List.of();
    }

    public void handleSetupClick(Player player, Location loc) {

        SetupSession session = setupListener.getSession(player.getUniqueId());
        int currentStepIndex = session.getStep() - 1;

        if (currentStepIndex == 7) {
            return;

        }else {


            String stepName = steps[currentStepIndex];
            String path = session.getArenaName() + "." + stepName;

            plugin.getArenaConfig().set(path + ".world", loc.getWorld().getName());
            plugin.getArenaConfig().set(path + ".x", loc.getBlockX());
            plugin.getArenaConfig().set(path + ".y", loc.getBlockY());
            plugin.getArenaConfig().set(path + ".z", loc.getBlockZ());




        }

        // WICHTIG: Die Datei tatsächlich auf der Festplatte speichern
        plugin.saveArenaConfig();

        // player.sendMessage(ChatColor.GREEN + "Saved " + stepName + "!");

        session.nextStep();
        askNextStep(player, session);
    }

    private void askNextStep(Player player, SetupSession session){
        int index = session.getStep() - 1;

        if (index < stepMessages.length) {
            // Wir holen den passenden Satz aus unserem neuen Array
            String message = stepMessages[index];
            player.sendMessage(ChatColor.BLUE + "Next Step: " + ChatColor.WHITE + message);
        } else {
            // Wenn alle Koordinaten fertig sind
            player.sendMessage(ChatColor.GOLD + "All positions are saved.");
            // Hier könnten wir später die Session beenden
            setupListener.removePlayer(player.getUniqueId());
        }
    }

    public void handleMaxPlayersInput(Player player, int amount) {
        // 1. Hol dir die Session des Spielers vom setupListener
        SetupSession session = setupListener.getSession(player.getUniqueId());

        // 2. Den Pfad für die Config erstellen (z.B. "meineArena.maxplayers")
        String path = session.getArenaName() + ".maxplayers";

        // 3. Den Wert 'amount' in die Config setzen
        plugin.getArenaConfig().set(path, amount);

        // 4. Die Config dauerhaft auf der Festplatte speichern
        plugin.saveArenaConfig();

        // 5. Die Session zum nächsten Schritt bewegen und askNextStep aufrufen
        session.nextStep();
        askNextStep(player, session);
    }
}