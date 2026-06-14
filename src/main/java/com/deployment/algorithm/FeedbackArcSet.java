package com.deployment.algorithm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.deployment.model.Graph;

/**
 * Feedback Arc Set Algorithmus
 * Findet minimale Kanten die entfernt werden müssen um alle Zyklen aufzulösen
 * Nutzt Greedy Heuristik (NP-schweres Problem)
 */
public class FeedbackArcSet {

    // Kanten die entfernt werden sollen
    // Format: "ServiceA -> ServiceB"
    private List<String> edgesToRemove;

    // Laufzeit messen
    private long executionTime;
 
    public FeedbackArcSet() {
        edgesToRemove = new ArrayList<>();
        executionTime = 0;
    }

    /**
     * Führt Feedback Arc Set Greedy Heuristik aus
     */
    public void execute(Graph graph, List<List<String>> cycles) {

        // Laufzeit Start
        long startTime = System.nanoTime();

        // Kopie der Adjazenzliste erstellen
        // (wir verändern sie – original bleibt unberührt)
        Map<String, List<String>> adjCopy = 
            new HashMap<>();
        for (String node : graph.getNodes()) {
            adjCopy.put(node, 
                new ArrayList<>(graph.getNeighbors(node)));
        }

        // Kopie der Zyklen
        List<List<String>> remainingCycles = 
            new ArrayList<>(cycles);

        // Greedy: solange noch Zyklen existieren
        while (!remainingCycles.isEmpty()) {

            // Schritt 1: Zähle für jede Kante
            // in wie vielen Zyklen sie vorkommt
            Map<String, Integer> edgeCount = new HashMap<>();

            for (List<String> cycle : remainingCycles) {
                for (int i = 0; i < cycle.size(); i++) {

                    // Kante: cycle[i] → cycle[i+1]
                    String from = cycle.get(i);
                    String to = cycle.get(
                        (i + 1) % cycle.size());
                    String edge = from + " -> " + to;

                    edgeCount.put(edge,
                        edgeCount.getOrDefault(edge, 0) + 1);
                }
            }

            // Schritt 2: Finde Kante mit höchstem Vorkommen
            String bestEdge = null;
            int maxCount = 0;

            for (String edge : edgeCount.keySet()) {
                if (edgeCount.get(edge) > maxCount) {
                    maxCount = edgeCount.get(edge);
                    bestEdge = edge;
                }
            }

            // Schritt 3: Entferne diese Kante
            if (bestEdge != null) {
                edgesToRemove.add(bestEdge);

                // Aus Adjazenzliste entfernen
                String[] parts = bestEdge.split(" -> ");
                String from = parts[0];
                String to = parts[1];
                adjCopy.get(from).remove(to);

                // Schritt 4: Entferne Zyklen die 
                // diese Kante enthalten
                remainingCycles.removeIf(cycle -> {
                    for (int i = 0; i < cycle.size(); i++) {
                        String f = cycle.get(i);
                        String t = cycle.get(
                            (i + 1) % cycle.size());
                        if (f.equals(from) && t.equals(to)) {
                            return true;
                        }
                    }
                    return false;
                });
            }
        }

        // Laufzeit Ende
        executionTime = System.nanoTime() - startTime;
    }

    /**
     * Gibt den Graph ohne die Zyklus-Kanten zurück
     */
    public Map<String, List<String>> getCleanAdjacencyList(
        Graph graph) {

        Map<String, List<String>> cleanAdj = new HashMap<>();

        for (String node : graph.getNodes()) {
            cleanAdj.put(node, 
                new ArrayList<>(graph.getNeighbors(node)));
        }

        // Entferne die problematischen Kanten
        for (String edge : edgesToRemove) {
            String[] parts = edge.split(" -> ");
            String from = parts[0];
            String to = parts[1];
            cleanAdj.get(from).remove(to);
        }

        return cleanAdj;
    }

    // Getter
    public List<String> getEdgesToRemove() {
        return edgesToRemove;
    }

    public long getExecutionTime() {
        return executionTime;
    }

    // Ergebnis ausgeben
    public void printResult() {
        if (edgesToRemove.isEmpty()) {
            System.out.println(" Keine Kanten zu entfernen!");
        } else {
            System.out.println(
                " Feedback Arc Set Lösung:");
            System.out.println(
                "Entferne folgende Abhängigkeiten:");
            for (String edge : edgesToRemove) {
                System.out.println(" - " + edge);
            }
            System.out.println(
                " Nach Entfernung: Kein Zyklus mehr!");

                
            System.out.println(
                " Laufzeit: " + executionTime + " ns");
        }
    }
}