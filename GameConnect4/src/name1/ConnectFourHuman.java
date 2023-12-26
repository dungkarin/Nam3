package name1;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.Color;
import java.awt.Graphics;
import javax.swing.*;

public class ConnectFourHuman extends JPanel{
    public static final int ROW_COUNT = 6;
    public static final int COLUMN_COUNT = 7;
    private static final Color BLUE = new Color(0, 0, 255);
    private static final Color BLACK = new Color(0, 0, 0);
    private static final Color RED = new Color(255, 0, 0);
    private static final Color YELLOW = new Color(255, 255, 0);
    private static final int SQUARESIZE = 80;
    private static final int BACKSIZE = 35;
    private static final int RADIUS = SQUARESIZE-10;
    private static final int WIDTH = COLUMN_COUNT * SQUARESIZE + SQUARESIZE/4 - 6;
    private static final int HEI = (ROW_COUNT) * SQUARESIZE + SQUARESIZE/4 - 15;
    private int turn;
    private boolean game_over;
    private int mouseX;
    private int[][] board;
    private CardLayout cardLayout;
    private JPanel cardPanel;
    public void resetBoard() {
        turn = 0;
        game_over = false;
        board = create_board();
    }
    public static int[][] create_board() {
        int[][] board = new int[ROW_COUNT][COLUMN_COUNT];
        for (int i = 0; i < ROW_COUNT; i++) {
            for (int j = 0; j < COLUMN_COUNT; j++) {
                board[i][j] = 0;
            }
        }
        return board;
    }

    public static void drop_piece(int[][] board, int row, int col, int piece) {
        board[row][col] = piece;
    }

    public static boolean is_valid_location(int[][] board, int col) {
        return board[ROW_COUNT-1][col] == 0;
    }

    public static int get_next_open_row(int[][] board, int col) {
        for (int i = 0; i < ROW_COUNT; i++) {
            if (board[i][col] == 0) {
                return i;
            }
        }
        return 0;
    }

    public static boolean winning_move(int[][] board, int piece) {
        //check ngang
        for (int c = 0; c < COLUMN_COUNT-3; c++) {
            for (int r = 0; r < ROW_COUNT; r++) {
                if (board[r][c] == piece && board[r][c+1] == piece && board[r][c+2] == piece && board[r][c+3] == piece) {
                    return true;
                }
            }
        }
        //check doc
        for (int c = 0; c < COLUMN_COUNT; c++) {
            for (int r = 0; r < ROW_COUNT-3; r++) {
                if (board[r][c] == piece && board[r+1][c] == piece && board[r+2][c] == piece && board[r+3][c] == piece) {
                    return true;
                }
            }
        }
        //check cheo di len
        for (int c = 0; c < COLUMN_COUNT-3; c++) {
            for (int r = 0; r < ROW_COUNT-3; r++) {
                if (board[r][c] == piece && board[r+1][c+1] == piece && board[r+2][c+2] == piece && board[r+3][c+3] == piece) {
                    return true;
                }
            }
        }
        //check cheo di xuong
        for (int c = 0; c < COLUMN_COUNT-3; c++) {
            for (int r = 3; r < ROW_COUNT; r++) {
                if (board[r][c] == piece && board[r-1][c+1] == piece && board[r-2][c+2] == piece && board[r-3][c+3] == piece) {
                    return true;
                }
            }
        }
        return false;
    }

    public void run() {
        addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                mouseX = e.getX();
                repaint();
                if (game_over) {
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                    System.exit(0);
                }
            }
        });
        addMouseListener(new MouseAdapter() {
            int col, row;
            @Override
            public void mouseClicked(MouseEvent e) {
                // Xử lý game khi nhấp chuột
                if (turn == 0) {
                    //System.out.println("Player 1 Make your selection (0-6):");
                    int posx = e.getX();
                    col = (int) Math.floor(posx / SQUARESIZE);
                    if (is_valid_location(board, col)) {
                        row = get_next_open_row(board, col);
                        drop_piece(board, row, col, 1);
                        if (winning_move(board, 1)) {
                            game_over = true;
                        }
                    }
                } else {
                    int posx = e.getX();
                    col = (int) Math.floor(posx / SQUARESIZE);
                    if (is_valid_location(board, col)) {
                        row = get_next_open_row(board, col);
                        drop_piece(board, row, col, 2);
                        if (winning_move(board, 2)) {
                            game_over = true;
                        }
                    }
                }
                repaint();
                turn += 1;
                turn = turn % 2;
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        setBackground(BLACK);
        for (int c = 0; c < COLUMN_COUNT; c++) {
            for (int r = 0; r < ROW_COUNT; r++) {
                g.setColor(BLUE);
                g.fillRect(c * SQUARESIZE, r * SQUARESIZE + SQUARESIZE + BACKSIZE, SQUARESIZE, SQUARESIZE);
                g.setColor(BLACK);
                g.fillOval(c * SQUARESIZE + 5, r * SQUARESIZE + SQUARESIZE + 5 + BACKSIZE, RADIUS, RADIUS);
            }
        }
        for (int c = 0; c < COLUMN_COUNT; c++) {
            for (int r = 0; r < ROW_COUNT; r++) {
                if (board[r][c] == 1) {
                    g.setColor(RED);
                    g.fillOval(c * SQUARESIZE + 5, HEI - (r * SQUARESIZE) + BACKSIZE, RADIUS, RADIUS);
                } else if (board[r][c] == 2){
                    g.setColor(YELLOW);
                    g.fillOval(c * SQUARESIZE + 5, HEI - (r * SQUARESIZE) + BACKSIZE, RADIUS, RADIUS);
                }
            }
        }
        if (!game_over) {
            g.setColor(Color.BLACK);
            g.fillRect(0, 0, WIDTH, SQUARESIZE);
            if (turn == 0) {
                g.setColor(Color.RED);
                g.fillOval(mouseX - RADIUS/2, 5 / 2 + BACKSIZE, RADIUS, RADIUS);
            } else {
                g.setColor(Color.YELLOW);
                g.fillOval(mouseX - RADIUS/2, 5 / 2 + BACKSIZE, RADIUS, RADIUS);
            }
        } else {
            Font myFont = new Font("monospace", Font.PLAIN, 60);
            g.setFont(myFont);
            if (turn == 1) {
                g.setColor(Color.RED);
                g.drawString("Player 1 wins!!!", 60, 60  + BACKSIZE);
            } else {
                g.setColor(Color.YELLOW);
                g.drawString("Player 2 wins!!!", 60, 60  + BACKSIZE);
            }
        }
    }
    public ConnectFourHuman(CardLayout cardLayout, JPanel cardPanel) {
        this.cardLayout = cardLayout;
        this.cardPanel = cardPanel;

        JButton backButton = new JButton("Quay lại");
        backButton.addActionListener(e -> {
            cardLayout.show(cardPanel, "menu");
        });
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.add(backButton);
        buttonPanel.setBackground(BLACK);
        setLayout(new BorderLayout());
        add(buttonPanel, BorderLayout.NORTH);
        turn = 0;
        game_over = false;
        board = create_board();
    }
}
