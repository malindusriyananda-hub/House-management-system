package model;


public class Guest {

    private int guestId;
    private String name;
    private String nic;
    private String phone;
    private String email;
    private String address;

    public Guest() {
    }

    public Guest(int guestId, String name, String nic, String phone, String email, String address) {
        this.guestId = guestId;
        this.name = name;
        this.nic = nic;
        this.phone = phone;
        this.email = email;
        this.address = address;
    }

    public int getGuestId() {
        return guestId;
    }

    public void setGuestId(int guestId) {
        this.guestId = guestId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNic() {
        return nic;
    }

    public void setNic(String nic) {
        this.nic = nic;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Override
    public String toString() {
        return name;
    }
}
