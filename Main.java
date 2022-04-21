import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.Future;

public class Main {

    public static final int processorCount = 8;

    public static void main(String[] args) throws Exception {
        int arrSize = 1_0;
        String[] unsorted = new String[arrSize];

        Random randomizer = new Random();

        for ( int i = 0; i < arrSize; i++ ) {
            unsorted[i] = Integer.toString(randomizer.nextInt( 10_00000 ));
            if (i < 20) System.out.println(unsorted[i]);
        }

        List<Future> futures = new ArrayList<>();
        int batchSize = arrSize/processorCount;
//        long startTime = System.currentTimeMillis();

        ForkJoinPool forkJoinPool = new ForkJoinPool(processorCount);
        ArrayList<Merger> mergers = new ArrayList<>();
        for (int i = 0; i < processorCount; i++) {
            String[] part = new String[batchSize];


            System.arraycopy( unsorted, i*batchSize, part, 0, batchSize );
            // create merger
            Merger merger = new Merger(part);

            futures.add(forkJoinPool.submit(merger));
            //add merger to list to get result in future
            mergers.add(merger);
        }
        for (Future<Double> future : futures) {
            future.get();
        }

        int j = 0;
        // array to get result
        String[] mergered = new String[arrSize];
        // sequential merge of all part of array
        for (Merger merger:mergers){
            if (j == 0) {
                mergered = merger.getSorted();
                j+=1;
            }
            else{
                String[] part = merger.getSorted();
                mergered = SimpleMerger.merge( mergered, part);
            }
        }

        for (String s : mergered) System.out.println(s);

//        long timeSpent = System.currentTimeMillis() - startTime;
//        System.out.println("Program execution time is " + timeSpent + " milliseconds");
//        if (arrSize < 100) {System.out.print(Arrays.toString(mergered));}
//        startTime = System.currentTimeMillis();
//        Arrays.sort(unsorted);
//        timeSpent = System.currentTimeMillis() - startTime;
//        System.out.println("\n Program (non parallel )execution time is " + timeSpent + " milliseconds");
//        for (int i = 0; i < 20; i++) {
//            System.out.println(mergered[i]);
//        }
    }
}
