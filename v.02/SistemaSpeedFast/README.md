
# Sistema de Reparto SpeedFast - Clases abstractas, Sobreescritura y Sobrecarga de Métodos - Herencia y Polimorfismo

Sistema de gestión de pedidos desarrollado en Java que modela distintos tipos de pedidos (comida, encomiendas y compras express) mediante clases organizadas en paquetes, aplicando herencia, sobreescritura (override) y sobrecarga (overload) de métodos para el cálculo del tiempo de entrega y la asignación de repartidores.

## 📋 Descripción

- Separación del código en `package model` (clases de dominio) y `package ui` (clase de ejecución).
- Clase base abstracta `Pedido` con atributos encapsulados mediante modificador `private` (`idPedido`, `cliente`, `direccion`, `distanciaKm`, `tipoPedido`) y expuestos a través de métodos `getter`/`setter`.
- Herencia mediante `extends` de `PedidoComida`, `PedidoEncomienda` y `PedidoExpress` respecto de `Pedido`.
- Uso de `super()` en los constructores de las subclases para inicializar los atributos heredados (`idPedido`, `cliente`, `direccion`, `distanciaKm`).
- Atributos propios y encapsulados en cada subclase (`restaurante`/`tiempoPreparacion`, `peso`/`volumen`, `tienda`).
- `Pedido` declara tres métodos **abstractos** que obligan a cada subclase a implementarlos con su propia lógica: `calcularTiempoEntrega()`, `asignarRepartidor()` y `asignarRepartidor(String)` (sobrecarga: mismo nombre, distinta lista de parámetros).
- Sobreescritura (`@Override`) de los tres métodos abstractos en cada una de las tres subclases, redefiniendo el comportamiento según el tipo concreto de pedido:
  - `PedidoComida`: tiempo de entrega = 15 min base + 2 min/km; repartidor con mochila térmica.
  - `PedidoEncomienda`: tiempo de entrega = 20 min base + 1.5 min/km; repartidor con transporte especial si `peso >= 100 kg`.
  - `PedidoExpress`: tiempo de entrega = 10 min base (+5 min si `distanciaKm > 5`); repartidor más cercano con disponibilidad inmediata.
- Uso de condicionales dentro de los métodos sobreescritos para variar la respuesta según el estado del objeto (por ejemplo, validación de `peso` en `PedidoEncomienda`, validación de `distanciaKm` en `PedidoExpress`).
- Método concreto `mostrarResumen()` heredado por todas las subclases, que imprime los datos comunes del pedido.
- Arreglo polimórfico de tipo `Pedido[]` que almacena instancias de las distintas subclases bajo el tipo de la superclase común.
- Recorrido con `for-each` que invoca los métodos sobreescritos y sobrecargados sobre cada elemento del arreglo, dejando que la resolución del comportamiento ocurra en tiempo de ejecución.

## 📂 Estructura del proyecto
```
sistema-pedidos/
├── .idea/
├── src/
│   ├── model/
│   │   ├── Pedido.java              # Clase abstracta base: atributos comunes, mostrarResumen() y métodos abstractos
│   │   ├── PedidoComida.java        # extends Pedido; implementa calcularTiempoEntrega() y asignarRepartidor() con mochila térmica
│   │   ├── PedidoEncomienda.java    # extends Pedido; implementa calcularTiempoEntrega() y asignarRepartidor() validando el peso de la carga
│   │   └── PedidoExpress.java       # extends Pedido; implementa calcularTiempoEntrega() y asignarRepartidor() según la distancia a la tienda
│   └── ui/
│       └── Main.java                # Clase principal: crea los pedidos y recorre el arreglo polimórfico Pedido[]
└── .gitignore
```

## ▶️ Funcionamiento

Al ejecutar el programa se realizan las siguientes acciones:

1. Se crean tres objetos, uno por cada subclase concreta: `PedidoComida` (`p1`), `PedidoEncomienda` (`p2`) y `PedidoExpress` (`p3`). `Pedido` no se instancia directamente porque es una clase abstracta; cada subclase inicializa sus atributos heredados mediante `super()` y los propios en su propio constructor.
2. Los tres objetos se agrupan en un arreglo de tipo `Pedido[]`, que almacena referencias de distintas subclases bajo el tipo de la superclase común.
3. Se recorre el arreglo con `for-each`, y para cada elemento se invoca `mostrarResumen()`, que imprime los datos comunes del pedido.
4. Luego se invoca `calcularTiempoEntrega()`. La JVM resuelve en tiempo de ejecución la versión sobreescrita correspondiente al tipo real del objeto: `PedidoComida` aplica 15 min + 2 min/km, `PedidoEncomienda` aplica 20 min + 1.5 min/km y `PedidoExpress` aplica 10 min (+5 si la distancia supera los 5 km) — un ejemplo de polimorfismo por sobreescritura.
5. A continuación se invoca `asignarRepartidor()` sin argumentos: cada subclase ejecuta su propia lógica (mochila térmica, validación de peso, cercanía a la tienda).
6. Inmediatamente después, sobre el mismo elemento se invoca `asignarRepartidor(String)`, pasando el nombre `"Carlos Palma"` como argumento. Esta sobrecarga también se resuelve de forma polimórfica: cada subclase agrega un mensaje adicional propio de su tipo de pedido.
7. El resultado observable es una secuencia de mensajes por consola donde el mismo conjunto de llamadas (`mostrarResumen()`, `calcularTiempoEntrega()`, `asignarRepartidor()` y `asignarRepartidor(String)`) produce una salida distinta según el tipo real de cada objeto, evidenciando cómo la sobreescritura resuelve el comportamiento en tiempo de ejecución mientras la firma del método sigue definida en tiempo de compilación.

## 🚀 Cómo clonarlo y ejecutarlo en IntelliJ

1. Clona el repositorio con el siguiente comando:
   ```
   git clone <URL-del-repositorio>
   ```
2. Abre IntelliJ IDEA y selecciona `File > Open`, luego elige la carpeta del proyecto que acabas de clonar.
3. Si IntelliJ no detecta automáticamente el SDK de Java, ve a `File > Project Structure` y configura una versión de JDK instalada en tu equipo.
4. En el panel de la izquierda, navega hasta `src/ui/Main.java`.
5. Haz clic derecho sobre el archivo `Main.java` y selecciona `Run 'Main.main()'` (o usa el botón ▶️ que aparece junto al método `main`).
6. La consola de IntelliJ mostrará la salida del programa con los mensajes generados por cada pedido.

## 🖥️ Cómo ejecutarlo por línea de comandos

```
javac -d out src/model/*.java src/ui/*.java
java -cp out ui.Main
```

El ejercicio muestra dos formas distintas en que un mismo nombre de método puede comportarse de manera diferente. Por un lado está la sobreescritura: `PedidoComida`, `PedidoEncomienda` y `PedidoExpress` implementan `calcularTiempoEntrega()`, `asignarRepartidor()` y `asignarRepartidor(String)` —declarados como abstractos en `Pedido`—, así que cuando se llama a esos métodos sobre cada objeto, se ejecuta la versión propia de esa subclase. Por otro lado está la sobrecarga: dentro de `Pedido` existen dos métodos con el mismo nombre `asignarRepartidor`, pero diferentes parámetros (`asignarRepartidor()` y `asignarRepartidor(String)`), y Java decide cuál usar según los argumentos que se le pasen.
