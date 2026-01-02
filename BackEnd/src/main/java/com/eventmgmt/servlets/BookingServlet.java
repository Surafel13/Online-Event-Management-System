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
        resp.setContentType("application/json");
        PrintWriter out = resp.getWriter();

        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            out.print("{\"error\": \"Unauthorized: Please login\"}");
            return;
        }

        User user = (User) session.getAttribute("user");
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
            out.print("{\"error\": \"Database error: " + e.getMessage().replace("\"", "\\\"") + "\"}");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        PrintWriter out = resp.getWriter();

        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            out.print("{\"error\": \"Unauthorized: Please login\"}");
            return;
        }

        User user = (User) session.getAttribute("user");
        String eventIdStr = req.getParameter("eventId");
        if (eventIdStr == null || eventIdStr.isEmpty()) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print("{\"error\": \"Missing eventId\"}");
            return;
        }

        try {
            int eventId = Integer.parseInt(eventIdStr);

            if (ticketDAO.hasUserBookedEvent(user.getId(), eventId)) {
                resp.setStatus(HttpServletResponse.SC_CONFLICT);
                out.print("{\"error\": \"You have already booked this event\"}");
                return;
            }

            Event event = eventDAO.getEventById(eventId);
            if (event == null) {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                out.print("{\"error\": \"Event not found\"}");
                return;
            }

            if (event.getCapacity() <= 0) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("{\"error\": \"Event is full\"}");
                return;
            }

            if (eventDAO.reduceCapacity(eventId)) {
                String ticketId = "TKT-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
                Ticket ticket = new Ticket(ticketId, user.getId(), eventId, null, "BOOKED");
                if (ticketDAO.bookTicket(ticket)) {
                    resp.setStatus(HttpServletResponse.SC_CREATED);
                    out.print("{\"message\": \"Ticket booked successfully\", \"ticketId\": \"" + ticketId + "\"}");
                } else {
                    resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    out.print("{\"error\": \"Failed to create ticket in database\"}");
                }
            } else {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("{\"error\": \"Could not reduce capacity, maybe event is full\"}");
            }
        } catch (NumberFormatException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print("{\"error\": \"Invalid eventId format\"}");
        } catch (SQLException e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"error\": \"Database error: " + e.getMessage() + "\"}");
        }
    }
}
