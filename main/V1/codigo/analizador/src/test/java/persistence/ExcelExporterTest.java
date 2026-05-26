package persistence;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.FileInputStream;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Test;

class ExcelExporterTest {

    @Test
    void exportaAnalisisRentabilidadConTablasNativas() throws Exception {
        ExcelExporter exporter = new ExcelExporter();
        Path tempFile = Files.createTempFile("analisis-rentabilidad", ".xlsx");

        exporter.exportAnalisisRentabilidad(
                tempFile.toString(),
                List.of(
                        new ExcelExporter.RankingCategoriaExportRow(1, "Electrónica", new BigDecimal("120.00"), new BigDecimal("30.00"), new BigDecimal("25.00")),
                        new ExcelExporter.RankingCategoriaExportRow(2, "Hogar", new BigDecimal("90.00"), new BigDecimal("20.00"), new BigDecimal("22.22"))),
                List.of(
                        new ExcelExporter.LineaBajoMargenExportRow(10, "Portátil", "Electrónica", new BigDecimal("12.50"), new BigDecimal("15.00"), new BigDecimal("2.50"))));

        try (XSSFWorkbook workbook = new XSSFWorkbook(new FileInputStream(tempFile.toFile()))) {
            XSSFSheet rankingSheet = workbook.getSheet("RankingCategorias");
            XSSFSheet lineasSheet = workbook.getSheet("LineasBajoMargen");

            assertNotNull(rankingSheet);
            assertNotNull(lineasSheet);
            assertFalse(rankingSheet.getTables().isEmpty());
            assertFalse(lineasSheet.getTables().isEmpty());
            assertEquals("TablaRankingCategorias", rankingSheet.getTables().iterator().next().getName());
            assertEquals("TablaLineasBajoMargen", lineasSheet.getTables().iterator().next().getName());
            assertEquals("Categoría", rankingSheet.getRow(0).getCell(1).getStringCellValue());
            assertEquals("Producto", lineasSheet.getRow(0).getCell(1).getStringCellValue());
        }

        Files.deleteIfExists(tempFile);
    }
}
