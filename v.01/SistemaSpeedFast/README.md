
# Sistema de Reparto SpeedFast - Sobreescritura y Sobrecarga de Métodos - Herencia y Polimorfismo

Sistema de gestión de pedidos desarrollado en Java que modela distintos tipos de pedidos (comida, encomiendas y compras express) mediante clases organizadas en paquetes, aplicando herencia, sobreescritura (override) y sobrecarga (overload) de métodos para la asignación de repartidores.

## 📋 Descripción

- Separación del código en `package model` (clases de dominio) y `package ui` (clase de ejecución).
- Clase base `Pedido` con atributos encapsulados mediante modificador `private` y expuestos a través de métodos `getter`/`setter`.
- Herencia mediante `extends` de `PedidoComida`, `PedidoEncomienda` y `PedidoExpress` respecto de `Pedido`.
- Uso de `super()` en los constructores de las subclases para inicializar los atributos heredados (`idPedido`, `cliente`, `direccion`).
- Atributos propios y encapsulados en cada subclase (`restaurante`/`tiempoPreparacion`, `peso`/`volumen`, `tienda`/`distancia`).
- Sobrecarga de métodos definida en `Pedido`: `asignarRepartidor()`, `asignarRepartidor(String)` y `asignarRepartidor(boolean)`, que comparten nombre pero difieren en su lista de parámetros.
- Sobreescritura (`@Override`) de `asignarRepartidor()` y `asignarRepartidor(String)` en cada una de las tres subclases, redefiniendo el comportamiento heredado según el tipo concreto de pedido.
- Uso de condicionales dentro de los métodos sobreescritos para variar la respuesta según el estado del objeto (por ejemplo, validación de `peso` en `PedidoEncomienda`).
- Arreglo polimórfico de tipo `Pedido[]` que almacena instancias de las distintas subclases bajo el tipo de la superclase común.
- Recorrido con `for-each` que invoca los métodos sobreescritos y sobrecargados sobre cada elemento del arreglo, dejando que la resolución del comportamiento ocurra en tiempo de ejecución.

## 📂 Estructura del proyecto
```
sistema-pedidos/
├── .idea/
├── src/
│   ├── model/
│   │   ├── Pedido.java              # Clase base: atributos comunes y asignarRepartidor() sobrecargado
│   │   ├── PedidoComida.java        # extends Pedido; sobreescribe asignarRepartidor() y asignarRepartidor(String)
│   │   ├── PedidoEncomienda.java    # extends Pedido; sobreescribe asignarRepartidor() validando el peso de la carga
│   │   └── PedidoExpress.java       # extends Pedido; sobreescribe asignarRepartidor() según la distancia a la tienda
│   └── ui/
│       └── Main.java                # Clase principal: crea los pedidos y recorre el arreglo polimórfico Pedido[]
└── .gitignore
```
## ▶️ Funcionamiento

Al ejecutar el programa se realizan las siguientes acciones:

1. Se crean cuatro objetos: uno de tipo `Pedido` (`p1`) y tres de las subclases `PedidoComida`, `PedidoEncomienda` y `PedidoExpress` (`p2`, `p3`, `p4`), cada uno con sus atributos particulares inicializados a través de `super()` y de su propio constructor.
2. Los cuatro objetos se agrupan en un arreglo de tipo `Pedido[]`, que almacena referencias de distintas subclases bajo el tipo de la superclase común.
3. Se recorre el arreglo con `for-each`, y para cada elemento se invoca `asignarRepartidor()` sin argumentos.
4. La JVM resuelve en tiempo de ejecución la versión sobreescrita correspondiente al tipo real del objeto: `Pedido` ejecuta la búsqueda genérica, `PedidoComida` valida la necesidad de mochila térmica, `PedidoEncomienda` valida el `peso` de la carga y `PedidoExpress` busca el repartidor más cercano según la `distancia`  un ejemplo de polimorfismo por sobreescritura.
5. Inmediatamente después, sobre el mismo elemento se invoca `asignarRepartidor(String)`, pasando el nombre `"Carlos Palma"` como argumento.
6. Esta sobrecarga también se resuelve de forma polimórfica: cada subclase que la sobreescribe agrega un mensaje adicional propio de su tipo de pedido (mochila térmica, transporte especial, distancia a la tienda), mientras que `Pedido` ejecuta la versión genérica heredada al no poseer una sobreescritura propia.
7. El resultado observable es una secuencia de mensajes por consola donde el mismo par de llamadas (`asignarRepartidor()` y `asignarRepartidor(String)`) produce una salida distinta según el tipo real de cada objeto, evidenciando cómo la sobreescritura resuelve el comportamiento en tiempo de ejecución mientras la firma del método sigue definida en tiempo de compilación.

## 🚀 Cómo clonarlo y ejecutarlo en IntelliJ

1. Clona el repositorio con el siguiente comando:
   ```
   git clone <URL-del-repositorio>
   ```
2. Abre IntelliJ IDEA y selecciona `File > Open`, luego elige la carpeta del proyecto que acabas de clonar.
3. IntelliJ detectará el archivo `pom.xml` y reconocerá el proyecto como un proyecto Maven, descargando automáticamente lo necesario.
4. En el panel de la izquierda, navega hasta `src/main/java/ui/Main.java`.
5. Haz clic derecho sobre el archivo `Main.java` y selecciona `Run 'Main.main()'` (o usa el botón ▶️ que aparece junto al método `main`).
6. La consola de IntelliJ mostrará la salida del programa con los mensajes generados por cada pedido.

El ejercicio muestra dos formas distintas en que un mismo nombre de método puede comportarse de manera diferente. Por un lado está la sobreescritura: `PedidoComida`, `PedidoEncomienda` y `PedidoExpress` redefinen `asignarRepartidor()` y `asignarRepartidor(String)`, así que cuando se llama a ese método sobre cada objeto, se ejecuta la versión propia de esa subclase y no la de `Pedido`. Por otro lado está la sobrecarga: dentro de `Pedido` existen tres métodos con el mismo nombre `asignarRepartidor`, pero diferentes parámetros (`asignarRepartidor()`, `asignarRepartidor(String)`, `asignarRepartidor(boolean)`), y Java decide cuál usar según los argumentos que se le pasen.
