import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Scanner;

public class ReaderThread implements Runnable {
    private final String file;
    private final int thread_id;
    private final int offset;
    private final int bytes;

    public ReaderThread(String file, int thread_id, int offset, int bytes) {
        this.file = file;
        this.thread_id = thread_id;
        this.bytes = bytes;
        this.offset = offset;
    }

    public String getStringByteArray(int start, int end, byte[] arr) {
        StringBuilder res = new StringBuilder();
        for (int i = start; i < end; i++) {
            res.append((char) arr[i]);
        }
        return res.toString();
    }

    @Override
    public void run() {
        File f = new File(file);
        FileInputStream orders_file = null;
        try {
            orders_file = new FileInputStream(String.valueOf(file));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        byte[] byte_array = new byte[2 * bytes];

        int chars_read = 0;
        int size = byte_array.length;
        if (orders_file != null) {
            try {
                orders_file.getChannel().position(offset);
                int bytes_to_read = 0;
                if (f.length() >= bytes + offset)
                    bytes_to_read = bytes;
                else
                    bytes_to_read = (int) (f.length() - offset);
                chars_read = orders_file.read(byte_array, 0, bytes_to_read);

                if (chars_read == 0)
                    System.out.println("Couldn't read from file!");
                // take care of the beginning
                int new_line = 0;
                if (byte_array[0] != 'o' && byte_array[1] != '_' && offset != 0) {
                    // take only what is from the next new line
                    for (int i = 0; i < byte_array.length; i++) {
                        if (byte_array[i] == '\n') {
                            new_line = i;
                            break;
                        }
                    }
                    byte_array = Arrays.copyOfRange(byte_array, new_line + 1, byte_array.length);
                }

                size = chars_read - new_line - 1;

                // take care of the end
                if ((thread_id != Tema2.no_threads - 1 && byte_array[size] != '\n') ||
                        (thread_id == Tema2.no_threads - 1 && size - 1 >= 0 && byte_array[size - 1] != '\n')) {
                    // read some more bytes until we find a new line
                    if (new_line != 0) {
                        chars_read = orders_file.read(byte_array, size, byte_array.length - (size));
                    } else {
                        chars_read = orders_file.read(byte_array, byte_array.length - chars_read, chars_read);
                    }
                    if (chars_read == 0)
                        System.out.println("Couldn't read from file!");
                    for (int i = size; i < byte_array.length; i++) {
                        if (byte_array[i] == '\n') {
                            new_line = i;
                            break;
                        }
                    }
                    byte_array = Arrays.copyOfRange(byte_array, 0, new_line + 1);
                    size = new_line;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // convert the byte array in a list of strings
        // each thread has to fill in the order pool now
        int order_beginning = 0;
        int order_end;
        int no_prod_beginning = 0;
        int no_prod_fin;
        String no_products;
        String order_id = null;
        for (int i = 0; i < size; i++) {
            if (byte_array[i] == 'o' && byte_array[i + 1] == '_') {
                order_beginning = i;
            }
            if (byte_array[i] == ',') {
                order_end = i;
                order_id = getStringByteArray(order_beginning, order_end, byte_array);
                no_prod_beginning = i + 1;
                order_beginning = 0;
                continue;
            }
            if ((byte_array[i + 1] < '0' || byte_array[i + 1] > '9') &&
                    (byte_array[i] >= '0' && byte_array[i] <= '9') &&
                    no_prod_beginning != 0) {
                no_prod_fin = i + 1;
                no_products = getStringByteArray(no_prod_beginning, no_prod_fin, byte_array);
                no_prod_beginning = 0;

                synchronized (Tema2.order_pool_lock) {
                    Tema2.orderPool.add(new Order(order_id, Integer.parseInt(no_products)));
                }
            }
        }
    }
}
