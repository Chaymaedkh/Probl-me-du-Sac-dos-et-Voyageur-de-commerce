import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

public class PSOTemplate {
    private JFrame frame;
    private JTextField fileInput;
    private JComboBox<String> problemTypeComboBox;
    private JTextField c1Input;
    private JTextField c2Input;
    private JTextField inertiaWeightInput;
    private JTextField numIterationsInput;
    private JTextField numParticlesInput;
    private JTextField numCitiesInput;
    private JTextField vMaxInput;
    private JTextField numPrecisionInput;
    private JTextArea outputArea;
    private JFileChooser fileChooser;
    private JPanel middlePanel;

    public PSOTemplate() {
        frame = new JFrame("PSO Template");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        // Top panel for file input and problem type selection
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new FlowLayout());

        JLabel fileLabel = new JLabel("Fichier :");
        fileInput = new JTextField(20);
        fileInput.setEditable(false);
        JButton browseButton = new JButton("Parcourir");
        problemTypeComboBox = new JComboBox<>(new String[]{"Voyage du commerce", "Sac à Dos", "Problème quelconque"});

        topPanel.add(fileLabel);
        topPanel.add(fileInput);
        topPanel.add(browseButton);
        topPanel.add(problemTypeComboBox);

        fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));

        browseButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int returnValue = fileChooser.showOpenDialog(frame);
                if (returnValue == JFileChooser.APPROVE_OPTION) {
                    File selectedFile = fileChooser.getSelectedFile();
                    fileInput.setText(selectedFile.getAbsolutePath());
                }
            }
        });

        problemTypeComboBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String selectedProblemType = (String) problemTypeComboBox.getSelectedItem();
                if (selectedProblemType.equals("Voyage du commerce")) {
                    // Show components for "Voyage du commerce"
                    numCitiesInput.setVisible(true);
                    numParticlesInput.setVisible(true);
                    numIterationsInput.setVisible(true);
                    c1Input.setVisible(true);
                    c2Input.setVisible(true);
                    inertiaWeightInput.setVisible(true);
                    vMaxInput.setVisible(true);
                    numPrecisionInput.setVisible(true);
                } else if (selectedProblemType.equals("Sac à Dos")) {
                    frame.dispose();
                    new PSIOTemplate_Sac_à_Dos();
                } else {
                    // Hide components for other problem types
                    numCitiesInput.setVisible(false);
                    numParticlesInput.setVisible(false);
                    numIterationsInput.setVisible(false);
                    c1Input.setVisible(false);
                    c2Input.setVisible(false);
                    inertiaWeightInput.setVisible(false);
                    vMaxInput.setVisible(false);
                    numPrecisionInput.setVisible(false);
                }
            }
        });

        // Middle panel for PSO parameters
        middlePanel = new JPanel();
        middlePanel.setLayout(new GridLayout(0, 2));
        middlePanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel c1Label = new JLabel("C1 :");
        c1Input = new JTextField(10);
        c1Input.setVisible(false);
        JLabel c2Label = new JLabel("C2 :");
        c2Input = new JTextField(10);
        c2Input.setVisible(false);
        JLabel inertiaWeightLabel = new JLabel("Poids d'inertie :");
        inertiaWeightInput = new JTextField(10);
        inertiaWeightInput.setVisible(false);
        JLabel numIterationsLabel = new JLabel("Nombre d'itérations :");
        numIterationsInput = new JTextField(10);
        numIterationsInput.setVisible(false);
        JLabel numParticlesLabel = new JLabel("Nombre de particules :");
        numParticlesInput = new JTextField(10);
        numParticlesInput.setVisible(false);
        JLabel numCitiesLabel = new JLabel("Nombre de villes :");
        numCitiesInput = new JTextField(10);
        numCitiesInput.setVisible(false);
        JLabel vMaxLabel = new JLabel("Vitesse maximale :");
        vMaxInput = new JTextField(10);
        vMaxInput.setVisible(false);
        JLabel numPrecisionLabel = new JLabel("Précision :");
        numPrecisionInput = new JTextField(10);
        numPrecisionInput.setVisible(true);

        middlePanel.add(c1Label);
        middlePanel.add(c1Input);
        middlePanel.add(c2Label);
        middlePanel.add(c2Input);
        middlePanel.add(inertiaWeightLabel);
        middlePanel.add(inertiaWeightInput);
        middlePanel.add(numIterationsLabel);
        middlePanel.add(numIterationsInput);
        middlePanel.add(numParticlesLabel);
        middlePanel.add(numParticlesInput);
        middlePanel.add(numCitiesLabel);
        middlePanel.add(numCitiesInput);
        middlePanel.add(vMaxLabel);
        middlePanel.add(vMaxInput);
        middlePanel.add(numPrecisionLabel);
        middlePanel.add(numPrecisionInput);

        middlePanel.add(new JLabel());
        JButton trouverButton = new JButton("Trouver");
        trouverButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (validateInputs()) {
                    runPSOAlgorithm();
                } else {
                    JOptionPane.showMessageDialog(frame, "Veuillez saisir des valeurs valides pour tous les champs.");
                }
            }
        });
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(trouverButton);
        middlePanel.add(buttonPanel);

        // Bottom panel for output
        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new BorderLayout());

        JLabel outputLabel = new JLabel("Sortie :");
        outputArea = new JTextArea(10, 40);
        outputArea.setEditable(false);

        bottomPanel.add(outputLabel, BorderLayout.NORTH);
        bottomPanel.add(new JScrollPane(outputArea), BorderLayout.CENTER);

        frame.add(topPanel, BorderLayout.NORTH);
        frame.add(middlePanel, BorderLayout.CENTER);
        frame.add(bottomPanel, BorderLayout.SOUTH);

        frame.pack();
        frame.setVisible(true);

        problemTypeComboBox.setSelectedIndex(0);
    }

    private boolean validateInputs() {
        if (c1Input.isVisible() && c1Input.getText().isEmpty()) return false;
        if (c2Input.isVisible() && c2Input.getText().isEmpty()) return false;
        if (inertiaWeightInput.isVisible() && inertiaWeightInput.getText().isEmpty()) return false;
        if (numIterationsInput.isVisible() && numIterationsInput.getText().isEmpty()) return false;
        if (numParticlesInput.isVisible() && numParticlesInput.getText().isEmpty()) return false;
        if (numCitiesInput.isVisible() && numCitiesInput.getText().isEmpty()) return false;
        if (vMaxInput.isVisible() && vMaxInput.getText().isEmpty()) return false;
        if (numPrecisionInput.isVisible() && numPrecisionInput.getText().isEmpty()) return false;
        return true;
    }

    private void updateOutput(String result) {
        outputArea.setText(result);
    }

    private void runPSOAlgorithm() {
        double c1 = Double.parseDouble(c1Input.getText());
        double c2 = Double.parseDouble(c2Input.getText());
        double inertiaWeight = Double.parseDouble(inertiaWeightInput.getText());
        int numIterations = Integer.parseInt(numIterationsInput.getText());
        int numParticles = Integer.parseInt(numParticlesInput.getText());
        int numCities = Integer.parseInt(numCitiesInput.getText());
        double vMax = Double.parseDouble(vMaxInput.getText());
        int numPrecision = Integer.parseInt(numPrecisionInput.getText());

        String result = PSO_TSP.runAlgorithm(c1, c2, inertiaWeight, numIterations, numParticles, numCities, vMax, numPrecision, fileInput.getText());
        updateOutput(result);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new PSOTemplate();
            }
        });
    }
}
