package twitter;

//import static org.junit.Assert.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.*;


public class SocialNetworkTest {

    /*
     * TESTING STRATEGY
     *
     * Partition input space as follows:
     *
     * tweets : 0, 1, >1
     * tweet authors : same author, different authors
     *               : case insensitivity
     * tweet text : 0 @mentions, 1 @mention, >1 @mentions
     *
     */
    private static final Instant d1 = Instant.parse("2016-02-17T10:00:00Z");
    private static final Instant d2 = Instant.parse("2016-02-17T11:00:00Z");

    private static final Tweet tweet1 = new Tweet(
            1,
            "alyssa",
            "@bbitdiddle, is it reasonable to talk about rivest so much?",
            d1);
    private static final Tweet tweet2 = new Tweet(
            2,
            "bbitdiddle",
            "rivest talk in 30 minutes #hype",
            d2);
    private static final Tweet tweet3 = new Tweet(
            3,
            "bbitdiddle",
            "MIT is so cool because @alyssa is there with @Remy!",
            d2);
    private static final Tweet tweet4 = new Tweet(
            4,
            "somed00d",
            "@alyssa is the coolest!",
            d2);

    @Test
    void testAssertionsEnabled() {
        assertThrows(AssertionError.class, () -> {
            assert false;
        }); // make sure assertions are enabled with VM argument: -ea
    }
    
    @Test
    void testGuessFollowsGraphDoesNotModifyInputList() {
        final List<Tweet> original_list = Arrays.asList(tweet1, tweet2, tweet3);
        final List<Tweet> input_list = Arrays.asList(tweet1, tweet2, tweet3);

        Map<String, Set<String>> discarded_results = SocialNetwork.guessFollowsGraph(
                input_list);

        assertEquals(original_list, input_list);
    }

    @Test
    void testGuessFollowsGraphEmpty() {
        Map<String, Set<String>> followsGraph = SocialNetwork.guessFollowsGraph(new ArrayList<>());
        
        assertTrue(followsGraph.isEmpty(), "expected empty graph");
    }

    @Test
    void testGuessFollowsGraphOneMention() {
        final Set<String> users_alyssa_follows = new HashSet<>();
        users_alyssa_follows.add("bbitdiddle");

        Map<String, Set<String>> followsGraph = SocialNetwork.guessFollowsGraph(
                Collections.singletonList(tweet1));

        assertEquals(1, followsGraph.size());
        assertEquals(users_alyssa_follows, followsGraph.get("alyssa"));
    }

    @Test
    void testGuessFollowsGraphMultipleMentions() {
        final Set<String> users_alyssa_follows = new HashSet<>();
        users_alyssa_follows.add("bbitdiddle");

        final Set<String> users_bbitdiddle_follows = new HashSet<>();
        users_bbitdiddle_follows.add("alyssa");
        users_bbitdiddle_follows.add("remy");

        Map<String, Set<String>> followsGraph = SocialNetwork.guessFollowsGraph(
                Arrays.asList(tweet1, tweet2, tweet3));

        assertTrue(followsGraph.containsKey("alyssa"));
        assertTrue(followsGraph.containsKey("bbitdiddle"));

        assertEquals(users_alyssa_follows, followsGraph.get("alyssa"));
        assertEquals(users_bbitdiddle_follows, followsGraph.get("bbitdiddle"));
    }
    
    @Test
    void testInfluencersDoesNotModifyInputList() {
        final Set<String> original_followed = new HashSet<>();
        original_followed.add("bbitdiddle");

        final Map<String, Set<String>> original_Graph = new HashMap<>();
        original_Graph.put("alyssa", original_followed);

        final Map<String, Set<String>> input_Graph = new HashMap<>();
        input_Graph.put("alyssa", original_followed);

        List<String> discarded_results = SocialNetwork.influencers(input_Graph);

        assertEquals(original_Graph, input_Graph);
    }

    @Test
    void testInfluencersEmpty() {
        Map<String, Set<String>> followsGraph = new HashMap<>();
        List<String> influencers = SocialNetwork.influencers(followsGraph);
        
        assertTrue(influencers.isEmpty(), "expected empty list");
    }

    @Test
    void testInfluencersOneAuthorGraph() {
        final Set<String> alyssa_followed = new HashSet<>();
        alyssa_followed.add("bbitdiddle");

        final Map<String, Set<String>> followsGraph = new HashMap<>();
        followsGraph.put("alyssa", alyssa_followed);

        List<String> influencers = SocialNetwork.influencers(followsGraph);

        assertEquals(1, influencers.size());
        assertEquals(0, influencers.indexOf("alyssa"));
    }

    @Test
    void testInfluencersMultiAuthorGraph() {
        final Set<String> alyssa_followed = new HashSet<>();
        alyssa_followed.add("bbitdiddle");

        final Set<String> bbitdiddle_followed = new HashSet<>();
        bbitdiddle_followed.add("remy");
        bbitdiddle_followed.add("alyssa");

        final Set<String> somed00d_followed = new HashSet<>();
        somed00d_followed.add("alyssa");

        final Map<String, Set<String>> followsGraph = new HashMap<>();
        followsGraph.put("alyssa", alyssa_followed);
        followsGraph.put("bbitdiddle", bbitdiddle_followed);
        followsGraph.put("somed00d", somed00d_followed);

        List<String> influencers = SocialNetwork.influencers(followsGraph);

        assertEquals(3, influencers.size());
        assertEquals(0, influencers.indexOf("alyssa"));
        assertTrue(influencers.indexOf("alyssa") > influencers.indexOf("bbitdiddle"));

        assertTrue(influencers.contains("alyssa"));
        assertTrue(influencers.contains("bbitdiddle"));
        assertTrue(influencers.contains("somed00d"));
    }

    /*
     * Warning: all the tests you write here must be runnable against any
     * SocialNetwork class that follows the spec. It will be run against several
     * staff implementations of SocialNetwork, which will be done by overwriting
     * (temporarily) your version of SocialNetwork with the staff's version.
     * DO NOT strengthen the spec of SocialNetwork or its methods.
     * 
     * In particular, your test cases must not call helper methods of your own
     * that you have put in SocialNetwork, because that means you're testing a
     * stronger spec than SocialNetwork says. If you need such helper methods,
     * define them in a different class. If you only need them in this test
     * class, then keep them in this test class.
     */


    /* Copyright (c) 2016 MIT 6.005 course staff, all rights reserved.
     * Redistribution of original or derived work requires explicit permission.
     * Don't post any of this code on the web or to a public Github repository.
     */
}
