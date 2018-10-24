###########################
# 6.0002 Problem Set 1b: Space Change
# Name: Ethan Fulbright
# Collaborators: Jesi Ross, Yale CS lecture - Computer Science 201a, Prof. Dana Angluin
# Time:
# Author: charz, cdenise

# ================================
# Part B: Golden Eggs
# ================================


# Problem 1
def dp_make_weight(egg_weights, target_weight, memo={}):
    """
    Find number of eggs to bring back, using the smallest number of eggs. Assumes there is
    an infinite supply of eggs of each weight, and there is always a egg of value 1.

    Parameters:
    egg_weights - tuple of integers, available egg weights sorted from smallest to largest
     value (1 = d1 < d2 < ... < dk)
    target_weight - int, amount of weight we want to find eggs to fit
    memo - dictionary, OPTIONAL parameter for memoization (you may not need to use this
     parameter depending on your implementation)

    Returns: int, smallest number of eggs needed to make target weight
    """
    # This will be the key used to find answers in the memo
    subproblem = (egg_weights, target_weight)

    # If we've already stored this answer in the memo, return it
    if subproblem in memo:
        return memo[subproblem]

    # If no eggs are left or no space is left on ship, there's nothing left to do
    if egg_weights == () or target_weight == 0:
        return 0

    # If the next heaviest egg is too heavy to fit, consider subset of lighter eggs
    elif egg_weights[-1] > target_weight:
        result = dp_make_weight(egg_weights[:-1], target_weight, memo)

    else:
        # Find the minimum number of eggs by testing both taking heaviest egg and not
        # taking heaviest egg.
        this_egg = egg_weights[-1]
        num_eggs_with_this_egg = 1 + dp_make_weight(
            egg_weights,
            target_weight - this_egg,
            memo)

        num_eggs_without_this_egg = dp_make_weight(egg_weights[:-1], target_weight, memo)

        if num_eggs_without_this_egg != 0:
            result = min(num_eggs_with_this_egg, num_eggs_without_this_egg)
        else:
            result = num_eggs_with_this_egg

        # Store this answer in the memo for future use.
        memo[subproblem] = result

    return result


# EXAMPLE TESTING CODE, feel free to add more if you'd like
if __name__ == "__main__":
    egg_weights = (1, 5, 10, 25)
    n = 99
    print("Egg weights = (1, 5, 10, 25)")
    print("n = 99")
    print("Expected ouput: 9 (3 * 25 + 2 * 10 + 4 * 1 = 99)")
    print("Actual output:", dp_make_weight(egg_weights, n))
    print()

    egg_weights = (1, 5, 10, 20)
    n = 99
    print("Egg weights = (1, 5, 10, 20)")
    print("n = 99")
    print("Expected ouput: 10 (4 * 20 + 1 * 10 + 1 * 5 + 4 * 1 = 99)")
    print("Actual output:", dp_make_weight(egg_weights, n))
    print()

    egg_weights = (1, 5, 10, 20, 25, 30)
    n = 99
    print("Egg weights = (1, 5, 10, 20, 25, 30)")
    print("n = 99")
    print("Expected ouput: 8 (3 * 30 + 0 * 10 + 1 * 5 + 4 * 1 = 99)")
    print("Actual output:", dp_make_weight(egg_weights, n))
    print()

    egg_weights = (1, 2, 6, 12, 20)
    n = 37
    print("Egg weights = (1, 2, 6, 12, 20)")
    print("n = 37")
    print("Expected ouput: 4 (0 * 20 + 3 * 12 + 0 * 6 + 0 * 2 + 1 * 1 = 37)")
    print("Actual output:", dp_make_weight(egg_weights, n))
    print()

    egg_weights = (1, 5)
    n = 6
    print("Egg weights = (1, 5)")
    print("n = 6")
    print("Expected ouput: 2 (1 * 5 + 1 * 1 = 6)")
    print("Actual output:", dp_make_weight(egg_weights, n))
    print()
