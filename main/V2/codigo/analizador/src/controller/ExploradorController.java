package controller;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import model.EstadoPedido;
import model.LineaPedido;
import persistence.CsvImporter;
import view.ConsolaErroresDialog;

/**
 * Controlador para el explorador de pedidos.
 * Implementado como Singleton para asignarlo manualmente desde VistaManager.
 */
public class ExploradorController {

    private static final String OPCION_TODAS = "Todas";
    private static final String FILTRO_TODAS = "Todas";
    private static final String FILTRO_CATEGORIA = "Categoría";
    private static final String FILTRO_ZONA = "Zona Comercial";
    private static final String FILTRO_ESTADO = "Estado";
    private static final String FILTRO_FECHA = "Fecha";
    private static final Path DEFAULT_CSV_FILE = Paths.get("src", "resources", "data", "lineas_pedidos.csv");

    private static ExploradorController instance = null;

    private final List<LineaPedido> pedidos = new ArrayList<>();
    private final ObservableList<LineaPedido> pedidosTabla = FXCollections.observableArrayList();
    private final List<String> validationErrors = new ArrayList<>();
    private final CsvImporter csvImporter = new CsvImporter();
    private Integer zonaComercialForzada;
    private boolean filtroComercialBloqueado;

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-uuuu")
            .withResolverStyle(ResolverStyle.STRICT);

    @FXML private ComboBox<String> tipoFiltroComboBox;
    @FXML private TextField valorFiltroTextField;
    @FXML private ComboBox<String> valorFiltroComboBox;
    @FXML private DatePicker fechaInicioDatePicker;
    @FXML private DatePicker fechaFinDatePicker;
    @FXML private Label valorFiltroLabel;
    @FXML private Label fechaInicioLabel;
    @FXML private Label fechaFinLabel;
    @FXML private Label estadoLabel;

    @FXML private TableView<LineaPedido> pedidosTableView;
    @FXML private TableColumn<LineaPedido, Integer> idLineaColumn;
    @FXML private TableColumn<LineaPedido, Integer> idPedidoColumn;
    @FXML private TableColumn<LineaPedido, String> referenciaProdutoColumn;
    @FXML private TableColumn<LineaPedido, String> descripcionProductoColumn;
    @FXML private TableColumn<LineaPedido, String> categoriaColumn;
    @FXML private TableColumn<LineaPedido, Integer> zonaComercialColumn;
    @FXML private TableColumn<LineaPedido, Integer> unidadesColumn;
    @FXML private TableColumn<LineaPedido, BigDecimal> costeUnitarioColumn;
    @FXML private TableColumn<LineaPedido, BigDecimal> precioVentaUnitarioColumn;
    @FXML private TableColumn<LineaPedido, String> fechaPedidoColumn;
    @FXML private TableColumn<LineaPedido, Object> estadoColumn;

    public static ExploradorController getInstance() {
        if (instance == null) {
            instance = new ExploradorController();
        }
        return instance;
    }

    private ExploradorController() {
    }

    public DateTimeFormatter getDateFormatter() {
        return DATE_TIME_FORMATTER;
    }

    public String getTipoFiltroSeleccionado() {
        return tipoFiltroComboBox == null ? null : tipoFiltroComboBox.getValue();
    }

    public String getValorFiltroSeleccionado() {
        if (valorFiltroComboBox != null && valorFiltroComboBox.getValue() != null) {
            return valorFiltroComboBox.getValue();
        }

        return valorFiltroTextField != null ? valorFiltroTextField.getText() : null;
    }

    public void aplicarSesion(SesionUsuario sesion) {
        if (sesion != null && sesion.esComercial() && sesion.zonasComerciales() != null && !sesion.zonasComerciales().isEmpty()) {
            // Usuario comercial con una o varias zonas: no forzamos un único valor en la UI,
            // pero las vistas filtrarán los pedidos por las zonas asignadas.
            zonaComercialForzada = null;
            filtroComercialBloqueado = false;
        } else {
            desbloquearFiltroComercial();
        }
    }

    public void fijarFiltroZonaComercial(Integer zonaId, boolean bloquearCambios) {
        if (tipoFiltroComboBox == null || valorFiltroComboBox == null || zonaId == null) {
            return;
        }

        tipoFiltroComboBox.setDisable(bloquearCambios);
        valorFiltroComboBox.setDisable(false);
        tipoFiltroComboBox.getSelectionModel().select(FILTRO_ZONA);
        actualizarControlesFiltro();
        valorFiltroComboBox.getSelectionModel().select(String.valueOf(zonaId));
        valorFiltroComboBox.setDisable(bloquearCambios);
        tipoFiltroComboBox.setDisable(bloquearCambios);
    }

    public void desbloquearFiltroComercial() {
        zonaComercialForzada = null;
        filtroComercialBloqueado = false;
        if (tipoFiltroComboBox != null) {
            tipoFiltroComboBox.setDisable(false);
        }
        if (valorFiltroComboBox != null) {
            valorFiltroComboBox.setDisable(false);
        }
    }

    @FXML
    public void initialize() {
        configurarColumnas();
        configurarControlesFiltro();
        pedidosTableView.setItems(pedidosTabla);
        aplicarSesion(SesionAplicacion.obtener());
        refrescarTabla();
        Platform.runLater(this::cargarPedidosDesdeCsv);
    }

    private List<LineaPedido> aplicarSesion(List<LineaPedido> lineas) {
        if (lineas == null || lineas.isEmpty()) {
            return new ArrayList<>();
        }

        SesionUsuario sesion = SesionAplicacion.obtener();
        if (sesion == null || sesion.esDirectorFinanciero() || sesion.zonasComerciales() == null || sesion.zonasComerciales().isEmpty()) {
            return new ArrayList<>(lineas);
        }

        var zonasPermitidas = sesion.zonasComerciales();
        return lineas.stream()
            .filter(pedido -> pedido != null && pedido.getZonaComercial() != null && zonasPermitidas.contains(pedido.getZonaComercial()))
            .collect(Collectors.toList());
    }

    private void cargarPedidosDesdeCsv() {
        if (!pedidos.isEmpty()) {
            return;
        }

        // Cargar en segundo plano para no bloquear el hilo de la interfaz
        actualizarEstado("Cargando pedidos desde CSV...");

        Task<CsvImporter.ImportResult<LineaPedido>> tareaCarga = new Task<>() {
            @Override
            protected CsvImporter.ImportResult<LineaPedido> call() throws Exception {
                return cargarPedidosDesdeFuenteDisponible();
            }
        };

        tareaCarga.setOnSucceeded(evt -> {
            CsvImporter.ImportResult<LineaPedido> resultadoImportacion = tareaCarga.getValue();
            List<LineaPedido> cargados = resultadoImportacion == null ? List.of() : resultadoImportacion.getElementos();
            List<LineaPedido> visibles = aplicarSesion(cargados);
            int avisos = resultadoImportacion == null ? 0 : resultadoImportacion.getAvisos().size();

            if (resultadoImportacion != null && resultadoImportacion.tieneAvisos()) {
                ConsolaErroresDialog.mostrarAdvertencia(
                    "Líneas del CSV no importadas",
                    "Se cargaron " + cargados.size() + " líneas válidas, pero estas filas no se pudieron importar:\n\n"
                        + String.join("\n", resultadoImportacion.getAvisos())
                );
            }

            if (visibles == null || visibles.isEmpty()) {
                actualizarEstado(avisos > 0
                    ? "El CSV no devolvió pedidos válidos para tu sesión. Avisos: " + avisos
                    : "El CSV no devolvió pedidos válidos.");
            } else {
                pedidos.addAll(visibles);
                refrescarOpcionesFiltros();
                actualizarEstado(avisos > 0
                    ? "Pedidos cargados desde CSV: " + pedidos.size() + " | Avisos: " + avisos
                    : "Pedidos cargados desde CSV: " + pedidos.size());
                refrescarTabla();
            }
        });

        tareaCarga.setOnFailed(evt -> {
            Throwable ex = tareaCarga.getException();
            String msg = ex == null ? "Error desconocido al cargar CSV." : ex.getMessage();
            actualizarEstado("Error al cargar CSV: " + msg);
        });

        Thread hilo = new Thread(tareaCarga, "csv-loader");
        hilo.setDaemon(true);
        hilo.start();
    }

    private CsvImporter.ImportResult<LineaPedido> cargarPedidosDesdeFuenteDisponible() throws IOException {
        if (Files.exists(DEFAULT_CSV_FILE)) {
            return csvImporter.importCSVLineaPedidosConAvisos(DEFAULT_CSV_FILE.toString());
        }

        try (var recurso = getClass().getResourceAsStream("/data/lineas_pedidos.csv")) {
            if (recurso != null) {
                return csvImporter.importCSVLineaPedidosConAvisos(new java.io.InputStreamReader(recurso, java.nio.charset.StandardCharsets.UTF_8));
            }
        }

        throw new IOException("No se encontró el CSV de líneas de pedido en src/resources/data ni en classpath.");
    }

    private void configurarColumnas() {
        idLineaColumn.setCellValueFactory(new PropertyValueFactory<>("idLinea"));
        idPedidoColumn.setCellValueFactory(new PropertyValueFactory<>("idPedido"));
        referenciaProdutoColumn.setCellValueFactory(new PropertyValueFactory<>("referenciaProduto"));
        descripcionProductoColumn.setCellValueFactory(new PropertyValueFactory<>("descripcionProducto"));
        categoriaColumn.setCellValueFactory(new PropertyValueFactory<>("categoria"));
        zonaComercialColumn.setCellValueFactory(new PropertyValueFactory<>("zonaComercial"));
        unidadesColumn.setCellValueFactory(new PropertyValueFactory<>("unidades"));
        costeUnitarioColumn.setCellValueFactory(new PropertyValueFactory<>("costeUnitario"));
        precioVentaUnitarioColumn.setCellValueFactory(new PropertyValueFactory<>("precioVentaUnitario"));
        fechaPedidoColumn.setCellValueFactory(new PropertyValueFactory<>("fechaPedido"));
        estadoColumn.setCellValueFactory(new PropertyValueFactory<>("estado"));
    }

    private void configurarControlesFiltro() {
        tipoFiltroComboBox.getItems().setAll(FILTRO_TODAS, FILTRO_CATEGORIA, FILTRO_ZONA, FILTRO_ESTADO, FILTRO_FECHA);
        tipoFiltroComboBox.getSelectionModel().select(FILTRO_TODAS);
        tipoFiltroComboBox.valueProperty().addListener((observable, valorAnterior, valorNuevo) -> {
            actualizarControlesFiltro();
            if (!FILTRO_FECHA.equals(valorNuevo)) {
                aplicarFiltroDesdeUI();
            }
        });

        if (valorFiltroComboBox != null) {
            valorFiltroComboBox.valueProperty().addListener((observable, valorAnterior, valorNuevo) -> {
                if (!FILTRO_FECHA.equals(tipoFiltroComboBox.getValue())) {
                    aplicarFiltroDesdeUI();
                }
            });
        }

        valorFiltroTextField.textProperty().addListener((observable, valorAnterior, valorNuevo) -> {
            if (!FILTRO_FECHA.equals(tipoFiltroComboBox.getValue())) {
                aplicarFiltroDesdeUI();
            }
        });

        fechaInicioDatePicker.valueProperty().addListener((observable, valorAnterior, valorNuevo) -> {
            if (FILTRO_FECHA.equals(tipoFiltroComboBox.getValue()) && fechaInicioDatePicker.getValue() != null && fechaFinDatePicker.getValue() != null) {
                aplicarFiltroDesdeUI();
            }
        });

        fechaFinDatePicker.valueProperty().addListener((observable, valorAnterior, valorNuevo) -> {
            if (FILTRO_FECHA.equals(tipoFiltroComboBox.getValue()) && fechaInicioDatePicker.getValue() != null && fechaFinDatePicker.getValue() != null) {
                aplicarFiltroDesdeUI();
            }
        });

        refrescarOpcionesFiltros();

        valorFiltroTextField.setPromptText("Escribe la categoría");
        fechaInicioLabel.setVisible(false);
        fechaInicioLabel.setManaged(false);
        fechaInicioDatePicker.setVisible(false);
        fechaInicioDatePicker.setManaged(false);
        fechaFinLabel.setVisible(false);
        fechaFinLabel.setManaged(false);
        fechaFinDatePicker.setVisible(false);
        fechaFinDatePicker.setManaged(false);

        actualizarControlesFiltro();
    }

    @FXML
    public void actualizarControlesFiltro() {
        String tipoFiltro = tipoFiltroComboBox.getValue();
        refrescarOpcionesFiltros();
        boolean usaFecha = FILTRO_FECHA.equals(tipoFiltro);
        boolean usaCombo = !usaFecha;

        valorFiltroLabel.setVisible(usaCombo);
        valorFiltroLabel.setManaged(usaCombo);
        valorFiltroTextField.setVisible(false);
        valorFiltroTextField.setManaged(false);

        if (valorFiltroComboBox != null) {
            valorFiltroComboBox.setVisible(usaCombo);
            valorFiltroComboBox.setManaged(usaCombo);
        }

        fechaInicioLabel.setVisible(usaFecha);
        fechaInicioLabel.setManaged(usaFecha);
        fechaInicioDatePicker.setVisible(usaFecha);
        fechaInicioDatePicker.setManaged(usaFecha);
        fechaFinLabel.setVisible(usaFecha);
        fechaFinLabel.setManaged(usaFecha);
        fechaFinDatePicker.setVisible(usaFecha);
        fechaFinDatePicker.setManaged(usaFecha);

        switch (tipoFiltro) {
            case FILTRO_TODAS -> {
                valorFiltroLabel.setText("Valor");
                if (valorFiltroComboBox != null) {
                    valorFiltroComboBox.setPromptText("Selecciona una opción");
                }
            }
            case FILTRO_CATEGORIA -> {
                valorFiltroLabel.setText("Categoría");
                if (valorFiltroComboBox != null) {
                    valorFiltroComboBox.setPromptText("Selecciona la categoría");
                }
            }
            case FILTRO_ZONA -> {
                valorFiltroLabel.setText("Zona comercial");
                if (valorFiltroComboBox != null) {
                    valorFiltroComboBox.setPromptText("Selecciona la zona comercial");
                }
            }
            case FILTRO_ESTADO -> {
                valorFiltroLabel.setText("Estado");
                if (valorFiltroComboBox != null) {
                    valorFiltroComboBox.setPromptText("Selecciona el estado");
                }
            }
            case FILTRO_FECHA -> {
                valorFiltroLabel.setText("Valor");
            }
            default -> {
                valorFiltroLabel.setText("Categoría");
                if (valorFiltroComboBox != null) {
                    valorFiltroComboBox.setPromptText("Selecciona la categoría");
                }
            }
        }
    }

    public List<String> getValidationErrors() {
        return new ArrayList<>(validationErrors);
    }

    public void clearValidationErrors() {
        validationErrors.clear();
    }

    public List<LineaPedido> getPedidos() {
        return new ArrayList<>(pedidos);
    }

    public void limpiarPedidos() {
        pedidos.clear();
        mostrarTodosLosPedidos();
    }

    public void setPedidos(List<LineaPedido> nuevosPedidos) {
        clearValidationErrors();
        pedidos.clear();

        List<LineaPedido> pedidosVisibles = aplicarSesion(nuevosPedidos);

        if (pedidosVisibles == null || pedidosVisibles.isEmpty()) {
            actualizarEstado("No hay pedidos para mostrar.");
            mostrarTodosLosPedidos();
            return;
        }

        for (LineaPedido pedido : pedidosVisibles) {
            if (pedido == null) {
                validationErrors.add("Se ha encontrado un pedido nulo.");
                continue;
            }

            if (validarPedido(pedido)) {
                pedidos.add(pedido);
            }
        }

        mostrarTodosLosPedidos();
        refrescarOpcionesFiltros();

        if (!validationErrors.isEmpty()) {
            actualizarEstado("Pedidos cargados: " + pedidos.size() + " | Avisos: " + validationErrors.size());
        } else {
            actualizarEstado("Pedidos cargados: " + pedidos.size());
        }

        refrescarTabla();
    }

    private boolean validarPedido(LineaPedido pedido) {
        boolean valido = true;

        if (pedido.getUnidades() == null || pedido.getUnidades() <= 0) {
            validationErrors.add(String.format("El pedido con id %d tiene unidades no válidas.", pedido.getIdPedido()));
            valido = false;
        }

        if (pedido.getCosteUnitario() == null || pedido.getCosteUnitario().compareTo(BigDecimal.ZERO) <= 0) {
            validationErrors.add(String.format("El pedido con id %d tiene coste unitario negativo.", pedido.getIdPedido()));
            valido = false;
        }

        if (pedido.getPrecioVentaUnitario() == null || pedido.getPrecioVentaUnitario().compareTo(BigDecimal.ZERO) <= 0) {
            validationErrors.add(String.format("El pedido con id %d tiene precio de venta unitario negativo.", pedido.getIdPedido()));
            valido = false;
        }

        if (pedido.getFechaPedido() == null || pedido.getFechaPedido().isEmpty()) {
            validationErrors.add(String.format("El pedido con id %d no tiene fecha de pedido válida.", pedido.getIdPedido()));
            valido = false;
        } else {
            try {
                parseFechaPedido(pedido.getFechaPedido());
            } catch (DateTimeParseException exception) {
                validationErrors.add(String.format("El pedido con id %d tiene una fecha con formato incorrecto.", pedido.getIdPedido()));
                valido = false;
            }
        }

        if (pedido.getCategoria() == null || pedido.getCategoria().isEmpty()) {
            validationErrors.add(String.format("El pedido con id %d no tiene categoría válida.", pedido.getIdPedido()));
            valido = false;
        }

        if (pedido.getZonaComercial() == null || pedido.getZonaComercial() <= 0) {
            validationErrors.add(String.format("El pedido con id %d no tiene zona comercial válida.", pedido.getIdPedido()));
            valido = false;
        }

        if (pedido.getEstado() == null) {
            validationErrors.add(String.format("El pedido con id %d no tiene estado válido.", pedido.getIdPedido()));
            valido = false;
        }

        if (pedido.getReferenciaProduto() == null || pedido.getReferenciaProduto().isEmpty()) {
            validationErrors.add(String.format("El pedido con id %d no tiene referencia de producto válida.", pedido.getIdPedido()));
            valido = false;
        }

        if (pedido.getIdPedido() == null || pedido.getIdPedido() <= 0) {
            validationErrors.add(String.format("El pedido con id %d tiene un ID no válido.", pedido.getIdPedido()));
            valido = false;
        }

        if (pedido.getIdLinea() == null || pedido.getIdLinea() <= 0) {
            validationErrors.add(String.format("El pedido con id %d tiene un ID línea no válido.", pedido.getIdLinea()));
            valido = false;
        }

        return valido;
    }

    public Set<String> obtenerCategoriasUnicas() {
        Set<String> categorias = new HashSet<>();
        for (LineaPedido pedido : pedidos) {
            if (pedido.getCategoria() != null) {
                categorias.add(pedido.getCategoria());
            }
        }
        return categorias;
    }

    public Set<Integer> obtenerZonasComerciales() {
        Set<Integer> zonas = new HashSet<>();
        for (LineaPedido pedido : pedidos) {
            if (pedido.getZonaComercial() != null) {
                zonas.add(pedido.getZonaComercial());
            }
        }
        return zonas;
    }

    public Set<String> obtenerEstadosUnicos() {
        Set<String> estados = new HashSet<>();
        for (LineaPedido pedido : pedidos) {
            if (pedido.getEstado() != null) {
                estados.add(pedido.getEstado().toString());
            }
        }
        return estados;
    }

    public List<LineaPedido> filtrarPorCategoria(String categoria) {
        List<LineaPedido> filtrados = new ArrayList<>();
        if (categoria == null || categoria.isBlank()) {
            return filtrados;
        }

        for (LineaPedido pedido : pedidos) {
            if (pedido.getCategoria() != null && pedido.getCategoria().equalsIgnoreCase(categoria)) {
                filtrados.add(pedido);
            }
        }
        return filtrados;
    }

    public void refrescarTabla() {
        if (pedidosTableView == null) {
            return;
        }

        Runnable accion = () -> {
            pedidosTabla.setAll(new java.util.ArrayList<>(pedidos));
            pedidosTableView.setItems(FXCollections.observableArrayList(pedidosTabla));
            pedidosTableView.refresh();
        };

        ejecutarEnHiloFxSiEsPosible(accion);
    }

    public List<LineaPedido> filtrarPorZonaComercial(int idZona) {
        List<LineaPedido> filtrados = new ArrayList<>();
        for (LineaPedido pedido : pedidos) {
            if (pedido.getZonaComercial() != null && pedido.getZonaComercial() == idZona) {
                filtrados.add(pedido);
            }
        }
        return filtrados;
    }

    public List<LineaPedido> filtrarPorEstado(String estado) {
        List<LineaPedido> filtrados = new ArrayList<>();
        if (estado == null || estado.isBlank()) {
            return filtrados;
        }

        for (LineaPedido pedido : pedidos) {
            if (pedido.getEstado() != null && pedido.getEstado().toString().equalsIgnoreCase(estado)) {
                filtrados.add(pedido);
            }
        }
        return filtrados;
    }

    public List<LineaPedido> filtrarPorFecha(String fechaInicio, String fechaFin) {
        List<LineaPedido> filtrados = new ArrayList<>();

        try {
            LocalDate inicio = parseFechaPedido(fechaInicio);
            LocalDate fin = parseFechaPedido(fechaFin);

            if (inicio.isAfter(fin)) {
                validationErrors.add("La fecha de inicio no puede ser posterior a la fecha de fin.");
                return filtrados;
            }

            for (LineaPedido pedido : pedidos) {
                if (pedido.getFechaPedido() == null || pedido.getFechaPedido().isBlank()) {
                    continue;
                }

                LocalDate fechaPedido = parseFechaPedido(pedido.getFechaPedido());
                if (!fechaPedido.isBefore(inicio) && !fechaPedido.isAfter(fin)) {
                    filtrados.add(pedido);
                }
            }
        } catch (DateTimeParseException exception) {
            validationErrors.add("Error al parsear las fechas. Usa el formato dd-MM-uuuu.");
        }

        return filtrados;
    }

    @Deprecated
    public List<LineaPedido> filtrarPedidosPorCategoria(String opcionSeleccionada) {
        if (opcionSeleccionada == null) {
            return new ArrayList<>(pedidos);
        }

        if (valorFiltroTextField == null && fechaInicioDatePicker == null && fechaFinDatePicker == null) {
            return new ArrayList<>(pedidos);
        }

        String tipoFiltro = opcionSeleccionada.trim().toLowerCase();

        return switch (tipoFiltro) {
            case "categoría", "categoria" -> filtrarPorCategoria(obtenerValorFiltroSeleccionado());
            case "zona comercial", "zonacomercial" -> {
                String valor = obtenerValorFiltroSeleccionado();
                if (valor == null || valor.isBlank()) {
                    yield new ArrayList<>();
                }
                try {
                    yield filtrarPorZonaComercial(Integer.parseInt(valor.trim()));
                } catch (NumberFormatException exception) {
                    yield new ArrayList<>();
                }
            }
            case "estado" -> filtrarPorEstado(obtenerValorEstadoSeleccionado());
            case "fecha" -> {
                if (fechaInicioDatePicker == null || fechaFinDatePicker == null || fechaInicioDatePicker.getValue() == null || fechaFinDatePicker.getValue() == null) {
                    yield new ArrayList<>();
                }
                yield filtrarPorFecha(
                        fechaInicioDatePicker.getValue().format(DATE_TIME_FORMATTER),
                        fechaFinDatePicker.getValue().format(DATE_TIME_FORMATTER));
            }
            default -> new ArrayList<>(pedidos);
        };
    }

    @FXML
    public void aplicarFiltroDesdeUI() {
        String tipoFiltro = tipoFiltroComboBox.getValue();
        if (tipoFiltro == null || tipoFiltro.isBlank()) {
            actualizarEstado("Selecciona un tipo de filtro.");
            return;
        }

        List<LineaPedido> resultado;
        switch (tipoFiltro) {
            case FILTRO_TODAS -> {
                mostrarTodosLosPedidos();
                actualizarEstado("Mostrando todos los pedidos.");
                return;
            }
            case FILTRO_CATEGORIA -> {
                String categoria = obtenerValorFiltroSeleccionado();
                if (categoria == null || categoria.isBlank() || OPCION_TODAS.equalsIgnoreCase(categoria.trim())) {
                    mostrarTodosLosPedidos();
                    actualizarEstado("Mostrando todos los pedidos.");
                    return;
                }
                if (categoria.isBlank()) {
                    actualizarEstado("Selecciona una categoría para filtrar.");
                    return;
                }
                resultado = filtrarPorCategoria(categoria.trim());
                actualizarEstado("Filtrado por categoría: " + categoria.trim() + " | Resultados: " + resultado.size());
            }
            case FILTRO_ZONA -> {
                String zonaTexto = obtenerValorFiltroSeleccionado();
                if (zonaTexto == null || zonaTexto.isBlank() || OPCION_TODAS.equalsIgnoreCase(zonaTexto.trim())) {
                    mostrarTodosLosPedidos();
                    actualizarEstado("Mostrando todos los pedidos.");
                    return;
                }
                if (zonaTexto.isBlank()) {
                    actualizarEstado("Selecciona una zona comercial para filtrar.");
                    return;
                }
                try {
                    int zona = Integer.parseInt(zonaTexto.trim());
                    resultado = filtrarPorZonaComercial(zona);
                    actualizarEstado("Filtrado por zona comercial: " + zona + " | Resultados: " + resultado.size());
                } catch (NumberFormatException exception) {
                    actualizarEstado("La zona comercial debe ser un número entero.");
                    return;
                }
            }
            case FILTRO_ESTADO -> {
                String estado = obtenerValorEstadoSeleccionado();
                if (estado == null || estado.isBlank() || OPCION_TODAS.equalsIgnoreCase(estado.trim())) {
                    mostrarTodosLosPedidos();
                    actualizarEstado("Mostrando todos los pedidos.");
                    return;
                }
                if (estado.isBlank()) {
                    actualizarEstado("Selecciona un estado para filtrar.");
                    return;
                }
                resultado = filtrarPorEstado(estado.trim());
                actualizarEstado("Filtrado por estado: " + estado.trim() + " | Resultados: " + resultado.size());
            }
            case FILTRO_FECHA -> {
                LocalDate inicio = fechaInicioDatePicker.getValue();
                LocalDate fin = fechaFinDatePicker.getValue();
                if (inicio == null || fin == null) {
                    actualizarEstado("Selecciona ambas fechas para filtrar.");
                    return;
                }
                resultado = filtrarPorFecha(inicio.format(DATE_TIME_FORMATTER), fin.format(DATE_TIME_FORMATTER));
                if (!validationErrors.isEmpty()) {
                    actualizarEstado(validationErrors.get(validationErrors.size() - 1));
                    return;
                }
                actualizarEstado("Filtrado por fecha: " + inicio.format(DATE_TIME_FORMATTER) + " a " + fin.format(DATE_TIME_FORMATTER) + " | Resultados: " + resultado.size());
            }
            default -> {
                actualizarEstado("Tipo de filtro no válido.");
                return;
            }
        }

        mostrarPedidos(resultado);
    }

    @FXML
    public void limpiarFiltros() {
        valorFiltroTextField.clear();
        if (valorFiltroComboBox != null) {
            valorFiltroComboBox.getSelectionModel().select(OPCION_TODAS);
        }
        fechaInicioDatePicker.setValue(null);
        fechaFinDatePicker.setValue(null);
        tipoFiltroComboBox.getSelectionModel().select(FILTRO_TODAS);
        actualizarControlesFiltro();
        mostrarTodosLosPedidos();
        actualizarEstado("Filtros limpiados. Mostrando todos los pedidos.");
    }

    private void refrescarOpcionesFiltros() {
        if (valorFiltroComboBox == null) {
            return;
        }

        String tipoFiltro = tipoFiltroComboBox != null ? tipoFiltroComboBox.getValue() : null;
        if (tipoFiltro == null || tipoFiltro.isBlank()) {
            valorFiltroComboBox.getItems().setAll(OPCION_TODAS);
            valorFiltroComboBox.getSelectionModel().select(OPCION_TODAS);
            valorFiltroComboBox.setPromptText("Selecciona una opción");
            return;
        }

        switch (tipoFiltro) {
            case FILTRO_TODAS -> {
                valorFiltroComboBox.getItems().setAll(OPCION_TODAS);
                valorFiltroComboBox.getSelectionModel().select(OPCION_TODAS);
                valorFiltroComboBox.setPromptText("Selecciona una opción");
            }
            case FILTRO_CATEGORIA -> {
                List<String> categorias = new ArrayList<>();
                categorias.add(OPCION_TODAS);
                categorias.addAll(obtenerCategoriasOrdenadas());
                valorFiltroComboBox.getItems().setAll(categorias);
                valorFiltroComboBox.getSelectionModel().select(OPCION_TODAS);
                valorFiltroComboBox.setPromptText("Selecciona la categoría");
            }
            case FILTRO_ZONA -> {
                List<String> zonas = new ArrayList<>();
                zonas.add(OPCION_TODAS);
                zonas.addAll(obtenerZonasOrdenadas());
                valorFiltroComboBox.getItems().setAll(zonas);
                valorFiltroComboBox.getSelectionModel().select(OPCION_TODAS);
                valorFiltroComboBox.setPromptText("Selecciona la zona comercial");
            }
            case FILTRO_ESTADO -> {
                valorFiltroComboBox.getItems().setAll(
                    OPCION_TODAS,
                    aCapitalCasePrimeraPalabra(EstadoPedido.COMPLETADO.getValor()),
                    aCapitalCasePrimeraPalabra(EstadoPedido.CANCELADO.getValor()),
                    aCapitalCasePrimeraPalabra(EstadoPedido.PENDIENTE.getValor())
                );
                valorFiltroComboBox.getSelectionModel().select(OPCION_TODAS);
                valorFiltroComboBox.setPromptText("Selecciona el estado");
            }
            default -> {
                valorFiltroComboBox.getItems().clear();
            }
        }

        restaurarFiltroComercialForzadoSiCorresponde();
    }

    private void restaurarFiltroComercialForzadoSiCorresponde() {
        if (!filtroComercialBloqueado || zonaComercialForzada == null || tipoFiltroComboBox == null || valorFiltroComboBox == null) {
            return;
        }

        tipoFiltroComboBox.getSelectionModel().select(FILTRO_ZONA);
        List<String> zonas = new ArrayList<>();
        zonas.add(OPCION_TODAS);
        zonas.addAll(obtenerZonasOrdenadas());
        valorFiltroComboBox.getItems().setAll(zonas);
        valorFiltroComboBox.getSelectionModel().select(String.valueOf(zonaComercialForzada));
        tipoFiltroComboBox.setDisable(true);
        valorFiltroComboBox.setDisable(true);
    }

    private List<String> obtenerCategoriasOrdenadas() {
        return obtenerCategoriasUnicas().stream()
                .map(this::aCapitalCasePrimeraPalabra)
                .sorted(String.CASE_INSENSITIVE_ORDER)
                .collect(Collectors.toList());
    }

    private List<String> obtenerZonasOrdenadas() {
        return obtenerZonasComerciales().stream()
                .sorted()
                .map(String::valueOf)
                .collect(Collectors.toList());
    }

    private String aCapitalCasePrimeraPalabra(String valor) {
        if (valor == null) {
            return null;
        }

        String texto = valor.trim();
        if (texto.isEmpty()) {
            return texto;
        }

        int primerSeparador = texto.indexOf(' ');
        String primeraPalabra = primerSeparador < 0 ? texto : texto.substring(0, primerSeparador);
        String resto = primerSeparador < 0 ? "" : texto.substring(primerSeparador);
        if (primeraPalabra.isEmpty()) {
            return texto;
        }

        String palabraFormateada = primeraPalabra.substring(0, 1).toUpperCase() + primeraPalabra.substring(1).toLowerCase();
        return palabraFormateada + resto;
    }

    private String obtenerValorFiltroSeleccionado() {
        if (valorFiltroComboBox != null && valorFiltroComboBox.getValue() != null) {
            return valorFiltroComboBox.getValue();
        }

        return valorFiltroTextField != null ? valorFiltroTextField.getText() : null;
    }

    private void mostrarTodosLosPedidos() {
        mostrarPedidos(pedidos);
    }

    private void mostrarPedidos(List<LineaPedido> pedidosAmostrar) {
        Runnable accion = () -> {
            pedidosTabla.setAll(new java.util.ArrayList<>(pedidosAmostrar));
            if (pedidosTableView != null) {
                pedidosTableView.setItems(FXCollections.observableArrayList(pedidosTabla));
                pedidosTableView.refresh();
            }
        };

        ejecutarEnHiloFxSiEsPosible(accion);
    }

    private void actualizarEstado(String mensaje) {
        Runnable accion = () -> {
            if (estadoLabel != null) {
                estadoLabel.setText(mensaje);
            }
        };

        ejecutarEnHiloFxSiEsPosible(accion);
    }

    private void ejecutarEnHiloFxSiEsPosible(Runnable accion) {
        try {
            if (Platform.isFxApplicationThread()) {
                accion.run();
            } else {
                Platform.runLater(accion);
            }
        } catch (IllegalStateException exception) {
            accion.run();
        }
    }

    private LocalDate parseFechaPedido(String textoFecha) {
        try {
            return LocalDate.parse(textoFecha, DateTimeFormatter.ISO_LOCAL_DATE);
        } catch (DateTimeParseException ignored) {
            return LocalDate.parse(textoFecha, DATE_TIME_FORMATTER);
        }
    }

    private String obtenerValorEstadoSeleccionado() {
        if (valorFiltroComboBox != null && valorFiltroComboBox.getValue() != null) {
            return valorFiltroComboBox.getValue();
        }

        return valorFiltroTextField != null ? valorFiltroTextField.getText() : null;
    }
}
