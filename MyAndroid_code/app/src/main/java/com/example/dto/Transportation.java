package com.example.dto;

public class Transportation {
    //COMMON FIELD
    private int time;
    private String trafficType;

    //BUS ONLY FIELD
    private String busNum;
    private String startName;
    private String endName;


    public String getBusNum() {
        return busNum;
    }

    public String getStartName() {
        return startName;
    }

    public String getEndName() {
        return endName;
    }

    public String getTrafficType() {
        return trafficType;
    }

    public int getTime() {
        return time;
    }
}
