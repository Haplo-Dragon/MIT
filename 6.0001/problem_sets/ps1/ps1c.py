# Annual return on savings
annual_savings_return_rate = 0.04
# We're saving for a $1M house
total_cost = 1000000
# We get a 7% raise every 6 months
SEMI_ANNUAL_RAISE = .07
# We want to have the down payment saved in 36 months
num_months_to_save = 36


def save(number_of_months, return_rate, annual_salary, savings_rate):
    amount_saved = 0
    monthly_savings_amount = (annual_salary / 12) * savings_rate
    for month in range(1, number_of_months + 1):
        # Earn interest on saved money
        amount_saved += amount_saved * return_rate / 12
        # Save money
        amount_saved += monthly_savings_amount

        # Check to see if earning a raise this month
        if month % 6 == 0:
            # Get a raise
            annual_salary += annual_salary * SEMI_ANNUAL_RAISE
            monthly_savings_amount = (annual_salary / 12) * savings_rate
    return amount_saved


annual_salary = float(input("Enter the starting salary: "))
# Percentage of total dream home price needed for down payment
portion_down_payment = 0.25
down_payment = total_cost * portion_down_payment
# Reality check that it's even possible to save enough in 36 months
if save(num_months_to_save, annual_savings_return_rate, annual_salary, 1) < down_payment:
    print("It is not possible to pay the down payment in three years.")
    exit()

# Need savings to be within $100 of down payment
epsilon = 100
num_guesses = 0
low = 0
high = 10000

guess = (low + high) / 2
current_savings = 0
# While our current savings are not within $100 of down payment...
while (abs(down_payment - current_savings) >= epsilon):
    guess_rate = guess / 10000
    # Save for 36 months
    current_savings = save(
        num_months_to_save,
        annual_savings_return_rate,
        annual_salary,
        guess_rate)

    if current_savings < down_payment:
        low = guess
    else:
        high = guess

    guess = (low + high) / 2
    num_guesses += 1

print("Best savings rate: {:.4f}".format(guess / 10000))
print("Steps in bisection search: ", num_guesses)
