# range-partition
Range partitioning concepts

This repo tests different array-search implementations for range partitioning. 

##Problem Statement
Range partition elements into B+1 intervals, given B pivots or boundary elements that define right-open intervals:
>[-max, pivot_1), [pivot_1, pivot_2), ..., [pivot_{b}, +max)

Picking good pivots e.g. for defining equal-sized intervals is a separate problem, and not addressed here. 

##Solutions
* Binary search is the most general approach - it only requires that elements be comparable and is hard to beat in performance if nothing is known about the distribution of elements and size of intervals.
* Interpolation search will be faster if the element distribution is uniform, and numeric operations like min,max,diff are defined.

###Code description and caveats
* Primary goal is to count #comparisons made by different search algorithm in practice. The code may simple, almost practice/scratch code. For instance, binary search is implemented from scratch and interpolation search is restricted to integers. 
* Number of comparisons and elapsed time) are averaged across several runs. 
* There is a utility class that maintains running statistics on these metrics.

TODO 
* Test performance of interpolation seach for non-uniform distributions
