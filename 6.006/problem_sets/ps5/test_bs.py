def bs(x, k):
    low = 0
    high = x // k
    epsilon = 0.000001

    guess = float((low + high) / 2)
    guess_to_k = guess**k

    while abs(guess_to_k - x) > epsilon:
            print("Current guess is {}.".format(guess))

            if guess_to_k > x:
                    print("Too high.")
                    high = guess
            elif guess_to_k < x:
                    print("Too low.")
                    low = guess
            elif guess_to_k == x:
                    print("Equal!")
                    break

            guess = (low + high) / 2
            guess_to_k = guess**k

    print("{}th root of {} is {}.".format(k, x, guess))
