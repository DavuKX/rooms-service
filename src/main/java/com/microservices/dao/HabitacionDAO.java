package com.microservices.dao;

import com.microservices.config.DatabaseConnection;
import com.microservices.models.Habitacion;
import jakarta.servlet.http.HttpServletRequest;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

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
                Habitacion habitacion = mapHabitacion(rs);
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
                Habitacion habitacion = mapHabitacion(rs);
                habitacion.setServicios(servicioDAO.obtenerServiciosPorIdHabitacion(habitacion.getId()));
                habitacion.setImagenes(imagenDAO.obtenerImagenesPorHabitacion(habitacion.getId()));
                return habitacion;
            }
            return null;
        }
    }

    public List<Habitacion> search(Map<String, String[]> params) throws SQLException, ClassNotFoundException {
        String ciudad = params.get("ciudad") != null ? params.get("ciudad")[0] : null;
        String precioMin = params.get("precioMin") != null ? params.get("precioMin")[0] : null;
        String precioMax = params.get("precioMax") != null ? params.get("precioMax")[0] : null;
        String capacidad = params.get("capacidad") != null ? params.get("capacidad")[0] : null;
        String servicios = params.get("servicios") != null ? params.get("servicios")[0] : null;
        String verificada = params.get("verificada") != null ? params.get("verificada")[0] : null;

        StringBuilder sql = new StringBuilder("SELECT DISTINCT h.* FROM habitacion h LEFT JOIN habitacion_servicios hs ON h.id = hs.habitacion_id WHERE 1=1 ");
        List<Object> parameters = new ArrayList<>();

        if (ciudad != null) {
            sql.append("AND h.ciudad = ? ");
            parameters.add(ciudad);
        }

        if (precioMin != null) {
            sql.append("AND h.precio_noche >= ? ");
            parameters.add(Double.parseDouble(precioMin));
        }

        if (precioMax != null) {
            sql.append("AND h.precio_noche <= ? ");
            parameters.add(Double.parseDouble(precioMax));
        }

        if (capacidad != null) {
            sql.append("AND h.capacidad >= ? ");
            parameters.add(Integer.parseInt(capacidad));
        }

        if (verificada != null) {
            boolean verificado = Boolean.parseBoolean(verificada);
            sql.append("AND h.verificada = ? ");
            parameters.add(verificado);
        }

        if (servicios != null && !servicios.isEmpty()) {
            String[] serviciosArray = servicios.split(",");
            sql.append("AND hs.servicio_id IN (");
            sql.append("?,".repeat(serviciosArray.length));
            sql.setLength(sql.length() - 1); // Remove last comma
            sql.append(") ");

            for (String servicio : serviciosArray) {
                parameters.add(Integer.parseInt(servicio));
            }
        }

        try (Connection connection = DatabaseConnection.getInstance().getConnection()) {
            PreparedStatement stmt = connection.prepareStatement(sql.toString());

            for (int i = 0; i < parameters.size(); i++) {
                stmt.setObject(i + 1, parameters.get(i));
            }

            ResultSet rs = stmt.executeQuery();
            List<Habitacion> habitaciones = new ArrayList<>();
            while (rs.next()) {
                Habitacion habitacion = mapHabitacion(rs);
                habitacion.setServicios(servicioDAO.obtenerServiciosPorIdHabitacion(habitacion.getId()));
                habitacion.setImagenes(imagenDAO.obtenerImagenesPorHabitacion(habitacion.getId()));
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

