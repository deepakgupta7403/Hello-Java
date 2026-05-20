package Basics.Multithreading.SnakeGame;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

/**
 * SnakeGame — main runner.
 *
 * Demonstrates the canonical "GUI + simulation" thread split:
 *
 *   - Swing's EVENT-DISPATCH THREAD (EDT) builds the JFrame, owns the
 *     SnakeBoard, dispatches input, paints frames.
 *   - A SEPARATE GAME-LOOP THREAD ticks the simulation at a steady rate
 *     and asks the EDT to repaint via SwingUtilities.invokeLater.
 *   - The shared GameState is guarded by a ReentrantLock so the two
 *     threads never tear each other's view of the world.
 *
 *
 * Lifecycle
 * ---------
 *   1. main() invokeAndWait's onto the EDT to build the UI.
 *   2. Once the JFrame is visible, main starts the GameLoop thread.
 *   3. On window close, main stops the loop and joins the thread.
 *
 *
 * Tweakables
 * ----------
 *   - TICK_MS is the simulation period (lower = harder).
 */
public final class SnakeGame {

    private static final long TICK_MS = 110;

    public static void main(String[] args) throws Exception {
        // 1. Build the UI on the EDT.
        GameState state = new GameState();
        SnakeBoard board = new SnakeBoard(state);
        JFrame[] frameHolder = new JFrame[1];

        SwingUtilities.invokeAndWait(() -> {
            JFrame f = new JFrame("Snake — multithreading demo");
            f.add(board);
            f.pack();
            f.setLocationRelativeTo(null);
            f.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
            f.setVisible(true);
            board.requestFocusInWindow();
            frameHolder[0] = f;
        });

        // 2. Spin the simulation thread.
        GameLoop loop = new GameLoop(state, board, TICK_MS);
        Thread loopThread = new Thread(loop, "snake-game-loop");
        loopThread.setDaemon(false);              // don't let the JVM kill it mid-frame
        loopThread.start();

        // 3. Stop the loop cleanly when the window closes.
        frameHolder[0].addWindowListener(new java.awt.event.WindowAdapter() {
            @Override public void windowClosed(java.awt.event.WindowEvent e) {
                loop.stop();
                loopThread.interrupt();
                try { loopThread.join(); } catch (InterruptedException ignored) {}
            }
        });

        // Reset hook so the board can refocus after R is pressed.
        board.setOnReset(board::requestFocusInWindow);
    }
}
