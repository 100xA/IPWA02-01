package de.iu.ipwa.ghostnet.service;

public class RescuerIdentity {

    private final String name;
    private final String phone;

    public RescuerIdentity(String name, String phone) {
        this.name = name;
        this.phone = phone;
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }
}
