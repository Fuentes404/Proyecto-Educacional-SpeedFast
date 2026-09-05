 # SpeedFast - Simulación de Entregas Concurrentes con Hilos en Java

Este proyecto simula el sistema de reparto de la empresa **SpeedFast**, incorporando programación concurrente
mediante **`Thread`**, **`Runnable`** y **`ExecutorService`**. Además de la gestión clásica de pedidos
(comida, encomiendas y compras express), el sistema permite simular la entrega simultánea de múltiples
repartidores, cada uno operando como un hilo independiente que procesa su propia lista de pedidos.

## 📋 Descripción

- Modelado de un sistema orientado a objetos con una clase abstracta `Pedido` y tres especializaciones:
  `PedidoComida`, `PedidoEncomienda` y `PedidoExpress`.
- Uso de interfaces (`Cancelable`, `Despachable`, `Rastreable`) para definir comportamientos opcionales
  según el tipo de pedido, verificados en tiempo de ejecución mediante `instanceof`.
- Creación de hilos mediante **implementación de `Runnable`**: la clase `Repartidor` *usa* un hilo, no lo hereda,
  lo que permite ejecutarla dentro de un pool de hilos administrado.
- Sobreescritura del método `run()`, el código que se ejecuta automáticamente cuando el hilo es lanzado.
- Uso de **`ExecutorService`** (`Executors.newFixedThreadPool()`) para lanzar la ejecución concurrente real de
  varios repartidores a la vez, en lugar de crear y controlar hilos manualmente con `start()`.
- Pausas independientes por hilo mediante `Thread.sleep(ms)` con valores aleatorios, sin bloquear a los demás
  repartidores.
- Manejo obligatorio de `InterruptedException` al usar `sleep()` (excepción *checked*).
- Cierre y sincronización del pool de hilos mediante `shutdown()` y `awaitTermination()`, para esperar la
  finalización de todos los repartidores antes de continuar.
- Comportamiento no determinista del orden de impresión en consola, propio de la concurrencia gestionada por
  el *scheduler* del sistema operativo.

## 🧩 Estructuras utilizadas

| Tipo | Sintaxis | Descripción |
|------|----------|-------------|
| Clase abstracta | `abstract class Pedido` | Define atributos y comportamiento común a todos los pedidos. |
| Interfaz | `interface Cancelable / Despachable / Rastreable` | Declaran comportamientos opcionales según el tipo de pedido. |
| Hilo por interfaz | `class Repartidor implements Runnable` | Convierte la clase en una tarea ejecutable dentro de un hilo, sobreescribiendo `run()`. |
| Método de ejecución | `run()` | Código que se ejecuta automáticamente cuando el hilo es lanzado por el `ExecutorService`. |
| Pool de hilos | `Executors.newFixedThreadPool(n)` | Crea un conjunto fijo de hilos para ejecutar tareas en paralelo. |
| Lanzar tarea al pool | `executor.execute(repartidor)` | Envía un `Runnable` al pool para que sea ejecutado por un hilo disponible. |
| Pausa del hilo | `Thread.sleep(ms)` | Pausa únicamente el hilo que la invoca, simulando el tiempo de entrega. |
| Excepción de interrupción | `try/catch (InterruptedException e)` | Captura la interrupción que puede ocurrir durante `sleep()`. |
| Cierre del pool | `executor.shutdown()` | Indica que no se aceptarán nuevas tareas una vez finalizadas las actuales. |
| Esperar finalización | `executor.awaitTermination(t, unidad)` | Bloquea el hilo principal hasta que todas las tareas terminen o se cumpla el tiempo límite. |

## 📂 Estructura del proyecto

```
SistemaSpeedFast/
├── src/
│   ├── ui/
│   │   └── Main.java                      # Clase principal: menú de consola y lanzamiento de la simulación concurrente
│   ├── services/
│   │   ├── ControladorEnvios.java         # Administra el ciclo de vida de los pedidos (crear, cancelar, despachar, buscar)
│   │   └── Repartidor.java                # Hilo (Runnable) que entrega secuencialmente sus pedidos asignados
│   ├── model/
│   │   ├── Pedido.java                    # Clase abstracta base
│   │   ├── PedidoComida.java              # Pedido de comida (Cancelable, Rastreable)
│   │   ├── PedidoEncomienda.java          # Pedido de encomienda (Cancelable, Rastreable)
│   │   └── PedidoExpress.java             # Compra express (Despachable)
│   └── interfaces/
│       ├── Cancelable.java
│       ├── Despachable.java
│       └── Rastreable.java
└── README.md
```

## ▶️ Funcionamiento

Al ejecutar el programa se realizan las siguientes acciones en orden:

1. Se muestra el **menú principal** por consola, con las opciones de gestión de pedidos y la simulación
   concurrente. El programa permanece en un bucle hasta que el usuario elige `0. Salir`.
2. El usuario puede **crear pedidos** (opción 1) de tres tipos: Comida, Encomienda o Express. Cada creación
   invoca `asignarRepartidor()`, propio de cada subclase, y agrega el pedido al listado de vigentes.
3. Los pedidos que implementan `Cancelable` pueden ser **cancelados** (opción 2), moviéndose del listado de
   vigentes al historial. Lo mismo ocurre con los pedidos `Despachable` (opción 3, solo Express).
4. Las opciones 4, 5 y 6 permiten **consultar** el estado del sistema: pedidos vigentes, historial completo,
   o la búsqueda puntual de un pedido por su número (disponible si implementa `Rastreable`).
5. Al elegir la opción **7. Simular Entregas Concurrentes**, el sistema toma todos los pedidos vigentes y los
   reparte en forma **round-robin** entre tres repartidores fijos (`Carlos`, `Fernanda`, `Matias`).
6. Se crea un `ExecutorService` con un pool de tamaño igual a la cantidad de repartidores, y cada `Repartidor`
   se envía al pool mediante `executor.execute()`. A partir de aquí **nace la concurrencia real**: los tres
   repartidores procesan sus pedidos en paralelo.
7. Cada `Repartidor`, dentro de su `run()`, recorre secuencialmente su propia lista de pedidos, simulando el
   tiempo de entrega con `Thread.sleep()` (entre 1 y 3 segundos aleatorios) e imprimiendo mensajes de avance
   e inicio/fin por cada pedido.
8. El hilo principal llama a `executor.shutdown()` y luego `executor.awaitTermination(10, TimeUnit.SECONDS)`,
   quedando bloqueado hasta que **todos** los repartidores terminen sus entregas (o se cumplan los 10 segundos).
9. Finalmente, se imprime un mensaje de cierre confirmando que la simulación terminó, y el usuario vuelve al
   menú principal.

> ⚠️ **Nota sobre el orden de salida:** debido a que los repartidores corren de forma concurrente dentro del
> pool de hilos, el orden exacto en que se intercalan los mensajes de `Carlos`, `Fernanda` y `Matias` **no está
> garantizado** y puede variar entre ejecuciones, dependiendo de cómo el *scheduler* del sistema operativo y el
> `Thread.sleep()` aleatorio repartan el tiempo de CPU.

---

## 📖 Manual de Usuario

A continuación se describe el uso de cada opción del menú principal, disponible al ejecutar `Main.java`.

```
------ MENÚ SpeedFast ------
1. Crear Pedido
2. Cancelar Pedido
3. Despachar Pedido
4. Ver Pedidos Vigentes
5. Ver Historial de Pedidos
6. Buscar Pedido
7. Simular Entregas Concurrentes
0. Salir
```

### Opción 1 — Crear Pedido
Abre un submenú para elegir el tipo de pedido a registrar:

- **1. Pedido de Comida:** solicita cliente, dirección, distancia (km), restaurante y tiempo de preparación.
  El sistema calcula el tiempo de entrega (15 min base + 2 min por km) y busca un repartidor con mochila térmica.
- **2. Encomienda:** solicita cliente, dirección, distancia (km), peso (kg) y volumen (m³). El tiempo de
  entrega se calcula con 20 min base + 1,5 min por km; si el peso es ≥ 100 kg, se indica que requiere
  transporte especial.
- **3. Pedido Express:** solicita cliente, dirección, distancia (km) y tienda. El tiempo de entrega es de
  10 min, sumando 5 min extra si la distancia supera los 5 km.
- **0. Volver:** regresa al menú principal.

Cada pedido creado recibe automáticamente un número correlativo (`idPedido`) y queda registrado como vigente.

### Opción 2 — Cancelar Pedido
Muestra todos los pedidos que implementan `Cancelable` (Comida y Encomienda). El usuario ingresa el número
del pedido a cancelar; el sistema lo mueve del listado de vigentes al historial e informa al cliente.
Ingresando `0` se vuelve al menú principal. Los pedidos Express no pueden cancelarse por este medio.

### Opción 3 — Despachar Pedido
Muestra todos los pedidos que implementan `Despachable` (solo Express). El usuario ingresa el número del
pedido a despachar; el sistema lo marca como despachado, lo mueve al historial e informa la dirección de
entrega. Ingresando `0` se vuelve al menú principal.

### Opción 4 — Ver Pedidos Vigentes
Lista todos los pedidos que aún no han sido cancelados ni despachados, mostrando tipo, número, cliente,
dirección y distancia de cada uno. Si no hay pedidos vigentes, se informa explícitamente. Ingresar `0`
para volver.

### Opción 5 — Ver Historial de Pedidos
Lista todos los pedidos que ya fueron cancelados o despachados (historial). Si el historial está vacío,
se informa explícitamente. Ingresar `0` para volver.

### Opción 6 — Buscar Pedido
Solicita el número de un pedido y lo busca primero entre los vigentes y luego en el historial. Si el pedido
implementa `Rastreable` (Comida o Encomienda), se muestra su estado actual; los pedidos Express no son
rastreables por esta vía.

### Opción 7 — Simular Entregas Concurrentes
Toma todos los pedidos vigentes en el sistema y los distribuye equitativamente (round-robin) entre tres
repartidores fijos: **Carlos, Fernanda y Matias**. Luego los ejecuta en paralelo mediante un
`ExecutorService`, simulando la entrega real de cada uno con tiempos de espera aleatorios. El programa espera
hasta que todos los repartidores terminen (máximo 10 segundos) antes de mostrar el mensaje de finalización.
Si no hay pedidos vigentes, se informa y la simulación no se ejecuta.

### Opción 0 — Salir
Finaliza la ejecución del programa y cierra el menú principal.
