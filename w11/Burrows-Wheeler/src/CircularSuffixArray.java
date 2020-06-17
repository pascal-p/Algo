/* *****************************************************************************
 *  Name: Pascal
 *  Date: June 2020
 *  Description: CircularSuffixArray API
 **************************************************************************** */

import java.util.Arrays;

public class CircularSuffixArray {
    private final CircularSuffix[] circSuffixes;
            // Array of circular suffixes - package visibility?
    private final int n; // number of characters is original string - package visibility?

    /*
     * Implement the CircularSuffixArray.
     * Be sure NOT to create new String objects when you sort the suffixes. That would take quadratic space.

     * A natural approach is to define a nested class CircularSuffix that represents a circular suffix implicitly
     * (via a reference to the input string and a pointer to the first character in the circular suffix).

     * The constructor of CircularSuffix should take constant time and use constant space.
     * You might also consider making CircularSuffix implement the Comparable<CircularSuffix> interface.
     * Note, that while this is, perhaps, the cleanest solution, it is not the fastest.
     *
     *
     */
    private class CircularSuffix implements Comparable<CircularSuffix> {
        final String str;
        final int idx;

        CircularSuffix(String s, int idx) {
            this.str = s; // Just the reference
            this.idx = idx;
        }

        public int compareTo(CircularSuffix that) {
            if (this == that) return 0;  // optimization

            for (int ix = 0; ix < n; ix++) {
                if (charAt(this.str, this.idx + ix) < charAt(that.str, that.idx + ix)) return -1;
                if (charAt(this.str, this.idx + ix) > charAt(that.str, that.idx + ix)) return +1;
            }

            return 0;
        }

        public String str() {
            return this.str;
        }

        // Just for testing the idea
        public String toString() {
            StringBuilder s = new StringBuilder();
            for (int ix = this.idx; ix < this.str.length(); ix++)
                s.append(this.str.charAt(ix));

            for (int ix = 0; ix < this.idx; ix++)
                s.append(this.str.charAt(ix));

            assert s.length() == this.str.length();
            return s.toString();
        }

        private char charAt(String s, int ix) {
            return s.charAt(ix % n);
        }
    }

    // circular suffix array of s
    public CircularSuffixArray(String s) {
        if (s == null)
            throw new IllegalArgumentException("the input string cannot be null");

        this.n = s.length();
        this.circSuffixes = new CircularSuffix[n];

        // build array of n-suffixes
        for (int i = 0; i < n; i++) {
            this.circSuffixes[i] = new CircularSuffix(s, i);
        }

        // sort
        Arrays.sort(this.circSuffixes);
    }

    // length of s
    public int length() {
        return this.n;
    }

    // returns index of ith sorted suffix
    public int index(int ix) {
        if (ix < 0 || ix >= this.n)
            throw new IllegalArgumentException("Out of bounds");

        return this.circSuffixes[ix].idx; // this.index[ix];
    }

    // unit testing (required)
    public static void main(String[] args) {
        String str = "ABRACADABRA!";

        CircularSuffixArray csa = new CircularSuffixArray(str);
        csa.myprint();

        System.out.println("\n=> original string: " + str);

        System.out.println("\n 3rd entry is sorted array: " + csa.index(3));
        System.out.println(" 7th entry is sorted array: " + csa.index(7));
        System.out.println("11th entry is sorted array: " + csa.index(11));
        System.out.println("\nDone...");
    }

    private void myprint() {
        for (int ix = 0; ix < this.n; ix++) {
            System.out.println(this.circSuffixes[ix] + " / " + this.circSuffixes[ix].idx);
        }
    }
}
