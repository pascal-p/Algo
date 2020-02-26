/* *****************************************************************************
 *  Name:
 *  Date:
 *  Description:
 **************************************************************************** */

import edu.princeton.cs.algs4.In;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

// import org.jetbrains.annotations.NotNull;

public class TestPercolation {

    // public static void main(@NotNull String[] args) {
    public static void main(String[] args) {
        In in = new In(args[0]);      // input file
        int n = in.readInt();         // n-by-n percolation system

        String pattern = "input(\\d+)\\-no\\.txt";
        Pattern r = Pattern.compile(pattern);

        // repeatedly read in sites to open and draw resulting system
        Percolation perc = new Percolation(n);

        while (!in.isEmpty()) {
            int i = in.readInt();
            int j = in.readInt();
            perc.open(i, j);
        }

        // if file is no
        Matcher m = r.matcher(args[0]);
        if (m.find() || args[0].compareTo("res/greeting57.txt") == 0 ||
                args[0].compareTo("res/heart25.txt") == 0) {
            assert !perc.percolates() : "should NOT percolate, but it does with: " + args[0];
        }
        else {
            assert perc.percolates() : "should percolate, but it does NOT with: " + args[0];
        }
    }
}
