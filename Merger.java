import java.util.Arrays;
import java.util.concurrent.RecursiveAction;

public class Merger extends RecursiveAction {
    private String[] unsorted, sorted;

    // number of threads
    private static final int MAX_THREADS = 8;
    public Merger(String[] unsorted) {
        this.unsorted = unsorted;
    }

    public void compute() {
        int middle;
        String[] left, right;
        // array is sorted
        if ( unsorted.length <= 1 ) {
            sorted = unsorted;
        } else {
            //
            middle = unsorted.length / 2;
            left = new String[middle];
            right = new String[unsorted.length - middle];
            //split array on two
            System.arraycopy(unsorted, 0, left, 0, middle);
            System.arraycopy(unsorted, middle, right, 0, unsorted.length - middle);
            SimpleMerger leftSort = new SimpleMerger(left);
            SimpleMerger rightSort = new SimpleMerger(right);
            invokeAll(leftSort, rightSort);
            //sort and merge
            sorted = SimpleMerger.merge(leftSort.getSorted(), rightSort.getSorted());
        }
    }
    public String[] getSorted() {
        return sorted;
    }
}
