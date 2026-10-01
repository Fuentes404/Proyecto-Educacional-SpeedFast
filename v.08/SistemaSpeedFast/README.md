# SpeedFast - Gestión de Entregas con Java, Swing y MySQL

Este proyecto simula el sistema de gestión de pedidos de **SpeedFast**, una empresa de reparto. Aplica
**Programación Orientada a Objetos** (herencia, clase abstracta, interfaces, polimorfismo, sobrecarga y
sobreescritura), **programación concurrente** (`Runnable` e hilos), una interfaz gráfica con **Java Swing**
y **persistencia en MySQL** mediante JDBC. Los pedidos, repartidores y entregas se guardan en la base de
datos, por lo que se conservan al cerrar la aplicación.

## 📋 Descripción

SpeedFast es una aplicación de escritorio para que una empresa de reparto administre sus pedidos, repartidores y entregas desde una sola ventana. Permite registrar pedidos de tres tipos (comida, encomienda y express), asignarlos a un repartidor, enviarlos a ruta y seguir en pantalla el avance de cada entrega hasta que el pedido queda entregado.

Los datos se guardan en una base de datos MySQL, por lo que se conservan al cerrar la aplicación. El proyecto está organizado en capas (modelo, lógica, interfaz y acceso a datos) y sirve como ejemplo práctico de programación orientada a objetos, hilos, interfaz gráfica con Swing y conexión a base de datos con JDBC.

## 🧩 Estructuras utilizadas

| Tipo | Sintaxis | Descripción |
|------|----------|-------------|
| Clase abstracta | `abstract class Pedido` | Define los datos y métodos comunes a todos los pedidos. |
| Herencia | `class PedidoComida extends Pedido` | Cada tipo de pedido especializa a la clase base. |
| Interfaces | `implements Cancelable, Rastreable` | Definen contratos que cumplen solo algunos tipos de pedido. |
| Enumeraciones | `enum EstadoPedido` / `enum TipoPedido` | Definen los valores fijos de estado y tipo de un pedido. |
| Sobreescritura | `@Override calcularTiempoEntrega()` | Cada pedido calcula su tiempo de entrega de forma distinta. |
| Sobrecarga | `asignarRepartidor()` / `asignarRepartidor(String)` | Mismo método con distintos parámetros. |
| Polimorfismo | `pedido instanceof Cancelable` | El controlador decide según el contrato que cumple el pedido. |
| Acceso a datos | `DAO` + `PreparedStatement` | Cada entidad tiene su DAO, y las consultas usan parámetros para evitar inyección SQL. |
| Conexión a BD | `DriverManager.getConnection(...)` | `ConexionBD` entrega una conexión nueva, y cada DAO la cierra con `try-with-resources`. |
| Hilos | `class Repartidor implements Runnable` | Cada reparto corre en segundo plano mientras la ventana sigue disponible. |
| Interfaz funcional | `Consumer<String> salida` | Permite decidir por dónde salen los mensajes del repartidor (consola o `JTextArea`). |
| Interfaz gráfica | `JFrame`, `JPanel`, `JTable`, `JOptionPane` | Ventana, paneles, tablas y formularios emergentes. |
| Cambio de pantalla | `CardLayout` | La ventana principal alterna entre los paneles de Repartidores, Pedidos y Entregas. |
| Modelo de tabla | `DefaultTableModel` | Los controladores llenan la tabla de cada panel con los datos de la BD. |
| Actualización segura de la UI | `SwingUtilities.invokeLater(...)` | Permite que los hilos de los repartidores escriban en el área de seguimiento. |
| Validación | `String validarXxx(...)` | Devuelve el mensaje de error, o `null` si el dato es válido. |
| Expresiones regulares | `valor.matches("^\\d+([.,]\\d+)?$")` | Validan nombres y números. |
| Fecha y hora | `LocalDate` / `LocalTime` | Registran y validan la fecha y hora de cada entrega. |
| Formulario con reintento | `while (true) { ... }` | Repite el formulario hasta que los datos sean válidos o el usuario cancele. |

## 📂 Estructura del proyecto

```
SpeedFast/
├── src/
│   ├── main/
│   │   └── java/
│   │       ├── main/
│   │       │   └── Main.java                    # Punto de entrada: comprueba la BD y abre la ventana
│   │       ├── model/
│   │       │   ├── Pedido.java                  # Clase abstracta base
│   │       │   ├── PedidoComida.java            # Restaurante y tiempo de preparación
│   │       │   ├── PedidoEncomienda.java        # Peso y volumen
│   │       │   ├── PedidoExpress.java           # Tienda
│   │       │   ├── Repartidor.java              # Repartidor (Runnable) que simula las entregas
│   │       │   ├── Entrega.java                 # Relación pedido - repartidor, con fecha y hora
│   │       │   ├── EstadoPedido.java            # PENDIENTE, EN_REPARTO, ENTREGADO, CANCELADO
│   │       │   └── TipoPedido.java              # COMIDA, ENCOMIENDA, EXPRESS
│   │       ├── interfaces/
│   │       │   ├── Cancelable.java              # Contrato: cancelar() (Comida y Encomienda)
│   │       │   ├── Despachable.java             # Contrato: despachar() (Compra Express)
│   │       │   └── Rastreable.java              # Contrato: verHistorial() (Comida y Encomienda)
│   │       ├── services/
│   │       │   ├── ControladorPedidos.java      # Lógica de pedidos
│   │       │   ├── ControladorRepartidores.java # Lógica de repartidores
│   │       │   └── ControladorEntregas.java     # Lógica de entregas y envío
│   │       ├── util/
│   │       │   └── ValidadorDatos.java          # Validaciones de campos
│   │       ├── view/
│   │       │   ├── VentanaPrincipal.java        # Ventana con menú lateral
│   │       │   ├── PanelPedidos.java            # Panel de pedidos
│   │       │   ├── PanelRepartidores.java       # Panel de repartidores
│   │       │   └── PanelEntregas.java           # Panel de entregas y seguimiento
│   │       └── dao/
│   │           ├── ConexionBD.java              # Abre y entrega la conexión a MySQL
│   │           ├── PedidoDAO.java               # Operaciones sobre la tabla pedido
│   │           ├── RepartidorDAO.java           # Operaciones sobre la tabla repartidor
│   │           └── EntregaDAO.java              # Operaciones sobre la tabla entrega
│   └── test/
├── .gitignore
├── pom.xml
└── README.md
```

## 🏗️ Capas

| Capa | Responsabilidad |
|------|-----------------|
| `main` | Arranca el sistema: comprueba la conexión con MySQL y abre la ventana principal. |
| `model` | Entidades del sistema y sus reglas propias (tiempos de entrega, mensajes, estados). No conoce la UI ni la BD. |
| `interfaces` | Contratos que cumplen algunos tipos de pedido. |
| `services` | Lógica de negocio: crear, editar, cancelar, eliminar y consultar. No muestra mensajes, solo devuelve el error (o `null` si todo salió bien). |
| `view` | Interfaz gráfica: muestra tablas y botones, pide datos con formularios y llama a los controladores. |
| `util` | Herramientas de apoyo: validaciones de campos. |
| `dao` | Conexión y acceso a MySQL. No aplica reglas de negocio ni muestra mensajes. |

## 🗄️ Base de datos

La base se llama `speedfast_db` y tiene tres tablas:

| Tabla | Contenido |
|-------|-----------|
| `repartidor` | `id`, `nombre` |
| `pedido` | `id`, `tipo`, `cliente`, `direccion`, `distancia_km`, `estado` y las columnas específicas de cada tipo (`restaurante`, `tiempo_preparacion`, `peso`, `volumen`, `tienda`) |
| `entrega` | `id`, `id_pedido`, `id_repartidor`, `fecha`, `hora`, `estado` |

- Los `id` los genera MySQL al guardar (`AUTO_INCREMENT`).
- Los estados y tipos se guardan como texto: `PENDIENTE`, `EN_REPARTO`, `ENTREGADO`, `CANCELADO` y `COMIDA`, `ENCOMIENDA`, `EXPRESS`.
- Las columnas que no aplican al tipo del pedido (por ejemplo `peso` en un pedido de comida) quedan en `NULL`.
- La base parte **sin repartidores**: se registran desde la aplicación.

Script de creación:

```sql
CREATE DATABASE IF NOT EXISTS speedfast_db;
USE speedfast_db;

CREATE TABLE repartidor (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL
);

CREATE TABLE pedido (
    id INT AUTO_INCREMENT PRIMARY KEY,
    tipo VARCHAR(20) NOT NULL,
    cliente VARCHAR(100) NOT NULL,
    direccion VARCHAR(150) NOT NULL,
    distancia_km DOUBLE NOT NULL,
    estado VARCHAR(20) NOT NULL DEFAULT 'PENDIENTE',
    restaurante VARCHAR(100),
    tiempo_preparacion VARCHAR(50),
    peso DOUBLE,
    volumen DOUBLE,
    tienda VARCHAR(100)
);

CREATE TABLE entrega (
    id INT AUTO_INCREMENT PRIMARY KEY,
    id_pedido INT NOT NULL,
    id_repartidor INT NOT NULL,
    fecha DATE NOT NULL,
    hora TIME NOT NULL,
    estado VARCHAR(20) NOT NULL DEFAULT 'PENDIENTE',
    FOREIGN KEY (id_pedido) REFERENCES pedido(id),
    FOREIGN KEY (id_repartidor) REFERENCES repartidor(id)
);
```

Para conectar con MySQL se agrega esta dependencia en el `pom.xml`, entre `</properties>` y `</project>`:

```xml
<dependencies>
    <dependency>
        <groupId>com.mysql</groupId>
        <artifactId>mysql-connector-j</artifactId>
        <version>9.0.0</version>
    </dependency>
</dependencies>
```

> Los datos de conexión (URL, usuario y clave) están en `ConexionBD`. Si el proyecto se sube a un repositorio, no dejar la clave escrita en el código.

## ▶️ Funcionamiento

Al ejecutar `Main.java` se realizan las siguientes acciones en orden:

1. `Main` comprueba la conexión con MySQL y lo informa por consola (si falla, muestra el error y sigue).
2. `SwingUtilities.invokeLater()` crea y muestra la `VentanaPrincipal`.
3. La `VentanaPrincipal` crea **una única instancia** de cada controlador y la comparte con los paneles
   que lo necesitan. Se abre en el panel de **Pedidos**.
4. En **Pedidos**, el formulario valida los datos y el controlador guarda el pedido con `PedidoDAO`.
   El ID lo genera MySQL.
5. En **Entregas**, el controlador valida las reglas del negocio (el pedido existe, está pendiente y no
   tiene entrega) y guarda una `Entrega` con `EntregaDAO`, con la fecha y hora actuales.
6. Al presionar **Enviar pedido**, el controlador:
   - Lee desde la base de datos los pedidos pendientes del repartidor de la entrega seleccionada.
   - Pasa esos pedidos a `EN_REPARTO`.
   - Crea un hilo con la ruta del `Repartidor`, que simula cada entrega (entre 1 y 3 segundos) y envía sus
     mensajes al área **Seguimiento de entregas** con `invokeLater()`.
7. Al terminar la ruta (si no fue interrumpida), el hilo marca los pedidos como **ENTREGADO** en la base de
   datos. Se ve al presionar **Actualizar** o al cambiar de panel.

**Tiempo estimado de entrega por tipo de pedido** (método `calcularTiempoEntrega()` del modelo):

| Tipo | Fórmula |
|------|---------|
| Comida | 15 min base + 2 min por km (redondeado al minuto) |
| Encomienda | 20 min base + 1.5 min por km (redondeado al minuto) |
| Compra Express | 10 min base, más 5 min si la distancia supera los 5 km |

**Contratos que cumple cada tipo de pedido:**

| Tipo | Cancelable | Rastreable | Despachable |
|------|:----------:|:----------:|:-----------:|
| Comida | ✔ | ✔ | |
| Encomienda | ✔ | ✔ | |
| Compra Express | | | ✔ |

## 📖 Manual de usuario

### Requisitos

- **Java JDK** (la versión definida en el `pom.xml`).
- **MySQL Server** en ejecución en `localhost:3306`.
- Un IDE con soporte Maven (IntelliJ IDEA, Eclipse o NetBeans).

### Configuración

1. **Base de datos:** ejecute el script de la sección *Base de datos* en MySQL Workbench (o en la consola
   de MySQL) para crear `speedfast_db` y sus tablas.
2. **Credenciales:** abra `src/main/java/dao/ConexionBD.java` y reemplace el usuario y la contraseña por los
   de su MySQL:

```java
private static final String URL = "jdbc:mysql://localhost:3306/speedfast_db";
private static final String USER = "root";
private static final String PASSWORD = "tu_contraseña";
```

### Cómo ejecutar

Abrir el proyecto en el IDE, esperar a que Maven descargue las dependencias (incluye el conector
`mysql-connector-j`) y ejecutar la clase `main.Main`. Si la conexión con MySQL falla, el error aparece
en la consola.

### Ventana principal

Al iniciar aparece la ventana **SpeedFast - Sistema de Gestión de Entregas**, con un menú lateral y el
panel de Pedidos abierto:

| Botón | Qué hace |
|-------|----------|
| Repartidores | Abre el panel para crear, editar y eliminar repartidores. |
| Pedidos | Abre el panel para crear, editar, cancelar y eliminar pedidos. |
| Entregas | Abre el panel para asignar pedidos, editarlos, eliminarlos y enviarlos. |
| Salir | Cierra la aplicación. |

Cada panel tiene una tabla con los datos y una fila de botones. El botón **Actualizar** vuelve a cargar la
tabla desde la base de datos, y esto también ocurre al cambiar de panel.

**Reglas de los campos** (se validan antes de guardar):

- **Nombre de repartidor y Cliente:** solo letras (con tildes y ñ) y espacios.
- **Dirección, Restaurante, Tiempo de preparación y Tienda:** obligatorios.
- **Distancia, Peso y Volumen:** números mayores que 0. Se acepta punto o coma (`3.5` o `3,5`).
- **Fecha:** formato `AAAA-MM-DD`, no anterior a hoy.
- **Hora:** formato `HH:mm`.

Si algún dato es inválido, se muestra el error y el formulario se vuelve a abrir para corregirlo. Si se
presiona **Cancelar** en el formulario, no se guarda nada.

### 1. Gestionar repartidores

1. Presione **Repartidores** en el menú lateral.
2. Presione **Nuevo**, escriba el nombre y acepte. El repartidor aparece en la tabla con su ID.
3. Para cambiar el nombre, seleccione la fila y presione **Editar**.
4. Para borrar uno, seleccione la fila y presione **Eliminar** (pide confirmación).

No se puede eliminar un repartidor que tiene entregas en proceso (pedidos pendientes o en reparto).
Si solo tiene entregas terminadas, se eliminan junto con él.

### 2. Gestionar pedidos

1. Presione **Pedidos** en el menú lateral.
2. Presione **Nuevo** y elija el **tipo de pedido**. El formulario cambia según la opción:

| Tipo | Campos |
|------|--------|
| Comida | Cliente, Dirección, Distancia (km), Restaurante y Tiempo de preparación |
| Encomienda | Cliente, Dirección, Distancia (km), Peso (kg) y Volumen (m3) |
| Express | Cliente, Dirección, Distancia (km) y Tienda |

3. Al aceptar, el pedido se guarda como **PENDIENTE** y aparece en la tabla. Las columnas que no
   corresponden a su tipo quedan vacías.

**Estados de un pedido:**

| Estado | Significado |
|--------|-------------|
| PENDIENTE | Registrado y listo para asignar o enviar. |
| EN_REPARTO | El repartidor salió a ruta con el pedido. |
| ENTREGADO | La simulación completó la entrega. El pedido queda como historial. |
| CANCELADO | El pedido fue cancelado. |

**Acciones sobre un pedido seleccionado:**

- **Editar:** solo si está pendiente. El formulario aparece con los valores actuales. Un pedido en
  reparto, entregado o cancelado no se puede modificar.
- **Cancelar pedido:** solo para pedidos pendientes que cumplen el contrato `Cancelable` (Comida y
  Encomienda). Al cancelar se muestra el mensaje propio del tipo de pedido. Los pedidos Express no se
  pueden cancelar.
- **Eliminar:** no se elimina un pedido entregado, en reparto, ni pendiente con un repartidor asignado.
  Un pedido cancelado se elimina junto con su entrega.

### 3. Asignar, editar, eliminar y enviar entregas

1. Presione **Entregas** en el menú lateral.
2. Presione **Nuevo**, elija un **Pedido** y un **Repartidor**, y acepte. Solo aparecen los pedidos
   pendientes que aún no tienen entrega, y se necesita al menos un repartidor creado. La entrega
   queda con la fecha y hora actuales.
3. Para cambiar el repartidor, la fecha o la hora, seleccione la fila y presione **Editar**.
4. Para deshacer la asignación, seleccione la fila y presione **Eliminar**.
   Editar y eliminar solo funcionan mientras el pedido siga pendiente.
5. Para iniciar el reparto, seleccione una entrega y presione **Enviar pedido**. Se enviarán **todos los
   pedidos pendientes del repartidor** de esa entrega.
6. Los pedidos pasan a EN_REPARTO y en **Seguimiento de entregas** aparece en tiempo real el avance de
   la ruta. Al terminar, los pedidos pasan a ENTREGADO (presione **Actualizar** para verlo en la tabla).

**Ejemplo de salida** (un repartidor con dos pedidos):

```
Repartidor Carlos inicia su ruta con 2 pedido(s).
[Carlos] Iniciando entrega del Pedido N°: 1 (COMIDA)
[Carlos] Pedido N°: 1 entregado con éxito
[Carlos] Iniciando entrega del Pedido N°: 2 (ENCOMIENDA)
[Carlos] Pedido N°: 2 entregado con éxito
Repartidor: Carlos ha finalizado todas sus entregas
```
