package com.xdandroid.sample;

public class DakaInfo {

    private String timeLabel;//时间段
    private int count;//处罚次数


    public DakaInfo(String timeLabel, int count) {
        this.timeLabel = timeLabel;
        this.count = count;
    }

    public String getTimeLabel() {
        return timeLabel;
    }

    public void setTimeLabel(String timeLabel) {
        this.timeLabel = timeLabel;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
