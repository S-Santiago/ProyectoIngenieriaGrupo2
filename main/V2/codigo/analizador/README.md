# Analizador de Rentabilidad de Líneas de Producto

Este proyecto es una aplicación de escritorio desarrollada en Java orientada a la dirección financiera de una empresa distribuidora. Su objetivo es importar líneas de pedido desde archivos CSV, validar su integridad, calcular métricas de rentabilidad (margen bruto, EBIT) y exportar los resultados a Excel.

### 🎓 Contexto Académico y Corporativo

* **Institución:** Universidad Europea de Andalucía
* **Titulación:** 1º Ingeniería Informática (Curso 2025-2026)
* **Asignatura:** Proyecto de Ingeniería
* **Grupo 2**
* **Colaboración Industrial:** Accenture

---

## 👥 Equipo de Desarrollo (Grupo 2)

* **Santiago Fernández Sánchez** ([@S-Santiago](https://github.com/S-Santiago))
* **Héctor López García** ([@HectorLopGar-Dev](https://github.com/HectorLopGar-Dev))
* **Haojun Zhang** ([@HaoJunnnzhang](https://github.com/HaoJunnnzhang))
* **Daniel García Fielding** ([@maesefielding](https://github.com/maesefielding))

---

## 🚀 Flujo de Ejecución y Características

La aplicación soporta dos modos de interacción (Interfaz Gráfica o Consola) y gestiona el flujo de datos basándose en roles y persistencia local.

* **Modos de Arranque (V1 vs V2):** La V1 permite elegir entre GUI (JavaFX) o CLI (Consola) al inicio. La V2 arranca directamente en JavaFX con un sistema de autenticación (Login modal) basado en roles, permitiendo continuar en GUI o cambiar a CLI tras iniciar sesión.
* **Importación Transaccional:** Carga de líneas de pedido desde archivos CSV a memoria, validando campos, formatos de fecha y estados.
* **Gestión de Configuración (CRUD):** Las Zonas Comerciales y Reglas de Margen Mínimo se persisten y cargan desde archivos JSON locales de forma automática.
* **Cálculo y Filtrado:** Explorador de pedidos con filtros por categoría, zona, estado y fecha para calcular KPIs de rentabilidad en tiempo real.
* **Exportación de Datos:** Generación de reportes y rankings en formato Excel (XLSX).

---

## 🏗 Arquitectura y Estructura del Proyecto

El código fuente sigue un patrón **MVC (Model-View-Controller)** claro, apoyado por un patrón *Repository* para el acceso a datos y un *Singleton* para el contexto de sesión global.

**Estructura de paquetes principales:**

* `app`: Contiene las clases de arranque (`Main` y `AppFX`).
* `cli`: Motor y lógica para la ejecución por consola (`CliEngine`).
* `controller`: Lógica de interacción, autenticación, orquestación funcional y cálculos financieros.
* `model`: Definición del dominio (`LineaPedido`, `EstadoPedido`, `ReglaMargen`, `ZonaComercial`).
* `persistence`: Encapsulación de importación/exportación (`CsvImporter`, `ExcelExporter`) y repositorios JSON.
* `view`: Utilidades visuales y gestión de diálogos.

**Nota sobre la estructura de directorios:**
El proyecto no utiliza el layout estándar de Maven (`src/main/java`). El código fuente se encuentra directamente bajo `src/` y las pantallas FXML junto con los datos persistentes se alojan en `src/resources/`.

---

## 💻 Stack Tecnológico

| Tecnología / Herramienta | Versión / Detalle |
| --- | --- |
| **Lenguaje Core** | Java 21 |
| **Gestor de Dependencias** | Maven (Requiere `mvn` en el PATH) |
| **Framework GUI** | JavaFX 21.0.2 |
| **Persistencia (JSON)** | Jackson Databind 2.17.0 |
| **Exportación (Excel)** | Apache POI 5.2.5 |
| **Testing** | JUnit Jupiter 5.11.4 |
| **Plugins Maven** | Compiler 3.14.1, Surefire 3.5.2 |

---

## 🛠 Instalación y Ejecución Local

Es necesario tener instalado **Java 21** y **Maven** configurado en las variables de entorno (`PATH`).

**1. Compilar el proyecto:**

```bash
mvn clean compile
```

**2. Ejecutar la aplicación:**

```bash
mvn -q -DskipTests org.codehaus.mojo:exec-maven-plugin:3.5.0:java -Dexec.mainClass=app.Main
```

*(Nota: Para la V2, existen scripts ejecutables adicionales en el repositorio preparados para entornos macOS y Windows que encapsulan este comando).*

**3. Empaquetar el proyecto:**

```bash
mvn package
```

---

## 🧪 Pruebas y Calidad de Código

El proyecto cuenta con una batería de pruebas unitarias implementadas con JUnit Jupiter para garantizar la integridad de la lógica de negocio, cálculos financieros, controladores visuales y la serialización JSON/CSV.

**Ejecutar la suite de tests:**

```bash
mvn test
```