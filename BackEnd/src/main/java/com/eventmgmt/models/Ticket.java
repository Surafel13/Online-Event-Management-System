package com.eventmgmt.models;

import java.sql.Timestamp;

public class TicketDetail {
    private String ticketId;
    private String userName;
    private String userEmail;
    private String eventTitle;
    private String eventLocation;
    private Timestamp bookingDate;
    private String status;

    public TicketDetail(String ticketId, String userName, String userEmail, String eventTitle, String eventLocation,
            Timestamp bookingDate, String status) {
        this.ticketId = ticketId;
        this.userName = userName;
        this.userEmail = userEmail;
        this.eventTitle = eventTitle;
        this.eventLocation = eventLocation;
        this.bookingDate = bookingDate;
        this.status = status;
    }

    // Getters
    public String getTicketId() {
        return ticketId;
    }

    public String getUserName() {
        return userName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public String getEventTitle() {
        return eventTitle;
    }

    public String getEventLocation() {
        return eventLocation;
    }

    public Timestamp getBookingDate() {
        return bookingDate;
    }

    public String getStatus() {
        return status;
    }
}
