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
VALUES ('rest_rekondo', 'Rekondo Manager', 'rest_rekondo', 3);

INSERT INTO usuario (nombre_usuario, nombre, contrasenia, id_perfil)
VALUES ('rest_casa_marcos', 'Casa Marcos Manager', 'rest_casa_marcos', 3);

INSERT INTO usuario (nombre_usuario, nombre, contrasenia, id_perfil)
VALUES ('rest_casa_gerardo', 'Casa Gerardo Manager', 'rest_casa_gerardo', 3);

INSERT INTO usuario (nombre_usuario, nombre, contrasenia, id_perfil)
VALUES ('rest_augusto', 'Augusto Manager', 'rest_augusto', 3);

INSERT INTO usuario (nombre_usuario, nombre, contrasenia, id_perfil)
VALUES ('rest_laparrilla', 'La Parrilla Manager', 'rest_laparrilla', 3);

-- RESTAURANTES
INSERT INTO restaurante (nombre, ubicacion, carta, horarios, contacto, nombre_usuario)
VALUES ('Rekondo', 'Oviedo', 'Cocina asturiana tradicional', 'Lunes a Domingo: 13:00–23:00', '+34 985 222 157', 'rest_rekondo');

INSERT INTO restaurante (nombre, ubicacion, carta, horarios, contacto, nombre_usuario)
VALUES ('Casa Marcos', 'Gijón', 'Cocina casera y parrilla', 'Martes a Domingo: 13:00–23:00', '+34 985 486 896', 'rest_casa_marcos');

INSERT INTO restaurante (nombre, ubicacion, carta, horarios, contacto, nombre_usuario)
VALUES ('Casa Gerardo', 'Prendes', 'Cocina de autor asturiana', 'Miércoles a Domingo: 13:00–22:00', '+34 985 30 85 11', 'rest_casa_gerardo');

INSERT INTO restaurante (nombre, ubicacion, carta, horarios, contacto, nombre_usuario)
VALUES ('Augusto', 'Avilés', 'Cocina marinera', 'Martes a Sábado: 13:30–15:30', '+34 985 83 50 10', 'rest_augusto');

INSERT INTO restaurante (nombre, ubicacion, carta, horarios, contacto, nombre_usuario)
VALUES ('La Parrilla de Mieres', 'Mieres', 'Parrilla y carne a la brasa', 'Martes a Sábado: 13:00–15:30', '+34 985 54 22 42', 'rest_laparrilla');

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
