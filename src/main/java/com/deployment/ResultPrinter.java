package com.deployment;

import java.util.List;

import com.deployment.algorithm.DFSTopologicalSort;
import com.deployment.algorithm.FeedbackArcSet;
import com.deployment.algorithm.KahnAlgorithm;
import com.deployment.algorithm.LevelBFS;
import com.deployment.algorithm.TarjanAlgorithm;
import com.deployment.model.Graph;

/**
 * Zuständig für die formatierte Ausgabe
 * aller Ergebnisse
 */
public class ResultPrinter {

    private static final String LINE =
        "#=====================================#";

    private static final String THIN_LINE =
        "--------------------------------------";

    /**
     * Graph Übersicht ausgeben
     */
    public void printGraph(Graph graph) {
        System.out.println();
        System.out.println();
        System.out.println(LINE);
        System.out.println("#          GRAPH UEBERSICHT           #");
        System.out.println(LINE);
        System.out.println(
            " Services: " + graph.getNodeCount());
        System.out.println(THIN_LINE + "---------------");

        for (String node : graph.getNodes()) {
            System.out.printf(
                " %-13s -> %-20s (In-Degree: %d)%n",
                node,
                graph.getNeighbors(node).toString(),
                graph.getInDegreeOf(node));
        }
        System.out.println(THIN_LINE + "---------------");
        System.out.println();
    } 

    /**
     * Kahn Ergebnis ausgeben
     */
    public void printKahn(KahnAlgorithm kahn) {
        System.out.println();
        System.out.println(LINE);
        System.out.println("#      KAHN'S ALGORITHMUS (BFS)       #");
        System.out.println(LINE);

        if (kahn.hasCycle()) {
            System.out.println(
                " [FEHLER] Zyklus gefunden");
            System.out.println(
                " Deployment nicht moeglich.");
        } else {
            System.out.println(
                " [OK] Kein Zyklus gefunden");
            System.out.println(THIN_LINE);
            System.out.println(
                " Deployment-Reihenfolge:");
            System.out.println();
            List<String> order = kahn.getDeploymentOrder();
            for (int i = 0; i < order.size(); i++) {
                System.out.println(
                    "   " + (i + 1) + ". " + order.get(i));
            }
        }
         System.out.println(THIN_LINE);
    System.out.println(
        " Laufzeit : " + kahn.getTimeFormatted());
    System.out.println(
        " Speicher : " + kahn.getMemoryFormatted());
    System.out.println(THIN_LINE);
    System.out.println();
    }

    /**
     * DFS Ergebnis ausgeben
     */
    public void printDFS(DFSTopologicalSort dfs) {
        System.out.println();
        System.out.println(LINE);
        System.out.println("#     DFS TOPOLOGISCHE SORTIERUNG     #");
        System.out.println(LINE);

        if (dfs.hasCycle()) {
            System.out.println(
                " [FEHLER] Zyklus gefunden");
            System.out.println(
                " Deployment nicht moeglich.");
        } else {
            System.out.println(
                " [OK] Kein Zyklus gefunden!");
            System.out.println(THIN_LINE);
            System.out.println(
                " Deployment-Reihenfolge:");
            System.out.println();
            List<String> order = dfs.getDeploymentOrder();
            for (int i = 0; i < order.size(); i++) {
                System.out.println(
                    "   " + (i + 1) + ". " + order.get(i));
            }
        }
            System.out.println();
            System.out.println(THIN_LINE);
            System.out.println(
                " Laufzeit : " + dfs.getTimeFormatted());
            System.out.println(
                " Speicher : " + dfs.getMemoryFormatted());
            System.out.println(THIN_LINE);
            System.out.println();
    }

    /**
     * Vergleich Kahn vs DFS
     */
    public void printComparison(
        KahnAlgorithm kahn,
        DFSTopologicalSort dfs,
        Graph graph) {

        System.out.println();
        System.out.println(LINE);
        System.out.println("#        VERGLEICH: KAHN vs DFS       #");
        System.out.println(LINE);
        System.out.println();
        System.out.println(
            " Graph-Groesse: " 
            + graph.getNodeCount() 
            + " Services");
        System.out.println();
        System.out.printf(
            " %-15s %-20s %-20s%n",
            "", "Kahn", "DFS");
        System.out.println(THIN_LINE + "-------------------");
        System.out.printf(
            " %-15s %-20s %-20s%n",
            "Laufzeit",
            kahn.getExecutionTime() + " ns",
            dfs.getExecutionTime() + " ns");
        System.out.printf(
            " %-15s %-20s %-20s%n",
            "in ms",
            String.format("%.3f ms",
                kahn.getExecutionTime() / 1_000_000.0),
            String.format("%.3f ms",
                dfs.getExecutionTime() / 1_000_000.0));
        System.out.printf(
            " %-15s %-20s %-20s%n",
            "Speicher",
            kahn.getMemoryFormatted(),
            dfs.getMemoryFormatted());
        System.out.printf(
            " %-15s %-20s %-20s%n",
            "Zyklus",
            kahn.hasCycle() ? "Ja" : "Nein",
            dfs.hasCycle() ? "Ja" : "Nein");

        String faster = kahn.getExecutionTime()
            < dfs.getExecutionTime() ? "Kahn" : "DFS";
        String lessMemory = kahn.getMemoryUsed()
            < dfs.getMemoryUsed() ? "Kahn" : "DFS";

        System.out.println();
        System.out.println(THIN_LINE);
        System.out.println(" Schneller        : " + faster);
        System.out.println(" Weniger Speicher : " + lessMemory);
        System.out.println(THIN_LINE);
        System.out.println();
    }

    /**
     * Tarjan Ergebnis ausgeben
     */
    public void printTarjan(TarjanAlgorithm tarjan) {
        System.out.println();
        System.out.println(LINE);
        System.out.println("#        TARJAN'S ALGORITHMUS         #");
        System.out.println(LINE);
        System.out.println();

        if (!tarjan.hasCycles()) {
            System.out.println(
                " [OK] Kein Zyklus gefunden!");
        } else {
            System.out.println(
                " [FEHLER] Zyklen gefunden: "
                + tarjan.getCycles().size());
            System.out.println(THIN_LINE);
            System.out.println();

            List<List<String>> cycles = tarjan.getCycles();
            for (int i = 0; i < cycles.size(); i++) {
                System.out.println(
                    " Zyklus " + (i + 1) + ": "
                    + cycles.get(i));
            }
        }
        System.out.println();
        System.out.println(THIN_LINE);
        System.out.println(
            " Laufzeit: "
            + tarjan.getExecutionTime() + " ns");
        System.out.println(THIN_LINE);
        System.out.println();
    }

    /**
     * Feedback Arc Set Ergebnis ausgeben
     */
    public void printFAS(FeedbackArcSet fas) {
        System.out.println();
        System.out.println(LINE);
        System.out.println("#          FEEDBACK ARC SET           #");
        System.out.println(LINE);
        System.out.println();

        if (fas.getEdgesToRemove().isEmpty()) {
            System.out.println(
                " [OK] Keine Kanten zu entfernen!");
        } else {
            System.out.println(
                " Entferne folgende Abhaengigkeiten:");
            System.out.println();
            for (String edge : fas.getEdgesToRemove()) {
                System.out.println(
                    "   [X] " + edge);
            }
            System.out.println();
            System.out.println(
                " [OK] Nach Entfernung: Kein Zyklus mehr!");
        }
        System.out.println();
        System.out.println(THIN_LINE);
        System.out.println(
            " Laufzeit: "
            + fas.getExecutionTime() + " ns");
        System.out.println(THIN_LINE);
        System.out.println();
    }

    /**
     * Level-BFS Ergebnis ausgeben
     */
    public void printLevelBFS(LevelBFS levelBFS) {
        System.out.println();
        System.out.println(LINE);
        System.out.println("#      LEVEL-BFS PARALLELISIERUNG     #");
        System.out.println(LINE);
        System.out.println();
        System.out.println(
            " Parallele Deployment-Gruppen:");
        System.out.println();

        List<List<String>> levels = levelBFS.getLevels();
        for (int i = 0; i < levels.size(); i++) {
            System.out.printf(
                "   Level %-3d -> %s%n",
                i, levels.get(i));
        }

        System.out.println();
        System.out.println(THIN_LINE);
        System.out.println(" Zeitvergleich:");
        System.out.println();
        System.out.println(
            "   Sequenziell : "
            + levelBFS.getSequentialTime()
            + " Zeiteinheiten");
        System.out.println(
            "   Parallel    : "
            + levelBFS.getParallelTime()
            + " Zeiteinheiten");

        int saving = levelBFS.getSequentialTime()
                   - levelBFS.getParallelTime();
        int percent = Math.round(
            (1.0f - (float) levelBFS.getParallelTime()
            / levelBFS.getSequentialTime()) * 100);

        System.out.println(
            "   Ersparnis   : "
            + saving + " Zeiteinheiten ("
            + percent + "%)");
        System.out.println();
        System.out.println(THIN_LINE);
        System.out.println(
    " Algorithmus-Laufzeit: "
    + levelBFS.getExecutionTime() + " ns ("
    + String.format("%.3f",
        levelBFS.getExecutionTime() / 1_000_000.0)
    + " ms)");
        System.out.println(THIN_LINE);
        System.out.println();
    }

    
}