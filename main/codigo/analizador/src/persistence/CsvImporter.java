package persistence;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import model.LineaPedido;
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
                else if (line.startsWith("idLinea") || line.startsWith("idPedido") || line.startsWith("unidades") || line.startsWith("costeUnitario") || line.startsWith("precioVentaUnitario") || line.startsWith("fechaPedido") || line.startsWith("categoria") || line.startsWith("zonaComercial") || line.startsWith("estado") || line.startsWith("referenciaProducto")) continue; // Salta la cabecera{ 
                else {
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
        } 
        catch (IOException e) {
            e.printStackTrace();
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
        }
        finally {
            System.out.println("Número total de líneas procesadas: " + contadorLineas);
            System.out.println("Número total de líneas importadas: " + lineasPedido.size());
        }
        return lineasPedido;
    }
}
