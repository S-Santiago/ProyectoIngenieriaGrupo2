package persistence;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;

import model.LineaPedido;

class CsvImporterTest {

    @Test
    void importaSoloLineasValidasYNormalizaFechaIso() throws Exception {
        Path tempFile = Files.createTempFile("lineas-pedidos", ".csv");
        Files.writeString(tempFile, String.join(System.lineSeparator(),
                "idLinea,idPedido,referenciaProduto,descripcionProducto,categoria,costeUnitario,precioVentaUnitario,unidades,fechaPedido,zonaComercial,estado",
                "1,100,PROD-1,Producto valido,Electrónica,10.50,20.00,2,2025-01-15,1,completado",
                "2,101,PROD-2,Producto invalido,Electrónica,0,20.00,2,2025-01-15,1,completado"));

        CsvImporter importer = new CsvImporter();
        List<LineaPedido> lineas = importer.importCSVLineaPedidos(tempFile.toString());

        assertEquals(1, lineas.size());
        assertEquals("2025-01-15", lineas.get(0).getFechaPedido());
        assertTrue(lineas.get(0).getCosteUnitario().compareTo(java.math.BigDecimal.ZERO) > 0);
        assertFalse(lineas.get(0).getPrecioVentaUnitario().compareTo(java.math.BigDecimal.ZERO) <= 0);
    }
}