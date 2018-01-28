package org.seemsGood.shara.model.firebase;

import java.io.Serializable;

public class WorkTime implements Serializable {

    private String from;
    private String to;

    public WorkTime() {
    }

    public WorkTime(String from, String to) {
        this.from = from;
        this.to = to;
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

    public String getWorkTime() {
        if(from.equals("00:00") && to.equals("00:00")) {
            return "24 hours";
        }
        return from+"-"+to;
    }
}