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

}