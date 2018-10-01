# Percentage of total dream home price needed for down payment
portion_down_payment = 0.25

# Annual return
annual_savings_return_rate = 0.04

current_savings = 0
num_months_saved = 0

annual_salary = float(input("Enter the starting salary: "))
portion_saved = float(input("Enter the percent of your salary to save, as a decimal: "))
total_cost = float(input("Enter the cost of your dream home: "))
semi_annual_raise = float(input("Enter the semi-annual raise, as a decimal: "))

down_payment = total_cost * portion_down_payment
monthly_savings_amount = (annual_salary / 12) * portion_saved

while current_savings < down_payment:
    # Earn interest on saved money
    current_savings += current_savings * annual_savings_return_rate / 12
    # Save money
    current_savings += monthly_savings_amount
    num_months_saved += 1

    # Check to see if earning a raise this month
    if num_months_saved % 6 == 0:
        # Get a raise
        annual_salary += annual_salary * semi_annual_raise
        monthly_savings_amount = (annual_salary / 12) * portion_saved

print("Number of months until you can afford a down payment on your dream home: ",
    num_months_saved)
