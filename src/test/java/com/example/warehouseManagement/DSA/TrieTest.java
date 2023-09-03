package com.example.warehouseManagement.DSA;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

public class TrieTest {
    @Test
    void testGetWordList() {
        System.out.println("### Test Get Word List Method");
        List<String> list = List.of("Apple", "application", "app", "appreciate", "appropriate", "approach", "ball",
                "blink", "ballistic", "battalion", "pluto", "platonic", "plasma");

        Trie trie = new Trie();
        for (String str : list)
            trie.insert(str);

        System.out.printf("Trie size: %d\n", trie.size());

        List<String> output = trie.getWordList("ap");
        List<String> expResult = List.of("app", "apple", "application", "appreciate", "approach", "appropriate");
        assertArrayEquals(expResult.toArray(), output.toArray());
        System.out.println("List -> " + Arrays.toString(output.toArray()));
    }

}
