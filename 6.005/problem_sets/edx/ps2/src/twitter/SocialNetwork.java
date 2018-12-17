package twitter;

import java.time.Instant;
import java.util.*;

/**
 * SocialNetwork provides methods that operate on a social network.
 * 
 * A social network is represented by a Map<String, Set<String>> where map[A] is
 * the set of people that person A follows on Twitter, and all people are
 * represented by their Twitter usernames. Users can't follow themselves. If A
 * doesn't follow anybody, then map[A] may be the empty set, or A may not even exist
 * as a key in the map; this is true even if A is followed by other people in the network.
 * Twitter usernames are not case sensitive, so "ernie" is the same as "ERNie".
 * A username should appear at most once as a key in the map or in any given
 * map[A] set.
 * 
 * DO NOT change the method signatures and specifications of these methods, but
 * you should implement their method bodies, and you may add new public or
 * private methods or classes if you like.
 */
class SocialNetwork {

    // The span of time (in seconds) two tweets must fall into to be considered
    // "concurrent".
    private static final long CONCURRENCE_THRESHOLD_IN_SECONDS = 10;

    /**
     * Guess who might follow whom, from evidence found in tweets.
     * 
     * @param tweets
     *            a list of tweets providing the evidence, not modified by this
     *            method.
     * @return a social network (as defined above) in which Ernie follows Bert
     *         if and only if there is evidence for it in the given list of
     *         tweets.
     *         One kind of evidence that Ernie follows Bert is if Ernie
     *         @ mentions Bert in a tweet. This must be implemented. Other kinds
     *         of evidence may be used at the implementor's discretion.
     *         All the Twitter usernames in the returned social network must be
     *         either authors or @-mentions in the list of tweets.
     *
     *         Another kind of evidence that Ernie follows Bert is if Bert's tweet is
     *         closely followed by Ernie's. Concurrent tweets could imply that Ernie saw
     *         Bert's tweet (because Ernie follows Bert), then tweeted his own.
     */
    static Map<String, Set<String>> guessFollowsGraph(List<Tweet> tweets) {
        final Map<String, Set<String>> followsGraph = new HashMap<>();

        // Get all tweet authors.
        final Set<String> authors = new HashSet<>();
        for (Tweet tweet : tweets) {
            authors.add(tweet.getAuthor().toLowerCase());
        }

        // Initially, all authors follow no one.
        for (String author : authors) {
            followsGraph.put(author, new HashSet<>());
        }

        // Look for all tweets by a specific author.
        for (String current_author : authors) {
            List<Tweet> tweets_by_current_author = Filter.writtenBy(tweets, current_author);

            // Get all @mentions from the current author's tweets.
            Set<String> mentioned_users =
                    Extract.getMentionedUsers(tweets_by_current_author);

            // You can't follow yourself.
            if (mentioned_users.contains(current_author)) {
                mentioned_users.remove(current_author);
            }

            // Add the @mentions for the current author to the follow graph.
            mentioned_users.addAll(followsGraph.get(current_author));
            followsGraph.put(current_author, mentioned_users);

            // Look for all tweets that fall within a specified number of seconds.
            for (Tweet source_tweet : tweets_by_current_author) {
                // Get all tweets that fall within the specified time interval.
                List<Tweet> concurrent_tweets = getConcurrentTweets(source_tweet, tweets);

                // Add the current author to the follows graph of each author of a
                // concurrent tweet.
                for (Tweet concurrent_tweet : concurrent_tweets) {
                    String following_author = concurrent_tweet.getAuthor();
                    Set<String> current_follows = followsGraph.get(following_author);
                    current_follows.add(current_author);
                    followsGraph.put(following_author, current_follows);
                }
            }
        }

        return followsGraph;
    }

    private static List<Tweet> getConcurrentTweets(
            Tweet source_tweet, List<Tweet> all_tweets) {
        Instant current_timestamp = source_tweet.getTimestamp();
        Timespan concurrence_timespan = new Timespan(
                current_timestamp,
                current_timestamp.plusSeconds(CONCURRENCE_THRESHOLD_IN_SECONDS));

        List<Tweet> concurrent_tweets = Filter.inTimespan(
                all_tweets, concurrence_timespan);

        // Remove any tweets from the source tweet's author (you can't follow yourself).
        concurrent_tweets.removeIf(
                concurrent_tweet ->
                        concurrent_tweet.getAuthor().equals(source_tweet.getAuthor()));

        return concurrent_tweets;
    }

    /**
     * Find the people in a social network who have the greatest influence, in
     * the sense that they have the most followers.
     * 
     * @param followsGraph
     *            a social network (as defined above)
     * @return a list of all distinct Twitter usernames in followsGraph, in
     *         descending order of follower count.
     */
    static List<String> influencers(Map<String, Set<String>> followsGraph) {
        // Get all usernames from social network.
        final Set<String> all_usernames = getAllUsernames(followsGraph);

        final TreeMap<String, Integer> follower_count = new TreeMap<>();
        // Look at each username in the social network.
        for (String current_username : all_usernames) {
            // The username starts with zero followers.
            follower_count.put(current_username, 0);

            // Count the number of @mentions for each username.
            for (Set<String> mentions : followsGraph.values()) {
                if (mentions.contains(current_username)) {
                    follower_count.put(
                            current_username, follower_count.get(current_username) + 1);
                }
            }
        }

        // Sort influencers by follower count in descending order.
        return sortInfluencersByFollowerCount(follower_count, true);
    }

    private static Set<String> getAllUsernames(Map<String, Set<String>> followsGraph) {
        final Set<String> all_usernames = new HashSet<>(followsGraph.keySet());

        for (String current_author : followsGraph.keySet()) {
            all_usernames.addAll(followsGraph.get(current_author));
        }

        return all_usernames;
    }

    private static List<String> sortInfluencersByFollowerCount(
            TreeMap<String, Integer> follower_count, boolean descending_order) {
        // Make a list of map entries from follower_count (initially sorted in ascending
        // order).
        List<Map.Entry<String, Integer>> usernames_by_value =
                new ArrayList<>(follower_count.entrySet());
        usernames_by_value.sort(Map.Entry.comparingByValue());

        if (descending_order) {
            // Sort influencers in descending order of follower count.
            Collections.reverse(usernames_by_value);
        }

        // Use the sorted list of map entries to create a sorted list of usernames.
        final List<String> influencers = new ArrayList<>();
        for (Map.Entry entry : usernames_by_value) {
            influencers.add((String) entry.getKey());
        }

        return influencers;
    }

    /* Copyright (c) 2007-2016 MIT 6.005 course staff, all rights reserved.
     * Redistribution of original or derived work requires explicit permission.
     * Don't post any of this code on the web or to a public Github repository.
     */
}
