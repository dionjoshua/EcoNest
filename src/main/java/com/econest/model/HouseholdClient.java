package com.econest.model;

public class HouseholdClient extends Client {

    @Override
    public ClientType getType() {
        return ClientType.HOUSEHOLD;
    }

    @Override
    public String getContactName() {
        return getName();
    }
}
