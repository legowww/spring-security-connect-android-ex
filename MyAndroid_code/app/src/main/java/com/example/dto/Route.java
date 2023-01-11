package com.example.dto;

import java.util.ArrayList;
import java.util.List;

public class Route {

    private List<Transportation> transportationList = new ArrayList<>();

    private int totalTime;

    private String firstStartStation;

    private String lastEndStation;


    public List<Transportation> getTransportationList() {
        return transportationList;
    }

    public int getTotalTime() {
        return totalTime;
    }

    public String getFirstStartStation() {
        return firstStartStation;
    }

    public String getLastEndStation() {
        return lastEndStation;
    }
}
