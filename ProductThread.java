import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class ProductThread implements Runnable {
    private final String id;
    private final int index;

    HashMap<String, ArrayList<String>> products;

    public ProductThread(String id, int index) {
        this.id = id;
        this.index = index;
        products = new HashMap<>();
    }

    public void readFile(String file) {
        BufferedReader reader;

        try {
            reader = new BufferedReader(new FileReader(file));
            String line = reader.readLine();

            while (line != null) {
                int comma_index = 0;

                for (int i = 0; i < line.length(); i++) {
                    if (line.charAt(i) == ',') {
                        comma_index = i;
                        break;
                    }
                }

                String order_id = line.substring(0, comma_index);
                String product_id = line.substring(comma_index + 1);

                if (!products.containsKey(order_id)) {
                    products.put(order_id, new ArrayList<>());
                }
                products.get(order_id).add(product_id);

                line = reader.readLine();
            }

            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        String order_products = Tema2.test_directory + "/order_products.txt";
        readFile(order_products);

        if (products.containsKey(id)) {
            ArrayList<String> arr = products.get(id);
            String product_id = arr.get(index);

            Thread t = new Thread(new ShippingThread(id, product_id));
            t.start();

            try {
                t.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
