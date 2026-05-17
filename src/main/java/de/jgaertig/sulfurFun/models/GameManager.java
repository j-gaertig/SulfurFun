package de.jgaertig.sulfurFun.models;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.potion.PotionEffect;

import java.util.*;

public class GameManager {

    private final ArenaManager arenaManager;

    private final Map<UUID, ItemStack[]> savedInventories = new HashMap<>();
    private final Map<UUID, ItemStack[]> savedArmor = new HashMap<>();
    private final Map<UUID, Location> savedLocations = new HashMap<>();
    private final Map<UUID, Integer> savedLevels = new HashMap<>();
    private final Map<UUID, Float> savedExp = new HashMap<>();
    private final Map<UUID, Integer> savedFood = new HashMap<>();
    private final Map<UUID, String> playerTeams = new HashMap<>();
    private final Map<String, List<UUID>> activeArenaPlayers = new HashMap<>();

    public GameManager(ArenaManager arenaManager) {
        this.arenaManager = arenaManager;
    }

    public void tryToStartGame(String arenaName) {
        List<UUID> players = arenaManager.pollPlayersForStart(arenaName);
        if (!players.isEmpty()) {
            distributeTeams(players, arenaName);
            // Später: Teleport
        }
    }

    public void distributeTeams(List<UUID> players, String arenaName) {

        Collections.shuffle(players);

        // 1. Die Liste der aktiven Spieler für diese Arena speichern 📝
        activeArenaPlayers.put(arenaName, new ArrayList<>(players));

        for (int i = 0; i < players.size(); i++) {
            UUID uuid = players.get(i);
            Player player = Bukkit.getPlayer(uuid);

            if (player == null) continue;

            String team = (i % 2 == 0) ? "blue" : "red";

            // 3. Team-Zugehörigkeit in der Map speichern
            playerTeams.put(uuid, team);

            // 4. Spieler vorbereiten (Inventar, Helm etc.)
            preparePlayerForGame(player, team);
        }
    }

        public void preparePlayerForGame(Player player, String team) {

        savePlayerData(player);

        for (PotionEffect effect : player.getActivePotionEffects()) {
            player.removePotionEffect(effect.getType());
        }

        // Inventar und Rüstung komplett leeren
        player.getInventory().clear();
        player.getInventory().setArmorContents(new ItemStack[4]);

        // Gesundheit und Hunger auffüllen
        player.setHealth(20.0);
        player.setFoodLevel(20);

        // XP auf 0 setzen
        player.setLevel(0);
        player.setExp(0);

        setHelmet(team, player);

    }

    public void savePlayerData(Player player) {
        UUID uuid = player.getUniqueId();

        savedInventories.put(uuid, player.getInventory().getContents());
        savedArmor.put(uuid, player.getInventory().getArmorContents());
        savedLocations.put(uuid, player.getLocation());
        savedLevels.put(uuid, player.getLevel());
        savedExp.put(uuid, player.getExp());
        savedFood.put(uuid, player.getFoodLevel());
    }

    public void setHelmet (String team, Player player) {

        if (Objects.equals(team, "red")) {
            ItemStack redHelm = new ItemStack(Material.LEATHER_HELMET);
            LeatherArmorMeta meta = (LeatherArmorMeta) redHelm.getItemMeta();
            meta.setColor(Color.RED);
            meta.setDisplayName("§cTeam Rot");
            redHelm.setItemMeta(meta);

            player.getInventory().setHelmet(redHelm);

        }else if (Objects.equals(team, "blue")) {
            ItemStack blueHelm = new ItemStack(Material.LEATHER_HELMET);
            LeatherArmorMeta meta = (LeatherArmorMeta) blueHelm.getItemMeta();
            meta.setColor(Color.BLUE);
            meta.setDisplayName("§cTeam Blue");
            blueHelm.setItemMeta(meta);

            player.getInventory().setHelmet(blueHelm);
        }

    }

    public boolean isPlayerInGame(UUID uuid) {
        return playerTeams.containsKey(uuid);
    }

    public void removePlayerFromGame(UUID uuid, Player player) {

        for (PotionEffect effect : player.getActivePotionEffects()) {
            player.removePotionEffect(effect.getType());
        }

        if (savedInventories.containsKey(uuid)) {
            // Inventar zurücksetzen
            player.getInventory().setContents(savedInventories.get(uuid));
            player.getInventory().setArmorContents(savedArmor.get(uuid));

            player.setLevel(savedLevels.get(uuid));
            player.setExp(savedExp.get(uuid));
            player.setFoodLevel(savedFood.get(uuid));

            Location oldLoc = savedLocations.get(uuid);
            if (oldLoc != null) {
                player.teleport(oldLoc);
            }

            cleanupData(uuid);
        }
    }

    private void cleanupData(UUID uuid) {
        savedInventories.remove(uuid);
        savedArmor.remove(uuid);
        savedLocations.remove(uuid);
        savedLevels.remove(uuid);
        savedExp.remove(uuid);
        savedFood.remove(uuid);
        playerTeams.remove(uuid);

        // Wir müssen den Spieler auch aus der Liste der aktiven Arena-Spieler löschen
        for (List<UUID> arenaPlayers : activeArenaPlayers.values()) {
            arenaPlayers.remove(uuid);
        }
    }
}
