package controller;

import java.io.File;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import model.ReglaMargen;
import model.ZonaComercial;
import persistence.CsvImporter;
import persistence.ExcelExporter;
import persistence.JsonRepositoryReglaMargen;
import persistence.JsonRepositoryZonaComercial;
import view.ConsolaErroresDialog;

public class RentabilidadController {
    private final JsonRepositoryZonaComercial repoZonas = new JsonRepositoryZonaComercial();
    private final JsonRepositoryReglaMargen repoReglas = new JsonRepositoryReglaMargen();
    private final CsvImporter csvImporter = new CsvImporter();
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
    public void initialize() {
        configurarTablas();
        refrescarPanel();
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
                ConsolaErroresDialog.mostrarInfo(
                    "Análisis de Margen",
                    "Se encontraron " + lineasBajoMargen.size() + 
                    " líneas de pedido por debajo del margen mínimo requerido."
                );
            } else {
                ConsolaErroresDialog.mostrarInfo(
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
                ConsolaErroresDialog.mostrarInfo(
                    "Desviaciones por Zona",
                    "No hay zonas comerciales cargadas o no existen datos para calcular desviaciones."
                );
            } else {
                ConsolaErroresDialog.mostrarInfo(
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
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Guardar Análisis como Excel");
            fileChooser.setInitialFileName("analisis_rentabilidad.xlsx");
            fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Archivos Excel", "*.xlsx")
            );
            
            Stage stage = (Stage) rankingCategoriasTableView.getScene().getWindow();
            File selectedFile = fileChooser.showSaveDialog(stage);
            
            if (selectedFile != null) {
                excelExporter.exportLineasPedido(
                    selectedFile.getAbsolutePath(),
                    exploradorController.getPedidos()
                );
                
                ConsolaErroresDialog.mostrarInfo(
                    "Exportación Exitosa",
                    "El análisis se exportó correctamente a:\n" + selectedFile.getAbsolutePath()
                );
            }
        } catch (Exception e) {
            ConsolaErroresDialog.mostrarError(
                "Error en Exportación",
                "No se pudo exportar el análisis.\nDetalle: " + e.getMessage()
            );
        }
    }

    // ==================== MÉTODOS CRUD ORIGINALES ====================

    public void cargarDatos() {
        //carga zona
        List<ZonaComercial>zonas=repoZonas.findAll();
        if(zonas.isEmpty()){
            System.out.println(" JSON de Zonas vacío  Cargando desde CSV...");
            if (Files.exists(Paths.get("data", "zonas.csv"))) {
                try {
                    List<ZonaComercial>zonaComercialsCSv=csvImporter.importCSVZonasComerciales("data/zonas.csv");
                    for(ZonaComercial z:zonaComercialsCSv){
                        //save a repozonas
                        repoZonas.save(z);
                    }
                } catch (Exception e) {
                    System.out.println("Aviso: no se pudo cargar data/zonas.csv. Se continuará con la lista vacía. Detalle: " + e.getMessage());
                }
            } else {
                System.out.println("Aviso: no existe data/zonas.csv. Se continuará con la lista vacía.");
            }
        }else {
            System.out.println("Zonas cargadas desde JSON correctamente.");
        }
    List<ReglaMargen> reglas = repoReglas.findAll();
        
        if (reglas.isEmpty()) {
            System.out.println("JSON de Reglas vacío. Cargando desde CSV...");
            if (Files.exists(Paths.get("data", "reglas.csv"))) {
                try {
                    List<ReglaMargen> reglasCSV = csvImporter.importCSVReglasMargen("data/reglas.csv");
                    for (ReglaMargen r : reglasCSV) {
                        repoReglas.save(r);
                    }
                } catch (Exception e) {
                    System.out.println("Aviso: no se pudo cargar data/reglas.csv. Se continuará con la lista vacía. Detalle: " + e.getMessage());
                }
            } else {
                System.out.println("Aviso: no existe data/reglas.csv. Se continuará con la lista vacía.");
            }
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
        System.out.println("Zona guardada correctamente.");
    }

    public void addMarginRule(ReglaMargen nuevaRegla) {
        repoReglas.save(nuevaRegla);
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
            System.out.println(" Zona comercial con ID " + id + " eliminada correctamente.");
        } else {
            System.out.println(" Error: No encontró la zona con ID " + id + ".");
        }
    }

    public void deleteReglaMargen(String id) {
        boolean exito = repoReglas.delete(id);
        
        if (exito) {
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
        System.out.println("Regla de margen actualizada correctamente.");
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

}

