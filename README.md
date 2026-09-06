
# Proyecto Educacional - SpeedFast 

Este repositorio contiene el desarrollo de un proyecto educacional en Java: 
SpeedFast, un sistema de reparto de pedidos.
A través de una serie de desafíos prácticos, el proyecto irá evolucionando a lo largo del bimestre,
aplicando progresivamente los distintos conceptos de la Programación Orientada a Objetos.

## 📚 Contenido del repositorio

| Versión | Semana | Contenido | Ubicación |
|---|---|---|---|
| v.01 | Semana 1 | Sobreescritura y sobrecarga de métodos, herencia y polimorfismo | [📂 v.01](./v.01) |
| v.02 | Semana 2 | Definiendo una clase abstracta | [📂 v.02](./v.02) |
| v.03 | Semana 3 | Integrando abstracción, polimorfismo y desacoplamiento | [📂 v.03](./v.03) |
| v.04 | Semana 4 | Integrando Concurrencia con hilos | [📂 v.04](./v.04) |
| v.05 | Semana 5 | | [📂 v.05](./v.05) |
| v.06 | Semana 6 | | [📂 v.06](./v.06) |
| v.07 | Semana 7 | | _(próximamente)_ |
| v.08 | Semana 8 | | _(próximamente)_ |

## 🧾 Cuadro resumen por semana

| Semana | Clases / paquetes principales | Qué se agregó respecto a la semana anterior |
|---|---|---|
| **Semana 1** | `model` (`Pedido`, `PedidoComida`, `PedidoEncomienda`, `PedidoExpress`), `ui.Main` | Se define la jerarquía base de pedidos, aplicando **herencia**, **sobreescritura** y **sobrecarga de métodos** entre la clase `Pedido` y sus subclases. |
| **Semana 2** | `model` (misma jerarquía de pedidos), `ui.Main` | `Pedido` pasa a ser una **clase abstracta**, obligando a cada subtipo de pedido a implementar su propio comportamiento. |
| **Semana 3** | `interfaces` (`Despachable`, `Cancelable`, `Rastreable`), `model`, `services.ControladorEnvios`, `ui.Main` | Se incorporan **interfaces** para desacoplar comportamientos (despacho, cancelación, rastreo) y aparece `ControladorEnvios` como capa de servicio que coordina la lógica de negocio. |
| **Semana 4** | `interfaces`, `model`, `services` (`ControladorEnvios`, `Repartidor`), `ui.Main` | Se suma la clase `Repartidor` y se integra **concurrencia con hilos** para simular el procesamiento simultáneo de pedidos. |
| **Semana 5** | _Pendiente de definir_ | Carpeta creada como base para el siguiente avance del proyecto. |
| **Semana 6** | _Pendiente de definir_ | Carpeta creada como base para el siguiente avance del proyecto. |
| **Semana 7** | _Por publicar_ | — |
| **Semana 8** | _Por publicar_ | Entrega final: interfaz gráfica con Swing y conexión a base de datos (JDBC). |

> Nota: el detalle de las semanas 5 en adelante se irá completando a medida que se suban los avances correspondientes.

## 🛠️ Software y herramientas de desarrollo

- **IDE:** [IntelliJ IDEA](https://www.jetbrains.com/idea/) (Community Edition o Ultimate)
- **JDK:** Java Development Kit (JDK) 17 o superior
- **Sistema de control de versiones:** Git
- **Plataforma:** GitHub

## 🔁 Cómo duplicar este repositorio

Si quieres tener tu propia copia del proyecto para estudiarlo, modificarlo o usarlo como base para tus propias entregas, sigue estos pasos:

1. **Clonar el repositorio** en tu computador:
   ```bash
   git clone https://github.com/Fuentes404/Proyecto-Educacional-SpeedFast.git
   ```
2. **Entrar a la carpeta del proyecto:**
   ```bash
   cd Proyecto-Educacional-SpeedFast
   ```
3. **Abrir la versión que te interese** (por ejemplo, la semana 4) desde tu IDE (IntelliJ IDEA):
   ```bash
   cd v.04/SistemaSpeedFast
   ```
   y abrir esa carpeta como proyecto Maven/Java desde IntelliJ.
4. **(Opcional) Crear tu propia copia independiente en GitHub:**
   - Opción A — *Fork*: entra al repositorio en GitHub y presiona el botón **Fork** para crear una copia en tu propia cuenta.
   - Opción B — *Repositorio nuevo desde este código*:
     ```bash
     git clone https://github.com/Fuentes404/Proyecto-Educacional-SpeedFast.git
     cd Proyecto-Educacional-SpeedFast
     rm -rf .git
     git init
     git remote add origin https://github.com/TU-USUARIO/TU-NUEVO-REPO.git
     git add .
     git commit -m "Copia inicial del proyecto SpeedFast"
     git push -u origin main
     ```
5. **Compilar y ejecutar** la clase `ui/Main.java` de la versión elegida para probar el sistema.

## 🎯 Finalidad del proyecto

SpeedFast es un proyecto **educativo** desarrollado en el contexto del ramo *Desarrollo Orientado a Objetos* de Duoc UC. Su objetivo es servir como hilo conductor a lo largo del bimestre para **aplicar de forma progresiva y práctica los conceptos de la Programación Orientada a Objetos** (herencia, polimorfismo, clases abstractas, interfaces, desacoplamiento, concurrencia, GUI con Swing y persistencia con JDBC), usando como caso de estudio un sistema de reparto de pedidos (comida, encomiendas y envíos express).

Más que una aplicación de producción, busca ser una **evidencia de aprendizaje incremental**: cada carpeta de versión (`v.01` a `v.08`) representa un hito semanal que construye sobre el anterior, permitiendo ver la evolución del diseño de software a medida que se incorporan nuevas herramientas y buenas prácticas.

---
© Duoc UC | Escuela de Informática y Telecomunicaciones
