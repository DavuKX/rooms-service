package com.microservices.dao;

import com.microservices.config.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ImagenDao {
    public List<String> obtenerImagenesPorHabitacion(int idHabitacion) throws SQLException, ClassNotFoundException {
        try (Connection connection = DatabaseConnection.getInstance().getConnection()) {
            PreparedStatement stmt = connection.prepareStatement("SELECT * FROM imagen WHERE habitacion_id = ?");
            stmt.setInt(1, idHabitacion);
            ResultSet rs = stmt.executeQuery();

            List<String> imagenes = new ArrayList<>();

            while (rs.next()) {
                String url = rs.getString("url");
                imagenes.add(url);
            }
            return imagenes;
        }
    }
}
