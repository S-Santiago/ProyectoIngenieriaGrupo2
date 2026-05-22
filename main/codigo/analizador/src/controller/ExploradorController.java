package controller;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
import model.LineaPedido;
import persistence.CsvImporter;
import view.ConsolaErroresDialog;

/**
 * Controlador para el explorador de pedidos.
 * Implementado como Singleton para asignarlo manualmente desde VistaManager.
 */
public class ExploradorController {

    private static final String FILTRO_CATEGORIA = "Categoría";
    private static final String FILTRO_ZONA = "Zona Comercial";
    private static final String FILTRO_ESTADO = "Estado";
    private static final String FILTRO_FECHA = "Fecha";

    private static ExploradorController instance = null;

    private final List<LineaPedido> pedidos = new ArrayList<>();
    private final ObservableList<LineaPedido> pedidosTabla = FXCollections.observableArrayList();
    private final List<String> validationErrors = new ArrayList<>();
    private final CsvImporter csvImporter = new CsvImporter();

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-uuuu")
            .withResolverStyle(ResolverStyle.STRICT);

    @FXML private ComboBox<String> tipoFiltroComboBox;
    @FXML private TextField valorFiltroTextField;
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

    @FXML
    public void initialize() {
        configurarColumnas();
        configurarControlesFiltro();
        cargarPedidosDesdeCsv();
        pedidosTableView.setItems(pedidosTabla);
        refrescarTabla();
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
                var recurso = getClass().getResource("/data/lineas_pedidos.csv");
                if (recurso == null) {
                    throw new IOException("No se encontró el CSV de pedidos en /data/lineas_pedidos.csv.");
                }
                return csvImporter.importCSVLineaPedidosConAvisos(Path.of(recurso.toURI()).toString());
            }
        };

        tareaCarga.setOnSucceeded(evt -> {
            CsvImporter.ImportResult<LineaPedido> resultadoImportacion = tareaCarga.getValue();
            List<LineaPedido> cargados = resultadoImportacion == null ? List.of() : resultadoImportacion.getElementos();

            if (resultadoImportacion != null && resultadoImportacion.tieneAvisos()) {
                ConsolaErroresDialog.mostrarAdvertencia(
                    "Líneas del CSV no importadas",
                    "Se cargaron " + cargados.size() + " líneas válidas, pero estas filas no se pudieron importar:\n\n"
                        + String.join("\n", resultadoImportacion.getAvisos())
                );
            }

            if (cargados == null || cargados.isEmpty()) {
                actualizarEstado("El CSV no devolvió pedidos válidos.");
            } else {
                pedidos.addAll(cargados);
                actualizarEstado("Pedidos cargados desde CSV: " + pedidos.size());
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
        tipoFiltroComboBox.getItems().setAll(FILTRO_CATEGORIA, FILTRO_ZONA, FILTRO_ESTADO, FILTRO_FECHA);
        tipoFiltroComboBox.getSelectionModel().select(FILTRO_CATEGORIA);
        tipoFiltroComboBox.valueProperty().addListener((observable, valorAnterior, valorNuevo) -> actualizarControlesFiltro());

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
        boolean usaTexto = !FILTRO_FECHA.equals(tipoFiltro);

        valorFiltroLabel.setVisible(usaTexto);
        valorFiltroLabel.setManaged(usaTexto);
        valorFiltroTextField.setVisible(usaTexto);
        valorFiltroTextField.setManaged(usaTexto);

        fechaInicioLabel.setVisible(!usaTexto);
        fechaInicioLabel.setManaged(!usaTexto);
        fechaInicioDatePicker.setVisible(!usaTexto);
        fechaInicioDatePicker.setManaged(!usaTexto);
        fechaFinLabel.setVisible(!usaTexto);
        fechaFinLabel.setManaged(!usaTexto);
        fechaFinDatePicker.setVisible(!usaTexto);
        fechaFinDatePicker.setManaged(!usaTexto);

        switch (tipoFiltro) {
            case FILTRO_CATEGORIA -> {
                valorFiltroLabel.setText("Categoría");
                valorFiltroTextField.setPromptText("Escribe la categoría");
            }
            case FILTRO_ZONA -> {
                valorFiltroLabel.setText("Zona comercial");
                valorFiltroTextField.setPromptText("Introduce el ID de la zona");
            }
            case FILTRO_ESTADO -> {
                valorFiltroLabel.setText("Estado");
                valorFiltroTextField.setPromptText("Escribe el estado");
            }
            case FILTRO_FECHA -> {
                valorFiltroLabel.setText("Valor");
            }
            default -> {
                valorFiltroLabel.setText("Categoría");
                valorFiltroTextField.setPromptText("Escribe la categoría");
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

        if (nuevosPedidos == null || nuevosPedidos.isEmpty()) {
            actualizarEstado("No hay pedidos para mostrar.");
            mostrarTodosLosPedidos();
            return;
        }

        for (LineaPedido pedido : nuevosPedidos) {
            if (pedido == null) {
                validationErrors.add("Se ha encontrado un pedido nulo.");
                continue;
            }

            if (validarPedido(pedido)) {
                pedidos.add(pedido);
            }
        }

        mostrarTodosLosPedidos();

        if (!validationErrors.isEmpty()) {
            actualizarEstado("Pedidos cargados: " + pedidos.size() + " | Avisos: " + validationErrors.size());
        } else {
            actualizarEstado("Pedidos cargados: " + pedidos.size());
        }

        refrescarTabla();
    }

    private boolean validarPedido(LineaPedido pedido) {
        boolean valido = true;

        if (pedido.getUnidades() == null || pedido.getUnidades() < 0) {
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
            pedidosTabla.setAll(pedidos);
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
            case "categoría", "categoria" -> filtrarPorCategoria(valorFiltroTextField != null ? valorFiltroTextField.getText() : null);
            case "zona comercial", "zonacomercial" -> {
                String valor = valorFiltroTextField != null ? valorFiltroTextField.getText() : null;
                if (valor == null || valor.isBlank()) {
                    yield new ArrayList<>();
                }
                try {
                    yield filtrarPorZonaComercial(Integer.parseInt(valor.trim()));
                } catch (NumberFormatException exception) {
                    yield new ArrayList<>();
                }
            }
            case "estado" -> filtrarPorEstado(valorFiltroTextField != null ? valorFiltroTextField.getText() : null);
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
            case FILTRO_CATEGORIA -> {
                String categoria = valorFiltroTextField.getText();
                if (categoria == null || categoria.isBlank()) {
                    actualizarEstado("Escribe una categoría para filtrar.");
                    return;
                }
                resultado = filtrarPorCategoria(categoria.trim());
                actualizarEstado("Filtrado por categoría: " + categoria.trim() + " | Resultados: " + resultado.size());
            }
            case FILTRO_ZONA -> {
                String zonaTexto = valorFiltroTextField.getText();
                if (zonaTexto == null || zonaTexto.isBlank()) {
                    actualizarEstado("Escribe un ID de zona para filtrar.");
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
                String estado = valorFiltroTextField.getText();
                if (estado == null || estado.isBlank()) {
                    actualizarEstado("Escribe un estado para filtrar.");
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
        fechaInicioDatePicker.setValue(null);
        fechaFinDatePicker.setValue(null);
        tipoFiltroComboBox.getSelectionModel().select(FILTRO_CATEGORIA);
        actualizarControlesFiltro();
        mostrarTodosLosPedidos();
        actualizarEstado("Filtros limpiados. Mostrando todos los pedidos.");
    }

    private void mostrarTodosLosPedidos() {
        mostrarPedidos(pedidos);
    }

    private void mostrarPedidos(List<LineaPedido> pedidosAmostrar) {
        Runnable accion = () -> {
            pedidosTabla.setAll(pedidosAmostrar);
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
}
