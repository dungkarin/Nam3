package name1;

import javax.swing.*;
import java.awt.*;

public class RunGame {

    public static final int ROW_COUNT = 6;
    public static final int COLUMN_COUNT = 7;
    private static final int SQUARESIZE = 80;
    private static final int BACKSIZE = 35;
    private static final int WIDTH = COLUMN_COUNT * SQUARESIZE + SQUARESIZE / 4 - 6;
    private static final int HEIGHT = (ROW_COUNT + 1) * SQUARESIZE + SQUARESIZE / 2 - 2 + BACKSIZE;
    private static final String MENU_PANEL = "menu";
    private static final String AI_PANEL = "ai";
    private static final String HUMAN_PANEL = "human";
    private JFrame frame;
    private JPanel cardPanel;
    private CardLayout cardLayout;
    private ConnectFourAI ai;
    private ConnectFourHuman human;

    private void createAndShowGUI() {
        frame = new JFrame("Connect Four Game");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);

        JButton playWithHumanButton = new JButton("Chơi với người");
        playWithHumanButton.setBackground(Color.PINK);
        playWithHumanButton.setForeground(Color.WHITE);
        playWithHumanButton.setPreferredSize(new Dimension(250, 70));
        playWithHumanButton.setFont(new Font("Arial", Font.BOLD, 18));

        JButton playWithAIButton = new JButton("Chơi với máy");
        playWithAIButton.setBackground(Color.PINK);
        playWithAIButton.setForeground(Color.WHITE);
        playWithAIButton.setPreferredSize(new Dimension(250, 70));
        playWithAIButton.setFont(new Font("Arial", Font.BOLD, 18));

        JButton Out = new JButton("Thoát");
        Out.setBackground(Color.PINK);
        Out.setForeground(Color.WHITE);
        Out.setPreferredSize(new Dimension(250, 70));
        Out.setFont(new Font("Arial", Font.BOLD, 18));

        playWithHumanButton.addActionListener(e -> {
            cardLayout.show(cardPanel, HUMAN_PANEL);
            human.resetBoard();
            human.run();
        });

        playWithAIButton.addActionListener(e -> {
            cardLayout.show(cardPanel, AI_PANEL);
            ai.resetBoard();
            ai.run();
        });
        Out.addActionListener(e -> {
            System.exit(0);
        });

        JPanel menuPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(10, 10, 10, 10);
        menuPanel.add(playWithHumanButton, gbc);

        gbc.gridy = 1;
        menuPanel.add(playWithAIButton, gbc);
        
        gbc.gridy = 2;
        menuPanel.add(Out, gbc);

        menuPanel.setBackground(Color.BLACK);

        cardPanel.add(menuPanel, MENU_PANEL);
        ai = new ConnectFourAI(cardLayout, cardPanel);
        cardPanel.add(ai, AI_PANEL);

        human = new ConnectFourHuman(cardLayout, cardPanel);
        cardPanel.add(human, HUMAN_PANEL);

        frame.getContentPane().add(cardPanel);
        frame.setSize(WIDTH, HEIGHT);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new RunGame().createAndShowGUI());
    }
}