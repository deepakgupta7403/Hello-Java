package Phase7_Concurrency.Multithreading.SnakeGame;

import javax.swing.SwingUtilities;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * GameLoop — the simulation thread.
 *
 * Runs on its OWN thread (not the EDT). Sleeps between ticks to control
 * speed, then advances the game state and asks the Swing renderer to
 * repaint via SwingUtilities.invokeLater. (Swing components must be
 * touched on the EDT.)
 *
 *
 * Why a dedicated thread?
 * -----------------------
 *   - The EDT must stay responsive to input and repaint.
 *   - The simulation should tick at a steady rate independent of how
 *     long painting takes.
 *
 *
 * Coordination
 * ------------
 *   - state.tick() takes the GameState lock briefly.
 *   - state.snapshot() (called from the EDT) takes the same lock.
 *     They never overlap painting and ticking. No torn frames.
 *
 *   - A volatile `running` flag controls the loop; setRunning(false)
 *     stops the loop and the thread exits.
 *
 *
 * Frame-pacing
 * ------------
 * For simplicity this uses Thread.sleep(tickMs). A production game
 * would track wall-clock drift and skip ticks if behind. The pattern
 * is the same; the math is more.
 */
public final class GameLoop implements Runnable {

    private final GameState state;
    private final SnakeBoard board;
    private final AtomicBoolean running = new AtomicBoolean(true);
    private volatile long tickMs;

    public GameLoop(GameState state, SnakeBoard board, long initialTickMs) {
        this.state  = state;
        this.board  = board;
        this.tickMs = initialTickMs;
    }

    public void setTickMs(long ms) { this.tickMs = ms; }
    public void stop() { running.set(false); }

    @Override public void run() {
        while (running.get() && !Thread.currentThread().isInterrupted()) {
            long t0 = System.currentTimeMillis();
            state.tick();

            // Request a repaint on the EDT. This does NOT paint on this
            // thread; it asks Swing to do it soon.
            SwingUtilities.invokeLater(board::repaint);

            // Sleep the rest of the frame.
            long elapsed = System.currentTimeMillis() - t0;
            long sleep   = Math.max(0, tickMs - elapsed);
            try { Thread.sleep(sleep); }
            catch (InterruptedException ie) {
                Thread.currentThread().interrupt();      // honour the signal
                return;
            }
        }
    }
}
