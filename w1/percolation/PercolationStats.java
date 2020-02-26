/* *****************************************************************************
 *  Name:
 *  Date:
 *  Description:
 **************************************************************************** */

import edu.princeton.cs.algs4.StdRandom;
import edu.princeton.cs.algs4.StdStats;

public class PercolationStats {
    private static final double CI_95 = 1.96; // value for 95% confidence interval
    private final int trials;
    private final double[] statOpenSites;
    private double mu = Double.NaN, stdev = Double.NaN;

    // perform independent trials on an n-by-n grid
    public PercolationStats(int n, int trials) {
        if (n <= 0) throw new IllegalArgumentException("n (size) must be >= 1");
        if (trials <= 0) throw new IllegalArgumentException("trials must be >= 1");

        this.trials = trials;
        this.statOpenSites = new double[trials];

        Percolation perco = null;

        // initiate experiment
        for (int ix = 0; ix < this.trials; ix++) {
            perco = new Percolation(n);

            while (!perco.percolates()) {
                int jx = StdRandom.uniform(1, n + 1);
                int kx = StdRandom.uniform(1, n + 1);
                perco.open(jx, kx);
            }

            // get number of open sites
            this.statOpenSites[ix] = 1.0 * perco.numberOfOpenSites() / (n * n);
            perco = null;
        }
    }

    // test client (see below)
    public static void main(String[] args) {
        // instanciate the stat perco with grid size and number of trials
        int n = Integer.parseInt(args[0]);
        int trials = Integer.parseInt(args[1]);

        PercolationStats percoStat = new PercolationStats(n, trials);

        System.out.println("mean                    = " + percoStat.mean());
        System.out.println("stddev                  = " + percoStat.stddev());
        System.out.println(
                "95% confidence interval = [" + percoStat.confidenceLo() + ", " + percoStat
                        .confidenceHi() + "]");

    }

    // sample mean of percolation threshold
    public double mean() {
        this.mu = StdStats.mean(this.statOpenSites);
        return this.mu;
    }

    // sample standard deviation of percolation threshold
    public double stddev() {
        this.stdev = StdStats.stddev(this.statOpenSites);
        return this.stdev;
    }

    // low endpoint of 95% confidence interval
    public double confidenceLo() {
        checkPreq();
        return this.mu - CI_95 * (this.stdev / Math.sqrt(this.trials));
    }

    // high endpoint of 95% confidence interval
    public double confidenceHi() {
        checkPreq();
        return this.mu + CI_95 * (this.stdev / Math.sqrt(this.trials));
    }

    private void checkPreq() {
        if (Double.isNaN(this.mu)) this.mean();
        if (Double.isNaN(this.stdev)) this.stddev();
    }
}
