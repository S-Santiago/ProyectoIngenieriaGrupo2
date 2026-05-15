package persistence;

import org.apache.poi.xssf.usermodel.*;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import model.LineaPedido;
import model.ZonaComercial;
import model.ReglaMargen;

public class ExcelExporter {
    public void exportToExcel(String filePath, List<LineaPedido> lineasPedido) {
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("LineasPedido");

        // Create header row
        XSSFRow headerRow = sheet.createRow(0);
        String[] headers = {"ID", "Cantidad", "Producto", "Categoria", "Subcategoria", "PrecioUnitario", "PrecioTotal", "Descuento", "Cliente", "ZonaComercial", "Estado"};
        for (int i = 0; i < headers.length; i++) {
            XSSFCell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
        }
        while (true) {
            // Fill data rows
            if (opcionExcel == "LineasPedido") {
                int rowNum = 1;
                for (LineaPedido linea : lineasPedido) {
                    XSSFRow row = sheet.createRow(rowNum++);
                    row.createCell(0).setCellValue(linea.getId());
                    row.createCell(1).setCellValue(linea.getCantidad());
                    row.createCell(2).setCellValue(linea.getProducto());
                    row.createCell(3).setCellValue(linea.getCategoria());
                    row.createCell(4).setCellValue(linea.getSubcategoria());
                    row.createCell(5).setCellValue(linea.getPrecioUnitario());
                    row.createCell(6).setCellValue(linea.getPrecioTotal());
                    row.createCell(7).setCellValue(linea.getDescuento());
                    row.createCell(8).setCellValue(linea.getCliente());
                    row.createCell(9).setCellValue(linea.getZonaComercial().toString());
                    row.createCell(10).setCellValue(linea.getEstado().toString());
                }
            }
            else if (opcionExcel == "ReglasMargen") {
                int rowNum = 1;
                for (ReglaMargen regla : reglasMargen) {
                    XSSFRow row = sheet.createRow(rowNum++);
                    row.createCell(0).setCellValue(regla.getId());
                    row.createCell(1).setCellValue(regla.getCategoria());
                    row.createCell(2).setCellValue(regla.getMargenMinimo());
                    row.createCell(3).setCellValue(regla.isEsObligatoria());
                    row.createCell(4).setCellValue(regla.getDescripcion());
                }
            }  
            else if (opcionExcel == "ZonasComerciales") {
                int rowNum = 1;
                for (ZonaComercial zona : zonasComerciales) {
                    XSSFRow row = sheet.createRow(rowNum++);
                    row.createCell(0).setCellValue(zona.getId());
                    row.createCell(1).setCellValue(zona.getNombre());
                    row.createCell(2).setCellValue(zona.getRegion());
                    row.createCell(3).setCellValue(zona.getPais());
                    row.createCell(4).setCellValue(zona.getDescuentoMaximo());
                }
            }
        }
        // Write to file
        try (FileOutputStream outputStream = new FileOutputStream(filePath)) {
            workbook.write(outputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
