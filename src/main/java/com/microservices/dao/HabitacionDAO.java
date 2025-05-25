package com.microservices.dao;

import com.microservices.config.DatabaseConnection;
import com.microservices.dao.builder.HabitacionQueryBuilder;
import com.microservices.mapper.HabitacionMapper;
import com.microservices.models.Habitacion;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HabitacionDAO {
    private final ServicioDAO servicioDAO;
    private final ImagenDao imagenDAO;

    public HabitacionDAO() {
        this.servicioDAO = new ServicioDAO();
        this.imagenDAO = new ImagenDao();
    }

    public Habitacion save(Habitacion habitacion) throws SQLException, ClassNotFoundException {
        try (Connection connection = DatabaseConnection.getInstance().getConnection()) {
            connection.setAutoCommit(false);

            try {
                PreparedStatement stmt = connection.prepareStatement(
                        "INSERT INTO habitacion (ciudad, direccion, capacidad, precio_noche, descripcion, propietario_id, verificada) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?) RETURNING id"
                );
                stmt.setString(1, habitacion.getCiudad());
                stmt.setString(2, habitacion.getDireccion());
                stmt.setInt(3, habitacion.getCapacidad());
                stmt.setDouble(4, habitacion.getPrecioNoche());
                stmt.setString(5, habitacion.getDescripcion());
                stmt.setInt(6, habitacion.getPropietarioId());
                stmt.setBoolean(7, habitacion.isVerificada());

                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    habitacion.setId(rs.getInt("id"));
                }

                if (habitacion.getImagenes() != null) {
                    for (String url : habitacion.getImagenes()) {
                        PreparedStatement imgStmt = connection.prepareStatement(
                                "INSERT INTO imagen (habitacion_id, url) VALUES (?, ?)"
                        );
                        imgStmt.setInt(1, habitacion.getId());
                        imgStmt.setString(2, url);
                        imgStmt.executeUpdate();
                    }
                }

                connection.commit();
                return habitacion;

            } catch (SQLException e) {
                connection.rollback();
                throw e;
            }
        }
    }

    public List<Habitacion> getAll() throws SQLException, ClassNotFoundException {
        try (Connection connection = DatabaseConnection.getInstance().getConnection()) {
            PreparedStatement stmt = connection.prepareStatement("SELECT * FROM habitacion");
            ResultSet rs = stmt.executeQuery();

            List<Habitacion> habitaciones = new ArrayList<>();
            while (rs.next()) {
                Habitacion habitacion = HabitacionMapper.fromResultSet(rs);
                habitacion.setServicios(servicioDAO.obtenerServiciosPorIdHabitacion(habitacion.getId()));
                habitacion.setImagenes(imagenDAO.obtenerImagenesPorHabitacion(habitacion.getId()));
                habitaciones.add(habitacion);
            }
            return habitaciones;
        }
    }

    public Habitacion getById(int id) throws SQLException, ClassNotFoundException {
        try (Connection connection = DatabaseConnection.getInstance().getConnection()) {
            PreparedStatement stmt = connection.prepareStatement("SELECT * FROM habitacion WHERE id = ?");
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Habitacion habitacion = HabitacionMapper.fromResultSet(rs);
                habitacion.setServicios(servicioDAO.obtenerServiciosPorIdHabitacion(habitacion.getId()));
                habitacion.setImagenes(imagenDAO.obtenerImagenesPorHabitacion(habitacion.getId()));
                return habitacion;
            }
            return null;
        }
    }

    public List<Habitacion> search(Map<String, String[]> params) throws SQLException, ClassNotFoundException {
        HabitacionQueryBuilder builder = HabitacionQueryBuilder.fromParams(params);

        try (Connection connection = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = connection.prepareStatement(builder.getQuery())) {

            List<Object> parameters = builder.getParameters();
            for (int i = 0; i < parameters.size(); i++) {
                stmt.setObject(i + 1, parameters.get(i));
            }

            ResultSet rs = stmt.executeQuery();
            List<Habitacion> habitaciones = new ArrayList<>();
            while (rs.next()) {
                Habitacion habitacion = HabitacionMapper.fromResultSet(rs);
                habitacion.setServicios(servicioDAO.obtenerServiciosPorIdHabitacion(habitacion.getId()));
                habitacion.setImagenes(imagenDAO.obtenerImagenesPorHabitacion(habitacion.getId()));
                habitaciones.add(habitacion);
            }
            return habitaciones;
        }
    }

    public boolean update(Habitacion habitacion) throws SQLException, ClassNotFoundException {
        try (Connection connection = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = connection.prepareStatement(
                     "UPDATE habitacion SET ciudad = ?, direccion = ?, capacidad = ?, precio_noche = ?, verificada = ? WHERE id = ?"
             )) {

            stmt.setString(1, habitacion.getCiudad());
            stmt.setString(2, habitacion.getDireccion());
            stmt.setInt(3, habitacion.getCapacidad());
            stmt.setDouble(4, habitacion.getPrecioNoche());
            stmt.setBoolean(5, habitacion.isVerificada());
            stmt.setInt(6, habitacion.getId());

            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        }
    }
}

