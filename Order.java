public class Order {
    private String id;
    private int no_products;

    public Order(String id, int no_products) {
        this.id = id;
        this.no_products = no_products;
    }

    public String getId() {
        return id;
    }

    public int getNo_products() {
        return no_products;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setNo_products(int no_products) {
        this.no_products = no_products;
    }

    @Override
    public String toString() {
        return "Order{" +
                "id='" + id + '\'' +
                ", no_products=" + no_products +
                '}';
    }
}
