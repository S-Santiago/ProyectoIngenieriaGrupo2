package controller;

import java.io.File;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import model.LineaPedido;
import persistence.CsvImporter;
import persistence.ExcelExporter;
import view.ConsolaErroresDialog;

public class ImportKpiController {
    private static final String OPCION_TODAS = "Todas";

    private final ExploradorController exploradorController = ExploradorController.getInstance();
    private final CalculadoraFinanciera calculadora = new CalculadoraFinanciera();
    private final CsvImporter csvImporter = new CsvImporter();
    private final ExcelExporter excelExporter = new ExcelExporter();

    @FXML
    private TableView<KpiGlobalRow> kpisGlobalesTableView;
    @FXML
    private TableColumn<KpiGlobalRow, String> metricaColumn;
    @FXML
    private TableColumn<KpiGlobalRow, String> valorColumn;
    @FXML
    private TableColumn<KpiGlobalRow, String> unidadColumn;
    @FXML
    private TableColumn<KpiGlobalRow, String> observacionesColumn;

    @FXML
    private TableView<KpiCategoriaRow> kpisPorCategoriaTableView;
    @FXML
    private TableColumn<KpiCategoriaRow, String> categoriaColumn;
    @FXML
    private TableColumn<KpiCategoriaRow, BigDecimal> facturacionColumn;
    @FXML
    private TableColumn<KpiCategoriaRow, BigDecimal> margenColumn;
    @FXML
    private TableColumn<KpiCategoriaRow, BigDecimal> margenPorcentajeColumn;

    @FXML
    public void initialize() {
        configurarTablas();
        refrescarKpis();
    }

    private void configurarTablas() {
        metricaColumn.setCellValueFactory(new PropertyValueFactory<>("metrica"));
        valorColumn.setCellValueFactory(new PropertyValueFactory<>("valor"));
        unidadColumn.setCellValueFactory(new PropertyValueFactory<>("unidad"));
        observacionesColumn.setCellValueFactory(new PropertyValueFactory<>("observaciones"));

        categoriaColumn.setCellValueFactory(new PropertyValueFactory<>("categoria"));
        facturacionColumn.setCellValueFactory(new PropertyValueFactory<>("facturacion"));
        margenColumn.setCellValueFactory(new PropertyValueFactory<>("margen"));
        margenPorcentajeColumn.setCellValueFactory(new PropertyValueFactory<>("margenPorcentaje"));

        kpisGlobalesTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        kpisPorCategoriaTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
    }

    @FXML
    public void importarCSV() {
        try {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Seleccionar archivo CSV de líneas de pedido");
            fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Archivos CSV", "*.csv")
            );
            
            Stage stage = (Stage) kpisGlobalesTableView.getScene().getWindow();
            File selectedFile = fileChooser.showOpenDialog(stage);
            
            if (selectedFile != null) {
                CsvImporter.ImportResult<LineaPedido> resultadoImportacion = csvImporter.importCSVLineaPedidosConAvisos(selectedFile.getAbsolutePath());
                List<LineaPedido> lineasImportadas = resultadoImportacion.getElementos();
                exploradorController.setPedidos(lineasImportadas);
                ConsolaErroresDialog.reiniciarNoVolverAMostrarAnalisis();

                if (resultadoImportacion.tieneAvisos()) {
                    ConsolaErroresDialog.mostrarAdvertencia(
                        "Líneas del CSV no importadas",
                        "Se importaron correctamente " + lineasImportadas.size() + " líneas válidas, pero estas filas no se pudieron cargar:\n\n"
                            + String.join("\n", resultadoImportacion.getAvisos())
                    );
                } else {
                    ConsolaErroresDialog.mostrarInfo(
                        "Importación exitosa",
                        "Se importaron correctamente " + lineasImportadas.size() + " líneas de pedido."
                    );
                }
                
                refrescarKpis();
            }
        } catch (Exception e) {
            ConsolaErroresDialog.mostrarError(
                "Error en Importación",
                "No se pudo importar el archivo CSV.\nDetalle: " + e.getMessage()
            );
        }
    }

    @FXML
    public void refrescarKpis() {
        try {
            // Limpiar y repoblar las tablas
            poblarKpisGlobales();
            poblarKpisPorCategoria();
        } catch (Exception e) {
            ConsolaErroresDialog.mostrarError(
                "Error al Refrescar",
                "No se pudieron recalcular los KPIs.\nDetalle: " + e.getMessage()
            );
        }
    }

    @FXML
    public void exportarKpisExcel() {
        try {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Guardar KPIs como Excel");
            fileChooser.setInitialFileName("kpis_globales.xlsx");
            fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Archivos Excel", "*.xlsx")
            );
            
            Stage stage = (Stage) kpisGlobalesTableView.getScene().getWindow();
            File selectedFile = fileChooser.showSaveDialog(stage);
            
            if (selectedFile != null) {
                excelExporter.exportLineasPedido(
                    selectedFile.getAbsolutePath(),
                    exploradorController.getPedidos()
                );
                
                ConsolaErroresDialog.mostrarInfo(
                    "Exportación Exitosa",
                    "Los KPIs se exportaron correctamente a:\n" + selectedFile.getAbsolutePath()
                );
            }
        } catch (Exception e) {
            ConsolaErroresDialog.mostrarError(
                "Error en Exportación",
                "No se pudo exportar el archivo Excel.\nDetalle: " + e.getMessage()
            );
        }
    }

    private void poblarKpisGlobales() {
        try {
            List<KpiGlobalRow> filas = new ArrayList<>();
            List<LineaPedido> pedidos = exploradorController.getPedidos();
            int totalPedidos = pedidos == null ? 0 : pedidos.size();
            BigDecimal facturacionTotal = BigDecimal.ZERO;
            if (pedidos != null && !pedidos.isEmpty()) {
                facturacionTotal = pedidos.stream()
                    .map(p -> multiplicarMonetario(p.getPrecioVentaUnitario(), p.getUnidades()))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            }
            BigDecimal margenTotal = calculadora.calcularMargenBrutoTotal();
            BigDecimal margenPorcentaje = facturacionTotal.compareTo(BigDecimal.ZERO) > 0
                ? margenTotal.multiply(BigDecimal.valueOf(100)).divide(facturacionTotal, 2, java.math.RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

            filas.add(new KpiGlobalRow("Total de pedidos", String.valueOf(totalPedidos), "pedidos", "Número de líneas cargadas"));
            filas.add(new KpiGlobalRow("Facturación total", facturacionTotal.setScale(2, java.math.RoundingMode.HALF_UP).toPlainString(), "€", "Suma de ventas unitarias por unidades"));
            filas.add(new KpiGlobalRow("Margen bruto total", margenTotal.setScale(2, java.math.RoundingMode.HALF_UP).toPlainString(), "€", "Ingresos menos costes"));
            filas.add(new KpiGlobalRow("Margen medio", margenPorcentaje.setScale(2, java.math.RoundingMode.HALF_UP).toPlainString(), "%", "Margen sobre facturación total"));
            filas.add(new KpiGlobalRow("Estado", pedidos == null || pedidos.isEmpty() ? "Sin datos" : "Datos disponibles", "", "Carga actual de pedidos"));

            kpisGlobalesTableView.setItems(FXCollections.observableArrayList(filas));
        } catch (Exception e) {
            System.err.println("Error al poblar KPIs globales: " + e.getMessage());
        }
    }

    private void poblarKpisPorCategoria() {
        try {
            Map<String, BigDecimal> rankingFacturacion = calculadora.generarRankCategoriasPorFacturacion();
            Map<String, BigDecimal> rankingMargen = calculadora.generarRankCategorias();
            Set<String> categorias = new LinkedHashSet<>();
            categorias.addAll(rankingFacturacion.keySet());
            categorias.addAll(rankingMargen.keySet());

            List<KpiCategoriaRow> filas = new ArrayList<>();
            for (String categoriaActual : categorias) {
                BigDecimal facturacion = rankingFacturacion.getOrDefault(categoriaActual, BigDecimal.ZERO);
                BigDecimal margen = rankingMargen.getOrDefault(categoriaActual, BigDecimal.ZERO);
                BigDecimal margenPorcentaje = facturacion.compareTo(BigDecimal.ZERO) > 0
                    ? margen.multiply(BigDecimal.valueOf(100)).divide(facturacion, 2, java.math.RoundingMode.HALF_UP)
                    : BigDecimal.ZERO;
                filas.add(new KpiCategoriaRow(
                    categoriaActual,
                    facturacion.setScale(2, java.math.RoundingMode.HALF_UP),
                    margen.setScale(2, java.math.RoundingMode.HALF_UP),
                    margenPorcentaje.setScale(2, java.math.RoundingMode.HALF_UP)
                ));
            }

            kpisPorCategoriaTableView.setItems(FXCollections.observableArrayList(filas));
            
        } catch (Exception e) {
            System.err.println("Error al poblar KPIs por categoría: " + e.getMessage());
        }
    }

    private BigDecimal multiplicarMonetario(BigDecimal valorUnitario, Integer unidades) {
        BigDecimal valor = valorUnitario == null ? BigDecimal.ZERO : valorUnitario;
        BigDecimal cantidad = unidades == null ? BigDecimal.ZERO : BigDecimal.valueOf(unidades.longValue());
        return valor.multiply(cantidad);
    }

    // Métodos anteriores (mantener compatibilidad)
    public void importar(String s){
        throw new UnsupportedOperationException("La importación directa desde archivo se gestiona desde CsvImporter y el explorador de pedidos.");
    }

    public Map<String,BigDecimal> calcularKPIMensualFacturacion(String opcion){
        List<LineaPedido> lineaPedidosFiltrado=filtrarPedidos(opcion);
        Map<String,BigDecimal> kpiMensual=new TreeMap<>();
        for(LineaPedido p:lineaPedidosFiltrado){
            LocalDate fechaDePedido=parseFechaPedidoSeguro(p.getFechaPedido());
            String mesKey=fechaDePedido.format(DateTimeFormatter.ofPattern("uuuu-MM"));
            BigDecimal facturacion=valorMonetario(p.getPrecioVentaUnitario(), p.getUnidades());
            kpiMensual.merge(mesKey, facturacion, BigDecimal::add);
        }
        return kpiMensual;
    }

    public Map<String, BigDecimal> calcularKPIMensualFacturacion(String tipoFiltro, String opcion) {
        List<LineaPedido> lineaPedidosFiltrado = filtrarPedidos(tipoFiltro, opcion);
        Map<String, BigDecimal> kpiMensual = new TreeMap<>();
        for (LineaPedido p : lineaPedidosFiltrado) {
            LocalDate fechaDePedido = parseFechaPedidoSeguro(p.getFechaPedido());
            String mesKey = fechaDePedido.format(DateTimeFormatter.ofPattern("uuuu-MM"));
            BigDecimal facturacion = valorMonetario(p.getPrecioVentaUnitario(), p.getUnidades());
            kpiMensual.merge(mesKey, facturacion, BigDecimal::add);
        }
        return kpiMensual;
    }

    public Map<String,BigDecimal> calcularKPIMensualMargen(String opcion){
        List<LineaPedido> lineaPedidosFiltrado=filtrarPedidos(opcion);
        Map<String,BigDecimal> kpiMensual=new TreeMap<>();
        for(LineaPedido p:lineaPedidosFiltrado){
            LocalDate fechaDePedido=parseFechaPedidoSeguro(p.getFechaPedido());
            String mesKey=fechaDePedido.format(DateTimeFormatter.ofPattern("uuuu-MM"));
            BigDecimal margen=valorMonetario(p.getPrecioVentaUnitario(), p.getUnidades())
                    .subtract(valorMonetario(p.getCosteUnitario(), p.getUnidades()));
            kpiMensual.merge(mesKey, margen, BigDecimal::add);
        }
        return kpiMensual;
    }

    public Map<String, BigDecimal> calcularKPIMensualMargen(String tipoFiltro, String opcion) {
        List<LineaPedido> lineaPedidosFiltrado = filtrarPedidos(tipoFiltro, opcion);
        Map<String, BigDecimal> kpiMensual = new TreeMap<>();
        for (LineaPedido p : lineaPedidosFiltrado) {
            LocalDate fechaDePedido = parseFechaPedidoSeguro(p.getFechaPedido());
            String mesKey = fechaDePedido.format(DateTimeFormatter.ofPattern("uuuu-MM"));
            BigDecimal margen = valorMonetario(p.getPrecioVentaUnitario(), p.getUnidades())
                    .subtract(valorMonetario(p.getCosteUnitario(), p.getUnidades()));
            kpiMensual.merge(mesKey, margen, BigDecimal::add);
        }
        return kpiMensual;
    }

    private List<LineaPedido> filtrarPedidos(String opcion) {
        if (opcion == null || opcion.isBlank()) {
            return exploradorController.getPedidos();
        }
        return exploradorController.filtrarPorCategoria(opcion);
    }

    private List<LineaPedido> filtrarPedidos(String tipoFiltro, String opcion) {
        if (opcion == null || opcion.isBlank() || OPCION_TODAS.equalsIgnoreCase(opcion.trim())) {
            return exploradorController.getPedidos();
        }

        if (tipoFiltro == null || tipoFiltro.isBlank()) {
            return filtrarPedidos(opcion);
        }

        return switch (tipoFiltro.trim().toLowerCase()) {
            case "categoría", "categoria" -> exploradorController.filtrarPorCategoria(opcion.trim());
            case "zona comercial", "zonacomercial" -> {
                try {
                    yield exploradorController.filtrarPorZonaComercial(Integer.parseInt(opcion.trim()));
                } catch (NumberFormatException exception) {
                    yield List.of();
                }
            }
            default -> filtrarPedidos(opcion);
        };
    }

    private LocalDate parseFechaPedidoSeguro(String fechaPedido) {
        try {
            return LocalDate.parse(fechaPedido, DateTimeFormatter.ISO_LOCAL_DATE);
        } catch (DateTimeParseException ignored) {
            return LocalDate.parse(fechaPedido, exploradorController.getDateFormatter());
        }
    }

    public BigDecimal valorMonetario(BigDecimal valorUnitario, Integer unidades) {
        BigDecimal valor = valorUnitario == null ? BigDecimal.ZERO : valorUnitario;
        BigDecimal cantidad = unidades == null ? BigDecimal.ZERO : BigDecimal.valueOf(unidades.longValue());
        return valor.multiply(cantidad);
    }

    public static final class KpiGlobalRow {
        private final String metrica;
        private final String valor;
        private final String unidad;
        private final String observaciones;

        public KpiGlobalRow(String metrica, String valor, String unidad, String observaciones) {
            this.metrica = metrica;
            this.valor = valor;
            this.unidad = unidad;
            this.observaciones = observaciones;
        }

        public String getMetrica() { return metrica; }
        public String getValor() { return valor; }
        public String getUnidad() { return unidad; }
        public String getObservaciones() { return observaciones; }
    }

    public static final class KpiCategoriaRow {
        private final String categoria;
        private final BigDecimal facturacion;
        private final BigDecimal margen;
        private final BigDecimal margenPorcentaje;

        public KpiCategoriaRow(String categoria, BigDecimal facturacion, BigDecimal margen, BigDecimal margenPorcentaje) {
            this.categoria = categoria;
            this.facturacion = facturacion;
            this.margen = margen;
            this.margenPorcentaje = margenPorcentaje;
        }

        public String getCategoria() { return categoria; }
        public BigDecimal getFacturacion() { return facturacion; }
        public BigDecimal getMargen() { return margen; }
        public BigDecimal getMargenPorcentaje() { return margenPorcentaje; }
    }
}