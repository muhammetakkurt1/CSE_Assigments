public class QuickSort extends SortAlgorithm {

	public QuickSort(int input_array[]) {
		super(input_array);
	}
	
    private int partition(int low, int high){
        int pivot = arr[high];              /*Choosing the last element as the pivot*/
        int i = (low - 1);                  /* Index of smaller element and indicates the right position of pivot found so far*/
        for (int j = low; j < high; j++) {      /*Traverse through all elements*/
            comparison_counter++;               /*Increment the comparison counter*/
            if (arr[j] <= pivot) {          /*If current element is smaller than or equal to pivot*/
                i++;                        /*Increment index of smaller element*/
                swap(i, j);                 /*Swap the elements*/
            }
        }
        swap(i + 1, high);                  /*Swap the pivot element with the element at (i + 1)*/
        return i + 1;                       /*Return the partitioning index*/
    }

    private void sort(int low, int high){
        if (low < high) {                   /*Base case: If the current segment of the array is not reduced to one element*/
            int PartIndex = partition(low, high);   /*Call the partition method to find the correct position of the pivot element*/
            sort(low, PartIndex - 1);       /*Recursively apply quick sort to the left part of the array (elements before the pivot)*/
            sort(PartIndex + 1, high);          /*Recursively apply quick sort to the right part of the array (elements after the pivot)*/
        }
    }

    @Override
    public void sort() {
        sort(0, arr.length - 1);        /*Initializes the recursive sorting process for the entire array*/
    }

    @Override
    public void print() {
    	System.out.print("Quick Sort\t=>\t");
    	super.print();
    }
}
