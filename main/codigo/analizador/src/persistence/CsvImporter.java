package persistence;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.ArrayList;
import java.util.List;

import model.EstadoPedido;
import model.LineaPedido;
import model.ReglaMargen;
import model.ZonaComercial;

public class CsvImporter {
    private static final DateTimeFormatter FECHA_DD_MM_YYYY = DateTimeFormatter.ofPattern("dd/MM/uuuu").withResolverStyle(ResolverStyle.STRICT);
    private static final DateTimeFormatter FECHA_YYYY_MM_DD = DateTimeFormatter.ofPattern("uuuu-MM-dd").withResolverStyle(ResolverStyle.STRICT);
    private static final DateTimeFormatter FECHA_NORMALIZADA = DateTimeFormatter.ofPattern("dd-MM-uuuu").withResolverStyle(ResolverStyle.STRICT);

    public List<LineaPedido> importCSVLineaPedidos(String filePath) {
        
        List<LineaPedido> lineasPedido = new ArrayList<>();
        int contadorLineas = 0;
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                contadorLineas++;
                if (line.trim().isEmpty()) {
                    continue;
                }
                if (esCabeceraLineaPedido(line)) {
                    continue;
                }
                try {
                    List<String> values = parseCsvLine(line);
                    if (values.size() != 11) {
                        throw new IllegalArgumentException("Número de columnas inválido. Se esperaban 11 y se recibieron " + values.size());
                    }

                    int idLinea = parseEnteroPositivo(values.get(0), "ID de línea", contadorLineas, 1);
                    int idPedido = parseEnteroPositivo(values.get(1), "ID de pedido", contadorLineas, 2);
                    String referenciaProducto = valorNoVacio(values.get(2), "Referencia de producto", contadorLineas, 3);
                    String descripcionProducto = valorNoVacio(values.get(3), "Descripción de producto", contadorLineas, 4);
                    String categoria = valorNoVacio(values.get(4), "Categoría", contadorLineas, 5);
                    BigDecimal costeUnitario = parseDecimalNoNegativo(values.get(5), "Coste unitario", contadorLineas, 6);
                    BigDecimal precioVentaUnitario = parseDecimalNoNegativo(values.get(6), "Precio de venta unitario", contadorLineas, 7);
                    int unidades = parseEnteroNoNegativo(values.get(7), "Número de unidades", contadorLineas, 8);
                    String fechaPedido = parseFechaFlexible(values.get(8), "Fecha de pedido", contadorLineas, 9);
                    int zonaComercial = parseEnteroNoNegativo(values.get(9), "Zona comercial", contadorLineas, 10);
                    EstadoPedido estado = parseEstadoPedido(values.get(10), contadorLineas, 11);

                    LineaPedido linea = new LineaPedido(
                        idLinea,
                        idPedido,
                        referenciaProducto,
                        descripcionProducto,
                        categoria,
                        costeUnitario,
                        precioVentaUnitario,
                        unidades,
                        fechaPedido,
                        zonaComercial,
                        estado
                    );
                    lineasPedido.add(linea);
                } catch (IllegalArgumentException e) {
                    System.out.println("Aviso: línea inválida en fila " + contadorLineas + ": " + e.getMessage());
                }
            }
        } 
        catch (IOException e) {
            System.err.println("Error al importar líneas de pedido: " + e.getMessage());
        }
        finally {
            System.out.println("Número total de líneas procesadas: " + contadorLineas);
            System.out.println("Número total de líneas importadas: " + lineasPedido.size());
        }
        return lineasPedido;
    }

    public List<ZonaComercial> importCSVZonasComerciales(String filePath) {
        List<ZonaComercial> zonas = new ArrayList<>();
        int contadorLineas = 0;
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                contadorLineas++;
                if (line.trim().isEmpty()) {
                    continue;
                }
                if (esCabeceraZonaComercial(line)) {
                    continue;
                }
                
                try {
                    List<String> values = parseCsvLine(line);
                    if (values.size() != 5) {
                        throw new IllegalArgumentException("Número de columnas inválido. Se esperaban 5 y se recibieron " + values.size());
                    }

                    int id = parseEnteroPositivo(values.get(0), "ID de zona", contadorLineas, 1);
                    String nombre = valorNoVacio(values.get(1), "Nombre", contadorLineas, 2);
                    String pais = valorNoVacio(values.get(2), "País", contadorLineas, 3);
                    String responsableComercial = valorNoVacio(values.get(3), "Responsable comercial", contadorLineas, 4);
                    BigDecimal objetivoFacturacionAnual = parseDecimalNoNegativo(values.get(4), "Objetivo de facturación anual", contadorLineas, 5);

                    ZonaComercial zona = new ZonaComercial(id, nombre, pais, responsableComercial, objetivoFacturacionAnual.doubleValue());
                    zonas.add(zona);
                } catch (IllegalArgumentException e) {
                    System.out.println("Aviso: línea inválida en fila " + contadorLineas + ": " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.err.println("Error al importar zonas comerciales: " + e.getMessage());
        }
        System.out.println("Número total de líneas procesadas: " + contadorLineas);
        System.out.println("Número total de zonas importadas: " + zonas.size());
        return zonas;
    }

    public List<ReglaMargen> importCSVReglasMargen(String filePath) {
        List<ReglaMargen> reglas = new ArrayList<>();
        int contadorLineas = 0;
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                contadorLineas++;
                if (line.trim().isEmpty()) {
                    continue;
                }
                if (esCabeceraReglaMargen(line)) {
                    continue;
                }
                
                try {
                    List<String> values = parseCsvLine(line);
                    if (values.size() != 5) {
                        throw new IllegalArgumentException("Número de columnas inválido. Se esperaban 5 y se recibieron " + values.size());
                    }

                    int id = parseEnteroPositivo(values.get(0), "ID de regla", contadorLineas, 1);
                    String categoriaProductoAfectada = valorNoVacio(values.get(1), "Categoría afectada", contadorLineas, 2);
                    BigDecimal margenMinimoPortcentaje = parseDecimalNoNegativo(values.get(2), "Margen mínimo porcentual", contadorLineas, 3);
                    boolean activa = parseBooleano(values.get(3), "Activa", contadorLineas, 4);
                    String descripcion = valorNoVacio(values.get(4), "Descripción", contadorLineas, 5);

                    ReglaMargen regla = new ReglaMargen(id, categoriaProductoAfectada, margenMinimoPortcentaje.doubleValue(), activa, descripcion);
                    reglas.add(regla);
                } catch (IllegalArgumentException e) {
                    System.out.println("Aviso: línea inválida en fila " + contadorLineas + ": " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.err.println("Error al importar reglas de margen: " + e.getMessage());
        }
        System.out.println("Número total de líneas procesadas: " + contadorLineas);
        System.out.println("Número total de reglas importadas: " + reglas.size());
        return reglas;
    }

    private boolean esCabeceraLineaPedido(String line) {
        String normalizada = line.trim().toLowerCase();
        return normalizada.startsWith("idlinea")
            || normalizada.startsWith("idpedido")
            || normalizada.startsWith("unidades")
            || normalizada.startsWith("costeunitario")
            || normalizada.startsWith("precioventaunitario")
                || normalizada.startsWith("fechapedido")
                || normalizada.startsWith("categoria")
                || normalizada.startsWith("zonacomercial")
                || normalizada.startsWith("estado")
                || normalizada.startsWith("referenciaproducto");
    }

    private boolean esCabeceraZonaComercial(String line) {
        String normalizada = line.trim().toLowerCase();
        return normalizada.startsWith("id")
                || normalizada.startsWith("nombre")
                || normalizada.startsWith("pais")
                || normalizada.startsWith("responsablecomercial")
                || normalizada.startsWith("objetivofacturacionanual");
    }

    private boolean esCabeceraReglaMargen(String line) {
        String normalizada = line.trim().toLowerCase();
        return normalizada.startsWith("id")
                || normalizada.startsWith("categoriaproductoafectada")
                || normalizada.startsWith("margenminimoportcentaje")
                || normalizada.startsWith("activa")
                || normalizada.startsWith("descripcion");
    }

    private List<String> parseCsvLine(String line) {
        List<String> values = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean insideQuotes = false;

        for (int i = 0; i < line.length(); i++) {
            char currentChar = line.charAt(i);

            if (currentChar == '"') {
                if (insideQuotes && i + 1 < line.length() && line.charAt(i + 1) == '"') {
                    current.append('"');
                    i++;
                } else {
                    insideQuotes = !insideQuotes;
                }
            } else if (currentChar == ',' && !insideQuotes) {
                values.add(current.toString().trim());
                current.setLength(0);
            } else {
                current.append(currentChar);
            }
        }

        if (insideQuotes) {
            throw new IllegalArgumentException("Comillas dobles sin cerrar en la línea: " + line);
        }

        values.add(current.toString().trim());
        return values;
    }

    private int parseEnteroPositivo(String value, String fieldName, int row, int column) {
        int parsedValue;
        try {
            parsedValue = Integer.parseInt(value.trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(formatearError(row, column, fieldName + " no válido (debe ser un número entero): " + value));
        }
        if (parsedValue <= 0) {
            throw new IllegalArgumentException(formatearError(row, column, fieldName + " no válido (debe ser positivo): " + value));
        }
        return parsedValue;
    }

    private int parseEnteroNoNegativo(String value, String fieldName, int row, int column) {
        int parsedValue;
        try {
            parsedValue = Integer.parseInt(value.trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(formatearError(row, column, fieldName + " no válido (debe ser un número entero): " + value));
        }
        if (parsedValue < 0) {
            throw new IllegalArgumentException(formatearError(row, column, fieldName + " no válido (no puede ser negativo): " + value));
        }
        return parsedValue;
    }

    private BigDecimal parseDecimalNoNegativo(String value, String fieldName, int row, int column) {
        BigDecimal parsedValue;
        try {
            parsedValue = new BigDecimal(normalizarDecimal(value));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(formatearError(row, column, fieldName + " no válido (debe ser decimal): " + value));
        }
        if (parsedValue.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException(formatearError(row, column, fieldName + " no válido (no puede ser negativo): " + value));
        }
        return parsedValue;
    }

    private String valorNoVacio(String value, String fieldName, int row, int column) {
        String trimmedValue = value.trim();
        if (trimmedValue.isEmpty()) {
            throw new IllegalArgumentException(formatearError(row, column, fieldName + " no válido (no puede estar vacío)"));
        }
        return trimmedValue;
    }

    private boolean parseBooleano(String value, String fieldName, int row, int column) {
        String trimmedValue = value.trim().toLowerCase();
        if ("true".equals(trimmedValue) || "false".equals(trimmedValue)) {
            return Boolean.parseBoolean(trimmedValue);
        }
        throw new IllegalArgumentException(formatearError(row, column, fieldName + " no válido (use true o false): " + value));
    }

    private String parseFechaFlexible(String value, String fieldName, int row, int column) {
        String raw = value.trim();
        if (raw.isEmpty()) {
            throw new IllegalArgumentException(formatearError(row, column, fieldName + " no válido (no puede estar vacío)"));
        }

        LocalDate parsedDate = tryParseDate(raw);
        if (parsedDate == null) {
            throw new IllegalArgumentException(formatearError(row, column, fieldName + " no reconocible. Formatos admitidos: dd/MM/yyyy, yyyy-MM-dd"));
        }

        return parsedDate.format(FECHA_NORMALIZADA);
    }

    private LocalDate tryParseDate(String value) {
        try {
            return LocalDate.parse(value, FECHA_DD_MM_YYYY);
        } catch (DateTimeParseException ignored) {
            // Intento siguiente.
        }

        try {
            return LocalDate.parse(value, FECHA_YYYY_MM_DD);
        } catch (DateTimeParseException ignored) {
            // Intento siguiente.
        }

        try {
            return LocalDate.parse(value, DateTimeFormatter.ofPattern("dd-MM-uuuu").withResolverStyle(ResolverStyle.STRICT));
        } catch (DateTimeParseException ignored) {
            return null;
        }
    }

    private String normalizarDecimal(String value) {
        String cleaned = value == null ? "" : value.trim().replace(" ", "");
        if (cleaned.isEmpty()) {
            throw new NumberFormatException("Valor vacío");
        }

        boolean hasComma = cleaned.contains(",");
        boolean hasDot = cleaned.contains(".");

        if (hasComma && hasDot) {
            if (cleaned.lastIndexOf(',') > cleaned.lastIndexOf('.')) {
                cleaned = cleaned.replace(".", "");
                cleaned = cleaned.replace(",", ".");
            } else {
                cleaned = cleaned.replace(",", "");
            }
        } else if (hasComma) {
            int digitsAfter = cleaned.length() - cleaned.lastIndexOf(',') - 1;
            if (digitsAfter == 3) {
                cleaned = cleaned.replace(",", "");
            } else {
                cleaned = cleaned.replace(",", ".");
            }
        } else if (hasDot) {
            int digitsAfter = cleaned.length() - cleaned.lastIndexOf('.') - 1;
            if (digitsAfter == 3) {
                cleaned = cleaned.replace(".", "");
            }
        }

        return cleaned;
    }

    private String formatearError(int row, int column, String mensaje) {
        return "fila " + row + ", columna " + column + ": " + mensaje;
    }

    private EstadoPedido parseEstadoPedido(String value, int row, int column) {
        try {
            return EstadoPedido.fromString(value.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(formatearError(row, column, "Estado de pedido no válido: " + value));
        }
    }
}
