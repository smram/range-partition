package org.smram.stats;

/**
 * Running Statistics for long objects.
 * 
 * This class is not thread-safe because the update() method is not synchronized
 * 
 * This is my test implementation; normally Apache Commons Math has one I'd use
 * @author smram
 */
public class LongSummaryStats {
	//public final static double FP_COMPARE_EPSILON = 1e-6;
	
	private long num;
	private double mean;
	private double moment2;
	private long min;
	private long max;
	
	public LongSummaryStats() {
		clear();
	}
	
	public void update(long val)
	{
		num++;
		
		double oldMean = mean;
		// I could compute mean after moment2 and avoid caching oldMean, but
		// this code is clearer to me...
		// Also: Apache Math initializes mean specially (mean=x_1) for first
		// data point... mathematically with oldMean=0 and num=1 its the same.
		// but perhaps Apache Math avoids loss of precision due to div by 1.0?
		// TODO test it
		
		// computing mean by maintaining sum: sum may overflow. Do this instead
		mean += (val - oldMean)/((double)num);
		// John Cook's blog uses mean_now and mean_{now-1}. See Apache Math code
		// for equivalent formula using mean_{now-1} which I found simpler
		moment2 += (val - oldMean)*(val - oldMean)*(num-1)/(double)num;
		
		if (val < min) 
			min = val;
		if (val > max)
			max = val;
	}
	
	public long getNumPoints() { 
		return num;
	}
	
	public double getMean() {
		return mean;
	}
	
	/**
	 * References: 
	 * http://www.johndcook.com/blog/2008/09/26/comparing-three-methods-of-computing-standard-deviation/
	 * 
	 * Apache Commons Math uses this implementation too, see source of 
	 * org/apache/commons/math3/stat/descriptive/moment/SecondMoment.html
	 * org/apache/commons/math3/stat/descriptive/moment/Variance.html
	 * org/apache/commons/math3/stat/descriptive/SummaryStatistics.html
	 * 
	 * @return bias-corrected variance
	 */
	public double getVar() {
		return moment2/((double)(getNumPoints()-1));
		
		// this obvious impl can have severe loss of precision when data points
		// are large and their variance is small
		// See http://www.johndcook.com/blog/standard_deviation/
		//return (sum*sum - sumSq)/((double)(num - 1));
	}
	
	public long getMin() {
		return min;
	}
	
	public long getMax() {
		return max;
	}
	
	/**
	 * Resets internal state to start state
	 */
	public void clear() {
		num = 0;
		mean = 0;
		moment2 = 0;
		max = Long.MIN_VALUE;
		min = Long.MAX_VALUE;	
	}
	
	@Override
	public String toString()
	{
		return String.format("num=%d, avg=%f, sd=%f, min=%d, max=%d",
				getNumPoints(), getMean(), Math.sqrt(getVar()), getMin(), getMax());
	}
}