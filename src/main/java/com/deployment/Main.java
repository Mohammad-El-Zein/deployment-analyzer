package com.deployment;

import java.util.List;
import java.util.Map;

import com.deployment.algorithm.DFSTopologicalSort;
import com.deployment.algorithm.FeedbackArcSet;
import com.deployment.algorithm.KahnAlgorithm;
import com.deployment.algorithm.LevelBFS;
import com.deployment.algorithm.TarjanAlgorithm;
import com.deployment.model.Graph;
import com.deployment.parser.YamlParser;
import com.deployment.validator.Validator;

public class Main {   

    public static void main(String[] args) {

        String filePath =
            "src/main/resources/examples/xlarge-withCycle.yaml";      //mvn exec:java

        ResultPrinter printer = new ResultPrinter();

        // Header
        System.out.println(
            "╔════════════════════════════════════╗");
        System.out.println(
            "║       DEPLOYMENT ANALYZER          ║");
        System.out.println(
            "╚════════════════════════════════════╝");

        // Schritt 1: YAML einlesen
        System.out.println(
            "\nLese YAML: " + filePath);
        YamlParser parser = new YamlParser();
        Map<String, List<String>> dependencies =
            parser.parse(filePath);

        // Schritt 2: Validieren
        Validator validator = new Validator();
        try {
            validator.validateFile(filePath);
            validator.validateDependencies(dependencies);
        } catch (RuntimeException e) {
            System.out.println(e.getMessage());
            return;
        }

        // Schritt 3: Graph aufbauen
        Graph graph = new Graph(dependencies);
        printer.printGraph(graph);

        // Schritt 4: Kahn ausführen
        KahnAlgorithm kahn = new KahnAlgorithm();
        kahn.execute(graph);
        printer.printKahn(kahn);

        // Schritt 5: DFS ausführen
        DFSTopologicalSort dfs = new DFSTopologicalSort();
        dfs.execute(graph);
        printer.printDFS(dfs);

        // Schritt 6: Vergleich Kahn vs DFS
        if (!kahn.hasCycle() && !dfs.hasCycle()) {
            printer.printComparison(kahn, dfs, graph);
        }

        // Schritt 7: Zyklus?
        if (kahn.hasCycle() || dfs.hasCycle()) {

            TarjanAlgorithm tarjan = new TarjanAlgorithm();
            tarjan.execute(graph);
            printer.printTarjan(tarjan);

            FeedbackArcSet fas = new FeedbackArcSet();
            fas.execute(graph, tarjan.getCycles());
            printer.printFAS(fas);

            LevelBFS levelBFS = new LevelBFS();
            levelBFS.executeWithAdjacency(
                graph,
                fas.getCleanAdjacencyList(graph));
            printer.printLevelBFS(levelBFS);

        } else {

            LevelBFS levelBFS = new LevelBFS();
            levelBFS.execute(graph);
            printer.printLevelBFS(levelBFS);
        }

        // Footer
        System.out.println();
        System.out.println(
            "╔════════════════════════════════════╗");
        System.out.println(
            "║      Analyse abgeschlossen         ║");
        System.out.println(
            "╚════════════════════════════════════╝");
        
    }

    
}