package com.example.beaconsandroid;

class Beacon {

    private String uuid;

    public Beacon(String uuid) {
        this.uuid = uuid;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
}

