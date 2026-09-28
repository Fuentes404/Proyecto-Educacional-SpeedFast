# SpeedFast - Gestión de Entregas con Java y MySQL

Este proyecto simula el sistema de gestión de pedidos de **SpeedFast**, una empresa de reparto. Aplica
**Programación Orientada a Objetos** (herencia, clase abstracta, interfaces, polimorfismo, sobrecarga y
sobreescritura), **programación concurrente** (`Runnable`, `ExecutorService`), una interfaz gráfica con
**Java Swing** y **persistencia en MySQL** mediante JDBC. Los pedidos, repartidores y entregas se guardan en
la base de datos, por lo que se conservan al cerrar la aplicación.

## 📋 Descripción

- **Clase abstracta** `Pedido`, con los datos comunes (id, cliente, dirección, distancia, tipo y estado).
- **Herencia:** `PedidoComida`, `PedidoEncomienda` y `PedidoExpress` extienden de `Pedido` y agregan sus
  propios atributos (restaurante, peso y volumen, tienda).
- **Interfaces:** `Cancelable`, `Rastreable` y `Despachable`. Cada tipo implementa solo las que le corresponden.
- **Sobreescritura** de `mostrarResumen()`, `calcularTiempoEntrega()` y `asignarRepartidor()`.
- **Sobrecarga** de `asignarRepartidor()` y `asignarRepartidor(String nombreRepartidor)`.
- **Polimorfismo:** los pedidos se procesan como `Pedido`, sin importar su tipo concreto.
- **Concurrencia:** `Repartidor` implementa `Runnable` y cada repartidor corre en su propio hilo, gestionado
  con `ExecutorService`. La simulación usa `SwingWorker` para no congelar la interfaz.
- **Persistencia con JDBC:** clases DAO (`PedidoDAO`, `RepartidorDAO`, `EntregaDAO`) que usan
  `PreparedStatement`, `ResultSet` y `try-with-resources`. La conexión se gestiona en `ConexionDB`.
- **Herencia en la base de datos:** los tres tipos de pedido se guardan en una sola tabla (`pedido`), y la
  columna `tipo` indica cuál es. Las columnas de otros tipos quedan en `NULL`.
- **Validación** de los datos del formulario con la clase de utilidades `Validador`.
- **Manejo de excepciones:** los errores de validación, de reglas de negocio y de base de datos se muestran
  al usuario en cuadros de diálogo.

## 📂 Estructura del proyecto

```
SpeedFast/
├── sql/
│   └── speedfast_db.sql                  # Script de creación de la base de datos
├── src/main/java/
│   ├── main/
│   │   └── Main.java                     # Punto de entrada: abre la ventana principal
│   ├── dao/
│   │   ├── ConexionDB.java               # Conexión JDBC y creación de tablas
│   │   ├── PedidoDAO.java                # Guardar, buscar, listar y actualizar pedidos
│   │   ├── RepartidorDAO.java            # Guardar, buscar, listar y eliminar repartidores
│   │   └── EntregaDAO.java               # Guardar y listar entregas
│   ├── interfaces/
│   │   ├── Cancelable.java               # Contrato: cancelar() (Comida y Encomienda)
│   │   ├── Despachable.java              # Contrato: despachar() (Compra Express)
│   │   └── Rastreable.java               # Contrato: verHistorial() (Comida y Encomienda)
│   ├── model/
│   │   ├── Pedido.java                   # Clase base abstracta
│   │   ├── PedidoComida.java             # Restaurante y tiempo de preparación
│   │   ├── PedidoEncomienda.java         # Peso y volumen
│   │   ├── PedidoExpress.java            # Tienda
│   │   ├── Repartidor.java               # Hilo (Runnable) que simula las entregas
│   │   ├── Entrega.java                  # Relación pedido - repartidor, con fecha y hora
│   │   ├── EstadoPedido.java             # PENDIENTE, EN_REPARTO, ENTREGADO, CANCELADO
│   │   └── TipoPedido.java               # COMIDA, ENCOMIENDA, EXPRESS
│   ├── services/
│   │   ├── ControladorPedidos.java       # Registro, consulta, asignación y cancelación de pedidos
│   │   └── ControladorRepartidores.java  # Repartidores y simulación concurrente
│   ├── util/
│   │   └── Validador.java                # Validaciones del formulario
│   └── view/
│       ├── VentanaPrincipal.java         # Menú principal
│       ├── VentanaRegistroPedido.java    # Formulario para registrar pedidos
│       ├── VentanaListaPedidos.java      # Tabla con todos los pedidos
│       ├── VentanaRepartidores.java      # Registro y eliminación de repartidores
│       └── VentanaEntregas.java          # Asignación de repartidores e inicio de entregas
├── pom.xml
└── README.md
```

## 🗄️ Base de datos

La base se llama `speedfast_db` y tiene tres tablas:

| Tabla | Contenido |
|-------|-----------|
| `repartidor` | `id`, `nombre` |
| `pedido` | `id`, `cliente`, `direccion`, `distancia_km`, `tipo`, `estado` y las columnas específicas de cada tipo (`restaurante`, `tiempo_preparacion`, `peso`, `volumen`, `tienda`) |
| `entrega` | `id`, `id_pedido`, `id_repartidor`, `fecha`, `hora` (con claves foráneas a `pedido` y `repartidor`) |

El script completo está en `sql/speedfast_db.sql`.

## ▶️ Funcionamiento

Al ejecutar `Main.java` se realizan las siguientes acciones en orden:

1. `SwingUtilities.invokeLater()` crea y muestra la `VentanaPrincipal`.
2. La `VentanaPrincipal` crea **una única instancia** de `ControladorPedidos` y de `ControladorRepartidores`,
   y la comparte con todas las ventanas que abre.
3. La primera vez que se usa la base de datos, `ConexionDB` crea `speedfast_db` y sus tablas si no existen.
   La base parte **sin repartidores**: se registran desde la aplicación.
4. En **Registrar pedido**, el formulario valida los datos y el controlador guarda el pedido con `PedidoDAO`.
   El ID lo genera MySQL (`AUTO_INCREMENT`).
5. En **Asignar repartidor**, el controlador guarda una `Entrega` con `EntregaDAO` y pasa el pedido a
   `EN_REPARTO`. El mensaje de la asignación lo arma el propio pedido (sobrecarga y polimorfismo).
6. En **Iniciar entregas**, se ejecutan estos pasos:
   - Se toman los pedidos pendientes y las asignaciones manuales guardadas en la base de datos.
   - Se respetan las asignaciones manuales y el resto se reparte entre el repartidor con menos pedidos.
   - Se crea un `ExecutorService` con un hilo por repartidor activo, y cada `Repartidor` simula sus
     entregas (1 a 3 segundos cada una) enviando sus mensajes a la pantalla con `invokeLater()`.
   - Se llama a `shutdown()` y `awaitTermination()` con un tiempo máximo de espera.
7. Al terminar, el `SwingWorker` marca los pedidos como **Entregado** en la base de datos y actualiza la lista.

**Tiempo estimado de entrega por tipo de pedido:**

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

> ⚠️ **Nota sobre el orden de salida:** los repartidores corren de forma concurrente, por lo que el orden
> exacto de los mensajes durante la simulación **no está garantizado** y puede variar entre ejecuciones.

## 📖 Manual de usuario

### Requisitos

- **Java JDK 23** (o ajustar la versión en el `pom.xml`).
- **MySQL Server** en ejecución en `localhost:3306`.
- Un IDE con soporte Maven (IntelliJ IDEA, Eclipse o NetBeans).

### Configuración

1. **Base de datos:** no es necesario hacer nada, porque la aplicación crea la base de datos y las tablas al
   iniciar. Si prefiere crearlas a mano, ejecute `sql/speedfast_db.sql` en MySQL Workbench
   (ojo: el script borra las tablas existentes antes de crearlas).
2. **Contraseña:** abra `src/main/java/dao/ConexionDB.java` y reemplace la contraseña por la de su usuario
   de MySQL:

```java
private static final String USER = "root";
private static final String PASSWORD = "tu_contraseña";
```

### Cómo ejecutar

Abrir el proyecto en el IDE, esperar a que Maven descargue las dependencias (incluye el conector
`mysql-connector-j`) y ejecutar la clase `main.Main`.

### Ventana principal

Al iniciar aparece el menú **SpeedFast - Gestión de Entregas** con cinco botones:

| Botón | Qué hace |
|-------|----------|
| Registrar pedido | Abre el formulario para crear un pedido nuevo. |
| Listar pedidos | Abre la tabla con todos los pedidos y su estado. |
| Gestionar repartidores | Permite registrar y eliminar repartidores. |
| Asignar repartidor / Iniciar entrega | Asigna repartidores, cancela pedidos e inicia la simulación. |
| Salir | Cierra la aplicación. |

### 1. Registrar repartidores

1. Presione **Gestionar repartidores**.
2. Escriba el nombre y presione **Agregar** (o **Enter**). El repartidor aparece en la tabla con su ID.
3. Para eliminar uno, selecciónelo en la tabla y presione **Eliminar seleccionado**.

No se puede repetir un nombre ni eliminar a un repartidor que ya tiene entregas registradas.

### 2. Registrar un pedido

1. Presione **Registrar pedido**.
2. Complete los campos comunes: **Cliente**, **Dirección** y **Distancia (km)**.
3. Elija el **Tipo de pedido**. Los campos específicos cambian según la opción:

| Tipo | Campos específicos |
|------|--------------------|
| Comida | Restaurante y Tiempo de preparación |
| Encomienda | Peso (kg) y Volumen (m3) |
| Express | Tienda |

4. Presione **Guardar** (o **Enter**). Aparece un resumen del pedido con su ID y su tiempo estimado de entrega.

**Reglas de los campos:**

- **Distancia, Peso y Volumen:** números mayores que 0, con hasta 6 dígitos enteros y 3 decimales. Se acepta
  punto o coma (`3.5` o `3,5`).
- **Cliente, Dirección, Restaurante, Tiempo de preparación y Tienda:** obligatorios.

Si algún dato es inválido, se muestra un mensaje con los errores a corregir y el pedido no se guarda.

### 3. Consultar los pedidos

1. Presione **Listar pedidos**.
2. La tabla muestra: ID, Tipo, Cliente, Dirección, Distancia, Tiempo estimado y Estado.
3. Se actualiza al abrirla y al volver a la ventana, o con el botón **Refrescar**.

| Estado | Significado |
|--------|-------------|
| Pendiente | Registrado y aún sin repartidor. |
| Asignado a (nombre) | Se eligió un repartidor. Aún no se entrega. |
| Entregado | La simulación ya lo completó. |
| Cancelado | El pedido fue cancelado. |

### 4. Asignar, cancelar e iniciar entregas

1. Presione **Asignar repartidor / Iniciar entrega**.
2. Elija un **Pedido** y un **Repartidor**, y presione **Asignar repartidor**.
   Este paso es opcional: los pedidos sin asignar se reparten automáticamente al iniciar las entregas.
3. **Cancelar pedido** solo se habilita para los pedidos que se pueden cancelar (Comida y Encomienda).
4. Presione **Iniciar entregas**. Los controles se bloquean y en **Salida** aparece en tiempo real el avance
   de cada repartidor.
5. Al terminar, los pedidos pasan a **Entregado**.

**Ejemplo de salida** (el orden puede variar entre ejecuciones):

```
Iniciando simulacion de entregas concurrentes:
---------------------------------------------
Repartidor Carlos inicia su ruta con 1 pedido(s).
Repartidor Fernanda inicia su ruta con 1 pedido(s).
[Carlos] Iniciando entrega del Pedido N°: 1 (COMIDA)
[Fernanda] Iniciando entrega del Pedido N°: 2 (ENCOMIENDA)
[Fernanda] Pedido N°: 2 entregado con éxito
[Carlos] Pedido N°: 1 entregado con éxito
Repartidor: Fernanda ha finalizado todas sus entregas
Repartidor: Carlos ha finalizado todas sus entregas
---------------------------------------------
Simulacion finalizada: Todos los repartidores completaron sus rutas.
```
