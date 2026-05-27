# Documentación V2
Esta versión del analizador está orientada a una aplicación de escritorio JavaFX con arquitectura MVC por capas. La entrada principal pasa por un login modal, crea una sesión de usuario y, a partir de esa sesión, habilita o restringe funcionalidades según el rol. El núcleo funcional sigue siendo el análisis de líneas de pedido, el cálculo de KPIs y la gestión comercial de zonas y reglas de margen.

## Visión General
La aplicación trabaja con tres tipos de datos claramente separados.

Los datos transaccionales son las líneas de pedido, que se importan desde CSV y se visualizan en el explorador. Los datos maestros de negocio son las zonas comerciales y las reglas de margen, que se almacenan en JSON y también pueden cargarse desde CSV como respaldo. La salida analítica se presenta en tablas, gráficas y exportaciones XLSX.

La V2 incorpora control de acceso por rol. El rol `COMERCIAL` queda limitado a su zona comercial y no puede acceder a la gestión completa de reglas y zonas, mientras que el rol `DIRECTOR_FINANCIERO` mantiene acceso total a los paneles de análisis y mantenimiento.

## Estructura del Proyecto
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
│       ├── explorador_pedidos.fxml
│       ├── importacion_kpis.fxml
│       ├── login.fxml
│       ├── main.fxml
│       └── panel_rentabilidad.fxml
├── test/
│   └── java/
└── view/
    ├── ConsolaErroresDialog.java
    └── VistaManager.java
```

## Arranque Y Flujo De Ejecución
### `app/Main.java`
Punto de entrada de la aplicación. Fuerza el renderizado por software en Mac y lanza la clase JavaFX principal.

### `app/AppFX.java`
Gestiona el arranque visual. Primero abre el login modal, valida credenciales, guarda la sesión y, solo si la autenticación es correcta, carga los datos persistentes y la vista principal.

## Control De Acceso
### `controller/LoginController.java`
Controla la pantalla de acceso. Solicita usuario y contraseña, invoca el validador y, si la autenticación es correcta, cierra el modal y entrega la sesión a la aplicación.

### `controller/LoginCredentialsValidator.java`
Contiene las credenciales de prueba usadas por la V2 y construye la sesión autenticada con usuario, rol y zona comercial asociada.

### `controller/RolUsuario.java`
Define los roles disponibles: `COMERCIAL` y `DIRECTOR_FINANCIERO`.

### `controller/SesionUsuario.java` y `controller/SesionAplicacion.java`
Representan la sesión activa y su almacenamiento global durante la ejecución. Se usan para aplicar restricciones de navegación, filtrado y acceso a la gestión.

## Navegación Y Vistas
### `controller/NavigationController.java`
Coordina la carga dinámica de las tres vistas funcionales principales: explorador de pedidos, importación de KPIs y panel de rentabilidad. También adapta la visibilidad de botones según el rol autenticado.

### `controller/ExploradorController.java`
Es el núcleo de trabajo con pedidos. Carga el CSV inicial en segundo plano, valida líneas, mantiene errores de validación y expone filtros por categoría, zona comercial, estado y fecha. En sesión comercial, fuerza la zona permitida y bloquea su cambio.

### `controller/ImportKpiController.java`
Calcula KPIs globales y por categoría a partir de los pedidos cargados. También permite importar un CSV nuevo y exportar la información visible a Excel.

### `controller/RentabilidadController.java`
Presenta el análisis avanzado de rentabilidad. Incluye ranking de categorías, detección de líneas por debajo del margen mínimo, desviaciones frente al objetivo de zona y gestión de zonas y reglas. La parte de mantenimiento queda deshabilitada para el rol comercial.

### `view/VistaManager.java`
Funciona como apoyo para abrir pantallas JavaFX concretas cuando hace falta cambiar de vista desde otra capa.

### `view/ConsolaErroresDialog.java`
Centraliza la presentación de errores, advertencias e información al usuario mediante diálogos JavaFX.

## Lógica De Negocio
### `controller/CalculadoraFinanciera.java`
Encapsula los cálculos financieros reutilizados por el resto de controladores. Calcula margen bruto total, rankings de facturación y margen por categoría, líneas bajo margen mínimo y desviación de zonas comerciales frente a su objetivo.

## Modelo De Dominio
### `model/LineaPedido.java`
Representa una línea de pedido con información de producto, categoría, importes, unidades, fecha, zona comercial y estado.

### `model/EstadoPedido.java`
Enumera los estados válidos de un pedido y ofrece conversión desde texto para la importación CSV.

### `model/ReglaMargen.java`
Modela la regla de margen mínima asociada a una categoría, junto con su activación y descripción.

### `model/ZonaComercial.java`
Describe una zona comercial con nombre, país, responsable y objetivo anual de facturación.

## Persistencia E I/O
### `persistence/CsvImporter.java`
Importa líneas de pedido, zonas y reglas desde CSV con validación de cabeceras, tipos, formatos de fecha y estados. Devuelve únicamente los registros válidos y registra avisos de rechazo.

### `persistence/ExcelExporter.java`
Exporta colecciones a XLSX mediante Apache POI para facilitar la entrega de resultados en formato tabular.

### `persistence/JsonRepositoryReglaMargen.java`
Gestiona la persistencia de reglas de margen en JSON.

### `persistence/JsonRepositoryZonaComercial.java`
Gestiona la persistencia de zonas comerciales en JSON.

## Recursos Y Datos
La carpeta `resources/fxml/` contiene las pantallas visibles de la aplicación: login, explorador, importación de KPIs y rentabilidad, más la vista principal que actúa como contenedor.

La carpeta `resources/data/` incluye los datos de arranque y ejemplo usados por la aplicación: `lineas_pedidos.csv`, `zonas.csv`, `reglas.csv`, `zonas.json` y `reglas.json`.

## Pruebas
La V2 incluye pruebas JUnit 5 para validar piezas clave del sistema, entre ellas:

* validación de credenciales de acceso;
* cálculo de KPIs mensuales;
* comportamiento del panel de rentabilidad;
* importación CSV;
* exportación Excel;
* repositorios JSON.

## Dependencias Y Build
El proyecto se compila con Maven para Java 21 y usa estas dependencias principales:

* JavaFX para la interfaz;
* Jackson para JSON;
* Apache POI para Excel;
* JUnit Jupiter para pruebas.

## Resumen Funcional
La V2 consolida el flujo completo de trabajo: acceso autenticado, carga de pedidos, filtrado y análisis de rentabilidad, visualización de KPIs, exportación de resultados y mantenimiento de reglas y zonas con control de permisos por rol.