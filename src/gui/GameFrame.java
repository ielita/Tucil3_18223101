package gui;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import models.Map;
import models.SlidingState;
import algorithms.Pathfinder;

public class GameFrame extends JFrame {
    private Map map;
    private List<SlidingState> solutionPath;
    private int currStep = 0;

    private MatrixPanel matrixPanel;
    private JLabel lblStatus;
    private JComboBox<String> comboAlgo;

    private Timer playbackTimer;
    private int playbackSpeed = 50;

    public GameFrame() {
        setTitle("Sleding Es");
        setSize(900, 700);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton btnLoad = new JButton("Open .txt Map");
        comboAlgo = new JComboBox<>(new String[]{"A*", "UCS", "GBFS"});
        JButton btnSolve = new JButton("Run Algo");
        btnSolve.setBackground(new Color(40, 26, 103, 50));

        topPanel.add(btnLoad);
        topPanel.add(new JLabel(" Algorithm: "));
        topPanel.add(comboAlgo);
        topPanel.add(btnSolve);
        add(topPanel, BorderLayout.NORTH);

        JPanel centerWrapper = new JPanel(new GridBagLayout());
        matrixPanel = new MatrixPanel();
        centerWrapper.add(matrixPanel);
        add(centerWrapper, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new BorderLayout());
        lblStatus = new JLabel(" Status: Waiting for map...");
        lblStatus.setFont(new Font("Monospaced", Font.BOLD, 12));
        lblStatus.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        
        bottomPanel.add(lblStatus, BorderLayout.NORTH);
        setupPlaybackControls(bottomPanel);
        add(bottomPanel, BorderLayout.SOUTH);

        btnLoad.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser("src/case");
            if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                map = new Map();
                if (map.loadFromFile(chooser.getSelectedFile().getAbsolutePath())) {
                    matrixPanel.setMap(map);
                    solutionPath = null;
                    currStep = 0;
                    lblStatus.setText(" Status: Map Loaded (" + map.n + "x" + map.m + ")");
                    matrixPanel.revalidate();
                    matrixPanel.repaint();
                }
            }
        });

        btnSolve.addActionListener(e -> {
            if (map == null) {
                JOptionPane.showMessageDialog(this, "Load map dulu!");
                return;
            }

            Pathfinder solver = new Pathfinder();
            String selectedAlgo = (String) comboAlgo.getSelectedItem();
            
            long startTime = System.currentTimeMillis();
            solutionPath = solver.solve(map, selectedAlgo);
            long endTime = System.currentTimeMillis();
            long executionTime = endTime - startTime;

            if (solutionPath != null) {
                currStep = 0;
                updateDisplay();

                int totalIterations = solver.getIterations();
                int totalCost = solutionPath.get(solutionPath.size() - 1).totalACost;
                int totalSteps = solutionPath.size() - 1;
                
                String detailMessage = String.format(
                    "Path Found! ^-^)/\n\n" +
                    "Algorithm (%s)\n" +
                    "Execution Time : %d ms\n" +
                    "Iterations : %d iterations\n" +
                    "Path Length  : %d steps\n" +
                    "Total Cost      : %d\n\n" +
                    "Save results to file .txt?",
                    selectedAlgo, executionTime, totalIterations, totalSteps, totalCost
                );
                int confirm = JOptionPane.showConfirmDialog(this, 
                    detailMessage,
                    "Success - " + selectedAlgo,
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.INFORMATION_MESSAGE);

                if (confirm == JOptionPane.YES_NO_OPTION) {
                    String outputFileName = JOptionPane.showInputDialog(this, "Insert file name:");
                    if (outputFileName != null && !outputFileName.trim().isEmpty()) {
                        solver.saveFullReport(outputFileName, selectedAlgo, executionTime, solutionPath, map);
                        JOptionPane.showMessageDialog(this, "File saved at path: test/" + outputFileName + ".txt");
                    }
                }
            } else {
                JOptionPane.showMessageDialog(this, "Solution not found for " + selectedAlgo);
            }
        });
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        }
        catch (Exception ignored) {}
        
        SwingUtilities.invokeLater(() -> {
            new GameFrame().setVisible(true);
        });
    }

    private void setupPlaybackControls(JPanel container) {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton btnPrev = new JButton("<< Prev");
        JButton btnNext = new JButton(">> Next");
        JButton btnReset = new JButton("Reset");
        JButton btnPlay = new JButton("Play");
        JButton btnJump = new JButton("Jump");
        buttonPanel.add(btnPrev);
        buttonPanel.add(btnPlay);
        buttonPanel.add(btnNext);
        buttonPanel.add(btnReset);
        buttonPanel.add(btnJump);

        playbackTimer = new Timer(playbackSpeed, e -> {
            if (solutionPath != null && currStep < solutionPath.size() - 1) {
                currStep++;
                updateDisplay();
            } else {
                playbackTimer.stop();
                btnPlay.setText("Play");
            }
        });

        JPanel speedPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JLabel lblSpeed = new JLabel("Playback Speed: ");

        String[] speedOptions = {"0.5x", "1.0x", "2.0x"};
        JComboBox<String> comboSpeed = new JComboBox<>(speedOptions);
        comboSpeed.setSelectedIndex(1);

        comboSpeed.addActionListener(e -> {
            String selected = (String) comboSpeed.getSelectedItem();
            int baseDelay = 500;
            
            switch (selected) {
                case "0.5x": playbackSpeed = baseDelay * 2; break;
                case "1.0x": playbackSpeed = baseDelay; break;
                case "2.0x": playbackSpeed = baseDelay / 2; break;
            }
            
            if (playbackTimer != null) {
                playbackTimer.setDelay(playbackSpeed);
            }
        });

        speedPanel.add(lblSpeed);
        speedPanel.add(comboSpeed);

        JPanel playbackWrapper = new JPanel();
        playbackWrapper.setLayout(new BoxLayout(playbackWrapper, BoxLayout.Y_AXIS));
        playbackWrapper.add(buttonPanel);
        playbackWrapper.add(speedPanel);

        btnPlay.addActionListener(e -> {
            if (playbackTimer.isRunning()) {
                playbackTimer.stop();
                btnPlay.setText("Play");
            }
            else {
                playbackTimer.start();
                btnPlay.setText("Stop");
            }
        });

        btnReset.addActionListener(e -> {
            if (!playbackTimer.isRunning()) {
                currStep = 0;
                updateDisplay();
            }
        });

        btnNext.addActionListener(e -> {
            if (solutionPath != null && currStep < solutionPath.size() - 1) {
                currStep++;
                updateDisplay();
            }
        });

        btnPrev.addActionListener(e -> {
            if (solutionPath != null && currStep > 0) {
                currStep--;
                updateDisplay();
            }
        });

        btnJump.addActionListener(e -> {
            String input = JOptionPane.showInputDialog(this, "Lompat ke step (0-" + (solutionPath.size() - 1) + "):");
            try {
                int target = Integer.parseInt(input);
                if (target >= 0 && target < solutionPath.size()) {
                    currStep = target;
                    updateDisplay();
                }
            }
            catch (Exception e2) {}
        });

        container.add(playbackWrapper, BorderLayout.CENTER);
    }

    private void updateDisplay() {
        SlidingState state = solutionPath.get(currStep);
        matrixPanel.setCurrentState(state);
        String selectedAlgo = (String) comboAlgo.getSelectedItem();
        String info = String.format(
            "<html><div style='padding: 5px;'>" +
            "<b>Path Found! ^-^)/</b><br><br>" +
            "Algorithm (%s)<br>" +
            "Step: %d / %d<br>" +
            "Path Length  : %d steps<br>" +
            "Total Cost    : %d" +
            "</div></html>",
            selectedAlgo,
            currStep, 
            solutionPath.size() - 1,
            currStep,
            state.totalACost
        );
        lblStatus.setText(info);
        matrixPanel.repaint();
    }
}