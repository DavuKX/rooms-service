package com.microservices.repository;

import com.microservices.models.Habitacion;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public interface IHabitacionRepository {
    Habitacion save(Habitacion habitacion) throws SQLException, ClassNotFoundException;
    Habitacion getById(int id) throws SQLException, ClassNotFoundException;
    List<Habitacion> getAll() throws SQLException, ClassNotFoundException;
    List<Habitacion> search(Map<String, String[]> params) throws SQLException, ClassNotFoundException;
    boolean update(Habitacion habitacion) throws SQLException, ClassNotFoundException;
}
