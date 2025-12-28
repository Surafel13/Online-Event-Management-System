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
public class BookingServlet extends HttpServlet {}
