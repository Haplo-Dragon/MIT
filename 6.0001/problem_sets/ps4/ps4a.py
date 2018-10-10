# Problem Set 4A
# Name: Ethan Fulbright
# Collaborators:
# Time Spent: x:xx


def get_permutations(sequence):
    """
    Enumerate all permutations of a given string

    sequence (string): an arbitrary string to permute. Assume that it is a
    non-empty string.

    You MUST use recursion for this part. Non-recursive solutions will not be
    accepted.

    Returns: a list of all permutations of sequence

    Example:
    >>> get_permutations('abc')
    ['abc', 'acb', 'bac', 'bca', 'cab', 'cba']

    Note: depending on your implementation, you may return the permutations in
    a different order than what is listed here.
    """
    # Base case - single character is the only permutation of itself
    if len(sequence) == 1:
        return list(sequence)
    # Recursive case
    first_character = sequence[0]
    permutations = []
    candidates = get_permutations(sequence[1:])

    for candidate in candidates:
        for position in range(len(candidate) + 1):
            permutations.append(
                candidate[:position] + first_character + candidate[position:])

    return permutations


if __name__ == "__main__":
    #    #EXAMPLE
    #    example_input = 'abc'
    #    print('Input:', example_input)
    #    print('Expected Output:', ['abc', 'acb', 'bac', 'bca', 'cab', 'cba'])
    #    print('Actual Output:', get_permutations(example_input))

    #    # Put three example test cases here (for your sanity, limit your inputs
    #    to be three characters or fewer as you will have n! permutations for a
    #    sequence of length n)

    example_input = 'cat'
    print('Input:', example_input)
    print('Expected Output:', ['cat', 'act', 'atc', 'tac', 'tca', 'cta'])
    print('Actual Output:', get_permutations(example_input))

    example_input = 'dog'
    print('Input:', example_input)
    print('Expected Output:', ['dog', 'odg', 'ogd', 'god', 'gdo', 'dgo'])
    print('Actual Output:', get_permutations(example_input))

    example_input = 'emu'
    print('Input:', example_input)
    print('Expected Output:', ['emu', 'meu', 'mue', 'ume', 'uem', 'eum'])
    print('Actual Output:', get_permutations(example_input))
