package minesweeper;

import org.junit.Test;

import static org.junit.jupiter.api.Assertions.*;

public class SquareTest {
    @Test
    public void testSquare() {
        final Square square = new Square(4,2);

        // A square may or may not contain a mine.
        assertFalse(square.hasMine());
        square.mine();
        assertTrue(square.hasMine());

        // A square can have 0-8 neighbors with mines.
        assertEquals(0, square.numNeighborsWithMines());

        square.setNeighborsWithMines(-4);
        assertEquals(0, square.numNeighborsWithMines());

        square.setNeighborsWithMines(5);
        assertEquals(5, square.numNeighborsWithMines());

        // A square starts unflagged and undug.
        assertFalse(square.isFlagged());
        square.flag();
        assertTrue(square.isFlagged());
        square.deflag();
        assertFalse(square.isFlagged());

        // Digging a square with a mine will explode the mine, but since that's handled
        // in Board, we'll test it in BoardTest.
        assertFalse(square.isDug());
        square.dig();
        assertTrue(square.isDug());
    }
}
