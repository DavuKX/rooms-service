package com.microservices.repository;

import com.microservices.dao.HabitacionDAO;
import com.microservices.models.Habitacion;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class HabitacionRepository implements IHabitacionRepository {
    private final HabitacionDAO habitacionDAO = new HabitacionDAO();

    @Override
    public Habitacion save(Habitacion habitacion) throws SQLException, ClassNotFoundException {
        return habitacionDAO.save(habitacion);
    }

    @Override
    public Habitacion getById(int id) throws SQLException, ClassNotFoundException {
        return habitacionDAO.getById(id);
    }

    @Override
    public List<Habitacion> getAll() throws SQLException, ClassNotFoundException {
        return habitacionDAO.getAll();
    }

    @Override
    public List<Habitacion> search(Map<String, String[]> params) throws SQLException, ClassNotFoundException {
        return habitacionDAO.search(params);
    }

    @Override
    public boolean update(Habitacion habitacion) throws SQLException, ClassNotFoundException {
        return habitacionDAO.update(habitacion);
    }
}
