package com.econest.model;

// Office clients have a company name plus a person we deal with
public class OfficeClient extends Client {
    private String contactPerson;

    @Override
    public ClientType getType() {
        return ClientType.OFFICE;
    }

    @Override
    public String getContactName() {
        if (contactPerson == null || contactPerson.isEmpty()) {
            return getName();
        }
        return contactPerson;
    }

    public String getContactPerson() { return contactPerson; }
    public void setContactPerson(String contactPerson) { this.contactPerson = contactPerson; }
}
