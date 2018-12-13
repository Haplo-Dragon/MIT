package twitter;

//import static org.junit.Assert.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.lang.reflect.Array;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;


public class FilterTest {

    /*
     * TESTING STRATEGY:
     *
     * Partition input space as follows:
     *
     * tweets.length() : 0, 1, >1
     * username : found in tweet list, not found in tweet list
     *          : uppercase, lowercase, mixed case
     * timespan : instantaneous timespan (Instants equal each other), long timespan
     * words : empty words list, 1 word, >1 word
     *       : case insensitivity
     */
    
    private static final Instant d1 = Instant.parse("2016-02-17T10:00:00Z");
    private static final Instant d2 = Instant.parse("2016-02-17T11:00:00Z");
    
    private static final Tweet tweet1 = new Tweet(
            1,
            "alyssa",
            "is it reasonable to talk about rivest so much?",
            d1);
    private static final Tweet tweet2 = new Tweet(
            2,
            "bbitdiddle",
            "rivest talk in 30 minutes #hype",
            d2);
    private static final Tweet tweet3 = new Tweet(
            3,
            "bbitdiddle",
            "MIT is so cool!",
            d2);
    private static final List<String> multi_word_list =
            Arrays.asList("rivest", "talk", "hype");
    
    @Test
    void testAssertionsEnabled() {
        assertThrows(AssertionError.class, () -> {
            assert false;
        }); // make sure assertions are enabled with VM argument: -ea
    }
    
    @Test
    void testWrittenByDoesNotModifyInputList() {
        List<Tweet> original_list = Arrays.asList(tweet1, tweet2, tweet3);
        List<Tweet> input_list = Arrays.asList(tweet1, tweet2, tweet3);

        List<Tweet> discarded_results = Filter.writtenBy(input_list, "Remy");

        assertEquals(original_list, input_list);
    }

    @Test
    void testWrittenByNoTweets() {
        List<Tweet> emptyTweetList = new ArrayList<>();
        List<Tweet> writtenBy = Filter.writtenBy(emptyTweetList, "alyssa");

        assertTrue(writtenBy.isEmpty());
    }

    @Test
    void testWrittenBySingleTweetSingleResult() {
        List<Tweet> writtenBy = Filter.writtenBy(
                Collections.singletonList(tweet1), "alyssa");

        assertEquals(1, writtenBy.size());
        assertTrue(writtenBy.contains(tweet1));
    }

    @Test
    void testWrittenByMultipleTweetsNoResult() {
        List<Tweet> writtenBy = Filter.writtenBy(
                Arrays.asList(tweet1, tweet2), "Remy");

        assertTrue(writtenBy.isEmpty());
    }

    @Test
    void testWrittenByMultipleTweetsSingleResult() {
        List<Tweet> writtenBy = Filter.writtenBy(
                Arrays.asList(tweet1, tweet2), "alyssa");
        
        assertEquals(1, writtenBy.size(), "expected singleton list");
        assertTrue(writtenBy.contains(tweet1), "expected list to contain tweet");
    }

    @Test
    void testWrittenByMultipleTweetsMultipleResults() {
        List<Tweet> writtenBy = Filter.writtenBy(
                Arrays.asList(tweet1, tweet2, tweet3), "bbitdiddle");

        assertEquals(2, writtenBy.size());
        assertTrue(writtenBy.contains(tweet2));
        assertTrue(writtenBy.contains(tweet3));
        assertEquals(1, writtenBy.indexOf(tweet3));
    }

    @Test
    void testWrittenByMixedCaseAuthor(){
        List<Tweet> writtenBy = Filter.writtenBy(
                Arrays.asList(tweet1, tweet2), "ALYssA");

        assertEquals(1, writtenBy.size());
        assertTrue(writtenBy.contains(tweet1));
    }

    @Test
    void testInTimespanDoesNotModifyInputList() {
        Instant testStart = Instant.parse("2016-02-17T09:00:00Z");
        Instant testEnd = Instant.parse("2016-02-17T10:00:00Z");

        List<Tweet> original_list = Arrays.asList(tweet1, tweet2);
        List<Tweet> input_list = Arrays.asList(tweet1, tweet2);

        List<Tweet> discarded_results = Filter.inTimespan(
                input_list, new Timespan(testStart, testEnd));

        assertEquals(original_list, input_list);
    }
    
    @Test
    void testInTimespanSingleTweetNoResult() {
        Instant testStart = Instant.parse("2016-02-17T09:00:00Z");
        Instant testEnd = Instant.parse("2016-02-17T10:00:00Z");

        List<Tweet> inTimespan = Filter.inTimespan(
                Collections.singletonList(tweet2), new Timespan(testStart, testEnd));

        assertTrue(inTimespan.isEmpty());
    }

    @Test
    void testInTimespanMultipleTweetsSingleResult() {
        Instant testStart = Instant.parse("2016-02-17T09:00:00Z");
        Instant testEnd = Instant.parse("2016-02-17T10:00:00Z");

        List<Tweet> inTimespan = Filter.inTimespan(
                Arrays.asList(tweet1, tweet2), new Timespan(testStart, testEnd));

        assertEquals(1, inTimespan.size());
        assertTrue(inTimespan.contains(tweet1));
    }

    @Test
    void testInTimespanMultipleTweetsMultipleResults() {
        Instant testStart = Instant.parse("2016-02-17T09:00:00Z");
        Instant testEnd = Instant.parse("2016-02-17T12:00:00Z");
        
        List<Tweet> inTimespan = Filter.inTimespan(
                Arrays.asList(tweet1, tweet2), new Timespan(testStart, testEnd));
        
        assertFalse(inTimespan.isEmpty(), "expected non-empty list");
        assertTrue(inTimespan.containsAll(
                Arrays.asList(tweet1, tweet2)), "expected list to contain tweets");
        assertEquals(0, inTimespan.indexOf(tweet1), "expected same order");
    }

    @Test
    void testInTimespanMultipleTweetsInstantaneousTimespan() {
        Instant testStart = Instant.parse("2016-02-17T11:00:00Z");
        Instant testEnd = Instant.parse("2016-02-17T11:00:00Z");

        List<Tweet> inTimespan = Filter.inTimespan(
                Arrays.asList(tweet1, tweet2, tweet3), new Timespan(testStart, testEnd));

        assertEquals(2, inTimespan.size());
        assertTrue(inTimespan.containsAll(Arrays.asList(tweet2, tweet3)));
        assertEquals(1, inTimespan.indexOf(tweet3));
    }

    @Test
    void testContainingDoesNotModifyInputList() {
        List<Tweet> original_list = Arrays.asList(tweet1, tweet2);
        List<Tweet> input_list = Arrays.asList(tweet1, tweet2);

        List<Tweet> discarded_results = Filter.containing(
                input_list, Collections.singletonList("reasonable"));

        assertEquals(original_list, input_list);
    }

    @Test
    void testContainingEmptyWordList() {
        List<String> emptyWordList = new ArrayList<>();
        List<Tweet> containing = Filter.containing(
                Arrays.asList(tweet1, tweet2), emptyWordList);

        assertTrue(containing.isEmpty());
    }

    @Test
    void testContainingSingleWordMultipleResults() {
        List<Tweet> containing = Filter.containing(Arrays.asList(tweet1, tweet2),
                Collections.singletonList("talk"));
        
        assertFalse(containing.isEmpty(), "expected non-empty list");
        assertTrue(containing.containsAll(
                Arrays.asList(tweet1, tweet2)), "expected list to contain tweets");
        assertEquals(0, containing.indexOf(tweet1), "expected same order");
    }

    @Test
    void testContainingMultipleWordsSingleResult() {
        List<String> search_words = Arrays.asList("mit", "cool");
        List<Tweet> containing = Filter.containing(
                Arrays.asList(tweet1, tweet2, tweet3), search_words);

        assertEquals(1, containing.size());
        assertTrue(containing.contains(tweet3));
    }

    /*
     * Warning: all the tests you write here must be runnable against any Filter
     * class that follows the spec. It will be run against several staff
     * implementations of Filter, which will be done by overwriting
     * (temporarily) your version of Filter with the staff's version.
     * DO NOT strengthen the spec of Filter or its methods.
     * 
     * In particular, your test cases must not call helper methods of your own
     * that you have put in Filter, because that means you're testing a stronger
     * spec than Filter says. If you need such helper methods, define them in a
     * different class. If you only need them in this test class, then keep them
     * in this test class.
     */


    /* Copyright (c) 2016 MIT 6.005 course staff, all rights reserved.
     * Redistribution of original or derived work requires explicit permission.
     * Don't post any of this code on the web or to a public Github repository.
     */
}
