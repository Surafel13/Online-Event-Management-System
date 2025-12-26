package com.eventmgmt.dao;

import com.eventmgmt.models.User;
import com.eventmgmt.utils.DBUtils;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
public class UserDAO {
    public boolean registerUser(User user) throws SQLException {
        String sql = "INSERT INTO users (name, email, password, role) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBUtils.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, user.getName());
            stmt.setString(2, user.getEmail());
            stmt.setString(3, user.getPassword());
            stmt.setString(4, user.getRole());
            return stmt.executeUpdate() > 0;
        }
    }}
    public User login(String email, String password) throws SQLException {
    String sql = "SELECT * FROM users WHERE email = ? AND password = ?";
    try (Connection conn = DBUtils.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
        stmt.setString(1, email);
        stmt.setString(2, password);
        try (ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return new User(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getString("password"),
                        rs.getString("role"));
            }
        }
    }
    return null;
}
}
