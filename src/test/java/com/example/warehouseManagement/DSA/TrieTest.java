package com.example.warehouseManagement.DSA;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

class TrieTest {
    @Test
    void getWordList() {
        System.out.println("### Test Get Word List Method");
        List<String> list = List.of("Apple", "application", "app", "appreciate", "appropriate", "approach", "ball",
                "blink", "ballistic", "battalion", "pluto", "platonic", "plasma");

        Trie trie = new Trie();
        for (String str : list)
            trie.insert(str);

        System.out.printf("Trie size: %d\n", trie.size());

        List<String> output = trie.getWordList("ap");
        System.out.printf("Word List ->%s\n", output.toString());
        List<String> expResult = List.of("app", "appreciate", "appropriate", "approach","application");
        assertArrayEquals(expResult.toArray(), output.toArray());
        System.out.println("List -> " + Arrays.toString(output.toArray()));

        expResult = List.of("Apple");
        output = trie.getWordList("Ap");
        assertArrayEquals(expResult.toArray(), output.toArray());
    }

}
