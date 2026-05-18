package persistence;

import model.LineaPedido;
import model.ReglaMargen;
import model.ZonaComercial;
import org.apache.poi.xssf.usermodel.*;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class ExcelExporter {

    public void exportLineasPedido(String filePath, List<LineaPedido> lineasPedido) {
            try (XSSFWorkbook workbook = new XSSFWorkbook()) {
                XSSFSheet sheet = workbook.createSheet("LineasPedido");

            XSSFRow headerRow = sheet.createRow(0);
            String[] headers = {"ID", "Cantidad", "Producto", "Categoria", "PrecioUnitario", "ZonaComercial", "Estado"};
            for (int i = 0; i < headers.length; i++) {
                headerRow.createCell(i).setCellValue(headers[i]);
            }

            int rowNum = 1;
            for (LineaPedido linea : lineasPedido) {
                XSSFRow row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(linea.getIdLinea());
                row.createCell(1).setCellValue(linea.getUnidades());
                row.createCell(2).setCellValue(linea.getDescripcionProducto());
                row.createCell(3).setCellValue(linea.getCategoria());
                row.createCell(4).setCellValue(linea.getPrecioVentaUnitario());
                row.createCell(5).setCellValue(linea.getZonaComercial().toString());
                row.createCell(6).setCellValue(linea.getEstado().toString());
            }

            escribirFichero(workbook, filePath);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void exportReglasMargen(String filePath, List<ReglaMargen> reglasMargen) {
        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            XSSFSheet sheet = workbook.createSheet("ReglasMargen");

            XSSFRow headerRow = sheet.createRow(0);
            String[] headers = {"ID", "Categoria", "MargenMinimo", "Activa", "Descripcion"};
            for (int i = 0; i < headers.length; i++) {
                headerRow.createCell(i).setCellValue(headers[i]);
            }

            int rowNum = 1;
            for (ReglaMargen regla : reglasMargen) {
                XSSFRow row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(regla.getId());
                row.createCell(1).setCellValue(regla.getCategoriaProductoAfectada());
                row.createCell(2).setCellValue(regla.getMargenMinimoPortcentaje());
                row.createCell(3).setCellValue(regla.isActiva());
                row.createCell(4).setCellValue(regla.getDescripcion());
            }

            escribirFichero(workbook, filePath);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void exportZonasComerciales(String filePath, List<ZonaComercial> zonasComerciales) {
        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            XSSFSheet sheet = workbook.createSheet("ZonasComerciales");

            XSSFRow headerRow = sheet.createRow(0);
            String[] headers = {"ID", "Nombre", "Pais", "Responsable Comercial", "Objetivo Facturacion Anual"};
            for (int i = 0; i < headers.length; i++) {
                headerRow.createCell(i).setCellValue(headers[i]);
            }

            int rowNum = 1;
            for (ZonaComercial zona : zonasComerciales) {
                XSSFRow row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(zona.getId());
                row.createCell(1).setCellValue(zona.getNombre());
                row.createCell(2).setCellValue(zona.getPais());
                row.createCell(3).setCellValue(zona.getResponsableComercial());
                row.createCell(4).setCellValue(zona.getObjetivoFacturacionAnual());
            }

            escribirFichero(workbook, filePath);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void escribirFichero(XSSFWorkbook workbook, String filePath) {
        try (FileOutputStream outputStream = new FileOutputStream(filePath)) {
            workbook.write(outputStream);
            workbook.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}