package controller;

import java.io.File;
import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.StackedBarChart;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import model.ReglaMargen;
import model.ZonaComercial;
import persistence.ExcelExporter;
import persistence.JsonRepositoryReglaMargen;
import persistence.JsonRepositoryZonaComercial;
import view.ConsolaErroresDialog;

public class RentabilidadController {
    private final JsonRepositoryZonaComercial repoZonas = new JsonRepositoryZonaComercial();
    private final JsonRepositoryReglaMargen repoReglas = new JsonRepositoryReglaMargen();
    private final CalculadoraFinanciera calculadora = new CalculadoraFinanciera();
    private final ExcelExporter excelExporter = new ExcelExporter();
    private final ExploradorController exploradorController = ExploradorController.getInstance();

    @FXML
    private TableView<RankingCategoriaRow> rankingCategoriasTableView;
    @FXML
    private TableColumn<RankingCategoriaRow, Integer> rankPosicionColumn;
    @FXML
    private TableColumn<RankingCategoriaRow, String> rankCategoriaColumn;
    @FXML
    private TableColumn<RankingCategoriaRow, BigDecimal> rankFacturacionColumn;
    @FXML
    private TableColumn<RankingCategoriaRow, BigDecimal> rankMargenColumn;
    @FXML
    private TableColumn<RankingCategoriaRow, BigDecimal> rankMargenPorcentajeColumn;

    @FXML
    private TableView<LineaBajoMargenRow> lineasBajoMargenTableView;
    @FXML
    private TableColumn<LineaBajoMargenRow, Integer> idLineaColumn;
    @FXML
    private TableColumn<LineaBajoMargenRow, String> productoColumn;
    @FXML
    private TableColumn<LineaBajoMargenRow, String> categoriaColumn;
    @FXML
    private TableColumn<LineaBajoMargenRow, BigDecimal> margenActualColumn;
    @FXML
    private TableColumn<LineaBajoMargenRow, BigDecimal> margenRequeridoColumn;
    @FXML
    private TableColumn<LineaBajoMargenRow, BigDecimal> deficienciaColumn;
    @FXML
    private Label contadorBajoMargenLabel;

    @FXML
    private TableView<DesviacionZonaRow> desviacionesZonasTableView;
    @FXML
    private TableColumn<DesviacionZonaRow, String> zonaColumn;
    @FXML
    private TableColumn<DesviacionZonaRow, String> paisColumn;
    @FXML
    private TableColumn<DesviacionZonaRow, BigDecimal> objetivoZonaColumn;
    @FXML
    private TableColumn<DesviacionZonaRow, BigDecimal> realizadoZonaColumn;
    @FXML
    private TableColumn<DesviacionZonaRow, BigDecimal> desviacionColumn;
    @FXML
    private TableColumn<DesviacionZonaRow, BigDecimal> porcentajeZonaColumn;

    @FXML
    private TableView<ZonaComercial> zonasTableView;
    @FXML
    private TableColumn<ZonaComercial, Integer> zonaIdColumn;
    @FXML
    private TableColumn<ZonaComercial, String> zonaNombreColumn;
    @FXML
    private TableColumn<ZonaComercial, String> zonaPaisColumn;
    @FXML
    private TableColumn<ZonaComercial, String> zonaResponsableColumn;
    @FXML
    private TableColumn<ZonaComercial, Double> zonaObjetivoColumn;

    @FXML
    private TextField zonaIdField;
    @FXML
    private TextField zonaNombreField;
    @FXML
    private TextField zonaPaisField;
    @FXML
    private TextField zonaResponsableField;
    @FXML
    private TextField zonaObjetivoField;
    @FXML
    private Button guardarZonaButton;

    @FXML
    private TableView<ReglaMargen> reglasTableView;
    @FXML
    private TableColumn<ReglaMargen, Integer> reglaIdColumn;
    @FXML
    private TableColumn<ReglaMargen, String> reglaCategoriaColumn;
    @FXML
    private TableColumn<ReglaMargen, Double> reglaMargenColumn;
    @FXML
    private TableColumn<ReglaMargen, Boolean> reglaActivaColumn;
    @FXML
    private TableColumn<ReglaMargen, String> reglaDescripcionColumn;

    @FXML
    private TextField reglaIdField;
    @FXML
    private TextField reglaCategoriaField;
    @FXML
    private TextField reglaMargenField;
    @FXML
    private CheckBox reglaActivaCheckBox;
    @FXML
    private TextArea reglaDescripcionTextArea;
    @FXML
    private Button guardarReglaButton;

    @FXML
    private BarChart<String, Number> categoriasBarChart;
    @FXML
    private LineChart<String, Number> kpisMensualesLineChart;
    @FXML
    private StackedBarChart<String, Number> stackedBarChartMensual;
    @FXML
    private PieChart margenCategoriasPieChart;
    @FXML
    private PieChart zonasPieChart;

    @FXML
    private Tab tabGestion;

    private final ObservableList<ZonaComercial> zonasItems = FXCollections.observableArrayList();
    private final ObservableList<ReglaMargen> reglasItems = FXCollections.observableArrayList();
    private final ImportKpiController importKpiController = new ImportKpiController();

    @FXML
    public void initialize() {
        cargarDatos();

        configurarTablas();
        configurarGestion();
        configurarGraficas();
        aplicarSesion(SesionAplicacion.obtener());
        refrescarPanel();
        refrescarGraficas();

        if (accesoFinancieroPermitido()) {
            refrescarGestion();
        }
    }

    boolean accesoFinancieroPermitido() {
        SesionUsuario sesion = SesionAplicacion.obtener();
        return sesion == null || !sesion.esComercial();
    }

    public void aplicarSesion(SesionUsuario sesion) {
        if (sesion != null && sesion.esComercial()) {
            if (tabGestion != null) {
                tabGestion.setDisable(true);
            }
            exploradorController.aplicarSesion(sesion);
        } else {
            if (tabGestion != null) {
                tabGestion.setDisable(false);
            }
            exploradorController.desbloquearFiltroComercial();
        }
    }

    private boolean exigirAccesoFinanciero(String accion) {
        if (accesoFinancieroPermitido()) {
            return true;
        }

        ConsolaErroresDialog.mostrarError(
            "Acceso denegado",
            "El rol comercial no puede " + accion + "."
        );
        return false;
    }

    private void configurarTablas() {
        rankPosicionColumn.setCellValueFactory(new PropertyValueFactory<>("posicion"));
        rankCategoriaColumn.setCellValueFactory(new PropertyValueFactory<>("categoria"));
        rankFacturacionColumn.setCellValueFactory(new PropertyValueFactory<>("facturacion"));
        rankMargenColumn.setCellValueFactory(new PropertyValueFactory<>("margen"));
        rankMargenPorcentajeColumn.setCellValueFactory(new PropertyValueFactory<>("margenPorcentaje"));

        idLineaColumn.setCellValueFactory(new PropertyValueFactory<>("idLinea"));
        productoColumn.setCellValueFactory(new PropertyValueFactory<>("producto"));
        categoriaColumn.setCellValueFactory(new PropertyValueFactory<>("categoria"));
        margenActualColumn.setCellValueFactory(new PropertyValueFactory<>("margenActual"));
        margenRequeridoColumn.setCellValueFactory(new PropertyValueFactory<>("margenRequerido"));
        deficienciaColumn.setCellValueFactory(new PropertyValueFactory<>("deficiencia"));

        zonaColumn.setCellValueFactory(new PropertyValueFactory<>("zona"));
        paisColumn.setCellValueFactory(new PropertyValueFactory<>("pais"));
        objetivoZonaColumn.setCellValueFactory(new PropertyValueFactory<>("objetivo"));
        realizadoZonaColumn.setCellValueFactory(new PropertyValueFactory<>("real"));
        desviacionColumn.setCellValueFactory(new PropertyValueFactory<>("desviacion"));
        porcentajeZonaColumn.setCellValueFactory(new PropertyValueFactory<>("porcentaje"));

        rankingCategoriasTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        lineasBajoMargenTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        desviacionesZonasTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);

        desviacionesZonasTableView.setPlaceholder(new Label("No hay zonas comerciales cargadas para mostrar desviaciones."));
    }

    private void configurarGestion() {
        zonasTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        reglasTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        zonasTableView.setItems(zonasItems);
        reglasTableView.setItems(reglasItems);

        zonaIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        zonaNombreColumn.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        zonaPaisColumn.setCellValueFactory(new PropertyValueFactory<>("pais"));
        zonaResponsableColumn.setCellValueFactory(new PropertyValueFactory<>("responsableComercial"));
        zonaObjetivoColumn.setCellValueFactory(new PropertyValueFactory<>("objetivoFacturacionAnual"));

        reglaIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        reglaCategoriaColumn.setCellValueFactory(new PropertyValueFactory<>("categoriaProductoAfectada"));
        reglaMargenColumn.setCellValueFactory(new PropertyValueFactory<>("margenMinimoPortcentaje"));
        reglaActivaColumn.setCellValueFactory(new PropertyValueFactory<>("activa"));
        reglaDescripcionColumn.setCellValueFactory(new PropertyValueFactory<>("descripcion"));

        zonasTableView.setPlaceholder(new Label("No hay zonas comerciales guardadas."));
        reglasTableView.setPlaceholder(new Label("No hay reglas de margen guardadas."));

        zonasTableView.getSelectionModel().selectedItemProperty().addListener((observable, anterior, seleccionada) -> cargarZonaEnFormulario(seleccionada));
        zonasTableView.getSelectionModel().selectedItemProperty().addListener((observable, anterior, seleccionada) -> actualizarTextoBotonZona());
        zonaIdField.textProperty().addListener((observable, anterior, nuevo) -> actualizarTextoBotonZona());
        reglasTableView.getSelectionModel().selectedItemProperty().addListener((observable, anterior, seleccionada) -> cargarReglaEnFormulario(seleccionada));
        reglasTableView.getSelectionModel().selectedItemProperty().addListener((observable, anterior, seleccionada) -> actualizarTextoBotonRegla());
        reglaIdField.textProperty().addListener((observable, anterior, nuevo) -> actualizarTextoBotonRegla());

        reglaActivaCheckBox.setSelected(true);
        actualizarTextoBotonZona();
        actualizarTextoBotonRegla();
    }

    private void configurarGraficas() {
        categoriasBarChart.setAnimated(false);
        kpisMensualesLineChart.setAnimated(false);
        if (stackedBarChartMensual != null) {
            stackedBarChartMensual.setAnimated(false);
            stackedBarChartMensual.setLegendVisible(true);
        }
        if (margenCategoriasPieChart != null) {
            margenCategoriasPieChart.setLabelsVisible(true);
            margenCategoriasPieChart.setLegendVisible(true);
        }
        zonasPieChart.setLabelsVisible(true);
        zonasPieChart.setLegendVisible(true);
    }

    @FXML
    public void refrescarPanel() {
        try {
            detectarBajoMargen();
            mostrarDesviaciones();
            mostrarRankingCategorias();
        } catch (Exception e) {
            ConsolaErroresDialog.mostrarError(
                "Error al Refrescar",
                "No se pudo refrescar el panel.\nDetalle: " + e.getMessage()
            );
        }
    }

    @FXML
    public void refrescarGestion() {
        if (!accesoFinancieroPermitido()) {
            return;
        }

        try {
            zonasItems.setAll(repoZonas.findAll());
            reglasItems.setAll(repoReglas.findAll());
            zonasTableView.refresh();
            reglasTableView.refresh();
            limpiarFormularioZona();
            limpiarFormularioRegla();
        } catch (Exception e) {
            ConsolaErroresDialog.mostrarError(
                "Error al Refrescar",
                "No se pudo refrescar la gestión comercial.\nDetalle: " + e.getMessage()
            );
        }
    }

    @FXML
    public void refrescarGraficas() {
        try {
            cargarGraficaCategorias();
            cargarGraficaMensual();
            cargarGraficaMensualApilada();
            cargarGraficaMargenPorCategoria();
            cargarGraficaZonas();
        } catch (Exception e) {
            ConsolaErroresDialog.mostrarError(
                "Error en Gráficas",
                "No se pudieron actualizar las gráficas.\nDetalle: " + e.getMessage()
            );
        }
    }

    @FXML
    public void detectarBajoMargen() {
        try {
            List<Map<String, Object>> lineasBajoMargen = calculadora.detectarLineasBajoMargen();
            List<LineaBajoMargenRow> filas = new ArrayList<>();

            for (Map<String, Object> incidencia : lineasBajoMargen) {
                filas.add(new LineaBajoMargenRow(
                    toInteger(incidencia.get("idLinea")),
                    String.valueOf(incidencia.getOrDefault("producto", "")),
                    String.valueOf(incidencia.getOrDefault("categoria", "")),
                    toBigDecimal(incidencia.get("margenActual")),
                    toBigDecimal(incidencia.get("margenRequerido")),
                    toBigDecimal(incidencia.get("deficiencia"))
                ));
            }

            lineasBajoMargenTableView.setItems(FXCollections.observableArrayList(filas));
            
            // Actualizar el contador
            contadorBajoMargenLabel.setText("Incidencias encontradas: " + lineasBajoMargen.size());
            
            if (!lineasBajoMargen.isEmpty()) {
                ConsolaErroresDialog.mostrarInfoConOpcionNoMostrar(
                    ConsolaErroresDialog.CLAVE_ANALISIS_MARGEN,
                    "Análisis de Margen",
                    "Se encontraron " + lineasBajoMargen.size() + 
                    " líneas de pedido por debajo del margen mínimo requerido."
                );
            } else {
                ConsolaErroresDialog.mostrarInfoConOpcionNoMostrar(
                    ConsolaErroresDialog.CLAVE_ANALISIS_MARGEN,
                    "Análisis de Margen",
                    "Todas las líneas cumplen el margen mínimo requerido. ✓"
                );
            }
        } catch (Exception e) {
            ConsolaErroresDialog.mostrarError(
                "Error en Detección",
                "No se pudieron detectar líneas bajo margen.\nDetalle: " + e.getMessage()
            );
        }
    }

    @FXML
    public void mostrarDesviaciones() {
        try {
            List<Map<String, Object>> desviaciones = calculadora.generarRankingZonasPorDesviacion();
            List<DesviacionZonaRow> filas = new ArrayList<>();

            for (Map<String, Object> desviacion : desviaciones) {
                filas.add(new DesviacionZonaRow(
                    String.valueOf(desviacion.getOrDefault("nombre", "")),
                    String.valueOf(desviacion.getOrDefault("pais", "")),
                    toBigDecimal(desviacion.get("objetivo")),
                    toBigDecimal(desviacion.get("real")),
                    toBigDecimal(desviacion.get("desviacion")),
                    toBigDecimal(desviacion.get("porcentaje"))
                ));
            }

            desviacionesZonasTableView.setItems(FXCollections.observableArrayList(filas));

            if (filas.isEmpty()) {
                ConsolaErroresDialog.mostrarInfoConOpcionNoMostrar(
                    ConsolaErroresDialog.CLAVE_DESVIACIONES_ZONAS,
                    "Desviaciones por Zona",
                    "No hay zonas comerciales cargadas o no existen datos para calcular desviaciones."
                );
            } else {
                ConsolaErroresDialog.mostrarInfoConOpcionNoMostrar(
                    ConsolaErroresDialog.CLAVE_DESVIACIONES_ZONAS,
                    "Desviaciones por Zona",
                    "Se cargaron " + filas.size() + " zonas con su desviación respecto al objetivo."
                );
            }
            
        } catch (Exception e) {
            ConsolaErroresDialog.mostrarError(
                "Error en Desviaciones",
                "No se pudieron calcular las desviaciones.\nDetalle: " + e.getMessage()
            );
        }
    }

    private void mostrarRankingCategorias() {
        try {
            Map<String, BigDecimal> rankingFacturacion = calculadora.generarRankCategoriasPorFacturacion();
            Map<String, BigDecimal> rankingMargen = calculadora.generarRankCategorias();
            Set<String> categorias = new LinkedHashSet<>();
            categorias.addAll(rankingFacturacion.keySet());
            categorias.addAll(rankingMargen.keySet());

            List<RankingCategoriaRow> filas = new ArrayList<>();
            int posicion = 1;
            for (String categoriaActual : categorias) {
                BigDecimal facturacion = rankingFacturacion.getOrDefault(categoriaActual, BigDecimal.ZERO);
                BigDecimal margen = rankingMargen.getOrDefault(categoriaActual, BigDecimal.ZERO);
                BigDecimal porcentaje = facturacion.compareTo(BigDecimal.ZERO) > 0
                    ? margen.multiply(BigDecimal.valueOf(100)).divide(facturacion, 2, java.math.RoundingMode.HALF_UP)
                    : BigDecimal.ZERO;
                filas.add(new RankingCategoriaRow(
                    posicion++,
                    categoriaActual,
                    facturacion.setScale(2, java.math.RoundingMode.HALF_UP),
                    margen.setScale(2, java.math.RoundingMode.HALF_UP),
                    porcentaje.setScale(2, java.math.RoundingMode.HALF_UP)
                ));
            }

            rankingCategoriasTableView.setItems(FXCollections.observableArrayList(filas));
        } catch (Exception e) {
            System.err.println("Error al mostrar ranking: " + e.getMessage());
        }
    }

    @FXML
    public void exportarAnalisis() {
        try {
            Stage stage = (Stage) rankingCategoriasTableView.getScene().getWindow();
            exportarExcelConDialogo(
                stage,
                "Guardar Análisis como Excel",
                "analisis_rentabilidad.xlsx",
                ruta -> excelExporter.exportAnalisisRentabilidad(
                    ruta,
                    obtenerRankingCategoriasParaExportar(),
                    obtenerLineasBajoMargenParaExportar()
                ),
                "Exportación Exitosa",
                "El análisis se exportó correctamente a:",
                "Error en Exportación",
                "exportar el análisis"
            );
        } catch (Exception e) {
            ConsolaErroresDialog.mostrarError(
                "Error en Exportación",
                "No se pudo exportar el análisis.\nDetalle: " + e.getMessage()
            );
        }
    }

    @FXML
    public void exportarRankingCategorias() {
        try {
            Stage stage = (Stage) rankingCategoriasTableView.getScene().getWindow();
            exportarExcelConDialogo(
                stage,
                "Guardar Ranking de Categorías como Excel",
                "ranking_categorias.xlsx",
                ruta -> excelExporter.exportRankingCategorias(
                    ruta,
                    obtenerRankingCategoriasParaExportar()
                ),
                "Exportación Exitosa",
                "El ranking se exportó correctamente a:",
                "Error en Exportación",
                "exportar el ranking"
            );
        } catch (Exception e) {
            ConsolaErroresDialog.mostrarError(
                "Error en Exportación",
                "No se pudo exportar el ranking.\nDetalle: " + e.getMessage()
            );
        }
    }

    @FXML
    public void exportarLineasBajoMargen() {
        try {
            Stage stage = (Stage) lineasBajoMargenTableView.getScene().getWindow();
            exportarExcelConDialogo(
                stage,
                "Guardar Líneas bajo margen como Excel",
                "lineas_bajo_margen.xlsx",
                ruta -> excelExporter.exportLineasBajoMargen(
                    ruta,
                    obtenerLineasBajoMargenParaExportar()
                ),
                "Exportación Exitosa",
                "Las líneas bajo margen se exportaron correctamente a:",
                "Error en Exportación",
                "exportar las líneas bajo margen"
            );
        } catch (Exception e) {
            ConsolaErroresDialog.mostrarError(
                "Error en Exportación",
                "No se pudo exportar las líneas bajo margen.\nDetalle: " + e.getMessage()
            );
        }
    }

    @FXML
    public void guardarZonaDesdeFormulario() {
        if (!exigirAccesoFinanciero("crear o actualizar zonas comerciales")) {
            return;
        }

        try {
            Integer id = leerEntero(zonaIdField.getText(), siguienteIdZona());
            String nombre = zonaNombreField.getText() == null ? "" : zonaNombreField.getText().trim();
            String pais = zonaPaisField.getText() == null ? "" : zonaPaisField.getText().trim();
            String responsable = zonaResponsableField.getText() == null ? "" : zonaResponsableField.getText().trim();
            double objetivo = leerDouble(zonaObjetivoField.getText(), 0.0);

            ZonaComercial zona = new ZonaComercial(id, nombre, pais, responsable, objetivo);
            repoZonas.save(zona);
            ConsolaErroresDialog.reiniciarNoVolverAMostrarAnalisis();

            refrescarGestion();
            refrescarGraficas();
            ConsolaErroresDialog.mostrarInfo("Zona comercial", "Zona guardada correctamente.");
        } catch (Exception e) {
            ConsolaErroresDialog.mostrarError(
                "Error al Guardar Zona",
                "No se pudo guardar la zona comercial.\nDetalle: " + e.getMessage()
            );
        }
    }

    @FXML
    public void eliminarZonaSeleccionada() {
        if (!exigirAccesoFinanciero("eliminar zonas comerciales")) {
            return;
        }

        ZonaComercial seleccionada = zonasTableView.getSelectionModel().getSelectedItem();
        String id = seleccionada != null ? String.valueOf(seleccionada.getId()) : zonaIdField.getText();

        if (id == null || id.isBlank()) {
            ConsolaErroresDialog.mostrarError("Eliminar zona", "Selecciona una zona o indica un ID válido.");
            return;
        }

        if (repoZonas.delete(id)) {
            ConsolaErroresDialog.reiniciarNoVolverAMostrarAnalisis();
            refrescarGestion();
            refrescarGraficas();
            ConsolaErroresDialog.mostrarInfo("Zona comercial", "Zona eliminada correctamente.");
        } else {
            ConsolaErroresDialog.mostrarError("Eliminar zona", "No se encontró la zona con ID " + id + ".");
        }
    }

    @FXML
    public void limpiarFormularioZona() {
        if (!accesoFinancieroPermitido()) {
            return;
        }

        zonaIdField.clear();
        zonaNombreField.clear();
        zonaPaisField.clear();
        zonaResponsableField.clear();
        zonaObjetivoField.clear();
        zonasTableView.getSelectionModel().clearSelection();
        actualizarTextoBotonZona();
    }

    @FXML
    public void guardarReglaDesdeFormulario() {
        if (!exigirAccesoFinanciero("crear o actualizar reglas de margen")) {
            return;
        }

        try {
            Integer id = leerEntero(reglaIdField.getText(), siguienteIdRegla());
            String categoria = reglaCategoriaField.getText() == null ? "" : reglaCategoriaField.getText().trim();
            double margen = leerDouble(reglaMargenField.getText(), 0.0);
            boolean activa = reglaActivaCheckBox.isSelected();
            String descripcion = reglaDescripcionTextArea.getText() == null ? "" : reglaDescripcionTextArea.getText().trim();

            ReglaMargen regla = new ReglaMargen(id, categoria, margen, activa, descripcion);
            repoReglas.save(regla);
            ConsolaErroresDialog.reiniciarNoVolverAMostrarAnalisis();

            refrescarGestion();
            refrescarGraficas();
            ConsolaErroresDialog.mostrarInfo("Regla de margen", "Regla guardada correctamente.");
        } catch (Exception e) {
            ConsolaErroresDialog.mostrarError(
                "Error al Guardar Regla",
                "No se pudo guardar la regla de margen.\nDetalle: " + e.getMessage()
            );
        }
    }

    @FXML
    public void eliminarReglaSeleccionada() {
        if (!exigirAccesoFinanciero("eliminar reglas de margen")) {
            return;
        }

        ReglaMargen seleccionada = reglasTableView.getSelectionModel().getSelectedItem();
        String id = seleccionada != null ? String.valueOf(seleccionada.getId()) : reglaIdField.getText();

        if (id == null || id.isBlank()) {
            ConsolaErroresDialog.mostrarError("Eliminar regla", "Selecciona una regla o indica un ID válido.");
            return;
        }

        if (repoReglas.delete(id)) {
            ConsolaErroresDialog.reiniciarNoVolverAMostrarAnalisis();
            refrescarGestion();
            refrescarGraficas();
            ConsolaErroresDialog.mostrarInfo("Regla de margen", "Regla eliminada correctamente.");
        } else {
            ConsolaErroresDialog.mostrarError("Eliminar regla", "No se encontró la regla con ID " + id + ".");
        }
    }

    @FXML
    public void limpiarFormularioRegla() {
        if (!accesoFinancieroPermitido()) {
            return;
        }

        reglaIdField.clear();
        reglaCategoriaField.clear();
        reglaMargenField.clear();
        reglaActivaCheckBox.setSelected(true);
        reglaDescripcionTextArea.clear();
        reglasTableView.getSelectionModel().clearSelection();
        actualizarTextoBotonRegla();
    }

    // ==================== MÉTODOS CRUD ORIGINALES ====================

    public void cargarDatos() {
        List<ZonaComercial>zonas=repoZonas.findAll();
        if(zonas.isEmpty()){
            System.out.println("Aviso: el JSON de zonas está vacío o no existe. No se cargará ningún CSV; la lista permanecerá vacía.");
        }else {
            System.out.println("Zonas cargadas desde JSON correctamente.");
        }
        List<ReglaMargen> reglas = repoReglas.findAll();
        
        if (reglas.isEmpty()) {
            System.out.println("Aviso: el JSON de reglas está vacío o no existe. No se cargará ningún CSV; la lista permanecerá vacía.");
        } else {
            System.out.println("Reglas cargadas desde JSON correctamente.");
        }
    }
    //C
    public void addZona(Object obj) {
        if (!(obj instanceof ZonaComercial)) {
            System.out.println("Error: El objeto no es de la clase ZonaComercial");
            return;
        }
        ZonaComercial nuevaZona = (ZonaComercial) obj;
        repoZonas.save(nuevaZona);
        ConsolaErroresDialog.reiniciarNoVolverAMostrarAnalisis();
        System.out.println("Zona guardada correctamente.");
    }

    public void addMarginRule(ReglaMargen nuevaRegla) {
        repoReglas.save(nuevaRegla);
        ConsolaErroresDialog.reiniciarNoVolverAMostrarAnalisis();
        System.out.println("Regla de margen guardada correctamente.");
    }
    //R
    public List<ZonaComercial> readAllZones() {
        return repoZonas.findAll();
    }
    public List<ReglaMargen> readAllMarginRules() {
        return repoReglas.findAll();
    }
    // eleminar

    public void deleteZona(String id) {
        boolean exito = repoZonas.delete(id);
        
        if (exito) {
            ConsolaErroresDialog.reiniciarNoVolverAMostrarAnalisis();
            System.out.println(" Zona comercial con ID " + id + " eliminada correctamente.");
        } else {
            System.out.println(" Error: No encontró la zona con ID " + id + ".");
        }
    }

    public void deleteReglaMargen(String id) {
        boolean exito = repoReglas.delete(id);
        
        if (exito) {
            ConsolaErroresDialog.reiniciarNoVolverAMostrarAnalisis();
            System.out.println("Regla de margen con ID " + id + " eliminada correctamente.");
        } else {
            System.out.println("Error: No se encontró la regla con ID " + id + ".");
        }
    }
    //U
    public void updateZona(String id, String nuevoNombre, String nuevoPais, String nuevoResponsable, double nuevoObjetivo) {
        ZonaComercial zonaExistente = repoZonas.findById(id);

        if (zonaExistente == null) {
            System.out.println("Error: No  encontró la zona con ID " + id);
            return;
        }
        zonaExistente.setNombre(nuevoNombre);
        zonaExistente.setPais(nuevoPais);
        zonaExistente.setObjetivoFacturacionAnual(nuevoObjetivo);
        zonaExistente.setResponsableComercial(nuevoResponsable);

        repoZonas.save(zonaExistente);
        ConsolaErroresDialog.reiniciarNoVolverAMostrarAnalisis();
        System.out.println("Zona actualizada correctamente.");
    }

    public void updateReglaMargen(String id, String nuevaCategoria, double nuevoMargen, boolean nuevaActiva, String nuevaDescripcion) {
        ReglaMargen reglaExistente = repoReglas.findById(id);

        if (reglaExistente == null) {
            System.out.println(" Error: No  encontró la regla con ID " + id);
            return;
        }
        reglaExistente.setMargenMinimoPortcentaje(nuevoMargen);
        reglaExistente.setCategoriaProductoAfectada(nuevaCategoria);
        reglaExistente.setActiva(nuevaActiva);
        reglaExistente.setDescripcion(nuevaDescripcion);

        repoReglas.save(reglaExistente);
        ConsolaErroresDialog.reiniciarNoVolverAMostrarAnalisis();
        System.out.println("Regla de margen actualizada correctamente.");
    }

    private void cargarZonaEnFormulario(ZonaComercial zona) {
        if (zona == null) {
            actualizarTextoBotonZona();
            return;
        }
        zonaIdField.setText(valorSeguro(zona.getId()));
        zonaNombreField.setText(valorSeguro(zona.getNombre()));
        zonaPaisField.setText(valorSeguro(zona.getPais()));
        zonaResponsableField.setText(valorSeguro(zona.getResponsableComercial()));
        zonaObjetivoField.setText(BigDecimal.valueOf(zona.getObjetivoFacturacionAnual()).stripTrailingZeros().toPlainString());
        actualizarTextoBotonZona();
    }

    private void cargarReglaEnFormulario(ReglaMargen regla) {
        if (regla == null) {
            actualizarTextoBotonRegla();
            return;
        }
        reglaIdField.setText(valorSeguro(regla.getId()));
        reglaCategoriaField.setText(valorSeguro(regla.getCategoriaProductoAfectada()));
        reglaMargenField.setText(BigDecimal.valueOf(regla.getMargenMinimoPortcentaje()).stripTrailingZeros().toPlainString());
        reglaActivaCheckBox.setSelected(regla.isActiva());
        reglaDescripcionTextArea.setText(valorSeguro(regla.getDescripcion()));
        actualizarTextoBotonRegla();
    }

    void actualizarTextoBotonZona() {
        if (guardarZonaButton == null) {
            return;
        }

        boolean modoEdicion = zonaIdField != null && zonaIdField.getText() != null && !zonaIdField.getText().isBlank();
        guardarZonaButton.setText(modoEdicion ? "Modificar Zona" : "Crear Zona");
    }

    void actualizarTextoBotonRegla() {
        if (guardarReglaButton == null) {
            return;
        }

        boolean modoEdicion = reglaIdField != null && reglaIdField.getText() != null && !reglaIdField.getText().isBlank();
        guardarReglaButton.setText(modoEdicion ? "Modificar Regla" : "Crear Regla");
    }

    private void cargarGraficaCategorias() {
        categoriasBarChart.getData().clear();

        Map<String, BigDecimal> rankingFacturacion = calculadora.generarRankCategoriasPorFacturacion();
        Map<String, BigDecimal> rankingMargen = calculadora.generarRankCategorias();

        XYSeriesLoader.loadBarSeries(categoriasBarChart, "Facturación", rankingFacturacion, 8);
        XYSeriesLoader.loadBarSeries(categoriasBarChart, "Margen bruto", rankingMargen, 8);
    }

    private void cargarGraficaMensual() {
        kpisMensualesLineChart.getData().clear();

        KpisMensuales kpisMensuales = obtenerKpisMensuales();
        XYSeriesLoader.loadLineSeries(kpisMensualesLineChart, "Facturación mensual", kpisMensuales.facturacion());
        XYSeriesLoader.loadLineSeries(kpisMensualesLineChart, "Margen mensual", kpisMensuales.margen());
    }

    private void cargarGraficaMensualApilada() {
        if (stackedBarChartMensual == null) return;
        stackedBarChartMensual.getData().clear();

        KpisMensuales kpisMensuales = obtenerKpisMensuales();

        javafx.scene.chart.XYChart.Series<String, Number> seriesFact = new javafx.scene.chart.XYChart.Series<>();
        seriesFact.setName("Facturación");
        javafx.scene.chart.XYChart.Series<String, Number> seriesMarg = new javafx.scene.chart.XYChart.Series<>();
        seriesMarg.setName("Margen bruto");

        for (String mes : ultimosDoceMeses()) {
            double f = kpisMensuales.facturacion().getOrDefault(mes, BigDecimal.ZERO).doubleValue();
            double m = kpisMensuales.margen().getOrDefault(mes, BigDecimal.ZERO).doubleValue();
            seriesFact.getData().add(new javafx.scene.chart.XYChart.Data<>(mes, f));
            seriesMarg.getData().add(new javafx.scene.chart.XYChart.Data<>(mes, m));
        }

        stackedBarChartMensual.getData().addAll(seriesFact, seriesMarg);
    }

    private String obtenerTipoFiltroGraficaMensual() {
        SesionUsuario sesion = SesionAplicacion.obtener();
        if (sesion != null && sesion.esComercial() && sesion.zonasComerciales() != null && !sesion.zonasComerciales().isEmpty()) {
            return "Zona Comercial";
        }

        String tipoFiltro = exploradorController.getTipoFiltroSeleccionado();
        if (tipoFiltro == null || tipoFiltro.isBlank()) {
            return null;
        }

        return switch (tipoFiltro.trim().toLowerCase()) {
            case "categoría", "categoria", "zona comercial", "zonacomercial" -> tipoFiltro.trim();
            default -> null;
        };
    }

    private String obtenerValorFiltroGraficaMensual() {
        SesionUsuario sesion = SesionAplicacion.obtener();
        if (sesion != null && sesion.esComercial() && sesion.zonasComerciales() != null && !sesion.zonasComerciales().isEmpty()) {
            // Si solo tiene una zona asignada, devolverla; si tiene varias, dejar el filtro en null
            if (sesion.zonasComerciales().size() == 1) {
                return String.valueOf(sesion.zonasComerciales().get(0));
            }
            return null;
        }

        String valorFiltro = exploradorController.getValorFiltroSeleccionado();
        if (valorFiltro == null || valorFiltro.isBlank() || "Todas".equalsIgnoreCase(valorFiltro.trim())) {
            return null;
        }

        return valorFiltro.trim();
    }

    private List<ExcelExporter.RankingCategoriaExportRow> obtenerRankingCategoriasParaExportar() {
        if (rankingCategoriasTableView == null || rankingCategoriasTableView.getItems() == null) {
            return List.of();
        }

        return rankingCategoriasTableView.getItems().stream()
                .map(fila -> new ExcelExporter.RankingCategoriaExportRow(
                        fila.getPosicion(),
                        fila.getCategoria(),
                        fila.getFacturacion(),
                        fila.getMargen(),
                        fila.getMargenPorcentaje()))
                .toList();
    }

    private List<ExcelExporter.LineaBajoMargenExportRow> obtenerLineasBajoMargenParaExportar() {
        if (lineasBajoMargenTableView == null || lineasBajoMargenTableView.getItems() == null) {
            return List.of();
        }

        return lineasBajoMargenTableView.getItems().stream()
                .map(fila -> new ExcelExporter.LineaBajoMargenExportRow(
                        fila.getIdLinea(),
                        fila.getProducto(),
                        fila.getCategoria(),
                        fila.getMargenActual(),
                        fila.getMargenRequerido(),
                        fila.getDeficiencia()))
                .toList();
    }

    private void cargarGraficaZonas() {
        zonasPieChart.getData().clear();

        List<Map<String, Object>> rankingZonas = calculadora.generarRankingZonasPorDesviacion();
        List<PieChart.Data> data = new ArrayList<>();

        for (Map<String, Object> zona : rankingZonas) {
            BigDecimal real = toBigDecimal(zona.get("real"));
            if (real.compareTo(BigDecimal.ZERO) > 0) {
                data.add(new PieChart.Data(String.valueOf(zona.getOrDefault("nombre", "Zona")), real.doubleValue()));
            }
        }

        zonasPieChart.setData(FXCollections.observableArrayList(data));
    }

    private void cargarGraficaMargenPorCategoria() {
        if (margenCategoriasPieChart == null) return;
        margenCategoriasPieChart.getData().clear();

        Map<String, BigDecimal> rankingMargen = calculadora.generarRankCategorias();
        List<PieChart.Data> data = new ArrayList<>();

        for (Map.Entry<String, BigDecimal> e : rankingMargen.entrySet()) {
            BigDecimal val = e.getValue();
            if (val != null && val.compareTo(BigDecimal.ZERO) > 0) {
                data.add(new PieChart.Data(e.getKey(), val.doubleValue()));
            }
        }

        margenCategoriasPieChart.setData(FXCollections.observableArrayList(data));
    }

    private KpisMensuales obtenerKpisMensuales() {
        String tipoFiltro = obtenerTipoFiltroGraficaMensual();
        String valorFiltro = obtenerValorFiltroGraficaMensual();

        Map<String, BigDecimal> facturacionMensual = tipoFiltro == null
            ? importKpiController.calcularKPIMensualFacturacion(null)
            : importKpiController.calcularKPIMensualFacturacion(tipoFiltro, valorFiltro);
        Map<String, BigDecimal> margenMensual = tipoFiltro == null
            ? importKpiController.calcularKPIMensualMargen(null)
            : importKpiController.calcularKPIMensualMargen(tipoFiltro, valorFiltro);

        return new KpisMensuales(facturacionMensual, margenMensual);
    }

    private List<String> ultimosDoceMeses() {
        YearMonth ahora = YearMonth.now();
        List<String> meses = new ArrayList<>();
        for (int i = 11; i >= 0; i--) {
            meses.add(ahora.minusMonths(i).toString());
        }
        return meses;
    }

    private FileChooser crearSelectorExcel(String titulo, String nombrePorDefecto) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(titulo);
        fileChooser.setInitialFileName(nombrePorDefecto);
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Archivos Excel", "*.xlsx"));
        return fileChooser;
    }

    private void exportarExcelConDialogo(Stage stage,
                                         String titulo,
                                         String nombrePorDefecto,
                                         ExportacionExcel exportacion,
                                         String tituloExito,
                                         String mensajeExito,
                                         String tituloError,
                                         String contextoError) {
        FileChooser fileChooser = crearSelectorExcel(titulo, nombrePorDefecto);
        File selectedFile = fileChooser.showSaveDialog(stage);
        if (selectedFile == null) {
            return;
        }

        try {
            exportacion.exportar(selectedFile.getAbsolutePath());
            ConsolaErroresDialog.mostrarInfo(tituloExito, mensajeExito + "\n" + selectedFile.getAbsolutePath());
        } catch (Exception e) {
            ConsolaErroresDialog.mostrarError(
                tituloError,
                "No se pudo " + contextoError + ".\nDetalle: " + e.getMessage()
            );
        }
    }

    private Integer leerEntero(String texto, Integer valorPorDefecto) {
        if (texto == null || texto.trim().isEmpty()) {
            return valorPorDefecto;
        }
        return Integer.valueOf(texto.trim());
    }

    private double leerDouble(String texto, double valorPorDefecto) {
        if (texto == null || texto.trim().isEmpty()) {
            return valorPorDefecto;
        }
        return Double.parseDouble(texto.trim());
    }

    private Integer siguienteIdZona() {
        return repoZonas.findAll().stream()
            .map(ZonaComercial::getId)
            .filter(id -> id != null)
            .max(Integer::compareTo)
            .map(id -> id + 1)
            .orElse(1);
    }

    private Integer siguienteIdRegla() {
        return repoReglas.findAll().stream()
            .map(ReglaMargen::getId)
            .filter(id -> id != null)
            .max(Integer::compareTo)
            .map(id -> id + 1)
            .orElse(1);
    }

    private String valorSeguro(Object valor) {
        return valor == null ? "" : String.valueOf(valor);
    }

    private Integer toInteger(Object valor) {
        if (valor instanceof Number number) {
            return number.intValue();
        }
        if (valor == null) {
            return null;
        }
        return Integer.valueOf(String.valueOf(valor));
    }

    private BigDecimal toBigDecimal(Object valor) {
        if (valor instanceof BigDecimal bigDecimal) {
            return bigDecimal;
        }
        if (valor instanceof Number number) {
            return BigDecimal.valueOf(number.doubleValue());
        }
        if (valor == null) {
            return BigDecimal.ZERO;
        }
        return new BigDecimal(String.valueOf(valor));
    }

    public static final class RankingCategoriaRow {
        private final Integer posicion;
        private final String categoria;
        private final BigDecimal facturacion;
        private final BigDecimal margen;
        private final BigDecimal margenPorcentaje;

        public RankingCategoriaRow(Integer posicion, String categoria, BigDecimal facturacion, BigDecimal margen, BigDecimal margenPorcentaje) {
            this.posicion = posicion;
            this.categoria = categoria;
            this.facturacion = facturacion;
            this.margen = margen;
            this.margenPorcentaje = margenPorcentaje;
        }

        public Integer getPosicion() { return posicion; }
        public String getCategoria() { return categoria; }
        public BigDecimal getFacturacion() { return facturacion; }
        public BigDecimal getMargen() { return margen; }
        public BigDecimal getMargenPorcentaje() { return margenPorcentaje; }
    }

    public static final class LineaBajoMargenRow {
        private final Integer idLinea;
        private final String producto;
        private final String categoria;
        private final BigDecimal margenActual;
        private final BigDecimal margenRequerido;
        private final BigDecimal deficiencia;

        public LineaBajoMargenRow(Integer idLinea, String producto, String categoria, BigDecimal margenActual, BigDecimal margenRequerido, BigDecimal deficiencia) {
            this.idLinea = idLinea;
            this.producto = producto;
            this.categoria = categoria;
            this.margenActual = margenActual;
            this.margenRequerido = margenRequerido;
            this.deficiencia = deficiencia;
        }

        public Integer getIdLinea() { return idLinea; }
        public String getProducto() { return producto; }
        public String getCategoria() { return categoria; }
        public BigDecimal getMargenActual() { return margenActual; }
        public BigDecimal getMargenRequerido() { return margenRequerido; }
        public BigDecimal getDeficiencia() { return deficiencia; }
    }

    public static final class DesviacionZonaRow {
        private final String zona;
        private final String pais;
        private final BigDecimal objetivo;
        private final BigDecimal real;
        private final BigDecimal desviacion;
        private final BigDecimal porcentaje;

        public DesviacionZonaRow(String zona, String pais, BigDecimal objetivo, BigDecimal real, BigDecimal desviacion, BigDecimal porcentaje) {
            this.zona = zona;
            this.pais = pais;
            this.objetivo = objetivo;
            this.real = real;
            this.desviacion = desviacion;
            this.porcentaje = porcentaje;
        }

        public String getZona() { return zona; }
        public String getPais() { return pais; }
        public BigDecimal getObjetivo() { return objetivo; }
        public BigDecimal getReal() { return real; }
        public BigDecimal getDesviacion() { return desviacion; }
        public BigDecimal getPorcentaje() { return porcentaje; }
    }

    private static final class XYSeriesLoader {
        private static void loadBarSeries(BarChart<String, Number> chart, String nombreSerie, Map<String, BigDecimal> valores, int maxElementos) {
            if (chart == null || valores == null || valores.isEmpty()) {
                return;
            }

            javafx.scene.chart.XYChart.Series<String, Number> series = new javafx.scene.chart.XYChart.Series<>();
            series.setName(nombreSerie);

            int contador = 0;
            for (Map.Entry<String, BigDecimal> entrada : valores.entrySet()) {
                if (contador++ >= maxElementos) {
                    break;
                }
                series.getData().add(new javafx.scene.chart.XYChart.Data<>(entrada.getKey(), entrada.getValue()));
            }

            chart.getData().add(series);
        }

        private static void loadLineSeries(LineChart<String, Number> chart, String nombreSerie, Map<String, BigDecimal> valores) {
            if (chart == null || valores == null || valores.isEmpty()) {
                return;
            }

            javafx.scene.chart.XYChart.Series<String, Number> series = new javafx.scene.chart.XYChart.Series<>();
            series.setName(nombreSerie);

            for (Map.Entry<String, BigDecimal> entrada : valores.entrySet()) {
                series.getData().add(new javafx.scene.chart.XYChart.Data<>(entrada.getKey(), entrada.getValue()));
            }

            chart.getData().add(series);
        }
    }

    private record KpisMensuales(Map<String, BigDecimal> facturacion, Map<String, BigDecimal> margen) {
    }

    @FunctionalInterface
    private interface ExportacionExcel {
        void exportar(String ruta) throws Exception;
    }

}

