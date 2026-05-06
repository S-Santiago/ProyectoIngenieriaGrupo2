# 🏗️ Arquitectura del Proyecto y Archivos `.java`

> [!NOTE]
> Este documento detalla la responsabilidad exacta de cada archivo `.java` dentro de la arquitectura MVC y el sistema de persistencia exigido para el **Analizador de Rentabilidad de Líneas de Producto**.

---

## 🚀 Punto de Entrada

> [!IMPORTANT]
> Se requiere inicializar el sistema correctamente. `Main.java` debe arrancar el sistema de ventanas (JavaFX/Swing) y, de forma automática, invocar al repositorio para que recupere los datos persistentes (zonas y reglas) guardados previamente al iniciar el programa.

*   **`Main.java`**: Clase principal que sirve como punto de entrada y arranque de la aplicación.

---

## 📦 Paquete `model` (Entidades de Datos) [Daniel F.]

> [!TIP]
> Las clases de este paquete deben ser modelos "anémicos": solo deben contener atributos, constructores y métodos *getter/setter*. La información importada desde el CSV se carga en memoria y no contiene lógica de negocio.

*   **`LineaPedido.java`**: Modela los datos transaccionales extraídos del CSV. Sus atributos son: `id_linea`, `id_pedido`, `referencia_producto`, `descripcion_producto`, `categoría`, `coste_unitario`, `precio_venta_unitario`, `unidades`, `fecha_pedido` (ISO 8601), `zona_comercial` y `estado`.
*   **`ZonaComercial.java`**: Entidad persistente que define las áreas de venta. Contiene: `ID`, `nombre`, `país`, `responsable comercial` y `objetivo_facturacion_anual`.
*   **`ReglaMargen.java`**: Entidad persistente que establece los límites financieros. Contiene: `ID`, `categoría de producto afectada`, `margen_minimo_porcentaje`, `activa` y `descripción`.
*   **`EstadoPedido.java`**: Un `Enum` para tipificar los estados permitidos del pedido: `completado`, `cancelado` o `pendiente`.

---

## 💾 Paquete `persistence` (Entrada y Salida) [Héctor L.]

> [!WARNING]
> Este paquete aísla el uso de librerías de archivos (Jackson, Apache POI) y es el único lugar donde la aplicación debe interactuar con el sistema operativo.

> [!CAUTION]
> La validación en la importación es crítica: se debe asegurar que costes y precios sean positivos, las unidades mayores a 0, la fecha sea parseable y el estado válido.

*   **`CsvImporter.java`**: Lee el archivo CSV diario, valida los tipos de datos mencionados anteriormente, muestra al usuario las filas con error y carga las líneas correctas en memoria.
*   **`JsonRepository.java`**: Implementa la persistencia JSON para guardar automáticamente y recuperar los datos del CRUD de `ZonaComercial` y `ReglaMargen`.
*   **`ExcelExporter.java`**: Exporta un archivo XLSX con formato de tabla que contiene el ranking de categorías y el listado de líneas que están bajo el margen mínimo.

---

## ⚙️ Paquete `controller` (Lógica y Conexión) [Haojun Z.]

> [!IMPORTANT]
> Es el cerebro de la aplicación. Actúa como intermediario entre las vistas visuales y las entidades de datos, ejecutando las acciones matemáticas y los filtros que el usuario solicita.

*   **`CalculadoraFinanciera.java`**: Motor matemático. Calcula el margen bruto por línea usando la fórmula `(precio_venta - coste) x unidades` y genera agregaciones como rankings por facturación y margen total.
*   **`ImportKpiController.java`**: Conecta la vista principal con el importador CSV y solicita los datos para mostrar la evolución mensual de facturación y margen para la zona/categoría seleccionada.
*   **`ExploradorController.java`**: Aplica la lógica de filtrado sobre los pedidos cargados en memoria (por categoría, zona, estado y rango de fechas).
*   **`RentabilidadController.java`**: Maneja el CRUD de zonas comerciales y reglas de margen. Procesa la detección de líneas por debajo del mínimo de su regla activa y calcula la comparativa de facturación real frente al objetivo por zona comercial.

---

## 🖥️ Paquete `view` (Componentes Gráficos) [Santiago F.]

> [!NOTE]
> Gestiona la interfaz gráfica y el flujo de las al menos 3 pantallas diferenciadas de la aplicación sin procesar lógica de cálculo.

*   **`VistaManager.java`**: Controla la navegación fluida entre la vista de importación y KPIs globales, el explorador de pedidos, y el panel de rentabilidad.
*   **`ConsolaErroresDialog.java`**: Componente visual para mostrar mensajes de error claros al usuario. Gestiona excepciones comunes como ficheros no encontrados, costes negativos o fechas malformadas, impidiendo que existan cierres inesperados del programa.