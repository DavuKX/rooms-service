package com.microservices.dao.builder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HabitacionQueryBuilder {
    private final StringBuilder sql;
    private final List<Object> parameters;

    public HabitacionQueryBuilder() {
        this.sql = new StringBuilder("SELECT DISTINCT h.* FROM habitacion h LEFT JOIN habitacion_servicios hs ON h.id = hs.habitacion_id WHERE 1=1 ");
        this.parameters = new ArrayList<>();
    }

    public HabitacionQueryBuilder filterByCiudad(String ciudad) {
        if (ciudad != null) {
            sql.append("AND h.ciudad = ? ");
            parameters.add(ciudad);
        }
        return this;
    }

    public HabitacionQueryBuilder filterByPrecio(String min, String max) {
        if (min != null) {
            sql.append("AND h.precio_noche >= ? ");
            parameters.add(Double.parseDouble(min));
        }
        if (max != null) {
            sql.append("AND h.precio_noche <= ? ");
            parameters.add(Double.parseDouble(max));
        }
        return this;
    }

    public HabitacionQueryBuilder filterByCapacidad(String capacidad) {
        if (capacidad != null) {
            sql.append("AND h.capacidad >= ? ");
            parameters.add(Integer.parseInt(capacidad));
        }
        return this;
    }

    public HabitacionQueryBuilder filterByVerificada(String verificada) {
        if (verificada != null) {
            sql.append("AND h.verificada = ? ");
            parameters.add(Boolean.parseBoolean(verificada));
        }
        return this;
    }

    public HabitacionQueryBuilder filterByServicios(String servicios) {
        if (servicios != null && !servicios.isEmpty()) {
            String[] ids = servicios.split(",");
            sql.append("AND hs.servicio_id IN (");
            sql.append("?,".repeat(ids.length));
            sql.setLength(sql.length() - 1);
            sql.append(") ");
            for (String id : ids) {
                parameters.add(Integer.parseInt(id));
            }
        }
        return this;
    }

    public String getQuery() {
        return sql.toString();
    }

    public List<Object> getParameters() {
        return parameters;
    }

    public static HabitacionQueryBuilder fromParams(Map<String, String[]> params) {
        return new HabitacionQueryBuilder()
                .filterByCiudad(getParam(params, "ciudad"))
                .filterByPrecio(getParam(params, "precioMin"), getParam(params, "precioMax"))
                .filterByCapacidad(getParam(params, "capacidad"))
                .filterByVerificada(getParam(params, "verificada"))
                .filterByServicios(getParam(params, "servicios"));
    }

    private static String getParam(Map<String, String[]> params, String key) {
        return params.containsKey(key) ? params.get(key)[0] : null;
    }
}
