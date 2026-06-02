package cli;

import java.math.BigDecimal;
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
import controller.RentabilidadController;
import model.LineaPedido;
import model.ReglaMargen;
import model.ZonaComercial;
import persistence.CsvImporter;
import persistence.ExcelExporter;
import view.ConsolaErroresDialog;

public class CliEngine {

    private static final Path DEFAULT_CSV_FILE = Paths.get("src", "resources", "data", "lineas_pedidos.csv");
    private static final int MAX_FILAS_POR_PANTALLA = 20;

    private final Scanner scanner;
    private final ExploradorController exploradorController = ExploradorController.getInstance();
    private final ImportKpiController importKpiController = new ImportKpiController();
    private final RentabilidadController rentabilidadController = new RentabilidadController();
    private final CalculadoraFinanciera calculadoraFinanciera = new CalculadoraFinanciera();
    private final ExcelExporter excelExporter = new ExcelExporter();
    private final CsvImporter csvImporter = new CsvImporter();
    private List<LineaPedido> ultimoResultado = new ArrayList<>();

    public CliEngine(Scanner scanner) {
        this.scanner = scanner;
        rentabilidadController.cargarDatos();
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
                case "3" -> gestionarZonasYReglas();
                case "4" -> mostrarAnalisisRentabilidadYKpis();
                case "5" -> exportarResultadosAExcel();
                case "6" -> {
                    System.out.println("Saliendo del programa.");
                    salir = true;
                }
                default -> System.out.println("Opcion no valida. Selecciona una opcion del 1 al 6.");
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
        System.out.println("3. Gestionar zonas comerciales y reglas de margen");
        System.out.println("4. Ver analisis de rentabilidad y KPIs financieros");
        System.out.println("5. Exportar resultados a Excel");
        System.out.println("6. Salir del programa");
    }

    private void cargarPedidosDesdeCsv() {
        String ruta = leerTexto("Ruta del CSV (Enter para usar el CSV por defecto)");
        if (ruta.isBlank()) {
            if (Files.exists(DEFAULT_CSV_FILE)) {
                var resultado = csvImporter.importCSVLineaPedidosConAvisos(DEFAULT_CSV_FILE.toString());
                List<LineaPedido> lineasImportadas = resultado.getElementos();
                exploradorController.setPedidos(lineasImportadas);
                ConsolaErroresDialog.reiniciarNoVolverAMostrarAnalisis();
                ultimoResultado = exploradorController.getPedidos();

                System.out.println("Pedidos cargados correctamente: " + ultimoResultado.size());
                if (!exploradorController.getValidationErrors().isEmpty()) {
                    System.out.println("Se detectaron avisos al validar los pedidos importados:");
                    for (String aviso : exploradorController.getValidationErrors()) {
                        System.out.println("- " + aviso);
                    }
                }
                return;
            }

            try (var recurso = getClass().getResourceAsStream("/data/lineas_pedidos.csv")) {
                if (recurso == null) {
                    System.out.println("No se encontró el CSV por defecto en src/resources/data/lineas_pedidos.csv.");
                    return;
                }
                var resultado = csvImporter.importCSVLineaPedidosConAvisos(new java.io.InputStreamReader(recurso, java.nio.charset.StandardCharsets.UTF_8));
                List<LineaPedido> lineasImportadas = resultado.getElementos();
                exploradorController.setPedidos(lineasImportadas);
                ConsolaErroresDialog.reiniciarNoVolverAMostrarAnalisis();
                ultimoResultado = exploradorController.getPedidos();

                System.out.println("Pedidos cargados correctamente: " + ultimoResultado.size());
                if (!exploradorController.getValidationErrors().isEmpty()) {
                    System.out.println("Se detectaron avisos al validar los pedidos importados:");
                    for (String aviso : exploradorController.getValidationErrors()) {
                        System.out.println("- " + aviso);
                    }
                }
                return;
            } catch (Exception e) {
                System.out.println("Error al cargar CSV desde archivo por defecto: " + e.getMessage());
                return;
            }
        }

        String rutaFinal = ruta;

        if (!Files.exists(Paths.get(rutaFinal))) {
            System.out.println("No existe el archivo CSV indicado: " + rutaFinal);
            return;
        }

        List<LineaPedido> lineasImportadas = csvImporter.importCSVLineaPedidos(rutaFinal);
        exploradorController.setPedidos(lineasImportadas);
        ConsolaErroresDialog.reiniciarNoVolverAMostrarAnalisis();
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

    private void gestionarZonasYReglas() {
        boolean volver = false;
        while (!volver) {
            System.out.println();
            System.out.println("------ GESTION COMERCIAL ------");
            System.out.println("1. Ver zonas comerciales");
            System.out.println("2. Crear zona comercial");
            System.out.println("3. Actualizar zona comercial");
            System.out.println("4. Eliminar zona comercial");
            System.out.println("5. Ver reglas de margen");
            System.out.println("6. Crear regla de margen");
            System.out.println("7. Actualizar regla de margen");
            System.out.println("8. Eliminar regla de margen");
            System.out.println("9. Volver al menu principal");

            String opcion = leerTexto("Elige una opcion");
            switch (opcion) {
                case "1" -> mostrarZonas();
                case "2" -> crearZona();
                case "3" -> actualizarZona();
                case "4" -> eliminarZona();
                case "5" -> mostrarReglas();
                case "6" -> crearRegla();
                case "7" -> actualizarRegla();
                case "8" -> eliminarRegla();
                case "9" -> volver = true;
                default -> System.out.println("Opcion no valida.");
            }
        }
    }

    private void mostrarZonas() {
        List<ZonaComercial> zonas = rentabilidadController.readAllZones();
        System.out.println();
        System.out.println("ZONAS COMERCIALES");
        if (zonas.isEmpty()) {
            System.out.println("No hay zonas comerciales registradas.");
            return;
        }
        for (ZonaComercial zona : zonas) {
            System.out.println(formatearZona(zona));
        }
    }

    private void crearZona() {
        try {
            Integer id = leerEnteroOpcional("ID de zona (Enter para autogenerar)", siguienteIdZona());
            String nombre = leerTexto("Nombre de la zona");
            String pais = leerTexto("Pais");
            String responsable = leerTexto("Responsable comercial");
            double objetivo = leerDouble("Objetivo de facturacion anual");

            rentabilidadController.addZona(new ZonaComercial(id, nombre, pais, responsable, objetivo));
            System.out.println("Zona comercial guardada correctamente.");
        } catch (Exception e) {
            System.out.println("No se pudo crear la zona: " + e.getMessage());
        }
    }

    private void actualizarZona() {
        Integer id = leerEnteroObligatorio("ID de la zona a actualizar");
        ZonaComercial zona = buscarZona(id);
        if (zona == null) {
            System.out.println("No se encontró la zona indicada.");
            return;
        }

        String nombre = leerTextoConDefault("Nombre", zona.getNombre());
        String pais = leerTextoConDefault("Pais", zona.getPais());
        String responsable = leerTextoConDefault("Responsable comercial", zona.getResponsableComercial());
        double objetivo = leerDoubleConDefault("Objetivo de facturacion anual", zona.getObjetivoFacturacionAnual());

        rentabilidadController.updateZona(id.toString(), nombre, pais, responsable, objetivo);
    }

    private void eliminarZona() {
        Integer id = leerEnteroObligatorio("ID de la zona a eliminar");
        rentabilidadController.deleteZona(id.toString());
    }

    private void mostrarReglas() {
        List<ReglaMargen> reglas = rentabilidadController.readAllMarginRules();
        System.out.println();
        System.out.println("REGLAS DE MARGEN");
        if (reglas.isEmpty()) {
            System.out.println("No hay reglas de margen registradas.");
            return;
        }
        for (ReglaMargen regla : reglas) {
            System.out.println(formatearRegla(regla));
        }
    }

    private void crearRegla() {
        try {
            Integer id = leerEnteroOpcional("ID de regla (Enter para autogenerar)", siguienteIdRegla());
            String categoria = leerTexto("Categoria afectada");
            double margen = leerDouble("Margen minimo porcentual");
            boolean activa = leerBooleano("¿Regla activa? (s/n)");
            String descripcion = leerTexto("Descripcion");

            rentabilidadController.addMarginRule(new ReglaMargen(id, categoria, margen, activa, descripcion));
            System.out.println("Regla de margen guardada correctamente.");
        } catch (Exception e) {
            System.out.println("No se pudo crear la regla: " + e.getMessage());
        }
    }

    private void actualizarRegla() {
        Integer id = leerEnteroObligatorio("ID de la regla a actualizar");
        ReglaMargen regla = buscarRegla(id);
        if (regla == null) {
            System.out.println("No se encontró la regla indicada.");
            return;
        }

        String categoria = leerTextoConDefault("Categoria afectada", regla.getCategoriaProductoAfectada());
        double margen = leerDoubleConDefault("Margen minimo porcentual", regla.getMargenMinimoPortcentaje());
        boolean activa = leerBooleanoConDefault("¿Regla activa? (s/n)", regla.isActiva());
        String descripcion = leerTextoConDefault("Descripcion", regla.getDescripcion());

        rentabilidadController.updateReglaMargen(id.toString(), categoria, margen, activa, descripcion);
    }

    private void eliminarRegla() {
        Integer id = leerEnteroObligatorio("ID de la regla a eliminar");
        rentabilidadController.deleteReglaMargen(id.toString());
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

    private String formatearZona(ZonaComercial zona) {
        return String.format(
                "ID=%s | Nombre=%s | Pais=%s | Responsable=%s | Objetivo=%s",
                valorSeguro(zona.getId()),
                valorSeguro(zona.getNombre()),
                valorSeguro(zona.getPais()),
                valorSeguro(zona.getResponsableComercial()),
                formatearImporte(BigDecimal.valueOf(zona.getObjetivoFacturacionAnual())));
    }

    private String formatearRegla(ReglaMargen regla) {
        return String.format(
                "ID=%s | Categoria=%s | Margen=%s | Activa=%s | Descripcion=%s",
                valorSeguro(regla.getId()),
                valorSeguro(regla.getCategoriaProductoAfectada()),
                formatearImporte(BigDecimal.valueOf(regla.getMargenMinimoPortcentaje())),
                valorSeguro(regla.isActiva()),
                valorSeguro(regla.getDescripcion()));
    }

    private String valorSeguro(Object valor) {
        return valor == null ? "-" : String.valueOf(valor);
    }

    private Integer siguienteIdZona() {
        return rentabilidadController.readAllZones().stream()
                .map(ZonaComercial::getId)
                .filter(valor -> valor != null)
                .max(Integer::compareTo)
                .map(valor -> valor + 1)
                .orElse(1);
    }

    private Integer siguienteIdRegla() {
        return rentabilidadController.readAllMarginRules().stream()
                .map(ReglaMargen::getId)
                .filter(valor -> valor != null)
                .max(Integer::compareTo)
                .map(valor -> valor + 1)
                .orElse(1);
    }

    private ZonaComercial buscarZona(Integer id) {
        for (ZonaComercial zona : rentabilidadController.readAllZones()) {
            if (zona.getId() != null && zona.getId().equals(id)) {
                return zona;
            }
        }
        return null;
    }

    private ReglaMargen buscarRegla(Integer id) {
        for (ReglaMargen regla : rentabilidadController.readAllMarginRules()) {
            if (regla.getId() != null && regla.getId().equals(id)) {
                return regla;
            }
        }
        return null;
    }

    private Integer leerEnteroObligatorio(String mensaje) {
        while (true) {
            try {
                return Integer.valueOf(leerTexto(mensaje));
            } catch (NumberFormatException e) {
                System.out.println("Debes introducir un numero entero valido.");
            }
        }
    }

    private Integer leerEnteroOpcional(String mensaje, Integer valorPorDefecto) {
        String texto = leerTexto(mensaje);
        if (texto.isBlank()) {
            return valorPorDefecto;
        }
        return Integer.valueOf(texto);
    }

    private double leerDouble(String mensaje) {
        while (true) {
            try {
                return Double.parseDouble(leerTexto(mensaje));
            } catch (NumberFormatException e) {
                System.out.println("Debes introducir un numero decimal valido.");
            }
        }
    }

    private double leerDoubleConDefault(String mensaje, double valorPorDefecto) {
        String texto = leerTextoConDefault(mensaje, String.valueOf(valorPorDefecto));
        try {
            return Double.parseDouble(texto);
        } catch (NumberFormatException e) {
            return valorPorDefecto;
        }
    }

    private boolean leerBooleano(String mensaje) {
        while (true) {
            String texto = leerTexto(mensaje).toLowerCase();
            if (texto.equals("s") || texto.equals("si") || texto.equals("sí")) {
                return true;
            }
            if (texto.equals("n") || texto.equals("no")) {
                return false;
            }
            System.out.println("Responde 's' o 'n'.");
        }
    }

    private boolean leerBooleanoConDefault(String mensaje, boolean valorPorDefecto) {
        String texto = leerTextoConDefault(mensaje, valorPorDefecto ? "s" : "n").toLowerCase();
        if (texto.isBlank()) {
            return valorPorDefecto;
        }
        return texto.startsWith("s");
    }

    private String leerTextoConDefault(String mensaje, String valorPorDefecto) {
        System.out.print(mensaje + " [" + valorPorDefecto + "]: ");
        String texto = scanner.nextLine().trim();
        return texto.isBlank() ? valorPorDefecto : texto;
    }

    private String formatearImporte(BigDecimal valor) {
        return valor == null ? "0" : valor.stripTrailingZeros().toPlainString();
    }

    private String leerTexto(String mensaje) {
        System.out.print(mensaje + ": ");
        return scanner.nextLine().trim();
    }
}
