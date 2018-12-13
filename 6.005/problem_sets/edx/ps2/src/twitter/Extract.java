package twitter;

import java.sql.Time;
import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Extract consists of methods that extract information from a list of tweets.
 * 
 * DO NOT change the method signatures and specifications of these methods, but
 * you should implement their method bodies, and you may add new public or
 * private methods or classes if you like.
 */
public class Extract {

    /**
     * Get the time period spanned by tweets.
     * 
     * @param tweets
     *            list of tweets with distinct ids, not modified by this method.
     * @return a minimum-length time interval that contains the timestamp of
     *         every tweet in the list.
     */
    public static Timespan getTimespan(List<Tweet> tweets) {
        // The spec is underdetermined about what to do if we get an empty list.
        // Ideally, it would throw an exception, but for now we'll just return something
        // that isn't null.
        if (tweets.isEmpty()) {
            return new Timespan(Instant.MIN, Instant.MIN);
        }

        // Initialize the earliest and latest timestamp to their opposite max values.
        Instant earliest_timestamp = Instant.MAX;
        Instant latest_timestamp = Instant.MIN;

        // Look at each tweet's timestamp.
        for (Tweet tweet : tweets) {
            Instant current_timestamp = tweet.getTimestamp();

            // If there's an earlier start or a later end, store it.
            if (current_timestamp.isBefore(earliest_timestamp)) {
                earliest_timestamp = current_timestamp;
            }
            if (current_timestamp.isAfter(latest_timestamp)) {
                latest_timestamp = current_timestamp;
            }

        }

        // Make a Timespan from the earliest start and latest end, and return it.
        return new Timespan(earliest_timestamp, latest_timestamp);
    }

    /**
     * Get usernames mentioned in a list of tweets.
     * 
     * @param tweets
     *            list of tweets with distinct ids, not modified by this method.
     * @return the set of usernames who are mentioned in the text of the tweets.
     *         A username-mention is "@" followed by a Twitter username (as
     *         defined by Tweet.getAuthor()'s spec).
     *         The username-mention cannot be immediately preceded or followed by any
     *         character valid in a Twitter username.
     *         For this reason, an email address like bitdiddle@mit.edu does NOT 
     *         contain a mention of the username mit.
     *         Twitter usernames are case-insensitive, and the returned set may
     *         include a username at most once.
     */
    public static Set<String> getMentionedUsers(List<Tweet> tweets) {
        final Set<String> mentionedUsers = new HashSet<>();

        Pattern twitter_username_pattern = Pattern.compile(
            // NO valid Twitter username characters (as defined in Tweet.getAuthor())...
            "\\B" +
            // ...followed by ONE @ character...
            "@{1}" +
            // ...followed by any number of valid Twitter username characters...
            "[a-zA-Z\\d_-]+" +
            // ...followed by NO valid Twitter username characters.
            "[^a-zA-Z\\d_-]");

        // Get text of each tweet.
        for (Tweet tweet : tweets) {
            String currentText = tweet.getText();

            // Find usernames in text and add them to set.
            Matcher matcher = twitter_username_pattern.matcher(currentText);
            while (matcher.find()) {
                // +1 and -1 because we don't want to include the @ symbol or the character
                // following the end of the username.
                String found_username = currentText.substring(
                        matcher.start() + 1, matcher.end() - 1);
                mentionedUsers.add(found_username);
            }
        }

        return mentionedUsers;
    }

    /* Copyright (c) 2007-2016 MIT 6.005 course staff, all rights reserved.
     * Redistribution of original or derived work requires explicit permission.
     * Don't post any of this code on the web or to a public Github repository.
     */
}
