package com.econest.model;

public abstract class Client implements Notifiable {
    private int id;
    private String name;
    private String phone;
    private String email;
    private String address;

    public abstract ClientType getType();

    // who the delivery / email should be addressed to
    public abstract String getContactName();

    public static Client create(ClientType type) {
        return type == ClientType.OFFICE ? new OfficeClient() : new HouseholdClient();
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    @Override
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    @Override
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    @Override
    public String toString() {
        return name + " (" + getType() + ")";
    }
}
