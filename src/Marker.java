import java.util.*;

/**
 * Missions 3 and 4. Find a word in a long text.
 */
public class Marker {

    /**
     * Mission 3a. The prefix function of p. pi[i] = the length of the longest proper prefix of p[0..i] that is
     * also a suffix of p[0..i] ("proper" means shorter than p[0..i] itself).
     * Example: p = "ACGAC" gives [0, 0, 0, 1, 2]. The last number is the "Border length" line.
     */
    public static int[] prefixFunction(String p) {
        int m = p.length();
        int[] pi = new int[m];
        int k = 0; // length of the current border (of p[0..i-1])
        for (int i = 1; i < m; i++) {
            // the border cannot be extended by p[i]: fall back to the next shorter border
            while (k > 0 && p.charAt(i) != p.charAt(k)) k = pi[k - 1];
            // the border can be extended by one letter
            if (p.charAt(i) == p.charAt(k)) k++;
            pi[i] = k;
        }
        return pi;
    }

    /**
     * Mission 3b. KMP. All start positions (counting from 0, in increasing order) where p occurs in text.
     * Overlapping occurrences count: "ACG" occurs in "ACGACG" at 0 and 3, and "AA" in "AAA" at 0 and 1.
     * Use the prefix function: the text pointer never goes back. The number of positions is "Marker hits",
     * the first one is "First hit" (-1 when there is none).
     */
    public static int[] findAll(String text, String p) {
        int n = text.length(), m = p.length();
        if (m == 0 || m > n) return new int[0];
        int[] pi = prefixFunction(p);
        int[] found = new int[n]; // at most n occurrences
        int count = 0;
        int k = 0; // how many letters of the marker are matched right now
        for (int i = 0; i < n; i++) {       // i only grows: the text pointer never goes back
            while (k > 0 && text.charAt(i) != p.charAt(k)) k = pi[k - 1]; // mismatch: only k goes back
            if (text.charAt(i) == p.charAt(k)) k++;
            if (k == m) {                    // full match ends at i
                found[count++] = i - m + 1;
                k = pi[k - 1];               // not 0: the next match may overlap this one
            }
        }
        return Arrays.copyOf(found, count);
    }

    // letter -> number 1..4 (never 0, so that "A" and "AA" would not look the same)
    private static int code(char c) {
        switch (c) {
            case 'A': return 1;
            case 'C': return 2;
            case 'G': return 3;
            default:  return 4; // 'T'
        }
    }

    /**
     * Mission 4. Rabin-Karp. The number of positions i in a such that the word of k letters starting at i
     * (a[i..i+k-1]) also occurs somewhere in b. Count positions, not different words. This is the "Shared words" line.
     * The idea: give every window of k letters a number (a rolling hash, updated in O(1) when the window moves by one),
     * look the number up among the windows of b, and when it is found COMPARE THE LETTERS, because two different words
     * can have the same hash.
     * Example: a = "ACGTAC", b = "TTACGT", k = 4: the words of a are ACGT, CGTA, GTAC; only ACGT is in b, so the answer is 1.
     */
    public static int sharedWords(String a, String b, int k) {
        return sharedWords(a, b, k, MOD);
    }

    private static final long BASE = 131;
    private static final long MOD = 1_000_000_007L;

    /** Same as above, but with a chosen MOD (a tiny MOD gives many false alarms: the answer must not change). */
    static int sharedWords(String a, String b, int k, long mod) {
        int n = a.length(), m = b.length();
        if (k <= 0 || k > n || k > m) return 0;

        // BASE^(k-1) mod MOD: the weight of the first letter of a window
        long power = 1;
        for (int i = 0; i < k - 1; i++) power = power * BASE % mod;

        // 1) all windows of b: hash -> list of start positions (different words can share a hash)
        Map<Long, List<Integer>> windowsOfB = new HashMap<>();
        long h = 0;
        for (int i = 0; i < k; i++) h = (h * BASE + code(b.charAt(i))) % mod;
        windowsOfB.computeIfAbsent(h, x -> new ArrayList<>()).add(0);
        for (int i = 1; i + k <= m; i++) {
            // roll: remove the old first letter, shift, add the new last letter; + mod keeps the number non-negative
            h = ((h - code(b.charAt(i - 1)) * power % mod + mod) % mod * BASE + code(b.charAt(i + k - 1))) % mod;
            windowsOfB.computeIfAbsent(h, x -> new ArrayList<>()).add(i);
        }

        // 2) all windows of a: look the hash up, and if found COMPARE THE LETTERS
        int count = 0;
        h = 0;
        for (int i = 0; i < k; i++) h = (h * BASE + code(a.charAt(i))) % mod;
        for (int i = 0; ; i++) {
            List<Integer> candidates = windowsOfB.get(h);
            if (candidates != null) {
                for (int start : candidates) {
                    boolean same = true;
                    for (int t = 0; t < k; t++) {
                        if (a.charAt(i + t) != b.charAt(start + t)) { same = false; break; }
                    }
                    if (same) { count++; break; } // this position of a is counted once
                }
            }
            if (i + k >= n) break;
            h = ((h - code(a.charAt(i)) * power % mod + mod) % mod * BASE + code(a.charAt(i + k))) % mod;
        }
        return count;
    }
}
