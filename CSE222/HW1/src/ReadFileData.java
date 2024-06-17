import java.io.File;
import java.util.Scanner;

public class ReadFileData {
    /*Arrays to store operators, customers, and orders read from the file*/
    private Operator[] operators = new Operator[100];
    private Customer[] customers = new Customer[100];
    private Order[] orders = new Order[100];
    /*Arrays to keep track of unique IDs for operators and customers*/
    private int[] operatorIDs = new int[100];
    private int[] customerIDs = new int[100];
    private int operatorIDIndex = 0;
    private int customerIDIndex = 0;

    /*Indexes for adding elements to the arrays*/
    private int operatorIndex = 0;
    private int customerIndex = 0;
    private int orderIndex = 0;
    /*Reads data from a file and parses each line */
    public void readDataFromFile(String filePath) {
        File file = new File(filePath);
        /*Check if file exists before attempting to read */
        if (!file.exists()) {
            System.out.println("File not found: " + filePath);
            return;
        }

        try {
            Scanner scanner = new Scanner(file);
            /* Read file line by line */
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                try {
                    parseLine(line); /* Try to parse each line */
                } catch (Exception e) {
                    /* If an error occurs with this line, continue with the next line */
                }
            }
            scanner.close();
        } catch (Exception e) {
            /* Catch an error if there is a problem reading the file */
        }
        
    }
    /*Parses a line from the file and stores the data accordingly */
    private void parseLine(String line) {
        String[] sections = line.split(";",-1);         /*Each ";" on the line divide into sections at the sign */
        if (sections.length > 0) {                     /* Check if line is not empty */
            String type = sections[0];
            int id;
            try {
                switch (type) {                      /* Switch based on the type of data  */                   
                    case "order":
                    /*Keeping the parsed sections to check for incorrect input */
                    int count = Integer.parseInt(sections[2]);
                    int totalPrice = Integer.parseInt(sections[3]);
                    int status = Integer.parseInt(sections[4]);
                    int opID = Integer.parseInt(sections[5]);
                        if (sections.length == 6 ) {   /*Making sure there are no too many or too few sections */
                            for (String part : sections) {         /*Checking for empty strings */
                                if (part.trim().isEmpty()) {
                                  
                                    return; 
                                }
                            }
                            if( count > 0 && totalPrice > 0 && status >= 0 && opID > 0){        /*If the data is valid, create a new order object */
                            Order order = new Order(sections[1], Integer.parseInt(sections[2]),
                                    Integer.parseInt(sections[3]), Integer.parseInt(sections[4]),
                                    Integer.parseInt(sections[5]));
                            orders[orderIndex++] = order;
                            }
                            else {
                                return;
                            }
                        }
                        break;
                    case "retail_customer":
                        /*Keeping the parsed sections to check for incorrect input */
                        int customerID = Integer.parseInt(sections[5]);
                        opID = Integer.parseInt(sections[6]);
                        if (sections.length == 7) {            /*Making sure there are no too many or too few sections */
                            for (String part : sections) {          /*Checking for empty strings */
                                if (part.trim().isEmpty()) {
                                  
                                    return; 
                                }
                            }
                            if( customerID > 0 && opID > 0){    /*Checking if numbers are positive */
                            id = Integer.parseInt(sections[5]);    
                            if (isUniqueID(customerIDs, customerIDIndex, id) == 0) {    /*If there are similar Customer IDs, ignore that row */
                                return;
                            }
                            /*If it is a valid entry, create a new object */
                            customerIDs[customerIDIndex++] = id;
                            retail_customer customer = new retail_customer(sections[1], sections[2], sections[3], 
                                    sections[4], Integer.parseInt(sections[5]), Integer.parseInt(sections[6]));
                            customers[customerIndex++] = customer;
                            }
                            else {
                                return;
                            }
                        }
                        break;
                    case "corporate_customer":
                        /*Keeping the parsed sections to check for incorrect input */
                        customerID = Integer.parseInt(sections[5]);
                        opID = Integer.parseInt(sections[6]);
                        if (sections.length == 8) {            /*Making sure there are no too many or too few sections */
                            for (String part : sections) {         /*Checking for empty strings */
                                if (part.trim().isEmpty()) {

                                    return;
                                }
                            }
                            if( customerID > 0 && opID > 0){    /*Checking if numbers are positive */
                            id = Integer.parseInt(sections[5]);
                            if (isUniqueID(customerIDs, customerIDIndex, id) == 0) {    /*If there are similar Customer IDs, ignore that row */
                                return;
                            }
                            /*If it is a valid entry, create a new object */
                            customerIDs[customerIDIndex++] = id;
                            corporate_customer customer = new corporate_customer(sections[1], sections[2], 
                                    sections[3], sections[4], Integer.parseInt(sections[5]), 
                                    Integer.parseInt(sections[6]), sections[7]);
                            customers[customerIndex++] = customer;
                            }
                            else {
                                return;
                            }
                        }
                        break;
                    case "operator":
                    /*Keeping the parsed sections to check for incorrect input */
                    opID = Integer.parseInt(sections[5]);
                    int wage = Integer.parseInt(sections[6]);
                    for (String part : sections) {      /*Checking for empty strings */
                        if (part.trim().isEmpty()) {
                          
                            return; 
                        }
                    }
                        if (sections.length == 7) {        /*Making sure there are no too many or too few sections */
                            if( opID > 0 && wage > 0){  /*Checking if numbers are positive */
                            id = Integer.parseInt(sections[5]);
                                if (isUniqueIDop(operatorIDs, operatorIDIndex, customerIDs, customerIDIndex, id) == 0) {    /*Check operator ID for uniqueness within itself and among customer IDs */
                                    return;
                                }
                            /*If it is a valid entry, create a new object */
                            operatorIDs[operatorIDIndex++] = id;                                        
                            Operator operator = new Operator(sections[1], sections[2], sections[3], 
                                    sections[4], Integer.parseInt(sections[5]), Integer.parseInt(sections[6]));
                            operators[operatorIndex++] = operator;
                            }
                            else {
                                return;
                            }
                        }
                        break;
                    default:
                        break;
                }
            } catch (NumberFormatException e) {   /*Handle any format exceptions, possibly due to invalid data types */
            }
        }
    }

    private int isUniqueID(int[] ids, int length, int id) {     /*Checks if a given ID is unique within the specified array */
        for (int i = 0; i < length; i++) {
            if (ids[i] == id) {
                return 0;
            }
        }
        return 1;
    }

    private int isUniqueIDop(int[] operatorIDs, int operatorLength, int[] customerIDs, int customerLength, int id) {  /*Checks if a given operator ID is unique within the specified array */
        for (int i = 0; i < operatorLength; i++) {
            if (operatorIDs[i] == id) {
                return 0;
            }
        }
        for (int i = 0; i < customerLength; i++) {
            if (customerIDs[i] == id) {
                return 0;
            }
        }
        return 1;
    }
    
    public void defineRelationships() {         /*Establishes relationships between operators, customers, and orders after all data is read */
        defineCustomersForOperators();
        defineOrdersForCustomers();
    }

    private void defineCustomersForOperators() {        /* Matches customers to their respective operators */
        for (Operator operator : operators) {
            if (operator != null) {
                operator.define_customers(customers);
            }
        }
    }

    private void defineOrdersForCustomers() {       /* Matches orders to their respective customers */
        for (Customer customer : customers) {
            if (customer != null) {
                customer.define_orders(orders);
            }
        }
    }
    /* Main function */
    public static void main(String[] args) {
        
    ReadFileData reader = new ReadFileData();
    reader.readDataFromFile("content.txt"); 
    reader.defineRelationships(); 
    
    Scanner inputScanner = new Scanner(System.in);
    System.out.println("Please enter your ID...");
    
    int id = 0;
    try {
        id = inputScanner.nextInt();
        if (id <= 0) {              /*Validate that the ID is a positive integer */
            System.out.println("ID must be a positive integer. Exiting program.");
            return; 
        }
    } catch (Exception ime) { /* Handle any exceptions related to invalid input*/
        System.out.println("Invalid input. Exiting program.");
        return; 
    }

    /*Search among operators first*/
    int found = 0;
    for (Operator operator : reader.operators) {
        if (operator != null && operator.getID() == id) {
            /*Print operator and their customers' details if found*/
            System.out.println("*** Operator Screen ***");
            System.out.println("----------------------------");
            operator.print_operator();
            operator.print_customers(); 
            found = 1;
            break;
        }
    }

    /*If no operator is found, search among customers*/
    if (found == 0) {
        for (Customer customer : reader.customers) {
            if (customer != null && customer.getID() == id) {
                /*Print customer and their orders' details if found*/
                System.out.println("*** Customer Screen ***");
                customer.print_customer();
                
                found = 1;
                break;
            }
        }
    }

    /*Inform the user if no matching operator or customer is found*/
    if (found == 0) {
        System.out.println("No operator/customer was found with ID " + id + ". Please try again.");
    }
    
    inputScanner.close();
}
}

