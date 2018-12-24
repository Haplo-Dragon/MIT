package library;

import org.junit.Test;
import static org.junit.Assert.*;

import java.util.*;

/**
 * Test suite for BigLibrary's stronger specs.
 */
public class BigLibraryTest {
    
    /* 
     * NOTE: use this file only for tests of BigLibrary.find()'s stronger spec.
     * Tests of all other Library operations should be in LibraryTest.java 
     */

    /*
     * Testing strategy
     * ==================
     * 
     * TODO: your testing strategy for BigLibrary.find() should go here.
     * Make sure you have partitions.
     */

    final private Book traveller = new Book(
            "Traveller", Collections.singletonList("Marc Miller"), 1971);
    final private Book traveller_new_edition = new Book(
            "Traveller", Collections.singletonList("Marc Miller"), 2005);
    final private Book traveller_new_era = new Book(
            "Traveller: The New Era", Arrays.asList("Some Guy", "Marc Miller"), 1991);

    // TODO: put JUnit @Test methods here that you developed from your testing strategy

    @Test(expected=AssertionError.class)
    public void testAssertionsEnabled() {
        assert false; // make sure assertions are enabled with VM argument: -ea
    }

    @Test
    public void testBuyMoreBooks() {
        Library library = new BigLibrary();
        final BookCopy traveller_copy = library.buy(traveller);

        Set<BookCopy> total_travellers = library.allCopies(traveller);
        assertEquals(1, total_travellers.size());

        final BookCopy traveller_copy_2 = library.buy(traveller);
        total_travellers = library.allCopies(traveller);

        assertEquals("Expected two copies of Traveller in: " + total_travellers,
                2, total_travellers.size());
    }

    @Test
    public void testBuySecondCopyOfBook() {
        Library library = new BigLibrary();
        final BookCopy traveller_copy = library.buy(traveller);
        final BookCopy traveller_copy_2 = library.buy(traveller);

        assertTrue(library.availableCopies(traveller).contains(traveller_copy));
        assertTrue(library.availableCopies(traveller).contains(traveller_copy_2));

        assertEquals(2, library.availableCopies(traveller).size());
    }


    /* Copyright (c) 2016 MIT 6.005 course staff, all rights reserved.
     * Redistribution of original or derived work requires explicit permission.
     * Don't post any of this code on the web or to a public Github repository.
     */

}
