package com.example.warehouseManagement.DSA;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class BinarySearchTreeTest {
    @Test
    void add() {
       System.out.println("### Test Add Method");
        BinarySearchTree<String> bst = new BinarySearchTree<>();
        bst.add("A");
        bst.add("Z");
        bst.add("Y");
        bst.add("D");
        bst.printTreeGrid();
        System.out.println();
        bst.add("G");
        bst.add("H");
        bst.add("C");
        bst.printTreeGrid();
        System.out.println();
        System.out.printf("Is the bst balanced? %b\n", bst.isBalanced());
        bst.add("W");
        bst.add("L");
        bst.printTreeGrid();
        System.out.println();
        System.out.printf("Is the bst balanced? %b\n", bst.isBalanced());
        bst.add("Q");
        bst.add("J");
        bst.add("E");
        int expResult = 12;
        int result = bst.size();
        System.out.printf("Is the bst balanced? %b\n", bst.isBalanced());
        bst.printTreeGrid();
        assertEquals(expResult, result);
    }
    @Test
    void remove() {
        System.out.println("### Test Remove Method");
        BinarySearchTree<String> bst = new BinarySearchTree<>();
        bst.add("A");
        bst.add("Z");
        bst.add("Y");
        bst.add("D");
        bst.printTreeGrid();
        System.out.println();
        bst.add("G");
        bst.add("H");
        bst.add("C");
        bst.add("W");
        bst.add("L");
        bst.add("Q");
        bst.add("J");
        bst.add("E");
        bst.remove("H");
        bst.remove("A");
        bst.remove("D");
        bst.remove("L");
        bst.remove("C");
        int expResult = 7;
        int result = bst.size();
        System.out.printf("Is the bst balanced? %b\n", bst.isBalanced());
        bst.printTreeGrid();
        assertEquals(expResult, result);
    }
    @Test
    void contains() {
        System.out.println("### Test Contains Method");
        BinarySearchTree<String> bst = new BinarySearchTree<>();
        bst.add("A");
        bst.add("Z");
        bst.add("Y");
        bst.add("D");
        bst.printTreeGrid();
        System.out.println();
        bst.add("G");
        bst.add("H");
        bst.add("C");
        bst.add("W");
        bst.add("L");
        bst.add("Q");
        bst.add("J");
        bst.add("E");
        System.out.printf("Is the bst balanced? %b\n", bst.isBalanced());
        bst.printTreeGrid();
        System.out.println();
        bst.remove("H");
        bst.remove("J");
        bst.remove("w");
        bst.remove("E");
        System.out.printf("Is the bst balanced? %b\n", bst.isBalanced());
        bst.printTreeGrid();
        System.out.println();
        bst.remove("A");
        bst.remove("D");
        // bst.remove("L");
        // bst.remove("C");
        // bst.remove("H");
        boolean expResult = true;
        boolean result = bst.contains("Q");
        System.out.printf("Is the bst balanced? %b\n", bst.isBalanced());
        bst.printTreeGrid();
        assertEquals(expResult, result);
    }
}
