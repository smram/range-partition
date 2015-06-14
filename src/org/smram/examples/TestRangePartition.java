package org.smram.examples;

import java.util.Arrays;
import java.util.Random;

import org.smram.examples.RangePartition.SearchAlgo;
import org.smram.stats.LongSummaryStats;

public class TestRangePartition {
	final private Random rand = new Random();

	public void perfTest(int numPivots, int numRuns)
	{
		final int NUM_SAMPLES_FOR_AVGTIME = 100;

		Integer[] pivots = new Integer[numPivots];
		for (int i=0; i < pivots.length; i++)
			pivots[i] = rand.nextInt();
		Arrays.sort(pivots);
		
		RangePartition<Integer> rp = new RangePartition<Integer>(pivots);
		LongSummaryStats timeStatsCustom = new LongSummaryStats();
		LongSummaryStats timeStatsJava = new LongSummaryStats();

		// test: same pivots, different search algos
		// measure avg time for numRuns runs
		for (int j=0; j < NUM_SAMPLES_FOR_AVGTIME; j++)
		{
			long startBS = System.currentTimeMillis();
			for (int i = 0; i < numRuns; i++)
				rp.search(rand.nextInt(), SearchAlgo.CUSTOM);
			long timeCustomBS = System.currentTimeMillis() - startBS;
			timeStatsCustom.update(timeCustomBS);
			
			startBS = System.currentTimeMillis();
			for (int i = 0; i < numRuns; i++)
				rp.search(rand.nextInt(), SearchAlgo.JAVA);
			long timeJavaBS = System.currentTimeMillis() - startBS;
			timeStatsJava.update(timeJavaBS);
			
			//System.out.println(timeCustomBS + "," + timeJavaBS);
		}
		System.out.println(String.format("Time (ms) for %d searches, Cust BinSearch: %s",
				numRuns, timeStatsCustom));
		System.out.println(String.format("Time (ms) for %d searches, Java BinSearch: %s",
				numRuns, timeStatsJava));
		
		// TODO some 1-sided test here comparing two SummaryStats
		// Compare the two means.. assuming different variances... 
	}
	
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
// more for customBinSearch than javaBinSearch -- is it hot spot optimizing?
// reproduce by running with numPivots = 1000, numRuns=10000, NUM_SAMPLES_FOR_AVGTIME=100
