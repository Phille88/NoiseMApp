package be.kuleuven.noiseapp.tools;

import java.util.Comparator;
import java.util.List;


/**
 * Implementation of the quicksort algorithm to sort a list of objects based on their distance to a given location.
 * 
 * @author Allard Maarten, Coninx Kristof, De Croock Philippe, Salaets Carl
 * @version 1.1
 */
public class QuickSort<T> {
	Comparator<T> comp;
	
	public QuickSort(Comparator<T> comparator){
		comp = comparator;
	}
	
	
	/**
	 * Method for sorting an elementArray containing DistanceComparable objects, using quick sort.
	 * 
	 * @param elementArray, the arraylist to sort.
	 */
	public void sort(List<? extends T> elementArray) {
		recSort(elementArray, 0, elementArray.size()-1);
	}
	/**
	 * The Conquer-part of the quick insertion sort algorithm.
	 * @param elementArray the arary or subarray to sort.
	 * @param start The startindex.
	 * @param end	The endindex.
	 * @return i The pivot element.
	 */
	private int Partition(List<? extends T> elementArray, int start, int end) {
		T pivot = elementArray.get(start); //choose first element as pivot
		int i = start; //init i
		for(int j=start+1; j <= end; j++){
			if ( comp.compare(elementArray.get(j), pivot) <= 0)
				swapElements(elementArray, ++i, j);  // swap element to '<'-part
		}
		swapElements(elementArray, start, i); // swap pivot in position
		return i; //return pivotidx
	}
	/**
	 * The Conquer-part of the quick insertion sort algorithm.
	 * @param elementArray the arary or subarray to sort.
	 * @param start The startindex.
	 * @param end	The endindex.
	 * @return i The pivot element.
	 */
	private void recSort(List<? extends T> elementArray, int start, int end){
		if (start < end){//check for trivial case of recursion.
			int pivot = Partition(elementArray, start, end); //partition array around pivot
			recSort(elementArray, start, pivot-1);	//recursive call
			recSort(elementArray, pivot + 1, end);  //recursive call
		}
	}
	/**
	 * Method for swapping 2 elements in a arrayList
	 * @param elementArray The list to swap in
	 * @param i The index to swap from
	 * @param j The index to swap to
	 */
	private <T2 extends T> void swapElements(List<T2> elementArray ,int i, int j) {
		T2 temp = elementArray.get(i);
		elementArray.set(i,	elementArray.get(j));
		elementArray.set(j,temp);
	}
}