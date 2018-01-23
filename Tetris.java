import java.awt.*;

import javax.swing.*;

public class Tetris extends JFrame {
    private JLabel statusBar;

    private Tetris() {
        statusBar = new JLabel("Lines: 0 - Nedim: 0 - Level: 0 - Score: 0");
        add(statusBar, BorderLayout.SOUTH);
        TetrisBoard board = new TetrisBoard(this);
        final Component add = add(board);
        NextPiecePanel nextPiecePanel;
        nextPiecePanel = new NextPiecePanel(board);
        add(nextPiecePanel, BorderLayout.NORTH);

        board.start();
        nextPiecePanel.start();

        setSize(300, 600);
        setPreferredSize(new Dimension(350, 700));
        setTitle("Nedim's Awesome Tetris");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    }

    public static void main(String[] args) {
        Tetris nedim = new Tetris();
        nedim.setLocationRelativeTo(null);
        nedim.setVisible(true);
        nedim.pack();
    }

    public JLabel getStatusBar() {
        return statusBar;
    }
}