-- PERFILES
INSERT INTO perfil (id_perfil, tipo) VALUES (1, 'ADMIN');
INSERT INTO perfil (id_perfil, tipo) VALUES (2, 'USER');
INSERT INTO perfil (id_perfil, tipo) VALUES (3, 'RESTAURANTE');

-- USUARIOS
INSERT INTO usuario (nombre_usuario, nombre, contrasenia, id_perfil)
VALUES ('admin', 'Administrador', 'admin', 1);

INSERT INTO usuario (nombre_usuario, nombre, contrasenia, id_perfil)
VALUES ('user', 'Usuario Normal', 'user', 2);

-- USUARIOS DE RESTAURANTE
INSERT INTO usuario (nombre_usuario, nombre, contrasenia, id_perfil)
VALUES ('rest_celler', 'Celler Manager', 'rest_celler', 3);

INSERT INTO usuario (nombre_usuario, nombre, contrasenia, id_perfil)
VALUES ('rest_disfrutar', 'Disfrutar Manager', 'rest_disfrutar', 3);

INSERT INTO usuario (nombre_usuario, nombre, contrasenia, id_perfil)
VALUES ('rest_diverxo', 'DiverXO Manager', 'rest_diverxo', 3);

INSERT INTO usuario (nombre_usuario, nombre, contrasenia, id_perfil)
VALUES ('rest_aponiente', 'Aponiente Manager', 'rest_aponiente', 3);

INSERT INTO usuario (nombre_usuario, nombre, contrasenia, id_perfil)
VALUES ('rest_azurmendi', 'Azurmendi Manager', 'rest_azurmendi', 3);

-- RESTAURANTES (ya podemos referenciar nombre_usuario)
INSERT INTO restaurante (nombre, ubicacion, carta, horarios, contacto, nombre_usuario)
VALUES ('El Celler de Can Roca', 'Girona, España', 'Cocina de vanguardia', 'Lunes a Sábado: 12:30–15:30', '+34 972 222 157', 'rest_celler');

INSERT INTO restaurante (nombre, ubicacion, carta, horarios, contacto, nombre_usuario)
VALUES ('Disfrutar', 'Barcelona, España', 'Cocina moderna', 'Martes a Sábado: 13:00–16:00', '+34 933 486 896', 'rest_disfrutar');

INSERT INTO restaurante (nombre, ubicacion, carta, horarios, contacto, nombre_usuario)
VALUES ('DiverXO', 'Madrid, España', 'Cocina de autor', 'Miércoles a Sábado: 13:30–16:00', '+34 915 30 85 11', 'rest_diverxo');

INSERT INTO restaurante (nombre, ubicacion, carta, horarios, contacto, nombre_usuario)
VALUES ('Aponiente', 'El Puerto de Santa María, España', 'Cocina marinera', 'Martes a Sábado: 13:30–15:30', '+34 956 83 50 10', 'rest_aponiente');

INSERT INTO restaurante (nombre, ubicacion, carta, horarios, contacto, nombre_usuario)
VALUES ('Azurmendi', 'Larrabetzu, España', 'Cocina vasca', 'Martes a Sábado: 13:00–15:30', '+34 946 54 22 42', 'rest_azurmendi');

-- CUPONES
INSERT INTO cupon (tiempo_duracion, descuento, codigo) VALUES ('1 semana', 10.0, '634789');
INSERT INTO cupon (tiempo_duracion, descuento, codigo) VALUES ('2 semanas', 15.0, '154370');
INSERT INTO cupon (tiempo_duracion, descuento, codigo) VALUES ('1 mes', 20.0, '089768');
INSERT INTO cupon (tiempo_duracion, descuento, codigo) VALUES ('3 meses', 25.0, '245611');
INSERT INTO cupon (tiempo_duracion, descuento, codigo) VALUES ('6 meses', 30.0, '365769');

-- PERTENECE
INSERT INTO pertenece (id_restaurante, id_cupon) VALUES (1, 1);
INSERT INTO pertenece (id_restaurante, id_cupon) VALUES (1, 3);
INSERT INTO pertenece (id_restaurante, id_cupon) VALUES (2, 2);
INSERT INTO pertenece (id_restaurante, id_cupon) VALUES (2, 4);
INSERT INTO pertenece (id_restaurante, id_cupon) VALUES (3, 5);

-- RESERVAS (CON USUARIO)
INSERT INTO reserva (fecha, id_restaurante, nombre_usuario, comensales)
VALUES ('2025-01-15 20:30', 1, 'user', 2);

INSERT INTO reserva (fecha, id_restaurante, nombre_usuario, comensales)
VALUES ('2025-01-18 14:00', 1, 'admin', 4);

INSERT INTO reserva (fecha, id_restaurante, nombre_usuario, comensales)
VALUES ('2025-01-25 21:00', 2, 'user', 3);

INSERT INTO reserva (fecha, id_restaurante, nombre_usuario, comensales)
VALUES ('2025-02-02 13:30', 3, 'user', 2);
