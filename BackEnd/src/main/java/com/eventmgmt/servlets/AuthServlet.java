package com.eventmgmt.servlets;

import com.eventmgmt.dao.UserDAO;
import com.eventmgmt.models.User;
import com.google.gson.Gson;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;

@WebServlet({ "/api/auth", "/api/auth/*" })

public class AuthServlet extends HttpServlet {

    private UserDAO userDAO = new UserDAO();
    private Gson gson = new Gson();
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String pathInfo = req.getPathInfo();
        resp.setContentType("application/json");
        PrintWriter out = resp.getWriter();

        if ("/check".equals(pathInfo)) {
            HttpSession session = req.getSession(false);
            if (session != null && session.getAttribute("user") != null) {
                User user = (User) session.getAttribute("user");
                out.print("{\"role\": \"" + user.getRole() + "\"}");
            } else {
                resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                out.print("{\"error\": \"Not logged in\"}");
            }
        }
    }
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String pathInfo = req.getPathInfo();
        resp.setContentType("application/json");
        PrintWriter out = resp.getWriter();

    if ("/register".equals(pathInfo)) {

        User user;
        String contentType = req.getContentType();

        if (contentType != null && contentType.contains("application/json")) {
            user = gson.fromJson(req.getReader(), User.class);
            if (user.getRole() == null)
                user.setRole("USER");
        } else {
            String name = req.getParameter("name");
            String email = req.getParameter("email");
            String password = req.getParameter("password");
            String role = req.getParameter("role");
            if (role == null)
                role = "USER";
            user = new User(0, name, email, password, role);
        }

        try {
            if (user.getName() == null || user.getEmail() == null || user.getPassword() == null) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("{\"error\": \"Name, email, and password are required\"}");
                return;
            }

            if (userDAO.registerUser(user)) {
                resp.setStatus(HttpServletResponse.SC_CREATED);
                out.print("{\"message\": \"User registered successfully\"}");
            } else {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("{\"error\": \"Registration failed\"}");
            }
        } catch (SQLException e) {
            resp.setStatus(HttpServletResponse.SC_CONFLICT);
            out.print("{\"error\": \"Email already exists or database error: " + e.getMessage() + "\"}");
        }

    } else if ("/login".equals(pathInfo)) {

        String email, password;
        String contentType = req.getContentType();

        if (contentType != null && contentType.contains("application/json")) {
            User loginData = gson.fromJson(req.getReader(), User.class);
            email = loginData.getEmail();
            password = loginData.getPassword();
        } else {
            email = req.getParameter("email");
            password = req.getParameter("password");
        }

        try {
            if (email == null || password == null) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("{\"error\": \"Email and password are required\"}");
                return;
            }

            User user = userDAO.login(email, password);
            if (user != null) {
                HttpSession session = req.getSession();
                session.setAttribute("user", user);
                out.print("{\"message\": \"Login successful\", \"role\": \"" + user.getRole() + "\"}");
            } else {
                resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                out.print("{\"error\": \"Invalid email or password\"}");
            }
        } catch (SQLException e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"error\": \"Database error: " + e.getMessage() + "\"}");
        }

    } else if ("/logout".equals(pathInfo)) {
        HttpSession session = req.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        out.print("{\"message\": \"Logout successful\"}");
    }
  }
}