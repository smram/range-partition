package org.smram.examples;
import java.util.Arrays;

/**
 * Class to range partition T into buckets
 * @author smram
 *
 * @param <T>
 */
public class RangePartition<T extends Comparable<T>> {
	
	enum SearchAlgo { CUSTOM, JAVA, UNIF };
	final private T[] pivots;

	public RangePartition(T[] pivots) {
		this.pivots = pivots;
	}
	
	public int search(T searchKey, SearchAlgo searchAlgo) {
		if (searchAlgo == SearchAlgo.CUSTOM)
			return binarySearch(searchKey);
		else if (searchAlgo == SearchAlgo.JAVA) 
			return javaBinarySearch(searchKey);
		else 
			return unifDistBinarySearch(searchKey);
	}
	
	/**
	 * Does a classic binary search on the pivots
	 * 
	 * @return \in [0,numPivots] - the index of range into which the searchKey 
	 * falls, using right-closed ranges e.g.
	 * (-inf, pivot0], (pivot0, pivot1], (pivot1, pivot2]... (pivotN-1, inf)
	 */
	private int binarySearch(T searchKey) {
		int hi = pivots.length;
		int lo = 0;
		int mid = lo; // only for case when it doesn't enter loop
		
		while(lo < hi) {
			mid = getMidpointIndex(lo, hi); 
			int cmp = searchKey.compareTo(pivots[mid]); // the last one might be a waste
			if (cmp == 0)
				return mid; // searchKey = pivot, we assign to range = pivot indx
			else if (cmp < 0) {
				hi = mid-1; // searchKey < mid
			} else {
				lo = mid+1; // searchKey > mid
			}
		}
		// searchKey not found. so return the range that we must assign to
		int cmp = searchKey.compareTo(pivots[mid]);
		return (cmp < 0) ? mid : mid + 1;
	}
	
	private int getMidpointIndex(int lo, int hi)
	{
		// (int) (lo+hi)/2.0 is recommended written this way to avoid overflow
		return lo + (int) ((hi-lo)/2.0); 
	}
	
	/**
	 * @param searchKey
	 * @return the index of the bucket into which searchKey will fall if buckets
	 * are defined right-closed (except for last bucket which is both sides open)
	 */
	private int javaBinarySearch(T searchKey) {
		int indx = Arrays.binarySearch(pivots, searchKey);
		if (indx > 0)
			return indx; // intervals are right-closed => pivot indx=bucket array indx
		else {// searchKey not found in pivots
			// first part gets insertionIndx into the pivot array; see BinarySearch javadoc
			// plus one converts from pivot array index into bucket array indx
			return -(indx+1) + 1; 
		}
	}
	
	private int unifDistBinarySearch(T searchKey) {
		throw new UnsupportedOperationException();
	}
}