###########################
# 6.0002 Problem Set 1a: Space Cows
# Name: Ethan Fulbright
# Collaborators:
# Time:

from ps1_partition import get_partitions
import time
from pprint import pprint

# ================================
# Part A: Transporting Space Cows
# ================================


# Problem 1
def load_cows(filename):
    """
    Read the contents of the given file.  Assumes the file contents contain
    data in the form of comma-separated cow name, weight pairs, and return a
    dictionary containing cow names as keys and corresponding weights as values.

    Parameters:
    filename - the name of the data file as a string

    Returns:
    a dictionary of cow name (string), weight (int) pairs
    """
    # Initialize dictionary
    cows = {}
    # Open file in read mode using 'with' keyword for context management
    with open(filename, 'r') as f:
        for line in f:
            current_cow = line
            # Split the line into a list of the form [cow_name, cow_weight]
            cow_name, cow_weight = current_cow.split(',')
            cows[cow_name] = int(cow_weight)

    return cows


# Problem 2
def greedy_cow_transport(cows, limit=10):
    """
    Uses a greedy heuristic to determine an allocation of cows that attempts to
    minimize the number of spaceship trips needed to transport all the cows. The
    returned allocation of cows may or may not be optimal.
    The greedy heuristic should follow the following method:

    1. As long as the current trip can fit another cow, add the largest cow that will fit
        to the trip
    2. Once the trip is full, begin a new trip to transport the remaining cows

    Does not mutate the given dictionary of cows.

    Parameters:
    cows - a dictionary of name (string), weight (int) pairs
    limit - weight limit of the spaceship (an int)

    Returns:
    A list of lists, with each inner list containing the names of cows
    transported on a particular trip and the overall list containing all the
    trips
    """
    # Make a list of the provided cows dictionary, sorted by cow weight, heaviest first
    cows_remaining = sorted(cows.items(), key=lambda cow: cow[1], reverse=True)
    trips = []

    # While there are cows left to take
    while(cows_remaining):
        # Start a trip with an empty spaceship
        current_trip = []
        current_trip_weight = 0
        # Making a copy allows us to avoid changing the list we're iterating over
        earth_cows = cows_remaining.copy()
        # Check all cows left on Earth, heaviest first
        for cow in earth_cows:
            if current_trip_weight + cow[1] <= limit:
                # Add heaviest cow that doesn't break the limit to current trip
                current_trip.append(cow[0])
                current_trip_weight += cow[1]
                # Remove that cow from consideration
                cows_remaining.remove(cow)
        # Add current trip to trips list
        trips.append(current_trip)

    return trips


# Problem 3
def brute_force_cow_transport(cows, limit=10):
    """
    Finds the allocation of cows that minimizes the number of spaceship trips
    via brute force.  The brute force algorithm should follow the following method:

    1. Enumerate all possible ways that the cows can be divided into separate trips
        Use the given get_partitions function in ps1_partition.py to help you!
    2. Select the allocation that minimizes the number of trips without making any trip
        that does not obey the weight limitation

    Does not mutate the given dictionary of cows.

    Parameters:
    cows - a dictionary of name (string), weight (int) pairs
    limit - weight limit of the spaceship (an int)

    Returns:
    A list of lists, with each inner list containing the names of cows
    transported on a particular trip and the overall list containing all the
    trips
    """
    # Get list of cow names, copying to avoid mutation
    cow_names = list(cows)
    shortest_number_of_trips = len(cow_names)
    best_trip_plan = []

    # Try every possible set of trips, trying a single trip first, then two trips, etc.
    for set_of_trips in get_partitions(cow_names):
        current_number_of_trips = len(set_of_trips)
        trips_under_limit = True

        for trip in set_of_trips:
            # Calculate the weight of the current trip
            current_trip_weight = 0
            for cow in trip:
                current_trip_weight += cows[cow]

            # If any of the trips are over the weight limit, move on to the next set
            if current_trip_weight > limit:
                trips_under_limit = False
                break

        # If the spaceship isn't broken and this set used fewer trips, it's the best plan
        if (trips_under_limit) and (current_number_of_trips < shortest_number_of_trips):
            shortest_number_of_trips = current_number_of_trips
            best_trip_plan = set_of_trips

    return best_trip_plan


# Problem 4
def compare_cow_transport_algorithms():
    """
    Using the data from ps1_cow_data.txt and the specified weight limit, run your
    greedy_cow_transport and brute_force_cow_transport functions here. Use the
    default weight limits of 10 for both greedy_cow_transport and
    brute_force_cow_transport.

    Print out the number of trips returned by each method, and how long each
    method takes to run in seconds.

    Returns:
    Does not return anything.
    """
    # TODO: Your code here
    pass


if __name__ == "__main__":
    cows = load_cows('ps1_cow_data.txt')
    pprint(cows)
