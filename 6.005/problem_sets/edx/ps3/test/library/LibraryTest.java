package library;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

/**
 * Test suite for Library ADT.
 */
@RunWith(Parameterized.class)
public class LibraryTest {

    /*
     * Note: all the tests you write here must be runnable against any
     * Library class that follows the spec.  JUnit will automatically
     * run these tests against both SmallLibrary and BigLibrary.
     */

    /**
     * Implementation classes for the Library ADT.
     * JUnit runs this test suite once for each class name in the returned array.
     * @return array of Java class names, including their full package prefix
     */
    @Parameters(name="{0}")
    public static Object[] allImplementationClassNames() {
        return new Object[] { 
            "library.SmallLibrary", 
            "library.BigLibrary"
        }; 
    }

    /**
     * Implementation class being tested on this run of the test suite.
     * JUnit sets this variable automatically as it iterates through the array returned
     * by allImplementationClassNames.
     */
    @Parameter
    public String implementationClassName;    

    /**
     * @return a fresh instance of a Library, constructed from the implementation class specified
     * by implementationClassName.
     */
    public Library makeLibrary() {
        try {
            Class<?> cls = Class.forName(implementationClassName);
            return (Library) cls.newInstance();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
    
    
    /*
     * Testing strategy
     * ==================
     * 
     * Partition input space as follows:
     * buy
     * checkout
     * checkin
     * isAvailable
     * allCopies
     * availableCopies
     *      BookCopy: 0 copies in library, 1 copy in library, >1 copy in library
     * find
     *      query: title, author, title + author
     *             1 word, >1 word
     *
     *
     */

    final private Book traveller = new Book(
            "Traveller", Collections.singletonList("Marc Miller"), 1971);
    final private Book traveller_2005_edition = new Book(
            "Traveller", Collections.singletonList("Marc Miller"), 2005);
    final private Book traveller_new_era = new Book(
            "Traveller: The New Era", Arrays.asList("Some Guy", "Marc Miller"), 1991);
    final private Book miller_memoir = new Book(
            "Remembering Space", Collections.singletonList("Marc Miller"), 2010);

    @Test
    public void testAssertionsEnabled() {
        assertThrows(AssertionError.class, () -> {
            assert false;
        }); // make sure assertions are enabled with VM argument: -ea
    }

    @Test
    public void testBuyFirstBook() {
        Library library = makeLibrary();
        final BookCopy traveller_copy = library.buy(traveller);

        assertEquals(BookCopy.Condition.GOOD, traveller_copy.getCondition());
        assertTrue(library.isAvailable(traveller_copy));
    }

    @Test
    public void testBuySecondCopyOfBook() {
        Library library = makeLibrary();
        final BookCopy traveller_copy = library.buy(traveller);
        final BookCopy traveller_copy_2 = library.buy(traveller);

        assertTrue(library.availableCopies(traveller).contains(traveller_copy));
        assertTrue(library.availableCopies(traveller).contains(traveller_copy_2));

        assertEquals(2, library.availableCopies(traveller).size());
    }

    @Test
    public void testBuyMoreBooks() {
        Library library = makeLibrary();
        final BookCopy traveller_copy = library.buy(traveller);

        Set<BookCopy> total_travellers = library.allCopies(traveller);
        assertEquals(1, total_travellers.size());

        final BookCopy traveller_copy_2 = library.buy(traveller);
        total_travellers = library.allCopies(traveller);

        assertEquals("Expected two copies of Traveller in: " + total_travellers,
                2, total_travellers.size());
    }

    @Test
    public void testBuySameAuthorNewBook() {
        Library library = makeLibrary();

        final BookCopy traveller_copy = library.buy(traveller);
        final BookCopy memoir_copy = library.buy(miller_memoir);

        assertTrue(library.isAvailable(traveller_copy));
        assertTrue(library.isAvailable(memoir_copy));
    }

    @Test
    public void testCheckinCheckout() {
        Library library = makeLibrary();
        final BookCopy traveller_copy = library.buy(traveller);

        assertTrue(library.isAvailable(traveller_copy));

        library.checkout(traveller_copy);
        assertFalse(library.isAvailable(traveller_copy));

        library.checkin(traveller_copy);
        assertTrue(library.isAvailable(traveller_copy));
    }

    @Test
    public void testCopies() {
        Library library = makeLibrary();
        final BookCopy traveller_copy = library.buy(traveller);

        assertTrue(library.isAvailable(traveller_copy));

        Set<BookCopy> available_travellers = library.availableCopies(traveller);
        assertTrue(available_travellers.contains(traveller_copy));

        library.checkout(traveller_copy);

        Set<BookCopy> all_travelers = library.allCopies(traveller);
        available_travellers = library.availableCopies(traveller);

        assertTrue(all_travelers.contains(traveller_copy));
        assertFalse(available_travellers.contains(traveller_copy));
    }

    @Test
    public void testBuyBooksSameTitle() {
        Library library = makeLibrary();

        final BookCopy traveller_copy = library.buy(traveller);
        final BookCopy traveller_2005_copy = library.buy(traveller_2005_edition);

        assertTrue(library.isAvailable(traveller_copy));
        assertTrue(library.isAvailable(traveller_2005_copy));
    }

    @Test
    public void testFindOneWordQueryTitle() {
        Library library = makeLibrary();
        library.buy(traveller);
        library.buy(traveller_new_era);
        library.buy(traveller_2005_edition);

        List<Book> found_travellers = library.find("Traveller");

        assertTrue(found_travellers.contains(traveller));
        assertTrue(found_travellers.contains(traveller_2005_edition));

        assertTrue(
                found_travellers.indexOf(traveller_2005_edition) <
                        found_travellers.indexOf(traveller),
                "Expected newer books to appear earlier in search results.");
    }

    @Test
    public void testFindMultiWordQueryAuthor() {
        Library library = makeLibrary();
        final BookCopy traveller_copy = library.buy(traveller);
        final BookCopy TNE_copy = library.buy(traveller_new_era);
        final BookCopy new_edition_copy = library.buy(traveller_2005_edition);

        List<Book> found_millers = library.find("Marc Miller");

        assertEquals(3, found_millers.size());
        assertTrue(found_millers.contains(traveller));
        assertTrue(found_millers.contains(traveller_2005_edition));
        assertTrue(found_millers.contains(traveller_new_era));

        assertTrue(
                found_millers.indexOf(traveller_2005_edition) <
                        found_millers.indexOf(traveller),
                "Expected newer books to appear earlier in search results.");
    }

    @Test
    public void testFindStaffTest() {
        Library library = makeLibrary();
        Book book1 = new Book("Ulysses", Collections.singletonList("James Joyce"), 1922);
        Book book2 = new Book("Infinite Jest", Collections.singletonList("David Foster " +
                "Wallace"), 1996);
        Book book3 = new Book("Consider the Lobster and Other Essays", Collections.singletonList("David Foster Wallace"), 2005);

        // empty library, no matches
        assertEquals(0, library.find("Ulysses").size());

        // one-book library, one match, one copy of match, title search
        library.buy(book1);
        assertEquals(Collections.singletonList(book1), library.find("Ulysses"));

        // >1 book library, >1 match, >1 copy of each match, author search
        library.buy(book2);
        library.buy(book2);
        library.buy(book3);
        List<Book> result = library.find("David Foster Wallace");
        assertEquals("Expected find to disregard duplicate books." + result, 2, result.size());
        assertEquals(new HashSet<>(Arrays.asList(book2, book3)), new HashSet<>(result));

        // 4 matched books with same title/author but different dates must return in decreasing date order
        Book book4 = new Book("Ulysses", Collections.singletonList("James Joyce"), 1942);
        Book book5 = new Book("Ulysses", Collections.singletonList("James Joyce"), 1965);
        Book book6 = new Book("Ulysses", Collections.singletonList("James Joyce"), 2008);
        library.buy(book6);
        library.buy(book4);
        library.buy(book5);
        assertEquals(Arrays.asList(book6, book5, book4, book1), library.find("Ulysses"));
    }

    @Test
    public void testAllCopiesAvailableCopiesStaffTest() {
        Library library = makeLibrary();
        Book book = new Book("Sense and Sensibility", Arrays.asList("Jane Austen"), 1811);

        // no copies owned
        Set<BookCopy> noCopies = Collections.emptySet();
        assertEquals(noCopies, library.allCopies(book));
        assertEquals(noCopies, library.availableCopies(book));

        // 1 copy owned, available
        BookCopy copy1 = library.buy(book);
        Set<BookCopy> justCopy1 = new HashSet<>(Arrays.asList(copy1));
        assertEquals(justCopy1, library.allCopies(book));
        assertEquals(justCopy1, library.availableCopies(book));

        // 1 copy owned, checked out
        library.checkout(copy1);
        assertEquals(justCopy1, library.allCopies(book));
        assertEquals(noCopies, library.availableCopies(book));

        // >1 copy owned, >1 available
        library.checkin(copy1);
        BookCopy copy2 = library.buy(book);
        Set<BookCopy> copy1and2 = new HashSet<>(Arrays.asList(copy1, copy2));
        assertEquals(copy1and2, library.allCopies(book));
        assertEquals(copy1and2, library.availableCopies(book));

        // >1 copy owned, 1 available
        library.checkout(copy2);
        assertEquals(copy1and2, library.allCopies(book));
        assertEquals(justCopy1, library.availableCopies(book));
    }

    

    /* Copyright (c) 2016 MIT 6.005 course staff, all rights reserved.
     * Redistribution of original or derived work requires explicit permission.
     * Don't post any of this code on the web or to a public Github repository.
     */

}