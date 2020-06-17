/* *****************************************************************************
 *  Name: Pascal
 *  Date: June 2020
 *  Description: BW API for transform() and inverse-transfrom
 **************************************************************************** */

import edu.princeton.cs.algs4.BinaryStdIn;
import edu.princeton.cs.algs4.BinaryStdOut;

public class BurrowsWheeler {
    // apply Burrows-Wheeler transform,
    // reading from standard input and writing to standard output
    public static void transform() {
        String str = BinaryStdIn.readString();
        int n = str.length();
        CircularSuffixArray csa = new CircularSuffixArray(str);

        //  1 - lookup for (original) str in csa ie look for csa.index[ix] == 0 => first = ix
        int first = -1;
        for (int ix = 0; ix < str.length(); ix++) {
            if (csa.index(ix) == 0) {
                first = ix;
                break;
            }
        }
        assert first >= 0 && first < n;

        // 2 - build the string from last column of csa (aka t[])
        char[] chary = new char[n];
        for (int ix = 0; ix < n; ix++) {
            int jx = csa.index(ix);
            assert jx >= 0 && jx < n;
            // find index of last character in original String
            //            => last index,  previous index
            int kx = (jx == 0) ? n - 1 : jx - 1;
            chary[ix] = str.charAt(kx);
        }
        BinaryStdOut.write(first);
        BinaryStdOut.write(new String(chary));
        BinaryStdOut.flush();
    }

    // apply Burrows-Wheeler inverse transform,
    // reading from standard input and writing to standard output
    public static void inverseTransform() {
        //
        // Can I assume that inverseTransform() receives only valid inputs
        // (e.g., that correspond to the output of transform())? Yes.
        //
        int first = BinaryStdIn.readInt();
        String str = BinaryStdIn.readString();
        int n = str.length();

        // Part 1 - Build next
        char[] t = new char[n];
        char[] sortedT = new char[n];
        int[] next = buildTNext(t, sortedT, str, n);

        // Part 2 - Using next[], t[] and first
        int ix = first, cnt = 0;
        char[] chary = new char[n];
        while (cnt < n) {
            chary[cnt++] = sortedT[ix];
            ix = next[ix];
        }
        assert cnt == n;
        BinaryStdOut.write(new String(chary)); // output on std output
        BinaryStdOut.flush();
    }

    // if args[0] is "-", apply Burrows-Wheeler transform
    // if args[0] is "+", apply Burrows-Wheeler inverse transform
    public static void main(String[] args) {
        if (args[0].equals("-")) transform();
        else if (args[0].equals("+")) inverseTransform();
        else
            throw new IllegalArgumentException(
                    "Option not valid: only + for inverse transform or - for transform");

    }

    private static int[] buildTNext(char[] t, char[] sortedT, String str, int n) {
        for (int ix = 0; ix < n; ix++) {
            char ch = str.charAt(ix);
            sortedT[ix] = ch;
            t[ix] = ch;
        }
        int[] next = sort(t, sortedT);
        return next;
    }

    private static int[] sort(char[] t, char[] sortedT) { // LSD adapted
        int n = t.length;
        int R = 256;
        int[] count = new int[R + 1];
        int[] next = new int[n];

        // Compute frequency counts.
        for (int ix = 0; ix < n; ix++)
            count[t[ix] + 1]++;

        for (int r = 0; r < R; r++)
            count[r + 1] += count[r];

        // Transform counts to indices, build next
        for (int ix = 0; ix < n; ix++) { // Distribute.
            int jx = count[t[ix]]++;
            sortedT[jx] = t[ix];
            next[jx] = ix;
        }

        return next;
    }
}
