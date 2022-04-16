import java.util.Arrays;
import java.util.Comparator;
import java.util.Random;

//java -classpath .:target/dependency/* ParallelSortTester

public class ParallelSortTester {

    public static void main(String[] args) {
        runSortTester();
    }

    public static void runSortTester() {
        int SIZE = 1000,  
            ROUNDS = 3,
            availableThreads = (Runtime.getRuntime().availableProcessors())*4;

        Integer[] a;

        Comparator<Integer> comp = new Comparator<Integer>() {
            public int compare(Integer d1, Integer d2) {
                return d1.compareTo(d2);
            }
        };

        System.out.printf("\nMax number of threads == %d\n\n", availableThreads);
        for (int i = 1; i <= availableThreads; i*=2) {
            if (i == 1) {
                System.out.printf("%d Thread:\n", i);
            }
            else {
                System.out.printf("%d Threads:\n", i);
            }
            for (int j = 0, k = SIZE; j < ROUNDS; ++j, k*=2) {
                a = createRandomArray(k);
                System.out.print(Arrays.toString(a));
                System.out.print("\n");
                long startTime = System.currentTimeMillis();
                ParallelMergeSorter.sort(a, comp, availableThreads);
                System.out.print(Arrays.toString(a));
                System.out.print("\n");
                long endTime = System.currentTimeMillis();

                if (!isSorted(a, comp)) {
                    throw new RuntimeException("not sorted afterward: " + Arrays.toString(a));
                }

                System.out.printf("%10d elements  =>  %6d ms \n", k, endTime - startTime);
            }
            System.out.print("\n");
        }
    }


    public static <E> boolean isSorted(E[] a, Comparator<? super E> comp) {
        for (int i = 0; i < a.length - 1; i++) {
            if (comp.compare(a[i], a[i + 1]) > 0) {
                System.out.println(a[i] + " > " + a[i + 1]);
                return false;
            }
        }
        return true;
    }

    public static <E> void shuffle(E[] a) {
        for (int i = 0; i < a.length; i++) {
            int randomIndex = (int) (Math.random() * a.length - i);
            swap(a, i, i + randomIndex);
        }
    }

    public static final <E> void swap(E[] a, int i, int j) {
        if (i != j) {
            E temp = a[i];
            a[i] = a[j];
            a[j] = temp;
        }
    }

    public static Integer[] createRandomArray(int length) {
        Integer[] a = new Integer[length];
        Random rand = new Random(System.currentTimeMillis());
        for (int i = 0; i < a.length; i++) {
            a[i] = rand.nextInt(1000000);
        }
        return a;
    }
}


public class ParallelMergeSorter extends Thread {
    public static <E> void sort(E[] a, Comparator<? super E> comp, int threads) {
        parallelMergeSort(a, 0, a.length-1, comp, threads);
    }

    private static <E> void mergeSort(E[] a, int from, int to,
            Comparator<? super E> comp) {
        if (from == to) {
            return;
        }
        if (to - from >0) {
            int mid = (from + to) / 2;
            
            mergeSort(a, from, mid, comp);
            mergeSort(a, mid + 1, to, comp);
            merge(a, from, mid, to, comp);
        }
    }

    private static <E> void parallelMergeSort(E[] a, int from, int to, Comparator<? super E> comp, int availableThreads){
        if (to - from > 0){
            if (availableThreads <=1) {
                mergeSort(a, from, to, comp);
            }
            else {
                int middle = to/2;
            
                Thread firstHalf = new Thread(){
                    public void run(){
                        parallelMergeSort(a, from, middle, comp, availableThreads - 1);
                    }
                };
                Thread secondHalf = new Thread(){
                    public void run(){
                        parallelMergeSort(a, middle + 1, to, comp, availableThreads - 1);

                    }
                };
            
                firstHalf.start();
                secondHalf.start();
			
                try {
                    firstHalf.join();
                    secondHalf.join();
                } catch (InterruptedException ie) {
                    ie.printStackTrace();
                }
                
                merge(a, from, middle, to, comp);
            }
        }
    }


    @SuppressWarnings("unchecked")
    private static <E> void merge(E[] a,
            int from, int mid, int to, Comparator<? super E> comp) {
        int n = to - from + 1;

        Object[] b = new Object[n];

        int i1 = from;
        int i2 = mid + 1;
        int j = 0;

        while (i1 <= mid && i2 <= to) {
            if (comp.compare(a[i1], a[i2]) < 0) {
                b[j] = a[i1];
                i1++;
            } else {
                b[j] = a[i2];
                i2++;
            }
            j++;
        }


        while (i1 <= mid) {
            b[j] = a[i1];
            i1++;
            j++;
        }

        while (i2 <= to) {
            b[j] = a[i2];
            i2++;
            j++;
        }


        for (j = 0; j < n; j++) {
            a[from + j] = (E) b[j];
        }
    }
}