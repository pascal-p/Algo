/* *****************************************************************************
 *  Name: Pascal
 *  Date: June 2020
 *  Description: CircularSuffixArray API
 **************************************************************************** */

public class CircularSuffixArray {
    private static final int CUTOFF = 0;
    private final int[] circSuffixes;
    private final int n; // number of characters is original string
    private final String str;

    /*
     * Implement the CircularSuffixArray.
     * Be sure NOT to create new String objects when you sort the suffixes. That would take quadratic space.
     *
     * The constructor of CircularSuffix should take constant time and use constant space.
     * You might also consider making CircularSuffix implement the Comparable<CircularSuffix> interface.
     * Note, that while this is, perhaps, the cleanest solution, it is not the fastest.
     *
     *
     */

    private class Quick3StrSort {
        private static final int CUTOFF = 15;
        private final int[] ary;

        public Quick3StrSort(int[] ary) {
            this.ary = ary;
        }

        public void sort() {
            sort(0, n - 1, 0);
        }

        // return the d-th character of s, -1 if d = length of s
        private int charAt(int ix, int d) {
            assert d >= 0 && d <= n;
            if (d == str.length()) return -1;
            return str.charAt((ix + d) % n);
        }

        private void sort(int lo, int hi, int d) {
            // cutoff to insertion sort for small subarrays
            if (hi <= lo + CUTOFF) {
                insertion(lo, hi, d);
                return;
            }

            int lt = lo, gt = hi;
            int v = charAt(this.ary[lo], d);
            int i = lo + 1;
            while (i <= gt) {
                int t = charAt(this.ary[i], d);
                if (t < v) swap(lt++, i++);
                else if (t > v) swap(i, gt--);
                else i++;
            }

            // a[lo..lt-1] < v = a[lt..gt] < a[gt+1..hi].
            sort(lo, lt - 1, d);
            if (v >= 0) sort(lt, gt, d + 1);
            sort(gt + 1, hi, d);
        }

        private void insertion(int lo, int hi, int d) {
            for (int i = lo; i <= hi; i++)
                for (int j = i; j > lo && less(j, j - 1, d); j--)
                    swap(j, j - 1);
        }

        private boolean less(int v, int w, int d) {
            for (int ix = d; ix < n; ix++) {
                if (charAt(this.ary[v], ix) < charAt(this.ary[w], ix))
                    return true;
                if (charAt(this.ary[v], ix) > charAt(this.ary[w], ix))
                    return false;
            }
            return false; // v.length() < w.length();
        }

        private void swap(int i, int j) {
            int tmp = this.ary[i];
            this.ary[i] = this.ary[j];
            this.ary[j] = tmp;
        }
    }

    // circular suffix array of s
    public CircularSuffixArray(String s) {
        if (s == null)
            throw new IllegalArgumentException("the input string cannot be null");

        this.str = s;
        this.n = s.length();
        this.circSuffixes = new int[n];

        // build array of n-suffixes
        for (int i = 0; i < n; i++) {
            this.circSuffixes[i] = i;
        }
        Quick3StrSort qst = new Quick3StrSort(this.circSuffixes);
        qst.sort();
    }

    // length of s
    public int length() {
        return this.n;
    }

    // returns index of ith sorted suffix
    public int index(int ix) {
        if (ix < 0 || ix >= this.n)
            throw new IllegalArgumentException("Out of bounds");
        return this.circSuffixes[ix];
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

    private String makeStr(int jx) {
        StringBuilder s = new StringBuilder();
        for (int ix = jx; ix < str.length(); ix++)
            s.append(str.charAt(ix));

        for (int ix = 0; ix < jx; ix++)
            s.append(str.charAt(ix));

        assert s.length() == str.length();
        return s.toString();
    }

    private void myprint() {
        for (int ix = 0; ix < this.n; ix++) {
            System.out.println(makeStr(this.circSuffixes[ix]) + " / " + this.circSuffixes[ix]);
        }
    }
}
