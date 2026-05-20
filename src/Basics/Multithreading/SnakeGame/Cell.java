package Basics.Multithreading.SnakeGame;

/**
 * Immutable grid coordinate. Records are perfect for this — value
 * semantics, free equals/hashCode/toString, safe to share across
 * threads.
 */
public record Cell(int x, int y) {
    public Cell move(Direction d) { return new Cell(x + d.dx, y + d.dy); }
}
