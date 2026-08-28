
  # Ejemplo de Herencia, Polimorfismo, Abstracción e Interfaces en Java - Sistema de Entregas SpeedFast

Este ejercicio combina el uso de una **clase abstracta** que define el comportamiento común de un pedido, con **polimorfismo** aplicado mediante sobrecarga y sobrescritura de métodos, y **interfaces** que desacoplan responsabilidades funcionales específicas (cancelar, despachar y rastrear) de la jerarquía de clases.

## 📋 Descripción

- Uso de una clase abstracta `Pedido` que concentra los atributos y el comportamiento común a todo pedido, incluyendo un método ya implementado (`mostrarResumen()`) y métodos abstractos que cada subclase debe completar.
- Tres subclases (`PedidoComida`, `PedidoEncomienda`, `PedidoExpress`) que heredan de `Pedido` y reutilizan sus atributos y métodos mediante `super()` y los getters/setters heredados.
- **Sobrescritura (`@Override`)** del método `asignarRepartidor()` en cada subclase, con lógica de negocio distinta según el tipo de pedido.
- **Sobrecarga** del método `asignarRepartidor(String nombreRepartidor)`, que coexiste con la versión sin parámetros y permite asignar un repartidor específico por nombre.
- Implementación del método abstracto `calcularTiempoEntrega()`, con una fórmula distinta en cada subclase (tiempo base + variable según distancia, peso o tipo de producto).
- Tres **interfaces** (`Cancelable`, `Despachable`, `Rastreable`) que declaran una responsabilidad funcional específica, implementadas solo por las clases que realmente la necesitan.
- Uso de `instanceof` en la clase `ControladorEnvios` para verificar si un pedido implementa una interfaz antes de invocar su método (por ejemplo, solo se puede despachar un pedido si implementa `Despachable`).
- Separación de responsabilidades en paquetes: `model` (entidades del dominio), `interfaces` (contratos funcionales), `services` (lógica de negocio y control del flujo) y `ui` (interacción con el usuario por consola).
- Uso de dos `ArrayList<Pedido>` en `ControladorEnvios`: uno para los pedidos vigentes y otro que actúa como historial, donde solo ingresan los pedidos cuyo ciclo se cerró (despachados o cancelados).
- Menú de consola con `Scanner` y bucles `do-while` anidados, que permite crear, cancelar, despachar, listar y rastrear pedidos sin detener la ejecución del programa.

## 🧩 Estructuras utilizadas

| Tipo | Sintaxis | Descripción |
|------|----------|-------------|
| Clase abstracta | `public abstract class Pedido { ... }` | Define atributos, métodos concretos y métodos abstractos comunes a todas las subclases. |
| Método abstracto | `public abstract double calcularTiempoEntrega();` | Obliga a cada subclase a implementar su propia lógica de cálculo. |
| Herencia | `class PedidoComida extends Pedido` | Permite que las subclases reutilicen atributos y comportamientos de `Pedido`. |
| Sobrescritura | `@Override public void asignarRepartidor() { ... }` | Redefine el comportamiento heredado según el tipo de pedido. |
| Sobrecarga | `asignarRepartidor()` / `asignarRepartidor(String nombre)` | Dos versiones del mismo método con distinta firma. |
| Interfaz | `public interface Cancelable { void cancelar(); }` | Declara un contrato funcional que una clase puede implementar. |
| Implementación de interfaz | `class PedidoComida implements Cancelable, Rastreable` | Una clase puede implementar varias interfaces a la vez. |
| Verificación de tipo | `if (pedido instanceof Cancelable) { ... }` | Comprueba en tiempo de ejecución si un objeto implementa una interfaz antes de operar sobre él. |
| Colección dinámica | `ArrayList<Pedido> pedidos = new ArrayList<>();` | Almacena los pedidos vigentes y el historial de pedidos cerrados. |
| Bucle do-while | `do { ... } while (opcion != 0);` | Mantiene el menú activo hasta que el usuario decide salir. |
| package/import | `package services;` / `import model.Pedido;` | Organiza el código en módulos y permite reutilizar clases de otros paquetes. |

## 📂 Estructura del proyecto

```
proyecto/
├── src/
│   ├── main/
│   │   └── java/
│   │       ├── interfaces/
│   │       │   ├── Cancelable.java       # Contrato para pedidos que se pueden cancelar
│   │       │   ├── Despachable.java      # Contrato para pedidos que se pueden despachar
│   │       │   └── Rastreable.java       # Contrato para pedidos que se pueden rastrear
│   │       ├── model/
│   │       │   ├── Pedido.java           # Clase abstracta base
│   │       │   ├── PedidoComida.java     # Hereda de Pedido, implementa Cancelable y Rastreable
│   │       │   ├── PedidoEncomienda.java # Hereda de Pedido, implementa Cancelable y Rastreable
│   │       │   └── PedidoExpress.java    # Hereda de Pedido, implementa Despachable
│   │       ├── services/
│   │       │   └── ControladorEnvios.java # Lógica de negocio: crear, cancelar, despachar, listar y rastrear pedidos
│   │       └── ui/
│   │           └── Main.java             # Clase principal: menú de consola y control del flujo
│   └── test/
├── .gitignore
└── pom.xml
```

## ▶️ Funcionamiento

Al ejecutar el programa se realizan las siguientes acciones:

1. Se muestra el menú principal de **SpeedFast** con las opciones: crear pedido, cancelar pedido, despachar pedido, ver pedidos vigentes, ver historial y buscar pedido.
2. Al **crear un pedido**, un submenú permite elegir entre Comida, Encomienda o Compra Express; según el tipo elegido se solicitan datos distintos (restaurante y tiempo de preparación, o peso y volumen, o tienda) y se invoca la sobrecarga correspondiente de `crearPedido()` en `ControladorEnvios`.
3. Cada pedido nuevo asigna automáticamente un repartidor mediante `asignarRepartidor()`, con un mensaje distinto según el tipo de pedido (mochila térmica para comida, transporte especial si la encomienda supera cierto peso, o el repartidor más cercano para express).
4. Al **cancelar un pedido**, el controlador verifica con `instanceof` si el pedido implementa `Cancelable`; si es así, se ejecuta su lógica de cancelación y el pedido pasa de la lista de vigentes al historial.
5. Al **despachar un pedido**, se aplica la misma verificación pero con la interfaz `Despachable`, exclusiva de `PedidoExpress` en este diseño; el pedido también pasa al historial una vez despachado.
6. La opción **ver pedidos vigentes** recorre solo los pedidos que aún no han sido cerrados (ni cancelados ni despachados).
7. La opción **ver historial** recorre `pedidosTerminados`, que solo contiene pedidos cuyo ciclo de vida ya se cerró.
8. La opción **buscar pedido** permite ingresar un número de pedido y, si la clase implementa `Rastreable`, muestra su información de seguimiento; si no la implementa (como `PedidoExpress`), informa que ese tipo de pedido no admite rastreo.
9. El menú se repite indefinidamente mediante un bucle `do-while` hasta que el usuario elige la opción `0` para salir.

`Main` concentra toda la interacción con el usuario y el control del flujo del programa, mientras que `ControladorEnvios` encapsula la lógica de negocio: crear, cancelar, despachar y rastrear pedidos, delegando en cada subclase de `Pedido` el comportamiento específico que le corresponde. Las interfaces permiten que cada clase exponga únicamente las operaciones que realmente le aplican, sin forzar comportamientos comunes que no todos los tipos de pedido necesitan.
