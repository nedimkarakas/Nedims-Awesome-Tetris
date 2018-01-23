import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;



public class TetrisBoard extends JPanel implements ActionListener {
    private static final int BOARD_WIDTH = 10;
    private static final int BOARD_HEIGHT = 22;

    private static final int SCORE_SINGLE = 40;
    private static final int SCORE_DOUBLE = 100;
    private static final int SCORE_TRIPLE = 300;
    private static final int SCORE_TETRIS = 1200;

    public static final Color[] COLORS = { new Color(0, 0, 0), //
            new Color(204, 102, 102), new Color(102, 204, 102), //
            new Color(102, 102, 204), new Color(204, 204, 102), //
            new Color(204, 102, 204), new Color(102, 204, 204), //
            new Color(218, 0, 183) };


    class TetrisAdapter extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            if (!isStarted || (curPiece.getShape() == Shape.Tetrominos.NoShape)) {
                return;
            }

            int keyCode = e.getKeyCode();

            if ((keyCode == 'p') || (keyCode == 'P')) {
                pause();
            }

            if (isPaused) {
                return;
            }

            switch (keyCode) {
                case KeyEvent.VK_LEFT:
                    tryMove(curPiece, curX - 1, curY);
                    break;
                case KeyEvent.VK_RIGHT:
                    tryMove(curPiece, curX + 1, curY);
                    break;
                case KeyEvent.VK_DOWN:
                    tryMove(curPiece.rotateRight(), curX, curY);
                    break;
                case KeyEvent.VK_UP:
                    tryMove(curPiece.rotateLeft(), curX, curY);
                    break;
                case KeyEvent.VK_SPACE:
                    dropDown();
                    break;
                case 'd':
                    oneLineDown();
                    break;
                case 'D':
                    oneLineDown();
                    break;
                default:
                    break;
            }
        }
    }

    private boolean isFallingFinished = false;
    private boolean isPaused = false;
    private boolean isStarted = false;
    private int curX = 0;
    private int curY = 0;
    private int level = 0;

    private int numLinesRemoved = 0;

    private JLabel statusBar;
    private long score = 1;
    private Shape curPiece;
    private Shape nextPiece;
    private Shape.Tetrominos[] board;
    private Timer timer;

    TetrisBoard(Tetris aParent) {
        setFocusable(true);
        nextPiece = new Shape();
        nextPiece.setRandomShape();
        curPiece = new Shape();
        timer = new Timer(600, this);
        statusBar = aParent.getStatusBar();
        board = new Shape.Tetrominos[BOARD_WIDTH * BOARD_HEIGHT];
        clearBoard();
        addKeyListener(new TetrisAdapter());
    }

    @Override
    public void actionPerformed(ActionEvent aEvent) {
        if (isFallingFinished) {
            isFallingFinished = false;
            newPiece();
        } else {
            oneLineDown();
        }
    }

    private void clearBoard() {
        for (int i = 0; i < (BOARD_HEIGHT * BOARD_WIDTH); i++) {
            board[i] = Shape.Tetrominos.NoShape;
        }
    }

    public Shape getNextPiece() {
        return this.nextPiece;
    }

    private void newPiece() {
        curPiece = new Shape();
        curPiece.setShape(nextPiece.getShape());
        nextPiece.setRandomShape();
        curX = (BOARD_WIDTH / 2) + 1;
        curY = (BOARD_HEIGHT - 1) + curPiece.minY();

        if (!tryMove(curPiece, curX, curY - 1)) {
            curPiece.setShape(Shape.Tetrominos.NoShape);
            timer.stop();
            isStarted = false;
            settextStatusBar("GAME OVER - ");
        }
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);

        g.setColor(new Color(51, 51, 51));
        g.fillRect(0, 0, BOARD_WIDTH * squareWidth(), (BOARD_HEIGHT + 1) * squareHeight());

        Dimension size = getSize();

        int boardTop = (int) size.getHeight() - (BOARD_HEIGHT * squareHeight());

        for (int i = 0; i < BOARD_HEIGHT; i++) {
            for (int j = 0; j < BOARD_WIDTH; ++j) {
                Shape.Tetrominos shape = shapeAt(j, BOARD_HEIGHT - i - 1);

                if (shape != Shape.Tetrominos.NoShape) {
                    Shape.drawSquare(g, j * squareWidth(), boardTop + (i * squareHeight()), this, shape);
                }
            }
        }

        if (curPiece.getShape() != Shape.Tetrominos.NoShape) {
            for (int i = 0; i < 4; ++i) {
                int x = curX + curPiece.x(i);
                int y = curY - curPiece.y(i);
                Shape.drawSquare(g, x * squareWidth(), boardTop + ((BOARD_HEIGHT - y - 1) * squareHeight()), this,
                        curPiece.getShape());
            }
        }
    }

    private void pause() {
        if (!isStarted) {
            return;
        }

        isPaused = !isPaused;

        if (isPaused) {
            timer.stop();
            statusBar.setText("Paused");
        } else {
            timer.start();
            settextStatusBar("");
        }

        repaint();
    }

    private Shape.Tetrominos shapeAt(int aX, int aY) {
        return board[(aY * BOARD_WIDTH) + aX];
    }

    public int squareHeight() {
        return (int) getSize().getHeight() / BOARD_HEIGHT;
    }

    public int squareWidth() {
        return (int) getSize().getWidth() / BOARD_WIDTH;
    }

    public void start() {
        if (isPaused) {
            return;
        }

        isStarted = true;
        isFallingFinished = false;
        numLinesRemoved = 0;
        score = 0;
        level = 0;
        clearBoard();
        newPiece();
        timer.start();
    }

    private void dropDown() {
        int newY = curY;

        while (newY > 0) {
            if (!tryMove(curPiece, curX, newY - 1)) {
                break;
            }
            --newY;
        }

        pieceDropped();
    }

    private void oneLineDown() {
        if (!tryMove(curPiece, curX, curY - 1)) {
            pieceDropped();
        }
    }

    private void pieceDropped() {
        for (int i = 0; i < 4; i++) {
            int x = curX + curPiece.x(i);
            int y = curY - curPiece.y(i);
            board[(y * BOARD_WIDTH) + x] = curPiece.getShape();
        }

        removeFullLines();

        if (!isFallingFinished) {
            newPiece();
        }
    }

    private void removeFullLines() {
        int numFullLines = 0;

        for (int i = BOARD_HEIGHT - 1; i >= 0; --i) {
            boolean lineIsFull = true;

            for (int j = 0; j < BOARD_WIDTH; ++j) {
                if (shapeAt(j, i) == Shape.Tetrominos.NoShape) {
                    lineIsFull = false;
                    break;
                }
            }

            if (lineIsFull) {
                ++numFullLines;
                for (int k = i; k < (BOARD_HEIGHT - 1); ++k) {
                    for (int j = 0; j < BOARD_WIDTH; ++j) {
                        board[(k * BOARD_WIDTH) + j] = shapeAt(j, k + 1);
                    }
                }
            }
        }

        if (numFullLines >= 4) {
            score += (level + 1) * SCORE_TETRIS;
        } else if (numFullLines >= 3) {
            score += (level + 1) * SCORE_TRIPLE;
        } else if (numFullLines >= 2) {
            score += (level + 1) * SCORE_DOUBLE;
        } else if (numFullLines >= 1) {
            score += (level + 1) * SCORE_SINGLE;
        }

        if (numFullLines > 0) {
            numLinesRemoved += numFullLines;
            int oldLevel = level;
            level = Math.floorDiv(numLinesRemoved, 10);
            if (oldLevel != level) {
                timer.setDelay(timer.getDelay() - 50);
            }
            settextStatusBar("");
            isFallingFinished = true;
            curPiece.setShape(Shape.Tetrominos.NoShape);
            repaint();
        }
    }

    private void settextStatusBar(String aText) {
        statusBar.setText(
                aText + "Lines: " + String.valueOf(numLinesRemoved)
                        + " - Level: " + String.valueOf(level) + " - Score: " + String.valueOf(score));
    }

    private boolean tryMove(Shape newPiece, int newX, int newY) {
        for (int i = 0; i < 4; ++i) {
            int x = newX + newPiece.x(i);
            int y = newY - newPiece.y(i);

            if ((x < 0) || (x >= BOARD_WIDTH) || (y < 0) || (y >= BOARD_HEIGHT)) {
                return false;
            }

            if (shapeAt(x, y) != Shape.Tetrominos.NoShape) {
                return false;
            }
        }

        curPiece = newPiece;
        curX = newX;
        curY = newY;

        repaint();

        return true;
    }
}