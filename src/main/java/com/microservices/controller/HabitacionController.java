package com.microservices.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.microservices.dto.HabitacionDTO;
import com.microservices.models.Habitacion;
import com.microservices.repository.HabitacionRepository;
import com.microservices.service.HabitacionService;
import com.microservices.strategy.GetAllHandlerStrategy;
import com.microservices.strategy.GetByIdHandlerStrategy;
import com.microservices.strategy.IRequestHandlerStrategy;
import com.microservices.strategy.SearchHandlerStrategy;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@WebServlet("/api/rooms/*")
public class HabitacionController extends HttpServlet {
    private ObjectMapper mapper;
    private HabitacionService service;
    private final List<IRequestHandlerStrategy> handlers = new ArrayList<>();

    @Override
    public void init() throws ServletException {
        mapper = new ObjectMapper();
        HabitacionRepository repository = new HabitacionRepository();
        service = new HabitacionService(repository);

        handlers.add(new GetAllHandlerStrategy(service, mapper));
        handlers.add(new GetByIdHandlerStrategy(service, mapper));
        handlers.add(new SearchHandlerStrategy(service, mapper));
    }

    private void setCorsHeaders(HttpServletResponse resp) {
        resp.setHeader("Access-Control-Allow-Origin", "*");
        resp.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        resp.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization");
        resp.setHeader("Access-Control-Allow-Credentials", "true");
    }

    @Override
    protected void doOptions(HttpServletRequest req, HttpServletResponse resp) {
        setCorsHeaders(resp);
        resp.setStatus(HttpServletResponse.SC_OK);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        setCorsHeaders(resp);
        try {
            Habitacion habitacion = mapper.readValue(req.getInputStream(), Habitacion.class);
            HabitacionDTO dto = service.save(habitacion);
            resp.setStatus(HttpServletResponse.SC_CREATED);
            resp.setContentType("application/json");
            mapper.writeValue(resp.getWriter(), dto);
        } catch (Exception e) {
            resp.setStatus(500);
            resp.getWriter().write("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        setCorsHeaders(resp);
        String pathInfo = req.getPathInfo();

        try {
            resp.setContentType("application/json");

            for (IRequestHandlerStrategy handler : handlers) {
                if (handler.canHandle(pathInfo)) {
                    handler.handle(req, resp);
                    return;
                }
            }
        } catch (Exception e) {
            resp.setStatus(500);
            resp.getWriter().write("{\"error\":\"" + e.getMessage() + "\",\"stackTrace\":\"" + Arrays.toString(e.getStackTrace()) + "\"}");
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        setCorsHeaders(resp);
        try {
            String pathInfo = req.getPathInfo();

            if (pathInfo == null || pathInfo.equals("/")) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write("{\"error\":\"ID de habitación requerido\"}");
                return;
            }

            int id = Integer.parseInt(pathInfo.substring(1));
            HabitacionDTO dto = mapper.readValue(req.getInputStream(), HabitacionDTO.class);
            boolean updated = service.updateHabitacion(id, dto);

            if (updated) {
                mapper.writeValue(resp.getWriter(), service.getHabitacionById(id));
            } else {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                resp.getWriter().write("{\"error\": \"Habitación no encontrada\"}");
            }
        } catch (Exception e) {
            resp.setStatus(500);
            resp.getWriter().write("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }
}
