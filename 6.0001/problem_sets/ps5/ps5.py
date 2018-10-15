# 6.0001/6.00 Problem Set 5 - RSS Feed Filter
# Name:
# Collaborators: Python docs
# Time:

import feedparser
import string
import re
import time
import threading
from project_util import translate_html
from mtTkinter import *
from datetime import datetime
import pytz


POS_TESTS = [
    'PURPLE COW',
    'The purple cow is soft and cuddly.',
    'The farmer owns a really PURPLE cow.',
    'Purple!!! Cow!!!',
    'purple@#$%cow',
    'Did you see a purple cow?', ]

NEG_TESTS = [
    'Purple cows are cool!',
    'The purple blob over there is a cow.',
    'How now brown cow.',
    'Cow!!! Purple!!!',
    'purplecowpurplecowpurplecow', ]


# -----------------------------------------------------------------------

# ======================
# Code for retrieving and parsing
# Google and Yahoo News feeds
# Do not change this code
# ======================


def process(url):
    """
    Fetches news items from the rss url and parses them.
    Returns a list of NewsStory-s.
    """
    feed = feedparser.parse(url)
    entries = feed.entries
    ret = []
    for entry in entries:
        guid = entry.guid
        title = translate_html(entry.title)
        link = entry.link
        description = translate_html(entry.description)
        pubdate = translate_html(entry.published)

        try:
            pubdate = datetime.strptime(pubdate, "%a, %d %b %Y %H:%M:%S %Z")
            pubdate.replace(tzinfo=pytz.timezone("GMT"))
        #  pubdate = pubdate.astimezone(pytz.timezone('EST'))
        #  pubdate.replace(tzinfo=None)
        except ValueError:
            pubdate = datetime.strptime(pubdate, "%a, %d %b %Y %H:%M:%S %z")

        newsStory = NewsStory(guid, title, description, link, pubdate)
        ret.append(newsStory)
    return ret


# ======================
# Data structure design
# ======================

# Problem 1
class NewsStory(object):
    def __init__(self, guid, title, description, link, pubdate):
        """
        Initializes a NewsStory object.

        guid (string) : a globally unique identifier
        title (string) : a title for the news story
        description (string) : a description for the news story
        link (string) : a link to more news content
        pubdate (datetime): the publication date of the news story
        """
        self.guid = guid
        self.title = title
        self.description = description
        self.link = link
        self.pubdate = pubdate

    def get_guid(self):
        """
        Used to safely access guid outside of class.
        """
        return self.guid

    def get_title(self):
        """
        Used to safely access title outside of class.
        """
        return self.title

    def get_description(self):
        """
        Used to safely access description outside of class.
        """
        return self.description

    def get_link(self):
        """
        Used to safely access link outside of class.
        """
        return self.link

    def get_pubdate(self):
        """
        Used to safely access publication date outside of class.
        """
        return self.pubdate


# ======================
# Triggers
# ======================


class Trigger(object):
    def evaluate(self, story):
        """
        Returns True if an alert should be generated
        for the given news item, or False otherwise.
        """
        # DO NOT CHANGE THIS!
        raise NotImplementedError


# PHRASE TRIGGERS

# Problem 2
# TODO: PhraseTrigger
class PhraseTrigger(Trigger):
    def __init__(self, phrase):
        """
        Initializes a PhraseTrigger object.

        phrase (string): The phrase to be used as a trigger for news stories
        """
        self.phrase = phrase.lower()

    def is_phrase_in(self, text):
        """
        Returns true if phrase is present in text.

        text (string): Text to be checked for the phrase
        """
        text = text.lower()

        # This regular expression pattern will match any consecutive characters that are
        # NOT letters
        pattern = r'[^\w]+'
        # Remove any instances of that pattern and replace them with a single space
        cleaned_up_text = re.sub(pattern, " ", text)

        # This pattern will match the phrase exactly (it won't match "purple cow"
        # to "purple cows", for instance)
        phrase_pattern = r'\b' + self.phrase + r'\b'

        # Find all the occurences of phrase in the cleaned-up text
        match = re.search(phrase_pattern, cleaned_up_text)
        # True if there are any matches, False if there are no matches
        phrase_found = bool(match)

        return phrase_found


# Problem 3
# TODO: TitleTrigger
class TitleTrigger(PhraseTrigger):
    def __init__(self, phrase):
        """
        Initializes a TitleTrigger object.

        phrase (string): The phrase to be used as a trigger for news titles.
        """
        PhraseTrigger.__init__(self, phrase)

    def evaluate(self, story):
        return self.is_phrase_in(story.get_title())


# Problem 4
# TODO: DescriptionTrigger
class DescriptionTrigger(PhraseTrigger):
    def __init__(self, phrase):
        """
        Initializes a DescriptionTrigger object.

        phrase(string): The phrase to be used as a trigger for news descriptions.
        """
        PhraseTrigger.__init__(self, phrase)

    def evaluate(self, story):
        return self.is_phrase_in(story.get_description())


# TIME TRIGGERS

# Problem 5
# TODO: TimeTrigger
class TimeTrigger(Trigger):
    def __init__(self, time):
        # Constructor:
        # Input: Time has to be in EST and in the format of "%d %b %Y %H:%M:%S".
        # Convert time from string to a datetime before saving it as an attribute.
        time = datetime.strptime(time, "%d %b %Y %H:%M:%S")
        time = time.replace(tzinfo=pytz.timezone("EST"))
        self.time = time


# Problem 6
# TODO: BeforeTrigger and AfterTrigger
class BeforeTrigger(TimeTrigger):
    def __init__(self, time):
        TimeTrigger.__init__(self, time)

    def evaluate(self, story):
        return story.get_pubdate() < self.time


class AfterTrigger(TimeTrigger):
    def __init__(self, time):
        TimeTrigger.__init__(self, time)

    def evaluate(self, story):
        return story.get_pubdate() > self.time

# COMPOSITE TRIGGERS


# Problem 7
# TODO: NotTrigger
class NotTrigger(Trigger):
    def __init__(self, trigger):
        """
        Initializes a NotTrigger object.

        trigger (trigger): An instance of a subclass of PhraseTrigger
        """
        self.trigger = trigger

    def evaluate(self, story):
        return not self.trigger.evaluate(story)


# Problem 8
# TODO: AndTrigger
class AndTrigger(Trigger):
    def __init__(self, first_trigger, second_trigger):
        """
        Initializes an AndTrigger object.

        first_trigger (trigger): An instance of a subclass of PhraseTrigger
        second_trigger (trigger): An instance of a subclass of PhraseTrigger
        """
        self.first_trigger = first_trigger
        self.second_trigger = second_trigger

    def evaluate(self, story):
        return (self.first_trigger.evaluate(story) and
                self.second_trigger.evaluate(story))

# Problem 9
# TODO: OrTrigger


# ======================
# Filtering
# ======================

# Problem 10
def filter_stories(stories, triggerlist):
    """
    Takes in a list of NewsStory instances.

    Returns: a list of only the stories for which a trigger in triggerlist fires.
    """
    # TODO: Problem 10
    # This is a placeholder
    # (we're just returning all the stories, with no filtering)
    return stories


# ======================
# User-Specified Triggers
# ======================
# Problem 11
def read_trigger_config(filename):
    """
    filename: the name of a trigger configuration file

    Returns: a list of trigger objects specified by the trigger configuration
        file.
    """
    # We give you the code to read in the file and eliminate blank lines and
    # comments. You don't need to know how it works for now!
    trigger_file = open(filename, "r")
    lines = []
    for line in trigger_file:
        line = line.rstrip()
        if not (len(line) == 0 or line.startswith("//")):
            lines.append(line)

    # TODO: Problem 11
    # line is the list of lines that you need to parse and for which you need
    # to build triggers

    print(lines)  # for now, print it so you see what it contains!


SLEEPTIME = 120  # seconds -- how often we poll


def main_thread(master):
    # A sample trigger list - you might need to change the phrases to correspond
    # to what is currently in the news
    try:
        t1 = TitleTrigger("election")
        t2 = DescriptionTrigger("Trump")
        t3 = DescriptionTrigger("Clinton")
        t4 = AndTrigger(t2, t3)
        triggerlist = [t1, t4]

        # Problem 11
        # TODO: After implementing read_trigger_config, uncomment this line
        # triggerlist = read_trigger_config('triggers.txt')

        # HELPER CODE - you don't need to understand this!
        # Draws the popup window that displays the filtered stories
        # Retrieves and filters the stories from the RSS feeds
        frame = Frame(master)
        frame.pack(side=BOTTOM)
        scrollbar = Scrollbar(master)
        scrollbar.pack(side=RIGHT, fill=Y)

        t = "Google & Yahoo Top News"
        title = StringVar()
        title.set(t)
        ttl = Label(master, textvariable=title, font=("Helvetica", 18))
        ttl.pack(side=TOP)
        cont = Text(master, font=("Helvetica", 14), yscrollcommand=scrollbar.set)
        cont.pack(side=BOTTOM)
        cont.tag_config("title", justify="center")
        button = Button(frame, text="Exit", command=root.destroy)
        button.pack(side=BOTTOM)
        guidShown = []

        def get_cont(newstory):
            if newstory.get_guid() not in guidShown:
                cont.insert(END, newstory.get_title() + "\n", "title")
                cont.insert(
                    END,
                    "\n---------------------------------------------------------------\n",
                    "title",
                )
                cont.insert(END, newstory.get_description())
                cont.insert(
                    END,
                    "\n*********************************************************************\n",
                    "title",
                )
                guidShown.append(newstory.get_guid())

        while True:

            print("Polling . . .", end=" ")
            # Get stories from Google's Top Stories RSS news feed
            stories = process("http://news.google.com/news?output=rss")

            # Get stories from Yahoo's Top Stories RSS news feed
            stories.extend(process("http://news.yahoo.com/rss/topstories"))

            stories = filter_stories(stories, triggerlist)

            list(map(get_cont, stories))
            scrollbar.config(command=cont.yview)

            print("Sleeping...")
            time.sleep(SLEEPTIME)

    except Exception as e:
        print(e)


if __name__ == "__main__":
    # root = Tk()
    # root.title("Some RSS parser")
    # t = threading.Thread(target=main_thread, args=(root,))
    # t.start()
    # root.mainloop()

    trigger = PhraseTrigger("purple cow")
    for list_of_tests in [POS_TESTS, NEG_TESTS]:
        print("\n======================")
        for test in list_of_tests:
            print("Purple cow in {}?: {}".format(test, trigger.is_phrase_in(test)))
