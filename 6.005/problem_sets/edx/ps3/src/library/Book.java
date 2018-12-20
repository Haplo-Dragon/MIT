package library;

import java.util.*;

/**
 * Book is an immutable type representing an edition of a book -- not the physical object, 
 * but the combination of words and pictures that make up a book.  Each book is uniquely
 * identified by its title, author list, and publication year.  Alphabetic case and author 
 * order are significant, so a book written by "Fred" is different than a book written by "FRED".
 */
public class Book {

    private final String title;
    private final List<String> authors;
    private final int year;

    /*
      REP INVARIANT:
      Title must contain at least one non-space character
      Authors must contain at least one name
           Each name must contain at least one non-space character
      Year represented in conventional CE calendar, must be non-negative.

      ABSTRACTION FUNCTION:
      Represents an edition of a book (not the physical object that there may be many
      copies of). Uniquely identified by title, authors, and publication year.

      SAFETY FROM REP EXPOSURE:
      All fields are private, and all types in the rep are immutable.
     */
    
    /**
     * Make a Book.
     * @param title Title of the book. Must contain at least one non-space character.
     * @param authors Names of the authors of the book.  Must have at least one name, and
     *               each name must contain at least one non-space character.
     * @param year Year when this edition was published in the conventional (Common Era)
     *            calendar.  Must be non-negative.
     */
    Book(String title, List<String> authors, int year) {
        this.title = title;
        this.authors = Collections.unmodifiableList(authors);
        this.year = year;

        checkRep();
    }
    
    // assert the rep invariant
    private void checkRep() {
        assert !(title.isEmpty());
        assert !(authors.isEmpty());
        assert year >= 0;
    }
    
    /**
     * @return the title of this book
     */
    String getTitle() {
        return this.title;
    }
    
    /**
     * @return the authors of this book
     */
    List<String> getAuthors() {
        return new ArrayList<>(this.authors);
    }

    /**
     * @return the year that this book was published
     */
    int getYear() {
        return this.year;
    }

    /**
     * @return human-readable representation of this book that includes its title,
     *    authors, and publication year
     */
    public String toString() {
        return "Authors: " + this.authors +
                "\nTitle: " + this.title +
                "\nYear: " + this.year;
    }

    // Uncomment the following methods if you need to implement equals and hashCode,
    // or delete them if you don't
     @Override
     public boolean equals(Object that) {
         if (!(that instanceof Book)) return false;
         Book thatBook = (Book) that;

         return ((thatBook.getTitle().equals(this.getTitle())) &&
                 (thatBook.getAuthors().equals(this.getAuthors())) &&
                 (thatBook.getYear() == this.getYear()));
     }

     @Override
     public int hashCode() {
              return 31 *
                      Objects.hashCode(this.title) +
                      Objects.hashCode(this.authors) +
                      Objects.hashCode(this.year);
    }



    /* Copyright (c) 2016 MIT 6.005 course staff, all rights reserved.
     * Redistribution of original or derived work requires explicit permission.
     * Don't post any of this code on the web or to a public Github repository.
     */

}
