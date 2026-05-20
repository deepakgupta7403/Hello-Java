package Basics.Multithreading.SnakeGame;

/**
 * Four-direction movement enum for the snake.
 *
 * Each direction carries its (dx, dy) cell delta and knows which
 * direction is its OPPOSITE — we use that to reject illegal 180° turns
 * (the snake cannot turn back on itself).
 */
public enum Direction {
    UP(0, -1),
    DOWN(0, 1),
    LEFT(-1, 0),
    RIGHT(1, 0);

    public final int dx;
    public final int dy;

    Direction(int dx, int dy) { this.dx = dx; this.dy = dy; }

    public Direction opposite() {
        return switch (this) {
            case UP    -> DOWN;
            case DOWN  -> UP;
            case LEFT  -> RIGHT;
            case RIGHT -> LEFT;
        };
    }
}
