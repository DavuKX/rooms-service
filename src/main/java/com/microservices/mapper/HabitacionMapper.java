package com.microservices.mapper;

import com.microservices.dto.HabitacionDTO;
import com.microservices.models.Habitacion;
import com.microservices.models.Servicio;

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
}
