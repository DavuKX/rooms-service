CREATE TABLE servicio
(
    id            serial4      NOT NULL,
    nombre        varchar(255) NOT NULL,
    tipo varchar(255) NOT NULL,
    precio         float8       NOT NULL,
    icon          varchar(255),
    CONSTRAINT servicio_pkey PRIMARY KEY (id)
);

CREATE TABLE habitacion
(
    id             serial4            NOT NULL,
    ciudad         varchar(255)       NOT NULL,
    direccion      varchar(255)       NOT NULL,
    capacidad      int4               NOT NULL,
    precio_noche    float8             NOT NULL,
    descripcion    varchar(255)       NOT NULL,
    propietario_id int4               NOT NULL,
    verificada     bool DEFAULT false NOT NULL,
    CONSTRAINT habitacion_pkey PRIMARY KEY (id)
);

CREATE TABLE habitacion_servicios
(
    habitacion_id int4 NOT NULL,
    servicio_id   int4 NOT NULL,
    CONSTRAINT habitacion_servicio_pkey PRIMARY KEY (habitacion_id, servicio_id),
    CONSTRAINT habitacion_servicio_habitacion_id_fkey FOREIGN KEY (habitacion_id) REFERENCES habitacion (id),
    CONSTRAINT habitacion_servicio_servicio_id_fkey FOREIGN KEY (servicio_id) REFERENCES servicio (id)
);

CREATE TABLE imagen
(
    id            serial4      NOT NULL,
    habitacion_id int4         NOT NULL,
    url           varchar(255) NOT NULL,
    CONSTRAINT imagenes_pkey PRIMARY KEY (id),
    CONSTRAINT imagenes_habitacion_id_fkey FOREIGN KEY (habitacion_id) REFERENCES habitacion (id)
);