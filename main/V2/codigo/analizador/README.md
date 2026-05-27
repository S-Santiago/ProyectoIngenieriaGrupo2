# Analizador de Rentabilidad de Líneas de Producto

Aplicación de escritorio en JavaFX para analizar líneas de pedido, calcular KPIs de rentabilidad y gestionar reglas y zonas comerciales. La V2 incorpora autenticación por usuario, control de acceso por rol y un flujo de trabajo centrado en el explorador de pedidos, la importación de KPIs y el panel de rentabilidad.

## Arquitectura

El proyecto sigue una estructura MVC por capas:

* `app/`: arranque de la aplicación y bootstrap de JavaFX.
* `controller/`: lógica de interacción, sesión, navegación y cálculo.
* `model/`: entidades de dominio y configuraciones de negocio.
* `persistence/`: importación CSV, exportación Excel y repositorios JSON.
* `view/`: utilidades de interfaz y gestión de mensajes al usuario.

La aplicación arranca en una pantalla de login modal. Si el usuario se autentica correctamente, se guarda una sesión activa y se carga la vista principal con navegación entre módulos. El rol `COMERCIAL` tiene acceso limitado a su zona comercial, mientras que `DIRECTOR_FINANCIERO` conserva acceso completo al panel de gestión.

## Funcionalidades Principales

* Importación robusta de líneas de pedido desde CSV con validación de campos, fechas, importes y estados.
* Carga automática del CSV inicial de pedidos al entrar en la vista de explorador.
* Filtrado de pedidos por categoría, zona comercial, estado y fecha.
* Cálculo de KPIs globales y por categoría, incluyendo facturación y margen.
* Análisis de rentabilidad con ranking de categorías, detección de líneas bajo margen mínimo y desviación por zona comercial.
* Gestión persistente de zonas comerciales y reglas de margen en JSON.
* Exportación de resultados a Excel con Apache POI.
* Control de acceso por rol con sesión compartida durante toda la ejecución.

## Estructura Del Proyecto

```text
src/
├── app/
│   ├── Main.java
│   └── AppFX.java
├── controller/
│   ├── CalculadoraFinanciera.java
│   ├── ExploradorController.java
│   ├── ImportKpiController.java
│   ├── LoginController.java
│   ├── LoginCredentialsValidator.java
│   ├── NavigationController.java
│   ├── RentabilidadController.java
│   ├── RolUsuario.java
│   ├── SesionAplicacion.java
│   └── SesionUsuario.java
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
│   │   ├── lineas_pedidos.csv
│   │   ├── reglas.csv
│   │   ├── reglas.json
│   │   ├── zonas.csv
│   │   └── zonas.json
│   └── fxml/
│       ├── login.fxml
│       ├── main.fxml
│       ├── explorador_pedidos.fxml
│       ├── importacion_kpis.fxml
│       └── panel_rentabilidad.fxml
├── test/
│   └── java/
└── view/
    ├── ConsolaErroresDialog.java
    └── VistaManager.java
```

## Entradas Y Vistas

### `app/Main.java`
Punto de entrada de la aplicación. Fuerza el renderizado por software en macOS y lanza JavaFX.

### `app/AppFX.java`
Muestra primero el login, establece la sesión de usuario y después abre la ventana principal con navegación dinámica.

### `controller/NavigationController.java`
Carga las vistas principales dentro del contenedor central y adapta su disponibilidad según el rol autenticado.

### `controller/ExploradorController.java`
Gestiona la carga y validación de pedidos, el filtrado por criterios y la aplicación de restricciones de zona cuando la sesión pertenece a un comercial.

### `controller/ImportKpiController.java`
Calcula KPIs globales y por categoría, permite importar un CSV nuevo y exporta la información visible a Excel.

### `controller/RentabilidadController.java`
Presenta el análisis avanzado de rentabilidad y mantiene la gestión de zonas y reglas para usuarios con permisos financieros.

## Dominio Y Persistencia

### `controller/CalculadoraFinanciera.java`
Contiene los cálculos reutilizados por el resto de la aplicación: margen bruto total, rankings de facturación y margen, líneas bajo margen mínimo y desviaciones de zona.

### `model/LineaPedido.java`
Modelo principal de las líneas importadas desde CSV.

### `model/EstadoPedido.java`
Enum de estados válidos de pedido con conversión segura desde texto.

### `model/ReglaMargen.java`
Regla de margen mínimo asociada a una categoría concreta.

### `model/ZonaComercial.java`
Entidad de zona comercial con objetivo anual de facturación.

### `persistence/CsvImporter.java`
Importa líneas de pedido, zonas y reglas desde CSV con validación y avisos para filas rechazadas.

### `persistence/ExcelExporter.java`
Exporta colecciones a XLSX mediante Apache POI.

### `persistence/JsonRepositoryReglaMargen.java` y `persistence/JsonRepositoryZonaComercial.java`
Persisten reglas y zonas en JSON con carga y guardado automáticos.

## Recursos Y Pruebas

La carpeta `resources/fxml/` contiene las pantallas `login.fxml`, `main.fxml`, `explorador_pedidos.fxml`, `importacion_kpis.fxml` y `panel_rentabilidad.fxml`. La carpeta `resources/data/` incluye `lineas_pedidos.csv`, `zonas.csv`, `reglas.csv`, `zonas.json` y `reglas.json`.

El proyecto incluye pruebas JUnit 5 para validar credenciales, importación CSV, exportación Excel, cálculo de KPIs, lógica de rentabilidad y repositorios JSON.

## Tecnología

* Java 21.
* JavaFX 21.
* Maven.
* Jackson para JSON.
* Apache POI para Excel.
* JUnit Jupiter para pruebas.

## Resumen De Uso

1. Ejecutar la aplicación.
2. Autenticarse con un usuario válido.
3. Cargar y revisar pedidos en el explorador.
4. Consultar KPIs o el panel de rentabilidad.
5. Exportar resultados si es necesario.

La V2 ya no describe una interfaz genérica antigua: refleja un flujo con autenticación, permisos por rol y módulos de análisis claramente separados.