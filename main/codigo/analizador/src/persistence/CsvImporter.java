package persistence;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import model.LineaPedido;
import model.ZonaComercial;
import model.ReglaMargen;
import model.EstadoPedido;

public class CsvImporter {
    public List<LineaPedido> importCSVLineaPedidos(String filePath) {
        
        List<LineaPedido> lineasPedido = new ArrayList<>();
        int contadorLineas = 0;
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                contadorLineas++;
                if (line.trim().isEmpty()) continue; // Salta las líneas vacías
                else if (line.length() == 10) { 
                    String[] values = line.split(",");
                    if (values.length != 11) {
                        System.out.println("Línea con formato incorrecto: " + line);
                        System.out.println("Fila: " + contadorLineas);
                        break; // Saltar líneas con formato incorrecto
                    }
                    else if (Integer.parseInt(values[0].trim()) <= 0) {
                        System.out.println("ID de línea no válido (debe ser positivo): " + values[0]);
                        System.out.println("Fila: " + contadorLineas);
                        break; // Saltar líneas con ID de línea no válido
                    }
                    else if (Integer.parseInt(values[1].trim()) <= 0) {
                        System.out.println("ID de pedido no válido (debe ser positivo): " + values[1]);
                        System.out.println("Fila: " + contadorLineas);
                        break; // Saltar líneas con ID de pedido no válido
                    }
                    else if (Integer.parseInt(values[7].trim()) < 0) {
                        System.out.println("Número de unidades no válido (no puede ser negativo): " + values[7]);
                        System.out.println("Fila: " + contadorLineas);
                        break; // Saltar líneas con número de unidades no válido
                    }
                    else if (Integer.parseInt(values[9].trim()) < 0) {
                        System.out.println("Zona comercial no válida (debe ser positiva): " + values[9]);
                        System.out.println("Fila: " + contadorLineas);
                        break; // Saltar líneas con zona comercial no válida
                    }
                    else if (!values[10].equalsIgnoreCase("COMPLETADO") && !values[10].equalsIgnoreCase("CANCELADO") && !values[10].equalsIgnoreCase("PENDIENTE")) {
                        System.out.println("Estado de pedido no válido (debe ser 'COMPLETADO', 'CANCELADO' o 'PENDIENTE'): " + values[10]);
                        System.out.println("Fila: " + contadorLineas);
                        break; // Saltar líneas con estado de pedido no válido
                    }
                    else if (Double.parseDouble(values[5].trim()) < 0) {
                        System.out.println("Coste unitario no válido (no puede ser negativo): " + values[5]);
                        System.out.println("Fila: " + contadorLineas);
                        break; // Saltar líneas con coste unitario no válido
                    }
                    else if (Double.parseDouble(values[6].trim()) < 0) {
                        System.out.println("Precio de venta unitario no válido (no puede ser negativo): " + values[6]);
                        System.out.println("Fila: " + contadorLineas);
                        break; // Saltar líneas con precio de venta unitario no válido
                    }
                    else if (values[2].trim().isEmpty()) {
                        System.out.println("Referencia de producto no válida (no puede estar vacía): " + values[2]);
                        System.out.println("Fila: " + contadorLineas);
                        break; // Saltar líneas con referencia de producto vacía
                    }
                    else if (values[3].trim().isEmpty()) {
                        System.out.println("Descripción de producto no válida (no puede estar vacía): " + values[3]);
                        System.out.println("Fila: " + contadorLineas);
                        break; // Saltar líneas con descripción de producto vacía
                    }
                    else if (values[4].trim().isEmpty()) {
                        System.out.println("Categoría no válida (no puede estar vacía): " + values[4]);
                        System.out.println("Fila: " + contadorLineas);
                        break; // Saltar líneas con categoría vacía
                    }
                    else if (values[8].trim().isEmpty()) {
                        System.out.println("Fecha de pedido no válida (no puede estar vacía): " + values[8]);
                        System.out.println("Fila: " + contadorLineas);
                        break; // Saltar líneas con fecha de pedido vacía
                    }
                    else if (!values[8].matches("\\d{4}-\\d{2}-\\d{2}")) {
                        System.out.println("Fecha de pedido con formato no válido (debe ser YYYY-MM-DD): " + values[8]);
                        System.out.println("Fila: " + contadorLineas);
                        break; // Saltar líneas con formato de fecha no válido
                    }
                    else{
                        LineaPedido linea = new LineaPedido(
                            Integer.parseInt(values[0].trim()), 
                            Integer.parseInt(values[1].trim()), 
                            values[2].trim(),
                            values[3].trim(), 
                            values[4].trim(),
                            Double.parseDouble(values[5].trim()), 
                            Double.parseDouble(values[6].trim()),
                            Integer.parseInt(values[7].trim()), 
                            values[8].trim(), 
                            Integer.parseInt(values[9].trim()),
                            EstadoPedido.fromString(values[10].trim().toUpperCase())
                        );
                        lineasPedido.add(linea);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return lineasPedido;
    }
    
    public List<ZonaComercial> importCSVZonasComerciales(String filePath) {
        List<ZonaComercial> zonasComerciales = new ArrayList<>();
        int contadorLineas = 0;
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                contadorLineas++;
                if (line.trim().isEmpty()) continue; // Salta las líneas vacías
                String[] values = line.split(",");
                if (values.length != 5) {
                    System.out.println("Línea con formato incorrecto: " + line);
                    System.out.println("Fila: " + contadorLineas);
                    break; // Saltar las lineas con formato incorrecto
                }
                else if (Integer.parseInt(values[0]) <= 0) {
                    System.out.println("ID de zona comercial no válido (debe ser positivo): " + values[0]);
                    System.out.println("Fila: " + contadorLineas);
                    break; // Saltar las lineas con ID de zona comercial no válido
                }
                else if (Double.parseDouble(values[4]) < 0) {
                    System.out.println("Descuento máximo no válido (no puede ser negativo): " + values[4]);
                    System.out.println("Fila: " + contadorLineas);
                    break; // Saltar las lineas con descuento máximo negativo
                }
                else if (values[1].trim().isEmpty()) {
                    System.out.println("Nombre de zona comercial no válido (no puede estar vacío): " + values[1]);
                    System.out.println("Fila: " + contadorLineas);
                    break; // Saltar las lineas con nombre de zona comercial vacío
                }
                else if (values[2].trim().isEmpty()) {
                    System.out.println("Región de zona comercial no válida (no puede estar vacía): " + values[2]);
                    System.out.println("Fila: " + contadorLineas);
                    break; // Saltar las lineas con región de zona comercial vacía
                }
                else if (values[3].trim().isEmpty()) {
                    System.out.println("País de zona comercial no válido (no puede estar vacío): " + values[3]);
                    System.out.println("Fila: " + contadorLineas);
                    break; // Saltar las lineas con país de zona comercial vacío
                }
                else {
                    ZonaComercial zona = new ZonaComercial(
                        Integer.parseInt(values[0].trim()), 
                        values[1].trim(), 
                        values[2].trim(),
                        values[3].trim(), 
                        Double.parseDouble(values[4].trim())
                    );
                    zonasComerciales.add(zona);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return zonasComerciales;
    }


    public List<ReglaMargen> importCSVReglasMargen(String filePath) {
        List<ReglaMargen> reglasMargen = new ArrayList<>();
        int contadorLineas = 0;
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                contadorLineas++;
                if (line.trim().isEmpty()) continue; // Salta las líneas vacías
                String[] values = line.split(",");
                if (values.length != 5) {
                    System.out.println("Línea con formato incorrecto: " + line);
                    System.out.println("Fila: " + contadorLineas);
                    break; // Salta las líneas mal formateadas
                }
                else if (Integer.parseInt(values[0]) <= 0) {
                    System.out.println("ID de regla de margen no válido (debe ser positivo): " + values[0]);
                    System.out.println("Fila: " + contadorLineas);
                    break; // Salta las líneas con ID de regla de margen no válido
                }
                else if (Double.parseDouble(values[2]) < 0) {
                    System.out.println("Margen mínimo no válido (no puede ser negativo): " + values[2]);
                    System.out.println("Fila: " + contadorLineas);
                    break; // Salta las líneas con margen mínimo negativo
                }
                else if (values[1].trim().isEmpty()) {
                    System.out.println("Categoría de producto afectada no válida (no puede estar vacía): " + values[1]);
                    System.out.println("Fila: " + contadorLineas);
                    break; // Salta las líneas con categoría de producto afectada vacía
                }
                else if (values[4].trim().isEmpty()) {
                    System.out.println("Descripción de regla de margen no válida (no puede estar vacía): " + values[4]);
                    System.out.println("Fila: " + contadorLineas);
                    break; // Salta las líneas con descripción de regla de margen vacía
                }
                else {
                    ReglaMargen regla = new ReglaMargen(
                        Integer.parseInt(values[0].trim()), 
                        values[1].trim(), 
                        Double.parseDouble(values[2].trim()),
                        Boolean.parseBoolean(values[3].trim()), 
                        values[4].trim()
                    );
                    reglasMargen.add(regla);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return reglasMargen;
    }
}
