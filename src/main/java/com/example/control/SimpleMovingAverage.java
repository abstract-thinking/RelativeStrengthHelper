package com.example.control;

import java.util.LinkedList;
import java.util.Queue;

public class SimpleMovingAverage {

    // queue used to store list so that we get the average
    private final Queue<Double> dataset = new LinkedList<>();
    private final int period;
    private double sum;

    // constructor to initialize period
    public SimpleMovingAverage(int period) {
        this.period = period;
    }

    // function to add new data in the
    // list and update the sum so that
    // we get the new mean
    public void addData(double num) {
        sum += num;
        dataset.add(num);

        // Updating size so that length
        // of data set should be equal
        // to period as a normal mean has
        if (dataset.size() > period) {
            sum -= dataset.remove();
        }
    }

    // function to calculate mean
    public double getMean() {
        return sum / period;
    }

}