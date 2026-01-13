import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.Random;

public class ColorHuntGame {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(GamePanel::new);
    }
}

class GamePanel extends JFrame {

    private JLabel colorLabel, scoreLabel, timerLabel;
    private JButton[] colorButtons;
    private int score = 0;
    private int timeRemaining = 60;
    private String currentColorText;
    private Timer gameTimer;

    private final String[] colors = {
            "Red", "Green", "Blue", "Yellow", "Orange",
            "Purple", "Cyan", "Pink", "Brown", "Gray",
            "Teal", "Magenta"
    };

    public GamePanel() {
        setTitle("Color Hunt Game");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        showInstructions();
        initializeUI();
        startGame();

        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setVisible(true);
    }

    private void showInstructions() {
        JOptionPane.showMessageDialog(
                this,
                "Match the TEXT, not the COLOR!\n\n" +
                "+1 for correct\n-2 for wrong\n60 seconds",
                "Instructions",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    private void initializeUI() {

        JLabel header = new JLabel("Color Hunt Game - Match the Color!", SwingConstants.CENTER);
        header.setFont(new Font("Arial", Font.BOLD, 36));
        header.setOpaque(true);
        header.setBackground(new Color(60, 120, 200));
        header.setForeground(Color.WHITE);
        header.setPreferredSize(new Dimension(100, 100));
        add(header, BorderLayout.NORTH);

        colorLabel = new JLabel("COLOR", SwingConstants.CENTER);
        colorLabel.setFont(new Font("Arial", Font.BOLD, 100));
        colorLabel.setOpaque(true);
        colorLabel.setBackground(Color.WHITE);
        colorLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 3));
        add(colorLabel, BorderLayout.CENTER);

        JPanel footer = new JPanel(new BorderLayout());
        footer.setBackground(new Color(230, 240, 255));

        timerLabel = new JLabel("Time: 60");
        timerLabel.setFont(new Font("Arial", Font.BOLD, 24));
        footer.add(timerLabel, BorderLayout.WEST);

        scoreLabel = new JLabel("Score: 0");
        scoreLabel.setFont(new Font("Arial", Font.BOLD, 24));
        footer.add(scoreLabel, BorderLayout.EAST);

        JPanel buttonPanel = new JPanel(new GridLayout(3, 4, 20, 20));
        buttonPanel.setBackground(new Color(200, 230, 250));

        colorButtons = new JButton[colors.length];

        for (int i = 0; i < colors.length; i++) {
            JButton btn = new JButton(colors[i]);
            btn.setFont(new Font("Arial", Font.BOLD, 24));
            btn.setBackground(getColorFromString(colors[i]));
            btn.setForeground(Color.WHITE);
            btn.setFocusPainted(false);
            btn.addActionListener(e -> {
                animateButton(btn);
                checkAnswer(btn.getText());
            });
            colorButtons[i] = btn;
            buttonPanel.add(btn);
        }

        footer.add(buttonPanel, BorderLayout.SOUTH);
        add(footer, BorderLayout.SOUTH);
    }

    private void startGame() {
        gameTimer = new Timer(1000, e -> {
            timeRemaining--;
            timerLabel.setText("Time: " + timeRemaining);

            if (timeRemaining <= 0) {
                gameTimer.stop();
                gameOver();
            }
        });
        gameTimer.start();
        updateColor();
    }

    // ⭐ FIXED METHOD ⭐
    private void updateColor() {
        Random random = new Random();

        int textIndex = random.nextInt(colors.length);
        int colorIndex;

        do {
            colorIndex = random.nextInt(colors.length);
        } while (colorIndex == textIndex); // ENSURE mismatch

        currentColorText = colors[textIndex];
        colorLabel.setText(currentColorText);
        colorLabel.setForeground(getColorFromString(colors[colorIndex]));
    }

    private void checkAnswer(String chosenColor) {
        if (chosenColor.equalsIgnoreCase(currentColorText)) {
            score++;
        } else {
            score = Math.max(0, score - 2);
        }
        scoreLabel.setText("Score: " + score);
        updateColor();
    }

    private void animateButton(JButton button) {
        int w = button.getWidth();
        int h = button.getHeight();

        Timer t = new Timer(20, new AbstractAction() {
            int s = 10;
            public void actionPerformed(ActionEvent e) {
                button.setPreferredSize(new Dimension(w + s, h + s));
                button.revalidate();
                s -= 2;
                if (s <= 0) {
                    ((Timer) e.getSource()).stop();
                    button.setPreferredSize(new Dimension(w, h));
                    button.revalidate();
                }
            }
        });
        t.start();
    }

    private void gameOver() {
        int option = JOptionPane.showConfirmDialog(
                this,
                "Game Over!\nScore: " + score + "\nPlay again?",
                "Game Over",
                JOptionPane.YES_NO_OPTION
        );
        if (option == JOptionPane.YES_OPTION) {
            score = 0;
            timeRemaining = 60;
            scoreLabel.setText("Score: 0");
            timerLabel.setText("Time: 60");
            startGame();
        } else {
            System.exit(0);
        }
    }

   private Color getColorFromString(String name) {
    switch (name.toLowerCase()) {
        case "red":
            return Color.RED;
        case "green":
            return Color.GREEN;
        case "blue":
            return Color.BLUE;
        case "yellow":
            return Color.YELLOW;
        case "orange":
            return Color.ORANGE;
        case "purple":
            return new Color(128, 0, 128);
        case "cyan":
            return Color.CYAN;
        case "pink":
            return Color.PINK;
        case "brown":
            return new Color(139, 69, 19);
        case "gray":
            return Color.GRAY;
        case "teal":
            return new Color(0, 128, 128);
        case "magenta":
            return Color.MAGENTA;
        default:
            return Color.BLACK;
    }
    }
}

