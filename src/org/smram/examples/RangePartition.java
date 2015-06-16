package org.smram.examples;
import java.util.Arrays;

/**
 * Class to range partition T into buckets
 * @author smram
 *
 * @param <T>
 */
public class RangePartition<T extends Comparable<T>> {

	//enum SearchAlgo { CUSTOM, JAVA, INTERPOLATION };
	final protected T[] pivots;
	protected int numCompares;

	public RangePartition(T[] pivots) {
		this.pivots = pivots;
		numCompares = 0;
	}

	public int getNumCompares()
	{
		return numCompares;
	}
	
	/**
	 * Does a classic binary search on the pivots. 
	 * See {@link JavaRangePartition} for an impl that uses Java's built-in 
	 * binary search
	 * 
	 * @return \in [0,numPivots] - the index of range into which the searchKey 
	 * falls, using right-closed ranges e.g.
	 * (-inf, pivot0], (pivot0, pivot1], (pivot1, pivot2]... (pivotN-1, inf)
	 */
	int search(T searchKey) {
		resetNumCompares();

		int hi = pivots.length;
		int lo = 0;
		int mid = lo; // only for case when it doesn't enter loop

		while(lo < hi) {
			mid = getMidpointIndex(lo, hi); 
			int cmp = doCompare(searchKey, pivots[mid]); // the last one might be a waste
			if (cmp == 0)
				return mid; // searchKey = pivot, we assign to range = pivot indx
			else if (cmp < 0) {
				hi = mid-1; // searchKey < mid
			} else {
				lo = mid+1; // searchKey > mid
			}
		}
		// searchKey not found. so return the range that we must assign to
		int cmp = doCompare(searchKey, pivots[mid]);
		return (cmp < 0) ? mid : mid + 1;
	}

	private int doCompare(T p1, T p2) {
		numCompares++;
		return p1.compareTo(p2);
	}

	protected void resetNumCompares() {
		numCompares = 0;
	}

	private int getMidpointIndex(int lo, int hi) {
		// written this way instead of lo+hi/2 to avoid overflow
		return lo + (int) ((hi-lo)/2.0); 
	}
}

class JavaRangePartition<T extends Comparable<T>> extends RangePartition<T> {
	public JavaRangePartition(T[] pivots) {
		super(pivots);
	}

	/**
	 * @param searchKey
	 * @return the index of the bucket into which searchKey will fall if buckets
	 * are defined right-closed (except for last bucket which is both sides open)
	 */
	@Override
	int search(T searchKey) {
		int indx = Arrays.binarySearch(pivots, searchKey);
		if (indx > 0)
			return indx; // intervals are right-closed => pivot indx=bucket array indx
		else {// searchKey not found in pivots
			// first part gets insertionIndx into pivot[]; see BinarySearch javadoc
			// +1 converts from pivot array index into bucket array indx
			return -(indx+1) + 1; 
		}
	}
}

/**
 * Interpolation search - fewer comparisons than binary search when search 
 * space is uniform random distributed. This class is a reference implementation
 * for integer keys
 * 
 * @author smram
 */
// Given a choice, I wouldn't write this search code from scratch, its tricky. 
// This is practice.
// Here I try to recreate the search part from scratch from here:
// http://data.linkedin.com/blog/2010/06/beating-binary-search
class InterpolationRangePartition extends RangePartition<Integer> {
	public InterpolationRangePartition(Integer[] pivots) {
		super(pivots);
	}

	/**
	 * To do interpolation search you need values to be numerical with a notion
	 * of max, min, and difference. Not just notion of ranks. 
	 * @param searchKey
	 * @return index of the range that searchKey falls into
	 */
	@Override
	int search(Integer searchKey) {
		resetNumCompares();
		
		int lo = 0;
		int hi = pivots.length - 1;
		int min = Integer.MIN_VALUE;
		int max = Integer.MAX_VALUE;
		int splitter = lo;
		
		// if lo overshoots high... break
		// if min or max overshoot/undershoot searchKey... break
		while (lo < hi && searchKey >= min && searchKey <= max) {
			long valueRange = (long)max-min; // needs to be long else overflow
			final double interpolatedPct = 
					((long)searchKey - min)/((double)valueRange);
			final int loOffset = (int) ((hi - lo) * interpolatedPct);
			splitter = lo + loOffset;
			
			numCompares++;
			if (searchKey == pivots[splitter])
				return splitter;
			// actually, can splitter ever = pivots.length without lo=hi??
			else if (splitter == 0 || splitter == pivots.length-1) 
				break; // searched all pivots and not found
			else if (searchKey < pivots[splitter]) {		
				hi = splitter - 1;
				max = pivots[hi];
			} else {
				lo = splitter + 1;
				min = pivots[lo];
			}
		}
		
		// if we are here, lo >= hi, split point = lo
		// TODO I think comparing to splitter works for all 3 stop conditions
		numCompares++;
		return (searchKey < pivots[splitter]) ? splitter : splitter+1;
	}
}
