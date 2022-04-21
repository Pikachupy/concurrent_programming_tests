import java.util.Arrays;
import java.util.concurrent.RecursiveAction;

public class SimpleMerger extends RecursiveAction {

    private String[] unsorted, sorted;

    public SimpleMerger( String[] unsorted ) {
        this.unsorted = unsorted;
    }

    // split array on two part to sort
    public void compute() {
        int middle;
        String[] left, right;

        if ( unsorted.length <= 1 ) {
            sorted = unsorted;
        } else {
            middle = unsorted.length / 2;
            left = new String[middle];
            // array length may be odd
            right = new String[unsorted.length - middle];
            System.arraycopy( unsorted, 0, left, 0, middle );
            System.arraycopy( unsorted, middle, right, 0, unsorted.length - middle );
            // array sort
            Arrays.sort(left);
            Arrays.sort(right);
            sorted = merge(left, right);
        }
    }
    // function to merge two arrays in one
    public static String[] merge( String[] leftPart, String[] rightPart ) {
        int cursorLeft = 0, cursorRight = 0, counter = 0;
        String[] merged = new String[leftPart.length + rightPart.length];
        while ( cursorLeft < leftPart.length && cursorRight < rightPart.length ) {
            if (leftPart[cursorLeft].compareTo(rightPart[cursorRight] ) < 0 ) {
                merged[counter] = leftPart[cursorLeft];
                cursorLeft++;
            } else {
                merged[counter] = rightPart[cursorRight];
                cursorRight++;
            }
            counter++;
        }
        if ( cursorLeft < leftPart.length ) {
            System.arraycopy( leftPart, cursorLeft, merged, counter, merged.length - counter );
        }
        if ( cursorRight < rightPart.length ) {
            System.arraycopy( rightPart, cursorRight, merged, counter, merged.length - counter );
        }
        return merged;
    }
    public String[] getSorted() {
        return sorted;
    }
}
