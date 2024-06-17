public class Person {
    protected String name;
    protected String surname;
    protected String address;
    protected String phone;
    protected int ID;

    /* A constructor is initializing for the person whose personal information is given */
    public Person(String name, String surname, String address, String phone, int ID) {
        this.name = name;
        this.surname = surname;
        this.address = address;
        this.phone = phone;
        this.ID = ID;
    }

    /* Getter method for the name*/
    public String getName() {
        return name;
    }
    /* Getter method for the surname*/
    public String getSurname() {
        return surname;
    }
    /* Getter method for the address*/
    public String getAddress() {
        return address;
    }
    /* Getter method for the phone*/
    public String getPhone() {
        return phone;
    }
    /* Getter method for the ID*/
    public int getID() {
        return ID;
    }

}
