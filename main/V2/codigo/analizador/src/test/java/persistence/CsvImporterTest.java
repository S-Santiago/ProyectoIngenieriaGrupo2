package persistence;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.StringReader;
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
        CsvImporter.ImportResult<LineaPedido> resultado = importer.importCSVLineaPedidosConAvisos(tempFile.toString());
        List<LineaPedido> lineas = resultado.getElementos();

        assertEquals(1, lineas.size());
        assertEquals("2025-01-15", lineas.get(0).getFechaPedido());
        assertTrue(lineas.get(0).getCosteUnitario().compareTo(java.math.BigDecimal.ZERO) > 0);
        assertFalse(lineas.get(0).getPrecioVentaUnitario().compareTo(java.math.BigDecimal.ZERO) <= 0);
        assertEquals(1, resultado.getAvisos().size());
        assertTrue(resultado.getAvisos().get(0).contains("Fila 3"));
    }

        @Test
        void importaLineasDesdeReaderParaSoportarRecursosEmpaquetados() {
                String csv = String.join(System.lineSeparator(),
                                "idLinea,idPedido,referenciaProduto,descripcionProducto,categoria,costeUnitario,precioVentaUnitario,unidades,fechaPedido,zonaComercial,estado",
                                "3,102,PROD-3,Producto recurso,Electrónica,12.50,24.00,1,2025-01-16,2,completado");

                CsvImporter importer = new CsvImporter();
                CsvImporter.ImportResult<LineaPedido> resultado = importer.importCSVLineaPedidosConAvisos(new StringReader(csv));

                assertEquals(1, resultado.getElementos().size());
                assertTrue(resultado.getAvisos().isEmpty());
        }

    @Test
    void importaZonasOrdenadasPorIdAunqueElCsvVengaDesordenado() throws Exception {
        Path tempFile = Files.createTempFile("zonas", ".csv");
        Files.writeString(tempFile, String.join(System.lineSeparator(),
                "id,nombre,pais,responsableComercial,objetivoFacturacionAnual",
                "20,Zona C,España,Responsable C,3000",
                "5,Zona A,España,Responsable A,1000",
                "12,Zona B,España,Responsable B,2000"));

        CsvImporter importer = new CsvImporter();
        List<Integer> ids = importer.importCSVZonasComerciales(tempFile.toString()).stream()
                .map(model.ZonaComercial::getId)
                .toList();

        assertEquals(List.of(5, 12, 20), ids);
    }

    @Test
    void importaReglasOrdenadasPorIdAunqueElCsvVengaDesordenado() throws Exception {
        Path tempFile = Files.createTempFile("reglas", ".csv");
        Files.writeString(tempFile, String.join(System.lineSeparator(),
                "id,categoriaProductoAfectada,margenMinimoPortcentaje,activa,descripcion",
                "20,Categoria C,30,true,Regla C",
                "5,Categoria A,10,true,Regla A",
                "12,Categoria B,20,false,Regla B"));

        CsvImporter importer = new CsvImporter();
        List<Integer> ids = importer.importCSVReglasMargen(tempFile.toString()).stream()
                .map(model.ReglaMargen::getId)
                .toList();

        assertEquals(List.of(5, 12, 20), ids);
    }
}