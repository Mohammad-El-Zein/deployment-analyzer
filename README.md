# Deployment Analyzer

<p align="center">
  <img src="https://img.shields.io/badge/TU-Dortmund-4CAF50?style=for-the-badge" alt="Algorithm Engineering"/>
  <img src="https://img.shields.io/badge/Algorithm-Engineering-003DA5?style=for-the-badge" alt="TU Dortmund"/>
</p>

<p align="center">
  <strong>Graphbasierte Modellierung und algorithmische Optimierung von Microservice-Deployment-Reihenfolgen</strong>
</p>

<p align="center">
  Bachelorarbeit – TU Dortmund<br>
  Mohammad El Zein | Matrikelnummer: 259506<br>
  Betreuer: Prof. Dr. Kevin Buchin<br>
  Zweitgutachterin: Jun.-Prof. Dr. Carolin Rehs
</p>

---

## Beschreibung

Dieses Tool analysiert Microservice-Abhängigkeiten automatisch und berechnet die optimale Deployment-Reihenfolge mithilfe von Graphalgorithmen. Der Benutzer gibt nur die Abhängigkeiten an – alles andere berechnet das System automatisch.

### Probleme die gelöst werden

- Manuelle Reihenfolgebestimmung skaliert nicht bei vielen Services
- Zyklische Abhängigkeiten werden von aktuellen Tools nicht zuverlässig erkannt
- Parallelisierungspotenzial wird verschenkt

---

## Algorithmen

| Algorithmus | Zweck | Laufzeit |
|---|---|---|
| Kahn's Algorithmus | Topologische Sortierung (BFS) | O(V+E) |
| DFS Topologische Sortierung | Topologische Sortierung (DFS) | O(V+E) |
| Tarjan's Algorithmus | Zyklenerkennung (SCC) | O(V+E) |
| Feedback Arc Set | Zyklusauflösung (Greedy) | O(V+E) |
| Level-BFS | Parallelisierungsoptimierung | O(V+E) |

---

## Voraussetzungen

- Java 21
- Maven 3.9+

---

## Installation

```bash
git clone https://github.com/Mohammad-El-Zein/deployment-analyzer.git
cd deployment-analyzer
mvn compile
```

---

## Verwendung

### 1. Deployment Analyse

Standardmäßig wird `simple.yaml` (5 Services) analysiert.

Um eine andere YAML Datei zu analysieren, ändere den Pfad in `Main.java`:

```java
String filePath = "src/main/resources/examples/simple.yaml";
// Andere Optionen:
// String filePath = "src/main/resources/examples/medium.yaml";  // 20 Services
// String filePath = "src/main/resources/examples/large.yaml";   // 50 Services
// String filePath = "src/main/resources/examples/xlarge.yaml";  // 100 Services
// String filePath = "src/main/resources/examples/xxlarge.yaml"; // 1000 Services
// String filePath = "src/main/resources/examples/cycle.yaml"; // 3 Services mit Zyklus
// String filePath = "src/main/resources/examples/xlarge-withCycle.yaml"; // 103 Services

```

Dann ausführen:

```bash
mvn exec:java
```

### 2. Evaluation (Kahn vs DFS Vergleich)

In `pom.xml` ändern:

```xml
<mainClass>com.deployment.EvaluationRunner</mainClass>
```

Dann:

```bash
mvn exec:java
```

### 3. YAML Dateien generieren

Falls du eine eigene Größe generieren möchtest, füge in `GraphGenerator.java` am Ende der `main()` Methode folgende Zeile hinzu:

```java
generate(deine_gewuenschte_anzahl, "gewuenschter_name.yaml");
```

Dann in `pom.xml` ändern:

```xml
<mainClass>com.deployment.GraphGenerator</mainClass>
```

Und ausführen:

```bash
mvn exec:java
```

---

## YAML Format

```yaml
version: "3"
services:
  frontend:
    depends_on:
      - api-gateway
  api-gateway:
    depends_on:
      - auth-service
  auth-service:
    depends_on:
      - database
  database:
    depends_on: []
```

---

## Beispiel Output – Kein Zyklus (simple.yaml)

```
╔════════════════════════════════════╗
║       DEPLOYMENT ANALYZER          ║
╚════════════════════════════════════╝

Lese YAML: src/main/resources/examples/simple.yaml

#=====================================#
#          GRAPH UEBERSICHT           #
#=====================================#
 Services: 5
-----------------------------------------------------
 auth-service  -> [api-gateway]        (In-Degree: 2)
 database      -> [auth-service]       (In-Degree: 0)
 api-gateway   -> [frontend]           (In-Degree: 1)
 frontend      -> []                   (In-Degree: 1)
 redis         -> [auth-service]       (In-Degree: 0)
-----------------------------------------------------

#=====================================#
#      KAHN'S ALGORITHMUS (BFS)       #
#=====================================#
 [OK] Kein Zyklus gefunden
--------------------------------------
 Deployment-Reihenfolge:
   1. database
   2. redis
   3. auth-service
   4. api-gateway
   5. frontend
--------------------------------------
 Laufzeit : 308900 ns (0.309 ms)
 Speicher : 274.09 KB
--------------------------------------

#=====================================#
#     DFS TOPOLOGISCHE SORTIERUNG     #
#=====================================#
 [OK] Kein Zyklus gefunden!
--------------------------------------
 Deployment-Reihenfolge:
   1. redis
   2. database
   3. auth-service
   4. api-gateway
   5. frontend
--------------------------------------
 Laufzeit : 131900 ns (0.132 ms)
 Speicher : 167.50 KB
--------------------------------------

#=====================================#
#        VERGLEICH: KAHN vs DFS       #
#=====================================#
 Graph-Groesse: 5 Services

                 Kahn                 DFS
---------------------------------------------------------
 Laufzeit        308900 ns            131900 ns
 in ms           0.309 ms             0.132 ms
 Speicher        274.09 KB            167.50 KB
 Zyklus          Nein                 Nein
--------------------------------------
 Schneller        : DFS
 Weniger Speicher : DFS
--------------------------------------

#=====================================#
#      LEVEL-BFS PARALLELISIERUNG     #
#=====================================#
 Parallele Deployment-Gruppen:

   Level 0   -> [database, redis]
   Level 1   -> [auth-service]
   Level 2   -> [api-gateway]
   Level 3   -> [frontend]

--------------------------------------
 Zeitvergleich:
   Sequenziell : 5 Zeiteinheiten
   Parallel    : 4 Zeiteinheiten
   Ersparnis   : 1 Zeiteinheiten (20%)
--------------------------------------
 Algorithmus-Laufzeit: 59300 ns (0.059 ms)
--------------------------------------

╔════════════════════════════════════╗
║      Analyse abgeschlossen         ║
╚════════════════════════════════════╝
```

---

## Beispiel Output – Zyklus gefunden (cycle.yaml)

```
#=====================================#
#      KAHN'S ALGORITHMUS (BFS)       #
#=====================================#
 [FEHLER] Zyklus gefunden!
 Deployment nicht moeglich.
--------------------------------------

#=====================================#
#        TARJAN'S ALGORITHMUS         #
#=====================================#
 [FEHLER] Zyklen gefunden: 1
--------------------------------------
 Zyklus 1: [auth-service, user-service]
--------------------------------------

#=====================================#
#          FEEDBACK ARC SET           #
#=====================================#
 Entferne folgende Abhaengigkeiten:
   [X] user-service -> auth-service
 [OK] Nach Entfernung: Kein Zyklus mehr!
--------------------------------------

#=====================================#
#      LEVEL-BFS PARALLELISIERUNG     #
#=====================================#
 Level 0 -> [database, auth-service]
 Level 1 -> [user-service]
--------------------------------------
```

---

## Projektstruktur

```
deployment-analyzer/
├── src/
│   ├── main/java/com/deployment/
│   │   ├── algorithm/
│   │   │   ├── KahnAlgorithm.java
│   │   │   ├── DFSTopologicalSort.java
│   │   │   ├── TarjanAlgorithm.java
│   │   │   ├── FeedbackArcSet.java
│   │   │   └── LevelBFS.java
│   │   ├── model/
│   │   │   ├── Graph.java
│   │   │   └── Service.java
│   │   ├── parser/
│   │   │   └── YamlParser.java
│   │   ├── validator/
│   │   │   └── Validator.java
│   │   ├── EvaluationRunner.java
│   │   ├── GraphGenerator.java
│   │   ├── Main.java
│   │   └── ResultPrinter.java
│   └── resources/examples/
│       ├── simple.yaml        (5 Services)
│       ├── medium.yaml        (20 Services)
│       ├── large.yaml         (50 Services)
│       ├── xlarge.yaml        (100 Services)
│       └── xxlarge.yaml       (1000 Services)
└── pom.xml
```

---

## Testdaten

| Datei | Services | Zyklus | Parallelersparnis |
|---|---|---|---|
| simple.yaml | 5 | Nein | 20% |
| medium.yaml | 20 | Nein | 53% |
| large.yaml | 50 | Nein | 90% |
| xlarge.yaml | 100 | Ja (Test) | 95% |
| xxlarge.yaml | 1000 | Nein | 99% |

---

## Evaluation Ergebnisse (Kahn vs DFS)

| Services | Kahn (avg) | DFS (avg) | Schneller |
|---|---|---|---|
| 5 | 0.109 ms | 0.061 ms | DFS |
| 20 | 0.150 ms | 0.120 ms | DFS |
| 50 | 0.178 ms | 0.188 ms | Kahn |
| 100 | 0.265 ms | 0.196 ms | DFS |
| 1000 | 1.941 ms | 1.070 ms | DFS |

---

## Technologien

```
Java 21  •  Maven 3.9  •  SnakeYAML 2.0  •  JUnit Jupiter 5.9.2
```

---

## Autor

<table>
  <tr>
    <td align="center">
      <strong>Mohammad El Zein</strong><br>
      Bachelor Informatik @ TU Dortmund<br><br>
      <a href="https://github.com/Mohammad-El-Zein">
        <img src="https://img.shields.io/badge/GitHub-181717?style=for-the-badge&logo=github&logoColor=white" alt="GitHub"/>
      </a>
    </td>
  </tr>
</table>

---

## Lizenz

Dieses Projekt wurde im Rahmen einer Bachelorarbeit an der TU Dortmund erstellt.

**TU Dortmund – Lehrstuhl Algorithm Engineering**  
Prof. Dr. Kevin Buchin
