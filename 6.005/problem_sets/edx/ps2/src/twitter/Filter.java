package twitter;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * Filter consists of methods that filter a list of tweets for those matching a
 * condition.
 * 
 * DO NOT change the method signatures and specifications of these methods, but
 * you should implement their method bodies, and you may add new public or
 * private methods or classes if you like.
 */
public class Filter {

    /**
     * Find tweets written by a particular user.
     * 
     * @param tweets
     *            a list of tweets with distinct ids, not modified by this method.
     * @param username
     *            Twitter username, required to be a valid Twitter username as
     *            defined by Tweet.getAuthor()'s spec.
     * @return all and only the tweets in the list whose author is username,
     *         in the same order as in the input list.
     */
    static List<Tweet> writtenBy(List<Tweet> tweets, String username) {
        final String user_to_find = username.toLowerCase();
        final List<Tweet> found_tweets = new ArrayList<>();

        for (Tweet tweet : tweets) {
            if(tweet.getAuthor().equals(user_to_find)) {
                found_tweets.add(tweet);
            }
        }

        return found_tweets;
    }

    /**
     * Find tweets that were sent during a particular timespan.
     * 
     * @param tweets
     *            a list of tweets with distinct ids, not modified by this method.
     * @param timespan
     *            timespan
     * @return all and only the tweets in the list that were sent during the timespan,
     *         in the same order as in the input list.
     */
    static List<Tweet> inTimespan(List<Tweet> tweets, Timespan timespan) {
        final List<Tweet> found_tweets = new ArrayList<>();

        for (Tweet tweet : tweets) {
            if (isWithinTimespan(tweet, timespan)){
                found_tweets.add(tweet);
            }
        }

        return found_tweets;
    }

    /**
     * Find tweets that contain certain words.
     * 
     * @param tweets
     *            a list of tweets with distinct ids, not modified by this method.
     * @param words
     *            a list of words to search for in the tweets. 
     *            A word is a nonempty sequence of nonspace characters.
     * @return all and only the tweets in the list such that the tweet text (when 
     *         represented as a sequence of nonempty words bounded by space characters 
     *         and the ends of the string) includes *at least one* of the words 
     *         found in the words list. Word comparison is not case-sensitive,
     *         so "Obama" is the same as "obama".  The returned tweets are in the
     *         same order as in the input list.
     */
    static List<Tweet> containing(List<Tweet> tweets, List<String> words) {
        // Generate a case-insensitive list of search terms.
        final List<String> search_words = new ArrayList<>();
        for (String word : words) {
            search_words.add(word.toLowerCase());
        }
        final List<Tweet> found_tweets = new ArrayList<>();

        // Search each tweet's text for the search terms.
        for (Tweet tweet : tweets) {
            String current_tweet_text = tweet.getText();
            for (String word : search_words) {
                // Only add the tweet if it matches a search term and has not already been
                // added.
                if (current_tweet_text.contains(word) && !(found_tweets.contains(tweet))){
                    found_tweets.add(tweet);
                }
            }
        }

        return found_tweets;
    }

    private static boolean isWithinTimespan(Tweet tweet, Timespan timespan) {
        final Instant start = timespan.getStart();
        final Instant end = timespan.getEnd();
        final Instant timestamp = tweet.getTimestamp();

        final boolean on_or_after_start =
                timestamp.isAfter(start) || timestamp.equals(start);
        final boolean on_or_before_end =
                timestamp.isBefore(end) || timestamp.equals(end);

        return (on_or_after_start && on_or_before_end);
    }

    /* Copyright (c) 2007-2016 MIT 6.005 course staff, all rights reserved.
     * Redistribution of original or derived work requires explicit permission.
     * Don't post any of this code on the web or to a public Github repository.
     */
}
