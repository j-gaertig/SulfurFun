package de.jgaertig.sulfurFun.models;

import de.jgaertig.sulfurFun.SulfurFun;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Slime;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.potion.PotionEffect;

import java.util.*;

public class GameManager {

    private final SulfurFun plugin;
    private final ArenaManager arenaManager;

    private final Map<UUID, ItemStack[]> savedInventories = new HashMap<>();
    private final Map<UUID, ItemStack[]> savedArmor = new HashMap<>();
    private final Map<UUID, Location> savedLocations = new HashMap<>();
    private final Map<UUID, Integer> savedLevels = new HashMap<>();
    private final Map<UUID, Float> savedExp = new HashMap<>();
    private final Map<UUID, Integer> savedFood = new HashMap<>();
    private final Map<UUID, String> playerTeams = new HashMap<>();
    private final Map<String, List<UUID>> activeArenaPlayers = new HashMap<>();

    public GameManager(SulfurFun plugin, ArenaManager arenaManager) {
        this.plugin = plugin;
        this.arenaManager = arenaManager;
    }

    public void tryToStartGame(String arenaName) {
        List<UUID> players = arenaManager.pollPlayersForStart(arenaName);
        if (!players.isEmpty()) {
            distributeTeams(players, arenaName);
            // Später: Teleport & Ballspawn
            spawnBall(arenaName);
        }
    }

    public void distributeTeams(List<UUID> players, String arenaName) {
        Collections.shuffle(players);
        activeArenaPlayers.put(arenaName, new ArrayList<>(players));

        for (int i = 0; i < players.size(); i++) {
            UUID uuid = players.get(i);
            Player player = Bukkit.getPlayer(uuid);
            if (player == null) continue;

            String team = (i % 2 == 0) ? "blue" : "red";
            playerTeams.put(uuid, team);
            preparePlayerForGame(player, team);
        }
    }

    public void preparePlayerForGame(Player player, String team) {
        savePlayerData(player);
        for (PotionEffect effect : player.getActivePotionEffects()) {
            player.removePotionEffect(effect.getType());
        }
        player.getInventory().clear();
        player.getInventory().setArmorContents(new ItemStack[4]);
        player.setHealth(20.0);
        player.setFoodLevel(20);
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

    public void setHelmet(String team, Player player) {
        ItemStack helm = new ItemStack(Material.LEATHER_HELMET);
        LeatherArmorMeta meta = (LeatherArmorMeta) helm.getItemMeta();
        if (team.equals("red")) {
            meta.setColor(Color.RED);
            meta.setDisplayName("§cTeam Rot");
        } else {
            meta.setColor(Color.BLUE);
            meta.setDisplayName("§bTeam Blau");
        }
        helm.setItemMeta(meta);
        player.getInventory().setHelmet(helm);
    }

    public boolean isPlayerInGame(UUID uuid) {
        return playerTeams.containsKey(uuid);

    }

    public void removePlayerFromGame(UUID uuid, Player player) {
        for (PotionEffect effect : player.getActivePotionEffects()) {
            player.removePotionEffect(effect.getType());
        }
        if (savedInventories.containsKey(uuid)) {
            player.getInventory().setContents(savedInventories.get(uuid));
            player.getInventory().setArmorContents(savedArmor.get(uuid));
            player.setLevel(savedLevels.get(uuid));
            player.setExp(savedExp.get(uuid));
            player.setFoodLevel(savedFood.get(uuid));
            Location oldLoc = savedLocations.get(uuid);
            if (oldLoc != null) player.teleport(oldLoc);
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
        for (List<UUID> arenaPlayers : activeArenaPlayers.values()) {
            arenaPlayers.remove(uuid);
        }
    }

    public void spawnBall(String arenaName) {
        String path = arenaName + ".ballspawn";
        String worldName = plugin.getArenaConfig().getString(path + ".world");
        if (worldName == null) return;

        double x = plugin.getArenaConfig().getDouble(path + ".x");
        double y = plugin.getArenaConfig().getDouble(path + ".y");
        double z = plugin.getArenaConfig().getDouble(path + ".z");
        Location loc = new Location(Bukkit.getWorld(worldName), x + 0.5, y, z + 0.5);

        // --- AKTUELL: SLIME SPAWN ---
        Slime ball = loc.getWorld().spawn(loc, Slime.class);
        ball.setSize(1);
        ball.setCustomName("§8BALL_" + arenaName);
        ball.setCustomNameVisible(false);
        ball.setAI(false);
        ball.setInvulnerable(true);

        // --- SPÄTER: SULFUR CUBE SPAWN (Einfach auskommentieren) ---
        /*
        String nbt = "{Size:1,CustomName:'\"§8BALL_" + arenaName + "\"',equipment:{body:{id:\"minecraft:birch_wood\",count:1}},active_effects:[{id:\"minecraft:invisibility\",duration:999999999,show_particles:0b}],NoAI:1b,Invulnerable:1b}";
        String command = String.format("summon sulfur_cube %f %f %f %s", loc.getX(), loc.getY(), loc.getZ(), nbt);
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
        */
    }

    // Hilfsmethode zur Torerkennung
    public boolean isInside(Location loc, Location corner1, Location corner2) {
        double x = loc.getX();
        double y = loc.getY();
        double z = loc.getZ();

        return x >= Math.min(corner1.getX(), corner2.getX()) && x <= Math.max(corner1.getX(), corner2.getX()) &&
                y >= Math.min(corner1.getY(), corner2.getY()) && y <= Math.max(corner1.getY(), corner2.getY()) &&
                z >= Math.min(corner1.getZ(), corner2.getZ()) && z <= Math.max(corner1.getZ(), corner2.getZ());
    }

    public Entity getBall(String arenaName, World world) {
        for (Entity entity : world.getEntities()) {
            if (entity.getCustomName() != null) {
                if (entity.getCustomName().equals("§8BALL_" + arenaName)) {
                    return entity;
                }
            }
        }
        return null;
    }

    public String goaldetection(String arenaName, World world) {
        // 1. Ball suchen (Deine Hilfsmethode nutzen)
        Entity ball = getBall(arenaName, world);
        if (ball == null) return "nix"; // Sicherheit: Falls kein Ball da ist

        // 2. Koordinaten aus der Config laden (für Blau)
        double bc1x = plugin.getArenaConfig().getDouble(arenaName + ".bluegoal1.x");
        double bc2x = plugin.getArenaConfig().getDouble(arenaName + ".bluegoal2.x");
        double bc1y = plugin.getArenaConfig().getDouble(arenaName + ".bluegoal1.y");
        double bc2y = plugin.getArenaConfig().getDouble(arenaName + ".bluegoal2.y");
        double bc1z = plugin.getArenaConfig().getDouble(arenaName + ".bluegoal1.z");
        double bc2z = plugin.getArenaConfig().getDouble(arenaName + ".bluegoal2.z");

        // 3. Koordinaten aus der Config laden (für Rot)
        double rc1x = plugin.getArenaConfig().getDouble(arenaName + ".redgoal1.x");
        double rc2x = plugin.getArenaConfig().getDouble(arenaName + ".redgoal2.x");
        double rc1y = plugin.getArenaConfig().getDouble(arenaName + ".redgoal1.y");
        double rc2y = plugin.getArenaConfig().getDouble(arenaName + ".redgoal2.y");
        double rc1z = plugin.getArenaConfig().getDouble(arenaName + ".redgoal1.z");
        double rc2z = plugin.getArenaConfig().getDouble(arenaName + ".redgoal2.z");

        // Ball-Position (bx, by, bz)
        double bx = ball.getLocation().getX();
        double by = ball.getLocation().getY();
        double bz = ball.getLocation().getZ();

        // 4. Check für Blau (mit Math.max/min zur Sicherheit)
        if (bx <= Math.max(bc1x, bc2x) && bx >= Math.min(bc1x, bc2x) &&
                by <= Math.max(bc1y, bc2y) && by >= Math.min(bc1y, bc2y) &&
                bz <= Math.max(bc1z, bc2z) && bz >= Math.min(bc1z, bc2z)) {
            return "Blue1";
        }

        // 5. Check für Rot
        if (bx <= Math.max(rc1x, rc2x) && bx >= Math.min(rc1x, rc2x) &&
                by <= Math.max(rc1y, rc2y) && by >= Math.min(rc1y, rc2y) &&
                bz <= Math.max(rc1z, rc2z) && bz >= Math.min(rc1z, rc2z)) {
            return "Red1";
        }

        return "nix";
    }
}