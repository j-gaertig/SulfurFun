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

    public int getPosition(UUID uuid, String arenaName) {
        List<UUID> spielerListe = queue.get(arenaName);

        // Sicherheit: Falls die Arena oder die Liste nicht existiert
        if (spielerListe == null) return -1;

        int index = spielerListe.indexOf(uuid);

        // Falls der Spieler in der Liste ist, gib index + 1 zurück, sonst -1
        return (index != -1) ? (index + 1) : -1;
    }

    public void removePlayer(UUID uuid) {
        // Aus allen Warteschlangen entfernen
        for (List<UUID> q : queue.values()) {
            q.remove(uuid);
        }
        // Aus allen aktiven Spielen entfernen
        for (List<UUID> a : activePlayers.values()) {
            a.remove(uuid);
        }
    }

    public String getPlayerArena(UUID uuid) {
        for (Map.Entry<String, List<UUID>> entry : queue.entrySet()) {
            if (entry.getValue().contains(uuid)) return entry.getKey();
        }
        for (Map.Entry<String, List<UUID>> entry : activePlayers.entrySet()) {
            if (entry.getValue().contains(uuid)) return entry.getKey();
        }
        return null;
    }

}
