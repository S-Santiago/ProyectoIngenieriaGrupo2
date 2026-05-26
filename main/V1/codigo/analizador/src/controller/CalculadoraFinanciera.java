package controller;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import model.LineaPedido;
import model.ReglaMargen;
import model.ZonaComercial;
import persistence.JsonRepositoryReglaMargen;
import persistence.JsonRepositoryZonaComercial;


public class CalculadoraFinanciera {
    // Singleton para compartir la misma instancia de ExploradorController
    private final ExploradorController exploradorController = ExploradorController.getInstance();
    private final Map<Integer, BigDecimal> margenBrutoPedidos = new HashMap<>();
    private final Map<Integer, BigDecimal> porcentajeMargenBrutoPedidos = new HashMap<>();
    
    // Método para ordenar un mapa por valor en orden descendente
    public Map<String, BigDecimal> ordenMap(Map<String, BigDecimal> dMap) {
        Set<Map.Entry<String, BigDecimal>> mapParaOrdenar = dMap.entrySet();
        List<Map.Entry<String, BigDecimal>> listaParaOrdenar = new ArrayList<>(mapParaOrdenar);
        listaParaOrdenar.sort((a, b) -> b.getValue().compareTo(a.getValue()));
        Map<String, BigDecimal> resultado = new LinkedHashMap<>();
        for (Map.Entry<String, BigDecimal> d : listaParaOrdenar) {
            resultado.put(d.getKey(), d.getValue());
        }
        return resultado;
    }
    
    // Método para calcular el margen bruto total de todos los pedidos
    public BigDecimal calcularMargenBrutoTotal() {
        BigDecimal margenBrutoTotal = BigDecimal.ZERO;
        List<LineaPedido> pedidos = obtenerPedidosParaSesion();
        margenBrutoPedidos.clear();
        porcentajeMargenBrutoPedidos.clear();
        if (pedidos == null || pedidos.isEmpty()) {
            return BigDecimal.ZERO;
        }

        for (LineaPedido pedido : pedidos) {
            BigDecimal costeTotal = multiplicarMonetario(pedido.getCosteUnitario(), pedido.getUnidades());
            BigDecimal precioVentaTotal = multiplicarMonetario(pedido.getPrecioVentaUnitario(), pedido.getUnidades());
            BigDecimal margenBrutoPedido = precioVentaTotal.subtract(costeTotal);
            margenBrutoTotal = margenBrutoTotal.add(margenBrutoPedido);
            if (pedido.getIdLinea() != null) {
                margenBrutoPedidos.put(pedido.getIdLinea(), margenBrutoPedido);
                porcentajeMargenBrutoPedidos.put(pedido.getIdLinea(), calcularPorcentajeSeguro(margenBrutoPedido, precioVentaTotal));
            }
        }
        return margenBrutoTotal;
    }
    
    // Ranking por Margen (Margen Bruto)
    public Map<String, BigDecimal> generarRankCategorias() {
        Map<String, BigDecimal> ranking = new HashMap<>();
        List<LineaPedido> pedidos = obtenerPedidosParaSesion();
        if (pedidos == null || pedidos.isEmpty()) {
            return new LinkedHashMap<>();
        }

        for (LineaPedido p : pedidos) {
            String categoria = normalizarCategoria(p.getCategoria());
            BigDecimal costeTotal = multiplicarMonetario(p.getCosteUnitario(), p.getUnidades());
            BigDecimal precioVentaTotal = multiplicarMonetario(p.getPrecioVentaUnitario(), p.getUnidades());
            BigDecimal margenBrutoPedido = precioVentaTotal.subtract(costeTotal);
            ranking.merge(categoria, margenBrutoPedido, BigDecimal::add);
        }
        return ordenMap(ranking);
    }
    
    // Ranking por Factuación (no cuenta coste)
    public Map<String, BigDecimal> generarRankCategoriasPorFacturacion() {
        Map<String, BigDecimal> ranking = new HashMap<>();
        List<LineaPedido> pedidos = obtenerPedidosParaSesion();
        if (pedidos == null || pedidos.isEmpty()) {
            return new LinkedHashMap<>();
        }

        for (LineaPedido p : pedidos) {
            String categoria = normalizarCategoria(p.getCategoria());
            BigDecimal precioVentaTotal = multiplicarMonetario(p.getPrecioVentaUnitario(), p.getUnidades());
            ranking.merge(categoria, precioVentaTotal, BigDecimal::add);
        }
        return ordenMap(ranking);
}

    private BigDecimal multiplicarMonetario(BigDecimal valorUnitario, Integer unidades) {
        BigDecimal precio = valorUnitario == null ? BigDecimal.ZERO : valorUnitario;
        BigDecimal cantidad = unidades == null ? BigDecimal.ZERO : BigDecimal.valueOf(unidades.longValue());
        return precio.multiply(cantidad);
    }

    private BigDecimal calcularPorcentajeSeguro(BigDecimal numerador, BigDecimal denominador) {
        if (denominador == null || denominador.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        return numerador
                .multiply(BigDecimal.valueOf(100))
                .divide(denominador, 4, RoundingMode.HALF_UP);
    }

    private String normalizarCategoria(String categoria) {
        if (categoria == null || categoria.trim().isEmpty()) {
            return "(sin categoría)";
        }
        return categoria.trim();
    }

    // ==================== NUEVOS MÉTODOS DE NEGOCIO ====================

    /**
     * Detecta todas las líneas de pedido cuyo margen porcentual está por debajo
     * del margen mínimo establecido en su regla de categoría activa.
     * 
     * @return Lista de líneas que incumplen el margen mínimo (con detalles de incidencia)
     */
    public List<Map<String, Object>> detectarLineasBajoMargen() {
        List<Map<String, Object>> incidencias = new ArrayList<>();
        List<LineaPedido> pedidos = obtenerPedidosParaSesion();
        JsonRepositoryReglaMargen repoReglas = new JsonRepositoryReglaMargen();
        
        if (pedidos == null || pedidos.isEmpty()) {
            return incidencias;
        }

        List<ReglaMargen> reglasActivas = repoReglas.findAll().stream()
            .filter(ReglaMargen::isActiva)
            .collect(Collectors.toList());

        for (LineaPedido linea : pedidos) {
            String categoria = normalizarCategoria(linea.getCategoria());
            
            // Buscar regla activa para esta categoría
            for (ReglaMargen regla : reglasActivas) {
                if (normalizarCategoria(regla.getCategoriaProductoAfectada()).equals(categoria)) {
                    // Calcular margen % de la línea
                    BigDecimal costeTotal = multiplicarMonetario(linea.getCosteUnitario(), linea.getUnidades());
                    BigDecimal precioVentaTotal = multiplicarMonetario(linea.getPrecioVentaUnitario(), linea.getUnidades());
                    BigDecimal margenBrutoPedido = precioVentaTotal.subtract(costeTotal);
                    BigDecimal margenPorcentaje = calcularPorcentajeSeguro(margenBrutoPedido, precioVentaTotal);
                    
                    // Comparar con margen mínimo requerido
                    if (margenPorcentaje.compareTo(BigDecimal.valueOf(regla.getMargenMinimoPortcentaje())) < 0) {
                        Map<String, Object> incidencia = new HashMap<>();
                        incidencia.put("idLinea", linea.getIdLinea());
                        incidencia.put("producto", linea.getDescripcionProducto());
                        incidencia.put("categoria", categoria);
                        incidencia.put("margenActual", margenPorcentaje.setScale(2, RoundingMode.HALF_UP));
                        incidencia.put("margenRequerido", regla.getMargenMinimoPortcentaje());
                        incidencia.put("deficiencia", BigDecimal.valueOf(regla.getMargenMinimoPortcentaje())
                            .subtract(margenPorcentaje).setScale(2, RoundingMode.HALF_UP));
                        incidencias.add(incidencia);
                    }
                    break; // Solo una regla por categoría
                }
            }
        }
        return incidencias;
    }

    /**
     * Calcula la desviación entre la facturación real y el objetivo de facturación
     * de una zona comercial específica.
     * 
     * @param idZona ID de la zona comercial
     * @return Map con facturación real, objetivo, desviación en valor y porcentaje
     */
    public Map<String, BigDecimal> calcularDesviacionObjetivo(Integer idZona) {
        Map<String, BigDecimal> resultado = new HashMap<>();
        JsonRepositoryZonaComercial repoZonas = new JsonRepositoryZonaComercial();
        List<LineaPedido> pedidos = obtenerPedidosParaSesion();

        ZonaComercial zona = repoZonas.findById(idZona.toString());
        if (zona == null) {
            resultado.put("objetivo", BigDecimal.ZERO);
            resultado.put("real", BigDecimal.ZERO);
            resultado.put("desviacion", BigDecimal.ZERO);
            resultado.put("porcentajeDesviacion", BigDecimal.ZERO);
            return resultado;
        }

        // Calcular facturación real de la zona
        BigDecimal facturacionReal = BigDecimal.ZERO;
        if (pedidos != null) {
            facturacionReal = pedidos.stream()
                .filter(p -> p.getZonaComercial() != null && p.getZonaComercial().equals(idZona))
                .map(p -> multiplicarMonetario(p.getPrecioVentaUnitario(), p.getUnidades()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        }

        BigDecimal objetivo = BigDecimal.valueOf(zona.getObjetivoFacturacionAnual());
        BigDecimal desviacion = facturacionReal.subtract(objetivo);
        BigDecimal porcentajeDesviacion = calcularPorcentajeSeguro(desviacion, objetivo);

        resultado.put("zona", BigDecimal.valueOf(idZona));
        resultado.put("objetivo", objetivo.setScale(2, RoundingMode.HALF_UP));
        resultado.put("real", facturacionReal.setScale(2, RoundingMode.HALF_UP));
        resultado.put("desviacion", desviacion.setScale(2, RoundingMode.HALF_UP));
        resultado.put("porcentajeDesviacion", porcentajeDesviacion.setScale(2, RoundingMode.HALF_UP));

        return resultado;
    }

    /**
     * Genera un ranking de todas las zonas comerciales mostrando su facturación real
     * y desviación respecto al objetivo.
     * 
     * @return Lista de zonas con su desempeño ordenadas por desviación (mayor primero)
     */
    public List<Map<String, Object>> generarRankingZonasPorDesviacion() {
        List<Map<String, Object>> ranking = new ArrayList<>();
        JsonRepositoryZonaComercial repoZonas = new JsonRepositoryZonaComercial();
        List<ZonaComercial> zonas = repoZonas.findAll();
        SesionUsuario sesion = SesionAplicacion.obtener();

        if (sesion != null && sesion.esComercial() && sesion.zonaComercial() != null) {
            zonas = zonas.stream()
                .filter(zona -> zona != null && zona.getId() != null && zona.getId().equals(sesion.zonaComercial()))
                .collect(Collectors.toList());
        }

        for (ZonaComercial zona : zonas) {
            Map<String, BigDecimal> desviacion = calcularDesviacionObjetivo(zona.getId());
            
            Map<String, Object> entry = new HashMap<>();
            entry.put("idZona", zona.getId());
            entry.put("nombre", zona.getNombre());
            entry.put("pais", zona.getPais());
            entry.put("objetivo", desviacion.get("objetivo"));
            entry.put("real", desviacion.get("real"));
            entry.put("desviacion", desviacion.get("desviacion"));
            entry.put("porcentaje", desviacion.get("porcentajeDesviacion"));
            
            ranking.add(entry);
        }

        // Ordenar por desviación (descendente) - primero las que más superan el objetivo
        ranking.sort((a, b) -> {
            BigDecimal desv1 = (BigDecimal) a.get("desviacion");
            BigDecimal desv2 = (BigDecimal) b.get("desviacion");
            return desv2.compareTo(desv1);
        });

        return ranking;
    }

    private List<LineaPedido> obtenerPedidosParaSesion() {
        List<LineaPedido> pedidos = exploradorController.getPedidos();
        if (pedidos == null || pedidos.isEmpty()) {
            return pedidos;
        }

        SesionUsuario sesion = SesionAplicacion.obtener();
        if (sesion == null || !sesion.esComercial() || sesion.zonaComercial() == null) {
            return pedidos;
        }

        return pedidos.stream()
            .filter(p -> p != null && p.getZonaComercial() != null && p.getZonaComercial().equals(sesion.zonaComercial()))
            .collect(Collectors.toList());
    }
}