package com.microservices.service;

import com.microservices.dto.HabitacionDTO;
import com.microservices.mapper.HabitacionMapper;
import com.microservices.models.Habitacion;
import com.microservices.repository.HabitacionRepository;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class HabitacionService {
    private final HabitacionRepository repository;

    public HabitacionService(HabitacionRepository repository) {
        this.repository = repository;
    }

    public HabitacionDTO getHabitacionById(int id) throws SQLException, ClassNotFoundException {
        Habitacion habitacion = repository.getById(id);
        return habitacion != null ? HabitacionMapper.toDto(habitacion) : null;
    }

    public List<HabitacionDTO> getAll() throws SQLException, ClassNotFoundException {
        List<Habitacion> habitaciones = repository.getAll();
        return HabitacionMapper.toDtoList(habitaciones);
    }

    public HabitacionDTO save(Habitacion habitacion) throws SQLException, ClassNotFoundException {
        Habitacion saved = repository.save(habitacion);
        return HabitacionMapper.toDto(saved);
    }

    public boolean updateHabitacion(int id, HabitacionDTO dto) throws SQLException, ClassNotFoundException {
        Habitacion existing = repository.getById(id);
        if (existing == null) return false;

        if (dto.getCiudad() != null) existing.setCiudad(dto.getCiudad());
        if (dto.getDireccion() != null) existing.setDireccion(dto.getDireccion());
        if (dto.getCapacidad() > 0) existing.setCapacidad(dto.getCapacidad());
        if (dto.getPrecioNoche() > 0) existing.setPrecioNoche(dto.getPrecioNoche());
        existing.setVerificada(dto.isVerificada());

        return repository.update(existing);
    }

    public List<HabitacionDTO> search(Map<String, String[]> params) throws SQLException, ClassNotFoundException {
        List<Habitacion> result = repository.search(params);
        return HabitacionMapper.toDtoList(result);
    }
}
