package persistence;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;

import model.ReglaMargen;

public class JsonRepositoryReglaMargen {
    private static final Path DATA_DIRECTORY = Paths.get("data");
    private static final Path FILE_PATH = DATA_DIRECTORY.resolve("reglas.json");
    private final ObjectMapper mapper = new ObjectMapper();
    private final List<ReglaMargen> reglas;

    public JsonRepositoryReglaMargen() {
        this.reglas = cargarDesdeFichero();
    }

    // CRUD 

    public List<ReglaMargen> findAll() {
        return new ArrayList<>(reglas);
    }

    public ReglaMargen findById(String id) {
        Integer idParseado = parseId(id);
        if (idParseado == null) {
            return null;
        }
        return reglas.stream()
                .filter(r -> idParseado.equals(r.getId()))
                .findFirst()
                .orElse(null);
    }

    public void save(ReglaMargen regla) {
        reglas.removeIf(r -> regla.getId().equals(r.getId()));
        reglas.add(regla);
        guardarEnFichero();
    }

    public boolean delete(String id) {
        Integer idParseado = parseId(id);
        if (idParseado == null) {
            return false;
        }
        boolean eliminado = reglas.removeIf(r -> idParseado.equals(r.getId()));
        if (eliminado) guardarEnFichero();
        return eliminado;
    }

    //Lectura / Escritura JSON

    private List<ReglaMargen> cargarDesdeFichero() {
        try {
            Files.createDirectories(DATA_DIRECTORY);
        } catch (IOException e) {
            System.err.println("No se pudo crear el directorio data: " + e.getMessage());
            return new ArrayList<>();
        }

        if (!Files.exists(FILE_PATH)) {
            return new ArrayList<>();
        }

        try {
            return mapper.readValue(FILE_PATH.toFile(), new TypeReference<List<ReglaMargen>>() {});
        } catch (IOException e) {
            System.err.println("Error al leer reglas.json: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    private void guardarEnFichero() {
        try {
            Files.createDirectories(DATA_DIRECTORY);
            mapper.writerWithDefaultPrettyPrinter().writeValue(FILE_PATH.toFile(), reglas);
        } catch (IOException e) {
            System.err.println("Error al guardar reglas.json: " + e.getMessage());
        }
    }

    private Integer parseId(String id) {
        try {
            return Integer.valueOf(id);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
