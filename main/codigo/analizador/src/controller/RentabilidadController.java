package controller;

import java.io.File;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
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
    private TableView<?> rankingCategoriasTableView;
    @FXML
    private TableColumn<?, Integer> rankPosicionColumn;
    @FXML
    private TableColumn<?, String> rankCategoriaColumn;
    @FXML
    private TableColumn<?, BigDecimal> rankFacturacionColumn;
    @FXML
    private TableColumn<?, BigDecimal> rankMargenColumn;
    @FXML
    private TableColumn<?, BigDecimal> rankMargenPorcentajeColumn;

    @FXML
    private TableView<?> lineasBajoMargenTableView;
    @FXML
    private TableColumn<?, Integer> idLineaColumn;
    @FXML
    private TableColumn<?, String> productoColumn;
    @FXML
    private TableColumn<?, String> categoriaColumn;
    @FXML
    private TableColumn<?, BigDecimal> margenActualColumn;
    @FXML
    private TableColumn<?, BigDecimal> margenRequeridoColumn;
    @FXML
    private TableColumn<?, BigDecimal> deficienciaColumn;
    @FXML
    private Label contadorBajoMargenLabel;

    @FXML
    private TableView<?> desviacionesZonasTableView;
    @FXML
    private TableColumn<?, String> zonaColumn;
    @FXML
    private TableColumn<?, String> paisColumn;
    @FXML
    private TableColumn<?, BigDecimal> objetivoZonaColumn;
    @FXML
    private TableColumn<?, BigDecimal> realizadoZonaColumn;
    @FXML
    private TableColumn<?, BigDecimal> desviacionColumn;
    @FXML
    private TableColumn<?, BigDecimal> porcentajeZonaColumn;

    @FXML
    public void initialize() {
        refrescarPanel();
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
            
            // Aquí se poblaría la tabla con los datos de desviación
            // Placeholder para ahora
            
        } catch (Exception e) {
            ConsolaErroresDialog.mostrarError(
                "Error en Desviaciones",
                "No se pudieron calcular las desviaciones.\nDetalle: " + e.getMessage()
            );
        }
    }

    private void mostrarRankingCategorias() {
        try {
            Map<String, BigDecimal> ranking = calculadora.generarRankCategoriasPorFacturacion();
            // Aquí se poblaría la tabla
            
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

}

