package controller;

import java.io.File;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import model.LineaPedido;
import persistence.CsvImporter;
import persistence.ExcelExporter;
import view.ConsolaErroresDialog;

public class ImportKpiController {
    private final ExploradorController exploradorController = ExploradorController.getInstance();
    private final CalculadoraFinanciera calculadora = new CalculadoraFinanciera();
    private final CsvImporter csvImporter = new CsvImporter();
    private final ExcelExporter excelExporter = new ExcelExporter();

    @FXML
    private TableView<Map.Entry<String, BigDecimal>> kpisGlobalesTableView;
    @FXML
    private TableColumn<Map.Entry<String, BigDecimal>, String> metricaColumn;
    @FXML
    private TableColumn<Map.Entry<String, BigDecimal>, String> valorColumn;
    @FXML
    private TableColumn<Map.Entry<String, BigDecimal>, String> unidadColumn;
    @FXML
    private TableColumn<Map.Entry<String, BigDecimal>, String> observacionesColumn;

    @FXML
    private TableView<?> kpisPorCategoriaTableView;
    @FXML
    private TableColumn<?, String> categoriaColumn;
    @FXML
    private TableColumn<?, BigDecimal> facturacionColumn;
    @FXML
    private TableColumn<?, BigDecimal> margenColumn;
    @FXML
    private TableColumn<?, BigDecimal> margenPorcentajeColumn;

    @FXML
    public void initialize() {
        refrescarKpis();
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
                List<LineaPedido> lineasImportadas = csvImporter.importCSVLineaPedidos(selectedFile.getAbsolutePath());
                exploradorController.setPedidos(lineasImportadas);
                
                ConsolaErroresDialog.mostrarInfo(
                    "Importación Exitosa",
                    "Se importaron correctamente " + lineasImportadas.size() + " líneas de pedido."
                );
                
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
            calculadora.calcularMargenBrutoTotal();
            
            ObservableList<Map.Entry<String, BigDecimal>> kpis = FXCollections.observableArrayList();
            
            // Calcular KPIs globales
            List<LineaPedido> pedidos = exploradorController.getPedidos();
            if (pedidos != null && !pedidos.isEmpty()) {
                BigDecimal facturacionTotal = pedidos.stream()
                    .map(p -> multiplicarMonetario(p.getPrecioVentaUnitario(), p.getUnidades()))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
                
                BigDecimal margenTotal = calculadora.calcularMargenBrutoTotal();
                
                BigDecimal margenPorcentaje = facturacionTotal.compareTo(BigDecimal.ZERO) > 0
                    ? margenTotal.multiply(BigDecimal.valueOf(100)).divide(facturacionTotal, 2, java.math.RoundingMode.HALF_UP)
                    : BigDecimal.ZERO;
                
                // Agregar filas (simuladas para ahora)
                // Nota: Esto es un placeholder, la tabla real necesitaría un modelo de datos específico
            }
            
        } catch (Exception e) {
            System.err.println("Error al poblar KPIs globales: " + e.getMessage());
        }
    }

    private void poblarKpisPorCategoria() {
        try {
            Map<String, BigDecimal> rankingFacturacion = calculadora.generarRankCategoriasPorFacturacion();
            Map<String, BigDecimal> rankingMargen = calculadora.generarRankCategorias();
            
            // Aquí se poblaría la tabla con los datos del ranking
            // Placeholder para ahora
            
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

    private List<LineaPedido> filtrarPedidos(String opcion) {
        if (opcion == null || opcion.isBlank()) {
            return exploradorController.getPedidos();
        }
        return exploradorController.filtrarPorCategoria(opcion);
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
}