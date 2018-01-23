import java.awt.Color;
import java.awt.Graphics;
import java.util.Random;

public class Shape {
    enum Tetrominos {
        NoShape(new int[][] { { 0, 0 }, { 0, 0 }, { 0, 0 }, { 0, 0 } }),
        Z_BLOCK (new int[][] { { 0, -1 }, { 0, 0 }, { -1, 0 }, { -1, 1 } }),
        S_BLOCK (new int[][] { { 0, -1 }, { 0, 0 }, { 1, 0 }, { 1, 1 } }),
        I_BLOCK (new int[][] { { 0, -1 }, { 0, 0 }, { 0, 1 }, { 0, 2 } }),
        T_BLOCK (new int[][] { { -1, 0 }, { 0, 0 }, { 1, 0 }, { 0, 1 } }),
        L_BLOCK (new int[][] { { -1, -1 }, { 0, -1 }, { 0, 0 }, { 0, 1 } }),
        O_BLOCK(new int[][] { { 0, 0 }, { 1, 0 }, { 0, 1 }, { 1, 1 } }),
        J_BLOCK(new int[][] { { 1, -1 }, { 0, -1 }, { 0, 0 }, { 0, 1 } }
        );

        public int[][] coordinates;

        Tetrominos(int[][] tetraminoCoordinate) {
            this.coordinates = tetraminoCoordinate;
        }
    }

    private int[][] coordinates;

    private Tetrominos pieceShape;

    Shape() {
        coordinates = new int[4][2];
        setShape(Tetrominos.NoShape);
    }

    public static void drawSquare(Graphics g, int x, int y, TetrisBoard aBoard, Tetrominos shape) {
        Color color = TetrisBoard.COLORS[shape.ordinal()];
        g.setColor(color);
        g.fillRect(x + 1, y + 1, aBoard.squareWidth() - 2, aBoard.squareHeight() - 2);
    }

    public Tetrominos getShape() {
        return pieceShape;
    }

    public int minY() {
        int m = coordinates[0][1];

        for (int i = 0; i < 4; i++) {
            m = Math.min(m, coordinates[i][1]);
        }
        return m;
    }

    public Shape rotateLeft() {
        if (pieceShape == Tetrominos.O_BLOCK) {
            return this;
        }

        Shape result = new Shape();
        result.pieceShape = pieceShape;

        for (int i = 0; i < 4; i++) {
            result.setX(i, y(i));
            result.setY(i, -x(i));
        }
        return result;
    }

    public Shape rotateRight() {
        if (pieceShape == Tetrominos.O_BLOCK) {
            return this;
        }

        Shape result = new Shape();
        result.pieceShape = pieceShape;

        for (int i = 0; i < 4; i++) {
            result.setX(i, -y(i));
            result.setY(i, x(i));
        }
        return result;
    }

    public void setRandomShape() {
        Random r = new Random();

        int x;
        do x = Math.abs((r.nextInt() % 7) + 1); while (x <= 0);
        Tetrominos[] values = Tetrominos.values();
        setShape(values[x]);
    }

    public void setShape(Tetrominos aShape) {
        for (int i = 0; i < 4; i++) {
            System.arraycopy(aShape.coordinates[i], 0, coordinates[i], 0, 2);
        }

        pieceShape = aShape;
    }

    public int x(int aIndex) {
        return coordinates[aIndex][0];
    }

    public int y(int aIndex) {
        return coordinates[aIndex][1];
    }

    private void setX(int aIndex, int aX) {
        coordinates[aIndex][0] = aX;
    }

    private void setY(int aIndex, int aY) {
        coordinates[aIndex][1] = aY;
    }
}