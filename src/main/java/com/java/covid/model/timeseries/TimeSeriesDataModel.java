package com.java.covid.model.timeseries;

public class TimeSeriesDataModel {

    private int dailyconfirmed;
    private int dailydeceased;
    private String date;
    private int dailyrecovered;
    private int totalconfirmed;
    private int totaldeceased;
    private int totalrecovered;

    public int getDailyconfirmed() {
        return dailyconfirmed;
    }

    public void setDailyconfirmed(int dailyconfirmed) {
        this.dailyconfirmed = dailyconfirmed;
    }

    public int getDailydeceased() {
        return dailydeceased;
    }

    public void setDailydeceased(int dailydeceased) {
        this.dailydeceased = dailydeceased;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getDailyrecovered() {
        return dailyrecovered;
    }

    public void setDailyrecovered(int dailyrecovered) {
        this.dailyrecovered = dailyrecovered;
    }

    public int getTotalconfirmed() {
        return totalconfirmed;
    }

    public void setTotalconfirmed(int totalconfirmed) {
        this.totalconfirmed = totalconfirmed;
    }

    public int getTotaldeceased() {
        return totaldeceased;
    }

    public void setTotaldeceased(int totaldeceased) {
        this.totaldeceased = totaldeceased;
    }

    public int getTotalrecovered() {
        return totalrecovered;
    }

    public void setTotalrecovered(int totalrecovered) {
        this.totalrecovered = totalrecovered;
    }
}
