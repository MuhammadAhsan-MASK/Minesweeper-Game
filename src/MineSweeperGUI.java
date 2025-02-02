import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Random;

public class MineSweeperGUI {
    private final int GRID_SIZE = 10; // 10x10 grid
    private final int NUM_MINES = 10;
    private JButton[][] buttons = new JButton[GRID_SIZE][GRID_SIZE];
    private int[][] fieldHidden = new int[GRID_SIZE][GRID_SIZE];
    private boolean[][] revealed = new boolean[GRID_SIZE][GRID_SIZE];
    private JFrame frame;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(MineSweeperGUI::new);
    }

    public MineSweeperGUI() {
        setupGame();
    }

    private void setupGame() {
        frame = new JFrame("Minesweeper");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        // Add Menu
        frame.setJMenuBar(createMenuBar());

        // Setup game board
        JPanel panel = new JPanel(new GridLayout(GRID_SIZE, GRID_SIZE));
        for (int i = 0; i < GRID_SIZE; i++) {
            for (int j = 0; j < GRID_SIZE; j++) {
                buttons[i][j] = new JButton();
                buttons[i][j].setFont(new Font("Arial", Font.BOLD, 14));
                buttons[i][j].setFocusPainted(false);
                buttons[i][j].setBackground(Color.LIGHT_GRAY);
                final int x = i, y = j;
                buttons[i][j].addActionListener(e -> handleLeftClick(x, y));
                buttons[i][j].addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        if (SwingUtilities.isRightMouseButton(e)) {
                            handleRightClick(x, y);
                        }
                    }
                });
                panel.add(buttons[i][j]);
            }
        }

        frame.add(panel, BorderLayout.CENTER);

        setupField();
        frame.setSize(600, 600);
        frame.setVisible(true);
    }

    private JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        JMenu gameMenu = new JMenu("Game");

        JMenuItem newGame = new JMenuItem("New Game");
        newGame.addActionListener(e -> resetGame());

        JMenuItem exitGame = new JMenuItem("Exit");
        exitGame.addActionListener(e -> System.exit(0));

        gameMenu.add(newGame);
        gameMenu.add(exitGame);
        menuBar.add(gameMenu);

        return menuBar;
    }

    private void setupField() {
        Random random = new Random();
        int minesPlaced = 0;

        // Place mines randomly
        while (minesPlaced < NUM_MINES) {
            int x = random.nextInt(GRID_SIZE);
            int y = random.nextInt(GRID_SIZE);
            if (fieldHidden[x][y] != 100) {
                fieldHidden[x][y] = 100;
                minesPlaced++;
            }
        }

        // Calculate numbers for non-mine cells
        for (int i = 0; i < GRID_SIZE; i++) {
            for (int j = 0; j < GRID_SIZE; j++) {
                if (fieldHidden[i][j] != 100) {
                    fieldHidden[i][j] = countAdjacentMines(i, j);
                }
            }
        }
    }

    private int countAdjacentMines(int x, int y) {
        int count = 0;
        for (int i = x - 1; i <= x + 1; i++) {
            for (int j = y - 1; j <= y + 1; j++) {
                if (i >= 0 && i < GRID_SIZE && j >= 0 && j < GRID_SIZE && fieldHidden[i][j] == 100) {
                    count++;
                }
            }
        }
        return count;
    }

    private void handleLeftClick(int x, int y) {
        if (revealed[x][y]) return;

        revealed[x][y] = true;

        if (fieldHidden[x][y] == 100) {
            revealMines();
            JOptionPane.showMessageDialog(frame, "You hit a mine! Game Over.");
            resetGame();
        } else if (fieldHidden[x][y] == 0) {
            revealEmptyCells(x, y);
        } else {
            buttons[x][y].setText(String.valueOf(fieldHidden[x][y]));
            buttons[x][y].setBackground(Color.WHITE);
        }

        if (checkWin()) {
            revealMines();
            JOptionPane.showMessageDialog(frame, "Congratulations! You win!");
            resetGame();
        }
    }

    private void handleRightClick(int x, int y) {
        if (revealed[x][y]) return;

        if (buttons[x][y].getText().equals("F")) {
            buttons[x][y].setText("");
            buttons[x][y].setBackground(Color.LIGHT_GRAY);
        } else {
            buttons[x][y].setText("F");
            buttons[x][y].setBackground(Color.YELLOW);
        }
    }

    private void revealMines() {
        for (int i = 0; i < GRID_SIZE; i++) {
            for (int j = 0; j < GRID_SIZE; j++) {
                if (fieldHidden[i][j] == 100) {
                    buttons[i][j].setText("X");
                    buttons[i][j].setBackground(Color.RED);
                }
            }
        }
    }

    private void revealEmptyCells(int x, int y) {
        if (x < 0 || x >= GRID_SIZE || y < 0 || y >= GRID_SIZE || revealed[x][y]) return;

        revealed[x][y] = true;
        buttons[x][y].setBackground(Color.WHITE);

        if (fieldHidden[x][y] > 0) {
            buttons[x][y].setText(String.valueOf(fieldHidden[x][y]));
            return;
        }

        // Recursively reveal neighbors
        for (int i = x - 1; i <= x + 1; i++) {
            for (int j = y - 1; j <= y + 1; j++) {
                if (i != x || j != y) {
                    revealEmptyCells(i, j);
                }
            }
        }
    }

    private boolean checkWin() {
        for (int i = 0; i < GRID_SIZE; i++) {
            for (int j = 0; j < GRID_SIZE; j++) {
                if (!revealed[i][j] && fieldHidden[i][j] != 100) {
                    return false;
                }
            }
        }
        return true;
    }

    private void resetGame() {
        frame.dispose();
        new MineSweeperGUI();
    }
}
