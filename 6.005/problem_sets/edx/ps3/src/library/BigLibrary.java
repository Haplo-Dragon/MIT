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

    private final TreeMap<Book, HashSet<BookCopy>> inLibrary;
    private final TreeMap<Book, HashSet<BookCopy>> checkedOut;
    private final HashMap<String, Set<Book>> authors;
    private final HashMap<String, Set<Book>> titles;
    
    // Rep invariant:
    //      For any given Book, the intersection of the inLibrary SortedSet and the
    //      checkedOut Set is the empty set.
    //
    // Abstraction function:
    //      Represents the collection of books owned by a library. Books in the library's
    //      possession are in the inLibrary SortedSet. Books that are checked out are in
    //      the checkedOut SortedSet. Books in the library are sorted alphabetically by
    //      their first listed author, then by title, then by year.
    //
    // Safety from rep exposure:
    //      All fields are private. allCopies and availableCopies both return defensive
    //      copies of their respective fields.
    
    public BigLibrary() {
        // Books in the library are sorted alphabetically by their first listed
        // author, then by title, then by year.
        final Comparator<Book> authorComparator =
                Comparator.comparing(book -> book.getAuthors().get(0));
        final Comparator<Book> titleComparator = Comparator.comparing(Book::getTitle);
        final Comparator<Book> yearComparator = Comparator.comparing(Book::getYear);
        final Comparator<Book> bookComparator =
                authorComparator.thenComparing(titleComparator).thenComparing(yearComparator);

        this.inLibrary = new TreeMap<>(bookComparator);
        this.checkedOut = new TreeMap<>(bookComparator);
        this.authors = new HashMap<>();
        this.titles = new HashMap<>();

        checkRep();
    }
    
    // assert the rep invariant
    private void checkRep() {
        for (Book current_book : inLibrary.keySet()){
            // Get all the copies of the current book, both in the library's possession
            // and checked out. We're making copies here because vanilla Java doesn't
            // have a non-destructive set intersection operation.
            HashSet<BookCopy> inLibraryCopy = new HashSet<>(
                    inLibrary.get(current_book));
            final HashSet<BookCopy> checkedOutCopy = new HashSet<>(
                    checkedOut.get(current_book));

            // Calculate the intersection of the two sets - it should be empty (i.e.,
            // every book that is not checked out is in the library's possession).
            inLibraryCopy.retainAll(checkedOutCopy);

            assert inLibraryCopy.equals(Collections.emptySet());
        }
    }

    @Override
    public BookCopy buy(Book book) {
        final BookCopy new_book = new BookCopy(book);

        // If the library already owns copies of this book, we'll add this copy to the set.
        if (this.inLibrary.containsKey(book)) {
            this.inLibrary.get(book).add(new_book);

        } else {
            // If this is the first copy of the book the library has purchased, we'll
            // create a new set of BookCopies to contain it, and add an empty set to
            // checkedOut for it.
            final HashSet<BookCopy> copies_of_new_book = new HashSet<>();
            copies_of_new_book.add(new_book);

            this.inLibrary.put(book, copies_of_new_book);
            this.checkedOut.put(book, new HashSet<>());

            // Since it's new, we'll also add its title and authors to the library's
            // collection.
            addTitleAndAuthors(book);
        }

        checkRep();
        return new_book;
    }

    private void addTitleAndAuthors(Book book) {
        // Add the book's authors to the library's collection.
        addAuthors(book);
        // Add the book's titles to the library's collection.
        addTitles(book);
        checkRep();
    }

    private void addAuthors(Book book) {
        // Add the book's authors to the set of authors in the library's collection.
        for (String author : book.getAuthors()) {
            // If this is not the first book by this author, we'll add it to the set of
            // books associated with that author.
            if (this.authors.containsKey(author)) {
                this.authors.get(author).add(book);
            } else {
                // Otherwise, it is the first book by this author.
                final Set<Book> new_author_books = new HashSet<>();
                new_author_books.add(book);
                this.authors.put(author, new_author_books);
            }
        }
        checkRep();
    }

    private void addTitles(Book book) {
        final String current_book_title = book.getTitle();

        // If the library already contains books by this title, we'll add the current
        // book to the collection under that title.
        if (this.titles.containsKey(current_book_title)) {
            this.titles.get(current_book_title).add(book);
        } else {
            // Otherwise, we'll set up a new collection under the title.
            final Set<Book> new_title_books = new HashSet<>();
            new_title_books.add(book);
            this.titles.put(current_book_title, new_title_books);
        }
        checkRep();
    }
    
    @Override
    public void checkout(BookCopy copy) {
        final Book book = copy.getBook();

        this.inLibrary.get(book).remove(copy);
        this.checkedOut.get(book).add(copy);
    }
    
    @Override
    public void checkin(BookCopy copy) {
        final Book book = copy.getBook();

        this.checkedOut.get(book).remove(copy);
        this.inLibrary.get(book).add(copy);
    }
    
    @Override
    public Set<BookCopy> allCopies(Book book) {
        // Find all copies of the book that are in the library's possession.
        final Set<BookCopy> foundCopies = availableCopies(book);

        // Find all copies of the book that are checked out.
        if (this.checkedOut.containsKey(book)) {
            foundCopies.addAll(this.checkedOut.get(book));
        }

        return foundCopies;
    }

    @Override
    public Set<BookCopy> availableCopies(Book book) {
        // Find all copies of the book that are in the library's possession.
        final HashSet<BookCopy> available_copies = new HashSet<>();

        if (this.inLibrary.containsKey(book)) {
            available_copies.addAll(this.inLibrary.get(book));
        }

        return available_copies;
    }
    
    @Override
    public boolean isAvailable(BookCopy copy) {
        final Book book = copy.getBook();
        if (this.inLibrary.containsKey(book)) {
            return this.inLibrary.get(book).contains(copy);
        } else {
            return false;
        }
    }
    
    @Override
    public List<Book> find(String query) {
        final List<Book> found_books = new ArrayList<>();

        // Check for an exact author match.
        if (this.authors.containsKey(query)) {
            found_books.addAll(this.authors.get(query));
        }

        if (this.titles.containsKey(query)) {
            found_books.addAll(this.titles.get(query));
        }

        found_books.sort(Comparator.comparing(Book::getYear).reversed());
        return found_books;
    }
    
    @Override
    public void lose(BookCopy copy) {
        final Book book = copy.getBook();

        this.inLibrary.get(book).remove(copy);
        this.checkedOut.get(book).remove(copy);

        checkRep();
    }


    /* Copyright (c) 2016 MIT 6.005 course staff, all rights reserved.
     * Redistribution of original or derived work requires explicit permission.
     * Don't post any of this code on the web or to a public Github repository.
     */

}
