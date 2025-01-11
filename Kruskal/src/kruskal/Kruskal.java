package kruskal;

import java.util.*;

class Kruskal {

    // Classe représentant une arête
    static class Arete {
        int source, destination, poids;

        // Constructeur Arete
        public Arete(int source, int destination, int poids) {
            this.source = source;
            this.destination = destination;
            this.poids = poids;
        }
    }

    // Classe représentant un sous-ensemble 
    static class SousEnsemble {
        int parent, rang; // rang : nombre de liaisons
    }

    // Trouver l'ensemble représentant un élément et  compresser le chemin
    static int trouver(SousEnsemble[] sousEnsembles, int i) {
        if (sousEnsembles[i].parent != i)
            sousEnsembles[i].parent = trouver(sousEnsembles, sousEnsembles[i].parent);
        return sousEnsembles[i].parent;
    }

    // Liaison de deux sous-ensembles
    static void Liaison(SousEnsemble[] sousEnsembles, int x, int y) { //Union
        int parentX = trouver(sousEnsembles, x);
        int parentY = trouver(sousEnsembles, y);

        // Union par le rang
        if (sousEnsembles[parentX].rang < sousEnsembles[parentY].rang)
            sousEnsembles[parentX].parent = parentY;
        else if (sousEnsembles[parentX].rang > sousEnsembles[parentY].rang)
            sousEnsembles[parentY].parent = parentX;
        else {
            sousEnsembles[parentY].parent = parentX;
            sousEnsembles[parentX].rang++;
        }
    }

    // Algorithme de Kruskal
    static void kruskal(List<Arete> aretes, int V, List<Arete> aretesFinaux ) {
        // Tri des arêtes par poids
        Collections.sort(aretes, Comparator.comparingInt(a -> a.poids));

        SousEnsemble[] sousEnsembles = new SousEnsemble[V];
        for (int i = 0; i < V; i++) {
            sousEnsembles[i] = new SousEnsemble();  //initialisation
            sousEnsembles[i].parent = i;           //des sous ensembles      
            sousEnsembles[i].rang = 0;
        }

        for (Arete arete : aretes) {
            int x = trouver(sousEnsembles, arete.source);
            int y = trouver(sousEnsembles, arete.destination);

            if (x != y) {
            	aretesFinaux .add(arete);
            	Liaison(sousEnsembles, x, y);
                if (aretesFinaux .size() == V - 1) break;
            }
        }
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Entrez le nombre de sommets : ");
        int V = scanner.nextInt();  // Nombre de sommets

        List<Arete> aretes = new ArrayList<>();
        Map<String, Integer> sommetIndex = new HashMap<>();
        int index = 0;

        // Saisie des sommets
        System.out.println("Entrez les noms des sommets ( A, B, C, ...)");
        for (int i = 0; i < V; i++) {
            System.out.print("Nom du sommet " + (i + 1) + ": ");
            String sommet = scanner.next();
            if (!sommetIndex.containsKey(sommet)) {  //conversion en entiers
                sommetIndex.put(sommet, index++);
            }
        }

        System.out.print("Entrez le nombre d'arêtes : ");
        int A = scanner.nextInt();

        // Saisie des arêtes
        for (int i = 0; i < A; i++) {
            System.out.print("Entrez l'arête " + (i + 1) + " (source destination poids) : ");
            String source = scanner.next();
            String destination = scanner.next();
            int poids = scanner.nextInt();

            int sourceIndex = sommetIndex.get(source);
            int destinationIndex = sommetIndex.get(destination);

            aretes.add(new Arete(sourceIndex, destinationIndex, poids));
        }

        List<Arete> aretesFinaux  = new ArrayList<>();
        kruskal(aretes, V, aretesFinaux );
  int s=0; // compteur pour cout minimale
        System.out.println("\nLes arêtes de l'arbre couvrant minimal sont :");
        for (Arete arete : aretesFinaux ) {
            String source = sommetIndex.entrySet()
                    .stream()
                    .filter(entry -> Objects.equals(entry.getValue(), arete.source))
                    .map(Map.Entry::getKey)
                    .findFirst()
                    .orElse("");
            String destination = sommetIndex.entrySet()
                    .stream()
                    .filter(entry -> Objects.equals(entry.getValue(), arete.destination))
                    .map(Map.Entry::getKey)
                    .findFirst()
                    .orElse("");
            System.out.println(source + " -- " + destination + " == " + arete.poids);
        s+=arete.poids;
        }
        System.out.println("Le Cout Totale est :  " +s);
    }
}
