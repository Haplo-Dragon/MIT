# 6.0001 Problem Set 3
#
# The 6.0001 Word Game
# Created by: Kevin Luu <luuk> and Jenna Wiens <jwiens>
#
# Name          : Ethan Fulbright
# Collaborators : <your collaborators>
# Time spent    : <total time>

import math
import random
import string

VOWELS = "aeiou"
CONSONANTS = "bcdfghjklmnpqrstvwxyz"
WILDCARD = "*"
HAND_SIZE = 7

SCRABBLE_LETTER_VALUES = {
    "a": 1,
    "b": 3,
    "c": 3,
    "d": 2,
    "e": 1,
    "f": 4,
    "g": 2,
    "h": 4,
    "i": 1,
    "j": 8,
    "k": 5,
    "l": 1,
    "m": 3,
    "n": 1,
    "o": 1,
    "p": 3,
    "q": 10,
    "r": 1,
    "s": 1,
    "t": 1,
    "u": 1,
    "v": 4,
    "w": 4,
    "x": 8,
    "y": 4,
    "z": 10,
    WILDCARD: 0,
}

SEPARATOR = "-------------"
END_TURN = "!!"
CURRENT_HAND = "Current hand: "
PROMPT = """Enter word, or "{}" to indicate that you're finished: """.format(END_TURN)
VALID_WORD = """"{}" earned {} points. Total: {} points"""
INVALID_WORD = "That is not a valid word. Please choose another word."
OUT_OF_LETTERS = "Ran out of letters."
TOTAL_SCORE = "Total score: {} points"
SUBSTITUTE = "Would you like to substitute a letter? "
REPLACE_LETTER = "Which letter would like to replace? "
REPLAY = "Would you like to replay the hand? "
GAME_SCORE = "Total score over all hands: {} points"
# -----------------------------------
# Helper code
# (you don't need to understand this helper code)

WORDLIST_FILENAME = "words.txt"


def load_words():
    """
    Returns a list of valid words. Words are strings of lowercase letters.

    Depending on the size of the word list, this function may
    take a while to finish.
    """

    print("Loading word list from file...")
    # inFile: file
    inFile = open(WORDLIST_FILENAME, "r")
    # wordlist: list of strings
    wordlist = []
    for line in inFile:
        wordlist.append(line.strip().lower())
    print("  ", len(wordlist), "words loaded.")
    return wordlist


def get_frequency_dict(sequence):
    """
    Returns a dictionary where the keys are elements of the sequence
    and the values are integer counts, for the number of times that
    an element is repeated in the sequence.

    sequence: string or list
    return: dictionary
    """

    # freqs: dictionary (element_type -> int)
    freq = {}
    for x in sequence:
        freq[x] = freq.get(x, 0) + 1
    return freq


# (end of helper code)
# -----------------------------------

#
# Problem #1: Scoring a word
#
def get_word_score(word, current_hand_length):
    """
    Returns the score for a word. Assumes the word is a
    valid word.

    You may assume that the input word is always either a string of letters,
    or the empty string "". You may not assume that the string will only contain
    lowercase letters, so you will have to handle uppercase and mixed case strings
    appropriately.

    The score for a word is the product of two components:

    The first component is the sum of the points for letters in the word.
    The second component is the larger of:
            1, or
            7*wordlen - 3*(n-wordlen), where wordlen is the length of the word
            and n is the hand length when the word was played

    Letters are scored as in Scrabble; A is worth 1, B is
    worth 3, C is worth 3, D is worth 2, E is worth 1, and so on.

    word: string
    current_hand_length: int >= 0
    returns: int >= 0
    """
    word = word.lower()
    # Storing the length isn't strictly necessary, but it makes for easier reading below
    word_length = len(word)
    letter_score = 0
    length_score = 0

    for letter in word:
        letter_score += SCRABBLE_LETTER_VALUES[letter]
    length_score += max(1, (7 * word_length) - (3 * (current_hand_length - word_length)))

    return letter_score * length_score


#
# Make sure you understand how this function works and what it does!
#
def display_hand(hand):
    """
    Displays the letters currently in the hand.

    For example:
       display_hand({'a':1, 'x':2, 'l':3, 'e':1})
    Should print out something like:
       a x x l l l e
    The order of the letters is unimportant.

    hand: dictionary (string -> int)
    """

    print(CURRENT_HAND)
    for letter in hand.keys():
        for j in range(hand[letter]):
            print(letter, end=" ")  # print all on the same line
    print()  # print an empty line


#
# Make sure you understand how this function works and what it does!
# You will need to modify this for Problem #4.
#
def deal_hand(n):
    """
    Returns a random hand containing n lowercase letters.
    ceil(n/3) letters in the hand should be VOWELS (note,
    ceil(n/3) means the smallest integer not less than n/3).

    Hands are represented as dictionaries. The keys are
    letters and the values are the number of times the
    particular letter is repeated in that hand.

    n: int >= 0
    returns: dictionary (string -> int)
    """

    hand = {}
    # We're reserving one vowel slot for the wildcard
    num_vowels = int(math.ceil(n / 3))
    hand[WILDCARD] = 1

    for i in range(num_vowels - 1):
        x = random.choice(VOWELS)
        hand[x] = hand.get(x, 0) + 1

    for i in range(num_vowels, n):
        x = random.choice(CONSONANTS)
        hand[x] = hand.get(x, 0) + 1

    return hand


#
# Problem #2: Update a hand by removing letters
#
def update_hand(hand, word):
    """
    Does NOT assume that hand contains every letter in word at least as
    many times as the letter appears in word. Letters in word that don't
    appear in hand should be ignored. Letters that appear in word more times
    than in hand should never result in a negative count; instead, set the
    count in the returned hand to 0 (or remove the letter from the
    dictionary, depending on how your code is structured).

    Updates the hand: uses up the letters in the given word
    and returns the new hand, without those letters in it.

    Has no side effects: does not modify hand.

    word: string
    hand: dictionary (string -> int)
    returns: dictionary (string -> int)
    """
    new_hand = hand.copy()

    for letter in word.lower():
        if new_hand.get(letter):
            new_hand[letter] = new_hand[letter] - 1

    # Clean up the new hand by removing letters we've run out of
    new_hand = {letter: count for (letter, count) in new_hand.items() if count > 0}

    return new_hand


#
# Problem #3: Test word validity
#
def is_valid_word(word, hand, word_list):
    """
    Returns True if word is in the word_list and is entirely
    composed of letters in the hand. Otherwise, returns False.
    Does not mutate hand or word_list.

    word: string
    hand: dictionary (string -> int)
    word_list: list of lowercase strings
    returns: boolean
    """
    is_valid = True
    word = word.lower()
    word_letter_count = get_frequency_dict(word)

    if word not in word_list:
        is_valid = False

        if WILDCARD in word:
            for vowel in VOWELS:
                if word.replace(WILDCARD, vowel) in word_list:
                    is_valid = True
                    break

    for letter in word_letter_count.keys():
        if word_letter_count[letter] > hand.get(letter, 0):
            is_valid = False
            break

    return is_valid


#
# Problem #5: Playing a hand
#
def calculate_handlen(hand):
    """
    Returns the length (number of letters) in the current hand.

    hand: dictionary (string-> int)
    returns: integer
    """
    return sum(hand.values())


def play_hand(hand, word_list):
    """
    Allows the user to play the given hand, as follows:

    * The hand is displayed.

    * The user may input a word.

    * When any word is entered (valid or invalid), it uses up letters
      from the hand.

    * An invalid word is rejected, and a message is displayed asking
      the user to choose another word.

    * After every valid word: the score for that word is displayed,
      the remaining letters in the hand are displayed, and the user
      is asked to input another word.

    * The sum of the word scores is displayed when the hand finishes.

    * The hand finishes when there are no more unused letters.
      The user can also finish playing the hand by inputing two
      exclamation points (the string '!!') instead of a word.

      hand: dictionary (string -> int)
      word_list: list of lowercase strings
      returns: the total score for the hand

    """

    # BEGIN PSEUDOCODE <-- Remove this comment when you implement this function
    # Keep track of the total score
    total_score = 0

    # As long as there are still letters left in the hand:
    while calculate_handlen(hand) > 0:
        # Display the hand
        display_hand(hand)

        # Ask user for input
        current_word = input(PROMPT)

        # If the input is two exclamation points:
        if current_word == END_TURN:
            # End the game (break out of the loop)
            break

        # Otherwise (the input is not two exclamation points):
        # If the word is valid:
        if is_valid_word(current_word, hand, word_list):
            # Tell the user how many points the word earned,
            # and the updated total score
            current_score = get_word_score(current_word, calculate_handlen(hand))
            total_score += current_score
            print(VALID_WORD.format(current_word, current_score, total_score))

        # Otherwise (the word is not valid):
        else:
            # Reject invalid word (print a message)
            print(INVALID_WORD)

        # update the user's hand by removing the letters of their inputted word
        hand = update_hand(hand, current_word)

    # Game is over (user entered '!!' or ran out of letters),
    # so tell user the total score
    if calculate_handlen(hand) > 0:
        print(TOTAL_SCORE.format(total_score))
    else:
        print(OUT_OF_LETTERS, TOTAL_SCORE.format(total_score))

    print(SEPARATOR)

    # Return the total score as result of function
    return total_score


#
# Problem #6: Playing a game
#


#
# procedure you will use to substitute a letter in a hand
#


def substitute_hand(hand, letter):
    """
    Allow the user to replace all copies of one letter in the hand (chosen by user)
    with a new letter chosen from the VOWELS and CONSONANTS at random. The new letter
    should be different from user's choice, and should not be any of the letters
    already in the hand.

    If user provide a letter not in the hand, the hand should be the same.

    Has no side effects: does not mutate hand.

    For example:
        substitute_hand({'h':1, 'e':1, 'l':2, 'o':1}, 'l')
    might return:
        {'h':1, 'e':1, 'o':1, 'x':2} -> if the new letter is 'x'
    The new letter should not be 'h', 'e', 'l', or 'o' since those letters were
    already in the hand.

    hand: dictionary (string -> int)
    letter: string
    returns: dictionary (string -> int)
    """
    new_hand = hand.copy()

    if letter in hand:
        letter_choices = VOWELS + CONSONANTS
        # Remove the substituted letter from the new letter choices
        letter_choices = letter_choices.replace(letter, '')

        # Remove any letters in the hand from the new letter choices
        letter_choices = ''.join(
            [letter for letter in letter_choices if letter not in hand.keys()])

        # Choose a new random letter
        new_letter = random.choice(letter_choices)
        new_hand[new_letter] = new_hand[letter]

        del new_hand[letter]

    return new_hand


def play_game(word_list):
    """
    Allow the user to play a series of hands

    * Asks the user to input a total number of hands

    * Accumulates the score for each hand into a total score for the
      entire series

    * For each hand, before playing, ask the user if they want to substitute
      one letter for another. If the user inputs 'yes', prompt them for their
      desired letter. This can only be done once during the game. Once the
      substitue option is used, the user should not be asked if they want to
      substitute letters in the future.

    * For each hand, ask the user if they would like to replay the hand.
      If the user inputs 'yes', they will replay the hand and keep
      the better of the two scores for that hand.  This can only be done once
      during the game. Once the replay option is used, the user should not
      be asked if they want to replay future hands. Replaying the hand does
      not count as one of the total number of hands the user initially
      wanted to play.

            * Note: if you replay a hand, you do not get the option to substitute
                    a letter - you must play whatever hand you just had.

    * Returns the total score for the series of hands

    word_list: list of lowercase strings
    """
    num_hands = int(input("Enter total number of hands: "))
    substition_allowed = True
    replay_allowed = True
    total_score = 0

    while num_hands > 0:
        # Deal new hand
        hand = deal_hand(HAND_SIZE)

        # Ask user if they want to substitute
        if substition_allowed:
            # Show user the dealt hand
            display_hand(hand)
            sub = input(SUBSTITUTE).lower()
            # If they do, prompt them for letter and substitute it
            if sub == "yes":
                letter_to_replace = input(REPLACE_LETTER).lower()
                hand = substitute_hand(hand, letter_to_replace)
                # Only one substitution is allowed per game
                substition_allowed = False

        # Play the hand
        current_score = play_hand(hand, word_list)

        # Ask user if they would like to replay the hand
        if replay_allowed:
            replay = input(REPLAY).lower()
            # If they do, replay hand and keep highest score
            if replay == "yes":
                new_score = play_hand(hand, word_list)
                current_score = max(current_score, new_score)
                # Only one replay is allowed per game
                replay_allowed = False

        # Add current hand score to total score
        total_score += current_score

        num_hands -= 1

    # Display total score from all hands
    print(GAME_SCORE.format(total_score))


#
# Build data structures used for entire session and play game
# Do not remove the "if __name__ == '__main__':" line - this code is executed
# when the program is run directly, instead of through an import statement
#
if __name__ == "__main__":
    word_list = load_words()
    play_game(word_list)
