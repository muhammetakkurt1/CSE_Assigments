/**
 * AVLTree class for managing a balanced binary search tree of stocks.
 * This class supports insertion, deletion, searching, and traversal operations,
 * while ensuring the tree remains balanced.
 *
 * @author Muhammet Akkurt
 * @version 1.0
 */
public class AVLTree {
    private class Node {
        Stock stock;
        Node left, right;
        int height;
        /**
         * Constructs a new Node with the specified stock.
         *
         * @param stock the stock associated with the node
         */
        Node(Stock stock) {
            this.stock = stock;
            this.height = 1;
        }
    }

    private Node root;
    /**
     * Inserts a stock into the AVL tree.
     *
     * @param stock the stock to be inserted
     */
    public void insert(Stock stock) {
        root = insert(root, stock);
    }
    /**
     * Helper method to insert a stock into the AVL tree starting from the given node.
     * Rebalances the tree if necessary.
     *
     * @param node the current node
     * @param stock the stock to be inserted
     * @return the updated node after insertion and rebalancing
     */
    private Node insert(Node node, Stock stock) {
        if (node == null) {
            return new Node(stock);
        }

        if (stock.getSymbol().compareTo(node.stock.getSymbol()) < 0) {
            node.left = insert(node.left, stock);
        } else if (stock.getSymbol().compareTo(node.stock.getSymbol()) > 0) {
            node.right = insert(node.right, stock);
        } else {
            node.stock = stock;
            return node;
        }

        node.height = 1 + Math.max(height(node.left), height(node.right));

        return rebalance(node);
    }
    /**
     * Deletes a stock from the AVL tree by its symbol.
     *
     * @param symbol the symbol of the stock to be deleted
     */
    public void delete(String symbol) {
        root = delete(root, symbol);
    }

    /**
     * Helper method to delete a stock from the AVL tree starting from the given node.
     * Rebalances the tree if necessary.
     *
     * @param root the current node
     * @param symbol the symbol of the stock to be deleted
     * @return the updated node after deletion and rebalancing
     */
    private Node delete(Node root, String symbol) {
        if (root == null) {
            return root;
        }

        if (symbol.compareTo(root.stock.getSymbol()) < 0) {
            root.left = delete(root.left, symbol);
        } else if (symbol.compareTo(root.stock.getSymbol()) > 0) {
            root.right = delete(root.right, symbol);
        } else {
            if ((root.left == null) || (root.right == null)) {
                Node temp = (root.left != null) ? root.left : root.right;

                if (temp == null) {
                    temp = root;
                    root = null;
                } else {
                    root = temp;
                }
            } else {
                Node temp = getMinValueNode(root.right);
                root.stock = temp.stock;
                root.right = delete(root.right, temp.stock.getSymbol());
            }
        }

        if (root == null) {
            return root;
        }

        root.height = Math.max(height(root.left), height(root.right)) + 1;

        return rebalance(root);
    }
    /**
     * Rebalances the AVL tree starting from the given node.
     *
     * @param node the current node
     * @return the updated node after rebalancing
     */
    private Node rebalance(Node node) {
        int balance = getBalance(node);


        if (balance > 1 && getBalance(node.left) >= 0) {
            return rotateRight(node);
        }

        if (balance > 1 && getBalance(node.left) < 0) {
            node.left = rotateLeft(node.left);
            return rotateRight(node);
        }

        if (balance < -1 && getBalance(node.right) <= 0) {
            return rotateLeft(node);
        }

        if (balance < -1 && getBalance(node.right) > 0) {
            node.right = rotateRight(node.right);
            return rotateLeft(node);
        }

        return node;
    }
    /**
     * Finds the node with the minimum value in the subtree rooted at the given node.
     *
     * @param node the root of the subtree
     * @return the node with the minimum value
     */
    private Node getMinValueNode(Node node) {
        Node current = node;
        while (current.left != null) {
            current = current.left;
        }
        return current;
    }
    /**
     * Searches for a stock by its symbol.
     *
     * @param symbol the symbol of the stock to search for
     * @return the found stock, or null if not found
     */
    public Stock search(String symbol) {
        Node result = search(root, symbol);
        return (result != null) ? result.stock : null;
    }

    /**
     * Helper method to search for a stock starting from the given node.
     *
     * @param node the current node
     * @param symbol the symbol of the stock to search for
     * @return the node containing the stock, or null if not found
     */
    private Node search(Node node, String symbol) {
        if (node == null || node.stock.getSymbol().equals(symbol)) {
            return node;
        }

        if (node.stock.getSymbol().compareTo(symbol) > 0) {
            return search(node.left, symbol);
        }

        return search(node.right, symbol);
    }
    /**
     * Gets the height of the given node.
     *
     * @param node the node to get the height of
     * @return the height of the node, or 0 if the node is null
     */
    private int height(Node node) {
        return node == null ? 0 : node.height;
    }
    /**
     * Gets the balance factor of the given node.
     *
     * @param node the node to get the balance factor of
     * @return the balance factor of the node
     */
    private int getBalance(Node node) {
        return node == null ? 0 : height(node.left) - height(node.right);
    }
    /**
     * Performs a left rotation on the given node.
     *
     * @param x the node to perform the left rotation on
     * @return the new root of the subtree
     */
    private Node rotateLeft(Node x) {
        Node y = x.right;
        Node T2 = y.left;

        y.left = x;
        x.right = T2;

        x.height = Math.max(height(x.left), height(x.right)) + 1;
        y.height = Math.max(height(y.left), height(y.right)) + 1;

        return y;
    }
    /**
     * Performs a right rotation on the given node.
     *
     * @param y the node to perform the right rotation on
     * @return the new root of the subtree
     */
    private Node rotateRight(Node y) {
        Node x = y.left;
        Node T2 = x.right;

        x.right = y;
        y.left = T2;

        y.height = Math.max(height(y.left), height(y.right)) + 1;
        x.height = Math.max(height(x.left), height(x.right)) + 1;

        return x;
    }
    /**
     * Performs an in-order traversal of the AVL tree.
     * Prints the stock information for each node.
     */
    public void inOrderTraversal() {
        inOrderTraversal(root);
    }
    /**
     * Helper method to perform an in-order traversal starting from the given node.
     *
     * @param node the current node
     */
    private void inOrderTraversal(Node node) {
        if (node != null) {
            inOrderTraversal(node.left);
            System.out.println(node.stock);
            inOrderTraversal(node.right);
        }
    }
    /**
     * Performs a pre-order traversal of the AVL tree.
     * Prints the stock information for each node.
     */
    public void preOrderTraversal() {
        preOrderTraversal(root);
    }
    /**
     * Performs a post-order traversal of the AVL tree.
     * Prints the stock information for each node.
     */
    private void preOrderTraversal(Node node) {
        if (node != null) {
            System.out.println(node.stock);
            preOrderTraversal(node.left);
            preOrderTraversal(node.right);
        }
    }
    /**
     * Performs a post-order traversal of the AVL tree.
     * Prints the stock information for each node.
     */
    public void postOrderTraversal() {
        postOrderTraversal(root);
    }
    /**
     * Helper method to perform a post-order traversal starting from the given node.
     *
     * @param node the current node
     */
    private void postOrderTraversal(Node node) {
        if (node != null) {
            postOrderTraversal(node.left);
            postOrderTraversal(node.right);
            System.out.println(node.stock);
        }
    }
}
