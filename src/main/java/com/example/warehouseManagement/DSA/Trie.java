package com.example.warehouseManagement.DSA;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Trie {

    private TrieNode root;
    private int size;

    

    public Trie() {
        this.root = new TrieNode();
        this.size = 0;
    }

    /**
     * Returns an integer representing the number of words in the Trie
     * @return
     */
    public int size() {
        return this.size;
    }

    public void insert(String word) {
        TrieNode current = this.root;
        for (char c : word.toCharArray()) {
            if (current.getChild(c) == null) {
                current.setChild(c);
                current = current.getChild(c);
            }
            else {
                current = current.getChild(c);
            }
        }
        current.setWord(word);
        current.setIsWord(true);
        current.size++; // size of current TrieNode
        this.size++; // size of Trie
    }

    public void remove(String word) {
        TrieNode current = this.root;
        for (char c : word.toCharArray()) {
            if (current.getChild(c) == null) {
                return;
            }
            else {
                current = current.getChild(c);
            }
        }
        current.setWord(null);
        current.setIsWord(false);
        current.size--; // size of current TrieNode
        if (current.size == 0)
            current = null;
        this.size--; // size of Trie
    }

    public List<String> getWordList(String prefix) {
        List<String> wordList = new ArrayList<>();
        getWordList(prefix, 0, root, wordList);
        return wordList;
    }

    private void getWordList(String prefix, int i, TrieNode root, List<String> list) {
        if (root != null) {
            if (i == prefix.length()) {
                for (char c : root.getChildren()) {
                    if (root.getChild(c).isWord)
                        list.add(root.getChild(c).getWord());
                    getWordList(prefix, i, root.getChild(c), list);               
                }
            }
            else {
                char c = prefix.charAt(i);
                if (root.getChild(c) != null) {
                    if (i == prefix.length() - 1 && root.getChild(c).isWord())
                        list.add(root.getChild(c).getWord());
                    getWordList(prefix, i + 1, root.getChild(c), list);
                }
            }
        }
    }


    private class TrieNode {
        private Map<Character, TrieNode> children;
        private boolean isWord;
        private String word;
        private int size;

        public TrieNode() {
            this.children = new HashMap<>();
            this.isWord = false;
            this.word = null;
            this.size = 0;
        }

        public TrieNode getChild(char c) {
            return children.getOrDefault(c, null);
        }

        public void setChild(char c) {
            children.put(c, new TrieNode());
        }

        public Set<Character> getChildren(){
            return children.keySet();
        }

        public boolean isWord() {
            return isWord;
        }

        public void setIsWord(boolean isWord) {
            this.isWord = isWord;
        }

        public String getWord() {
            return word;
        }

        public void setWord(String word) {
            this.word = word;
        }

    }
}
