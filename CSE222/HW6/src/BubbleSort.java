public class BubbleSort extends SortAlgorithm {

	public BubbleSort(int input_array[]) {
		super(input_array);
	}
	
    @Override
    public void sort() {
        int len = arr.length;       /*Get the length of the array*/
        boolean isSwapped;          /*This flag will check if a swap occurred in the inner loop*/
        for (int i = 0; i < len - 1; i++) {
            isSwapped = false;                      /*Initialize swapped to false on each new pass*/
            for (int j = 0; j < len - i - 1; j++) { /*Inner loop for comparing array elements*/
                comparison_counter++;               /*Increment the comparison counter*/
                if (arr[j] > arr[j + 1]) {          /*Compare adjacent elements and swap if necessary*/
                    swap(j, j + 1);                 /*Call the swap method if elements are in wrong order*/
                    isSwapped = true;               /*Set swapped to true if a swap happened*/
                }
            }
            if (!isSwapped) break;      /*If no elements were swapped, the array is sorted*/
        }
    }
    
    @Override
    public void print() {
    	System.out.print("Bubble Sort\t=>\t");
    	super.print();
    }
}
