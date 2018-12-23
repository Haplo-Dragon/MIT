package library;

import java.util.*;

/**
 * BigLibrary represents a large collection of books that might be held by a city or
 * university library system -- millions of books.
 * 
 * In particular, every operation needs to run faster than linear time (as a function of the number of books
 * in the library).
 */
public class BigLibrary implements Library {

    private final TreeMap<Book, TreeSet<BookCopy>> inLibrary;
    private final TreeMap<Book, TreeSet<BookCopy>> checkedOut;
    
    // Rep invariant:
    //      For any given Book, the intersection of the inLibrary SortedSet and the
    //      checkedOut SortedSet is the empty set.
    //
    // Abstraction function:
    //      Represents the collection of books owned by a library. Books in the library's
    //      possession are in the inLibrary SortedSet. Books that are checked out are in
    //      the checkedOut SortedSet.
    // TODO: safety from rep exposure argument
    
    public BigLibrary() {
        throw new RuntimeException("not implemented yet");
    }
    
    // assert the rep invariant
    private void checkRep() {
        for (Book current_book : inLibrary.keySet()){
            // Get all the copies of the current book, both in the library's possession
            // and checked out. We're making copies here because vanilla Java doesn't
            // have a non-destructive set intersection operation.
            TreeSet<BookCopy> inLibraryCopy = new TreeSet<>(
                    inLibrary.get(current_book));
            final TreeSet<BookCopy> checkedOutCopy = new TreeSet<>(
                    checkedOut.get(current_book));

            // Calculate the intersection of the two sets - it should be empty (i.e.,
            // every book that is not checked out is in the library's possession).
            inLibraryCopy.retainAll(checkedOutCopy);

            assert inLibraryCopy.equals(Collections.emptySet());
        }
    }

    @Override
    public BookCopy buy(Book book) {
        throw new RuntimeException("not implemented yet");
    }
    
    @Override
    public void checkout(BookCopy copy) {
        throw new RuntimeException("not implemented yet");
    }
    
    @Override
    public void checkin(BookCopy copy) {
        throw new RuntimeException("not implemented yet");
    }
    
    @Override
    public Set<BookCopy> allCopies(Book book) {
        throw new RuntimeException("not implemented yet");
    }

    @Override
    public Set<BookCopy> availableCopies(Book book) {
        throw new RuntimeException("not implemented yet");
    }
    
    @Override
    public boolean isAvailable(BookCopy copy) {
        throw new RuntimeException("not implemented yet");
    }
    
    @Override
    public List<Book> find(String query) {
        throw new RuntimeException("not implemented yet");
    }
    
    @Override
    public void lose(BookCopy copy) {
        throw new RuntimeException("not implemented yet");
    }


    /* Copyright (c) 2016 MIT 6.005 course staff, all rights reserved.
     * Redistribution of original or derived work requires explicit permission.
     * Don't post any of this code on the web or to a public Github repository.
     */

}
