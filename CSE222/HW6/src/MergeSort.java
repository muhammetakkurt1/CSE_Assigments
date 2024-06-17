public class MergeSort extends SortAlgorithm {
	
	public MergeSort(int input_array[]) {
		super(input_array);
	}
	
	private void merge(int left, int mid, int right){
        int firstHalf = mid - left + 1;     /*Number of elements in the first half*/
        int secondHalf = right - mid;       /*Number of elements in the second half*/
        int L[] = new int[firstHalf];       /*Temporary array for the first half*/
        int R[] = new int[secondHalf];      /*Temporary array for the second half*/

        for (int i = 0; i < firstHalf; i++) {       /*Copy data to temporary arrays L[] and R[]*/
            L[i] = arr[left + i];
        }
        for (int j = 0; j < secondHalf; j++) {
            R[j] = arr[mid + 1 + j];
        }

        int i = 0, j = 0;                       /*Initial indexes of first and second subarrays*/
        int k = left;                           /*Initial index of merged subarray*/
        while (i < firstHalf && j < secondHalf) {       /*Merge the temp arrays back into the original array*/
            comparison_counter++;                       /*Increment the comparison counter*/
            if (L[i] <= R[j]) {             /*Compare elements from both subarrays and place the smaller element into the main array*/
                arr[k] = L[i];              /*If the current element in L is smaller or equal, add it to the main array*/
                i++;                        /*Move the index of the L array forward*/
            } else {
                arr[k] = R[j];              /*If the current element in R is smaller, add it to the main array*/
                j++;                        /*Move the index of the R array forward*/
            }
            k++;                            /*Move forward in the main array's current position for the next insertion*/
        }

        while (i < firstHalf) {             /*Copy the remaining elements of L[], if any*/
            arr[k] = L[i];
            i++;
            k++;
        }

        while (j < secondHalf) {            /*Copy the remaining elements of R[], if any*/
            arr[k] = R[j];
            j++;
            k++;
        }
    }

    private void sort(int left, int right){
        if (left < right) {                 /*Base case: Check if the current segment of the array contains more than one element*/
            int mid = (left + right) / 2;   /*Find the middle point*/
            sort(left, mid);                /*Sort first halves recursively*/
            sort(mid + 1, right);      /*Sort second halves recursively*/
            merge(left, mid, right);        /*Merge the sorted halves*/
        }
    }
    
    @Override
    public void sort() {
        sort(0, arr.length - 1);    /*Calls the private sort method to sort the entire array*/
    }
    
    @Override
    public void print() {
    	System.out.print("Merge Sort\t=>\t");
    	super.print();
    }
}
