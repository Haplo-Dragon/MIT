import rubik


def shortest_path(start, end):
    """
    Using 2-way BFS, finds the shortest path from start_position to
    end_position. Returns a list of moves.

    You can use the rubik.quarter_twists move set.
    Each move can be applied using rubik.perm_apply

    The forward and backward search use exactly the same code, so it seems prudent
    to refactor them into a function. However, the number of variables that must be
    passed in order for this to work makes it seem like a clumsy solution. I wonder
    if there's something better...
    """
    if start == end:
        # Length 0 path.
        return []

    # Initialize level counters and parent pointers.
    level = {start: 0, end: 0}
    forward_parent = {start: None}
    backward_parent = {end: None}

    # Initialize forward and backward search frontiers.
    i = 1
    found = False
    middle = None
    forward_frontier = [start]
    backward_frontier = [end]

    # Keep track of whether a given node has been discovered by both searches.
    found_by_forward_search = set()
    found_by_forward_search.add(start)
    found_by_backward_search = set()
    found_by_backward_search.add(end)

    # Once the same node has been touched by both searches, we're done.
    # Checking only out to level 9 is an ugly hack! There has to be a better way to
    # determine if there's no solution. I've read some things about permutation parity
    # and checking corner twists, but I'm not sure how to do that.
    while (forward_frontier or backward_frontier) and not found and i < 9:
        # Reset next level of both search frontiers.
        next_forward = []
        next_backward = []

        # Search forward frontier (one level).
        for config in forward_frontier:
            # Find the config's neighbors by applying all possible moves.
            for permutation in rubik.quarter_twists:
                neighbor = rubik.perm_apply(permutation, config)

                # Avoid duplicates.
                if neighbor not in forward_parent:
                    # Set the level and parent of the neighbor.
                    level[neighbor] = i
                    forward_parent[neighbor] = permutation
                    # Add the neighbor to the new search frontier.
                    next_forward.append(neighbor)

        # Search backward frontier (one level).
        for config in backward_frontier:
            # Find the config's neighbors by applying all possible moves.
            for permutation in rubik.quarter_twists:
                neighbor = rubik.perm_apply(permutation, config)

                # Avoid duplicates.
                if neighbor not in backward_parent:
                    # Set the level and parent of the neighbor.
                    level[neighbor] = i
                    backward_parent[neighbor] = permutation
                    # Add the neighbor to the new search frontier.
                    next_backward.append(neighbor)

        # Check to see if any neighbors have been discovered by both searches. If one
        # has, we're done.
        found_by_forward_search = found_by_forward_search.union(set(next_forward))
        found_by_backward_search = found_by_backward_search.union(set(next_backward))
        found_by_both = found_by_forward_search.intersection(found_by_backward_search)
        if found_by_both:
            found = True
            middle = found_by_both.pop()

        # Move to the new search frontiers, one level deeper.
        forward_frontier = next_forward
        backward_frontier = next_backward
        i += 1

    # Return the shortest path between the start and end configurations.
    if middle is None:
        # No path exists between the two configurations.
        return None
    elif middle == start or middle == end:
        # Path of length 1.
        if middle == start:
            return [rubik.perm_inverse(backward_parent[middle])]
        else:
            return [forward_parent[middle]]
    else:
        # Path of length >1.
        moves_to_end = build(backward_parent, end, middle, backward=True)
        moves_to_end.reverse()
        moves_to_start = build(forward_parent, start, middle, backward=False)

        path = moves_to_start
        path.extend(moves_to_end)

        return path


def build(parent, start_config, end_config, backward):
    """
    Gets recursion started off cleanly. Follows parent pointers to get shortest path.
    """
    path = []
    build_path(parent, start_config, end_config, path, backward)
    return path


def build_path(parents, start_config, current_config, path, backward):
    """
    Shamelessly stolen from CLRS (where it's called PRINT_PATH).
    """
    if current_config == start_config:
        # Bottom of the recursion. We've reached our goal config, so we don't need
        # to record it - we'll get the permutation required to GET to our goal config
        # when we step back up the call stack.
        pass
    else:
        next_config = rubik.perm_apply(
            rubik.perm_inverse(parents[current_config]),
            current_config)
        build_path(parents, start_config, next_config, path, backward)

        if backward:
            # We don't add the configuration to the path, we add the PERMUTATION
            # required to GET to the next configuration.
            path.append(rubik.perm_inverse(parents[current_config]))
        else:
            path.append(parents[current_config])
