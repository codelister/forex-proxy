package com.example.forexproxy.services.models;

public class ExchangeRate {
    private String from;
    private String to;
    private double bid;
    private double ask;
    private double price;
    private String time_stamp;

    public ExchangeRate() {

    }
    public ExchangeRate(String from, String to, double bid, double ask, double price, String time_stamp) {
        this.from = from;
        this.to = to;
        this.bid = bid;
        this.ask = ask;
        this.price = price;
        this.time_stamp = time_stamp;
    }

    public double getAsk() {
        return ask;
    }

    public void setAsk(double ask) {
        this.ask = ask;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getTime_stamp() {
        return time_stamp;
    }

    public void setTime_stamp(String time_stamp) {
        this.time_stamp = time_stamp;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public double getBid() {
        return bid;
    }

    public void setBid(double bid) {
        this.bid = bid;
    }
}
