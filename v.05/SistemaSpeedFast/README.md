# SpeedFast - Simulación de Zona de Carga con Productor-Consumidor en Java

Este proyecto simula el sistema de despacho de pedidos de **SpeedFast**, aplicando el patrón
**Productor-Consumidor** mediante programación concurrente en Java con **`Thread`**, **`Runnable`**,
**`ExecutorService`** y sincronización manual con **`wait()`/`notifyAll()`**. Un único punto central,
la `ZonaDeCarga`, actúa como cola compartida entre el hilo principal (que registra los pedidos) y
varios repartidores (que los retiran y entregan de forma concurrente).

## 📋 Descripción

- Modelado simple de un pedido (`Pedido`) con un estado representado por el enum `EstadoPedido`
  (`PENDIENTE`, `EN_REPARTO`, `ENTREGADO`).
- Implementación del patrón **Productor-Consumidor** mediante la clase `ZonaDeCarga`, que encapsula
  una cola (`Queue<Pedido>`) protegida con métodos `synchronized`.
- Sincronización manual entre hilos usando **`wait()`** (el repartidor espera si no hay pedidos) y
  **`notifyAll()`** (se despierta a los repartidores cuando se agrega un pedido o se cierra la zona).
- Uso de una bandera de cierre (`cerrada`) como señal de "no vendrán más pedidos", que permite a los
  repartidores terminar su ejecución de forma prolija cuando la cola se vacía.
- Creación de hilos mediante **implementación de `Runnable`**: la clase `Repartidor` *usa* un hilo,
  no lo hereda, lo que permite ejecutarla dentro de un pool de hilos administrado.
- Sobreescritura del método `run()`, el código que se ejecuta automáticamente cuando el hilo es
  lanzado por el pool.
- Uso de **`ExecutorService`** (`Executors.newFixedThreadPool()`) para lanzar la ejecución concurrente
  real de varios repartidores a la vez, en lugar de crear y controlar hilos manualmente con `start()`.
- Pausa fija del hilo mediante `Thread.sleep(1000)` para simular el tiempo de entrega de cada pedido,
  sin bloquear a los demás repartidores.
- Manejo obligatorio de `InterruptedException` al usar `sleep()` y `wait()` (excepciones *checked*).
- Cierre y sincronización del pool de hilos mediante `shutdown()` y `awaitTermination()`, para esperar
  la finalización de todos los repartidores antes de continuar.
- Comportamiento no determinista del orden de impresión en consola, propio de la concurrencia
  gestionada por el *scheduler* del sistema operativo.

## 🧩 Estructuras utilizadas

| Tipo | Sintaxis | Descripción |
|------|----------|-------------|
| Enum | `enum EstadoPedido` | Define los estados posibles de un pedido. |
| Clase de datos | `class Pedido` | Encapsula id, dirección de entrega y estado del pedido. |
| Cola compartida sincronizada | `class ZonaDeCarga` | Actúa como buffer entre productor (Main) y consumidores (Repartidores), protegido con `synchronized`. |
| Espera condicional | `wait()` dentro de `while` | Bloquea al hilo consumidor mientras la cola esté vacía y no se haya cerrado la zona. |
| Notificación | `notifyAll()` | Despierta a todos los hilos en espera al agregar un pedido o cerrar la zona. |
| Señal de fin | `boolean cerrada` | Indica que no se agregarán más pedidos, permitiendo a los repartidores terminar su ciclo. |
| Hilo por interfaz | `class Repartidor implements Runnable` | Convierte la clase en una tarea ejecutable dentro de un hilo, sobreescribiendo `run()`. |
| Método de ejecución | `run()` | Código que se ejecuta automáticamente cuando el hilo es lanzado por el `ExecutorService`. |
| Pool de hilos | `Executors.newFixedThreadPool(n)` | Crea un conjunto fijo de hilos para ejecutar tareas en paralelo. |
| Lanzar tarea al pool | `executor.execute(repartidor)` | Envía un `Runnable` al pool para que sea ejecutado por un hilo disponible. |
| Pausa del hilo | `Thread.sleep(ms)` | Pausa únicamente el hilo que la invoca, simulando el tiempo de entrega. |
| Excepción de interrupción | `try/catch (InterruptedException e)` | Captura la interrupción que puede ocurrir durante `sleep()` o `wait()`. |
| Cierre del pool | `executor.shutdown()` | Indica que no se aceptarán nuevas tareas una vez finalizadas las actuales. |
| Esperar finalización | `executor.awaitTermination(t, unidad)` | Bloquea el hilo principal hasta que todas las tareas terminen o se cumpla el tiempo límite. |

## 📂 Estructura del proyecto

```
SpeedFast/
├── src/
│   ├── ui/
│   │   └── Main.java                # Clase principal: registra los pedidos y lanza a los repartidores
│   └── model/
│       ├── Pedido.java              # Datos del pedido (id, dirección, estado)
│       ├── EstadoPedido.java        # Enum con los estados posibles de un pedido
│       ├── ZonaDeCarga.java         # Cola compartida sincronizada (productor-consumidor)
│       └── Repartidor.java          # Hilo (Runnable) que retira y entrega pedidos de la ZonaDeCarga
└── README.md
```

## ▶️ Funcionamiento

Al ejecutar `Main.java` se realizan las siguientes acciones en orden:

1. Se crea una única instancia de `ZonaDeCarga`, compartida entre el hilo principal y todos los
   repartidores.
2. El hilo principal actúa como **productor**: registra 5 pedidos en estado `PENDIENTE` mediante
   `agregarPedido()`. Cada llamada agrega el pedido a la cola interna y notifica a los repartidores
   que puedan estar esperando.
3. Una vez registrados todos los pedidos, se llama a `zonaDeCarga.cerrar()`. Esto marca la zona como
   **cerrada** y notifica a los hilos en espera, indicándoles que no llegarán más pedidos.
4. Se crea un `ExecutorService` con un pool fijo de **3 hilos**, y se lanzan tres repartidores
   (`Pablo`, `Lourdes` y `Sebastian`) mediante `executor.execute()`. A partir de aquí **nace la
   concurrencia real**: los tres repartidores compiten por retirar pedidos de la misma cola.
5. Cada `Repartidor`, dentro de su `run()`, llama repetidamente a `zonaDeCarga.retirarPedido()`:
   - Si hay pedidos disponibles, retira uno, lo marca como `EN_REPARTO`, simula el tiempo de entrega
     con `Thread.sleep(1000)` y finalmente lo marca como `ENTREGADO`.
   - Si la cola está vacía pero la zona **no** está cerrada, el repartidor queda en `wait()` hasta
     que se agregue un nuevo pedido o se cierre la zona.
   - Si la cola está vacía y la zona **ya** está cerrada, `retirarPedido()` devuelve `null` y el
     repartidor termina su ejecución.
6. El hilo principal llama a `executor.shutdown()` y luego `executor.awaitTermination(1, TimeUnit.MINUTES)`,
   quedando bloqueado hasta que **todos** los repartidores terminen de procesar sus pedidos (o se
   cumpla el minuto límite).
7. Finalmente, se imprime un mensaje confirmando que todos los pedidos fueron entregados.

> ⚠️ **Nota sobre el orden de salida:** debido a que los tres repartidores corren de forma concurrente
> y compiten por la misma cola de pedidos, el orden exacto en que `Pablo`, `Lourdes` y `Sebastian`
> retiran y entregan cada pedido **no está garantizado** y puede variar entre ejecuciones, dependiendo
> de cómo el *scheduler* del sistema operativo reparta el tiempo de CPU.

> ℹ️ Este proyecto no cuenta con un menú interactivo: todas las instancias de `Pedido` se crean
> directamente en `Main.java` al momento de ejecutar el programa, por lo que no aplica un manual de
> usuario.
