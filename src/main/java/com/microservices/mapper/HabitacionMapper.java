package com.microservices.mapper;

import com.microservices.dto.HabitacionDTO;
import com.microservices.models.Habitacion;
import com.microservices.models.Servicio;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

public class HabitacionMapper {
    public static HabitacionDTO toDto(Habitacion habitacion) {
        List<Servicio> serviciosIncluidos = habitacion.getServicios().stream()
                .filter(s -> "incluido".equals(s.getTipo()))
                .collect(Collectors.toList());

        List<Servicio> serviciosAdicionales = habitacion.getServicios().stream()
                .filter(s -> "adicional".equals(s.getTipo()))
                .collect(Collectors.toList());

        return new HabitacionDTO(
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
    }

    public static List<HabitacionDTO> toDtoList(List<Habitacion> habitaciones) {
        return habitaciones.stream()
                .map(HabitacionMapper::toDto)
                .collect(Collectors.toList());
    }

    public static Habitacion fromResultSet(ResultSet rs) throws SQLException {
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
