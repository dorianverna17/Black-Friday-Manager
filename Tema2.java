import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.CyclicBarrier;

public class Tema2 {
    public static ArrayList<Order> orderPool;

    public static int no_threads;
    public static String test_directory;
    public static String output_directory;

    // synchronization elements
    public static final Object order_pool_lock = new Object();
    public static final Object order_file_lock = new Object();
    public static final Object products_file_lock = new Object();

    public static void main(String[] args) {
        // get the argument values
        test_directory = args[0];
        output_directory = "output/output_" + test_directory.charAt(test_directory.length() - 1);

        int workers_count = Integer.parseInt(args[1]);
        int offset, bytes, aux_bytes;
        int start, end;

        String orders = test_directory + "/orders.txt";

        File f = new File(orders);

        no_threads = workers_count;
        bytes = (int) (f.length() / no_threads);
        aux_bytes = (int) (f.length() % no_threads);
        offset = 0;

        orderPool = new ArrayList<>();

        // Create the Reader Threads
        Thread[] threads = new Thread[workers_count];
        for (int i = 0; i < workers_count; i++) {
            if (aux_bytes > 0) {
                threads[i] = new Thread(new ReaderThread(orders, i, offset, bytes + 1));
                aux_bytes -= 1;
                offset += bytes + 1;
            } else {
                threads[i] = new Thread(new ReaderThread(orders, i, offset, bytes));
                offset += bytes;
            }
            threads[i].start();
        }

        // finish the Reader Threads
        for (int i = 0; i < workers_count; i++) {
            try {
                threads[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        // create the Order Threads
        for (int i = 0; i < workers_count; i++) {
            start = (int) (i * (double) orderPool.size() / no_threads);
            end = Math.min((int) ((i + 1) * (double) orderPool.size() / no_threads), orderPool.size());
            threads[i] = new Thread(new OrderThread(i, start, end));
            threads[i].start();
        }

        // finish the Order Threads
        for (int i = 0; i < workers_count; i++) {
            try {
                threads[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
