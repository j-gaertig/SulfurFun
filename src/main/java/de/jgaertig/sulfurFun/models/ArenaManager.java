package de.jgaertig.sulfurFun.models;

import java.util.*;

public class ArenaManager {

    private final Map<String, List<UUID>> activePlayers = new HashMap<>();
    private final Map<String, List<UUID>> queue = new HashMap<>();
    private final Map<String, Integer> arenaMaxPlayers = new HashMap<>();

    // Methode, um jemanden in die Warteschlange zu setzen
    public void addToQueue(String arenaName, UUID uuid) {
        // Wir holen die Liste für die Arena. Wenn es keine gibt, erstellen wir eine neue.
        queue.computeIfAbsent(arenaName, k -> new ArrayList<>()).add(uuid);
    }

    public void loadArena(String name, int max) {
        arenaMaxPlayers.put(name, max);
    }

    // Wir brauchen den Arena-Namen und die maxPlayers als Zahl
    public String getStatus(UUID uuid, String arenaName, int maxPlayers) {
        int maxTotal = maxPlayers * 2;

        // 1. Hol die Liste für diese spezifische Arena
        List<UUID> arenaQueue = queue.get(arenaName);

        // 2. Finde heraus, an welcher Stelle der Spieler steht (0, 1, 2...)
        int position = arenaQueue.indexOf(uuid);
        int realPosition = position + 1; // Für die Anzeige (1, 2, 3...)

        // Jetzt die Entscheidung für die Actionbar
        if (realPosition <= maxTotal) {
            // Der Spieler ist einer derjenigen, die das Spiel voll machen
            return realPosition + "/" + maxTotal + " Players";
        } else {
            // Der Spieler ist zu weit hinten in der Schlange
            return "In Queue";
        }
    }

    public QueueData getQueueData(UUID uuid, String arenaName) {
        // 1. Sicherheit: Existiert die Arena überhaupt in der Warteschlange?
        if (!queue.containsKey(arenaName)) return null;

        List<UUID> arenaQueue = queue.get(arenaName);
        int index = arenaQueue.indexOf(uuid);

        // 2. Sicherheit: Ist der Spieler überhaupt in dieser Liste?
        if (index == -1) return null;

        // 3. Daten holen (maxPlayers wurde vorher per loadArena gespeichert)
        int maxTotal = arenaMaxPlayers.getOrDefault(arenaName, 0) * 2;

        return new QueueData(index + 1, maxTotal);
    }

    public boolean isAlreadyInGame(UUID uuid) {
        // 1. Wir prüfen alle Warteschlangen
        for (List<UUID> queueList : queue.values()) {
            if (queueList.contains(uuid)) return true;
        }

        // 2. Wir prüfen alle aktiven Spiele
        for (List<UUID> activeList : activePlayers.values()) {
            if (activeList.contains(uuid)) return true;
        }

        return false;
    }

    public int getQueueSize(String arenaName) {
        List<UUID> arenaQueue = queue.get(arenaName);
        return (arenaQueue != null) ? arenaQueue.size() : 0;
    }

    public int getMaxPlayers(String arenaName) {
        // Gibt den gespeicherten Wert zurück, oder 0, falls die Arena unbekannt ist
        return arenaMaxPlayers.getOrDefault(arenaName, 0);
    }

}
