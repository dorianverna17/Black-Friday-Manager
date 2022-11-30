import java.io.FileWriter;
import java.io.IOException;

public class OrderThread implements Runnable {
    int thread_id;
    int start;
    int end;

    public OrderThread(int thread_id, int start, int end) {
        this.thread_id = thread_id;
        this.start = start;
        this.end = end;
    }

    @Override
    public void run() {
        int no_threads = 0;

        for (int i = start; i < end; i++) {
            // launch ProductThread threads
            Order order = Tema2.orderPool.get(i);
            no_threads = order.getNo_products();
            Thread[] threads = new Thread[order.getNo_products()];

            for (int j = 0; j < no_threads; j++) {
                threads[j] = new Thread(new ProductThread(order.getId(), j));
                threads[j].start();
            }

            for (int j = 0; j < no_threads; j++) {
                try {
                    threads[j].join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            // write completed to file
            synchronized (Tema2.order_file_lock) {
                try {
                    FileWriter myWriter = new FileWriter(Tema2.output_directory + "/orders_out.txt", true);
                    myWriter.write(order.getId() + "," + order.getNo_products() + ",completed\n");
                    myWriter.close();
                } catch (IOException e) {
                    System.out.println("An error occurred.");
                    e.printStackTrace();
                }
            }
        }
    }
}
