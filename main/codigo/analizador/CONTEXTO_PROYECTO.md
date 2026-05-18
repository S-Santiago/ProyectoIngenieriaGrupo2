# Contexto del Proyecto: Analizador de Rentabilidad de Líneas de Producto

Actúa como un Ingeniero de Software Senior y Arquitecto Java. Este documento contiene los requisitos oficiales y la estructura de un proyecto académico que estoy desarrollando. Úsalo como base de conocimiento para todas tus respuestas.

---

## 1. Resumen Ejecutivo
* **Dominio y Sector:** Finanzas / Distribución Comercial.
* **Problema:** Una empresa distribuidora de bienes de consumo exporta diariamente su cartera de pedidos a un fichero CSV. El archivo contiene el detalle de cada línea de pedido: producto, categoría, coste, precio de venta, cantidad, fecha y zona comercial.
* **Necesidad:** La dirección financiera necesita una aplicación de escritorio para importar esos datos y calcular KPIs de rentabilidad que hoy se calculan manualmente en Excel.
* **Arquitectura obligatoria:** Estructura de código organizada estrictamente en los paquetes: `model`, `view`, `controller`, y `persistence`.
* **Entorno y Librerías:** Entorno configurado con JDK, IDE (IntelliJ/Eclipse) y Maven/Gradle. Las dependencias identificadas de forma inicial incluyen Jackson, Apache POI y JavaFX.

---

## 2. Entidades del Modelo de Datos

### A. Línea de Pedido (Datos Transaccionales - Solo Memoria)
Se cargan en memoria desde el archivo CSV.
* **Atributos:** `id_linea`, `id_pedido`, `referencia_producto`, `descripcion_producto`, `categoría`, `coste_unitario`, `precio_venta_unitario`, `unidades`, `fecha_pedido` (ISO 8601), `zona_comercial`, `estado` (completado/cancelado/pendiente).

### B. Zona Comercial (Datos Persistentes - CRUD en App)
* **Atributos:** `ID`, `nombre`, `país`, `responsable comercial`, `objetivo_facturacion_anual`.

### C. Regla de Margen (Datos Persistentes - CRUD en App)
* **Atributos:** `ID`, `categoría de producto afectada`, `margen_minimo_porcentaje`, `activa`, `descripción`.

---

## 3. Requisitos Técnicos Obligatorios (Must-Have)

> [!IMPORTANT]
> **Regla de Oro de Persistencia:** Persistencia de zonas y reglas en JSON. Las líneas de pedido se cargan exclusivamente en memoria desde el archivo CSV.

* **Parser y Validación del CSV:** Importar líneas de pedido desde CSV, validar tipos de datos y mostrar filas con error. Validación estricta del CSV: coste y precio positivos, unidades > 0, fecha parseable y estado valido.
* **CRUD Completo:** CRUD completo de zonas comerciales y reglas de margen mínimo. Los datos deben guardarse automáticamente y recuperarse al iniciar la aplicación.
* **Motor Financiero de KPIs:**
  * Calcular margen bruto por línea: `(precio_venta - coste) x unidades`, y margen porcentual.
  * Ranking de categorías por facturación total (`precio_venta x unidades`) y por margen total.
  * Evolución mensual de facturación y margen total para la zona o categoría seleccionada.
  * Detección de líneas con margen por debajo del mínimo de su regla activa.
  * Comparativa de facturación real vs objetivo por zona comercial.
* **Interfaz de Usuario (GUI):** GUI con al menos 3 vistas: importación y KPIs globales, explorador de pedidos (con filtro por categoría, zona, estado y rango de fechas), y panel de rentabilidad. Debe funcionar completamente sin cierres inesperados.
* **Exportación:** Exportar ranking de categorías y líneas bajo margen a XLSX con formato de tabla.

---

## 4. Requisitos de la Memoria Técnica Académica
El documento de la memoria debe seguir rigurosamente la siguiente estructura de contenidos organizada por fases:
* **Fase de Análisis:** Descripción del proyecto, descripción del sistema actual, análisis del contexto y necesidades, definición de los problemas, descripción general del sistema, catálogo de objetivos, catálogo de usuarios, catálogo de requisitos funcionales y no funcionales, matriz de trazabilidad objetivos/requisitos, visión general, ámbito y alcance, descomposición de tareas WBS, planificación temporal PERT y Gantt, planificación de costes con datos estimados y reales, casos de uso a implementar y la tabla de requisitos-casos de uso.
* **Fase de Diseño:** Diseño de la arquitectura del sistema con descripción de clases, funciones y parámetros, catálogo de excepciones, entorno tecnológico (hardware y software), diseño de la interfaz de usuario con sus consideraciones y la especificación de la estructura física y de datos.
* **Fase de Implementación:** Entorno de desarrollo con herramientas, librerías y restricciones, código fuente de los componentes y el diario de desarrollo.
* **Fase de Prueba:** Especificación de pruebas unitarias, pruebas de integración y pruebas del sistema realizadas.
* **Fase de Mantenimiento:** Manual de usuario con capturas y sección de preguntas frecuentes.