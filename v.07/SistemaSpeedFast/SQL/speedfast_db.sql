-- Base de datos para el sistema SpeedFast
CREATE DATABASE IF NOT EXISTS speedfast_db;
USE speedfast_db;

-- Limpieza de las tablas de registro
DROP TABLE IF EXISTS entrega;
DROP TABLE IF EXISTS pedido;
DROP TABLE IF EXISTS repartidor;

-- Tabla repartidor
CREATE TABLE repartidor (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL
);

-- Tabla pedido: tabla unica para los 3 subtipos (herencia por tabla unica).
-- El campo tipo indica que subclase representa la fila (COMIDA | ENCOMIENDA | EXPRESS).
-- Las columnas especificas de cada subtipo quedan NULL cuando no corresponden a ese tipo.
-- Estas seran controladas por el validador de informacion del Proyecto java
CREATE TABLE pedido (
    id INT AUTO_INCREMENT PRIMARY KEY,
    cliente VARCHAR(100) NOT NULL,
    direccion VARCHAR(150) NOT NULL,
    distancia_km DECIMAL(9,3) NOT NULL,
    tipo VARCHAR(30) NOT NULL,                              -- COMIDA | ENCOMIENDA | EXPRESS
    estado VARCHAR(20) NOT NULL,                            -- PENDIENTE | EN_REPARTO | ENTREGADO | CANCELADO

    -- especificos de Comida
    restaurante VARCHAR(100),
    tiempo_preparacion VARCHAR(50),

    -- especificos de Encomienda
    peso DECIMAL(9,3),
    volumen DECIMAL(9,3),

    -- especificos de Express
    tienda VARCHAR(100)
);

-- Tabla entrega
CREATE TABLE entrega (
    id INT AUTO_INCREMENT PRIMARY KEY,
    id_pedido INT NOT NULL,
    id_repartidor INT NOT NULL,
    fecha DATE NOT NULL,
    hora TIME NOT NULL,
    FOREIGN KEY (id_pedido) REFERENCES pedido(id),
    FOREIGN KEY (id_repartidor) REFERENCES repartidor(id)
);