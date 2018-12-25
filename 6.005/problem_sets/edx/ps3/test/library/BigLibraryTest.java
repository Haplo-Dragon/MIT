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
    final private Book name_of_wind = new Book(
            "The Name of the Wind", Collections.singletonList("Patrick Rothfuss"), 2008);
    final private Book marcus_test = new Book(
            "I'm a Cool Guy, Too", Collections.singletonList("Marcus D00D"), 2010);

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

    @Test
    public void testFindPartialMatches() {
        Library library = new BigLibrary();
        library.buy(traveller);
        library.buy(traveller_new_era);
        library.buy(marcus_test);
        library.buy(name_of_wind);

        final List<Book> author_found_books = library.find("Marc");

        assertEquals("Expected all books with Marc in authors: " + author_found_books,
                3, author_found_books.size());
        assertTrue(author_found_books.contains(traveller));
        assertTrue(author_found_books.contains(traveller_new_era));
        assertTrue(author_found_books.contains(marcus_test));

        assertFalse(author_found_books.contains(name_of_wind));

        final List<Book> title_found_books = library.find("Trav");

        assertEquals(2, title_found_books.size());
        assertTrue(title_found_books.contains(traveller));
        assertTrue(title_found_books.contains(traveller_new_era));

        assertFalse(title_found_books.contains(marcus_test));
        assertFalse(title_found_books.contains(name_of_wind));
    }

    @Test
    public void testFindPartialAuthors() {
        Library library = new BigLibrary();
        library.buy(marcus_test);

        final List<Book> author_found_books = library.find("Marc");

        assertTrue("Expected to see Marcus Test book: " + author_found_books,
                author_found_books.contains(marcus_test));
    }

    @Test
    public void testFindPartialMatchesMultiWordQuery() {
        Library library = new BigLibrary();
        library.buy(traveller);
        library.buy(traveller_new_era);
        library.buy(name_of_wind);

        final List<Book> found_books = library.find("The New");

        assertEquals("Expected TNE and Name of the Wind: " + found_books,
                2, found_books.size());
        assertTrue(found_books.contains(traveller_new_era));
        assertTrue(found_books.contains(name_of_wind));

        assertFalse(found_books.contains(traveller));
    }


    /* Copyright (c) 2016 MIT 6.005 course staff, all rights reserved.
     * Redistribution of original or derived work requires explicit permission.
     * Don't post any of this code on the web or to a public Github repository.
     */

}
