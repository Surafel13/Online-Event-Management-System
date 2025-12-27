package com.eventmgmt.models;

import java.sql.Timestamp;

public class Ticket {
    private String id;
    private int userId;
    private int eventId;
    private Timestamp bookingDate;
    private String status;

    public Ticket() {}

    public Ticket(String id, int userId, int eventId, Timestamp bookingDate, String status) {
        this.id = id;
        this.userId = userId;
        this.eventId = eventId;
        this.bookingDate = bookingDate;
        this.status = status;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public int getEventId() { return eventId; }
    public void setEventId(int eventId) { this.eventId = eventId; }

    public Timestamp getBookingDate() { return bookingDate; }
    public void setBookingDate(Timestamp bookingDate) { this.bookingDate = bookingDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
