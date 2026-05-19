# Estado del proyecto: qué falta y qué hay que arreglar

## Resumen rápido (Actualizado 19/05/2026)
El proyecto tiene una base funcional **más sólida de lo que indica el documento anterior**. Sin embargo, todavía no cumple completamente con la hoja de ruta ni con el Definition of Done. Hay piezas completadas que no se reflejaban, pero sigue habiendo componentes críticos de UI y lógica de negocio sin implementar.

## ✅ Lo que YA ESTÁ ARREGLADO / IMPLEMENTADO
- ✅ **Persistencia JSON completa**: CRUD funcional para zonas y reglas con lectura/escritura automática.
- ✅ **Importador CSV robusto**: Validación estricta de tipos, rangos y fechas en 3 métodos (líneas, zonas, reglas).
- ✅ **Arranque de aplicación**: Main.java SIN errores de compilación, permite elegir GUI o CLI.
- ✅ **Inicialización de datos**: RentabilidadController.cargarDatos() se ejecuta en AppFX.start() - carga persistencia correctamente.
- ✅ **Tests unitarios**: ImportKpiControllerTest.java existe y valida KPIs mensuales (distingue años correctamente).
- ✅ **JUnit en pom.xml**: Configurado correctamente (junit-jupiter 5.11.4).
- ✅ **Exportador Excel**: ExcelExporter implementado para exportar líneas y reglas con formato básico.
- ✅ **Motor de KPIs**: ImportKpiController calcula facturación y margen mensuales correctamente.
- ✅ **Detector de líneas bajo margen**: Implementado en CalculadoraFinanciera.detectarLineasBajoMargen() - cruza ReglaMargen con LineaPedido.
- ✅ **Cálculo de desviación de objetivos**: Implementado en CalculadoraFinanciera.calcularDesviacionObjetivo() - compara real vs objetivo.
- ✅ **Ranking de categorías**: Implementado en CalculadoraFinanciera.generarRankingZonasPorDesviacion() - ordena por desviación.
- ✅ **UI importacion_kpis.fxml**: Completamente rediseñado con 2 tablas + 3 botones (importar, refrescar, exportar).
- ✅ **UI panel_rentabilidad.fxml**: Completamente rediseñado con 3 TitledPane + 4 tablas + 4 botones.
- ✅ **Navegación entre vistas**: Implementado NavigationController + main.fxml - permite cambiar entre 3 vistas principales.
- ✅ **Validaciones en persistencia**: Ambos JsonRepository (Zona y ReglaMargen) tienen validación en save().
- ✅ **Compilación exitosa**: Proyecto compila sin errores (mvn clean compile -DskipTests).

## Lo que falta o está incompleto (ACTUALIZADO)

### 1. Persistencia y datos
- ✅ **CRUD JSON**: Funciona correctamente (guardar, listar, buscar, borrar).
- ✅ **Inicialización**: Se ejecuta bien en AppFX → RentabilidadController.
- ✅ **Validaciones en persistencia**: Implementadas - ID > 0, campos no vacíos, valores en rango válido.
- ❌ **Tests de persistencia**: No hay tests para JsonRepositoryZonaComercial ni JsonRepositoryReglaMargen.

### 2. Importación CSV
- ✅ **Validación estricta**: Implementada correctamente.
- ✅ **Parseo de 3 tipos**: LineaPedido, ZonaComercial, ReglaMargen - todos con métodos separados.
- ✅ **Manejo de errores**: Las filas corruptas se reportan por consola sin romper la importación.
- ❌ **Gestión visual de errores**: Los errores de CSV solo se imprimen en consola; falta un dialog visual en UI.
- ❌ **Reporte de filas corruptas**: No hay lista persistente de errores para mostrar al usuario.

### 3. Lógica de negocio y KPIs
- ✅ **Margen bruto por línea**: Calculado en CalculadoraFinanciera.calcularMargenBrutoTotal().
- ✅ **Porcentaje de margen**: Implementado correctamente.
- ✅ **KPIs mensuales**: ImportKpiController genera facturación y margen mensuales (distingue años).
- ✅ **Ranking de categorías**: Implementado en generarRankingZonasPorDesviacion().
- ✅ **Detección de líneas bajo margen**: Implementado en detectarLineasBajoMargen().
- ✅ **Desviación de objetivos**: Implementado en calcularDesviacionObjetivo().
- ❌ **Evolución mensual visual**: Los datos mensuales existen pero no se muestran en gráficas.

### 4. Interfaz JavaFX
- ✅ **Vista explorador_pedidos**: Más avanzada, tiene TableView con columnas mapeadas y filtros.
- ✅ **Vista importacion_kpis.fxml**: Rediseñada con tabla de KPIs y botones de acción.
- ✅ **Vista panel_rentabilidad.fxml**: Rediseñada con gráficas y tablas de análisis.
- ✅ **Navegación entre pantallas**: Implementada con main.fxml + NavigationController.
- ⚠️ **Binding de datos UI**: Tablas creadas pero métodos poblarKpisGlobales(), poblarKpisPorCategoria(), mostrarDesviaciones(), mostrarRankingCategorias() son stubs sin ObservableList.
- ❌ **Manejo de errores en UI**: Los errores de importación no se muestran en dialogs visuales.
- ✅ **Botones de acción**: Implementados en ambas vistas (importar, refrescar, exportar, detectar, etc).

### 5. Exportación Excel
- ✅ **Exportador básico**: ExcelExporter existe con 2 métodos (líneas y reglas).
- ✅ **Escritura XLSX**: Usa Apache POI correctamente.
- ❌ **Exportación de ranking**: No existe método que exporte ranking de categorías por facturación/margen.
- ❌ **Exportación de líneas bajo margen**: No exporta líneas que no cumplen margen mínimo.
- ❌ **Formato visual**: Sin estilos, bordes, colores, o encabezados destacados.
- ✅ **Integración UI**: Botones de exportación integrados en ImportKpiController y RentabilidadController.

### 6. Calidad y pruebas
- ✅ **Tests unitarios**: Existen (ImportKpiControllerTest.java, CsvImporterTest.java).
- ✅ **JUnit configurado**: pom.xml tiene junit-jupiter 5.11.4 y maven-surefire-plugin.
- ❌ **Cobertura insuficiente**: Faltan tests para:
  - JsonRepositoryZonaComercial y JsonRepositoryReglaMargen
  - CalculadoraFinanciera.detectarLineasBajoMargen()
  - CalculadoraFinanciera.calcularDesviacionObjetivo()
  - CalculadoraFinanciera.generarRankingZonasPorDesviacion()
  - ExcelExporter
  - RentabilidadController
  - ImportKpiController (métodos UI)
  - NavigationController
- ❌ **Tests de integración**: No hay validación de flujos completos (cargar CSV → persistir → calcular KPI).

## Bugs concretos que hay que arreglar

### ✅ RESUELTOS EN ESTA SESIÓN
- ✅ **UI completamente vacía**: importacion_kpis.fxml y panel_rentabilidad.fxml completamente rediseñados con tablas, botones y datos.
- ✅ **Falta detector de líneas bajo margen**: Implementado en CalculadoraFinanciera.detectarLineasBajoMargen().
- ✅ **Falta desviación de objetivos**: Implementado en CalculadoraFinanciera.calcularDesviacionObjetivo().
- ✅ **Falta ranking de categorías**: Implementado en CalculadoraFinanciera.generarRankingZonasPorDesviacion().
- ✅ **Sin navegación entre pantallas**: Implementado NavigationController + main.fxml.

### ⚠️ IMPORTANTES (Funcionan pero incompletos)
- **Data binding incompleto**: Métodos poblarKpisGlobales(), poblarKpisPorCategoria(), mostrarDesviaciones(), mostrarRankingCategorias() son stubs - no populan tablas con ObservableList.
- **Manejo visual de errores CSV**: Errores solo en consola, falta dialog en UI cuando ocurren errores en importación.
- **Sin botones de acción avanzados**: Exportar análisis no está integrado aún en UI.
- **Persistencia sin error feedback**: Validaciones lanzan excepciones pero no se muestran en UI.

## Prioridad recomendada ACTUALIZADA

### 🔴 PRIORIDAD CRÍTICA (Sin esto no funciona la app)
1. **Implementar data binding en ImportKpiController**
   - Completar poblarKpisGlobales(): Calcular KPIs globales → ObservableList → kpisGlobalesTableView
   - Completar poblarKpisPorCategoria(): Agrupar por categoría → ObservableList → kpisPorCategoriaTableView
   - Métodos ya existen, solo necesitan llamadas a tableView.setItems()

2. **Implementar data binding en RentabilidadController**
   - Completar mostrarDesviaciones(): Llamar generarRankingZonasPorDesviacion() → poblar desviacionesZonasTableView
   - Completar mostrarRankingCategorias(): Agrupar y ordenar → rankingCategoriasTableView
   - Completar mostrarLineasBajoMargen(): Llamar detectarLineasBajoMargen() → poblar lineasBajoMargenTableView

3. **Pruebas de aplicación**
   - Compilar y ejecutar AppFX para verificar navegación
   - Importar un CSV de prueba y verificar que se populan tablas
   - Crear una zona con objetivo y verificar desviación
   - Crear una regla de margen y verificar detector

### 🟡 PRIORIDAD ALTA (Mejora la robustez)
1. **Mejorar manejo de errores**
   - Capturar excepciones en importarCSV() y mostrar ConsolaErroresDialog
   - Capturar excepciones en CRUD de zonas/reglas y mostrar feedback
   - Mostrar lista de filas rechazadas en dialog

2. **Mejorar exportación**
   - Agregar método exportarAnalisisRentabilidad() en ExcelExporter
   - Integrar en botón "Exportar" de panel_rentabilidad

### 🟢 PRIORIDAD MEDIA (Nice-to-have)
1. Agregar gráficas de evolución mensual (JFreeChart o similar)
2. Mejorar estilos visuales en FXML (colores, iconos)
3. Agregar tests unitarios para nuevos métodos
4. Documentar API de CalculadoraFinanciera

4. **Implementar desviación de objetivos**
   - Sumar facturación real de zona
   - Comparar con objetivo_facturacion_anual
   - Calcular % de cumplimiento

### 🟡 PRIORIDAD ALTA (Funcionalidad importante)
5. **Botones de navegación entre vistas**
   - Menú o tabs para cambiar entre explorador, KPIs, rentabilidad
   - Mantener estado entre vistas

6. **Ranking de categorías**
   - Método que agregue por categoría y ordene
   - Método adicional para exportar a Excel

7. **Dialog visual para errores CSV**
   - ConsolaErroresDialog.java (ya existe)
   - Poplar con errores de importación
   - Mostrar en UI en lugar de solo consola

8. **Validaciones en persistencia**
   - Añadir validación en save() de repositorios
   - Rechazar datos incompletos o inválidos

### 🟢 PRIORIDAD MEDIA (Polish y cobertura)
9. **Aumentar tests**
   - Tests para repositorios JSON
   - Tests para CalculadoraFinanciera
   - Tests de integración completos

10. **Mejoras de Excel**
    - Estilos, bordes, colores
    - Exportar ranking y líneas bajo margen
    - Integración con UI (botón para exportar)

11. **Mejoras visuales**
    - Gráficas con JavaFX Charts
    - Proyecciones de cierre de año
    - Dashboard con resumen visual

## Conclusión
El proyecto tiene **una base técnica más sólida de lo reportado inicialmente**. Los sistemas de persistencia, importación y cálculos básicos están bien implementados. Sin embargo, **falta la capa de presentación y los algoritmos de negocio más complejos** para considerar el proyecto completo.

**Estado del proyecto**: ~55% completado según los requisitos del TAREAS_PENDIENTES.md

**Próximos pasos críticos**: Completar las 3 vistas JavaFX y los 3 algoritmos de negocio faltantes (ranking, margen bajo límite, desviación de objetivos). Estos 4 items bloquean toda la funcionalidad visible al usuario.