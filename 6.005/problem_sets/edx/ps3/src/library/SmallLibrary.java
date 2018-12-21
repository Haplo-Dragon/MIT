package library;

import java.util.*;

/** 
 * SmallLibrary represents a small collection of books, like a single person's home collection.
 */
public class SmallLibrary implements Library {

    // This rep is required! 
    // Do not change the types of inLibrary or checkedOut, 
    // and don't add or remove any other fields.
    // (BigLibrary is where you can create your own rep for
    // a Library implementation.)

    // rep
    private Set<BookCopy> inLibrary;
    private Set<BookCopy> checkedOut;
    
    // rep invariant:
    //    the intersection of inLibrary and checkedOut is the empty set
    //
    // abstraction function:
    //    represents the collection of books inLibrary union checkedOut,
    //      where if a book copy is in inLibrary then it is available,
    //      and if a copy is in checkedOut then it is checked out

    // TODO: safety from rep exposure argument
    
    public SmallLibrary() {
        this.inLibrary = new HashSet<>();
        this.checkedOut = new HashSet<>();

        checkRep();
    }
    
    // assert the rep invariant
    private void checkRep() {
        // We're making copies because Java's intersection method mutates the sets.
        // Side note: it is INCREDIBLY FRUSTRATING that Java implements Sets but doesn't
        // include a non-destructive intersection operation! Holy fuck. Guava has it, but
        // I'm using vanilla Java here.
        // See, Java? This is why Kotlin gets all the chicks.
        Set<BookCopy> inLibraryCopy = new HashSet<>(inLibrary);
        Set<BookCopy> checkedOutCopy = new HashSet<>(checkedOut);

        // Calculate the intersection of the two sets.
        inLibraryCopy.retainAll(checkedOutCopy);

        assert inLibraryCopy.equals(Collections.emptySet());
    }

    @Override
    public BookCopy buy(Book book) {
        final BookCopy new_book = new BookCopy(book);
        this.inLibrary.add(new_book);

        checkRep();
        return new_book;
    }
    
    @Override
    public void checkout(BookCopy copy) {
        this.inLibrary.remove(copy);
        this.checkedOut.add(copy);
        checkRep();
    }
    
    @Override
    public void checkin(BookCopy copy) {
        this.checkedOut.remove(copy);
        this.inLibrary.add(copy);
        checkRep();
    }
    
    @Override
    public boolean isAvailable(BookCopy copy) {
        return this.inLibrary.contains(copy);
    }
    
    @Override
    public Set<BookCopy> allCopies(Book book) {
        // Find all copies of the book that are in the library.
        final Set<BookCopy> foundCopies = new HashSet<>(availableCopies(book));

        // Find all copies of the book that are checked out.
        for (BookCopy copy: this.checkedOut) {
            if (copy.getBook().equals(book)) {
                foundCopies.add(copy);
            }
        }

        return foundCopies;
    }
    
    @Override
    public Set<BookCopy> availableCopies(Book book) {
        final Set<BookCopy> availableCopies = new HashSet<>();

        // Find all copies of book in library.
        for (BookCopy copy : this.inLibrary) {
            if (copy.getBook().equals(book)) {
                availableCopies.add(copy);
            }
        }

        return availableCopies;
    }

    @Override
    public List<Book> find(String query) {
        final List<Book> found_books = new ArrayList<>();

        // Get all unique books in the library's collection.
        final Set<Book> all_books = getAllUniqueBooks();

        // Check only for exact matches of title or author name.
        for (Book current_book : all_books) {
            if ((current_book.getTitle().equals(query)) ||
                (current_book.getAuthors().contains(query))); {
                found_books.add(current_book);
            }
        }
        // Sort the found books by publication year in descending order.
        found_books.sort(Comparator.comparing(Book::getYear).reversed());
        return found_books;
    }
    
    @Override
    public void lose(BookCopy copy) {
        this.inLibrary.remove(copy);
        this.checkedOut.remove(copy);
        checkRep();
    }

    private Set<Book> getAllUniqueBooks() {
        // Get a set of all the COPIES in the library's collection, both in library and
        // checked out.
        final Set<BookCopy> all_copies = new HashSet<>();
        all_copies.addAll(this.inLibrary);
        all_copies.addAll(this.checkedOut);

        // Get all unique books in the library's collection.
        final Set<Book> all_books = new HashSet<>();
        for (BookCopy copy : all_copies) {
            all_books.add(copy.getBook());
        }

        return all_books;
    }

    /* Copyright (c) 2016 MIT 6.005 course staff, all rights reserved.
     * Redistribution of original or derived work requires explicit permission.
     * Don't post any of this code on the web or to a public Github repository.
     */
}
