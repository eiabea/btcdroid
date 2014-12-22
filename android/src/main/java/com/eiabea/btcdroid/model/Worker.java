package com.eiabea.btcdroid.model;

public class Worker {

    // Attributes
    private String name;
    private long last_share;
    private String score;
    private boolean alive;
    private int shares;
    private int hashrate;

    // Standardconstructor
    public Worker() {
    }

    public long getLast_share() {
        return last_share;
    }

    public String getScore() {
        return score;
    }

    public boolean isAlive() {
        return alive;
    }

    public int getShares() {
        return shares;
    }

    public int getHashrate() {
        return hashrate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


}
