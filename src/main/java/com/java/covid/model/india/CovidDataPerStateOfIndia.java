package com.java.covid.model.india;

public class CovidDataPerStateOfIndia {

    private int no;
    private String state;
    private int active;
    private int confirmed;
    private int deaths;
    private String lastupdatedtime;
    private int recovered;
    private DeltaInformation delta;

    public int getNo() {
        return no;
    }

    public void setNo(int no) {
        this.no = no;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public int getActive() {
        return active;
    }

    public void setActive(int active) {
        this.active = active;
    }

    public int getConfirmed() {
        return confirmed;
    }

    public void setConfirmed(int confirmed) {
        this.confirmed = confirmed;
    }

    public int getDeaths() {
        return deaths;
    }

    public void setDeaths(int deaths) {
        this.deaths = deaths;
    }

    public String getLastupdatedtime() {
        return lastupdatedtime;
    }

    public void setLastupdatedtime(String lastupdatedtime) {
        this.lastupdatedtime = lastupdatedtime;
    }

    public int getRecovered() {
        return recovered;
    }

    public void setRecovered(int recovered) {
        this.recovered = recovered;
    }

    public DeltaInformation getDelta() {
        return delta;
    }

    public void setDelta(DeltaInformation delta) {
        this.delta = delta;
    }
}
