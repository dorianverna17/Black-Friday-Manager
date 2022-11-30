import java.io.FileWriter;
import java.io.IOException;

public class ShippingThread implements Runnable {
    private final String command_id;
    private final String product_id;

    public ShippingThread(String command_id, String product_id) {
        this.command_id = command_id;
        this.product_id = product_id;
    }

    @Override
    public void run() {
        // write shipped to file
        synchronized (Tema2.products_file_lock) {
            try {
                FileWriter myWriter = new FileWriter(Tema2.output_directory + "/order_products_out.txt", true);
                myWriter.write(command_id + "," + product_id + ",shipped\n");
                myWriter.close();
            } catch (IOException e) {
                System.out.println("An error occurred.");
                e.printStackTrace();
            }
        }
    }
}
