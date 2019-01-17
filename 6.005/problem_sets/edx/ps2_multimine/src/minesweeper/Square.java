package minesweeper;

/**
 * A single square in a minesweeper board. Can be dug or undug, flagged or unflagged, and
 * may contain a mine. A square that is undug and unflagged is considered untouched.
 * A square may have 0-8 neighbors with mines.
 */
public class Square {
    private boolean isFlagged;
    private boolean isDug;
    private boolean hasMine;

    // The number of neighbors of this square which contain mines.
    private int neighbors_with_mines;

    // The square's X coordinate.
    private final int x;
    // The square's Y coordinate.
    private final int y;

    public Square(int x, int y) {
        this.isFlagged = false;
        this.isDug = false;
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    public boolean isUntouched() {
        return !(this.isDug || this.isFlagged);
    }

    public boolean isSafe() {
        return !(this.hasMine);
    }

    public void flag(){
        this.isFlagged = true;
    }

    public void deflag() {
        this.isFlagged = false;
    }

    public boolean isFlagged() {
        return this.isFlagged;
    }

    public void dig() {
        this.isDug = true;
    }

    public boolean isDug() {
        return this.isDug;
    }

    public void mine() {
        this.hasMine = true;
    }

    public void explode() {
        this.hasMine = false;
    }

    public boolean hasMine() {
        return this.hasMine;
    }

    public void setNeighborsWithMines(int num_mined_neighbors) {
        // A square can't have fewer than 0 neighbors with mines or more than 8.
        if ((0 <= num_mined_neighbors) && (num_mined_neighbors <= 8)) {
            this.neighbors_with_mines = num_mined_neighbors;
        } else {
            this.neighbors_with_mines = 0;
        }
    }

    public int numNeighborsWithMines() {
        return this.neighbors_with_mines;
    }

    public String toString() {
        return String.format(
                "Square at (%d, %d)\n", this.x, this.y) +
                "===========\n" +
               String.format(
                "Has mine: %s\nIs flagged: %s\nIs dug: %s\nIs untouched: " +
                "%s\n", this.hasMine, this.isFlagged, this.isDug, this.isUntouched());
    }
}
