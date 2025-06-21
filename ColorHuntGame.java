import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class ColorHuntGame {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(GamePanel::new);
    }
}

class GamePanel extends JFrame {
    private JLabel colorLabel;
    private JButton[] colorButtons;
    private JLabel scoreLabel;
    private JLabel timerLabel;
    private int score = 0;
    private int timeRemaining = 60;
    private String currentColor;
    private Timer gameTimer;

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
            "Welcome to the Color Hunt Game!\n" +
            "Instructions:\n" +
            "- Match the displayed text (not the color of the text) with the correct button.\n" +
            "- Gain 1 point for correct answers.\n" +
            "- Lose 2 points for incorrect answers.\n" +
            "- The score will not go below 0.\n" +
            "- You have 60 seconds. Try to score as high as possible!\n\n" +
            "Good luck!",
            "Game Instructions",
            JOptionPane.INFORMATION_MESSAGE
        );
    }

    private void initializeUI() {
        JLabel header = new JLabel("Color Hunt Game - Match the Color!", SwingConstants.CENTER);
        header.setFont(new Font("Arial", Font.BOLD, 36));
        header.setOpaque(true);
        header.setBackground(new Color(60, 120, 200));
        header.setForeground(Color.WHITE);
        header.setPreferredSize(new Dimension(getWidth(), 100));
        add(header, BorderLayout.NORTH);

        colorLabel = new JLabel("COLOR", SwingConstants.CENTER);
        colorLabel.setFont(new Font("Arial", Font.BOLD, 100));
        colorLabel.setOpaque(true);
        colorLabel.setPreferredSize(new Dimension(getWidth(), 250));
        colorLabel.setBackground(Color.WHITE);
        colorLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 3));
        add(colorLabel, BorderLayout.CENTER);

        JPanel footer = new JPanel(new BorderLayout());
        footer.setBackground(new Color(230, 240, 255));

        timerLabel = new JLabel("Time: 60", SwingConstants.LEFT);
        timerLabel.setFont(new Font("Arial", Font.BOLD, 24));
        timerLabel.setForeground(new Color(200, 50, 50));
        footer.add(timerLabel, BorderLayout.WEST);

        scoreLabel = new JLabel("Score: 0", SwingConstants.RIGHT);
        scoreLabel.setFont(new Font("Arial", Font.BOLD, 24));
        scoreLabel.setForeground(new Color(60, 120, 200));
        footer.add(scoreLabel, BorderLayout.EAST);

        JPanel buttonPanel = new JPanel(new GridLayout(3, 4, 20, 20));
        buttonPanel.setBackground(new Color(200, 230, 250));
        String[] colors = {"Red", "Green", "Blue", "Yellow", "Orange", "Purple", "Cyan", "Pink", "Brown", "Gray", "Teal", "Magenta"};
        colorButtons = new JButton[colors.length];

        for (int i = 0; i < colors.length; i++) {
            colorButtons[i] = new JButton(colors[i]);
            colorButtons[i].setFont(new Font("Arial", Font.BOLD, 24));
            colorButtons[i].setPreferredSize(new Dimension(200, 80));
            colorButtons[i].setBackground(getColorFromString(colors[i]));
            colorButtons[i].setForeground(Color.WHITE);
            colorButtons[i].setFocusPainted(false);
            colorButtons[i].setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
            colorButtons[i].addActionListener(e -> {
                animateButton(e.getSource());
                checkAnswer(e.getActionCommand());
            });
            buttonPanel.add(colorButtons[i]);
        }

        footer.add(buttonPanel, BorderLayout.SOUTH);
        add(footer, BorderLayout.SOUTH);
    }

    private void startGame() {
        gameTimer = new Timer(1000, e -> {
            timeRemaining--;
            timerLabel.setText("Time: " + timeRemaining);

            if (timeRemaining > 40) {
                timerLabel.setForeground(new Color(0, 128, 0));
            } else if (timeRemaining > 20) {
                timerLabel.setForeground(new Color(255, 165, 0));
            } else {
                timerLabel.setForeground(new Color(200, 50, 50));
            }

            if (timeRemaining <= 0) {
                gameTimer.stop();
                gameOver();
            }
        });
        gameTimer.start();
        updateColor();
    }

    private void updateColor() {
        String[] colors = {"Red", "Green", "Blue", "Yellow", "Orange", "Purple", "Cyan", "Pink", "Brown", "Gray", "Teal", "Magenta"};
        int randomIndex = (int) (Math.random() * colors.length);
        currentColor = colors[randomIndex];
        colorLabel.setText(currentColor);

        new Thread(() -> {
            Color fromColor = colorLabel.getForeground();
            Color toColor = getColorFromString(colors[(int) (Math.random() * colors.length)]);
            for (int i = 0; i <= 10; i++) {
                int r = (int) (fromColor.getRed() + (toColor.getRed() - fromColor.getRed()) * i / 10.0);
                int g = (int) (fromColor.getGreen() + (toColor.getGreen() - fromColor.getGreen()) * i / 10.0);
                int b = (int) (fromColor.getBlue() + (toColor.getBlue() - fromColor.getBlue()) * i / 10.0);
                colorLabel.setForeground(new Color(r, g, b));
                try { Thread.sleep(30); } catch (InterruptedException e) {}
            }
        }).start();
    }

    private void animateButton(Object source) {
        JButton button = (JButton) source;
        int originalWidth = button.getWidth();
        int originalHeight = button.getHeight();
        Timer bounceTimer = new Timer(50, new AbstractAction() {
            int scale = 10;
            public void actionPerformed(ActionEvent e) {
                button.setPreferredSize(new Dimension(originalWidth + scale, originalHeight + scale));
                button.revalidate();
                scale -= 2;
                if (scale == 0) {
                    ((Timer) e.getSource()).stop();
                    button.setPreferredSize(new Dimension(originalWidth, originalHeight));
                    button.revalidate();
                }
            }
        });
        bounceTimer.start();
    }

    private void checkAnswer(String chosenColor) {
        if (chosenColor.equalsIgnoreCase(currentColor)) {
            score++;
        } else if (score > 1) {
            score -= 2;
        }
        scoreLabel.setText("Score: " + Math.max(score, 0));
        updateColor();
    }

    private void gameOver() {
        int option = JOptionPane.showOptionDialog(
            this,
            "Game Over! Your Score: " + score + "\nDo you want to play again?",
            "Game Over",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.INFORMATION_MESSAGE,
            null,
            new String[]{"Restart", "Exit"},
            "Restart"
        );
        if (option == JOptionPane.YES_OPTION) {
            score = 0;
            timeRemaining = 60;
            scoreLabel.setText("Score: 0");
            timerLabel.setText("Time: 60");
            updateColor();
            startGame();
        } else {
            System.exit(0);
        }
    }

    private Color getColorFromString(String colorName) {
        switch (colorName.toLowerCase()) {
            case "red": return Color.RED;
            case "green": return Color.GREEN;
            case "blue": return Color.BLUE;
            case "yellow": return Color.YELLOW;
            case "orange": return Color.ORANGE;
            case "purple": return new Color(128, 0, 128);
            case "cyan": return Color.CYAN;
            case "pink": return Color.PINK;
            case "brown": return new Color(139, 69, 19);
            case "gray": return Color.GRAY;
            case "teal": return new Color(0, 128, 128);
            case "magenta": return Color.MAGENTA;
            default: return Color.BLACK;
        }
    }
}
