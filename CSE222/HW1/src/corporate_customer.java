public class corporate_customer extends Customer {
    private String company_name;

    public corporate_customer(String name, String surname, String address, String phone, int ID, int operator_ID, String company_name) {
        super(name, surname, address, phone, ID, operator_ID);          /* Call to the superclass constructor to set common attributes */
        this.company_name = company_name;                   /*Sets the company name specific to the corporate customer */
    }

    /*Getter method for the company name*/
    public String getCompany_name() {
        return company_name;
    }

     /*  Overridden method to print details specific to a corporate customer*/
    @Override
    public void print_customer() {
        
       
        super.print_customer();  /*Calls the superclass method to print common customer details*/
        System.out.println("Company Name: " + company_name); /*Prints the company name specific to this corporate customer */
        print_orders();     /* Calls print_orders to print details of all orders associated with this customer */

    }
}
