public class Order {
    /*Private variables to hold order details */
    private String product_name;
    private int count;
    private int total_price;
    private int status;
    private int customer_ID;

    /*Constructor to initialize an order object with provided details*/
    public Order(String product_name, int count, int total_price, int status, int customer_ID) {
        this.product_name = product_name;
        this.count = count;
        this.total_price = total_price;
        this.status = status;
        this.customer_ID = customer_ID;
    }

    /*Method to print order details in a formatted string*/
    public void print_order() {
        System.out.println("Product Name: " + product_name + 
                           " - Count: " + count +
                           " - Total Price: " + total_price + 
                           " - Status: " + getStatusAsString());
                        
    }
    
    /*Converts the numeric status to a corresponding String representation*/
    private String getStatusAsString() {
        switch (status) {
            case 0: return "Initialized.";
            case 1: return "Processing.";
            case 2: return "Completed.";
            case 3: return "Cancelled.";
            default: return "Unknown Status.";      /*If it is an undefined situation */
        }
    }
    
    public String getProduct_name() {   /*Getter for product_name */
        return product_name;
    }

    public int getCount() {     /* Getter for count */
        return count;
    }                                  

    public int getTotal_price() {   /* Getter for total_price */
        return total_price;
    }

    public int getStatus() {        /*Getter for status */
        return status;
    }

    public int getCustomer_ID() {   /*Getter for customer_ID */
        return customer_ID;
    }

}
