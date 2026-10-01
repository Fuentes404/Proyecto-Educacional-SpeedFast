DROP DATABASE IF EXISTS speedfast_db;
CREATE DATABASE speedfast_db;
USE speedfast_db;

CREATE TABLE repartidor (
    id     INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL
);

CREATE TABLE pedido (
    id                 INT AUTO_INCREMENT PRIMARY KEY,
    tipo               ENUM('COMIDA', 'ENCOMIENDA', 'EXPRESS') NOT NULL,
    cliente            VARCHAR(100) NOT NULL,
    direccion          VARCHAR(200) NOT NULL,
    distancia_km       DOUBLE NOT NULL,
    estado             ENUM('PENDIENTE', 'EN_REPARTO', 'ENTREGADO', 'CANCELADO') NOT NULL DEFAULT 'PENDIENTE',
    restaurante        VARCHAR(100) NULL,
    tiempo_preparacion VARCHAR(50)  NULL,
    peso               DOUBLE NULL,
    volumen            DOUBLE NULL,
    tienda             VARCHAR(100) NULL
);

CREATE TABLE entrega (
    id            INT AUTO_INCREMENT PRIMARY KEY,
    id_pedido     INT  NOT NULL UNIQUE,
    id_repartidor INT  NOT NULL,
    fecha         DATE NOT NULL,
    hora          TIME NOT NULL,
    FOREIGN KEY (id_pedido)     REFERENCES pedido(id),
    FOREIGN KEY (id_repartidor) REFERENCES repartidor(id)
);

SHOW TABLES;