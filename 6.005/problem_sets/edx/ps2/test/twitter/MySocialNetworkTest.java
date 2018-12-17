package twitter;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.*;

class MySocialNetworkTest {
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
            "MIT is so cool because @alyssa is there with @Remy! #MIT #cool",
            d2);
    private static final Tweet tweet4 = new Tweet(
            4,
            "somed00d",
            "@alyssa is the coolest! #MIT #cool",
            d2);

    @Test
    void testGuessFollowsGraphConcurrentTweetsIndicateFollows(){
        final Set<String> users_somed00d_follows = new HashSet<>();
        users_somed00d_follows.add("alyssa");
        users_somed00d_follows.add("bbitdiddle");

        final Map<String, Set<String>> followsGraph = SocialNetwork.guessFollowsGraph(
                Arrays.asList(tweet1, tweet2, tweet3, tweet4));

        assertEquals(users_somed00d_follows, followsGraph.get("somed00d"));
    }
}
