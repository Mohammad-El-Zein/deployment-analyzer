package com.deployment.validator;

import java.io.File;
import java.util.List;
import java.util.Map;

/**
 * Prüft die Eingabe auf Sonderfälle
 * bevor die Algorithmen laufen
 */
public class Validator {

    /**
     * Prüft ob die YAML Datei existiert
     */
    public void validateFile(String filePath) {
        
        // Sonderfall 5: Datei nicht gefunden
        File file = new File(filePath);
        if (!file.exists()) {
            throw new RuntimeException(
                "Fehler: YAML Datei nicht gefunden: " 
                + filePath);
        }
    }


    /**
     * Prüft die eingelesenen Services
     */
    public void validateDependencies(
        Map<String, List<String>> dependencies) {

        // Sonderfall 4: Leere YAML
        if (dependencies == null || dependencies.isEmpty()) {
            throw new RuntimeException(
                "Fehler: Keine Services in der YAML gefunden!");
        }

        // Sonderfall 1: Nur ein Service
        if (dependencies.size() == 1) {
            String service = dependencies.keySet()
                                         .iterator()
                                         .next();
            System.out.println(
                "Info: Nur ein Service gefunden: " 
                + service 
                + " → wird direkt deployed.");
        }

        // Prüfe ob alle depends_on Services existieren
        //zb wenn auth-service depends_on database hat, aber database nicht in der YAML definiert ist, dann ist das ein Fehler
        for (String service : dependencies.keySet()) {
            List<String> deps = dependencies.get(service);
            
            for (String dep : deps) {
                if (!dependencies.containsKey(dep)) {
                    throw new RuntimeException(
                        "Fehler: Service '" + dep 
                        + "' in depends_on von '" 
                        + service 
                        + "' existiert nicht!");
                }
            }
        }
    }
}