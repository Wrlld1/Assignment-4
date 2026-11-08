/**
 * Missions 1 and 2. Compare two strains with dynamic programming.
 *
 * Both are the same kind of table: d[i][j] answers a question about the first i letters of a and the first j letters of b.
 * Think about three things before you type: what d[i][j] means, how it follows from smaller cells, and what the first row and column are.
 */
public class Strains {

    /**
     * Mission 1. The smallest number of single-letter changes that turn a into b. One change is: replace a letter,
     * insert a letter, or delete a letter. This number is the "Mutations" line.
     * Example: "ACGT" -> "AGT" is 1 (delete C). "GATTACA" -> "GCATGCA" needs 3.
     */
    public static int editDistance(String a, String b) {
        int n = a.length(), m = b.length();
        // d[i][j] = the smallest number of changes that turn the first i letters of a into the first j letters of b.
        // The table has one more row and one more column than the strings are long (row 0 / column 0 = empty prefix).
        int[][] d = new int[n + 1][m + 1];

        // Base cases: empty prefix of a -> first j letters of b needs j insertions;
        // first i letters of a -> empty word needs i deletions.
        for (int i = 0; i <= n; i++) d[i][0] = i;
        for (int j = 0; j <= m; j++) d[0][j] = j;

        for (int i = 1; i <= n; i++) {
            for (int j = 1; j <= m; j++) {
                // equal letters: no change (cost 0); different letters: one replacement (cost 1)
                int diag = d[i - 1][j - 1] + (a.charAt(i - 1) == b.charAt(j - 1) ? 0 : 1);
                int del = d[i - 1][j] + 1;   // delete the letter a[i-1]
                int ins = d[i][j - 1] + 1;   // insert the letter b[j-1]
                d[i][j] = Math.min(diag, Math.min(del, ins));
            }
        }
        return d[n][m];
    }

    /**
     * Mission 2. The length of the longest common subsequence: letters that appear in both strains in the same order,
     * but not necessarily next to each other. This is the "Shared core" line.
     * Example: "ACGTT" and "AGTC": the longest is "AGT", length 3.
     */
    public static int lcsLength(String a, String b) {
        int n = a.length(), m = b.length();
        // l[i][j] = the length of the longest common subsequence of the first i letters of a and the first j letters of b.
        // Row 0 and column 0 stay 0: an empty word has nothing in common with anything.
        int[][] l = new int[n + 1][m + 1];

        for (int i = 1; i <= n; i++) {
            for (int j = 1; j <= m; j++) {
                if (a.charAt(i - 1) == b.charAt(j - 1)) {
                    l[i][j] = l[i - 1][j - 1] + 1;                 // equal letters: extend the diagonal
                } else {
                    l[i][j] = Math.max(l[i - 1][j], l[i][j - 1]);  // otherwise: the better of top and left
                }
            }
        }
        return l[n][m];
    }
}
