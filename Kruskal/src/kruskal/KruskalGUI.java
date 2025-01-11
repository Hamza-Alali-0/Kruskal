package kruskal;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.*;
import java.util.List;

public class KruskalGUI extends JFrame {
    private JPanel graphPanel;
    private JTextArea resultArea;
    private Map<String, Point> vertices = new HashMap<>();
    private List<Kruskal.Arete> edges = new ArrayList<>();
    private Map<String, Integer> vertexIndex = new HashMap<>();
    private List<Kruskal.Arete> mstEdges = new ArrayList<>();
    private int vertexCounter = 0;

    // Etat pour la sélection des sommets afin d'ajouter une arête
    private String selectedVertex = null;

    public KruskalGUI() {
        setTitle("Algorithme de Kruskal");
        setSize(900, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Panneau du graphe
        graphPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                drawGraph(g);
            }
        };
        graphPanel.setBackground(Color.WHITE);
        graphPanel.setPreferredSize(new Dimension(600, 600));
        graphPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                handleGraphClick(e.getPoint());
            }
        });

        // Panneau de contrôle (Modification du Layout)
        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new GridBagLayout());
        controlPanel.setBackground(new Color(245, 245, 245)); // Soft light gray background
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);  // Add padding around components
        gbc.anchor = GridBagConstraints.CENTER;

        // Boutons
        JButton runKruskalButton = new JButton("Exécuter");
        JButton reinitializeButton = new JButton("Réinitialiser");

        // Styling for buttons
        runKruskalButton.setBackground(new Color(0, 150, 136)); // Greenish
        runKruskalButton.setForeground(Color.WHITE);
        runKruskalButton.setFont(new Font("Arial", Font.BOLD, 14));
        runKruskalButton.setFocusPainted(false);
        runKruskalButton.setBorder(BorderFactory.createLineBorder(new Color(0, 150, 136), 2));
        runKruskalButton.setPreferredSize(new Dimension(150, 40));

        reinitializeButton.setBackground(new Color(255, 87, 34)); // Orange
        reinitializeButton.setForeground(Color.WHITE);
        reinitializeButton.setFont(new Font("Arial", Font.BOLD, 14));
        reinitializeButton.setFocusPainted(false);
        reinitializeButton.setBorder(BorderFactory.createLineBorder(new Color(255, 87, 34), 2));
        reinitializeButton.setPreferredSize(new Dimension(150, 40));

        // Add ActionListeners
        runKruskalButton.addActionListener(e -> runKruskalAlgorithm());
        reinitializeButton.addActionListener(e -> reinitializeGraph());

        // Result area styling
        resultArea = new JTextArea(5, 40);
        resultArea.setEditable(false);
        resultArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        resultArea.setBackground(new Color(240, 240, 240)); // Light gray
        resultArea.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        resultArea.setPreferredSize(new Dimension(100, 150));
        JScrollPane resultScrollPane = new JScrollPane(resultArea);

        // Initially hide the resultScrollPane
        resultScrollPane.setVisible(false);

        // Adding components with GridBagLayout
        gbc.gridx = 0;
        gbc.gridy = 0;
        controlPanel.add(runKruskalButton, gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        controlPanel.add(reinitializeButton, gbc);

        // Move this to the next row to ensure the text area is always below the buttons
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        controlPanel.add(resultScrollPane, gbc);

        // Ajouter les panneaux au layout principal
        add(graphPanel, BorderLayout.CENTER);
        add(controlPanel, BorderLayout.SOUTH);
        
        
     // Ajouter le bouton "Charger un graphe"
        JButton loadGraphButton = new JButton("Charger un graphe");

        // Styling pour le bouton "Charger un graphe"
        loadGraphButton.setBackground(new Color(33, 150, 243)); // Bleu
        loadGraphButton.setForeground(Color.WHITE);
        loadGraphButton.setFont(new Font("Arial", Font.BOLD, 14));
        loadGraphButton.setFocusPainted(false);
        loadGraphButton.setBorder(BorderFactory.createLineBorder(new Color(33, 150, 243), 2));
        loadGraphButton.setPreferredSize(new Dimension(150, 40));

        // Ajoutez un actionListener pour le bouton
        loadGraphButton.addActionListener(e -> loadGraphFromFile());

        // Ajouter ce bouton dans le panneau de contrôle avec GridBagLayout
        gbc.gridx = 2; // Positionner le bouton à droite des autres
        gbc.gridy = 0;
        controlPanel.add(loadGraphButton, gbc);

    }

    private void handleGraphClick(Point point) {
        // Vérifier si le point cliqué est proche d'un sommet existant
        String clickedVertex = getVertexAtPoint(point);

        if (clickedVertex != null) {
            if (selectedVertex == null) {
                // Premier sommet sélectionné
                selectedVertex = clickedVertex;
                JOptionPane.showMessageDialog(this, "Sommet : " +" { "+ selectedVertex + " } "+" est choisi.");
            } else {
                // Deuxième sommet sélectionné, ajouter une arête
                String secondVertex = clickedVertex;

                if (selectedVertex.equals(secondVertex)) {
                    JOptionPane.showMessageDialog(this, "On ne peut pas créer une arête entre le même sommet.");
                } else {
                    String weightStr = JOptionPane.showInputDialog(this, "Entrer le poids de l'arête : ");
                    try {
                        int weight = Integer.parseInt(weightStr);
                        int sourceIndex = vertexIndex.get(selectedVertex);
                        int destinationIndex = vertexIndex.get(secondVertex);
                        edges.add(new Kruskal.Arete(sourceIndex, destinationIndex, weight));
                        JOptionPane.showMessageDialog(this, "Arête ajoutée :  " + selectedVertex + " ---  " + secondVertex);
                        graphPanel.repaint();
                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(this, "Poids invalide.");
                    }
                }

                // Réinitialiser l'état
                selectedVertex = null;
            }
        } else {
            // Ajouter un nouveau sommet
            addVertex(point);
        }
    }

    private void addVertex(Point point) {
        String vertexName = JOptionPane.showInputDialog(this, "Entrez le nom du sommet :");
        if (vertexName != null && !vertexName.isEmpty() && !vertices.containsKey(vertexName)) {
            vertices.put(vertexName, point);
            vertexIndex.put(vertexName, vertexCounter++);
            graphPanel.repaint();
        } else {
            JOptionPane.showMessageDialog(this, "Nom invalide ou sommet déjà existant.");
        }
    }

    private String getVertexAtPoint(Point point) {
        for (Map.Entry<String, Point> entry : vertices.entrySet()) {
            Point vertexPoint = entry.getValue();
            if (point.distance(vertexPoint) <= 10) { // Rayon de sélection
                return entry.getKey();
            }
        }
        return null;
    }

    private void runKruskalAlgorithm() {
        if (vertices.size() < 2 || edges.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Le graphe est incomplet.");
            return;
        }

        mstEdges.clear();
        Kruskal.kruskal(edges, vertices.size(), mstEdges);

        int totalWeight = 0;
        StringBuilder result = new StringBuilder("Les arêtes sont :\n");
        for (Kruskal.Arete edge : mstEdges) {
            String source = getVertexByIndex(edge.source);
            String destination = getVertexByIndex(edge.destination);
            result.append(source).append(" - ").append(destination).append(" : ").append(edge.poids).append("\n");
            totalWeight += edge.poids;
        }
        result.append("Coût total : ").append(totalWeight);
        resultArea.setText(result.toString());

        // Show the result area
        JScrollPane resultScrollPane = (JScrollPane) ((JPanel) getContentPane().getComponent(1)).getComponent(2);
        resultScrollPane.setVisible(true);

        graphPanel.repaint();
    }

    private void reinitializeGraph() {
        vertices.clear();
        edges.clear();
        mstEdges.clear();
        vertexIndex.clear();
        vertexCounter = 0;
        selectedVertex = null;
        resultArea.setText("");

        // Hide the result area
        JScrollPane resultScrollPane = (JScrollPane) ((JPanel) getContentPane().getComponent(1)).getComponent(2);
        resultScrollPane.setVisible(false);

        graphPanel.repaint();
    }

    private void drawGraph(Graphics g) {
        // Dessiner toutes les arêtes
        for (Kruskal.Arete edge : edges) {
            Point sourcePoint = getPointByIndex(edge.source);
            Point destPoint = getPointByIndex(edge.destination);
            g.setColor(mstEdges.contains(edge) ? Color.GREEN : Color.BLACK);
            g.drawLine(sourcePoint.x, sourcePoint.y, destPoint.x, destPoint.y);
            g.setColor(Color.BLACK);
            int midX = (sourcePoint.x + destPoint.x) / 2;
            int midY = (sourcePoint.y + destPoint.y) / 2;
            g.drawString(String.valueOf(edge.poids), midX, midY);
        }

        // Dessiner tous les sommets
        for (Map.Entry<String, Point> entry : vertices.entrySet()) {
            Point point = entry.getValue();
            g.setColor(Color.CYAN);
            g.fillOval(point.x - 10, point.y - 10, 20, 20);
            g.setColor(Color.BLACK);
            g.drawString(entry.getKey(), point.x - 5, point.y + 5);
        }
    }

    private Point getPointByIndex(int index) {
        for (Map.Entry<String, Integer> entry : vertexIndex.entrySet()) {
            if (entry.getValue() == index) {
                return vertices.get(entry.getKey());
            }
        }
        return null;
    }

    private String getVertexByIndex(int index) {
        for (Map.Entry<String, Integer> entry : vertexIndex.entrySet()) {
            if (entry.getValue() == index) {
                return entry.getKey();
            }
        }
        return null;
    }
    
    private void loadGraphFromFile() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Choisissez un fichier de graphe");

        // Ouvrir la boîte de dialogue pour choisir le fichier
        int result = fileChooser.showOpenDialog(this);

        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();

            try (Scanner scanner = new Scanner(selectedFile)) {
                // Réinitialiser le graphe actuel
                vertices.clear();
                edges.clear();
                vertexIndex.clear();
                vertexCounter = 0;
                selectedVertex = null;

                // Lire chaque ligne du fichier
                while (scanner.hasNextLine()) {
                    String line = scanner.nextLine();
                    String[] parts = line.split(" ");
                    if (parts.length == 3) {
                        String source = parts[0];
                        String destination = parts[1];
                        int weight = Integer.parseInt(parts[2]);

                        // Ajouter les sommets et arêtes
                        if (!vertexIndex.containsKey(source)) {
                            vertices.put(source, new Point(50 + vertexCounter * 60, 50)); // Position arbitraire
                            vertexIndex.put(source, vertexCounter++);
                        }
                        if (!vertexIndex.containsKey(destination)) {
                            vertices.put(destination, new Point(50 + vertexCounter * 60, 150)); // Position arbitraire
                            vertexIndex.put(destination, vertexCounter++);
                        }

                        edges.add(new Kruskal.Arete(vertexIndex.get(source), vertexIndex.get(destination), weight));
                    }
                }

                // Re-dessiner le graphe après le chargement
                graphPanel.repaint();
                JOptionPane.showMessageDialog(this, "Graphe chargé avec succès.");
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Erreur lors du chargement du fichier.");
            }
        }
    }

    

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            KruskalGUI frame = new KruskalGUI();
            frame.setVisible(true);
        });
    }
}
