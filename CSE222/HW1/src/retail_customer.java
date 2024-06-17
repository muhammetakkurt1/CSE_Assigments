public class retail_customer extends Customer {
    /*Constructor for creating a retail customer with provided details */
    public retail_customer(String name, String surname, String address, String phone, int ID, int operator_ID) {
        super(name, surname, address, phone, ID, operator_ID);  /*Calls the constructor of the Customer superclass*/
    }

    /*Overridden method to print details specific to a retail customer*/
    @Override
    public void print_customer() {
        
        super.print_customer(); /*Calls the superclass method to print common customer details */
        print_orders(); /* Calls print_orders to print details of all orders associated with this retail customer */
    }
}
