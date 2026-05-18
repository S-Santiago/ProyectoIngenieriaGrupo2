package persistence;

import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import model.LineaPedido;
import model.ReglaMargen;
import model.ZonaComercial;

public class ExcelExporter {

    public void exportLineasPedido(String filePath, List<LineaPedido> lineasPedido) {
        List<LineaPedido> lineasSeguras = lineasPedido == null ? Collections.emptyList() : lineasPedido;
        try (XSSFWorkbook workbook = new XSSFWorkbook(); FileOutputStream outputStream = new FileOutputStream(filePath)) {
            XSSFSheet sheet = workbook.createSheet("LineasPedido");

            XSSFRow headerRow = sheet.createRow(0);
            String[] headers = {"ID", "Cantidad", "Producto", "Categoria", "PrecioUnitario", "ZonaComercial", "Estado"};
            for (int i = 0; i < headers.length; i++) {
                headerRow.createCell(i).setCellValue(headers[i]);
            }

            int rowNum = 1;
            for (LineaPedido linea : lineasSeguras) {
                if (linea == null) {
                    continue;
                }
                XSSFRow row = sheet.createRow(rowNum++);
                setCellValue(row, 0, linea.getIdLinea());
                setCellValue(row, 1, linea.getUnidades());
                setCellValue(row, 2, linea.getDescripcionProducto());
                setCellValue(row, 3, linea.getCategoria());
                setCellValue(row, 4, linea.getPrecioVentaUnitario());
                setCellValue(row, 5, linea.getZonaComercial());
                setCellValue(row, 6, linea.getEstado());
            }

            workbook.write(outputStream);
        } catch (IOException e) {
            System.err.println("Error al exportar líneas de pedido: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Error inesperado al exportar líneas de pedido: " + e.getMessage());
        }
    }

    public void exportReglasMargen(String filePath, List<ReglaMargen> reglasMargen) {
        List<ReglaMargen> reglasSeguras = reglasMargen == null ? Collections.emptyList() : reglasMargen;
        try (XSSFWorkbook workbook = new XSSFWorkbook(); FileOutputStream outputStream = new FileOutputStream(filePath)) {
            XSSFSheet sheet = workbook.createSheet("ReglasMargen");

            XSSFRow headerRow = sheet.createRow(0);
            String[] headers = {"ID", "Categoria", "MargenMinimo", "Activa", "Descripcion"};
            for (int i = 0; i < headers.length; i++) {
                headerRow.createCell(i).setCellValue(headers[i]);
            }

            int rowNum = 1;
            for (ReglaMargen regla : reglasSeguras) {
                if (regla == null) {
                    continue;
                }
                XSSFRow row = sheet.createRow(rowNum++);
                setCellValue(row, 0, regla.getId());
                setCellValue(row, 1, regla.getCategoriaProductoAfectada());
                setCellValue(row, 2, BigDecimal.valueOf(regla.getMargenMinimoPortcentaje()));
                setCellValue(row, 3, regla.isActiva());
                setCellValue(row, 4, regla.getDescripcion());
            }

            workbook.write(outputStream);
        } catch (IOException e) {
            System.err.println("Error al exportar reglas de margen: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Error inesperado al exportar reglas de margen: " + e.getMessage());
        }
    }

    public void exportZonasComerciales(String filePath, List<ZonaComercial> zonasComerciales) {
        List<ZonaComercial> zonasSeguras = zonasComerciales == null ? Collections.emptyList() : zonasComerciales;
        try (XSSFWorkbook workbook = new XSSFWorkbook(); FileOutputStream outputStream = new FileOutputStream(filePath)) {
            XSSFSheet sheet = workbook.createSheet("ZonasComerciales");

            XSSFRow headerRow = sheet.createRow(0);
            String[] headers = {"ID", "Nombre", "Pais", "Responsable Comercial", "Objetivo Facturacion Anual"};
            for (int i = 0; i < headers.length; i++) {
                headerRow.createCell(i).setCellValue(headers[i]);
            }

            int rowNum = 1;
            for (ZonaComercial zona : zonasSeguras) {
                if (zona == null) {
                    continue;
                }
                XSSFRow row = sheet.createRow(rowNum++);
                setCellValue(row, 0, zona.getId());
                setCellValue(row, 1, zona.getNombre());
                setCellValue(row, 2, zona.getPais());
                setCellValue(row, 3, zona.getResponsableComercial());
                setCellValue(row, 4, BigDecimal.valueOf(zona.getObjetivoFacturacionAnual()));
            }

            workbook.write(outputStream);
        } catch (IOException e) {
            System.err.println("Error al exportar zonas comerciales: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Error inesperado al exportar zonas comerciales: " + e.getMessage());
        }
    }

    private void setCellValue(XSSFRow row, int columnIndex, String value) {
        row.createCell(columnIndex).setCellValue(value == null ? "" : value);
    }

    private void setCellValue(XSSFRow row, int columnIndex, Integer value) {
        row.createCell(columnIndex).setCellValue(value == null ? "" : String.valueOf(value));
    }

    private void setCellValue(XSSFRow row, int columnIndex, boolean value) {
        row.createCell(columnIndex).setCellValue(value);
    }

    private void setCellValue(XSSFRow row, int columnIndex, BigDecimal value) {
        row.createCell(columnIndex).setCellValue(value == null ? "" : value.toPlainString());
    }

    private void setCellValue(XSSFRow row, int columnIndex, Enum<?> value) {
        row.createCell(columnIndex).setCellValue(value == null ? "" : value.name());
    }
}