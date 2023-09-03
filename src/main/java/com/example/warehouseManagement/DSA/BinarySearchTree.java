package com.example.warehouseManagement.DSA;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BinarySearchTree<T extends Comparable<T>> {
    /**
     * A custom implementation of a generic binary search tree
     */

    /**
     * A custom node tree node inner class to support the custom
     * bst implementation
     */
    private class TreeNode {
        T value;
        int height;
        TreeNode left;
        TreeNode right;

        public TreeNode(T value) {
            this.value = value;
            this.height = 1;
            this.left = this.right = null;
        }
    }

    private int size;
    private TreeNode root;

    public BinarySearchTree() {
        this.root = null;
        this.size = 0;
    }

    /**
     * Returns the size of the bst. The size is the number of elements in the bst
     * @return
     */
    public int size() {
        return size;
    }

    /**
     * Returns true if the given element exist in this bst
     * Time: O(LOG N)
     * Space: O(1)
     * @param value
     * @return
     */
    public boolean contains(T value) {
        if (this.root == null)
            return false;
        TreeNode current = this.root;
        while (current != null) {
            if (value.compareTo(current.value) < 0)
                current = current.left;
            else if (value.compareTo(current.value) > 0)
                current = current.right;
            else //Bingo
                return true;
        }
        return false;
    }

    /**
     * Adds the given element in the bst
     * Time: O(LOG N)
     * Space: O(1)
     * @param value
     * @return
     */
    public void add(T value) {
        TreeNode newNode = new TreeNode(value);
        if (this.root == null) {
            this.root = newNode;
            this.size++;
            return;
        }
        TreeNode current = this.root;
        while (true) {
            if (value.compareTo(current.value) < 0) {
                if (current.left == null) {
                    current.left = newNode;
                    break;
                }
                current = current.left;
            }
            else if (value.compareTo(current.value) > 0) {
                if (current.right == null) {
                    current.right = newNode;
                    break;
                }
                current = current.right;
            }
            else // Duplicates are not allowed
                return;
        }
        balanceTreeAfterInsertion(value);
        this.size++;
    }
    /**
     * Removes the first instance of the given value in this bst if exists.
     * Time: O(LOG N)
     * Space: O(1)
     * @param value
     * @return
     */
    public boolean remove(T value) {
        if (this.root == null)
            return false;
        TreeNode parent = null;
        TreeNode current = this.root;
        while (current != null) {
            if (value.compareTo(current.value) < 0) {
                parent = current;
                current = current.left;
            }
            else if (value.compareTo(current.value) > 0) {
                parent = current;
                current = current.right;
            }
            else {
                //Bingo!
                //Option # 1 no right child
                if (current.right == null) {
                    // Checking if we are removing the root
                    if (parent == null)
                        this.root = current.left;
                    else {
                        if (current.value.compareTo(parent.value) < 0)
                            parent.left = current.left;
                        else
                            parent.right = current.left;
                    }
                    this.size--;
                    balanceTreeAfterDeletion(value);
                    return true;
                }
                //Option # 2 right child has no left child
                if (current.right.left == null) {
                    current.right.left = current.left;
                    if (parent == null)
                        this.root = current.right;
                    else {
                        //Checking whether we are deleting the parent left or right so current child will be assing 
                        //as the current parent child
                        if (current.value.compareTo(parent.value) < 0)
                            parent.left = current.right;
                        else
                            parent.right = current.right;
                    }
                    this.size--;
                    balanceTreeAfterDeletion(value);
                    return true;
                }
                //Option # 3 right child has a left child then find the left most node of right child
                if (current.right.left != null) {
                    TreeNode leftMostParent = current.right;
                    TreeNode leftMost = current.right.left;
                    while (leftMost.left != null) {
                        leftMostParent = leftMost;
                        leftMost = leftMost.left;
                    }
                    //The left most right is the leftmost parent left 
                    leftMostParent.left = leftMost.right;
                    leftMost.left = current.left;
                    leftMost.right = current.right;

                    if (parent == null)
                        this.root = leftMost;
                    else {
                        if (current.value.compareTo(parent.value) < 0)
                            parent.left = leftMost;
                        else
                            parent.right = leftMost;
                    }
                    this.size--;
                    balanceTreeAfterDeletion(value);
                    return true;
                }
            }
        }
        return false;
    }

    private int updateTreeHeight(TreeNode root) {
        if (root == null)
            return 0;
        int leftChildHeight;
        int rightChildHeight;
        if (root.left == null)
            leftChildHeight = 0;
        else
            leftChildHeight = root.left.height = updateTreeHeight(root.left);
        if (root.right == null)
            rightChildHeight = 0;
        else
            rightChildHeight = root.right.height = updateTreeHeight(root.right);
        return Math.max(rightChildHeight, leftChildHeight) + 1;
    }

    private int getBalance(TreeNode node) {
        if (node == null)
            return 0;
        return getHeight(node.left) - getHeight(node.right);
    }
    
    private int getHeight(TreeNode node) {
        if (node == null)
             return 0;
       return node.height; 
    }

    private int height(TreeNode node) {
        if (node == null)
            return 0;
        return Math.max(height(node.left), height(node.right)) + 1;
    }

    public boolean isValidBST() {
        return isValidBST(root);
    }

    private boolean isValidBST(TreeNode node) {
        if (node == null)
            return true;
        if (node.left != null && node.left.value.compareTo(node.value) > 0)
            return false;
        if (node.right != null && node.right.value.compareTo(node.value) < 0)
            return false;
        return true;
    }

    public boolean isBalanced() {
        return isBalanced(root);
    }

    private boolean isBalanced(TreeNode node) {
        if (node == null)
            return true;
        int leftChildHeight = height(node.left);
        int rightChildHeight = height(node.right);
        if (Math.abs(leftChildHeight - rightChildHeight) <= 1 && isBalanced(node.left) && isBalanced(node.right))
            return true;
        return false;
    }

    private void rightRotate(TreeNode node, TreeNode parent) {
        TreeNode leftChild = node.left;
        TreeNode subTree = leftChild.right;

        node.left = subTree;
        leftChild.right = node;

        node.height = Math.max(getHeight(node.left), getHeight(node.right)) + 1;
        leftChild.height = Math.max(getHeight(leftChild.left), getHeight(node.right)) + 1;

        node = leftChild;

        if (parent == null)
            this.root = node;
        else {
            if (node.value.compareTo(parent.value) < 0)
                parent.left = node;
            else
                parent.right = node;
        }
    }

    private void leftRotate(TreeNode node, TreeNode parent) {
        TreeNode rightChild = node.right;
        TreeNode subTree = rightChild.left;

        node.right = subTree;
        rightChild.left = node;

        node.height = Math.max(getBalance(node.left), getBalance(node.right)) + 1;
        rightChild.height = Math.max(getBalance(rightChild.left), getBalance(node.right)) + 1;

        node = rightChild;

        if (parent == null)
            this.root = node;
        else {
            if (node.value.compareTo(parent.value) < 0)
                parent.left = node;
            else
                parent.right = node;
        }
    }

    public void balanceTreeAfterInsertion(T value) {
        TreeNode parent = null;
        TreeNode current = this.root;
        updateTreeHeight(this.root); //Important step to balance the tree

        while (current != null) {
            int balance = getBalance(current);
            if (balance > 1) {
                //Unbalance on the left child
                if (value.compareTo(current.left.value) > 0)
                    leftRotate(current.left, current);
                rightRotate(current, parent);
            }
            else if (balance < -1) {
                //Unbalance on the right child
                if (value.compareTo(current.right.value) < 0)
                    rightRotate(current.right, current);
                leftRotate(current, parent);
            }
            //Traversing throught the path the deleted node traversed it
            parent = current;
            if (value.compareTo(current.value) < 0)
                current = current.left;
            else
                current = current.right;
        }
    }

    public void balanceTreeAfterDeletion(T value) {
        TreeNode parent = null;
        TreeNode current = this.root;
        updateTreeHeight(this.root); //Important step to balance the tree

        while (current != null) {
            int balance = getBalance(current);
            if (balance > 1) {
                //Unbalance on the left child
                if (getBalance(current.left) < 0)
                    leftRotate(current.left, current);
                rightRotate(current, parent);
            }
            else if (balance < -1) {
                //Unbalance on the right child
                if (getBalance(current.right) > 0)
                    rightRotate(current.right, current);
                leftRotate(current, parent);
            }
            //Traversing throught the path the deleted node traversed it
            parent = current;
            if (value.compareTo(current.value) < 0)
                current = current.left;
            else
                current = current.right;
        }
    }

    public void inOrder() {
        System.out.print("[");
        inOrder(this.root);
        System.out.println("]");
    }

    public void inOrder(TreeNode root) {
        if (root != null) {
            inOrder(root.left);
            System.out.print(root.value + " ");
            inOrder(root.right);
        }
    }

    private List<List<String>> getTreeGrid(TreeNode node) {
        int height = height(node);
        int width = (int)Math.pow(2, height) - 1;
        List<List<String>> grid = new ArrayList<>();
        List<String> row = new ArrayList<>();
        for (int i=0; i<width; i++)
            row.add(" ");
        for (int i=0; i<height; i++)
            grid.add(new ArrayList<>(row));
        setRow(grid, node, 0, 0, width -1);
        return grid;
    }

    private void setRow(List<List<String>> grid, TreeNode node, int row, int left, int right) {
        if (node != null) {
            int mid = (left + right) / 2;
            setRow(grid, node.left, row + 1, left, mid - 1);
            grid.get(row).set(mid, String.valueOf(node.value));
            setRow(grid, node.right, row + 1, mid + 1, right);
        }
    }

    public void printTreeGrid() {
        List<List<String>> grid = getTreeGrid(root);
        for (List<String> row : grid)
            System.out.println(Arrays.toString(row.toArray()));
    }
 }
     



