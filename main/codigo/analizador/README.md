# Analizador de Rentabilidad de Líneas de Producto

> [!NOTE]
> **Descripción del Proyecto**
> Aplicación de escritorio desarrollada para el sector de finanzas y distribución comercial. Está diseñada para una empresa distribuidora de bienes de consumo que exporta diariamente su cartera de pedidos a un fichero CSV y necesita importar esos datos para calcular KPIs de rentabilidad que hoy se calculan manualmente en Excel.

## 🏗️ Arquitectura

> [!TIP]
> El proyecto aplica el patrón de diseño arquitectónico MVC y estructura su código en paquetes estrictos (`model/`, `view/`, `controller/`, `persistence/`).

* **`model/`**: Entidades de negocio y lógica matemática.
* **`persistence/`**: Operaciones de entrada y salida (I/O).
* **`controller/`**: Intermediario y orquestador de flujos.
* **`view/`**: Componentes gráficos e interacciones del usuario.

---

## 👥 Equipo de Desarrollo y Responsabilidades

* **Daniel F. (Model)**: Responsable de las entidades de datos y del motor matemático para desarrollar habilidades de agregación y cálculo financiero básico (margen, EBIT simplificado) sobre grandes colecciones Java.
* **Héctor L. (Persistence)**: Encargado de la validación del CSV, el repositorio JSON para la persistencia de zonas y reglas, y la exportación a archivo (CSV/XLSX/PDF).
* **Haojun Z. (Controller)**: Coordinador que distingue entre datos persistentes (la configuración de negocio) y datos transaccionales (los pedidos del CSV). Gestiona el filtrado y las reglas de negocio.
* **Santiago F. (View)**: Desarrollador de la interfaz gráfica funcional con al menos 3 vistas o pantallas diferenciadas y de la gestión de errores mediante la interfaz.

---

## ✨ Características Principales

> [!IMPORTANT]
> **Importación Robusta**
> Lectura de líneas de pedido desde archivos CSV con validación estricta: coste y precio positivos, unidades > 0, fecha parseable, estado válido. Muestra las filas con error de manera clara al usuario.

* **Persistencia Transparente**: Sistema CRUD completo para zonas comerciales y reglas de margen, guardado automáticamente y recuperado al iniciar la aplicación.
* **Cálculo de Rentabilidad**: Evaluación del margen bruto por línea mediante la fórmula `(precio_venta - coste) x unidades` y detección de líneas con margen por debajo del mínimo de su regla activa.
* **Exploración Visual**: Listado de líneas de pedido con filtro por categoría, zona, estado y rango de fechas. Búsqueda o filtrado de registros funciona correctamente.
* **Exportación de Datos**: Exportar ranking de categorías y líneas bajo margen a XLSX con formato de tabla.

---

## 💻 Entorno y Tecnologías

* **Lenguaje**: Java.
* **Entorno**: JDK configurado, IDE (IntelliJ/Eclipse).
* **Gestor de Proyectos**: Maven/Gradle.
* **Librerías Clave**: Dependencias identificadas y descargadas como Jackson, Apache POI y JavaFX.

---

## ✅ Estado de Desarrollo (Definition of Done)

> [!CAUTION]
> **Criterios de Aceptación Obligatorios**
> * CRUD completo funciona sin errores: crear, consultar, modificar y eliminar.
> * Proyecto probado manualmente con casos de uso reales por el equipo.
> * Todas las validaciones implementadas con mensajes de error claros al usuario.
> * Errores comunes gestionados sin cierres inesperados del programa.