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
        } catch (SQLException e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"error\": \"Database error: " + e.getMessage() + "\"}");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        User user = (User) session.getAttribute("user");
        String eventIdStr = req.getParameter("eventId");
        if (eventIdStr == null || eventIdStr.isEmpty()) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().print("{\"error\": \"Missing eventId\"}");
            return;
        }
        int eventId = Integer.parseInt(eventIdStr);

        try {
            if (ticketDAO.hasUserBookedEvent(user.getId(), eventId)) {
                resp.setStatus(HttpServletResponse.SC_CONFLICT);
                resp.getWriter().print("{\"error\": \"You have already booked this event\"}");
                return;
            }

            Event event = eventDAO.getEventById(eventId);
            if (event == null) {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                resp.getWriter().print("{\"error\": \"Event not found\"}");
                return;
            }

            if (event.getCapacity() <= 0) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().print("{\"error\": \"Event is full\"}");
                return;
            }

            if (eventDAO.reduceCapacity(eventId)) {
                String ticketId = "TKT-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
                Ticket ticket = new Ticket(ticketId, user.getId(), eventId, null, "BOOKED");
                if (ticketDAO.bookTicket(ticket)) {
                    resp.setStatus(HttpServletResponse.SC_CREATED);
                    resp.getWriter()
                            .print("{\"message\": \"Ticket booked successfully\", \"ticketId\": \"" + ticketId + "\"}");
                } else {
                    resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                }
            } else {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().print("{\"error\": \"Could not book ticket\"}");
            }
        } catch (SQLException e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}
