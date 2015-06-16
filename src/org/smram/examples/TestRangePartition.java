package org.smram.examples;

import java.util.Arrays;
import java.util.Random;

import org.smram.stats.LongSummaryStats;

/**
 * Performance tests for {@link RangePartition} with integer keys
 * @author smram
 *
 */
public class TestRangePartition {
	final private Random rand = new Random();

	static class MoreMetrics<T extends Comparable<T>>
	{
		RangePartition<T> rp;
		LongSummaryStats timeStats = new LongSummaryStats();
		LongSummaryStats numCmpStats = new LongSummaryStats();
		
		MoreMetrics(RangePartition<T> rp)
		{
			this.rp = rp;
		}
	}
	
	public void perfTest(int numPivots, int numRuns)
	{
		final int NUM_SAMPLES_FOR_AVGTIME = 100;

		Integer[] pivots = new Integer[numPivots];
		for (int i=0; i < pivots.length; i++)
			pivots[i] = rand.nextInt();
		Arrays.sort(pivots);

		// test: same pivots, different search algos
		// measure average time for numRuns runs
		MoreMetrics<Integer> bsp = new MoreMetrics<Integer>(
				new RangePartition<Integer>(pivots));
		MoreMetrics<Integer> jsp = new MoreMetrics<Integer>(
				new JavaRangePartition<Integer>(pivots));
		MoreMetrics<Integer> isp = new MoreMetrics<Integer>(
				new InterpolationRangePartition(pivots));
		
		for (int j=0; j < NUM_SAMPLES_FOR_AVGTIME; j++)
		{
			runSearch(bsp, numRuns);
			
			runSearch(jsp, numRuns);
			
			runSearch(isp, numRuns);
		}
		
		System.out.println(String.format("Time (ms) for %d searches, Cust BinSearch: %s",
				numRuns, bsp.timeStats));
		System.out.println(String.format("Time (ms) for %d searches, Java BinSearch: %s",
				numRuns, jsp.timeStats));
		System.out.println(String.format("Time (ms) for %d searches, Interpol Search: %s",
				numRuns, isp.timeStats));
		
		System.out.println(String.format("#Cmp (ms) for %d searches, Cust BinSearch: %s",
				numRuns, bsp.numCmpStats));
		System.out.println(String.format("#Cmp (ms) for %d searches, Interpol Search: %s",
				numRuns, isp.numCmpStats));
		
		// TODO some 1-sided test comparing SummaryStats means 
		// Compare the means.. assume different variances... 
	}
	
	private MoreMetrics<Integer> runSearch(MoreMetrics<Integer> met, int numRuns)
	{
		long startBS = System.currentTimeMillis();
		for (int i = 0; i < numRuns; i++)
			met.rp.search(rand.nextInt());
		long time = System.currentTimeMillis() - startBS;
		
		met.timeStats.update(time);
		met.numCmpStats.update(met.rp.getNumCompares());
		
		return met;
	}

	///////////////////////////////////////////////////////////////////////////
	public static void main(String[] args) {
		if (args.length < 2)
			throw new IllegalArgumentException("Too few arguments. Usage: TestRangePartition <numPivots> <numRuns>");
		
		int numPivots = Integer.parseInt(args[0]);
		int numRuns = Integer.parseInt(args[1]);
		
		TestRangePartition test = new TestRangePartition();
		test.perfTest(numPivots, numRuns);
		// TODO unit tests
	}
}

// Result: my observation is the the first few numRuns samples take much longer
//         more for customBinSearch than javaBinSearch -- is it hot spot optimizing?
//         reproduce by running with numPivots = 1000, numRuns=10000, NUM_SAMPLES_FOR_AVGTIME=100
// Result: Interpolation search does expected fewer comparisons than binary search: O(log(log(n))
//         However, it isn't always faster in elapsed time, needs optimization.
// Example result:
//Time (ms) for 100 searches, Cust BinSearch: num=100, avg=0.240000, sd=1.146977, min=0, max=11
//Time (ms) for 100 searches, Java BinSearch: num=100, avg=0.230000, sd=1.369620, min=0, max=13
//Time (ms) for 100 searches, Interpol Search: num=100, avg=0.150000, sd=0.500000, min=0, max=4
//#Cmp (ms) for 100 searches, Cust BinSearch: num=100, avg=4.000000, sd=0.000000, min=4, max=4
//#Cmp (ms) for 100 searches, Interpol Search: num=100, avg=2.390000, sd=0.665074, min=2, max=4
