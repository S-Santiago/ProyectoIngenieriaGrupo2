# 🏗️ Arquitectura del Proyecto y Archivos `.java`

> [!NOTE]
> Este documento detalla la responsabilidad exacta de cada archivo `.java` dentro de la arquitectura MVC y el sistema de persistencia exigido para el **Analizador de Rentabilidad de Líneas de Producto**[cite: 1].

---

## 🚀 Punto de Entrada

*   **`Main.java`**: Es la clase principal que inicializa la aplicación.
    > [!IMPORTANT]
    > Se encarga de arrancar el sistema de ventanas (JavaFX/Swing) y de invocar al repositorio para que recupere los datos persistentes (zonas y reglas) guardados previamente al iniciar el programa[cite: 1].

---

## 📦 Paquete `model` (Entidades de Datos)

> [!TIP]
> Las clases de este paquete deben ser modelos "anémicos": solo deben contener atributos, constructores y métodos *getter/setter*. La información importada se carga en memoria y no contiene lógica de negocio[cite: 1].

*   **`LineaPedido.java`**: Modela los datos transaccionales extraídos del CSV[cite: 1]. Sus atributos son: `id_linea`, `id_pedido`, `referencia_producto`, `descripcion_producto`, `categoría`, `coste_unitario`, `precio_venta_unitario`, `unidades`, `fecha_pedido` (ISO 8601), `zona_comercial` y `estado`[cite: 1].
*   **`ZonaComercial.java`**: Entidad persistente que define las áreas de venta[cite: 1]. Contiene: `ID`, `nombre`, `país`, `responsable comercial` y `objetivo_facturacion_anual`[cite: 1].
*   **`ReglaMargen.java`**: Entidad persistente que establece los límites financieros[cite: 1]. Contiene: `ID`, `categoría de producto afectada`, `margen_minimo_porcentaje`, `activa` y `descripción`[cite: 1].
*   **`EstadoPedido.java`**: Un `Enum` para tipificar los estados permitidos del pedido: `completado`, `cancelado` o `pendiente`[cite: 1].

---

## 💾 Paquete `persistence` (Entrada y Salida)

> [!WARNING]
> Este paquete aísla el uso de librerías de archivos (Jackson, Apache POI) y es el único lugar donde la aplicación debe interactuar con el sistema operativo[cite: 1].

*   **`CsvImporter.java`**: Lee el archivo CSV diario y carga las líneas correctas en memoria[cite: 1].
    > [!CAUTION]
    > Es el responsable de validar los tipos de datos: debe asegurar que costes y precios sean positivos, las unidades mayores a 0, la fecha sea parseable y el estado válido[cite: 1]. Mostrará al usuario las filas con error[cite: 1].
*   **`JsonRepository.java`**: Implementa la persistencia JSON para guardar automáticamente y recuperar los datos del CRUD de `ZonaComercial` y `ReglaMargen`[cite: 1].
*   **`ExcelExporter.java`**: Exporta un archivo XLSX con formato de tabla que contiene el ranking de categorías y el listado de líneas que están bajo el margen mínimo[cite: 1].

---

## ⚙️ Paquete `controller` (Lógica y Conexión)

> [!IMPORTANT]
> Es el cerebro de la aplicación. Actúa como intermediario entre las vistas visuales y las entidades de datos, ejecutando las acciones que el usuario solicita.

*   **`CalculadoraFinanciera.java`**: Motor matemático[cite: 1]. Calcula el margen bruto por línea usando la fórmula `(precio_venta - coste) x unidades` y genera agregaciones como rankings por facturación y margen total[cite: 1].
*   **`ImportKpiController.java`**: Conecta la vista principal con el importador CSV y solicita los datos para mostrar la evolución mensual de facturación y margen para la zona/categoría seleccionada[cite: 1].
*   **`ExploradorController.java`**: Aplica la lógica de filtrado sobre los pedidos cargados en memoria (por categoría, zona, estado y rango de fechas)[cite: 1].
*   **`RentabilidadController.java`**: Maneja el CRUD de zonas comerciales y reglas de margen[cite: 1]. Procesa la detección de líneas por debajo del mínimo de su regla activa y calcula la facturación real frente al objetivo por zona[cite: 1].

---

## 🖥️ Paquete `view` (Componentes Gráficos)

> [!NOTE]
> Gestiona la interfaz gráfica y el flujo de las al menos 3 pantallas diferenciadas de la aplicación sin procesar lógica de cálculo[cite: 1].

*   **`VistaManager.java`**: Controla la navegación fluida entre la importación y KPIs globales, el explorador de pedidos, y el panel de rentabilidad[cite: 1].
*   **`ConsolaErroresDialog.java`**: Componente visual para mostrar mensajes de error claros al usuario[cite: 1]. Gestiona excepciones comunes como ficheros no encontrados, costes negativos o fechas malformadas, impidiendo cierres inesperados del programa[cite: 1].