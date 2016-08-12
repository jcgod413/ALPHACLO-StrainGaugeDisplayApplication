package jcgod.sgd.item;

/**
 * Created by Jaecheol on 16. 8. 13..
 */
public class ReportItem {
    private String date;
    private String time;
    private String distance;
    private String calory;
    private String lr;
    private String qh;
    private String lrqh;

    public void setDate(String date) {
        this.date = date;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public void setCalory(String calory) {
        this.calory = calory;
    }

    public void setLR(String lr) {
        this.lr = lr;
    }

    public void setQH(String qh) {
        this.qh = qh;
    }

    public void setLRQH(String lrqh) {
        this.lrqh = lrqh;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public String getDistance() {
        return distance;
    }

    public String getCalory() {
        return calory;
    }

    public String getLR() {
        return lr;
    }

    public String getQH() {
        return qh;
    }

    public String getLRQH() {
        return lrqh;
    }
}
