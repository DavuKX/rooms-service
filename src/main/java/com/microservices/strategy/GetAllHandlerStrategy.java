package com.microservices.strategy;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.microservices.service.HabitacionService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;

public class GetAllHandlerStrategy implements IRequestHandlerStrategy {
    private final HabitacionService service;
    private final ObjectMapper mapper;

    public GetAllHandlerStrategy(HabitacionService service, ObjectMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    @Override
    public boolean canHandle(String pathInfo) {
        return pathInfo == null || pathInfo.equals("/");
    }

    @Override
    public void handle(HttpServletRequest req, HttpServletResponse resp) throws IOException, SQLException, ClassNotFoundException {
        mapper.writeValue(resp.getWriter(), service.getAll());
    }
}
