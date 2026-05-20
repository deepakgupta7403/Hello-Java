package Basics.Multithreading.SnakeGame;

import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/**
 * SnakeBoard — the rendering component.
 *
 * Lives on the SWING EVENT-DISPATCH THREAD (EDT). All painting happens
 * on the EDT, courtesy of the AWT framework. Key events arrive on the
 * EDT and we hand them to GameState via setDirection — that call is
 * thread-safe because GameState itself synchronizes.
 *
 *
 * Threading contract
 * ------------------
 *   - paintComponent(g) reads a state snapshot ONCE under the lock,
 *     then paints — no chance of seeing a half-modified snake.
 *   - keyPressed forwards to GameState.setDirection / GameState.reset.
 *
 *
 * Look & feel
 * -----------
 *   16x16 cell tiles, score in the top-left, "GAME OVER" overlay when
 *   appropriate.
 */
public final class SnakeBoard extends JPanel {

    public static final int CELL = 22;
    private final GameState state;
    private Runnable onReset = () -> {};      // injected by main runner

    public SnakeBoard(GameState state) {
        this.state = state;
        setPreferredSize(new Dimension(GameState.COLS * CELL, GameState.ROWS * CELL));
        setBackground(new Color(20, 24, 28));
        setFocusable(true);
        addKeyListener(new KeyAdapter() {
            @Override public void keyPressed(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_UP, KeyEvent.VK_W    -> state.setDirection(Direction.UP);
                    case KeyEvent.VK_DOWN, KeyEvent.VK_S  -> state.setDirection(Direction.DOWN);
                    case KeyEvent.VK_LEFT, KeyEvent.VK_A  -> state.setDirection(Direction.LEFT);
                    case KeyEvent.VK_RIGHT, KeyEvent.VK_D -> state.setDirection(Direction.RIGHT);
                    case KeyEvent.VK_R                    -> { state.reset(); onReset.run(); }
                }
            }
        });
    }

    public void setOnReset(Runnable r) { this.onReset = r; }

    @Override protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        // Single atomic read.
        GameState.Snapshot snap = state.snapshot();

        // Subtle grid
        g2.setColor(new Color(28, 34, 40));
        for (int x = 0; x < GameState.COLS; x++)
            for (int y = 0; y < GameState.ROWS; y++)
                g2.drawRect(x * CELL, y * CELL, CELL, CELL);

        // Food
        if (snap.food() != null) {
            g2.setColor(new Color(220, 80, 80));
            g2.fillRoundRect(snap.food().x() * CELL + 3, snap.food().y() * CELL + 3,
                    CELL - 6, CELL - 6, 8, 8);
        }

        // Snake
        Cell[] body = snap.body();
        for (int i = 0; i < body.length; i++) {
            Cell c = body[i];
            // Head a touch brighter than body.
            g2.setColor(i == 0 ? new Color(120, 220, 140) : new Color(70, 170, 100));
            g2.fillRoundRect(c.x() * CELL + 1, c.y() * CELL + 1, CELL - 2, CELL - 2, 6, 6);
        }

        // HUD
        g2.setColor(new Color(220, 220, 220));
        g2.setFont(g2.getFont().deriveFont(Font.BOLD, 14f));
        g2.drawString("score " + snap.score(), 8, 18);
        g2.drawString("[WASD / arrows]  R = restart", 8, getHeight() - 8);

        if (snap.gameOver()) {
            g2.setColor(new Color(0, 0, 0, 170));
            g2.fillRect(0, 0, getWidth(), getHeight());
            g2.setColor(Color.WHITE);
            g2.setFont(g2.getFont().deriveFont(Font.BOLD, 28f));
            String msg = "GAME OVER";
            int w = g2.getFontMetrics().stringWidth(msg);
            g2.drawString(msg, (getWidth() - w) / 2, getHeight() / 2 - 10);
            g2.setFont(g2.getFont().deriveFont(Font.PLAIN, 14f));
            String s2 = "score " + snap.score() + "   —   press R";
            int w2 = g2.getFontMetrics().stringWidth(s2);
            g2.drawString(s2, (getWidth() - w2) / 2, getHeight() / 2 + 16);
        }
    }
}
