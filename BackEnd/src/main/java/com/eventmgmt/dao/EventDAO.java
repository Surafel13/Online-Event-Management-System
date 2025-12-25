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
    public boolean updateEvent(Event event) throws SQLException {
        String sql = "UPDATE events SET title=?, date=?, time=?, location=?, capacity=? WHERE id=?";
        try (Connection conn = DBUtils.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, event.getTitle());
            stmt.setDate(2, event.getDate());
            stmt.setTime(3, event.getTime());
            stmt.setString(4, event.getLocation());
            stmt.setInt(5, event.getCapacity());
            stmt.setInt(6, event.getId());
            return stmt.executeUpdate() > 0;
        }
    }
    public Event getEventById(int id) throws SQLException {
        String sql = "SELECT * FROM events WHERE id=?";
        try (Connection conn = DBUtils.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Event(
                            rs.getInt("id"),
                            rs.getString("title"),
                            rs.getDate("date"),
                            rs.getTime("time"),
                            rs.getString("location"),
                            rs.getInt("capacity"));
                }
            }
        }
        return null;
    }
  public boolean deleteEvent(int id) throws SQLException {
        String sql = "DELETE FROM events WHERE id=?";
        try (Connection conn = DBUtils.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        }
    }
