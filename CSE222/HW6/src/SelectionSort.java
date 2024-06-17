public class SelectionSort extends SortAlgorithm {

	public SelectionSort(int input_array[]) {
		super(input_array);
	}

    @Override
    public void sort() {
        int len = arr.length;                 /*Get the length of the array*/
        for (int i = 0; i < len - 1; i++) {     /*One by one move boundary of unsorted subarray*/
            int minIndex = i;                    /*Find the minimum element in unsorted array*/
            for (int j = i + 1; j < len; j++) {
                comparison_counter++;           /*Increment the comparison counter*/
                if (arr[j] < arr[minIndex]) {   /*If the found element is smaller than the currently assumed minimum*/
                    minIndex = j;               /*Update the index of minimum element*/
                }
            }
            swap(minIndex, i);                  /*Swap the found minimum element with the first element*/
        }
    }

    @Override
    public void print() {
    	System.out.print("Selection Sort\t=>\t");
    	super.print();
    }
}
