package library;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test suite for BookCopy ADT.
 */
public class BookCopyTest {

    /*
     * Testing strategy
     * ==================
     * 
     * Partition input space as follows:
     *
     * Book:
     *      author: 1, >1
     *      title: length 1, >1
     *      year: 0, 1, >1
     */

    @Test
    public void testAssertionsEnabled() {
        assertThrows(AssertionError.class, () -> {
            assert false;
        }); // make sure assertions are enabled with VM argument: -ea
    }

    @Test
    public void testBookCopy() {
        Book book = new Book("Traveller", Collections.singletonList("Mark Miller"), 1971);
        BookCopy copy = new BookCopy(book);

        assertEquals(book, copy.getBook());
        assertEquals(BookCopy.Condition.GOOD, copy.getCondition());
    }

    @Test
    public void testBookCopySetCondition() {
        Book book = new Book("Traveller", Collections.singletonList("Mark Miller"), 1971);
        BookCopy copy = new BookCopy(book);

        assertEquals(BookCopy.Condition.GOOD, copy.getCondition());

        copy.setCondition(BookCopy.Condition.DAMAGED);

        assertEquals(BookCopy.Condition.DAMAGED, copy.getCondition());
    }

    /* Copyright (c) 2016 MIT 6.005 course staff, all rights reserved.
     * Redistribution of original or derived work requires explicit permission.
     * Don't post any of this code on the web or to a public Github repository.
     */

}
