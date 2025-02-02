import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;

public class MineSweeperGUI extends JFrame {
    private static final int GRID_SIZE = 10;
    private static final int MINE = 100;
    private JButton[][] buttons = new JButton[GRID_SIZE][GRID_SIZE];
    private int[][] fieldHidden = new int[GRID_SIZE][GRID_SIZE];
    private boolean[][] revealed = new boolean[GRID_SIZE][GRID_SIZE];

    public MineSweeperGUI() {
        setTitle("Minesweeper");
        setSize(600, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new GridLayout(GRID_SIZE, GRID_SIZE));
        initializeField();
        initializeButtons();
    }

    private void initializeField() {
        Random random = new Random();
        int minesPlaced = 0;

        while (minesPlaced < 10) { // Place 10 mines
            int i = random.nextInt(GRID_SIZE);
            int j = random.nextInt(GRID_SIZE);
            if (fieldHidden[i][j] != MINE) {
                fieldHidden[i][j] = MINE;
                minesPlaced++;
            }
        }

        for (int i = 0; i < GRID_SIZE; i++) {
            for (int j = 0; j < GRID_SIZE; j++) {
                if (fieldHidden[i][j] != MINE) {
                    fieldHidden[i][j] = countAdjacentMines(i, j);
                }
            }
        }
    }

    private int countAdjacentMines(int x, int y) {
        int count = 0;
        for (int i = x - 1; i <= x + 1; i++) {
            for (int j = y - 1; j <= y + 1; j++) {
                if (i >= 0 && i < GRID_SIZE && j >= 0 && j < GRID_SIZE && fieldHidden[i][j] == MINE) {
                    count++;
                }
            }
        }
        return count;
    }

    private void initializeButtons() {
        for (int i = 0; i < GRID_SIZE; i++) {
            for (int j = 0; j < GRID_SIZE; j++) {
                buttons[i][j] = new JButton();
                buttons[i][j].setFont(new Font("Arial", Font.BOLD, 16));
                buttons[i][j].addActionListener(new ButtonClickListener(i, j));
                add(buttons[i][j]);
            }
        }
    }

    private class ButtonClickListener implements ActionListener {
        private int x, y;

        public ButtonClickListener(int x, int y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (revealed[x][y]) return;
            revealed[x][y] = true;

            if (fieldHidden[x][y] == MINE) {
                revealAllMines();
                JOptionPane.showMessageDialog(null, "Game Over! You hit a mine!");
                resetGame();
            } else if (fieldHidden[x][y] == 0) {
                revealEmptyCells(x, y);
            } else {
                buttons[x][y].setText(String.valueOf(fieldHidden[x][y]));
                buttons[x][y].setEnabled(false);
            }

            if (checkWin()) {
                JOptionPane.showMessageDialog(null, "Congratulations! You won!");
                resetGame();
            }
        }
    }

    private void revealEmptyCells(int x, int y) {
        if (x < 0 || x >= GRID_SIZE || y < 0 || y >= GRID_SIZE || revealed[x][y] || fieldHidden[x][y] == MINE) {
            return;
        }

        revealed[x][y] = true;
        buttons[x][y].setText(fieldHidden[x][y] == 0 ? "" : String.valueOf(fieldHidden[x][y]));
        buttons[x][y].setEnabled(false);

        if (fieldHidden[x][y] == 0) {
            for (int i = x - 1; i <= x + 1; i++) {
                for (int j = y - 1; j <= y + 1; j++) {
                    revealEmptyCells(i, j);
                }
            }
        }
    }

    private void revealAllMines() {
        for (int i = 0; i < GRID_SIZE; i++) {
            for (int j = 0; j < GRID_SIZE; j++) {
                if (fieldHidden[i][j] == MINE) {
                    buttons[i][j].setText("X");
                    buttons[i][j].setEnabled(false);
                }
            }
        }
    }

    private boolean checkWin() {
        for (int i = 0; i < GRID_SIZE; i++) {
            for (int j = 0; j < GRID_SIZE; j++) {
                if (fieldHidden[i][j] != MINE && !revealed[i][j]) {
                    return false;
                }
            }
        }
        return true;
    }

    private void resetGame() {
        dispose();
        new MineSweeperGUI().setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new MineSweeperGUI().setVisible(true);
        });
    }
}
