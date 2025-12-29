package com.eventmgmt.servlets;

import com.eventmgmt.dao.UserDAO;
import com.eventmgmt.models.User;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.List;

@WebServlet("/api/admin/users")
public class AdminServlet extends HttpServlet {
    private UserDAO userDAO = new UserDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null) {
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        User currentUser = (User) session.getAttribute("user");
        if (currentUser == null || !"ADMIN".equals(currentUser.getRole())) {
            resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        resp.setContentType("application/json");
        PrintWriter out = resp.getWriter();

        try {
            List<User> users = userDAO.getAllUsers();
            out.print("[");
            for (int i = 0; i < users.size(); i++) {
                User u = users.get(i);
                out.print(String.format("{\"id\": %d, \"name\": \"%s\", \"email\": \"%s\", \"role\": \"%s\"}",
                        u.getId(), u.getName(), u.getEmail(), u.getRole()));
                if (i < users.size() - 1)
                    out.print(",");
            }
            out.print("]");
        } catch (SQLException e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}
