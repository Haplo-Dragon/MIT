# 6.0002 Problem Set 5
# Graph optimization
# Name: Ethan Fulbright
# Collaborators:
# Time:

#
# Finding shortest paths through MIT buildings
#
import unittest
from graph import Digraph, Node, WeightedEdge

#
# Problem 2: Building up the Campus Map
#
# Problem 2a: Designing your graph
#
# What do the graph's nodes represent in this problem? What
# do the graph's edges represent? Where are the distances
# represented?
#
# Answer:
# The nodes in this problem are buildings on the MIT campus.
# The graph's edges are routes from building to building.
# The distances are represented in the total_distance and outdoor_distance
# properties of each weighted edge.


# Problem 2b: Implementing load_map
def load_map(map_filename):
    """
    Parses the map file and constructs a directed graph

    Parameters:
        map_filename : name of the map file

    Assumes:
        Each entry in the map file consists of the following four positive
        integers, separated by a blank space:
            From To TotalDistance DistanceOutdoors
        e.g.
            32 76 54 23
        This entry would become an edge from 32 to 76.

    Returns:
        a Digraph representing the map
    """
    print("Loading map from file...\n")

    edges = []
    nodes = set()
    campus_map = Digraph()

    with open(map_filename, 'r') as f:
        for line in f:
            current_edge_data = line.split(" ")
            src = Node(current_edge_data[0])
            dest = Node(current_edge_data[1])
            total_distance = int(current_edge_data[2])
            outdoor_distance = int(current_edge_data[3])

            for node in src, dest:
                nodes.add(node)

            current_edge = WeightedEdge(src, dest, total_distance, outdoor_distance)
            edges.append(current_edge)

    for node in nodes:
        campus_map.add_node(node)

    for edge in edges:
        campus_map.add_edge(edge)

    return campus_map

# Problem 2c: Testing load_map
# Include the lines used to test load_map below, but comment them out
# map = load_map('test_load_map.txt')
# print(map)
# for edge in map.get_edges_for_node('Eye'):
#     print(edge)
# print('Eye' in map)

#
# Problem 3: Finding the Shorest Path using Optimized Search Method
#
# Problem 3a: Objective function
#
# What is the objective function for this problem? What are the constraints?
#
# Answer:
# The objective function is to minimize the distance traveled. The contraint is
# a maximum distance to be spent outdoors (before we get too cold).


# Problem 3b: Implement get_best_path
def get_best_path(digraph, start, end, path, max_dist_outdoors, best_dist, best_path):
    """
    Finds the shortest path between buildings subject to constraints.

    Parameters:
        digraph: Digraph instance
            The graph on which to carry out the search
        start: string
            Building number at which to start
        end: string
            Building number at which to end
        path: list composed of [[list of strings], int, int]
            Represents the current path of nodes being traversed. Contains
            a list of node names, total distance traveled, and total
            distance outdoors.
        max_dist_outdoors: int
            Maximum distance spent outdoors on a path
        best_dist: int
            The smallest distance between the original start and end node
            for the initial problem that you are trying to solve
        best_path: list of strings
            The shortest path found so far between the original start
            and end node.

    Returns:
        A tuple with the shortest-path from start to end, represented by
        a list of building numbers (in strings), [n_1, n_2, ..., n_k],
        where there exists an edge from n_i to n_(i+1) in digraph,
        for all 1 <= i < k and the distance of that path.

        If there exists no path that satisfies max_total_dist and
        max_dist_outdoors constraints, then return None.
    """
    # Add string of start node to path
    path[0] = path[0] + [start]

    if best_path is not None:
        best_dist = best_path[1]
    else:
        best_dist = 0

    # If start and end are not valid nodes
    if not ((start in digraph) and (end in digraph)):
        raise KeyError('Start or end node are not valid nodes in the provided graph.')

    # Else, if start and end are the same node
    elif start == end:
        # Update global variables appropriately
        best_path = path[:]
        best_dist = best_path[1]
        # print("Reached end node,", best_path, best_dist)

    else:
        # print("\nChecking edges of node {}...".format(start))
        # For all child nodes of start (this returns a LIST of EDGES)
        for edge in digraph.get_edges_for_node(start):
            new_node = edge.get_destination().get_name()
            # Avoid loops
            if new_node not in path[0]:
                # print("Checking edge", edge, "Path is", path)
                # Construct a path including that node.
                # Add this edge's total distance and outdoor distance to the path
                # traveled so far.
                current_total_dist = path[1] + edge.get_total_distance()
                current_outdoor_dist = path[2] + edge.get_outdoor_distance()
                # If we're just starting or the current path is better than our shortest
                # path so far, we want to explore recursively. We also AVOID recursing
                # if the current edge would exceed our maximum distance outdoors
                # constraint.
                if ((best_path is None) or (current_total_dist < best_dist)) and \
                   (current_outdoor_dist <= max_dist_outdoors):
                    # print("Edge {} is viable, recursing.".format(edge))
                    # Copy the path so that local changes aren't propagated up the stack
                    current_path = path[:]
                    current_path[1] = current_total_dist
                    current_path[2] = current_outdoor_dist
                    # Recursively solve the rest of the path, from the child node to
                    # the end node
                    new_path, new_dist = get_best_path(
                        digraph,
                        new_node,
                        end,
                        current_path,
                        max_dist_outdoors,
                        best_dist,
                        best_path)
                    if new_path is not None:
                        best_path = new_path
                        best_dist = new_dist
                        # print("Found new best path:", best_path)

    # Return shortest path
    return (best_path, best_dist)


map = load_map('test_load_map2.txt')
print(map)
path, dist = get_best_path(
    digraph=map,
    start='Wasteland',
    end='Eye',
    path=[[], 0, 0],
    max_dist_outdoors=200,
    best_dist=0,
    best_path=None)
print("==================\nFinal path is: {}\nDistance is {}".format(path, dist))


# Problem 3c: Implement directed_dfs
def directed_dfs(digraph, start, end, max_total_dist, max_dist_outdoors):
    """
    Finds the shortest path from start to end using a directed depth-first
    search. The total distance traveled on the path must not
    exceed max_total_dist, and the distance spent outdoors on this path must
    not exceed max_dist_outdoors.

    Parameters:
        digraph: Digraph instance
            The graph on which to carry out the search
        start: string
            Building number at which to start
        end: string
            Building number at which to end
        max_total_dist: int
            Maximum total distance on a path
        max_dist_outdoors: int
            Maximum distance spent outdoors on a path

    Returns:
        The shortest-path from start to end, represented by
        a list of building numbers (in strings), [n_1, n_2, ..., n_k],
        where there exists an edge from n_i to n_(i+1) in digraph,
        for all 1 <= i < k

        If there exists no path that satisfies max_total_dist and
        max_dist_outdoors constraints, then raises a ValueError.
    """
    path, dist = get_best_path(
        digraph=digraph,
        start=start,
        end=end,
        path=[[], 0, 0],
        max_dist_outdoors=max_dist_outdoors,
        best_dist=0,
        best_path=None)

    # If a short enough path exists, return the list of buildings on that path.
    if 0 < dist < max_total_dist:
        return path[0]
    # Otherwise, no path exists that is short enough.
    raise ValueError(
        'No path can be found with total distance less than \
        {} and outdoor distance less than {}.'.format(max_total_dist, max_dist_outdoors))


# ================================================================
# Begin tests -- you do not need to modify anything below this line
# ================================================================


class Ps2Test(unittest.TestCase):
    LARGE_DIST = 99999

    def setUp(self):
        self.graph = load_map("mit_map.txt")

    def test_load_map_basic(self):
        self.assertTrue(isinstance(self.graph, Digraph))
        self.assertEqual(len(self.graph.nodes), 37)
        all_edges = []
        for _, edges in self.graph.edges.items():
            all_edges += edges  # edges must be dict of node -> list of edges
        all_edges = set(all_edges)
        self.assertEqual(len(all_edges), 129)

    def _print_path_description(self, start, end, total_dist, outdoor_dist):
        constraint = ""
        if outdoor_dist != Ps2Test.LARGE_DIST:
            constraint = "without walking more than {}m outdoors".format(outdoor_dist)
        if total_dist != Ps2Test.LARGE_DIST:
            if constraint:
                constraint += " or {}m total".format(total_dist)
            else:
                constraint = "without walking more than {}m total".format(total_dist)

        print("------------------------")
        print("Shortest path from Building {} to {} {}".format(start, end, constraint))

    def _test_path(self, expectedPath, total_dist=LARGE_DIST, outdoor_dist=LARGE_DIST):
        start, end = expectedPath[0], expectedPath[-1]
        self._print_path_description(start, end, total_dist, outdoor_dist)
        dfsPath = directed_dfs(self.graph, start, end, total_dist, outdoor_dist)
        print("Expected: ", expectedPath)
        print("DFS: ", dfsPath)
        self.assertEqual(expectedPath, dfsPath)

    def _test_impossible_path(
        self, start, end, total_dist=LARGE_DIST, outdoor_dist=LARGE_DIST
    ):
        self._print_path_description(start, end, total_dist, outdoor_dist)
        with self.assertRaises(ValueError):
            directed_dfs(self.graph, start, end, total_dist, outdoor_dist)

    def test_path_one_step(self):
        self._test_path(expectedPath=["32", "56"])

    def test_path_no_outdoors(self):
        self._test_path(expectedPath=["32", "36", "26", "16", "56"], outdoor_dist=0)

    def test_path_multi_step(self):
        self._test_path(expectedPath=["2", "3", "7", "9"])

    def test_path_multi_step_no_outdoors(self):
        self._test_path(expectedPath=["2", "4", "10", "13", "9"], outdoor_dist=0)

    def test_path_multi_step2(self):
        self._test_path(expectedPath=["1", "4", "12", "32"])

    def test_path_multi_step_no_outdoors2(self):
        self._test_path(
            expectedPath=["1", "3", "10", "4", "12", "24", "34", "36", "32"],
            outdoor_dist=0,
        )

    def test_impossible_path1(self):
        self._test_impossible_path("8", "50", outdoor_dist=0)

    def test_impossible_path2(self):
        self._test_impossible_path("10", "32", total_dist=100)


if __name__ == "__main__":
    unittest.main()
