package com.eventmgmt.dao;

import com.eventmgmt.models.Ticket;
import com.eventmgmt.models.TicketDetail;
import com.eventmgmt.utils.DBUtils;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TicketDAO {

    public boolean bookTicket(Ticket ticket) throws SQLException {
        String sql = "INSERT INTO tickets (id, user_id, event_id, status) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBUtils.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, ticket.getId());
            stmt.setInt(2, ticket.getUserId());
            stmt.setInt(3, ticket.getEventId());
            stmt.setString(4, ticket.getStatus());
            return stmt.executeUpdate() > 0;
        }
    }

    public List<TicketDetail> getDetailedTicketsByUserId(int userId) throws SQLException {
        List<TicketDetail> list = new ArrayList<>();
        String sql = "SELECT t.id, u.name, u.email, e.title, e.location, t.booking_date, t.status " +
                "FROM tickets t " +
                "JOIN users u ON t.user_id = u.id " +
                "JOIN events e ON t.event_id = e.id " +
                "WHERE t.user_id = ?";
        try (Connection conn = DBUtils.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(new TicketDetail(
                            rs.getString("id"),
                            rs.getString("name"),
                            rs.getString("email"),
                            rs.getString("title"),
                            rs.getString("location"),
                            rs.getTimestamp("booking_date"),
                            rs.getString("status")));
                }
            }
        }
        return list;
    }

    public List<TicketDetail> getAllDetailedTickets() throws SQLException {
        List<TicketDetail> list = new ArrayList<>();
        String sql = "SELECT t.id, u.name, u.email, e.title, e.location, t.booking_date, t.status " +
                "FROM tickets t " +
                "JOIN users u ON t.user_id = u.id " +
                "JOIN events e ON t.event_id = e.id";
        try (Connection conn = DBUtils.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new TicketDetail(
                        rs.getString("id"),
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getString("title"),
                        rs.getString("location"),
                        rs.getTimestamp("booking_date"),
                        rs.getString("status")));
            }
        }
        return list;
    }

    public List<Ticket> getTicketsByUserId(int userId) throws SQLException {
        List<Ticket> tickets = new ArrayList<>();
        String sql = "SELECT * FROM tickets WHERE user_id = ?";
        try (Connection conn = DBUtils.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    tickets.add(new Ticket(
                            rs.getString("id"),
                            rs.getInt("user_id"),
                            rs.getInt("event_id"),
                            rs.getTimestamp("booking_date"),
                            rs.getString("status")));
                }
            }
        }
        return tickets;
    }

    public List<Ticket> getAllBookings() throws SQLException {
        List<Ticket> tickets = new ArrayList<>();
        String sql = "SELECT * FROM tickets";
        try (Connection conn = DBUtils.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                tickets.add(new Ticket(
                        rs.getString("id"),
                        rs.getInt("user_id"),
                        rs.getInt("event_id"),
                        rs.getTimestamp("booking_date"),
                        rs.getString("status")));
            }
        }
        return tickets;
    }

    public boolean hasUserBookedEvent(int userId, int eventId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM tickets WHERE user_id = ? AND event_id = ?";
        try (Connection conn = DBUtils.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setInt(2, eventId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }
}
