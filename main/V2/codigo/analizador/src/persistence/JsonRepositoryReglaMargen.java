package persistence;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.core.type.TypeReference;

import model.ReglaMargen;

public class JsonRepositoryReglaMargen {
    private static final Path DATA_DIRECTORY = Paths.get("src", "resources", "data");
    private static final Path FILE_PATH = DATA_DIRECTORY.resolve("reglas.json");
        private final ObjectMapper mapper = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    private final List<ReglaMargen> reglas;

    public JsonRepositoryReglaMargen() {
        this.reglas = cargarDesdeFichero();
        ordenarPorId();
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
        // Validar antes de guardar
        if (regla == null) {
            throw new IllegalArgumentException("La regla de margen no puede ser nula");
        }
        if (regla.getId() == null || regla.getId() <= 0) {
            throw new IllegalArgumentException("El ID de regla debe ser un número positivo");
        }
        if (regla.getCategoriaProductoAfectada() == null || regla.getCategoriaProductoAfectada().trim().isEmpty()) {
            throw new IllegalArgumentException("La categoría de producto no puede estar vacía");
        }
        if (regla.getMargenMinimoPortcentaje() < 0 || regla.getMargenMinimoPortcentaje() > 100) {
            throw new IllegalArgumentException("El margen mínimo debe estar entre 0 y 100%");
        }
        
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
            System.err.println("No se pudo crear el directorio src/resources/data: " + e.getMessage());
            return new ArrayList<>();
        }

        // Si existe el fichero en disco, leerlo
        if (Files.exists(FILE_PATH)) {
            try {
                List<ReglaMargen> desdeDisco = mapper.readValue(FILE_PATH.toFile(), new TypeReference<List<ReglaMargen>>() {});
                System.out.println("[JsonRepositoryReglaMargen] Leídas " + desdeDisco.size() + " reglas desde disco: " + FILE_PATH.toAbsolutePath());
                return desdeDisco;
            } catch (IOException e) {
                System.err.println("Error al leer reglas.json desde disco (lectura directa): " + e.getMessage());
                // Intentar leer como lista de mapas y convertir campos alternativos
                try {
                    List<java.util.Map<String, Object>> raw = mapper.readValue(FILE_PATH.toFile(), new TypeReference<List<java.util.Map<String, Object>>>() {});
                    List<ReglaMargen> convertidas = new ArrayList<>();
                    for (java.util.Map<String, Object> m : raw) {
                        if (m == null) continue;
                        Integer id = null;
                        Object idObj = m.getOrDefault("id", m.get("idRegla"));
                        if (idObj instanceof Number) id = ((Number) idObj).intValue();
                        else if (idObj instanceof String) {
                            try { id = Integer.valueOf((String) idObj); } catch (NumberFormatException ex) {}
                        }

                        String categoria = (String) m.getOrDefault("categoriaProductoAfectada", m.get("nombre"));
                        Double margen = null;
                        Object margenObj = m.getOrDefault("margenMinimoPortcentaje", m.get("margenMinimo"));
                        if (margenObj instanceof Number) margen = ((Number) margenObj).doubleValue();
                        else if (margenObj instanceof String) {
                            try { margen = Double.valueOf((String) margenObj); } catch (NumberFormatException ex) {}
                        }
                        if (margen != null && margen <= 1.0) {
                            margen = margen * 100.0;
                        }

                        boolean activa = false;
                        Object actObj = m.getOrDefault("activa", m.get("activo"));
                        if (actObj instanceof Boolean) activa = (Boolean) actObj;
                        else if (actObj instanceof Number) activa = ((Number) actObj).intValue() != 0;
                        else if (actObj instanceof String) activa = Boolean.parseBoolean((String) actObj);

                        String descripcion = (String) m.getOrDefault("descripcion", m.get("descripcion"));

                        ReglaMargen r = new ReglaMargen();
                        r.setId(id);
                        r.setCategoriaProductoAfectada(categoria);
                        if (margen != null) r.setMargenMinimoPortcentaje(margen);
                        r.setActiva(activa);
                        r.setDescripcion(descripcion);
                        convertidas.add(r);
                    }
                    try {
                        mapper.writerWithDefaultPrettyPrinter().writeValue(FILE_PATH.toFile(), convertidas);
                    } catch (IOException ex) {
                        System.err.println("No se pudo escribir reglas.json convertido en src/resources/data: " + ex.getMessage());
                    }
                    System.out.println("[JsonRepositoryReglaMargen] Convertidas y leídas " + convertidas.size() + " reglas desde disco (formato alternativo)");
                    return convertidas;
                } catch (IOException ex2) {
                    System.err.println("Error al convertir reglas.json desde disco: " + ex2.getMessage());
                    return new ArrayList<>();
                }
            }
        }
        // Si no existe en disco, devolver vacío (no hacemos fallback a classpath en V2)
        System.out.println("Aviso: el JSON de reglas está vacío o no existe.");
        return new ArrayList<>();
    }

    private void guardarEnFichero() {
        try {
            Files.createDirectories(DATA_DIRECTORY);
            ordenarPorId();
            mapper.writerWithDefaultPrettyPrinter().writeValue(FILE_PATH.toFile(), reglas);
        } catch (IOException e) {
            System.err.println("Error al guardar reglas.json en src/resources/data: " + e.getMessage());
        }
    }

    private void ordenarPorId() {
        reglas.sort(Comparator.comparing(ReglaMargen::getId, Comparator.nullsLast(Comparator.naturalOrder())));
    }

    private Integer parseId(String id) {
        try {
            return Integer.valueOf(id);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
