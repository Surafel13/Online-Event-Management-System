package com.eventmgmt.servlets;

import com.eventmgmt.dao.EventDAO;
import com.eventmgmt.dao.TicketDAO;
import com.eventmgmt.models.Event;
import com.eventmgmt.models.Ticket;
import com.eventmgmt.models.User;
import com.google.gson.Gson;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.UUID;

@WebServlet({ "/api/bookings", "/api/bookings/*" })
public class BookingServlet extends HttpServlet {
    private TicketDAO ticketDAO = new TicketDAO();

private EventDAO eventDAO = new EventDAO();
private Gson gson = new Gson();

@Override
protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    HttpSession session = req.getSession(false);
    if (session == null || session.getAttribute("user") == null) {
        resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        return;
    }

    User user = (User) session.getAttribute("user");
    resp.setContentType("application/json");
    PrintWriter out = resp.getWriter();

    try {
        Object tickets;
        if ("ADMIN".equals(user.getRole())) {
            tickets = ticketDAO.getAllDetailedTickets();
        } else {
            tickets = ticketDAO.getDetailedTicketsByUserId(user.getId());
        }
        out.print(gson.toJson(tickets));

}
