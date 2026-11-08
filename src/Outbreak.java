import java.util.*;

/**
 * Mission 5. Dynamic programming on the tree of infections. Root the tree in person 0.
 * Input.Tree.neighbours() gives the neighbours of every person. The number of people is at most about 100, so a recursive DFS is fine here.
 */
public class Outbreak {

    /**
     * Choose people to test so that for every link (somebody infected somebody) at least one of the two is tested,
     * and the total cost of testing is as small as possible. cost[v] is the price of testing person v.
     * Returns tested[v] = true if person v is tested.
     *
     * State for every person v (looking only at v and everything below it): the smallest cost when v IS tested, and
     * the smallest cost when v is NOT tested. What must then be true about the children of v in each case?
     * Do not forget to rebuild the answer (who exactly is tested), not only the cost: the picture needs it.
     * Main calls this twice: with all costs equal to 1 (the line "Tests needed") and with the real costs ("Cheapest testing").
     */
    public static boolean[] cheapestTests(int n, int[][] nb, int[] cost) {
        boolean[] tested = new boolean[n];
        if (n == 0) return tested;
        long[] yes = new long[n]; // yes[v] = cheapest cost of the subtree of v when v IS tested
        long[] no = new long[n];  // no[v]  = cheapest cost of the subtree of v when v is NOT tested
        int[] parent = new int[n];
        parent[0] = -1;
        fill(0, -1, nb, cost, yes, no, parent);

        // rebuild: walk down from the root with the same rule
        decide(0, yes[0] <= no[0], nb, parent, yes, no, tested);
        return tested;
    }

    // computes yes[] and no[] for the subtree of v; "from" is the person we came from (the parent)
    private static void fill(int v, int from, int[][] nb, int[] cost, long[] yes, long[] no, int[] parent) {
        yes[v] = cost[v];
        no[v] = 0;
        for (int c : nb[v]) {
            if (c == from) continue; // the link back to the parent is not a child
            parent[c] = v;
            fill(c, v, nb, cost, yes, no, parent);
            yes[v] += Math.min(yes[c], no[c]); // v tested: every child is free to choose
            no[v] += yes[c];                   // v not tested: every child MUST be tested (else the link v-c is uncovered)
        }
    }

    // sets tested[] going down; "isTested" is the decision for v
    private static void decide(int v, boolean isTested, int[][] nb, int[] parent, long[] yes, long[] no, boolean[] tested) {
        tested[v] = isTested;
        for (int c : nb[v]) {
            if (c == parent[v]) continue;
            boolean childTested;
            if (!isTested) childTested = true;        // forced
            else childTested = yes[c] <= no[c];       // the cheaper state of the child
            decide(c, childTested, nb, parent, yes, no, tested);
        }
    }

    /**
     * The number of links on the longest chain of infections, that is the longest path in the tree (counted in links).
     * A path goes up from one person and down to another; for every person you need the two longest ways down.
     * Example: a line 0-1-2-3 has chain 3; a star with centre 0 and leaves 1, 2, 3 has chain 2.
     */
    public static int longestChain(int n, int[][] nb) {
        if (n == 0) return 0;
        int[] best = new int[1];
        down(0, -1, nb, best);
        return best[0];
    }

    // returns the length (in links) of the longest way down from v; updates best[0] with the best path through v
    private static int down(int v, int from, int[][] nb, int[] best) {
        int first = 0, second = 0; // two longest ways down through different children
        for (int c : nb[v]) {
            if (c == from) continue;
            int len = down(c, v, nb, best) + 1; // +1 for the link v-c
            if (len > first) { second = first; first = len; }
            else if (len > second) second = len;
        }
        best[0] = Math.max(best[0], first + second); // the best path with v as its highest point
        return first;
    }
}
