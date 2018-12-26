package library;

import java.util.*;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test suite for Book ADT.
 */
public class BookTest {

    /*
     * Testing strategy
     * ==================
     * 
     * Partition input space as follows:
     *
     * author:  one author, multiple authors
     *          order
     *          case sensitivity
     * title:   length 1, >1
     * year:    0, 1, >1
     */
    
    @Test
    public void testAssertionsEnabled() {
        assertThrows(AssertionError.class, () -> {
            assert false;
        }); // make sure assertions are enabled with VM argument: -ea
    }

    @Test
    public void testBookOneAuthor() {
        Book book = new Book("Traveller", Collections.singletonList("Mark Miller"), 0);

        assertEquals("Traveller", book.getTitle());
        assertEquals(Collections.singletonList("Mark Miller"), book.getAuthors());
        assertEquals(0, book.getYear());
    }

    @Test
    public void testBookManyAuthors() {
        Book book = new Book(
                "Traveller: The New Era",
                Arrays.asList("Some Guy", "Mark Miller"),
                1);

        assertEquals("Traveller: The New Era", book.getTitle());

        final List<String> authors = book.getAuthors();
        assertTrue(authors.contains("Some Guy") && authors.contains("Mark Miller"));
        assertTrue(authors.indexOf("Some Guy") < authors.indexOf("Mark Miller"));
    }

    @Test
    public void testBookComparison() {
        Book book_1 = new Book("T", Collections.singletonList("Mark Miller"), 1971);
        Book book_2 = new Book("T", Collections.singletonList("mark miller"), 1971);

        assertNotEquals(book_1.getAuthors(), book_2.getAuthors());
    }

    @Test
    public void testBookHashability() {
        Book traveller_original = new Book(
                "Traveller", Collections.singletonList("Marc Miller"), 1971);
        Book traveller_2005 = new Book(
                "Traveller", Collections.singletonList("Marc Miller"), 2005);

        assertNotEquals(traveller_original.hashCode(), traveller_2005.hashCode());

//        final TreeMap<Book, Integer> book_collection = new TreeMap<>(
//                Comparator.comparing(book -> book.getAuthors().get(0)));
        final HashMap<Book, Integer> book_collection = new HashMap<>();

        book_collection.put(traveller_original, 0);
        book_collection.put(traveller_2005, 1);

        assertEquals(2, book_collection.size());
    }


    /* Copyright (c) 2016 MIT 6.005 course staff, all rights reserved.
     * Redistribution of original or derived work requires explicit permission.
     * Don't post any of this code on the web or to a public Github repository.
     */

}
