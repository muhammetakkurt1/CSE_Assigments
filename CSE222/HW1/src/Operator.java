public class Operator extends Person {
    private int wage; 
    private Customer[] customers; /* An array to hold a list of customers associated with this operator */

    /*  Constructor for initializing an Operator object with provided details including personal information and wage*/
    public Operator(String name, String surname, String address, String phone, int ID, int wage) {
        super(name, surname, address, phone, ID); /*Calls the constructor of the Person superclass*/
        this.wage = wage;
        this.customers = new Customer[100]; /*Initializes the array to store up to 100 customers */
    }

    /*Method to print operator details*/
    public void print_operator() {
        System.out.println("Name & Surname: " + getName() + " "+ getSurname() +
                           "\nAddress: " + getAddress() + "\nPhone: " + getPhone() + "\nID: " + getID() + "\nWage: " + wage);
                           System.out.println("----------------------------");
    }

    /* Method to print information about all customers associated with this operator*/
    public void print_customers() {
        int foundCustomer = 0; /* Flag to track if any customers are associated with this operator*/
        int customerNumber = 1; /* Counter for numbering the customers in the output */
        for (Customer customer : customers) {
            if (customer != null) {
                foundCustomer = 1; /*Indicates that at least one customer has been found*/
                System.out.print("Customer #" + customerNumber + " ");
                if (customer instanceof corporate_customer) {           /*Checks the type of customer and prints appropriate message */         
                    System.out.println("(a corporate customer): ");
                } else {
                    System.out.println("(a retail customer): ");
                }
                customer.print_customer(); /* Calls the method to print customer details*/
                customerNumber++; /* Increments the customer counter*/
            }
        }
        if (foundCustomer == 0) {
            /*If no customers are found, prints a message indicating so*/
            System.out.println("This operator doesn't have any customer.");
            System.out.println("----------------------------");
        }
    }
    
    

    /* Method to associate customers with this operator based on the operator ID*/
    public void define_customers(Customer[] allCustomers) {
        for (Customer customer : allCustomers) {
            if (customer != null && customer.getOperator_ID() == this.getID()) {    /*If a customer's operator ID matches this operator's ID, the customer is added to the operator's list */
                addCustomer(customer);
            }
        }
    }

    /* Helper method to add a customer to the first null position in the customers array */
    private void addCustomer(Customer customer) {
        for (int i = 0; i < customers.length; i++) {
            if (customers[i] == null) {
                customers[i] = customer;
                break;
            }
        }
    }
}
