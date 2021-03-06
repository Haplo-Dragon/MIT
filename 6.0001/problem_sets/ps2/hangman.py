# Problem Set 2, hangman.py
# Name: Ethan Fulbright
# Collaborators:
# Time spent:

# Hangman Game
# -----------------------------------
# Helper code
# You don't need to understand this helper code,
# but you will have to know how to use the functions
# (so be sure to read the docstrings!)
import random
import string

WORDLIST_FILENAME = "words.txt"

VOWELS = "aeiou"
ALREADY_GUESSED = "Oops! You've already guessed that letter."
NOT_IN_MY_WORD = "Oops! That letter's not in my word."
NOT_VALID_LETTER = "Oops! That's not a valid letter."
NO_WARNINGS_LEFT = "You have no warnings left so you lose one guess:"
COMMAND_POSSIBLE_MATCHES = "*"
POSSIBLE_MATCHES = "Possible word matches are:\n"
WINNING_MESSAGE = "Congratulations, you won!\nYour total score for this game is: {}"
LOSING_MESSAGE = "Sorry, you ran out of guesses. The word was {}."


def load_words():
    """
    Returns a list of valid words. Words are strings of lowercase letters.

    Depending on the size of the word list, this function may
    take a while to finish.
    """
    print("Loading word list from file...")
    # inFile: file
    inFile = open(WORDLIST_FILENAME, 'r')
    # line: string
    line = inFile.readline()
    # wordlist: list of strings
    wordlist = line.split()
    print("  ", len(wordlist), "words loaded.")
    return wordlist


def choose_word(wordlist):
    """
    wordlist (list): list of words (strings)

    Returns a word from wordlist at random
    """
    return random.choice(wordlist)

# end of helper code

# -----------------------------------

# Load the list of words into the variable wordlist
# so that it can be accessed from anywhere in the program


wordlist = load_words()


def is_word_guessed(secret_word, letters_guessed):
    '''
    secret_word: string, the word the user is guessing; assumes all letters are
      lowercase
    letters_guessed: list (of letters), which letters have been guessed so far;
      assumes that all letters are lowercase
    returns: boolean, True if all the letters of secret_word are in letters_guessed;
      False otherwise
    '''
    guessed = True
    for letter in secret_word:
        if letter not in letters_guessed:
            guessed = False

    return guessed


def get_guessed_word(secret_word, letters_guessed):
    '''
    secret_word: string, the word the user is guessing
    letters_guessed: list (of letters), which letters have been guessed so far
    returns: string, comprised of letters, underscores (_), and spaces that represents
      which letters in secret_word have been guessed so far.
    '''
    progress_string = ""
    for letter in secret_word:
        if letter in letters_guessed:
            progress_string += letter
        else:
            progress_string += "_ "

    return progress_string


def get_available_letters(letters_guessed):
    '''
    letters_guessed: list (of letters), which letters have been guessed so far
    returns: string (of letters), comprised of letters that represents which letters have
    not yet been guessed.
    '''
    # FILL IN YOUR CODE HERE AND DELETE "pass"
    available_letters = ""
    for letter in string.ascii_lowercase:
        if letter not in letters_guessed:
            available_letters += letter

    return available_letters


def get_total_score(secret_word, guesses_remaining):
    unique_letters = ""
    for letter in secret_word:
        if letter not in unique_letters:
            unique_letters += letter

    return guesses_remaining * len(unique_letters)


def hangman(secret_word):
    '''
    secret_word: string, the secret word to guess.

    Starts up an interactive game of Hangman.

    * At the start of the game, let the user know how many
      letters the secret_word contains and how many guesses s/he starts with.

    * The user should start with 6 guesses

    * Before each round, you should display to the user how many guesses
      s/he has left and the letters that the user has not yet guessed.

    * Ask the user to supply one guess per round. Remember to make
      sure that the user puts in a letter!

    * The user should receive feedback immediately after each guess
      about whether their guess appears in the computer's word.

    * After each guess, you should display to the user the
      partially guessed word so far.

    Follows the other limitations detailed in the problem write-up.
    '''
    guesses_remaining = 6
    warnings_remaining = 3
    letters_guessed = ""

    print("Welcome to Hangman!")
    print("I'm thinking of a word that is {} letters long.".format(len(secret_word)))

    while not is_word_guessed(secret_word, letters_guessed) and guesses_remaining >= 0:
        print("--------------\n")
        print("You have {} warnings left.".format(warnings_remaining))
        print("You have {} guesses left.".format(guesses_remaining))
        print("Available letters:", get_available_letters(letters_guessed))

        guess = input("Please guess a letter: ").lower()
        if guess.isalpha() and (guess not in letters_guessed):
            letters_guessed += guess

            if guess in secret_word:
                print("Good guess:", get_guessed_word(secret_word, letters_guessed))
            else:
                print(NOT_IN_MY_WORD, get_guessed_word(secret_word, letters_guessed))

            if guess in VOWELS and (guess not in secret_word):
                guesses_remaining -= 2
            elif guess not in secret_word:
                guesses_remaining -= 1

        # Check to see if the user guessed a duplicate letter.
        elif guess.isalpha() and (guess in letters_guessed):
            if warnings_remaining > 0:
                warnings_remaining -= 1
                print(ALREADY_GUESSED, "You now have {} warnings: {}".format(
                    warnings_remaining,
                    get_guessed_word(secret_word, letters_guessed)))
            else:
                guesses_remaining -= 1
                print(
                    ALREADY_GUESSED,
                    NO_WARNINGS_LEFT,
                    get_guessed_word(secret_word, letters_guessed))

        # Warn the user about non-alphabet characters.
        else:
            if warnings_remaining > 0:
                warnings_remaining -= 1
                print(NOT_VALID_LETTER, "You now have {} warnings: {}".format(
                    warnings_remaining,
                    get_guessed_word(secret_word, letters_guessed)))
            else:
                guesses_remaining -= 1
                print(
                    NOT_VALID_LETTER,
                    NO_WARNINGS_LEFT,
                    get_guessed_word(secret_word, letters_guessed))

    # By now the user has either guessed the word or run out of guesses.
    print("\n\n=============")

    if is_word_guessed(secret_word, letters_guessed) and guesses_remaining >= 0:
        print(WINNING_MESSAGE.format(get_total_score(secret_word, guesses_remaining)))
    else:
        print(LOSING_MESSAGE.format(secret_word))


# When you've completed your hangman function, scroll down to the bottom
# of the file and uncomment the first two lines to test
# (hint: you might want to pick your own
# secret_word while you're doing your own testing)


# -----------------------------------


def match_with_gaps(my_word, other_word):
    '''
    my_word: string with _ characters, current guess of secret word
    other_word: string, regular English word
    returns: boolean, True if all the actual letters of my_word match the
        corresponding letters of other_word, or the letter is the special symbol
        _ , and my_word and other_word are of the same length;
        False otherwise:
    '''
    matched = True
    my_word_no_spaces = my_word.replace(" ", "")

    if (len(my_word_no_spaces) != len(other_word)):
        matched = False

    # Pair up the words letter by letter and compare them
    for letter_pair in zip(my_word_no_spaces, other_word):
        if ((letter_pair[0] != letter_pair[1]) and
            ((letter_pair[0].isalpha()) or (letter_pair[1] in my_word_no_spaces))):
            matched = False

    return matched


def show_possible_matches(my_word):
    '''
    my_word: string with _ characters, current guess of secret word
    returns: nothing, but should print out every word in wordlist that matches my_word
             Keep in mind that in hangman when a letter is guessed, all the positions
             at which that letter occurs in the secret word are revealed.
             Therefore, the hidden letter(_ ) cannot be one of the letters in the word
             that has already been revealed.

    '''
    # FILL IN YOUR CODE HERE AND DELETE "pass"
    matches = ""
    for word in wordlist:
        if match_with_gaps(my_word, word):
            matches += word + " "

    if matches:
        return matches
    else:
        return "No matches found."


def hangman_with_hints(secret_word):
    '''
    secret_word: string, the secret word to guess.

    Starts up an interactive game of Hangman.

    * At the start of the game, let the user know how many
      letters the secret_word contains and how many guesses s/he starts with.

    * The user should start with 6 guesses

    * Before each round, you should display to the user how many guesses
      s/he has left and the letters that the user has not yet guessed.

    * Ask the user to supply one guess per round. Make sure to check that the user
      guesses a letter

    * The user should receive feedback immediately after each guess
      about whether their guess appears in the computer's word.

    * After each guess, you should display to the user the
      partially guessed word so far.

    * If the guess is the symbol *, print out all words in wordlist that
      matches the current guessed word.

    Follows the other limitations detailed in the problem write-up.
    '''
    # FILL IN YOUR CODE HERE AND DELETE "pass"
    guesses_remaining = 6
    warnings_remaining = 3
    letters_guessed = ""

    print("Welcome to Hangman!")
    print("I'm thinking of a word that is {} letters long.".format(len(secret_word)))

    while not is_word_guessed(secret_word, letters_guessed) and guesses_remaining >= 0:
        print("--------------\n")
        print("You have {} warnings left.".format(warnings_remaining))
        print("You have {} guesses left.".format(guesses_remaining))
        print("Available letters:", get_available_letters(letters_guessed))
        print("Guessing * will show you possible word matches.")

        guess = input("Please guess a letter: ").lower()

        if guess.isalpha() and (guess not in letters_guessed):
            letters_guessed += guess

            if guess in secret_word:
                print("Good guess:", get_guessed_word(secret_word, letters_guessed))
            else:
                print(NOT_IN_MY_WORD, get_guessed_word(secret_word, letters_guessed))
                if guess in VOWELS:
                    guesses_remaining -= 2
                else:
                    guesses_remaining -= 1

            # if guess in VOWELS and (guess not in secret_word):
            #     guesses_remaining -= 2
            # elif guess not in secret_word:
            #     guesses_remaining -= 1

        # Check to see if the user guessed a duplicate letter.
        elif guess.isalpha() and (guess in letters_guessed):
            if warnings_remaining > 0:
                warnings_remaining -= 1
                print(ALREADY_GUESSED, "You now have {} warnings: {}".format(
                    warnings_remaining,
                    get_guessed_word(secret_word, letters_guessed)))
            else:
                guesses_remaining -= 1
                print(
                    ALREADY_GUESSED,
                    NO_WARNINGS_LEFT,
                    get_guessed_word(secret_word, letters_guessed))

        # Use a special command character to show all possible matches
        elif guess == COMMAND_POSSIBLE_MATCHES:
            print(POSSIBLE_MATCHES,
                  show_possible_matches(get_guessed_word(secret_word, letters_guessed)))

        # Warn the user about non-alphabet characters.
        else:
            if warnings_remaining > 0:
                warnings_remaining -= 1
                print(NOT_VALID_LETTER, "You now have {} warnings: {}".format(
                    warnings_remaining,
                    get_guessed_word(secret_word, letters_guessed)))
            else:
                guesses_remaining -= 1
                print(
                    NOT_VALID_LETTER,
                    NO_WARNINGS_LEFT,
                    get_guessed_word(secret_word, letters_guessed))

    # By now the user has either guessed the word or run out of guesses.
    print("\n\n=============")

    if is_word_guessed(secret_word, letters_guessed) and guesses_remaining >= 0:
        print(WINNING_MESSAGE.format(get_total_score(secret_word, guesses_remaining)))
    else:
        print(LOSING_MESSAGE.format(secret_word))


# When you've completed your hangman_with_hint function, comment the two similar
# lines above that were used to run the hangman function, and then uncomment
# these two lines and run this file to test!
# Hint: You might want to pick your own secret_word while you're testing.


if __name__ == "__main__":
    # pass

    # To test part 2, comment out the pass line above and
    # uncomment the following two lines.

    # secret_word = choose_word(wordlist)
    # hangman(secret_word)

    ###############

    # To test part 3 re-comment out the above lines and
    # uncomment the following two lines.

    secret_word = choose_word(wordlist)
    hangman_with_hints(secret_word)
