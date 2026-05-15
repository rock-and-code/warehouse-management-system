package com.example.warehouseManagement.Services;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.example.warehouseManagement.DSA.Trie;

/**
 * Prefix index for autocomplete + a "contains" linear-scan fallback.
 *
 * Built on top of the existing {@link Trie} (Map&lt;Character, TrieNode&gt;).
 * The Trie is case-sensitive by design; this wrapper normalizes every key to
 * lowercase and keeps two side maps so the suggestion result preserves the
 * original display string and carries the entity id back to the controller.
 */
public class SuggestionIndex {

    private final Trie trie = new Trie();
    /** lowercased term → original-case display (one canonical form per key). */
    private final Map<String, String> display = new HashMap<>();
    /** lowercased term → entity ids that share this term (handles duplicates). */
    private final Map<String, List<Long>> ids = new HashMap<>();
    /** Insertion-ordered registry of every term, used by the contains scan. */
    private final Map<String, Boolean> allKeys = new LinkedHashMap<>();

    public void add(String term, Long id) {
        if (term == null || term.isBlank() || id == null) {
            return;
        }
        String key = term.toLowerCase();
        if (!allKeys.containsKey(key)) {
            trie.insert(key);
            display.put(key, term);
            allKeys.put(key, Boolean.TRUE);
        }
        ids.computeIfAbsent(key, k -> new ArrayList<>()).add(id);
    }

    /** All terms (case-insensitive) starting with the prefix, in Trie order. */
    public List<Match> findByPrefix(String prefix, int limit) {
        String p = prefix == null ? "" : prefix.toLowerCase();
        List<String> keys = trie.getWordList(p);
        return materialize(keys, limit);
    }

    /** Linear scan over every indexed term. Use when the user wants "contains". */
    public List<Match> findContaining(String fragment, int limit) {
        String f = fragment == null ? "" : fragment.toLowerCase();
        if (f.isEmpty()) {
            return List.of();
        }
        List<String> hits = new ArrayList<>();
        for (String key : allKeys.keySet()) {
            if (key.contains(f)) {
                hits.add(key);
            }
        }
        return materialize(hits, limit);
    }

    public int size() {
        return allKeys.size();
    }

    private List<Match> materialize(Collection<String> keys, int limit) {
        List<Match> out = new ArrayList<>();
        for (String key : keys) {
            if (out.size() >= limit) break;
            List<Long> matchedIds = ids.getOrDefault(key, List.of());
            for (Long id : matchedIds) {
                if (out.size() >= limit) break;
                out.add(new Match(display.getOrDefault(key, key), id));
            }
        }
        return out;
    }

    /** Lightweight value object — display label + the entity id to link to. */
    public record Match(String label, Long id) {}
}
