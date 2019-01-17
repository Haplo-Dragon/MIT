/* Copyright (c) 2007-2017 MIT 6.005 course staff, all rights reserved.
 * Redistribution of original or derived work requires permission of course staff.
 */
package minesweeper;

import java.io.*;
import java.util.*;

/**
 * A threadsafe multiplayer minesweeper board, a grid of squares with X_SIZE x Y_SIZE
 * squares in it, each of which may or may not conceal a mine.
 * Squares are mined with a probability of 25%.
 *
 * Squares are numbered starting from the top left, which is (0,0).
 * The bottom right square would be at (X_SIZE - 1, Y_SIZE - 1).
 */
public class Board {
    /*
    * X_SIZE >= 1, Y_SIZE >= 1
    *
    * Thread safety:
    *   All methods which mutate or depend upon the rep are synchronized. Only one thread
    *   can change the grid at any given time.
    *
    * Operations:
    *
    *   Creators
    *       Constructors : with input file (deterministic), without input file (random)
    *   Producers
    *       None?
    *   Mutators
    *       dig: Unearth a square
    *       flag: Flag a square
    *       deflag: Deflag a square
    *   Observers
    *       look: Display the grid
    *       isDug: int x, int y -> boolean: Return true if the Square at the given X and Y
    *                                       has been dug.
    *       isFlagged: int x, int y -> boolean: Return true if the Square at the given X
    *                                           and Y has been dug.
    *       hasMine: int x, int y -> boolean: Return true if the Square at the given X and
    *                                         Y contains a mine.
    *       numMinesRemaining -> int: Get the number of undiscovered/unexploded mines
    *                                 remaining.
    *       hasMinesRemaining -> boolean: Return true if there are undiscovered/unexploded
    *                                  mines remaining on the board.
    *       getNeighbors: int x, int y -> List<Squares>: Get all neighbors of the Square
    *                                                    at the given X and Y.
    */

    private final int y_size;
    private final int x_size;
    // The current number of undiscovered/unexploded mines remaining on the grid.
    private int num_mines_remaining;
    // The number of mines the grid had when it was created.
    private int total_num_mines;
    private final ArrayList<ArrayList<Square>> grid;
    // The probability of a mine appearing in any given square when the board is created.
    private final double MINE_PROBABILITY = 0.25;
    private final String UNTOUCHED_SQUARE_SYMBOL = "- ";
    private final String FLAGGED_SQUARE_SYMBOL = "F ";
    private final String DUG_SQUARE_NO_BOMB_SYMBOL = "  ";
    // A cache of squares, mapped to their neighbors.
    private final Map<String, List<Square>> neighbors;

    /**
     * Create a new multiplayer minesweeper board, with a number of squares equal to
     * ROWS x COLUMNS and randomly placed mines.
     * @param y_size int >= 1, representing the number of y_size in the board.
     * @param x_size int >= 1, representing the number of x_size in the board.
     */
    public Board(int x_size, int y_size) {
        this.y_size = y_size;
        this.x_size = x_size;
        // Create a grid and place mines randomly.
        this.grid = createGrid(true);
        this.neighbors = new HashMap<>();
        // Calculate neighbor counts for all squares without mines.
        calculateMinedNeighborCounts();

        checkRep();
    }

    private void checkRep() {
        assert this.y_size >= 0;
        assert this.x_size >= 0;
        assert this.num_mines_remaining >= 0;
    }

    /**
     * Create a new multiplayer minesweeper board matching the given board file.
     * @param board_file The properly-formatted board file to use to generate the board.
     *                   The file format for board files is:
     *                   FILE ::= BOARD LINE+
     *                   BOARD := X SPACE Y NEWLINE
     *                   LINE ::= (VAL SPACE)* VAL NEWLINE
     *                   VAL ::= 0 | 1
     *                   X ::= INT
     *                   Y ::= INT
     *                   SPACE ::= " "
     *                   NEWLINE ::= "\n" | "\r" "\n"?
     *                   INT ::= [0-9]+
     */
    public Board(File board_file) {
        try (BufferedReader file = new BufferedReader(new FileReader(board_file))) {
            // Grab the first line, which should be the X and Y size of the board as
            // integers.
            String line = file.readLine();
            final String[] board_size = line.split(" ");
            this.x_size = Integer.valueOf(board_size[0]);
            this.y_size = Integer.valueOf(board_size[1]);

            // Create the grid, without random mines.
            this.grid = createGrid(false);

            // Read in the rest of the board, placing mines where necessary.
            for (int y = 0; y < this.y_size; y++) {
                final String[] current_row = file.readLine().split(" ");
                for (int x = 0; x < current_row.length; x++) {
                    if (current_row[x].equals("1")) {
                        // Mine the square where the 1 appears.
                        this.grid.get(x).get(y).mine();
                    }
                }
            }
        } catch (FileNotFoundException fe) {
            throw new RuntimeException("Specified file not found." +
                    fe.getMessage());
        } catch (IOException ioe) {
            throw new RuntimeException("Board file not properly formatted." +
                    ioe.getMessage());
        }

        // Calculate neighbor counts for all squares without mines.
        this.neighbors = new HashMap<>();
        calculateMinedNeighborCounts();

        checkRep();
    }

    /**
     * Creates a new grid of size X_SIZE x Y_SIZE, placing mines randomly with a
     * probability equal to MINE_PROBABILITY.
     * @param random_mines Whether or not the board should be populated with random mines.
     * @return The newly created grid, with mines if random_mines=true, but without mined
     * neighbor counts.
     */
    private ArrayList<ArrayList<Square>> createGrid(boolean random_mines) {
        final ArrayList<ArrayList<Square>> temp_grid = new ArrayList<>();

        // Columns are X coordinates because the grid is numbered starting from (0,0) at
        // the top left.
        for (int x = 0; x < this.x_size; x++) {
            temp_grid.add(x, new ArrayList<>());
            // Rows are Y coordinates.
            for (int y = 0; y < this.y_size; y++) {
                final Square current_square = new Square(x, y);

                if (random_mines) {
                    // Squares are mined randomly, with a probability of 25%.
                    if (Math.random() <= MINE_PROBABILITY) {
                        current_square.mine();
                    }
                }

                temp_grid.get(x).add(y, current_square);
            }
        }

        return temp_grid;
    }

    /**
     * Count the number of mined neighbors for each square on the board.
     */
    private void calculateMinedNeighborCounts() {
        int total_mine_count = 0;

        // Step through all the squares on the grid.
        for (ArrayList<Square> columns : this.grid) {
            for (Square current_square : columns) {
                // If the square has a mine, we'll count it toward the total number of
                // mines on the board.
                if (current_square.hasMine()) {
                    total_mine_count++;
                }

                // Get all neighbors of the current square.
                final List<Square> current_neighbors =
                        getNeighbors(current_square.getX(), current_square.getY());

                // Count the number of neighbors with mines.
                int current_mine_count = 0;
                for (Square neighbor : current_neighbors) {
                    if (neighbor.hasMine()) {
                        current_mine_count++;
                    }

                    current_square.setNeighborsWithMines(current_mine_count);
                }
            }
        }

        // Finally, we'll set the starting mine count for the entire grid.
        this.num_mines_remaining = total_mine_count;
        this.total_num_mines = total_mine_count;

        checkRep();
    }

    /**
     * Determine if the board has mines remaining.
     * @return true iff the board has undiscovered/unexploded mines remaining.
     */
    public boolean hasMinesRemaining() {
        return this.num_mines_remaining > 0;
    }

    /**
     * Get the number of mines remaining.
     * @return The number of undiscovered/unexploded mines remaining.
     */
    public int numMinesRemaining() {
        return this.num_mines_remaining;
    }

    /**
     * Determine if the square at the given position contains a mine.
     * @param x 0 <= x < board.x_size, the X coordinate of the square to search.
     * @param y 0 <= y < board.y_size, the Y coordinate of the square to search.
     * @return true iff the given position contains a mine.
     */
    public synchronized boolean hasMine(int x, int y) {
        if (isValidPosition(x, y)) {
            final Square target_square = this.grid.get(x).get(y);
            return target_square.hasMine();
        }
        return false;
    }

    /**
     * Determine if the square at the given position is flagged.
     * @param x 0 <= x < board.x_size, the X coordinate of the square to check.
     * @param y 0 <= y < board.y_size, the Y coordinate of the square to check.
     * @return true iff the square at the given position is flagged.
     */
    public synchronized boolean isFlagged(int x, int y) {
        if (isValidPosition(x, y)) {
            final Square target_square = this.grid.get(x).get(y);
            return target_square.isFlagged();
        }
        return false;
    }

    /**
     * Flag the square at the given position. If the square is already flagged, does
     * nothing.
     * @param x 0 <= x < board.x_size, the X coordinate of the square to flag.
     * @param y 0 <= y < board.y_size, the Y coordinate of the square to flag.
     */
    public synchronized void flag(int x, int y) {
        if (isValidPosition(x, y)) {
            final Square target_square = this.grid.get(x).get(y);
            target_square.flag();

            // If we're flagging a mined square (that is, a CORRECT flag), then the number
            // of undiscovered mines goes down by 1, because we've discovered a mine.
            if (target_square.hasMine()) {
                removeMine();
            }
        }

        checkRep();
    }

    /**
     * Remove the flag from the square at the given position. If the square is not
     * flagged, does nothing.
     * @param x 0 <= x < board.x_size, the X coordinate of the square to flag.
     * @param y 0 <= y < board.y_size, the Y coordinate of the square to flag.
     */
    public synchronized void deflag(int x, int y) {
        if (isValidPosition(x, y)) {
            final Square target_square = this.grid.get(x).get(y);
            target_square.deflag();

            // If we're removing a flag from a mined square (that is, a CORRECT flag), then
            // the number of undiscovered mines goes up by 1.
            if (target_square.hasMine()) {
                addMine();
            }
        }

        checkRep();
    }

    /**
     * Determine if the square at the given position has been dug.
     * @param x 0 <= x < board.x_size, the X coordinate of the square to check.
     * @param y 0 <= y < board.y_size, the Y coordinate of the square to check.
     * @return true iff the square at the given position has been dug.
     */
    public synchronized boolean isDug(int x, int y) {
        if (isValidPosition(x, y)) {
            final Square target_square = this.grid.get(x).get(y);
            return target_square.isDug();
        }
        return false;
    }

    /**
     * Dig the square at the given position, updating its neighboring squares recursively
     * if necessary.
     * If the square has already been dug, does nothing.
     * @param x 0 <= x < board.x_size, the X coordinate of the square to dig.
     * @param y 0 <= y < board.y_size, the Y coordinate of the square to dig.
     */
    public synchronized void dig(int x, int y) {
        if (isValidPosition(x, y)) {
            final Square target_square = this.grid.get(x).get(y);
            boolean boom = false;

            if (target_square.isUntouched()) {
                target_square.dig();

                if (target_square.hasMine()) {
                    // The mine explodes! BOOM
                    target_square.explode();
                    boom = true;
                    removeMine();
                }

                if (!(target_square.numNeighborsWithMines() > 0)) {
                    // Update the mine counts and dig states of the square's neighbors.
                    updateNeighbors(x, y, boom);
                }
            }
        }

        checkRep();
    }

    /**
     * Decreases the number of undiscovered/unexploded mines remaining on the board by 1,
     * with a minimum of 0.
     */
    private void removeMine() {
        if (this.num_mines_remaining > 0) {
            this.num_mines_remaining--;
        }
    }

    /**
     * Increases the number of undiscovered/unexploded mines remaining on the board by 1.
     */
    private void addMine() {
        this.num_mines_remaining++;
    }

    /**
     * Updates the neighbors of the square at the given position, changing mine counts
     * and propagating the dug state as appropriate.
     * @param x 0 <= x < board.x_size The X coordinate of the square to update neighbors
     *          for.
     * @param y 0 <= y < board.y_size The Y coordinate of the square to update neighbors
     *          for.
     * @param mine_exploded Whether or not a mine exploded when the square at the given
     *                      X and Y coordinates was dug.
     */
    private void updateNeighbors(int x, int y, boolean mine_exploded){
        final List<Square> neighbors = getNeighbors(x, y);

        // If a mine went off and was removed from the board, we need to update the mine
        // counts of its neighbors.
        if (mine_exploded) {
            for (Square neighbor : neighbors) {
                final int old_num_mines = neighbor.numNeighborsWithMines();
                neighbor.setNeighborsWithMines(old_num_mines - 1);
            }
        }

        // Otherwise, a safe square has been dug, and we need to propagate the dig to all
        // adjacent, safe, untouched squares.
        for (Square neighbor : neighbors) {
            if (neighbor.isUntouched() && neighbor.isSafe()) {
                neighbor.dig();
                updateNeighbors(neighbor.getX(), neighbor.getY(), false);
            }
        }
    }

    /**
     * Get all neighbors of the Square at the given position.
     * @param x 0 <= x < board.x_size, the X coordinate of the square to find neighbors
     *         for.
     * @param y 0 <= y < board.y_size, the Y coordinate of the square to find
     *          neighbors for.
     * @return List of Squares adjacent to the Square at the given position.
     */
    private List<Square> getNeighbors(int x, int y) {
        // This appears to be the easiest way to use two numbers as a map key in Java.
        // My kingdom for tuples from Python!
        final String target_square = x + "," + y;

        // If we've calculated the given square's neighbors before, we'll look them up.
        if (this.neighbors.containsKey(target_square)) {
            return this.neighbors.get(target_square);
        } else {
            // Otherwise, we'll calculate neighbors for the first time and store them for
            // later.
            final List<Square> new_neighbors = new ArrayList<>();

            // Get all the X coordinates which are adjacent to the given square.
            final List<Integer> x_positions = new ArrayList<>();
            for (int pos = x - 1; pos <= x + 1; pos++) {
                if (isValidColumn(pos)) {
                    x_positions.add(pos);
                }
            }

            // Get all the Y coordinates which are adjacent to the given square.
            final List<Integer> y_positions = new ArrayList<>();
            for (int pos = y - 1; pos <= y + 1; pos++) {
                if (isValidRow(pos)) {
                    y_positions.add(pos);
                }
            }

            // Add all the squares which are adjacent to the target square as neighbors.
            for (int x_pos : x_positions) {
                for (int y_pos : y_positions) {
                    // A square can't be its own neighbor.
                    if (! ((x_pos == x) && (y_pos == y)) ) {
                        new_neighbors.add(this.grid.get(x_pos).get(y_pos));
                    }
                }
            }

            // Store the list of neighbors for the given square.
            this.neighbors.put(target_square, new_neighbors);
            return new_neighbors;
        }
    }

    /**
     * Determine if the given position is a valid square on the board.
     * @param x The X coordinate of the space to check.
     * @param y The Y coordinate of the space to check.
     * @return true iff the given position represents a valid square within the
     *         current board.
     */
    public boolean isValidPosition(int x, int y) {
        return (isValidColumn(x) && isValidRow(y));
    }

    /**
     * Determine if the given Y coordinate is a valid Y coordinate on the board.
     * @param y The Y coordinate of the space to check.
     * @return true iff the given Y coordinate is within the bounds of the board.
     */
    private boolean isValidRow(int y) {
        return ((0 <= y) && (y < this.y_size));
    }

    /**
     * Determine if the given X coordinate is a valid X coordinate on the board.
     * @param x The Y coordinate of the space to check.
     * @return true iff the given X coordinate is within the bounds of the board.
     */
    private boolean isValidColumn(int x) {
        return ((0 <= x) && (x < this.x_size));
    }

    public synchronized String toString() {
        final StringBuilder s = new StringBuilder();

        // We're working across each row, then down. Y coordinates are the rows.
        for (int y = 0; y < this.y_size; y++) {

            // Print a symbol for each square in the current row. Note that each symbol
            // is followed by a single space.
            for (int x = 0; x < this.x_size; x++) {
                final Square current_square = this.grid.get(x).get(y);

                if (current_square.isUntouched()) {
                    s.append(UNTOUCHED_SQUARE_SYMBOL);
                } else if (current_square.isFlagged()) {
                    s.append(FLAGGED_SQUARE_SYMBOL);
                } else if (current_square.isDug()) {
                    // Dug squares are blank if they have no neighbors with mines.
                    // Otherwise, they display how many neighbors with mines they have.
                    if (current_square.numNeighborsWithMines() == 0) {
                        s.append(DUG_SQUARE_NO_BOMB_SYMBOL);
                    } else {
                        s.append(current_square.numNeighborsWithMines())
                                .append(" ");
                    }
                }
            }
            // We're finished with the current row, so we add a newline so we can print
            // the next row.
            s.append("\n");
        }
        return s.toString();
    }
}
