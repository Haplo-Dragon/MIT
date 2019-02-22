#!/usr/bin/env python


def height(node):
    if node is None:
        return -1
    else:
        return node.height


class AVLNode():
    """Node of an AVL tree."""

    def __init__(self, key, parent):
        self.key = key
        self.parent = parent
        self.size = 1
        self.left_child = None
        self.right_child = None

        self.update_height()
        # update_height(self)

        # self.check_rep()

    def insert(self, node):
        """Insert a node into the subtree rooted at this node."""
        if node is None:
            return
        if node.key < self.key:
            if self.left_child is None:
                node.parent = self
                self.left_child = node
            else:
                self.left_child.insert(node)
        else:
            if self.right_child is None:
                node.parent = self
                self.right_child = node
            else:
                self.right_child.insert(node)
        # self.check_rep()

    def delete(self):
        """Delete and return this node from the tree.

        Case 1: Node has no children - Change its parent to point to None.
        Case 2: Node has one child - Link node's parent to node's child.
        Case 3: Node has two children - Swap node and its next larger node, then
                delete the next larger (which now contains the original node's key).
        """
        # Cases 1 and 2: The node has 0-1 children.
        if self.left_child is None or self.right_child is None:
            # The node is a left child of its parent.
            if self is self.parent.left_child:
                # Link the node's parent to its child, or to None if the node has no
                # children.
                self.parent.left_child = self.left_child or self.right_child

                if self.parent.left_child is not None:
                    # If the node had a child, link the child to its new parent.
                    self.parent.left_child.parent = self.parent

            # The node is a right child of its parent.
            else:
                # Again, link the node's parent to its child, or to None if there are no
                # children.
                self.parent.right_child = self.left_child or self.right_child

                if self.parent.right_child is not None:
                    # Again, if the node had a child, link the child to its new parent.
                    self.parent.right_child.parent = self.parent

            # I initially forgot this return statement, which resulted in ENORMOUS
            # pain and suffering. Because of this missing line, RangeIndex.count was
            # returning incorrect counts, (unbeknowst to me) node deletion was not
            # happening properly. Essentially, every time I tried to delete a node, the
            # RangeIndex stayed the same size, so the count slowly creeped upward and
            # got out of sync with the correct answer. What a nightmare!
            # The bug seemed occasionally to come and go randomly (because sometimes
            # the deletion happened correctly by accident due to garbage collection,
            # maybe?).
            return self

        # Case 3: The node has 2 children.
        else:
            # Find the next larger node.
            successor = self.next_larger()
            # Swap the node and its next larger node.
            self.key, successor.key = successor.key, self.key
            # Delete the next larger (which now contains the original node's key).
            return successor.delete()

    def find(self, key):
        """Find the given key in the subtree rooted at this node."""
        if self.key == key:
            return self
        elif key < self.key:
            if self.left_child is not None:
                return self.left_child.find(key)
            else:
                return None
        else:
            if self.right_child is not None:
                return self.right_child.find(key)
            else:
                return None

    def find_min(self):
        """Find the smallest node in the subtree rooted at this node."""
        current_node = self
        while current_node.left_child is not None:
            current_node = current_node.left_child
        return current_node

    def next_larger(self):
        """Find the smallest node that is larger than this node."""
        if self.right_child is not None:
            # Case 1: the node has a right child, so the next larger node is just the
            # minimum node in its right child.
            return self.right_child.find_min()
        else:
            # Case 2: the node does not have a right child. We move up the tree until
            # we find a node that is the left child of its parent.
            ancestor = self.parent
            child = self
            while ancestor is not None and child == ancestor.right_child:
                # Move child and ancestor up the tree one node each.
                child = ancestor
                ancestor = ancestor.parent
            return ancestor

    def update_height(self):
        left_height = self.left_child.height if self.left_child else -1
        right_height = self.right_child.height if self.right_child else -1
        self.height = max(left_height, right_height) + 1

    def update_size(self):
        left_size = self.left_child.size if self.left_child else 0
        right_size = self.right_child.size if self.right_child else 0
        self.size = left_size + right_size + 1

    def is_left_heavy(self):
        if self.left_child is None:
            return False
        else:
            left_height = self.left_child.height
            right_height = self.right_child.height if self.right_child else -1
            return (left_height > right_height + 1)

    def is_right_heavy(self):
        if self.right_child is None:
            return False
        else:
            left_height = self.left_child.height if self.left_child else -1
            right_height = self.right_child.height
            return (right_height > left_height + 1)

    # def check_rep(self):
    #     if self.is_left_heavy() or self.is_right_heavy():
    #         raise RuntimeError("AVL property violated by node.")

    #     if self.left_child is not None:
    #         if self.left_child.parent is not self:
    #             raise RuntimeError("AVL node's left child parent pointer invalid.")
    #         if self.left_child.key > self.key:
    #             raise RuntimeError("AVL node's left child is greater than node.")
    #         self.left_child.check_rep()

    #     if self.right_child is not None:
    #         if self.right_child.parent is not self:
    #             raise RuntimeError("AVL node's right child parent pointer invalid.")
    #         if self.right_child.key < self.key:
    #             raise RuntimeError("AVL node's right child is less than node.")
    #         self.right_child.check_rep()


class RangeIndex():
    """AVL tree-based range index implementation."""

    def __init__(self):
        self.root = None

    def add(self, key):
        """Insert a node with the given key into the AVL tree."""
        if key is None:
            raise ValueError("Cannot insert None into the range index.")
        if self.find(key):
            raise ValueError("The range index already contains a node with that key.")

        node = AVLNode(key, parent=None)
        if self.root is None:
            self.root = node
        else:
            self.root.insert(node)
        self._rebalance(node)

    def remove(self, key):
        node = self._remove(key)
        # Since we just deleted a node, its old parent may be out of balance.
        self._rebalance(node.parent)

    def _remove(self, key):
        """Remove and return a node with the given key if it exists in the tree."""
        node = self.find(key)
        if node is None:
            return None
        if node is self.root:
            # Create a temporary root to hold the tree together.
            temp_root = AVLNode(0, parent=None)
            # Link the temporary root and the original root to each other.
            temp_root.left_child = self.root
            self.root.parent = temp_root
            # Delete the original root.
            deleted_root = self.root.delete()
            # Since the rest of the tree is currently a left child of the temporary root,
            # we'll just set the tree's new root to be the top of the temp's left child,
            # or None if there are no nodes left in the tree.
            self.root = temp_root.left_child

            # If there are still nodes, we'll set up our new root to have no parent.
            if self.root is not None:
                self.root.parent = None

            return deleted_root
        else:
            return node.delete()

    def find(self, key):
        """Find the node with a given key. Returns None if the key isn't in the tree."""
        return self.root and self.root.find(key)

    def find_min(self):
        """Return the node with the smallest key in the tree."""
        return self.root and self.root.find_min()

    def next_larger(self, key):
        """Return the node with the next larger key in relation to the given key."""
        node = self.find(key)
        return node and node.next_larger()

    def list(self, first_key, last_key):
        if last_key < first_key:
            raise ValueError("Range invalid - first_key must be lower than last_key.")
        least_common_ancestor = self._LCA(first_key, last_key)
        result = self._node_list(least_common_ancestor, first_key, last_key)
        return result

    def count(self, first_key, last_key):
        if last_key < first_key:
            return 0
        first_key_in_tree = self.find(first_key)
        # If the first key is in the tree, both ranks will count it. Since we're
        # subtracting one rank from the other, we end up removing the first key from
        # our count, so we need to add it back in. That's why we add 1.
        if first_key_in_tree:
            count = self._rank(last_key) - self._rank(first_key) + 1
        else:
            count = self._rank(last_key) - self._rank(first_key)
        return count

    def _LCA(self, first_key, last_key):
        node = self.root
        found = False

        # Until we hit a None node or first_key <= node.key <= last_key, we keep going.
        while (node is not None) and (not found):
            if (first_key <= node.key <= last_key):
                found = True
            elif first_key < node.key:
                node = node.left_child
            else:
                node = node.right_child

        return node

    def _node_list(self, node, first_key, last_key):
        if node is None:
            return []

        node_list = []
        # If the current key is between our first and last keys, we add it to the list.
        if first_key <= node.key <= last_key:
            node_list.append(node.key)
        # Then we'll recursively consider the left and right subtrees, if necessary.
        if first_key <= node.key:
            node_list.extend(self._node_list(node.left_child, first_key, last_key))
        if node.key <= last_key:
            node_list.extend(self._node_list(node.right_child, first_key, last_key))

        return node_list

    def _rank(self, key):
        """Returns the number of keys in the tree that are less than or equal to key."""
        rank = 0
        # Start at root.
        current_node = self.root
        # Walk down tree looking for key.
        while current_node is not None:
            # If the current node is smaller, we'll add to the rank total.
            if current_node.key <= key:
                # Add 1 for each node that's smaller or equal.
                rank += 1
                # Add subtree sizes to the left.
                if current_node.left_child is not None:
                    rank += current_node.left_child.size

                # If we haven't found the target key yet, continue down the tree.
                if current_node.key < key:
                    current_node = current_node.right_child
                # Otherwise, we've found the target key and we're done, so we'll break
                # out of the while loop.
                else:
                    break

            # If the current node is bigger, we'll just continue down the tree to the
            # left.
            else:
                current_node = current_node.left_child

        return rank

    def _left_rotate(self, x):
        # print("Left-rotating node with key {0}...".format(x.key))
        y = x.right_child
        # if y is None:
        #     print("Here's the problem! Right child is None!")
        #     print("Node is right-heavy?", x.is_right_heavy())
        #     print("Left child height {0}, Right child height {1}".format(
        #         x.left_child.height if x.left_child else -1,
        #         x.right_child.height if x.right_child else -1))
        #     print("Node's parent should be left-heavy. Is it?",
        #         x.parent.is_left_heavy())
        #     print("Node's parent: Left child height {0}, Right child height {1}".format(
        #         x.parent.left_child.height,
        #         x.parent.right_child.height if x.parent.right_child else -1))
        # Link x to its new right child, y's old left child, which may be None.
        x.right_child = y.left_child
        # If y had a left child, link it to its new parent, x.
        if y.left_child is not None:
            y.left_child.parent = x

        # Link y to its new parent, x's old parent. If x was the root of the tree (i.e.,
        # its parent was None), make y the new root of the tree. Otherwise, x was either
        # the left or right child of its parent, so make y the left or right child,
        # accordingly.
        y.parent = x.parent
        # x was the root of the tree.
        if x.parent is None:
            self.root = y
        else:
            # x was a left child.
            if x == x.parent.left_child:
                x.parent.left_child = y
            # x was a right child.
            else:
                x.parent.right_child = y

        # Link y to its new left child, x. Link x to its new parent, y.
        y.left_child = x
        x.parent = y

        # Because this is an AVL tree, we need to update the heights of the nodes
        # we changed.
        x.update_height()
        y.update_height()

        x.update_size()
        y.update_size()

    def _right_rotate(self, x):
        y = x.left_child
        # Link x to its new left child, y's old right child, which may be None.
        x.left_child = y.right_child
        # If y had a right child, link it to its new parent, x.
        if y.right_child is not None:
            y.right_child.parent = x

        # Link y to its new parent, x's old parent. If x was the root of the tree (i.e.,
        # its parent was None), make y the new root of the tree. Otherwise, x was either
        # the left or right child of its parent, so make y the left or right child,
        # accordingly.
        y.parent = x.parent
        # x was the root of the tree.
        if x.parent is None:
            self.root = y
        else:
            # x was a left child.
            if x == x.parent.left_child:
                x.parent.left_child = y
            # x was a right child.
            else:
                x.parent.right_child = y

        # Link y to its new right child, x. Link x to its new parent, y.
        y.right_child = x
        x.parent = y

        # Because this is an AVL tree, we need to update the heights of the nodes
        # we changed.
        x.update_height()
        y.update_height()

        x.update_size()
        y.update_size()

    def _rebalance(self, node):
        """Rebalances AVL tree to ensure that height is always O(lg n).

        Case 1: The node is left-heavy.
            Sub-case 1.1: The left child is left-heavy or balanced. The heaviness is all
                          in one direction (left), so we can just right-rotate the
                          original node.
            Sub-case 1.2: The left child is right-heavy. We'll left-rotate it so that the
                          heaviness is all to the left. Then it matches sub-case 1.1 and
                          we can just right-rotate the original node.

        Case 2: The node is right-heavy.
            Sub-case 2.1: The right child is left-heavy. We'll right-rotate it so that
                          the heaviness is all to the right. Then it matches sub-case
                          2.2 and we can just left-rotate the original node.
            Sub-case 2.2: The right child is right-heavy or balanced. The heaviness is
                          all in one direction (right), so we can just left-rotate the
                          original node.
        Case 3: The node is balanced. We're done with this node and can move up the tree
                to the next node.
        """
        while node is not None:
            node.update_height()
            # update_height(node)
            node.update_size()

            # Case 1: the node is left-heavy.
            if node.is_left_heavy():
                # Sub-case 1.1: the left child is left-heavy or balanced.
                # if node.left_child.is_left_heavy() or node.left_child.is_balanced():
                if height(node.left_child.left_child) >= height(node.left_child.right_child):
                    self._right_rotate(node)
                # Sub-case 1.2: the left child is right-heavy.
                else:
                    self._left_rotate(node.left_child)
                    self._right_rotate(node)

            # Case 2: the node is right-heavy.
            elif node.is_right_heavy():
                # Sub-case 2.1: the right child is left-heavy.
                # if node.right_child.is_left_heavy():
                if height(node.right_child.right_child) >= height(node.right_child.left_child):
                    # self._right_rotate(node.right_child)
                    # self._left_rotate(node)
                    self._left_rotate(node)
                # Sub-case 2.2: the right child is right-heavy or balanced.
                else:
                    # self._left_rotate(node)
                    self._right_rotate(node.right_child)
                    self._left_rotate(node)

            # Case 3: the node is balanced.
            node = node.parent


class TracedRangeIndex(RangeIndex):
    """Augments RangeIndex to build a trace for the visualizer."""

    def __init__(self, trace):
        """Sets the object receiving tracing info."""
        RangeIndex.__init__(self)
        self.trace = trace

    def add(self, key):
        self.trace.append({"type": "add", "id": key.wire.name})
        RangeIndex.add(self, key)

    def remove(self, key):
        self.trace.append({"type": "delete", "id": key.wire.name})
        RangeIndex.remove(self, key)

    def list(self, first_key, last_key):
        result = RangeIndex.list(self, first_key, last_key)
        self.trace.append(
            {
                "type": "list",
                "from": first_key.key,
                "to": last_key.key,
                "ids": [key.wire.name for key in result],
            }
        )
        return result

    def count(self, first_key, last_key):
        result = RangeIndex.count(self, first_key, last_key)
        self.trace.append(
            {"type": "list", "from": first_key.key, "to": last_key.key, "count": result}
        )
        return result


def testRangeIndex():
    tree = RangeIndex()
    tree.add(50)
    tree.add(25)
    tree.add(75)
    tree.add(13)
    tree.add(37)
    tree.add(87)
    tree.add(63)
    tree.add(7)
    tree.add(19)
    tree.add(31)
    return tree


def testRank():
    tree = testRangeIndex()
    print(tree._rank(50), '\t\t\tshould be 7')
    print(tree._rank(49), '\t\t\tshould be 6')
    print(tree._rank(25), '\t\t\tshould be 4')
    print(tree._rank(100),'\t\t\tshould be 10')
    print(tree._rank(1),  '\t\t\tshould be 0')
    print(tree._rank(7),  '\t\t\tshould be 1')


def testCount():
    tree = testRangeIndex()
    print(tree.count(0,100), '\t\t\tshould be 10')
    print(tree.count(7,87),  '\t\t\tshould be 10')
    print(tree.count(8,87),  '\t\t\tshould be 9')
    print(tree.count(19,62), '\t\t\tshould be 5')
    print(tree.count(100,0), '\t\t\tshould be 0')
    print(tree.count(50,50), '\t\t\tshould be 1')


if __name__ == "__main__":
    testRank()
    testCount()
