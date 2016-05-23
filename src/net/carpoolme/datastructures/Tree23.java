package net.carpoolme.datastructures;

/**
 * Created by John Andersen on 5/23/16.
 */
public class Tree23 {

    private static final int TREE23_NUM_NODES = 3;

    private static final int TREE23_LEFT = 0;
    private static final int TREE23_RIGHT = 1;
    private static final int TREE23_MIDDLE = 2;

    private Tree23[] nodes = null;
    private Object[] data = null;
    private String[] key = null;
    private int contains = 0;

    public Tree23() {
        nodes = new Tree23[TREE23_NUM_NODES];
        data = new Object[TREE23_NUM_NODES - 1];
        key = new String[TREE23_NUM_NODES - 1];
        int i;
        for (i = (TREE23_NUM_NODES - 1); i >= 0; --i) {
            nodes[i] = null;
        }
        for (i = (TREE23_NUM_NODES - 2); i >= 0; --i) {
            data[i] = null;
            key[i] = null;
        }
    }

    public Tree23(final Tree23 copyNode) {
        this();
        copySingle(copyNode);
    }

    public Tree23(final String copyKey, final Object copyData) {
        this();
        set(copyKey, copyData);
    }

    // Sets the left to the data and marks the right as inactive
    private Tree23 set(final String setKey, final Object setData) {
        data[TREE23_LEFT] = setData;
        key[TREE23_LEFT] = setKey;
        return this;
    }

    private void copySingle(final Tree23 copyNode) {
        if (copyNode == null || copyNode == this) {
            return;
        }
        int i;
        contains = copyNode.contains;
        for (i = (TREE23_NUM_NODES - 2); i >= 0; --i) {
            data[i] = copyNode.data[i];
            key[i] = copyNode.key[i];
        }
    }

    /**
     * Copies the contents of copyTree into the tree is was called on
     * @param copyTree The tree to copy from
     */
    public void copy(final Tree23 copyTree) {
        int i;
        if (copyTree == null || copyTree == this) {
            return;
        }
        copySingle(copyTree);
        for (i = (TREE23_NUM_NODES - 1); i >= 0; --i) {
            if (copyTree.nodes[i] != null) {
                nodes[i] = new Tree23();
                nodes[i].copy(copyTree.nodes[i]);
            }
        }
    }

    private boolean active(int index) {
        return nodes[index] != null;
    }

    public Tree23 add(final String addKey, final Object to_add) {
        return addToTree(addKey, to_add);
    }

    private Tree23 addToTree(final String addKey, final Object addData) {
        ++contains;
        Tree23 send_up = null;
        // Neither are active, set the left
        if (!active(TREE23_LEFT) && !active(TREE23_RIGHT)) {
            data[TREE23_LEFT] = addData;
        } else if (nodes[TREE23_LEFT] == null && nodes[TREE23_MIDDLE] == null &&
                nodes[TREE23_RIGHT] == null) {
            if (active(TREE23_LEFT) && !active(TREE23_RIGHT)) {
                if (0 < addKey.compareTo(key[TREE23_LEFT])) {
                    // Left is active and less than left
                    data[TREE23_RIGHT] = data[TREE23_LEFT];
                    data[TREE23_LEFT] = addData;
                } else {
                    // Left is active and greater than or equal to left
                    data[TREE23_RIGHT] = addData;
                }
            } else if (active(TREE23_RIGHT) && active(TREE23_LEFT)) {
                send_up = this;
                // Both active choose who to send up
                if (0 < addKey.compareTo(key[TREE23_LEFT])) {
                    // Less than left, left is middle send up left
                    nodes[TREE23_LEFT] = new Tree23(addKey, addData);
                    nodes[TREE23_RIGHT] = new Tree23(key[TREE23_RIGHT], data[TREE23_RIGHT]);
                } else if (0 >= addKey.compareTo(key[TREE23_RIGHT])) {
                    // Greater than or equal to the right, right is middle send up right
                    nodes[TREE23_LEFT] = new Tree23(key[TREE23_LEFT], data[TREE23_LEFT]);
                    nodes[TREE23_RIGHT] = new Tree23(addKey, addData);
                    set(key[TREE23_RIGHT], data[TREE23_RIGHT]);
                } else {
                    // Data to add is in between left and right it is the middle so
                    // send it up
                    nodes[TREE23_LEFT] = new Tree23(key[TREE23_LEFT], data[TREE23_LEFT]);
                    nodes[TREE23_RIGHT] = new Tree23(key[TREE23_RIGHT], data[TREE23_RIGHT]);
                    set(addKey, addData);
                }
            }
        } else {
            if (active(TREE23_LEFT) && !active(TREE23_RIGHT) &&
                    nodes[TREE23_LEFT] != null && nodes[TREE23_RIGHT] != null) {
                // Left is active right is not
                if (0 < addKey.compareTo(key[TREE23_LEFT])) {
                    // Left is active and less than left
                    send_up = push_up(nodes[TREE23_LEFT].add(addKey, addData));
                } else {
                    // Left is active and greater than or equal to left
                    send_up = push_up(nodes[TREE23_RIGHT].add(addKey, addData));
                }
            } else if (active(TREE23_RIGHT) && active(TREE23_LEFT) &&
                    nodes[TREE23_LEFT] != null && nodes[TREE23_MIDDLE] != null &&
                    nodes[TREE23_RIGHT] != null) {
                // Both active so we have a middle because we are not a leaf
                if (0 < addKey.compareTo(key[TREE23_LEFT])) {
                    // Both active and less than left
                    send_up = push_up(nodes[TREE23_LEFT].add(addKey, addData));
                } else if (0 >= addKey.compareTo(key[TREE23_RIGHT])) {
                    // Both active and greater than or equal to right
                    send_up = push_up(nodes[TREE23_RIGHT].add(addKey, addData));
                } else {
                    // Both active and in between left and right
                    send_up = push_up(nodes[TREE23_MIDDLE].add(addKey, addData));
                }
            }
        }
        return send_up;
    }

    // If add returns a node then it is pushing that node up. This handles the
    // pushing up of a node
    private Tree23 push_up(Tree23 pushed_up) {
        Tree23 send_up = null;
        // Nothing pushed up
        if (pushed_up == null) {
            return null;
        }
        if (this == pushed_up) {
            return null;
        }
        // If the child was full and I am full
        if (active(TREE23_RIGHT) && active(TREE23_LEFT)) {
            send_up = this;
            // Both active choose who to send up
            if (0 < pushed_up.key[TREE23_LEFT].compareTo(key[TREE23_LEFT])) {
                // Less than left, left is middle send up left
                Tree23 new_right = new Tree23(key[TREE23_RIGHT], data[TREE23_RIGHT]);
                new_right.nodes[TREE23_RIGHT] = nodes[TREE23_RIGHT];
                new_right.nodes[TREE23_LEFT] = nodes[TREE23_MIDDLE];
                nodes[TREE23_LEFT] = pushed_up;
                nodes[TREE23_RIGHT] = new_right;
                nodes[TREE23_MIDDLE] = null;
            } else if (0 >= pushed_up.key[TREE23_LEFT].compareTo(key[TREE23_RIGHT])) {
                // Greater than or equal to the right, right is middle send up right
                Tree23 new_left = new Tree23(key[TREE23_LEFT], data[TREE23_LEFT]);
                new_left.nodes[TREE23_LEFT] = nodes[TREE23_LEFT];
                new_left.nodes[TREE23_RIGHT] = nodes[TREE23_MIDDLE];
                set(key[TREE23_RIGHT], data[TREE23_RIGHT]);
                nodes[TREE23_LEFT] = new_left;
                nodes[TREE23_RIGHT] = pushed_up;
                nodes[TREE23_MIDDLE] = null;
            } else {
                // Data to add is in between left and right it is the middle so
                // send it up
                Tree23 new_left = new Tree23(key[TREE23_LEFT], data[TREE23_LEFT]);
                Tree23 new_right = new Tree23(key[TREE23_RIGHT], data[TREE23_RIGHT]);
                new_left.nodes[TREE23_LEFT] = nodes[TREE23_LEFT];
                new_left.nodes[TREE23_RIGHT] = pushed_up.nodes[TREE23_LEFT];
                new_right.nodes[TREE23_RIGHT] = nodes[TREE23_RIGHT];
                new_right.nodes[TREE23_LEFT] = pushed_up.nodes[TREE23_RIGHT];
                set(pushed_up.key[TREE23_LEFT], pushed_up.data[TREE23_LEFT]);
                nodes[TREE23_LEFT] = new_left;
                nodes[TREE23_RIGHT] = new_right;
                nodes[TREE23_MIDDLE] = null;
            }
            // Data pushed up goes into the left spot
        } else if (0 < pushed_up.key[TREE23_LEFT].compareTo(key[TREE23_LEFT])) {
            data[TREE23_RIGHT] = data[TREE23_LEFT];
            data[TREE23_LEFT] = pushed_up.data[TREE23_LEFT];
            nodes[TREE23_LEFT] = pushed_up.nodes[TREE23_LEFT];
            nodes[TREE23_MIDDLE] = pushed_up.nodes[TREE23_RIGHT];
            // Data pushed up goes in the right spot
        } else {
            data[TREE23_RIGHT] = pushed_up.data[TREE23_LEFT];
            nodes[TREE23_RIGHT] = pushed_up.nodes[TREE23_RIGHT];
            nodes[TREE23_MIDDLE] = pushed_up.nodes[TREE23_LEFT];
        }
        return send_up;
    }

    public Object get(String searchKey) throws IndexOutOfBoundsException {
        if (searchKey == null) {
            return null;
        }
        // Neither are active, set the left
        if (key[TREE23_LEFT] == null && key[TREE23_RIGHT] == null) {
            return null;
        } else if (key[TREE23_LEFT] != null && key[TREE23_LEFT].equals(searchKey)) {
            return nodes[TREE23_LEFT];
        } else if (key[TREE23_RIGHT] != null && key[TREE23_RIGHT].equals(searchKey)) {
            return nodes[TREE23_RIGHT];
        }
        if (nodes[TREE23_LEFT] != null && nodes[TREE23_RIGHT] != null
                && nodes[TREE23_MIDDLE] == null && key[TREE23_LEFT] != null) {
            // Left is active right is not
            if (0 < searchKey.compareTo(key[TREE23_LEFT])) {
                // Left is active and less than left
                return nodes[TREE23_LEFT].get(searchKey);
            } else {
                // Left is active and greater than or equal to left
                return nodes[TREE23_RIGHT].get(searchKey);
            }
        }
        if (nodes[TREE23_LEFT] != null && nodes[TREE23_MIDDLE] != null
                && nodes[TREE23_RIGHT] != null && key[TREE23_LEFT] != null &&
                key[TREE23_RIGHT] != null) {
            // Both active so we have a middle because we are not a leaf
            if (0 < searchKey.compareTo(key[TREE23_LEFT])) {
                // Both active and less than left
                return nodes[TREE23_LEFT].get(searchKey);
            } else if (0 >= searchKey.compareTo(key[TREE23_RIGHT])) {
                // Both active and greater than or equal to right
                return nodes[TREE23_RIGHT].get(searchKey);
            } else {
                // Both active and in between left and right
                return nodes[TREE23_MIDDLE].get(searchKey);
            }
        }
        throw new IndexOutOfBoundsException(String.format("Key: \'%s\' not found", searchKey));
    }

    public String key(int index) throws IndexOutOfBoundsException {
        int curr = 0;
        return get_count(index, curr).key[TREE23_LEFT];
    }

    public Object value(int index) throws IndexOutOfBoundsException {
        int curr = 0;
        return get_count(index, curr).data[TREE23_LEFT];
    }

    /*
     * Attempts to get a node at the given index and counts along the way
     */
    private Tree23 get_count(int index, int curr) {
        // To see if one of the sides found it
        Tree23 found;
        // Look of the index on the left, that will be index 0 if its the leftmost node
        if (nodes[TREE23_LEFT] != null) {
            try {
                ++curr;
                found = nodes[TREE23_LEFT].get_count(index, curr);
                return found;
            } catch (IndexOutOfBoundsException ignored) {}
        }
        // Check if this node is the index
        if (active(TREE23_LEFT)) {
            if (index == curr) {
                return new Tree23(key[TREE23_LEFT], data[TREE23_LEFT]);
            }
            // increment the index
            ++curr;
        }
        // Look of the index down the middle
        if (nodes[TREE23_MIDDLE] != null) {
            try {
                ++curr;
                found = nodes[TREE23_MIDDLE].get_count(index, curr);
                return found;
            } catch (IndexOutOfBoundsException ignored) {}
        }
        // Check if this node is the index
        if (active(TREE23_RIGHT)) {
            if (index == curr) {
                return new Tree23(key[TREE23_RIGHT], data[TREE23_RIGHT]);
            }
            // increment the index
            ++curr;
        }
        // Look of the index on the right
        if (nodes[TREE23_RIGHT] != null) {
            try {
                ++curr;
                found = nodes[TREE23_RIGHT].get_count(index, curr);
                return found;
            } catch (IndexOutOfBoundsException ignored) {}
        }
        throw(new IndexOutOfBoundsException());
    }

    // Everything in tree to string
    @Override
    public String toString() {
        String asString = "";
        int i;
        for (i = contains - 1; i >= 0; --i) {
            asString += value(i).toString();
        }
        return asString;
    }

    // Returns contains
    public int size(){
        return contains;
    }
}