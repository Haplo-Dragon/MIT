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
    final private Book traveller_new_edition = new Book(
            "Traveller", Collections.singletonList("Marc Miller"), 2005);
    final private Book traveller_new_era = new Book(
            "Traveller: The New Era", Arrays.asList("Some Guy", "Marc Miller"), 1991);

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
    public void testBuyMoreBooks() {
        Library library = makeLibrary();
        final BookCopy traveller_copy = library.buy(traveller);

        Set<BookCopy> total_travellers = library.allCopies(traveller);
        assertEquals(1, total_travellers.size());

        final BookCopy traveller_copy_2 = library.buy(traveller);
        total_travellers = library.allCopies(traveller);
        assertEquals(2, total_travellers.size());
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
    public void testFindOneWordQueryTitle() {
        Library library = makeLibrary();
        final BookCopy traveller_copy = library.buy(traveller);
        final BookCopy TNE_copy = library.buy(traveller_new_era);
        final BookCopy new_edition_copy = library.buy(traveller_new_edition);

        List<Book> found_travellers = library.find("Traveller");

        assertEquals(3, found_travellers.size());
        assertTrue(found_travellers.contains(traveller));
        assertTrue(found_travellers.contains(traveller_new_edition));
        assertTrue(found_travellers.contains(traveller_new_era));

        assertTrue(
                found_travellers.indexOf(traveller_new_edition) < found_travellers.indexOf(traveller));
    }

    @Test
    public void testFindMultiWordQueryAuthor() {
        Library library = makeLibrary();
        final BookCopy traveller_copy = library.buy(traveller);
        final BookCopy TNE_copy = library.buy(traveller_new_era);
        final BookCopy new_edition_copy = library.buy(traveller_new_edition);

        List<Book> found_millers = library.find("Marc Miller");

        assertEquals(3, found_millers.size());
        assertTrue(found_millers.contains(traveller));
        assertTrue(found_millers.contains(traveller_new_edition));
        assertTrue(found_millers.contains(traveller_new_era));

        assertTrue(
                found_millers.indexOf(traveller_new_edition) < found_millers.indexOf(traveller));
    }

    

    /* Copyright (c) 2016 MIT 6.005 course staff, all rights reserved.
     * Redistribution of original or derived work requires explicit permission.
     * Don't post any of this code on the web or to a public Github repository.
     */

}