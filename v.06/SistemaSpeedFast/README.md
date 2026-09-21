
# SpeedFast - Gestión de Entregas con Interfaz Gráfica en Java

Este proyecto simula el sistema de gestión de pedidos de **SpeedFast**, una empresa de reparto. Aplica
**Programación Orientada a Objetos** (herencia, clase abstracta, interfaces, polimorfismo, sobrecarga y
sobreescritura) junto con **programación concurrente** (`Runnable`, `ExecutorService`) y una interfaz
gráfica construida con **Java Swing**. Un controlador único de pedidos, compartido por todas las ventanas,
mantiene los datos en memoria, y las entregas se simulan con un hilo por repartidor sin congelar la interfaz.

## 📋 Descripción

- Modelado de pedidos mediante la clase abstracta `Pedido`, que guarda los datos comunes (id, cliente,
  dirección, distancia y tipo) y declara los métodos que cada tipo de pedido debe implementar.
- **Herencia:** `PedidoComida`, `PedidoEncomienda` y `PedidoExpress` extienden de `Pedido` y agregan sus
  propios atributos (restaurante, peso y volumen, tienda).
- **Interfaces** como contratos de comportamiento: `Cancelable`, `Rastreable` y `Despachable`. Cada tipo
  de pedido implementa solo las que le corresponden.
- **Sobreescritura** de `mostrarResumen()`, `calcularTiempoEntrega()` y `asignarRepartidor()`, donde cada
  tipo de pedido define su propio comportamiento.
- **Sobrecarga** de `asignarRepartidor()` y `asignarRepartidor(String nombreRepartidor)`: la primera indica
  que se busca un repartidor y la segunda confirma la asignación de uno ya elegido.
- **Polimorfismo:** los pedidos se almacenan y procesan como `Pedido`, sin importar su tipo concreto.
- Cálculo del tiempo estimado de entrega con una fórmula distinta por tipo de pedido.
- Clase `Repartidor` que implementa **`Runnable`**: cada repartidor corre en su propio hilo y simula sus
  entregas una tras otra con una espera aleatoria de 1 a 3 segundos (`Thread.sleep`).
- Uso de **`ExecutorService`** (`Executors.newFixedThreadPool()`) con un hilo por repartidor activo, y de
  `shutdown()` y `awaitTermination()` para esperar a que todos terminen, con un tiempo máximo de espera.
- Uso de **`SwingWorker`** para ejecutar la simulación (bloqueante) fuera del hilo de la interfaz, y de
  `SwingUtilities.invokeLater()` para que los hilos de los repartidores envíen sus mensajes a la pantalla
  sin tocar la interfaz directamente.
- Asignación de repartidores en dos pasos: primero se respetan las asignaciones manuales y luego el resto
  de los pedidos se reparte entre el repartidor que tenga menos pedidos.
- Validación de los datos del formulario mediante la clase de utilidades `Validador`.
- Manejo de errores con excepciones (`IllegalArgumentException` e `IllegalStateException`) que las
  ventanas muestran al usuario en cuadros de diálogo.
- Manejo de `InterruptedException` al usar `sleep()` y `awaitTermination()`, restaurando la marca de
  interrupción del hilo.
- Comportamiento no determinista del orden de los mensajes durante la simulación, propio de la
  concurrencia gestionada por el *scheduler* del sistema operativo.

## 🧩 Estructuras utilizadas

| Tipo | Sintaxis | Descripción |
|------|----------|-------------|
| Clase abstracta | `abstract class Pedido` | Base de todos los pedidos. Guarda los datos comunes y declara métodos abstractos. |
| Herencia | `class PedidoComida extends Pedido` | Cada tipo de pedido reutiliza los datos de `Pedido` y agrega los suyos. |
| Interfaz | `interface Cancelable`, `Rastreable`, `Despachable` | Contratos que definen un comportamiento sin implementarlo. |
| Implementación múltiple | `implements Cancelable, Rastreable` | Una clase puede cumplir varios contratos a la vez. |
| Sobreescritura | `@Override` | Cada subclase redefine `mostrarResumen()`, `calcularTiempoEntrega()` y `asignarRepartidor()`. |
| Sobrecarga | `asignarRepartidor()` / `asignarRepartidor(String)` | Mismo nombre de método con distintos parámetros. |
| Polimorfismo | `List<Pedido>` | Una lista de la clase base guarda pedidos de cualquier tipo. |
| Encapsulamiento | atributos `private` + getters y setters | Los datos solo se acceden a través de métodos. |
| Clase de utilidades | `final class Validador` con constructor privado | Reúne métodos estáticos de validación y no se instancia. |
| Controlador único | `ControladorPedidos` | Una sola instancia compartida por todas las ventanas mantiene los datos en memoria. |
| Colecciones | `List`, `Map`, `Set` | Pedidos registrados, asignaciones (id → repartidor) y pedidos entregados. |
| Hilo por interfaz | `class Repartidor implements Runnable` | Convierte al repartidor en una tarea ejecutable dentro de un hilo. |
| Método de ejecución | `run()` | Código que corre cuando el pool lanza al repartidor: recorre y entrega sus pedidos. |
| Pool de hilos | `Executors.newFixedThreadPool(n)` | Conjunto fijo de hilos, uno por repartidor activo. |
| Lanzar tarea al pool | `executor.execute(repartidor)` | Envía cada repartidor al pool para su ejecución concurrente. |
| Pausa del hilo | `Thread.sleep(ms)` | Simula el tiempo de entrega de cada pedido (1000 a 2999 ms). |
| Cierre del pool | `executor.shutdown()` | Deja de aceptar tareas nuevas, pero permite terminar las que ya están corriendo. |
| Esperar finalización | `executor.awaitTermination(t, unidad)` | Espera a que terminen todos los hilos o se cumpla el tiempo máximo. |
| Callback de mensajes | `Consumer<String>` | Permite que el repartidor envíe sus mensajes a la consola o a la ventana. |
| Hilo de fondo en GUI | `SwingWorker<Boolean, Void>` | Ejecuta la simulación fuera del hilo de eventos y avisa al terminar (`done()`). |
| Hilo de eventos | `SwingUtilities.invokeLater()` | Encola las actualizaciones de pantalla en el hilo de eventos de Swing (EDT). |
| Excepciones | `IllegalArgumentException`, `IllegalStateException` | Señalan ID repetido, pedido inexistente o pedido ya entregado. |
| Ventanas | `JFrame` | Cada pantalla de la aplicación extiende de `JFrame`. |
| Distribución de componentes | `BorderLayout`, `GridLayout`, `FlowLayout`, `CardLayout` | Ordenan los componentes; `CardLayout` cambia los campos según el tipo de pedido. |
| Tabla | `JTable` + `DefaultTableModel` | Muestra el listado de pedidos en modo solo lectura. |
| Diálogos | `JOptionPane` | Muestra confirmaciones, advertencias y errores al usuario. |

## 📂 Estructura del proyecto

```
SpeedFast/
├── src/
│   ├── main/
│   │   └── Main.java                     # Punto de entrada: abre la ventana principal
│   ├── interfaces/
│   │   ├── Cancelable.java               # Contrato: cancelar() (Comida y Encomienda)
│   │   ├── Despachable.java              # Contrato: despachar() (Compra Express)
│   │   └── Rastreable.java               # Contrato: verHistorial() (Comida y Encomienda)
│   ├── model/
│   │   ├── Pedido.java                   # Clase base abstracta con los datos comunes
│   │   ├── PedidoComida.java             # Pedido de comida (restaurante, tiempo de preparación)
│   │   ├── PedidoEncomienda.java         # Pedido de encomienda (peso, volumen)
│   │   ├── PedidoExpress.java            # Pedido de compra express (tienda)
│   │   └── Repartidor.java               # Hilo (Runnable) que simula las entregas de un repartidor
│   ├── services/
│   │   ├── ControladorPedidos.java       # Registro, consulta y asignación de pedidos
│   │   └── ControladorRepartidores.java  # Lista de repartidores y simulación concurrente
│   ├── util/
│   │   └── Validador.java                # Validaciones del formulario
│   └── view/
│       ├── VentanaPrincipal.java         # Menú principal de la aplicación
│       ├── VentanaRegistroPedido.java    # Formulario para registrar pedidos
│       ├── VentanaListaPedidos.java      # Tabla con todos los pedidos
│       └── VentanaEntregas.java          # Asignación de repartidores e inicio de entregas
└── README.md
```

## ▶️ Funcionamiento

Al ejecutar `Main.java` se realizan las siguientes acciones en orden:

1. `SwingUtilities.invokeLater()` crea y muestra la `VentanaPrincipal` en el hilo de eventos de Swing.
2. La `VentanaPrincipal` crea **una única instancia** de `ControladorPedidos` y de `ControladorRepartidores`,
   que comparte con todas las ventanas que abre. Así, todas trabajan sobre los mismos datos.
3. `ControladorRepartidores` deja registrados tres repartidores de ejemplo: `Carlos`, `Fernanda` y `Matias`.
4. En **Registrar pedido**, el formulario valida los datos con `Validador` y crea el pedido según el tipo
   elegido (`registrarComida()`, `registrarEncomienda()` o `registrarExpress()`). Si el ID ya existe, el
   controlador lanza una excepción y la ventana muestra el error.
5. En **Asignar repartidor**, `asignarRepartidor(id, nombre)` guarda la asignación y devuelve el mensaje que
   arma el propio pedido (sobrecarga), que cambia según el tipo (por ejemplo, mochila térmica para comida o
   transporte especial para encomiendas de 100 kg o más).
6. En **Iniciar entregas** se ejecutan estos pasos:
   - Se toman los pedidos pendientes y las asignaciones manuales.
   - Se crean copias de los repartidores con listas vacías, para que no se acumulen pedidos entre simulaciones.
   - Se respetan las asignaciones manuales y el resto de los pedidos se reparte entre quien tenga menos.
   - Solo salen a ruta los repartidores que tienen pedidos.
   - Se crea un `ExecutorService` con un hilo por repartidor activo y se lanza cada `Repartidor` con
     `executor.execute()`. A partir de aquí **nace la concurrencia real**.
   - Cada `Repartidor`, dentro de su `run()`, recorre sus pedidos, espera entre 1 y 3 segundos por entrega y
     envía sus mensajes a la pantalla mediante `invokeLater()`.
7. El controlador llama a `executor.shutdown()` y luego a `executor.awaitTermination()`, con un tiempo
   máximo de `pedidos del repartidor más cargado × 3 s + 5 s`. Si se excede, la simulación se detiene.
8. Al terminar, el `SwingWorker` ejecuta `done()` en el hilo de eventos: marca los pedidos como
   **Entregados**, actualiza la lista de pendientes y vuelve a habilitar los controles.

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
> exacto de los mensajes de `Carlos`, `Fernanda` y `Matias` **no está garantizado** y puede variar entre
> ejecuciones, dependiendo de cómo el *scheduler* del sistema operativo reparta el tiempo de CPU.

> ℹ️ **Nota sobre los datos:** los pedidos se guardan solo en memoria. Al cerrar la aplicación se pierden.

## 📖 Manual de usuario

### Requisitos

- **Java JDK 8 o superior** instalado.
- Un IDE (IntelliJ IDEA, Eclipse, NetBeans) o una terminal.

### Cómo ejecutar

**Desde un IDE:** abrir el proyecto y ejecutar la clase `main.Main`.

**Desde la terminal**, ubicado en la carpeta del proyecto:

```
javac -d out $(find src -name "*.java")
java -cp out main.Main
```

### Ventana principal

Al iniciar aparece el menú **SpeedFast - Gestión de Entregas** con tres botones:

| Botón | Qué hace |
|-------|----------|
| Registrar pedido | Abre el formulario para crear un pedido nuevo. |
| Listar pedidos | Abre la tabla con todos los pedidos y su estado. |
| Asignar repartidor / Iniciar entrega | Abre la ventana para asignar repartidores y simular las entregas. |

Si una ventana ya está abierta, el botón solo la trae al frente. **Cerrar la ventana principal termina la
aplicación.** Cerrar cualquiera de las otras ventanas no afecta al resto.

### 1. Registrar un pedido

1. Presione **Registrar pedido**.
2. Complete los campos comunes: **ID**, **Cliente**, **Dirección** y **Distancia (km)**.
3. Elija el **Tipo de pedido**. Los campos específicos cambian según la opción:

| Tipo | Campos específicos |
|------|--------------------|
| Comida | Restaurante y Tiempo de preparación |
| Encomienda | Peso (kg) y Volumen (m3) |
| Express | Tienda |

4. Presione **Guardar** (o la tecla **Enter**). Si todo es correcto, aparece un resumen del pedido con su
   tiempo estimado de entrega y el formulario se limpia para registrar otro.
5. **Limpiar** vacía todos los campos. **Cerrar** cierra la ventana.

**Reglas de los campos:**

- **ID:** obligatorio, solo letras, números y guiones (sin espacios), máximo 10 caracteres y no puede
  repetirse (no distingue mayúsculas de minúsculas).
- **Distancia, Peso y Volumen:** números mayores que 0, con hasta 6 dígitos enteros y 3 decimales. Se
  acepta punto o coma (`3.5` o `3,5`).
- **Cliente, Dirección, Restaurante, Tiempo de preparación y Tienda:** obligatorios.

Si algún dato es inválido, se muestra un mensaje con todos los errores a corregir y el pedido no se guarda.

### 2. Consultar los pedidos

1. Presione **Listar pedidos**.
2. La tabla muestra: ID, Tipo, Cliente, Dirección, Distancia (km), Tiempo estimado (min) y Estado.
3. La tabla se actualiza sola al abrirla y al volver a la ventana. También puede usar **Refrescar**.
4. Abajo a la izquierda se muestra el **total de pedidos** registrados.

**Estados posibles:**

| Estado | Significado |
|--------|-------------|
| Pendiente | El pedido está registrado y aún no tiene repartidor. |
| Asignado a (nombre) | Se eligió un repartidor manualmente. Aún no se entrega. |
| Entregado | La simulación de entregas ya lo completó. |

### 3. Asignar repartidores (opcional)

1. Presione **Asignar repartidor / Iniciar entrega**.
2. En **Pedido**, elija uno de los pedidos pendientes.
3. En **Repartidor**, elija a `Carlos`, `Fernanda` o `Matias`.
4. Presione **Asignar repartidor**. En el área **Salida** aparece el detalle de la asignación.

Este paso es opcional. Los pedidos que no se asignen manualmente se reparten automáticamente entre los
repartidores al iniciar las entregas. Si asigna dos veces un mismo pedido, queda el último repartidor elegido.

### 4. Iniciar las entregas

1. En la misma ventana, presione **Iniciar entregas**.
2. Los controles se bloquean mientras dura la simulación.
3. En el área **Salida** aparece en tiempo real el avance de cada repartidor. Cada entrega tarda entre 1 y
   3 segundos.
4. Al terminar, aparece el mensaje *"Simulacion finalizada"*. Los pedidos pasan a estado **Entregado** y
   salen de la lista de pendientes.
5. **Limpiar salida** borra el texto del área de salida. **Cerrar** cierra la ventana.

**Ejemplo de salida** (el orden puede variar entre ejecuciones):

```
Iniciando simulacion de entregas concurrentes:
---------------------------------------------
Repartidor Carlos inicia su ruta con 1 pedido(s).
Repartidor Fernanda inicia su ruta con 1 pedido(s).
[Carlos] Iniciando entrega del Pedido N°: 001 (Comida)
[Fernanda] Iniciando entrega del Pedido N°: 002 (Encomienda)
[Fernanda] Pedido N°: 002 entregado con éxito
[Carlos] Pedido N°: 001 entregado con éxito
Repartidor: Fernanda ha finalizado todas sus entregas
Repartidor: Carlos ha finalizado todas sus entregas
---------------------------------------------
Simulacion finalizada: Todos los repartidores completaron sus rutas.
```

### Flujo recomendado

1. Registrar uno o más pedidos.
2. (Opcional) Revisarlos en **Listar pedidos**.
3. (Opcional) Asignar repartidores manualmente.
4. Iniciar las entregas y observar la salida.
5. Volver a **Listar pedidos** y presionar **Refrescar** para ver los pedidos como **Entregado**.

### Mensajes y problemas frecuentes

| Mensaje | Causa | Solución |
|---------|-------|----------|
| "Corrija los siguientes datos" | Hay campos vacíos o con formato inválido. | Revise cada punto de la lista y corrija el campo indicado. |
| "Ya existe un pedido con el ID ..." | El ID ya fue registrado. | Use un ID distinto. |
| "Seleccione un pedido y un repartidor." | Falta elegir alguno de los dos en los combos. | Elija ambos y vuelva a intentar. |
| "No hay pedidos pendientes para entregar." | No hay pedidos registrados o todos ya fueron entregados. | Registre un pedido nuevo. |
| Botones de asignar e iniciar deshabilitados | No hay pedidos pendientes o hay una simulación en curso. | Registre un pedido o espere a que termine la simulación. |
| "La simulacion excedio el tiempo maximo y fue detenida." | La simulación tardó más del tiempo permitido. | Vuelva a iniciar las entregas. |
