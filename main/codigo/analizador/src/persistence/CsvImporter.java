package persistence;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import model.LineaPedido;
import model.ZonaComercial;
import model.ReglaMargen;
import model.estadoLineaPedido;

public class CsvImporter {
    public List<LineaPedido> importCsv(String filePath) {
        List<LineaPedido> lineasPedido = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue; // Skip empty lines
                else if (line.length() == 10) { 
                    String[] values = line.split(",");
                    LineaPedido linea = new LineaPedido(
                        Integer.parseInt(values[0]), 
                        Integer.parseInt(values[1]), 
                        values[2],
                        values[3], 
                        values[4],
                        Double.parseDouble(values[5]), 
                        Double.parseDouble(values[6]),
                        Integer.parseInt(values[7]), 
                        values[8], 
                        ZonaComercial.valueOf(values[9]),
                        estadoLineaPedido.valueOf(values[10])
                    );
                    lineasPedido.add(linea);
                }
                else if (line.length() == 5) { 
                    String[] values = line.split(",");
                    ZonaComercial linea = new ZonaComercial(
                        Integer.parseInt(values[0]), 
                        values[1], 
                        values[2],
                        values[3], 
                        Double.parseDouble(values[4])
                    );
                    lineasPedido.add(linea);
                }
                else if (line.length() == 5) { 
                    String[] values = line.split(",");
                    ReglaMargen linea = new ReglaMargen(
                        Integer.parseInt(values[0]), 
                        values[1], 
                        Double.parseDouble(values[2]),
                        Boolean.parseBoolean(values[3]), 
                        values[4]
                    );
                    lineasPedido.add(linea);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return lineasPedido;
    }
    
}
