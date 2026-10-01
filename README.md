# Proyecto Educacional - SpeedFast

Este repositorio contiene el desarrollo de un proyecto educacional en Java:
SpeedFast, un sistema de reparto de pedidos.
A través de una serie de desafíos prácticos, el proyecto fue evolucionando a lo largo del bimestre,
aplicando progresivamente los distintos conceptos de la Programación Orientada a Objetos.

## 📚 Contenido del repositorio

| Versión | Semana   | Contenido                                                       | Ubicación                                                           |
| ------- | -------- | --------------------------------------------------------------- | ------------------------------------------------------------------- |
| v.01    | Semana 1 | Sobreescritura y sobrecarga de métodos, herencia y polimorfismo | [📂 v.01](https://github.com/Fuentes404/Proyecto-Educacional-SpeedFast/blob/main/v.01) |
| v.02    | Semana 2 | Definiendo una clase abstracta                                  | [📂 v.02](https://github.com/Fuentes404/Proyecto-Educacional-SpeedFast/blob/main/v.02) |
| v.03    | Semana 3 | Integrando abstracción, polimorfismo y desacoplamiento          | [📂 v.03](https://github.com/Fuentes404/Proyecto-Educacional-SpeedFast/blob/main/v.03) |
| v.04    | Semana 4 | Integrando Concurrencia con hilos                               | [📂 v.04](https://github.com/Fuentes404/Proyecto-Educacional-SpeedFast/blob/main/v.04) |
| v.05    | Semana 5 | Coordinando clases en un entorno concurrente                    | [📂 v.05](https://github.com/Fuentes404/Proyecto-Educacional-SpeedFast/blob/main/v.05) |
| v.06    | Semana 6 | Interfaz gráfica con Swing y ejecución concurrente de entregas  | [📂 v.06](https://github.com/Fuentes404/Proyecto-Educacional-SpeedFast/blob/main/v.06) |
| v.07    | Semana 7 | Conectando la aplicación a JDBC (persistencia en MySQL)         | [📂 v.07](https://github.com/Fuentes404/Proyecto-Educacional-SpeedFast/blob/main/v.07) |
| v.08    | Semana 8 | Gestionando datos mediante operaciones CRUD                     | [📂 v.08](https://github.com/Fuentes404/Proyecto-Educacional-SpeedFast/blob/main/v.08) |

## 🧾 Cuadro resumen por semana

| Semana       | Clases / paquetes principales                                                                              | Qué se agregó respecto a la semana anterior                                                                                                                                          |
| ------------ | ---------------------------------------------------------------------------------------------------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------ |
| **Semana 1** | `model` (`Pedido`, `PedidoComida`, `PedidoEncomienda`, `PedidoExpress`), `ui.Main`                         | Se define la jerarquía base de pedidos, aplicando **herencia**, **sobreescritura** y **sobrecarga de métodos** entre la clase `Pedido` y sus subclases.                              |
| **Semana 2** | `model` (misma jerarquía de pedidos), `ui.Main`                                                            | `Pedido` pasa a ser una **clase abstracta**, obligando a cada subtipo de pedido a implementar su propio comportamiento.                                                              |
| **Semana 3** | `interfaces` (`Despachable`, `Cancelable`, `Rastreable`), `model`, `services.ControladorEnvios`, `ui.Main` | Se incorporan **interfaces** para desacoplar comportamientos (despacho, cancelación, rastreo) y aparece `ControladorEnvios` como capa de servicio que coordina la lógica de negocio. |
| **Semana 4** | `interfaces`, `model`, `services` (`ControladorEnvios`, `Repartidor`), `ui.Main`                           | Se suma la clase `Repartidor` y se integra **concurrencia con hilos** para simular el procesamiento simultáneo de pedidos.                                                           |
| **Semana 5** | `ui.Main`, `model` (`Pedido`, `EstadoPedido`, `ZonaDeCarga`, `Repartidor`)                                  | Se implementa el patrón **Productor-Consumidor**: la clase `ZonaDeCarga` actúa como cola compartida sincronizada (`synchronized`, `wait()` y `notifyAll()`) entre el hilo principal, que registra los pedidos, y varios repartidores (`Runnable` dentro de un `ExecutorService`) que los retiran y entregan de forma concurrente. Se agrega el enum `EstadoPedido` (`PENDIENTE`, `EN_REPARTO`, `ENTREGADO`). |
| **Semana 6** | `main`, `interfaces`, `model` (pedidos y `Repartidor`), `services` (`ControladorPedidos`, `ControladorRepartidores`), `util.Validador`, `view` (`VentanaPrincipal`, `VentanaRegistroPedido`, `VentanaListaPedidos`, `VentanaEntregas`) | Se incorpora una **interfaz gráfica con Java Swing** (ventanas `JFrame`, formulario de registro, tabla de pedidos y ventana de entregas). Un controlador único comparte los datos en memoria entre las ventanas, se validan los datos con `Validador` y las entregas se simulan de forma concurrente (`ExecutorService`) sin congelar la interfaz, usando `SwingWorker` y `SwingUtilities.invokeLater()`. |
| **Semana 7** | `dao` (`ConexionDB`, `PedidoDAO`, `RepartidorDAO`, `EntregaDAO`), `model` (se agregan `Entrega` y `TipoPedido`), `services`, `view` (se agrega `VentanaRepartidores`), `sql` (`speedfast_db.sql`) | Se **conecta la aplicación a MySQL mediante JDBC**: la información de pedidos, repartidores y entregas ya no vive en memoria, sino que se guarda de forma **persistente**. Se crea la capa de acceso a datos con clases **DAO** (`PreparedStatement`, `ResultSet` y `try-with-resources`) y la clase `ConexionDB` (`DriverManager`). Los tres tipos de pedido se almacenan en una sola tabla (**herencia de tabla única**) y la tabla `entrega` relaciona pedidos con repartidores mediante claves foráneas. Los ID los genera la base de datos (`AUTO_INCREMENT`), los controladores pasan a usar los DAO y los repartidores se registran y eliminan desde la interfaz. Se incluye el script `sql/speedfast_db.sql` con la estructura de la base de datos. |
| **Semana 8** | `main`, `interfaces`, `model`, `services` (`ControladorPedidos`, `ControladorRepartidores`, se agrega `ControladorEntregas`), `util.ValidadorDatos`, `view` (`VentanaPrincipal`, `PanelPedidos`, `PanelRepartidores`, `PanelEntregas`), `dao` (`ConexionBD`, `PedidoDAO`, `RepartidorDAO`, `EntregaDAO`) | Se completan las **operaciones CRUD** (crear, listar, editar y eliminar) sobre pedidos, repartidores y entregas, trabajando directamente con la base de datos MySQL. La interfaz se reorganiza en una `VentanaPrincipal` con **menú lateral** (`CardLayout`) y un **panel por entidad** (`PanelPedidos`, `PanelRepartidores`, `PanelEntregas`), cada uno con su tabla (`JTable` y `DefaultTableModel`) y formularios con `JOptionPane` que se repiten hasta que los datos sean válidos. Se agrega `ControladorEntregas` (asignar, editar, eliminar y enviar entregas) y los controladores pasan a aplicar las **reglas del negocio** (por ejemplo, solo se modifican pedidos pendientes) devolviendo el mensaje de error o `null` si todo salió bien. La validación pasa a `ValidadorDatos` y `ConexionDB` se renombra a `ConexionBD`. El envío de pedidos corre en un hilo por ruta (`Runnable`), con seguimiento en pantalla mediante `Consumer<String>` y `SwingUtilities.invokeLater()`. Además, el código se ordena con una estructura de secciones comentadas común a todas las clases. |

## 🛠️ Software y herramientas de desarrollo

- **IDE:** [IntelliJ IDEA](https://www.jetbrains.com/idea/) (Community Edition o Ultimate)
- **JDK:** Java Development Kit (JDK) 17 o superior (la versión 7 está configurada para JDK 23)
- **Base de datos (desde la semana 7):** [MySQL](https://dev.mysql.com/downloads/) 8 o superior (MySQL Server y MySQL Workbench)
- **Conector JDBC (desde la semana 7):** MySQL Connector/J, incluido como dependencia Maven en el `pom.xml`
- **Sistema de control de versiones:** Git
- **Plataforma:** GitHub

## 🔁 Cómo duplicar este repositorio

Si quieres tener tu propia copia del proyecto para estudiarlo, modificarlo o usarlo como base para tus propias entregas, sigue estos pasos:

1. **Clonar el repositorio** en tu computador:

```
git clone https://github.com/Fuentes404/Proyecto-Educacional-SpeedFast.git
```

2. **Entrar a la carpeta del proyecto:**

```
cd Proyecto-Educacional-SpeedFast
```

3. **Abrir la versión que te interese** (por ejemplo, la semana 4) desde tu IDE (IntelliJ IDEA):

```
cd v.04/SistemaSpeedFast
```

y abrir esa carpeta como proyecto Maven/Java desde IntelliJ.

> A partir de la **versión 7** también necesitas MySQL en ejecución y ajustar el usuario y la contraseña de tu
> MySQL en la clase de conexión (`ConexionDB` en la versión 7 y `ConexionBD` desde la versión 8).
> Las instrucciones detalladas están en el README de cada carpeta.

## 🎯 Finalidad del proyecto

SpeedFast es un proyecto **educativo** desarrollado en el contexto del ramo *Desarrollo Orientado a Objetos* de Duoc UC. Su objetivo es servir como hilo conductor a lo largo del bimestre para **aplicar de forma progresiva y práctica los conceptos de la Programación Orientada a Objetos** (herencia, polimorfismo, clases abstractas, interfaces, desacoplamiento, concurrencia, GUI con Swing, persistencia con JDBC y operaciones CRUD), usando como caso de estudio un sistema de reparto de pedidos (comida, encomiendas y envíos express).

Más que una aplicación de producción, busca ser una **evidencia de aprendizaje incremental**: cada carpeta de versión (`v.01` a `v.08`) representa un hito semanal que construye sobre el anterior, permitiendo ver la evolución del diseño de software a medida que se incorporan nuevas herramientas y buenas prácticas.

---

© Duoc UC | Escuela de Informática y Telecomunicaciones
