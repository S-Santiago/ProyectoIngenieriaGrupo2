package persistence;

import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import org.apache.poi.ss.SpreadsheetVersion;
import org.apache.poi.ss.util.AreaReference;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFTable;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTTable;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTTableColumn;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTTableColumns;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTTableStyleInfo;

import model.LineaPedido;
import model.ReglaMargen;
import model.ZonaComercial;

public class ExcelExporter {

    public record RankingCategoriaExportRow(Integer posicion, String categoria, BigDecimal facturacion,
            BigDecimal margen, BigDecimal margenPorcentaje) {
    }

    public record LineaBajoMargenExportRow(Integer idLinea, String producto, String categoria,
            BigDecimal margenActual, BigDecimal margenRequerido, BigDecimal deficiencia) {
    }

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

    public void exportAnalisisRentabilidad(String filePath, List<RankingCategoriaExportRow> rankingCategorias,
            List<LineaBajoMargenExportRow> lineasBajoMargen) {
        List<RankingCategoriaExportRow> rankingSeguro = rankingCategorias == null ? Collections.emptyList() : rankingCategorias;
        List<LineaBajoMargenExportRow> lineasSeguras = lineasBajoMargen == null ? Collections.emptyList() : lineasBajoMargen;

        try (XSSFWorkbook workbook = new XSSFWorkbook(); FileOutputStream outputStream = new FileOutputStream(filePath)) {
            crearHojaRankingCategorias(workbook, rankingSeguro);
            crearHojaLineasBajoMargen(workbook, lineasSeguras);
            workbook.write(outputStream);
        } catch (IOException e) {
            System.err.println("Error al exportar el análisis de rentabilidad: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Error inesperado al exportar el análisis de rentabilidad: " + e.getMessage());
        }
    }

    private void crearHojaRankingCategorias(XSSFWorkbook workbook, List<RankingCategoriaExportRow> rankingCategorias) {
        String[] headers = {"Posición", "Categoría", "Facturación", "Margen bruto", "Margen %"};
        List<List<?>> filas = new java.util.ArrayList<>();
        for (RankingCategoriaExportRow fila : rankingCategorias) {
            filas.add(List.<Object>of(
                fila.posicion(),
                fila.categoria(),
                fila.facturacion(),
                fila.margen(),
                fila.margenPorcentaje()));
        }
        crearHojaTabla(workbook, "RankingCategorias", "TablaRankingCategorias", headers, filas);
    }

    private void crearHojaLineasBajoMargen(XSSFWorkbook workbook, List<LineaBajoMargenExportRow> lineasBajoMargen) {
        String[] headers = {"ID Línea", "Producto", "Categoría", "Margen actual", "Margen requerido", "Deficiencia"};
        List<List<?>> filas = new java.util.ArrayList<>();
        for (LineaBajoMargenExportRow fila : lineasBajoMargen) {
            filas.add(List.<Object>of(
                fila.idLinea(),
                fila.producto(),
                fila.categoria(),
                fila.margenActual(),
                fila.margenRequerido(),
                fila.deficiencia()));
        }
        crearHojaTabla(workbook, "LineasBajoMargen", "TablaLineasBajoMargen", headers, filas);
    }

    private void crearHojaTabla(XSSFWorkbook workbook, String nombreHoja, String nombreTabla, String[] headers,
            List<? extends List<?>> filas) {
        XSSFSheet sheet = workbook.createSheet(nombreHoja);
        escribirCabecera(sheet, headers);

        int rowIndex = 1;
        for (List<?> fila : filas) {
            XSSFRow row = sheet.createRow(rowIndex++);
            for (int i = 0; i < headers.length; i++) {
                Object valor = i < fila.size() ? fila.get(i) : null;
                escribirCelda(row, i, valor);
            }
        }

        if (!filas.isEmpty()) {
            definirTabla(sheet, nombreTabla, headers.length, filas.size());
        }

        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }
    }

    private void escribirCabecera(XSSFSheet sheet, String[] headers) {
        XSSFRow headerRow = sheet.createRow(0);
        for (int i = 0; i < headers.length; i++) {
            headerRow.createCell(i).setCellValue(headers[i]);
        }
    }

    private void definirTabla(XSSFSheet sheet, String nombreTabla, int numeroColumnas, int numeroFilasDatos) {
        AreaReference areaReference = new AreaReference(
            new CellReference(0, 0),
            new CellReference(numeroFilasDatos, numeroColumnas - 1),
            SpreadsheetVersion.EXCEL2007);

        XSSFTable table = sheet.createTable(areaReference);
        table.setName(nombreTabla);
        table.setDisplayName(nombreTabla);

        CTTable ctTable = table.getCTTable();
        ctTable.setRef(areaReference.formatAsString());
        ctTable.setHeaderRowCount(1L);
        ctTable.setTotalsRowShown(false);

        CTTableColumns tableColumns = ctTable.addNewTableColumns();
        tableColumns.setCount(numeroColumnas);
        for (int i = 0; i < numeroColumnas; i++) {
            CTTableColumn tableColumn = tableColumns.addNewTableColumn();
            tableColumn.setId(i + 1);
            tableColumn.setName(sheet.getRow(0).getCell(i).getStringCellValue());
        }

        CTTableStyleInfo styleInfo = ctTable.addNewTableStyleInfo();
        styleInfo.setName("TableStyleMedium2");
        styleInfo.setShowRowStripes(true);
        styleInfo.setShowColumnStripes(false);
    }

    private void escribirCelda(XSSFRow row, int columnIndex, Object valor) {
        if (valor == null) {
            row.createCell(columnIndex).setBlank();
            return;
        }

        if (valor instanceof BigDecimal bigDecimal) {
            row.createCell(columnIndex).setCellValue(bigDecimal.doubleValue());
            return;
        }

        if (valor instanceof Number number) {
            row.createCell(columnIndex).setCellValue(number.doubleValue());
            return;
        }

        if (valor instanceof Boolean booleanValue) {
            row.createCell(columnIndex).setCellValue(booleanValue);
            return;
        }

        if (valor instanceof Enum<?> enumerado) {
            row.createCell(columnIndex).setCellValue(enumerado.name());
            return;
        }

        row.createCell(columnIndex).setCellValue(String.valueOf(valor));
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