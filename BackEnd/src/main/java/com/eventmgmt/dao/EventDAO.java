package com.eventmgmt.dao;

import com.eventmgmt.models.Event;
import com.eventmgmt.utils.DBUtils;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EventDAO {

    public boolean createEvent(Event event) throws SQLException {
        String sql = "INSERT INTO events (title, date, time, location, capacity) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBUtils.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, event.getTitle());
            stmt.setDate(2, event.getDate());
            stmt.setTime(3, event.getTime());
            stmt.setString(4, event.getLocation());
            stmt.setInt(5, event.getCapacity());
            return stmt.executeUpdate() > 0;
        }
    }

    public List<Event> getAllEvents() throws SQLException {
        List<Event> events = new ArrayList<>();
        String sql = "SELECT * FROM events";
        try (Connection conn = DBUtils.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                events.add(new Event(
                        rs.getInt("id"),
                        rs.getString("title"),
                        rs.getDate("date"),
                        rs.getTime("time"),
                        rs.getString("location"),
                        rs.getInt("capacity")));
            }
        }
        return events;
    }
}


