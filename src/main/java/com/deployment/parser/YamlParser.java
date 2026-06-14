package com.deployment.parser;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.yaml.snakeyaml.Yaml;

/**
 * Liest eine YAML Datei ein und extrahiert alle Services und ihre Abhängigkeiten.
 * YamlParser ist der Übersetzer zwischen YAML Datei und Java Code
 * 
  Schritt 1: YamlParser liest YAML
           ↓
           z.b gibt zurück:
           {
             "frontend"     → ["api-gateway"],
             "api-gateway"  → ["auth-service"],
             "auth-service" → ["database", "redis"],
             "database"     → [],
             "redis"        → []
           }
           ↓
   Schritt 2: Graph nimmt diese Map
           → baut Knoten auf
           → baut Kanten auf
           → berechnet In-Degree
           ↓
    Schritt 3: Algorithmen arbeiten auf dem Graph

 */
public class YamlParser {

    /**
     * Liest die YAML Datei und gibt eine Map zurück:
     * Service → Liste seiner Abhängigkeiten
     */
    public Map<String, List<String>> parse(String filePath) {

        Map<String, List<String>> dependencies = new HashMap<>();

        try {
            // YAML Datei öffnen
            Yaml yaml = new Yaml();
            FileInputStream file = new FileInputStream(filePath);

            // YAML einlesen
            Map<String, Object> data = yaml.load(file);

            // Prüfen ob "services" vorhanden
            if (data == null || !data.containsKey("services")) {
                System.out.println(
                    "Fehler: Kein 'services' in YAML gefunden!");
                return dependencies;
            }

            // "services" Teil holen
            Map<String, Object> services =
                (Map<String, Object>) data.get("services");

            // Für jeden Service
            for (String serviceName : services.keySet()) {

                Map<String, Object> serviceData =
                    (Map<String, Object>) services.get(serviceName);

                // depends_on holen
                List<String> deps = new ArrayList<>();

                if (serviceData != null &&
                    serviceData.containsKey("depends_on")) {
                    deps = (List<String>) serviceData.get("depends_on");
                }

                dependencies.put(serviceName, deps);
            }

        } catch (FileNotFoundException e) {
            System.out.println(
                "Fehler: YAML Datei nicht gefunden: " + filePath);
        } catch (Exception e) {
            System.out.println(
                "Fehler beim Einlesen: " + e.getMessage());
        }

        return dependencies;
    }
}