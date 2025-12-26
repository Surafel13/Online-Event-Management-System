package com.eventmgmt.models;

import java.sql.Date;
import java.sql.Time;

public class Event {
    private int id;
    private String title;
    private Date date;
    private Time time;
    private String location;
    private int capacity;

    public Event() {}
this.date = date;
        this.time = time;
        this.location = location;
        this.capacity = capacity;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public Date getDate() { return date; }
    public void setDate(Date date) { this.date = date; }

    public Time getTime() { return time; }
    public void setTime(Time time) { this.time = time; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public int getCapacity() { return capacity; }
    public void setCapacity(int capacity) { this.capacity = capacity; }
}
