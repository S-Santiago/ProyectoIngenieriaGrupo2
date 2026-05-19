# 🗺️ Hoja de Ruta Tecnológica: Estado Actual vs. Tareas Pendientes

> [!NOTE]
> **Estado de la Infraestructura:** El entorno está configurado con JDK, IDE y Maven/Gradle. Las dependencias básicas (Jackson, JavaFX) están identificadas y la estructura de paquetes (`model`, `view`, `controller`, `persistence`) ya está creada. Se ha acordado el formato de persistencia en JSON y la carga de CSV.

A continuación se detalla el backlog de desarrollo necesario para cumplir con los criterios de aceptación (Definition of Done) del proyecto:

---

## 🟥 Fase 1: Núcleo de Persistencia y Parsing (Prioridad Alta)
* [ ] **Implementar Persistencia JSON (`persistence/`):** Desarrollar el CRUD completo para que los datos de zonas comerciales y reglas de margen mínimo se guarden automáticamente y se recuperen al iniciar la aplicación.
* [ ] **Robustecer el Motor de Lectura CSV:** Implementar la importación de líneas de pedido desde el fichero CSV, validando tipos de datos. Aplicar las validaciones obligatorias: coste y precio positivos, unidades > 0, fecha parseable y estado valido.
* [ ] **Manejo e Informe de Filas Corruptas:** Programar el sistema para identificar y mostrar visualmente al usuario las filas con error del CSV sin que la aplicación sufra un cierre inesperado.

---

## 🟨 Fase 2: Lógica de Negocio y Cálculos Financieros (Prioridad Media)
* [ ] **Completar Motor de KPIs Financieros:** Desarrollar las funciones de agregación y cálculo financiero sobre las colecciones Java:
  * Calcular el margen bruto por línea `(precio_venta - coste) x unidades` y el margen porcentual.
  * Generar el ranking de categorías por facturación total y por margen total.
  * Calcular la evolución mensual de facturación y margen total para la zona o categoría seleccionada.
* [ ] **Algoritmo de Cruce de Reglas de Margen:** Programar el componente que realice la detección automática de líneas de pedido cuyo margen esté por debajo del mínimo configurado en su regla activa.
* [ ] **Cálculo de Desviación de Objetivos:** Programar la lógica comparativa entre la facturación real calculada y el objetivo de facturación anual de la zona comercial.

---

## 🟩 Fase 3: Conexión UI (JavaFX) y Eventos (Prioridad Media)
* [ ] **Vincular Controladores con Componentes de la GUI:** Desarrollar completamente la interfaz gráfica funcional con al menos 3 vistas:
  * `ImportKpiController`: Conectar la vista de importación y el panel de KPIs globales.
  * `ExploradorController`: Implementar el explorador de pedidos asegurando que la búsqueda o filtrado por categoría, zona, estado y rango de fechas funcione correctamente.
  * `Panel de Rentabilidad`: Cargar las gráficas y evoluciones mensuales requeridas.
* [ ] **Sincronización de Datos y Manejo de Errores:** Enlazar las validaciones con mensajes de error claros al usuario y asegurar la correcta gestión de excepciones como fichero no encontrado, precios negativos o fechas malformadas.

---

## 🟦 Fase 4: Exportación y Reportes Extensibles (Prioridad Baja)
* [ ] **Módulo Exportador Excel (`Apache POI`):** Diseñar y programar el componente encargado de exportar el ranking de categorías y las líneas bajo margen a un archivo XLSX con formato de tabla.

---

## 🚀 Valor Añadido (Nice-to-Have Opcionales)
* [ ] **Gráficas de Análisis Visual:** Incorporar la gráfica de barras apiladas con facturación vs margen por mes del último año y la gráfica de sectores con la distribución del margen total por categoría.
* [ ] **Sistema de Proyecciones:** Implementar la proyección de cierre de año extrapolando la facturación anual basándose en la media mensual acumulada.