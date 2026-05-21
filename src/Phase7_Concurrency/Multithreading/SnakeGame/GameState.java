package Phase7_Concurrency.Multithreading.SnakeGame;

import java.util.*;
import java.util.concurrent.locks.ReentrantLock;

/**
 * GameState — the shared mutable state, accessed by:
 * <p>
 *
 *   - GAME LOOP THREAD  (writes: ticks the snake, updates food, score, gameOver)
 *   - SWING EDT          (reads: paints the board)
 *   - SWING EDT          (writes via setDirection: keyboard input)
 * <p>
 *
 * All mutating and reading operations go through a SINGLE ReentrantLock
 * so the EDT never paints a half-modified state. Critical sections are
 * SHORT — never call back into Swing while holding the lock.
 * <p>
 *
 * Why ReentrantLock instead of synchronized
 * -----------------------------------------
 *   1. Multiple condition variables possible if we extend the game
 *      (e.g. a paused/unpaused signal).
 *   2. Easier to reason about lock ordering vs the EDT.
 *   3. Demonstration purposes — see ReentrantLockDemo for depth.
 * <p>
 *
 * Snake representation
 * --------------------
 *   - body: Deque<Cell> — head is first(), tail is last().
 *   - bodySet: Set<Cell> — same cells, for O(1) self-collision check.
 *   - direction: current direction; pendingDirection: requested by input.
 * <p>
 *
 *   On each tick the loop adopts pendingDirection (unless it's the
 *   opposite of `direction`), moves the head, optionally trims the tail.
 */
public final class GameState {

    public static final int COLS = 25;
    public static final int ROWS = 25;

    private final ReentrantLock lock = new ReentrantLock();
    private final Random rng = new Random();

    private final Deque<Cell> body = new ArrayDeque<>();
    private final Set<Cell>  bodySet = new HashSet<>();
    private Direction direction        = Direction.RIGHT;
    private Direction pendingDirection = Direction.RIGHT;

    private Cell food;
    private int score;
    private boolean gameOver;

    public GameState() { reset(); }

    public void reset() {
        lock.lock();
        try {
            body.clear();
            bodySet.clear();
            // 3-cell snake near the centre, facing right
            Cell head = new Cell(COLS / 2, ROWS / 2);
            body.addFirst(head);
            body.addLast(new Cell(head.x() - 1, head.y()));
            body.addLast(new Cell(head.x() - 2, head.y()));
            bodySet.addAll(body);
            direction        = Direction.RIGHT;
            pendingDirection = Direction.RIGHT;
            score    = 0;
            gameOver = false;
            placeFood();
        } finally { lock.unlock(); }
    }

    /** Called by the game loop. Advances the snake one cell. */
    public void tick() {
        lock.lock();
        try {
            if (gameOver) return;

            // Adopt the pending direction unless it would be a 180°.
            if (pendingDirection != direction.opposite()) direction = pendingDirection;

            Cell head    = body.peekFirst();
            Cell newHead = head.move(direction);

            // Wall collision?
            if (newHead.x() < 0 || newHead.x() >= COLS ||
                newHead.y() < 0 || newHead.y() >= ROWS) {
                gameOver = true;
                return;
            }

            // Eating the food?
            boolean ate = newHead.equals(food);

            // Move: add the new head; remove tail unless we ate.
            // We must update bodySet BEFORE the self-collision check
            // (excluding the tail we are about to remove).
            Cell tail = ate ? null : body.peekLast();
            if (tail != null) {
                bodySet.remove(tail);
            }

            if (bodySet.contains(newHead)) {
                // restore tail bookkeeping to keep state consistent
                if (tail != null) bodySet.add(tail);
                gameOver = true;
                return;
            }

            body.addFirst(newHead);
            bodySet.add(newHead);
            if (!ate) body.removeLast();
            else {
                score++;
                placeFood();
            }
        } finally { lock.unlock(); }
    }

    /** Called from the EDT (key listener). */
    public void setDirection(Direction d) {
        lock.lock();
        try {
            // Allow queueing one turn ahead of the next tick.
            if (d != direction.opposite()) pendingDirection = d;
        } finally { lock.unlock(); }
    }

    /** Called from the EDT (renderer) — takes a SNAPSHOT under the lock
     * so the snake doesn't appear half-moved on screen. */
    public Snapshot snapshot() {
        lock.lock();
        try {
            return new Snapshot(
                    body.toArray(new Cell[0]),
                    food,
                    score,
                    gameOver,
                    direction);
        } finally { lock.unlock(); }
    }

    /** Internal — must be called holding the lock. */
    private void placeFood() {
        while (true) {
            Cell c = new Cell(rng.nextInt(COLS), rng.nextInt(ROWS));
            if (!bodySet.contains(c)) { food = c; return; }
        }
    }

    /** Immutable snapshot for the renderer. */
    public record Snapshot(Cell[] body, Cell food, int score, boolean gameOver, Direction direction) {}
}
