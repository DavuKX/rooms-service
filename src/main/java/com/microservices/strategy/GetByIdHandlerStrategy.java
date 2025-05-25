package com.microservices.strategy;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.microservices.dto.HabitacionDTO;
import com.microservices.service.HabitacionService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;

public class GetByIdHandlerStrategy implements IRequestHandlerStrategy {
    private final HabitacionService service;
    private final ObjectMapper mapper;

    public GetByIdHandlerStrategy(HabitacionService service, ObjectMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    @Override
    public boolean canHandle(String pathInfo) {
        return pathInfo != null && pathInfo.matches("^/\\d+$");
    }

    @Override
    public void handle(HttpServletRequest req, HttpServletResponse resp) throws IOException, SQLException, ClassNotFoundException {
        int id = Integer.parseInt(req.getPathInfo().substring(1));
        HabitacionDTO dto = service.getHabitacionById(id);

        if (dto != null) {
            mapper.writeValue(resp.getWriter(), dto);
        } else {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            resp.getWriter().write("{\"error\": \"Habitación no encontrada\"}");
        }
    }
}
