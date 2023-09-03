package com.example.warehouseManagement.DSA;

import java.util.ArrayList;
import java.util.List;

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
        String w = word.toLowerCase();
        TrieNode current = this.root;
        for (int i=0; i<w.length(); i++) {
            char c = w.charAt(i);
            int index = c - 'a';
            if (current.getChild(index) == null) {
                current.setChild(index);
                current = current.getChild(index);
            }
            else {
                current = current.getChild(index);
            }
        }
        current.setWord(w);
        current.setIsWord(true);
        this.size++;
    }

    public List<String> getWordList(String prefix) {
        List<String> wordList = new ArrayList<>();
        getWordList(prefix, 0, root, wordList);
        return wordList;
    }

    private void getWordList(String prefix, int i, TrieNode root, List<String> list) {
        if (root != null) {
            if (i == prefix.length()) {
                for (TrieNode node : root.getChildren()) {
                    if (node != null && node.isWord())
                        list.add(node.getWord());
                    getWordList(prefix, i, node, list);               
                }
            }
            else {
                int index = prefix.charAt(i) - 'a';
                if (root.getChild(index) != null && i == prefix.length() - 1) {
                    if (root.getChild(index).isWord())
                        list.add(root.getChild(index).getWord());
                    getWordList(prefix, i + 1, root.getChild(index), list);
                } else {
                    getWordList(prefix, i + 1, root.getChild(index), list);
                }
            }
        }
    }


    private class TrieNode {
        private TrieNode[] children;
        private boolean isWord;
        private String word;

        public TrieNode() {
            this.children = new TrieNode[26];
            this.isWord = false;
            this.word = null;
        }

        public TrieNode getChild(int index) {
            if (index < 0 || index > 26) {
                throw new IndexOutOfBoundsException();
            }
            return this.children[index];
        }

        public void setChild(int index) {
            if (index < 0 || index > 26) {
                throw new IndexOutOfBoundsException();
            }
            this.children[index] = new TrieNode();
        }

        public TrieNode [] getChildren() {
            return this.children;
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
