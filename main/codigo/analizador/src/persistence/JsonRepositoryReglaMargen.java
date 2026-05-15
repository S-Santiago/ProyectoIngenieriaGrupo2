package persistence;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;

import model.ReglaMargen;

public class JsonRepositoryReglaMargen {
    private static final String FILE_PATH = "../resources/data/reglas.json";
    private final ObjectMapper mapper = new ObjectMapper();
    private List<ReglaMargen> reglas;

    public JsonRepositoryReglaMargen() {
        this.reglas = cargarDesdeFichero();
    }

    // CRUD 

    public List<ReglaMargen> findAll() {
        return new ArrayList<>(reglas);
    }

    public ReglaMargen findById(String id) {
        return reglas.stream()
                .filter(r -> r.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    public void save(ReglaMargen regla) {
        reglas.removeIf(r -> r.getId().equals(regla.getId()));
        reglas.add(regla);
        guardarEnFichero();
    }

    public boolean delete(String id) {
        boolean eliminado = reglas.removeIf(r -> r.getId().equals(id));
        if (eliminado) guardarEnFichero();
        return eliminado;
    }

    //Lectura / Escritura JSON

    private List<ReglaMargen> cargarDesdeFichero() {
        File fichero = new File(FILE_PATH);
        if (!fichero.exists()) {
            return new ArrayList<>();
        }
        try {
            return mapper.readValue(fichero, new TypeReference<List<ReglaMargen>>() {});
        } catch (IOException e) {
            System.err.println("Error al leer reglas_margen.json: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    private void guardarEnFichero() {
        try {
            new File("data").mkdirs();
            mapper.writerWithDefaultPrettyPrinter().writeValue(new File(FILE_PATH), reglas);
        } catch (IOException e) {
            System.err.println("Error al guardar reglas_margen.json: " + e.getMessage());
        }
    }
}
