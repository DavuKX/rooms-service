package com.microservices.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.microservices.daos.HabitacionDAO;
import com.microservices.dtos.HabitacionDTO;
import com.microservices.models.Habitacion;
import com.microservices.models.Servicio;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@WebServlet("/api/rooms/*")
public class HabitacionController extends HttpServlet {
    private ObjectMapper mapper;
    private HabitacionDAO habitacionDAO;

    @Override
    public void init() throws ServletException {
        mapper = new ObjectMapper();
        habitacionDAO = new HabitacionDAO();
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
            Habitacion savedHabitacion = habitacionDAO.save(habitacion);

            resp.setStatus(HttpServletResponse.SC_CREATED);
            resp.setContentType("application/json");
            mapper.writeValue(resp.getWriter(), savedHabitacion);
        } catch (SQLException | ClassNotFoundException e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        setCorsHeaders(resp);

        String pathInfo = req.getPathInfo();

        try {
            if (pathInfo == null || pathInfo.equals("/")) {
                // GET /api/rooms
                List<Habitacion> habitaciones = habitacionDAO.getAll();
                List<HabitacionDTO> habitacionDTOS = new ArrayList<>();

                for (Habitacion habitacion : habitaciones) {
                    List<Servicio> serviciosIncluidos = habitacion.getServicios().stream().filter(servicio -> Objects.equals(servicio.getTipo(), "incluido")).toList();
                    List<Servicio> serviciosAdicionales = habitacion.getServicios().stream().filter(servicio -> Objects.equals(servicio.getTipo(), "adicional")).toList();
                    HabitacionDTO dto = new HabitacionDTO(
                            habitacion.getId(),
                            habitacion.getCiudad(),
                            habitacion.getDireccion(),
                            habitacion.getCapacidad(),
                            (float) habitacion.getPrecioNoche(),
                            habitacion.getDescripcion(),
                            serviciosIncluidos,
                            serviciosAdicionales,
                            habitacion.getImagenes(),
                            habitacion.isVerificada()
                    );

                    habitacionDTOS.add(dto);
                }
                resp.setContentType("application/json");
                mapper.writeValue(resp.getWriter(), habitacionDTOS);

            } else if (pathInfo.startsWith("/search")) {
                // GET /api/rooms/search?ciudad=X&precio_min=Y&precio_max=Z&capacidad=A&servicios=1,2,3
                List<Habitacion> habitaciones = habitacionDAO.search(req);
                List<HabitacionDTO> habitacionDTOS = new ArrayList<>();
                for (Habitacion habitacion : habitaciones) {
                    List<Servicio> serviciosIncluidos = habitacion.getServicios().stream().filter(servicio -> Objects.equals(servicio.getTipo(), "incluido")).toList();
                    List<Servicio> serviciosAdicionales = habitacion.getServicios().stream().filter(servicio -> Objects.equals(servicio.getTipo(), "adicional")).toList();
                    HabitacionDTO dto = new HabitacionDTO(
                            habitacion.getId(),
                            habitacion.getCiudad(),
                            habitacion.getDireccion(),
                            habitacion.getCapacidad(),
                            (float) habitacion.getPrecioNoche(),
                            habitacion.getDescripcion(),
                            serviciosIncluidos,
                            serviciosAdicionales,
                            habitacion.getImagenes(),
                            habitacion.isVerificada()
                    );

                    habitacionDTOS.add(dto);
                }
                resp.setContentType("application/json");
                mapper.writeValue(resp.getWriter(), habitacionDTOS);
            } else {
                // GET /api/rooms/{id}
                int id = Integer.parseInt(pathInfo.substring(1));
                Habitacion habitacion = habitacionDAO.getById(id);
                if (habitacion != null) {
                    List<Servicio> serviciosIncluidos = habitacion.getServicios().stream().filter(servicio -> Objects.equals(servicio.getTipo(), "incluido")).toList();
                    List<Servicio> serviciosAdicionales = habitacion.getServicios().stream().filter(servicio -> Objects.equals(servicio.getTipo(), "adicional")).toList();
                    HabitacionDTO dto = new HabitacionDTO(
                            habitacion.getId(),
                            habitacion.getCiudad(),
                            habitacion.getDireccion(),
                            habitacion.getCapacidad(),
                            (float) habitacion.getPrecioNoche(),
                            habitacion.getDescripcion(),
                            serviciosIncluidos,
                            serviciosAdicionales,
                            habitacion.getImagenes(),
                            habitacion.isVerificada()
                    );

                    resp.setContentType("application/json");
                    mapper.writeValue(resp.getWriter(), dto);
                } else {
                    resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    resp.getWriter().write("{\"error\": \"Habitacion no encontrada\"}");
                }
            }
        } catch (SQLException | ClassNotFoundException e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        setCorsHeaders(resp);
        resp.setContentType("application/json");

        try {
            String pathInfo = req.getPathInfo();

            if (pathInfo == null || pathInfo.equals("/")) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write("{\"error\": \"Se requiere ID de habitación\"}");
                return;
            }

            int id = Integer.parseInt(pathInfo.substring(1));
            HabitacionDTO updateData = mapper.readValue(req.getInputStream(), HabitacionDTO.class);

            Habitacion existing = habitacionDAO.getById(id);
            if (existing == null) {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                resp.getWriter().write("{\"error\": \"Habitación no encontrada\"}");
                return;
            }

            if (updateData.getCiudad() != null) {
                existing.setCiudad(updateData.getCiudad());
            }
            if (updateData.getDireccion() != null) {
                existing.setDireccion(updateData.getDireccion());
            }
            if (updateData.getCapacidad() > 0) {
                existing.setCapacidad(updateData.getCapacidad());
            }
            if (updateData.getPrecioNoche() > 0) {
                existing.setPrecioNoche(updateData.getPrecioNoche());
            }
            existing.setVerificada(updateData.isVerificada());

            boolean success = habitacionDAO.update(existing);

            if (success) {
                Habitacion updated = habitacionDAO.getById(id);
                HabitacionDTO responseDto = new HabitacionDTO(
                        updated.getId(),
                        updated.getCiudad(),
                        updated.getDireccion(),
                        updated.getCapacidad(),
                        (float) updated.getPrecioNoche(),
                        updated.getDescripcion(),
                        updated.getServicios().stream()
                                .filter(s -> "incluido".equals(s.getTipo()))
                                .collect(Collectors.toList()),
                        updated.getServicios().stream()
                                .filter(s -> "adicional".equals(s.getTipo()))
                                .collect(Collectors.toList()),
                        updated.getImagenes(),
                        updated.isVerificada()
                );

                resp.setStatus(HttpServletResponse.SC_OK);
                mapper.writeValue(resp.getWriter(), responseDto);
            } else {
                resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                resp.getWriter().write("{\"error\": \"No se pudo actualizar la habitación\"}");
            }
        } catch (NumberFormatException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("{\"error\": \"ID de habitación inválido\"}");
        } catch (SQLException | ClassNotFoundException e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }
}

