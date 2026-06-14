package com.deployment.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Repräsentiert den Abhängigkeitsgraph
 * aller Microservices als DAG
 */
public class Graph {

    // Alle Services (Knoten)
    private List<String> nodes;

    // Adjazenzliste: Service → seine Nachfolger
    // z.B. database → [auth-service]
    private Map<String, List<String>> adjacencyList;

    // Für jeden Service: wie viele zeigen auf ihn?
    // z.B. auth-service → 2
    private Map<String, Integer> inDegree;

    // Konstruktor
    public Graph(Map<String, List<String>> dependencies) {

        nodes = new ArrayList<>();
        adjacencyList = new HashMap<>();
        inDegree = new HashMap<>();

        // Schritt 1: Alle Services als Knoten hinzufügen
        //Für jeden Service:
        //- füge ihn zu nodes hinzu
        //- erstelle leere Nachfolger-Liste
        //- setze In-Degree auf 0
        for (String service : dependencies.keySet()) {
            nodes.add(service);
            adjacencyList.put(service, new ArrayList<>());
            inDegree.put(service, 0);
        }

        // Schritt 2: Kanten aufbauen
        // auth depends_on: [database] , bedeutet : database → auth-service
        for (String service : dependencies.keySet()) {
            List<String> deps = dependencies.get(service);

            for (String dep : deps) {
                // dep muss vor service laufen
                // Kante: dep → service
                adjacencyList.get(dep).add(service);

                // In-Degree von service erhöhen
                inDegree.put(service, inDegree.get(service) + 1);
            }
        }
    }

    // Getter Methoden, damit wir in andere Klassen auf die Graph-Daten zugreifen können
    //Gibt alle Services zurück
    //z.B. ["database", "redis", "auth-service"]
    public List<String> getNodes() {
        return nodes;
    }

    // Gibt die ganze Adjazenzliste zurück
    // z.B. {database → [auth], redis → [auth]}
    public Map<String, List<String>> getAdjacencyList() {
        return adjacencyList;
    }

    // Gibt Nachfolger eines bestimmten Services zurück
    // Beispiel:
    // graph.getNeighbors("database") → ["auth-service"]
    public List<String> getNeighbors(String node) {
        return adjacencyList.getOrDefault(
            node, new ArrayList<>());
    }

   
     // Gibt die ganze In-Degree Map zurück
     // z.B. {database→0, redis→0, auth→2}
    public Map<String, Integer> getInDegree() {
        return inDegree;
    }

    // Gibt In-Degree eines bestimmten Services zurück
    public int getInDegreeOf(String node) {
        return inDegree.getOrDefault(node, 0);
    }

    // Gibt Anzahl der Services zurück
    public int getNodeCount() {
        return nodes.size();
    }

    // Für Debug – gibt den Graph aus
    public void print() {
        System.out.println("=== Graph ===");
        for (String node : nodes) {
            System.out.println(node
                + " → " + adjacencyList.get(node)
                + " (In-Degree: " + inDegree.get(node) + ")");
        }
    }
}