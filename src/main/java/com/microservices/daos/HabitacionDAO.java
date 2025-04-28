package com.microservices.daos;

import com.microservices.config.DatabaseConnection;
import com.microservices.models.Habitacion;
import jakarta.servlet.http.HttpServletRequest;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class HabitacionDAO {

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

                if (habitacion.getServicios() != null) {
                    for (String servicioNombre : habitacion.getServicios()) {
                        PreparedStatement servicioStmt = connection.prepareStatement(
                                "SELECT id FROM servicio WHERE nombre = ?"
                        );
                        servicioStmt.setString(1, servicioNombre);
                        ResultSet servicioRs = servicioStmt.executeQuery();
                        if (servicioRs.next()) {
                            int servicioId = servicioRs.getInt("id");
                            PreparedStatement habServStmt = connection.prepareStatement(
                                    "INSERT INTO habitacion_servicios (habitacion_id, servicio_id) VALUES (?, ?)"
                            );
                            habServStmt.setInt(1, habitacion.getId());
                            habServStmt.setInt(2, servicioId);
                            habServStmt.executeUpdate();
                        }
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
                Habitacion habitacion = mapHabitacion(rs);
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
                return mapHabitacion(rs);
            }
            return null;
        }
    }

    public List<Habitacion> search(HttpServletRequest req) throws SQLException, ClassNotFoundException {
        String ciudad = req.getParameter("ciudad");
        String precioMin = req.getParameter("precio_min");
        String precioMax = req.getParameter("precio_max");
        String capacidad = req.getParameter("capacidad");
        String servicios = req.getParameter("servicios");

        StringBuilder sql = new StringBuilder("SELECT DISTINCT h.* FROM habitacion h LEFT JOIN habitacion_servicios hs ON h.id = hs.habitacion_id WHERE 1=1 ");
        List<Object> params = new ArrayList<>();

        if (ciudad != null) {
            sql.append("AND h.ciudad = ? ");
            params.add(ciudad);
        }
        if (precioMin != null) {
            sql.append("AND h.precio_noche >= ? ");
            params.add(Double.parseDouble(precioMin));
        }
        if (precioMax != null) {
            sql.append("AND h.precio_noche <= ? ");
            params.add(Double.parseDouble(precioMax));
        }
        if (capacidad != null) {
            sql.append("AND h.capacidad >= ? ");
            params.add(Integer.parseInt(capacidad));
        }
        if (servicios != null && !servicios.isEmpty()) {
            String[] serviciosArray = servicios.split(",");
            sql.append("AND hs.servicio_id IN (");
            sql.append("?,".repeat(serviciosArray.length));
            sql.setLength(sql.length() - 1); // Remove last comma
            sql.append(") ");
            for (String servicio : serviciosArray) {
                params.add(Integer.parseInt(servicio));
            }
        }

        try (Connection connection = DatabaseConnection.getInstance().getConnection()) {
            PreparedStatement stmt = connection.prepareStatement(sql.toString());

            for (int i = 0; i < params.size(); i++) {
                stmt.setObject(i + 1, params.get(i));
            }

            ResultSet rs = stmt.executeQuery();
            List<Habitacion> habitaciones = new ArrayList<>();
            while (rs.next()) {
                Habitacion habitacion = mapHabitacion(rs);
                habitaciones.add(habitacion);
            }
            return habitaciones;
        }
    }

    private Habitacion mapHabitacion(ResultSet rs) throws SQLException {
        Habitacion habitacion = new Habitacion();
        habitacion.setId(rs.getInt("id"));
        habitacion.setCiudad(rs.getString("ciudad"));
        habitacion.setDireccion(rs.getString("direccion"));
        habitacion.setCapacidad(rs.getInt("capacidad"));
        habitacion.setPrecioNoche(rs.getDouble("precio_noche"));
        habitacion.setDescripcion(rs.getString("descripcion"));
        habitacion.setPropietarioId(rs.getInt("propietario_id"));
        habitacion.setVerificada(rs.getBoolean("verificada"));
        return habitacion;
    }
}

