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

    private final TreeMap<String, Set<Book>> authors;
    private final TreeMap<String, Set<Book>> titles;
    private final TreeMap<String, Set<Book>> keywords;

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
                authorComparator
                        .thenComparing(titleComparator)
                        .thenComparing(yearComparator);

        this.inLibrary = new TreeMap<>(bookComparator);
        this.checkedOut = new TreeMap<>(bookComparator);
        this.authors = new TreeMap<>();
        this.titles = new TreeMap<>();
        this.keywords = new TreeMap<>();

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

            // Finally, we'll add the new book's keywords to the library's collection.
            addKeywords(book);
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

    private void addKeywords(Book book) {
        // Add all keywords in the book's title.
        for (String word : book.getTitle().split("\\s+")) {
            if (this.keywords.containsKey(word)) {
                this.keywords.get(word).add(book);
            } else {
                final Set<Book> new_keyword_books = new HashSet<>();
                new_keyword_books.add(book);
                this.keywords.put(word, new_keyword_books);
            }
        }

        // Add all keywords in the book's list of authors.
        for (String author : book.getAuthors()) {
            for (String word : author.split("\\s+")) {
                if (this.keywords.containsKey(word)) {
                    this.keywords.get(word).add(book);
                } else {
                    final Set<Book> new_keyword_books = new HashSet<>();
                    new_keyword_books.add(book);
                    this.keywords.put(word, new_keyword_books);
                }
            }
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
        // First find any exact matches in authors or titles.
        final List<Book> found_books = new ArrayList<>(findExactMatches(query));

        // Then we'll find any partial matches in authors or titles.
        found_books.addAll(findPartialMatches(query));

        // Remove any duplicates generated by the search functions.
        final List<Book> found_books_no_duplicates =
                new ArrayList<>(
                        new HashSet<>(found_books));

        // Sort the found books so that newer books appear first.
        found_books_no_duplicates.sort(Comparator.comparing(Book::getYear).reversed());
        return found_books_no_duplicates;
    }

    private List<Book> findExactMatches(String query) {
        final List<Book> exact_matches = new ArrayList<>();

        // Check for an exact author match.
        if (this.authors.containsKey(query)) {
            exact_matches.addAll(this.authors.get(query));
        }

        // Check for an exact title match.
        if (this.titles.containsKey(query)) {
            exact_matches.addAll(this.titles.get(query));
        }

        return exact_matches;
    }

    private List<Book> findPartialMatches(String query) {
        final List<Book> partial_matches = new ArrayList<>();

        // Check each word in the query for partial matches to authors or titles.
        for (String word : query.split("\\s+")) {
            partial_matches.addAll(findPrefix(word, this.authors));
            partial_matches.addAll(findPrefix(word, this.titles));

            if (this.keywords.containsKey(word)) {
                partial_matches.addAll(this.keywords.get(word));
            }
        }

        return partial_matches;
    }

    private List<Book> findPrefix(String word, SortedMap<String, Set<Book>> map) {
        final List<Book> prefix_partial_matches = new ArrayList<>();

        // Search for authors or titles between the word itself and the word plus the
        // next letter in the alphabet (i.e., if word is "cat", we're searching for
        // authors and titles from "cat" to "cau").
        char nextLetterInAlphabet = (char) (word.charAt(word.length() - 1) + 1);
        String wordPlusNextLetter =
                word.substring(0, word.length() - 1) + nextLetterInAlphabet;

        // Get all the books whose titles or authors are a partial match.
        final Collection<Set<Book>> matching_books = map
                .subMap(word, wordPlusNextLetter)
                .values();

        // There could be multiple sets of books here, as there may have been multiple
        // matching authors or titles.
        for (Set<Book> booklist : matching_books) {
            prefix_partial_matches.addAll(booklist);
        }

        return prefix_partial_matches;
    }
    
    @Override
    public void lose(BookCopy copy) {
        final Book book = copy.getBook();

        this.inLibrary.get(book).remove(copy);
        this.checkedOut.get(book).remove(copy);

        // If this is the last copy of the book, its metadata should be removed, and its
        // key in the inLibrary and checkedOut maps should be removed, too.
        if (allCopies(book).isEmpty()) {
            for (Set<Book> authors_data : this.authors.values()) {
                authors_data.remove(book);
            }
            for (Set<Book> title_data : this.titles.values()) {
                title_data.remove(book);
            }
            for (Set<Book> keyword_data : this.keywords.values()) {
                keyword_data.remove(book);
            }

            this.inLibrary.remove(book);
            this.checkedOut.remove(book);
        }

        checkRep();
    }


    /* Copyright (c) 2016 MIT 6.005 course staff, all rights reserved.
     * Redistribution of original or derived work requires explicit permission.
     * Don't post any of this code on the web or to a public Github repository.
     */

}
