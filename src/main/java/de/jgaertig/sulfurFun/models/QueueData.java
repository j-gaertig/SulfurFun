package de.jgaertig.sulfurFun.models;

public class QueueData {
    private final int position;
    private final int maxTotal;

    public QueueData(int position, int maxTotal) {
        this.position = position;
        this.maxTotal = maxTotal;
    }

    public int getPosition() { return position; }
    public int getMaxTotal() { return maxTotal; }

}