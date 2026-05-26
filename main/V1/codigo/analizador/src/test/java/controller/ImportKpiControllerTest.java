package controller;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import model.EstadoPedido;
import model.LineaPedido;

class ImportKpiControllerTest {

    @Test
    void calculaKpiMensualSeparandoAnosYFiltrandoPorCategoria() {
        ExploradorController exploradorController = ExploradorController.getInstance();
        exploradorController.setPedidos(List.of(
                new LineaPedido(1, 100, "PROD-1", "Producto 1", "Electrónica",
                        new BigDecimal("10.00"), new BigDecimal("20.00"), 2, "2025-01-15", 1, EstadoPedido.COMPLETADO),
                new LineaPedido(2, 101, "PROD-2", "Producto 2", "Electrónica",
                        new BigDecimal("12.00"), new BigDecimal("24.00"), 3, "2026-01-15", 1, EstadoPedido.COMPLETADO),
                new LineaPedido(3, 102, "PROD-3", "Producto 3", "Accesorios",
                        new BigDecimal("5.00"), new BigDecimal("15.00"), 1, "2025-01-15", 1, EstadoPedido.COMPLETADO)));

        ImportKpiController controller = new ImportKpiController();

        Map<String, BigDecimal> facturacion = controller.calcularKPIMensualFacturacion("Electrónica");

        assertEquals(2, facturacion.size());
        assertEquals(new BigDecimal("40.00"), facturacion.get("2025-01"));
        assertEquals(new BigDecimal("72.00"), facturacion.get("2026-01"));

        Map<String, BigDecimal> margen = controller.calcularKPIMensualMargen("Electrónica");

        assertEquals(2, margen.size());
        assertEquals(new BigDecimal("20.00"), margen.get("2025-01"));
        assertEquals(new BigDecimal("36.00"), margen.get("2026-01"));
    }
}