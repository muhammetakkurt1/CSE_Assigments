public class Customer extends Person {
    protected Order[] orders; /*Array to store orders associated with this customer*/
    protected int operator_ID; /*  ID of the operator to which this customer is linked*/

    /* Constructor to initialize a Customer object with person details and operator ID*/
    public Customer(String name, String surname, String address, String phone, int ID, int operator_ID) {
        super(name, surname, address, phone, ID); /*Calls the constructor of the Person superclass*/
        this.operator_ID = operator_ID; /*Sets the operator ID for this customer */
        this.orders = new Order[100]; /* Initializes the orders array*/
    }

    /*Method of printing customer's information */
    public void print_customer() {
        System.out.println( "Name & Surname: " + getName() + " " + getSurname() +
                           "\nAddress: " + getAddress() + "\nPhone: " + getPhone() + "\nID: " + getID() + "\nOperator ID: " + operator_ID);
                           
    
    }

    /* Prints all orders associated with this customer*/
    public void print_orders() {
        int orderNumber = 1; /*Counter to number the orders for display*/
        for (Order order : orders) {
            if (order != null) {    /*Check if the order slot is not empty */
                System.out.print("Order #" + orderNumber + " => ");
                order.print_order(); /*Calls the method to print order details */
                orderNumber++; /* Increments the order counter */ 
            }
        }
        System.out.println("----------------------------");
    }
    

    /*Associates orders to this customer based on customer ID*/
    public void define_orders(Order[] allOrders) {
        for (Order order : allOrders) {
            if (order != null && order.getCustomer_ID() == this.getID()) {  /*Checks if the order belongs to this customer */
                addOrder(order);        /* Adds the order to this customer's orders array */
            }
        }
    }

    /*Helper method to add an order to the first empty slot in the orders array*/
    private void addOrder(Order order) {
        for (int i = 0; i < orders.length; i++) {
            if (orders[i] == null) { /*Finds the first empty slot */
                orders[i] = order;      /*Stores the order in the array */
                break;
            }
        }
    }
    
    /* Getter method for the operator ID*/
    public int getOperator_ID() {
        return operator_ID;
    }
}
