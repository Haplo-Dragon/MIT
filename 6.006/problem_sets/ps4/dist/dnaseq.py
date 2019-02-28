#!/usr/bin/env python2.7

import unittest
from dnaseqlib import *

# Utility classes ###


# Maps integer keys to a set of arbitrary values.
class Multidict:
    # Initializes a new multi-value dictionary, and adds any key-value
    # 2-tuples in the iterable sequence pairs to the data structure.
    def __init__(self, pairs=[]):
        self.dict = {}
        for pair in pairs:
            self.put(pair[0], pair[1])

    # Associates the value v with the key k.
    def put(self, k, v):
        if k in self.dict:
            self.dict[k].append(v)
        else:
            self.dict[k] = [v]

    # Gets any values that have been associated with the key k; or, if
    # none have been, returns an empty sequence.
    def get(self, k):
        if k in self.dict:
            return self.dict[k]
        else:
            return []

    def items(self):
        for key, value in self.dict.items():
            yield (key, value)


# Given a sequence of nucleotides, return all k-length subsequences,
# their hashes, and their position in the sequence (in the order hash, (subsequence,
# position)). (What else do you need to know about each subsequence?)
def subsequenceHashes(seq, k):
    try:
        current_position = 0

        # Assemble the first k-length subsequence.
        current_subseq = ""
        while len(current_subseq) < k:
            current_subseq += next(seq)

        # Get a hash value for the first k-length subsequence.
        rh = RollingHash(current_subseq)

        # As long as there are letters remaining in the sequence...
        while True:
            # Return the hash value, the subsequence, and the position of the subsequence.
            yield (rh.current_hash(), (current_subseq, current_position))

            # Slide the subsequence one letter along the sequence.
            prev_letter = current_subseq[0]
            next_letter = next(seq)
            current_subseq = current_subseq[1:]
            current_subseq += next_letter

            # Slide the rolling hash one letter along the sequence.
            rh.slide(prev_letter, next_letter)

            # Update the current position.
            current_position += 1
    # We've reached the end of the sequence.
    except StopIteration:
        return


# Similar to subsequenceHashes(), but returns one k-length subsequence
# every m nucleotides.  (This will be useful when you try to use two
# whole data files.)
def intervalSubsequenceHashes(seq, k, m):
    try:
        current_position = 0

        # While there are letters remaining in the sequence...
        while True:
            # Assemble a k-length subsequence.
            current_subseq = ""
            while len(current_subseq) < k:
                current_subseq += next(seq)

            # Get a hash value for the k-length subsequence.
            rh = RollingHash(current_subseq)

            # Return the hash value, the subsequence, and the position of the subsequence.
            yield (rh.current_hash(), (current_subseq, current_position))

            # Discard the next m-k letters in the sequence (since we're only concerned
            # with one k-length subsequence every m letters). In other words, if we
            # wanted one 3-letter subsequence every 10 letters, we'd need to skip 7
            # letters to get to the next 10 letters in the sequence.
            current_position += k
            for i in range(m - k):
                next(seq)
                current_position += 1
    # We've reached the end of the sequence.
    except StopIteration:
        return


# Searches for commonalities between sequences a and b by comparing
# subsequences of length k.  The sequences a and b should be iterators
# that return nucleotides.  The table is built by computing one hash
# every m nucleotides (for m >= k).
def getExactSubmatches(a, b, k, m):
    multi_dict = Multidict()
    # for sub in subsequenceHashes(a, k):
    for sub in intervalSubsequenceHashes(a, k, m):
        multi_dict.put(sub[0], (sub[1], 'a'))
    for sub in subsequenceHashes(b, k):
        multi_dict.put(sub[0], (sub[1], 'b'))

    for hash_value, sequences in multi_dict.items():
        # If more than one subsequence hashed to the same value, they may be equal.
        if len(sequences) > 1:
            for i in range(len(sequences)):
                for j in range(i + 1, len(sequences)):
                    # Compare the subsequences directly. If they match, return their
                    # positions in their respective sequences.
                    first_source = sequences[i][1]
                    second_source = sequences[j][1]

                    first_subseq = sequences[i][0][0]
                    second_subseq = sequences[j][0][0]

                    if first_subseq == second_subseq and first_source != second_source:
                        yield (sequences[i][0][1], sequences[j][0][1])


if __name__ == "__main__":
    if len(sys.argv) != 4:
        print("Usage: {0} [file_a.fa] [file_b.fa] [output.png]".format(sys.argv[0]))
        sys.exit(1)

    # The arguments are, in order: 1) Your getExactSubmatches
    # function, 2) the filename to which the image should be written,
    # 3) a tuple giving the width and height of the image, 4) the
    # filename of sequence A, 5) the filename of sequence B, 6) k, the
    # subsequence size, and 7) m, the sampling interval for sequence
    # A.
    compareSequences(
        getExactSubmatches, sys.argv[3], (500, 500), sys.argv[1], sys.argv[2], 8, 100
    )
    # compareSequences(
    #     getExactSubmatches, sys.argv[3], (500, 500), sys.argv[1], sys.argv[2], 8, 8
    # )
