package cli;

import java.math.BigDecimal;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import controller.CalculadoraFinanciera;
import controller.ExploradorController;
import controller.ImportKpiController;
import model.LineaPedido;
import persistence.CsvImporter;
import persistence.ExcelExporter;

public class CliEngine {

    private static final String DEFAULT_CSV_RESOURCE = "/data/lineas_pedidos.csv";
    private static final int MAX_FILAS_POR_PANTALLA = 20;

    private final Scanner scanner;
    private final ExploradorController exploradorController = ExploradorController.getInstance();
    private final ImportKpiController importKpiController = new ImportKpiController();
    private final CalculadoraFinanciera calculadoraFinanciera = new CalculadoraFinanciera();
    private final ExcelExporter excelExporter = new ExcelExporter();
    private final CsvImporter csvImporter = new CsvImporter();
    private List<LineaPedido> ultimoResultado = new ArrayList<>();

    public CliEngine(Scanner scanner) {
        this.scanner = scanner;
        this.ultimoResultado = exploradorController.getPedidos();
    }

    public void run() {
        boolean salir = false;

        while (!salir) {
            mostrarMenuPrincipal();
            String opcion = leerTexto("Elige una opcion");

            switch (opcion) {
                case "1" -> cargarPedidosDesdeCsv();
                case "2" -> explorarYFiltrarPedidos();
                case "3" -> mostrarAnalisisRentabilidadYKpis();
                case "4" -> exportarResultadosAExcel();
                case "5" -> {
                    System.out.println("Saliendo del programa.");
                    salir = true;
                }
                default -> System.out.println("Opcion no valida. Selecciona una opcion del 1 al 5.");
            }
        }
    }

    private void mostrarMenuPrincipal() {
        System.out.println();
        System.out.println("==============================");
        System.out.println("          MENU CLI             ");
        System.out.println("==============================");
        System.out.println("1. Cargar datos desde archivo CSV");
        System.out.println("2. Explorar y filtrar pedidos");
        System.out.println("3. Ver analisis de rentabilidad y KPIs financieros");
        System.out.println("4. Exportar resultados a Excel");
        System.out.println("5. Salir del programa");
    }

    private void cargarPedidosDesdeCsv() {
        String ruta = leerTexto("Ruta del CSV (Enter para usar el CSV por defecto)");
        String rutaFinal = ruta.isBlank() ? resolverRutaCsvPorDefecto() : ruta;

        if (rutaFinal == null || rutaFinal.isBlank()) {
            System.out.println("No se pudo resolver el CSV por defecto del proyecto.");
            return;
        }

        if (!Files.exists(Paths.get(rutaFinal))) {
            System.out.println("No existe el archivo CSV indicado: " + rutaFinal);
            return;
        }

        List<LineaPedido> lineasImportadas = csvImporter.importCSVLineaPedidos(rutaFinal);
        exploradorController.setPedidos(lineasImportadas);
        ultimoResultado = exploradorController.getPedidos();

        System.out.println("Pedidos cargados correctamente: " + ultimoResultado.size());
        if (!exploradorController.getValidationErrors().isEmpty()) {
            System.out.println("Se detectaron avisos al validar los pedidos importados:");
            for (String aviso : exploradorController.getValidationErrors()) {
                System.out.println("- " + aviso);
            }
        }
    }

    private void explorarYFiltrarPedidos() {
        if (exploradorController.getPedidos().isEmpty()) {
            System.out.println("Primero debes cargar pedidos desde un CSV.");
            return;
        }

        boolean volver = false;
        while (!volver) {
            System.out.println();
            System.out.println("------ EXPLORAR Y FILTRAR ------");
            System.out.println("1. Filtrar por categoria");
            System.out.println("2. Filtrar por zona comercial");
            System.out.println("3. Filtrar por estado");
            System.out.println("4. Filtrar por rango de fechas");
            System.out.println("5. Ver pedidos cargados");
            System.out.println("6. Volver al menu principal");

            String opcion = leerTexto("Elige una opcion");
            switch (opcion) {
                case "1" -> filtrarPorCategoria();
                case "2" -> filtrarPorZonaComercial();
                case "3" -> filtrarPorEstado();
                case "4" -> filtrarPorRangoDeFechas();
                case "5" -> mostrarPedidos("Pedidos cargados", exploradorController.getPedidos());
                case "6" -> volver = true;
                default -> System.out.println("Opcion no valida.");
            }
        }
    }

    private void filtrarPorCategoria() {
        System.out.println("Categorias disponibles: " + exploradorController.obtenerCategoriasUnicas());
        String categoria = leerTexto("Categoria a filtrar");
        List<LineaPedido> resultado = exploradorController.filtrarPorCategoria(categoria);
        mostrarPedidos("Resultados filtrados por categoria: " + categoria, resultado);
    }

    private void filtrarPorZonaComercial() {
        System.out.println("Zonas comerciales disponibles: " + exploradorController.obtenerZonasComerciales());
        String zonaTexto = leerTexto("ID de zona comercial");

        try {
            int zona = Integer.parseInt(zonaTexto.trim());
            List<LineaPedido> resultado = exploradorController.filtrarPorZonaComercial(zona);
            mostrarPedidos("Resultados filtrados por zona comercial: " + zona, resultado);
        } catch (NumberFormatException exception) {
            System.out.println("La zona comercial debe ser un numero entero.");
        }
    }

    private void filtrarPorEstado() {
        System.out.println("Estados disponibles: " + exploradorController.obtenerEstadosUnicos());
        String estado = leerTexto("Estado a filtrar");
        List<LineaPedido> resultado = exploradorController.filtrarPorEstado(estado);
        mostrarPedidos("Resultados filtrados por estado: " + estado, resultado);
    }

    private void filtrarPorRangoDeFechas() {
        String fechaInicio = leerTexto("Fecha de inicio (dd-MM-uuuu)");
        String fechaFin = leerTexto("Fecha de fin (dd-MM-uuuu)");

        exploradorController.clearValidationErrors();
        List<LineaPedido> resultado = exploradorController.filtrarPorFecha(fechaInicio, fechaFin);

        List<String> errores = exploradorController.getValidationErrors();
        if (!errores.isEmpty()) {
            System.out.println(errores.get(errores.size() - 1));
            exploradorController.clearValidationErrors();
            return;
        }

        mostrarPedidos("Resultados filtrados por rango de fechas: " + fechaInicio + " a " + fechaFin, resultado);
    }

    private void mostrarAnalisisRentabilidadYKpis() {
        if (exploradorController.getPedidos().isEmpty()) {
            System.out.println("Primero debes cargar pedidos desde un CSV.");
            return;
        }

        boolean volver = false;
        while (!volver) {
            System.out.println();
            System.out.println("------ RENTABILIDAD Y KPIS ------");
            System.out.println("1. Margen bruto total");
            System.out.println("2. Ranking de categorias por margen bruto");
            System.out.println("3. Ranking de categorias por facturacion");
            System.out.println("4. KPI mensual de facturacion");
            System.out.println("5. KPI mensual de margen");
            System.out.println("6. Volver al menu principal");

            String opcion = leerTexto("Elige una opcion");
            switch (opcion) {
                case "1" -> {
                    BigDecimal margenBrutoTotal = calculadoraFinanciera.calcularMargenBrutoTotal();
                    System.out.println("Margen bruto total: " + formatearImporte(margenBrutoTotal));
                }
                case "2" -> mostrarMapa("Ranking de categorias por margen bruto", calculadoraFinanciera.generarRankCategorias());
                case "3" -> mostrarMapa("Ranking de categorias por facturacion", calculadoraFinanciera.generarRankCategoriasPorFacturacion());
                case "4" -> mostrarKpiMensual(true);
                case "5" -> mostrarKpiMensual(false);
                case "6" -> volver = true;
                default -> System.out.println("Opcion no valida.");
            }
        }
    }

    private void mostrarKpiMensual(boolean facturacion) {
        String categoria = leerTexto("Categoria para el KPI mensual (Enter para usar todos los pedidos)");
        String opcion = categoria.isBlank() ? null : categoria;

        Map<String, BigDecimal> kpi = facturacion
                ? importKpiController.calcularKPIMensualFacturacion(opcion)
                : importKpiController.calcularKPIMensualMargen(opcion);

        String titulo = facturacion ? "KPI mensual de facturacion" : "KPI mensual de margen";
        if (opcion != null) {
            titulo += " para categoria: " + opcion;
        }

        mostrarMapa(titulo, kpi);
    }

    private void exportarResultadosAExcel() {
        if (ultimoResultado.isEmpty()) {
            System.out.println("No hay pedidos para exportar.");
            return;
        }

        String rutaSalida = leerTexto("Ruta de salida Excel (Enter para exportacion_cli.xlsx)");
        String rutaFinal = rutaSalida.isBlank() ? "exportacion_cli.xlsx" : rutaSalida;

        excelExporter.exportLineasPedido(rutaFinal, ultimoResultado);
        System.out.println("Exportacion completada en: " + rutaFinal);
    }

    private void mostrarPedidos(String titulo, List<LineaPedido> pedidos) {
        List<LineaPedido> pedidosSeguro = pedidos == null ? new ArrayList<>() : pedidos;
        ultimoResultado = new ArrayList<>(pedidosSeguro);

        System.out.println();
        System.out.println(titulo);
        System.out.println("Total de pedidos: " + pedidosSeguro.size());

        if (pedidosSeguro.isEmpty()) {
            System.out.println("No hay resultados para mostrar.");
            return;
        }

        int limite = Math.min(pedidosSeguro.size(), MAX_FILAS_POR_PANTALLA);
        for (int i = 0; i < limite; i++) {
            System.out.println(formatearPedido(pedidosSeguro.get(i)));
        }

        if (pedidosSeguro.size() > limite) {
            System.out.println("... se muestran solo los primeros " + limite + " registros.");
        }
    }

    private void mostrarMapa(String titulo, Map<String, BigDecimal> valores) {
        System.out.println();
        System.out.println(titulo);

        if (valores == null || valores.isEmpty()) {
            System.out.println("No hay datos para mostrar.");
            return;
        }

        for (Map.Entry<String, BigDecimal> entrada : valores.entrySet()) {
            System.out.println(entrada.getKey() + " -> " + formatearImporte(entrada.getValue()));
        }
    }

    private String formatearPedido(LineaPedido pedido) {
        if (pedido == null) {
            return "Pedido nulo";
        }

        return String.format(
                "IDLinea=%s | IDPedido=%s | Referencia=%s | Categoria=%s | Zona=%s | Estado=%s | Unidades=%s | Coste=%s | Precio=%s | Fecha=%s",
                valorSeguro(pedido.getIdLinea()),
                valorSeguro(pedido.getIdPedido()),
                valorSeguro(pedido.getReferenciaProduto()),
                valorSeguro(pedido.getCategoria()),
                valorSeguro(pedido.getZonaComercial()),
                valorSeguro(pedido.getEstado()),
                valorSeguro(pedido.getUnidades()),
                formatearImporte(pedido.getCosteUnitario()),
                formatearImporte(pedido.getPrecioVentaUnitario()),
                valorSeguro(pedido.getFechaPedido()));
    }

    private String valorSeguro(Object valor) {
        return valor == null ? "-" : String.valueOf(valor);
    }

    private String formatearImporte(BigDecimal valor) {
        return valor == null ? "0" : valor.stripTrailingZeros().toPlainString();
    }

    private String leerTexto(String mensaje) {
        System.out.print(mensaje + ": ");
        return scanner.nextLine().trim();
    }

    private String resolverRutaCsvPorDefecto() {
        URL recurso = getClass().getResource(DEFAULT_CSV_RESOURCE);
        if (recurso == null) {
            return null;
        }

        try {
            Path ruta = Paths.get(recurso.toURI());
            return ruta.toString();
        } catch (URISyntaxException exception) {
            return null;
        }
    }
}
