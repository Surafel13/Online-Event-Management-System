package com.eventmgmt.servlets;

import com.eventmgmt.dao.EventDAO;
import com.eventmgmt.models.Event;
import com.eventmgmt.models.User;
import com.google.gson.Gson;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Time;
import java.util.List;

@WebServlet({ "/api/events", "/api/events/*" })
public class EventServlet extends HttpServlet {
    private EventDAO eventDAO = new EventDAO();
    private Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        PrintWriter out = resp.getWriter();

        try {
            List<Event> events = eventDAO.getAllEvents();
            out.print(gson.toJson(events));
        } catch (SQLException e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"error\": \"Database error\"}");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        PrintWriter out = resp.getWriter();

        if (!isAdmin(req)) {
            resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
            out.print("{\"error\": \"Admin access required\"}");
            return;
        }

        try {
            Event event;
            String contentType = req.getContentType();

            if (contentType != null && contentType.contains("application/json")) {
                // Handle JSON input
                event = gson.fromJson(req.getReader(), Event.class);
            } else {
                // Handle Form input
                String title = req.getParameter("title");
                String dateStr = req.getParameter("date");
                String timeStr = req.getParameter("time");
                String location = req.getParameter("location");
                String capacityStr = req.getParameter("capacity");

                if (title == null || dateStr == null || timeStr == null || location == null || capacityStr == null) {
                    resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    out.print("{\"error\": \"Missing required parameters\"}");
                    return;
                }

                Date date = Date.valueOf(dateStr);
                Time time = Time.valueOf(timeStr + (timeStr.length() == 5 ? ":00" : ""));
                int capacity = Integer.parseInt(capacityStr);
                event = new Event(0, title, date, time, location, capacity);
            }

            if (eventDAO.createEvent(event)) {
                resp.setStatus(HttpServletResponse.SC_CREATED);
                out.print("{\"message\": \"Event created successfully\"}");
            } else {
                resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                out.print("{\"error\": \"Could not create event\"}");
            }
        } catch (IllegalArgumentException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print("{\"error\": \"Invalid date, time, or capacity format. Expected YYYY-MM-DD and HH:mm.\"}");
        } catch (SQLException e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"error\": \"Database error: " + e.getMessage() + "\"}");
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"error\": \"Unexpected error: " + e.getMessage() + "\"}");
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (!isAdmin(req)) {
            resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        String pathInfo = req.getPathInfo();
        if (pathInfo == null || pathInfo.equals("/")) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().print("{\"error\": \"Missing event ID\"}");
            return;
        }

        int id = Integer.parseInt(pathInfo.substring(1));
        String title = req.getParameter("title");
        Date date = Date.valueOf(req.getParameter("date"));
        Time time = Time.valueOf(req.getParameter("time") + ":00");
        String location = req.getParameter("location");
        int capacity = Integer.parseInt(req.getParameter("capacity"));

        Event event = new Event(id, title, date, time, location, capacity);
        try {
            if (eventDAO.updateEvent(event)) {
                resp.getWriter().print("{\"message\": \"Event updated successfully\"}");
            } else {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (SQLException e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (!isAdmin(req)) {
            resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        String pathInfo = req.getPathInfo();
        if (pathInfo == null || pathInfo.equals("/")) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().print("{\"error\": \"Missing event ID\"}");
            return;
        }

        int id = Integer.parseInt(pathInfo.substring(1));
        try {
            if (eventDAO.deleteEvent(id)) {
                resp.getWriter().print("{\"message\": \"Event deleted successfully\"}");
            } else {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (SQLException e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    private boolean isAdmin(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        if (session == null)
            return false;
        User user = (User) session.getAttribute("user");
        return user != null && "ADMIN".equals(user.getRole());
    }
}
