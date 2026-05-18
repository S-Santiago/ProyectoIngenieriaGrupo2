package controller;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import model.LineaPedido;


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
        List<LineaPedido> pedidos = exploradorController.getPedidos();
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
        List<LineaPedido> pedidos = exploradorController.getPedidos();
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
        List<LineaPedido> pedidos = exploradorController.getPedidos();
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
}