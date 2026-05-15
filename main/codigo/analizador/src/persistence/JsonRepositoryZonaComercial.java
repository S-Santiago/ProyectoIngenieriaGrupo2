package persistence;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;

import model.ZonaComercial;

public class JsonRepositoryZonaComercial {
    private static final String FILE_PATH = "../resources/data/zonas.json";
    private final ObjectMapper mapper = new ObjectMapper();
    private List<ZonaComercial> zonasComerciales;

    public JsonRepositoryZonaComercial() {
        this.zonasComerciales = cargarDesdeFichero();
    }

    //CRUD 

    public List<ZonaComercial> findAll() {
        return new ArrayList<>(zonasComerciales);
    }

    public ZonaComercial findById(String id) {
        return zonasComerciales.stream()
                .filter(z -> z.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    public void save(ZonaComercial zona) {
        // Si ya existe, la reemplaza; si no, la añade
        zonasComerciales.removeIf(z -> z.getId().equals(zona.getId()));
        zonasComerciales.add(zona);
        guardarEnFichero();
    }

    public boolean delete(String id) {
        boolean eliminado = zonasComerciales.removeIf(z -> z.getId().equals(id));
        if (eliminado) guardarEnFichero();
        return eliminado;
    }

    // Lectura / Escritura JSON 

    private List<ZonaComercial> cargarDesdeFichero() {
        File fichero = new File(FILE_PATH);
        if (!fichero.exists()) {
            return new ArrayList<>(); // Primera vez: lista vacía
        }
        try {
            return mapper.readValue(fichero, new TypeReference<List<ZonaComercial>>() {});
        } catch (IOException e) {
            System.err.println("Error al leer zonas_comerciales.json: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    private void guardarEnFichero() {
        try {
            // Crea la carpeta data/ si no existe
            new File("data").mkdirs();
            // writerWithDefaultPrettyPrinter hace el JSON legible con indentación
            mapper.writerWithDefaultPrettyPrinter().writeValue(new File(FILE_PATH), zonasComerciales);
        } catch (IOException e) {
            System.err.println("Error al guardar zonas_comerciales.json: " + e.getMessage());
        }
    }
}
