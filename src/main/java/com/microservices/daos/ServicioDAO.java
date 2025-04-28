package com.microservices.daos;

import com.microservices.config.DatabaseConnection;
import com.microservices.models.Servicio;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ServicioDAO {
    public List<Servicio> obtenerServiciosPorIdHabitacion(int idHabitacion) throws SQLException, ClassNotFoundException {
        try (Connection connection = DatabaseConnection.getInstance().getConnection()) {
            PreparedStatement stmt = connection.prepareStatement("SELECT * FROM servicio\n" +
                                                                 "INNER JOIN habitacion_servicios ON servicio.id = habitacion_servicios.servicio_id\n" +
                                                                 "WHERE habitacion_id = ?");
            stmt.setInt(1, idHabitacion);
            ResultSet rs = stmt.executeQuery();

            List<Servicio> servicios = new ArrayList<>();

            while (rs.next()) {
                Servicio servicio = mapServicio(rs);
                servicios.add(servicio);
            }
            return servicios;
        }
    }

    private Servicio mapServicio(ResultSet rs) throws SQLException {
        Servicio servicio = new Servicio();
        servicio.setId(rs.getInt("id"));
        servicio.setNombre(rs.getString("nombre"));
        servicio.setTipo(rs.getString("tipo"));
        servicio.setPrecio(rs.getFloat("precio"));
        servicio.setIcon(rs.getString("icon"));
        return servicio;
    }
}
