package twitter;

import static org.junit.jupiter.api.Assertions.*;

import com.sun.xml.internal.ws.api.ha.StickyFeature;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.*;

public class ExtractTest {

    /* TESTING STRATEGY
     *
     * Partition input space as follows:
     *
     * tweets.length(): 0, 1, >1
     * tweets.get(tweet).getTimestamp(): same timestamps or different timestamps
     *
     * tweets.get(tweet).getText(): Empty, 0 mentions, 1 mention, >1 mentions
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
    private static final Tweet twin1 = new Tweet(
            3,
            "bbitdiddle",
            "I'm a twin!",
            d1
    );
    private static final Tweet twin2 = new Tweet(
            4,
            "bbitdiddle",
            "We're twinsies!",
            d1
    );

    private static final Tweet empty_tweet = new Tweet(
            5,
            "bbitdiddle",
            "",
            d1
    );
    private static final Tweet one_mention = new Tweet(
            6,
            "bbitdiddle",
            "@alyssa is my favorite Twitter buddy!",
            d1
    );
    private static final Tweet many_mentions = new Tweet(
            7,
            "bbitdiddle",
            "@alyssa and @bbitdiddle are cool - their email is cool@mit.edu",
            d1
    );

    private static final Set<String> expectedUsers = new HashSet<>(
            Arrays.asList("alyssa", "bbitdiddle"));
    
    @Test
    public void testAssertionsEnabled() {
        assertThrows(AssertionError.class, () -> {
            assert false;
        });
    }

    /*
      Because the postcondition is underdetermined (in several ways), testing
      an empty list of tweets is problematic. We can't depend on the method to
      throw a particular exception or to return a particular value in this case,
      as the spec doesn't mention its behavior. So our test merely checks to see
      that the method returns a result AT ALL (i.e., it doesn't fail or return null).
    */
    @Test
    public void testGetTimespanNoTweets() {
        Timespan timespan = Extract.getTimespan(Collections.emptyList());
        assertNotNull(timespan);
    }

    @Test
    public void testGetTimespanOneTweet() {
        Timespan timespan = Extract.getTimespan(Collections.singletonList(tweet1));
        assertEquals(d1, timespan.getStart());
        assertEquals(d1, timespan.getEnd());
    }
    
    @Test
    public void testGetTimespanTwoTweets() {
        Timespan timespan = Extract.getTimespan(Arrays.asList(tweet1, tweet2));

        assertEquals(d1, timespan.getStart(), "expected start");
        assertEquals(d2, timespan.getEnd(), "expected end");
    }

    @Test
    public void testGetTimespanIdenticalTimestamps() {
        Timespan timespan = Extract.getTimespan(Arrays.asList(twin1, twin2));

        assertEquals(d1, timespan.getStart());
        assertEquals(d1, timespan.getEnd());
    }

    @Test
    public void testGetTimespanDoesNotModifyInputList() {
        final List<Tweet> tweets = Arrays.asList(tweet1, tweet2);
        final List<Tweet> tweets_copy = Arrays.asList(tweet1, tweet2);

        Timespan timespan = Extract.getTimespan(tweets);

        assertEquals(tweets, tweets_copy);

    }
    
    @Test
    public void testGetMentionedUsersNoText() {
        Set<String> mentionedUsers = Extract.getMentionedUsers(
                Collections.singletonList(empty_tweet));

        assertTrue(mentionedUsers.isEmpty());
    }

    @Test
    public void testGetMentionedUsersNoMention() {
        Set<String> mentionedUsers = Extract.getMentionedUsers(
                Collections.singletonList(tweet1));
        
        assertTrue(mentionedUsers.isEmpty(), "expected empty set");
    }

    @Test
    public void testGetMentionedUsersOneMention() {
        Set<String> mentionedUsers = Extract.getMentionedUsers(
                Collections.singletonList(one_mention));

        assertEquals(mentionedUsers.size(), 1);

        for (String user : mentionedUsers) {
            assertTrue(expectedUsers.contains(user.toLowerCase()));
        }
    }

    @Test
    public void testGetMentionedUsersManyMentions() {
        Set<String> mentionedUsers = Extract.getMentionedUsers(
                Arrays.asList(one_mention, many_mentions));

        assertEquals(mentionedUsers.size(), 2);

        for (String user : mentionedUsers) {
            assertTrue(expectedUsers.contains(user.toLowerCase()));
        }
    }

    @Test
    public void testGetMentionedUsersDoesNotModifyInputList() {
        final List<Tweet> tweets = Arrays.asList(one_mention, many_mentions);
        final List<Tweet> tweets_copy = Arrays.asList(one_mention, many_mentions);

        Set<String> mentionedUsers = Extract.getMentionedUsers(tweets);

        assertEquals(tweets, tweets_copy);
    }

    /*
     * Warning: all the tests you write here must be runnable against any
     * Extract class that follows the spec. It will be run against several staff
     * implementations of Extract, which will be done by overwriting
     * (temporarily) your version of Extract with the staff's version.
     * DO NOT strengthen the spec of Extract or its methods.
     * 
     * In particular, your test cases must not call helper methods of your own
     * that you have put in Extract, because that means you're testing a
     * stronger spec than Extract says. If you need such helper methods, define
     * them in a different class. If you only need them in this test class, then
     * keep them in this test class.
     */


    /* Copyright (c) 2016 MIT 6.005 course staff, all rights reserved.
     * Redistribution of original or derived work requires explicit permission.
     * Don't post any of this code on the web or to a public Github repository.
     */

}
