/* Copyright (c) 2007-2017 MIT 6.005 course staff, all rights reserved.
 * Redistribution of original or derived work requires permission of course staff.
 */
package minesweeper;

import org.junit.Test;
import org.junit.jupiter.api.TestTemplate;

import java.io.File;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests basic Board creation and observation functionality.
 */
public class BoardTest {
    
    /* Testing strategy
    * ====================
    * Partition input space as follows
    *
    * Rows, columns: 1, > 1, MAX_INT
    *                rows == columns, rows != columns
    * File: using board file, using no board file
    */

    private final String BOARD_FILE = "I:\\Users\\Haplo\\Documents\\GitHub\\MIT\\6" +
            ".005\\problem_sets\\edx\\ps2_multimine\\test\\minesweeper\\test_board.txt";
    
    @Test(expected=AssertionError.class)
    public void testAssertionsEnabled() {
        assert false; // make sure assertions are enabled with VM argument: -ea
    }
    
    @Test
    public void testMinimumBoard() {
        final Board board = new Board(1,1);

//        // Make sure the board has at least one mine.
//        assertTrue(board.hasMinesRemaining());
//        assertTrue(board.numMinesRemaining() >= 1);
//
//        // The board has only one square, so we'll test it. It must contain a mine.
//        assertTrue(board.hasMine(1,1));

        // Test flagging the square.
        assertFalse(board.isFlagged(0,0));
        board.flag(0,0);
        assertTrue(board.isFlagged(0,0));
        board.deflag(0,0);
        assertFalse(board.isFlagged(0,0));

        // Test digging the square.
        assertFalse(board.isDug(0,0));
        board.dig(0,0);
        assertTrue(board.isDug(0,0));

//        // After digging a square with a bomb, the bomb explodes and that square no
//        // longer contains a bomb.
//        assertFalse(board.hasMine(1,1));
//        assertFalse(board.hasMinesRemaining());
//        assertEquals(0, board.numMinesRemaining());
    }

    @Test
    public void testRandomBoard() {
        final Board board = new Board(10, 20);

//        // Make sure the board has at least one mine.
//        assertTrue(board.hasMinesRemaining());
//        assertTrue(board.numMinesRemaining() >= 1);

        // Test flagging a square.
        assertFalse(board.isFlagged(5, 17));
        board.flag(5,17);
        assertTrue(board.isFlagged(5,17));
        board.deflag(5,17);
        assertFalse(board.isFlagged(5,17));

        // Test digging a square.
        assertFalse(board.isDug(5,17));
        board.dig(5,17);
        assertTrue(board.isDug(5,17));
    }

    @Test
    public void testBoardFromFile() {
        final Board board = new Board(Paths.get(BOARD_FILE).toFile());

        // The test board should have 2 mines.
        assertEquals(2, board.numMinesRemaining());

        // At first, the board is blank.
        String expected_board =
                "- - - - \n" +
                "- - - - \n" +
                "- - - - \n" +
                "- - - - \n" +
                "- - - - \n";
        assertEquals(expected_board, board.toString());

        // Dig a square that has a mined neighbor.
        board.dig(2,1);
        expected_board =
                "- - - - \n" +
                "- - 1 - \n" +
                "- - - - \n" +
                "- - - - \n" +
                "- - - - \n";
        assertEquals(expected_board, board.toString());

        // Dig a square that does NOT have a mined neighbor. In this case, the dig state
        // should propagate outward and reveal more of the board.
        board.dig(0,3);
        expected_board =
                "1 1 1   \n" +
                "1 - 1   \n" +
                "1 1 1   \n" +
                "  1 1 1 \n" +
                "  1 - 1 \n";
        assertEquals(expected_board, board.toString());

        // Dig a mined square. This should explode the mine, updating the mine counts of
        // its neighbors and the overall number of mines on the board.
        board.dig(2,4);
        expected_board =
                "1 1 1   \n" +
                "1 - 1   \n" +
                "1 1 1   \n" +
                "        \n" +
                "        \n";
        assertEquals(expected_board, board.toString());
        assertEquals(1, board.numMinesRemaining());

        // Flag the last remaining mine.
        board.flag(1,1);
        expected_board =
                "1 1 1   \n" +
                "1 F 1   \n" +
                "1 1 1   \n" +
                "        \n" +
                "        \n";
        assertEquals(expected_board, board.toString());
        assertEquals(0, board.numMinesRemaining());
    }

    @Test
    public void testLook() {
        final Board board = new Board(7,7);
        final String expected_board =
            "- - - - - - - \n" +
            "- - - - - - - \n" +
            "- - - - - - - \n" +
            "- - - - - - - \n" +
            "- - - - - - - \n" +
            "- - - - - - - \n" +
            "- - - - - - - \n";
        assertEquals(expected_board, board.toString());

        board.flag(5, 2);
        final String expected_board_with_flag =
                "- - - - - - - \n" +
                "- - - - - - - \n" +
                "- - - - - F - \n" +
                "- - - - - - - \n" +
                "- - - - - - - \n" +
                "- - - - - - - \n" +
                "- - - - - - - \n";
        assertEquals(expected_board_with_flag, board.toString());
    }

    @Test
    public void testInvalidSquare() {
        final Board board = new Board(5, 5);

        // A nonexistent square can't have a mine.
        assertFalse(board.hasMine(6,6));

        // Flagging or digging a nonexistent square should do nothing.
        assertFalse(board.isFlagged(6,6));
        board.flag(6,6);
        assertFalse(board.isFlagged(6,6));

        assertFalse(board.isDug(6,6));
        board.dig(6,6);
        assertFalse(board.isDug(6,6));
    }
    
}
