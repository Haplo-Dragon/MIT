adjacency_list = {
    'u': ['A', 'C'],
    'A': ['B', 'C'],
    'B': ['D', 'V'],
    'C': ['B'],
    'D': ['V'],
    'V': []}

edgerank = {
    ('u', 'A'): 0.5,
    ('u', 'C'): 0.4,
    ('A', 'C'): 0.9,
    ('A', 'B'): 0.5,
    ('C', 'B'): 0.9,
    ('B', 'D'): 0.2,
    ('B', 'V'): 0.9,
    ('D', 'V'): 0.4,
}


def compute_strength(source_user, adjacency_list, k):
        # Initializations are all O(1)
        level = {source_user: 0}
        parent = {source_user: None}
        strength = {source_user: 1}
        i = 1
        frontier = [source_user]

        # We're only interested in potential friends who are k edges or fewer away.
        while frontier and i <= k:
            next = []
            for user in frontier:
                print("Examining edges leaving {}...".format(user))
                for friend in adjacency_list[user]:
                    if friend not in level:
                        print("\t{} not in level, setting its level to {}.".format(
                            friend, i))
                        level[friend] = i
                        parent[friend] = user
                        next.append(friend)

                    # We relax edges just like in Dijkstra.
                    if friend not in strength:
                        # It's the first time we've seen this node. Its strength will
                        # be the edgerank of the edge we're following times the previous
                        # node's strength.
                        print("\t{} not in strength.".format(friend))
                        strength[friend] = strength[user] * ER(user, friend)
                        print("\t\tNew strength for {} is {}.".format(
                            friend, strength[friend]))
                    else:
                        if strength[friend] < strength[user] * ER(user, friend):
                            print("\tFound better strength for {} through {}".format(
                                friend, user))
                            strength[friend] = strength[user] * ER(user, friend)
                            parent[friend] = user
                            print("\t\tNew strength for {} is {}.".format(
                                friend, strength[friend]))
            print("\tNext up: {}".format(next))
            frontier = next
            i += 1

        return strength


def ER(user, friend):
    return edgerank[(user, friend)]


if __name__ == "__main__":
    strength = compute_strength('u', adjacency_list, 3)
    print(strength)
