# Visión General de la Arquitectura
Este repositorio implementa una arquitectura en capas con patrón MVC, reforzada por una separación clara entre interfaz, lógica de negocio y persistencia. La aplicación se puede iniciar tanto por GUI como por CLI desde `app/`, pero ambas entradas convergen en los controladores compartidos de `controller/`, que coordinan la carga de pedidos, el filtrado, el cálculo de KPIs y el análisis de rentabilidad. Los datos transaccionales se importan desde CSV, mientras que la configuración de negocio se mantiene en JSON y los resultados se exportan a Excel.

# Estructura del Proyecto
```text
src/
├── app/
│   ├── Main.java
│   └── AppFX.java
├── cli/
│   └── CliEngine.java
├── controller/
│   ├── CalculadoraFinanciera.java
│   ├── ExploradorController.java
│   ├── ImportKpiController.java
│   ├── NavigationController.java
│   └── RentabilidadController.java
├── model/
│   ├── EstadoPedido.java
│   ├── LineaPedido.java
│   ├── ReglaMargen.java
│   └── ZonaComercial.java
├── persistence/
│   ├── CsvImporter.java
│   ├── ExcelExporter.java
│   ├── JsonRepositoryReglaMargen.java
│   └── JsonRepositoryZonaComercial.java
├── resources/
│   ├── data/
│   └── fxml/
├── test/
│   └── java/
└── view/
    ├── ConsolaErroresDialog.java
    └── VistaManager.java
```

# Referencia de Módulos y Archivos

## App

### `Main.java`
Punto de entrada principal de la aplicación. Pregunta al usuario si desea arrancar en GUI o CLI y delega en `AppFX` o `CliEngine` según la opción elegida.
También fuerza el renderizado por software en la ruta gráfica para evitar problemas de compatibilidad en algunos entornos Mac.

### `AppFX.java`
Clase de arranque JavaFX. Antes de mostrar la ventana principal invoca `RentabilidadController.cargarDatos()` para inicializar la persistencia de zonas y reglas desde JSON o CSV.
Después carga la escena raíz `main.fxml` y establece la ventana principal del sistema.

## Cli

### `CliEngine.java`
Motor interactivo de línea de comandos. Orquesta importación de pedidos desde CSV, navegación por filtros, consulta de KPIs y exportación a Excel usando los controladores compartidos.
Mantiene el último resultado visible para exportarlo y aplica límites de salida por pantalla para no saturar la consola.

## Controller

### `CalculadoraFinanciera.java`
Contiene la lógica de cálculo financiero: margen bruto total, rankings por categoría, ranking por facturación y detección de líneas por debajo del margen mínimo definido por regla activa.
También calcula desviaciones respecto a objetivos de facturación por zona comercial, leyendo las entidades persistidas desde `JsonRepositoryReglaMargen` y `JsonRepositoryZonaComercial`.

### `ExploradorController.java`
Es el controlador central para cargar, validar y filtrar pedidos. Usa un patrón Singleton para compartir el mismo conjunto de líneas entre GUI y CLI, y expone filtros por categoría, zona, estado y rango de fechas.
Además, enlaza la tabla JavaFX con los datos importados desde CSV y mantiene una lista de errores de validación que se reutiliza en la interfaz y en la consola.

### `ImportKpiController.java`
Gestiona la vista de importación y consulta de KPIs globales y por categoría. Calcula totales, márgenes, porcentajes y series mensuales a partir de los pedidos cargados en el explorador.
Incluye acciones de importar CSV y exportar a Excel, y define filas auxiliares internas para alimentar las tablas de la interfaz.

### `NavigationController.java`
Controla la navegación entre las vistas principales de la aplicación JavaFX. Carga dinámicamente los FXML de explorador, importación de KPIs y rentabilidad dentro del contenedor central.
Su responsabilidad es puramente de coordinación de UI: resuelve recursos, asigna controladores y gestiona errores de carga de vistas.

### `RentabilidadController.java`
Presenta el panel de rentabilidad avanzada con tres bloques de análisis: ranking de categorías, líneas por debajo del margen mínimo y desviaciones de zonas comerciales.
Además de refrescar la vista, conserva una lógica heredada de carga y mantenimiento de zonas/reglas en JSON, con respaldo desde CSV cuando los repositorios están vacíos.

## Model

### `EstadoPedido.java`
Enumera los estados válidos de un pedido: completado, cancelado y pendiente. También ofrece una conversión segura desde texto mediante `fromString`, usada por el importador CSV.
Actúa como catálogo tipado para evitar que la lógica de control trabaje con cadenas libres.

### `LineaPedido.java`
Modelo principal del dominio transaccional. Representa una línea de pedido con identificadores, descripción, categoría, costes, precio de venta, unidades, fecha, zona comercial y estado.
Sirve como DTO de negocio para importación CSV, filtrado, cálculo de rentabilidad y exportación.

### `ReglaMargen.java`
Modelo de configuración de negocio para definir el margen mínimo exigido a una categoría concreta. Guarda el identificador, la categoría afectada, el porcentaje mínimo, la activación de la regla y una descripción.
Es la base de la validación de líneas bajo margen en el panel de rentabilidad.

### `ZonaComercial.java`
Entidad de configuración comercial que describe una zona, su país, el responsable y el objetivo anual de facturación. Se persiste en JSON y se usa como referencia para calcular desviaciones frente al objetivo.
Su estructura permite cruzar datos maestros con los pedidos importados.

## Persistence

### `CsvImporter.java`
Implementa la importación robusta de CSV para líneas de pedido, zonas comerciales y reglas de margen. Valida cabeceras, número de columnas, tipos numéricos, fechas y estados antes de construir los objetos de dominio.
Normaliza fechas y decimales para admitir formatos habituales de intercambio y devuelve solo registros válidos, registrando avisos en consola para las filas rechazadas.

### `ExcelExporter.java`
Exporta colecciones de `LineaPedido`, `ReglaMargen` y `ZonaComercial` a archivos XLSX mediante Apache POI. Crea una hoja por tipo de entidad y escribe cabeceras explícitas para facilitar la lectura en Excel.
Su foco es la salida tabular simple, sin lógica de negocio adicional.

### `JsonRepositoryReglaMargen.java`
Repositorio local de reglas de margen basado en Jackson y un fichero `data/reglas.json`. Carga la colección al construir el repositorio y ofrece operaciones CRUD simples sobre la lista en memoria.
Cuando se guardan o eliminan reglas, persiste inmediatamente el estado actualizado en disco.

### `JsonRepositoryZonaComercial.java`
Repositorio local de zonas comerciales con el mismo enfoque que el de reglas: lectura inicial desde `data/zonas.json`, CRUD en memoria y persistencia automática al modificar datos.
Además de almacenar las entidades, valida campos clave como ID, nombre y objetivo de facturación antes de escribir el fichero.

## View

### `ConsolaErroresDialog.java`
Utilidad de presentación de mensajes basada en `JOptionPane`. Centraliza la visualización de errores, avisos de validación y mensajes informativos para la interfaz gráfica.
Evita repetir código de diálogo en los controladores y unifica el estilo de feedback al usuario.

### `VistaManager.java`
Pequeño gestor de navegación entre pantallas JavaFX cuando la aplicación se lanza en modo ventana. Carga FXML específicos, asigna controladores cuando es necesario y cambia el `Stage` principal.
Su papel complementa a `NavigationController` como punto de acceso reutilizable para abrir vistas concretas desde otras capas.