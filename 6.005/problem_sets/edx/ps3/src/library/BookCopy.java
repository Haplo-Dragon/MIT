package library;

/**
 * BookCopy is a mutable type representing a particular copy of a book that is held in a library's
 * collection.
 */
public class BookCopy {

    private final Book book;
    private final Condition condition;

    public static enum Condition {
        GOOD, DAMAGED
    };

    /*
        REP INVARIANT:
        Book must be a valid Book object.
        Condition must be either GOOD or DAMAGED.

        ABSTRACTION FUNCTION:
        Represents a particular copy of a book in a library's collection.

        SAFETY FROM REP EXPOSURE:

     */
    
    /**
     * Make a new BookCopy, initially in good condition.
     * @param book the Book of which this is a copy
     */
    BookCopy(Book book) {
        throw new RuntimeException("not implemented yet");
    }
    
    // assert the rep invariant
    private void checkRep() {
        assert (this.condition.equals(Condition.GOOD)) ||
               (this.condition.equals(Condition.DAMAGED));
    }
    
    /**
     * @return the Book of which this is a copy
     */
    Book getBook() {
        throw new RuntimeException("not implemented yet");
    }
    
    /**
     * @return the condition of this book copy
     */
    Condition getCondition() {
        throw new RuntimeException("not implemented yet");
    }

    /**
     * Set the condition of a book copy.  This typically happens when a book copy is
     * returned and a librarian inspects it.
     * @param condition the latest condition of the book copy
     */
    public void setCondition(Condition condition) {
        throw new RuntimeException("not implemented yet");
    }
    
    /**
     * @return human-readable representation of this book that includes book.toString()
     *    and the words "good" or "damaged" depending on its condition
     */
    public String toString() {
        throw new RuntimeException("not implemented yet");
    }

    /* Copyright (c) 2016 MIT 6.005 course staff, all rights reserved.
     * Redistribution of original or derived work requires explicit permission.
     * Don't post any of this code on the web or to a public Github repository.
     */

}
