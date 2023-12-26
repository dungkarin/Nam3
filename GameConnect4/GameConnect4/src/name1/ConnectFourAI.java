package name1;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.*;
import java.awt.Color;
import java.awt.Graphics;
import java.util.List;
import javax.swing.*;

public class ConnectFourAI extends JPanel{
    public static final int ROW_COUNT = 6;
    public static final int COLUMN_COUNT = 7;
    public static final int PLAYER = 0;
    public static final int AI = 1;
    public static final int PLAYER_PIECE = 1;
    public static final int AI_PIECE = 2;
    public static final int WINDOW_LENGTH = 4;
    public static final int EMPTY = 0;
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

    public static int count(int[] array, int t) {
        int count = 0;
        for (int i = 0; i < array.length; i++) {
            if (array[i] == t)
                count++;
        }
        return count;
    }
    public static int evaluate_window(int[] window, int piece) {
        int score = 0;
        int opp_piece = PLAYER_PIECE;
        if (piece == PLAYER_PIECE)
            opp_piece = AI_PIECE;
        if (count(window, piece) == 4) {
            score += 500001;
        } else if (count(window, piece) == 3 && count(window, EMPTY) == 1) {
            score += 5000;
        } else if (count(window, piece) == 2 && count(window, EMPTY) == 2) {
            score += 500;
        }
        if (count(window, opp_piece) == 2 && count(window, EMPTY) == 2) {
            score -= 501;
        }
        if (count(window, opp_piece) == 3 && count(window, EMPTY) == 1) {
            score -= 5001;
        }
        if (count(window, opp_piece) == 4) {
            score -= 50000;
        }
        return score;
    }
    public static int scorePosition(int[][] board, int piece) {
        int score = 0;
        //diem cot o giua
        int[] center_array = new int[ROW_COUNT];
        for (int i = 0; i < ROW_COUNT; i++) {
            center_array[i] = board[i][COLUMN_COUNT / 2];
        }
        int center_count = count(center_array, piece);
        score += center_count * 150;
        //diem ngang
        int[] row_array  = new int[COLUMN_COUNT];
        for (int r = 0; r < ROW_COUNT; r++) {
            System.arraycopy(board[r], 0, row_array , 0, COLUMN_COUNT);
            for (int c = 0; c < COLUMN_COUNT - 3; c++) {
                int[] window = Arrays.copyOfRange(row_array, c, c + WINDOW_LENGTH);
                score += evaluate_window(window, piece);
            }
        }
        //diem doc
        int[] col_array  = new int[ROW_COUNT];
        for (int c = 0; c < COLUMN_COUNT; c++) {
            for (int r = 0; r < ROW_COUNT; r++)
                col_array[r] = board[r][c];
            for (int r = 0; r < ROW_COUNT - 3; r++) {
                int[] window = Arrays.copyOfRange(col_array, r, r + WINDOW_LENGTH);
                score += evaluate_window(window, piece);
            }
        }
        //diem duong cheo duong
        for (int r = 0; r < ROW_COUNT - 3; r++) {
            for (int c = 0; c < COLUMN_COUNT - 3; c++) {
                int[] window = new int[WINDOW_LENGTH];
                for (int i = 0; i < WINDOW_LENGTH; i++) {
                    window[i] = board[r+i][c+i];
                }
                score += evaluate_window(window, piece);
            }
        }
        for (int r = 0; r < ROW_COUNT - 3; r++) {
            for (int c = 0; c < COLUMN_COUNT - 3; c++) {
                int[] window = new int[WINDOW_LENGTH];
                for (int i = 0; i < WINDOW_LENGTH; i++) {
                    window[i] = board[r+3-i][c+i];
                }
                score += evaluate_window(window, piece);
            }
        }
        return score;
    }

    public static boolean is_terminal_node(int[][] board) {
        return winning_move(board, PLAYER_PIECE) || winning_move(board, AI_PIECE) || get_valid_locations(board).size() == 0;
    }

    public static Integer[] min_max(int[][] board, int depth, double alpha, double beta, boolean maximizingPlayer) {
        List<Integer> valid_locations = get_valid_locations(board);
        boolean is_terminal = is_terminal_node(board);
        if (depth == 0 || is_terminal) {
            return new Integer[] { null, scorePosition(board, AI_PIECE)};
        }
        if (maximizingPlayer) {
            Integer value = (int) Double.NEGATIVE_INFINITY;
            Integer column = valid_locations.get(new Random().nextInt(valid_locations.size()));
            for (int col: valid_locations) {
                int row = get_next_open_row(board, col);
                int[][] temp_Board = new int[ROW_COUNT][COLUMN_COUNT];
                for (int i = 0; i < ROW_COUNT; i++) {
                    temp_Board[i] = Arrays.copyOf(board[i], COLUMN_COUNT);
                }
                drop_piece(temp_Board, row, col, AI_PIECE);
                int new_score = min_max(temp_Board, depth-1, alpha, beta, false)[1];
                if (new_score > value) {
                    value = new_score;
                    column = col;
                }
                alpha = Math.max(alpha, value);
                if (alpha >= beta)
                    break;
            }
            return new Integer[] {column, value};
        } else {
            Integer value = (int) Double.POSITIVE_INFINITY;
            Integer column = valid_locations.get(new Random().nextInt(valid_locations.size()));
            for (int col: valid_locations) {
                int row = get_next_open_row(board, col);
                int[][] temp_Board = new int[ROW_COUNT][COLUMN_COUNT];
                for (int i = 0; i < ROW_COUNT; i++) {
                    temp_Board[i] = Arrays.copyOf(board[i], COLUMN_COUNT);
                }
                drop_piece(temp_Board, row, col, PLAYER_PIECE);
                int new_score = min_max(temp_Board, depth-1, alpha, beta, true)[1];
                if (new_score < value) {
                    value = new_score;
                    column = col;
                }
                beta = Math.min(beta, value);
                if (alpha >= beta)
                    break;
            }
            return new Integer[] {column, value};
        }
    }

    public static List<Integer> get_valid_locations(int[][] board) {
        List<Integer> validLocations = new ArrayList<>();
        for (int col = 0; col < COLUMN_COUNT; col++) {
            if (is_valid_location(board, col)) {
                validLocations.add(col);
            }
        }
        return validLocations;
    }

//    public  static int pick_best_move(int[][] board, int piece) {
//        List<Integer> validLocations = get_valid_locations(board);
//        int best_Score = -10000;
//        int best_Col = validLocations.get(new Random().nextInt(validLocations.size()));
//        for (int col: validLocations) {
//            int row = get_next_open_row(board, col);
//            int[][] temp_Board = new int[ROW_COUNT][COLUMN_COUNT];
//            for (int i = 0; i < ROW_COUNT; i++) {
//                temp_Board[i] = Arrays.copyOf(board[i], COLUMN_COUNT);
//            }
//            drop_piece(temp_Board, row, col, piece);
//            int score = scorePosition(temp_Board, piece);
//            if (score > best_Score) {
//                best_Score = score;
//                best_Col = col;
//            }
//        }
//        return best_Col;
//    }

    public void run() {
        //Arrays.stream(board).forEach(row -> Arrays.fill(row, 0));
        addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                mouseX = e.getX();
                repaint();
                if (game_over) {
                    try {
                        Thread.sleep(3000); // Đợi 3000 miligiây (tương đương 3 giây)
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                    System.exit(0); // Đóng chương trình
                }
            }
        });
        addMouseListener(new MouseAdapter() {
            int col, row, posx, minmax_score;
            int[][] flip_board;
            @Override
            public void mouseClicked(MouseEvent e) {
                // Xử lý game khi nhấp chuột
                if (turn == PLAYER) {
                    posx = e.getX();
                    col = (int) Math.floor(posx / SQUARESIZE);
                    if (is_valid_location(board, col)) {
                        row = get_next_open_row(board, col);
                        drop_piece(board, row, col, PLAYER_PIECE);
                        if (winning_move(board, PLAYER_PIECE)) {
                            game_over = true;
                        }
                        turn += 1;
                        turn = turn % 2;
                        repaint();
                    }
                    SwingUtilities.invokeLater(() -> {
                        if (turn == AI && !game_over) {
                            //col = (int) (Math.random() * COLUMN_COUNT);
                            //col = pick_best_move(board, AI_PIECE);
                            Integer[] po = min_max(board,8, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, true);
                            col = po[0];
                            //minmax_score = po[1];
                            if (is_valid_location(board, col)) {
                                row = get_next_open_row(board, col);
                                drop_piece(board, row, col, AI_PIECE);
                                if (winning_move(board, AI_PIECE)) {
                                    repaint();
                                    game_over = true;
                                }
                                turn += 1;
                                turn = turn % 2;
                                repaint();
                            }
                        }
                    });
                }
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
                if (board[r][c] == PLAYER_PIECE) {
                    g.setColor(RED);
                    g.fillOval(c * SQUARESIZE + 5, HEI - (r * SQUARESIZE) + BACKSIZE, RADIUS, RADIUS);
                } else if (board[r][c] == AI_PIECE){
                    g.setColor(YELLOW);
                    g.fillOval(c * SQUARESIZE + 5, HEI - (r * SQUARESIZE) + BACKSIZE, RADIUS, RADIUS);
                }
            }
        }
        if (!game_over) {
            g.setColor(Color.BLACK);
            g.fillRect(0, 0, WIDTH, SQUARESIZE);
            if (turn == PLAYER) {
                g.setColor(Color.RED);
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
                g.drawString("You Lose!", 60, 60  + BACKSIZE);
            }
        }
    }

    public ConnectFourAI(CardLayout cardLayout, JPanel cardPanel) {
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
