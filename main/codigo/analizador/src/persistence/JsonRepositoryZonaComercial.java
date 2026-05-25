package persistence;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;

import model.ZonaComercial;

public class JsonRepositoryZonaComercial {
    private static final Path DATA_DIRECTORY = Paths.get("data");
    private static final Path FILE_PATH = DATA_DIRECTORY.resolve("zonas.json");
    private final ObjectMapper mapper = new ObjectMapper();
    private final List<ZonaComercial> zonasComerciales;

    public JsonRepositoryZonaComercial() {
        this.zonasComerciales = cargarDesdeFichero();
        ordenarPorId();
    }

    //CRUD 

    public List<ZonaComercial> findAll() {
        return new ArrayList<>(zonasComerciales);
    }

    public ZonaComercial findById(String id) {
        Integer idParseado = parseId(id);
        if (idParseado == null) {
            return null;
        }
        return zonasComerciales.stream()
                .filter(z -> idParseado.equals(z.getId()))
                .findFirst()
                .orElse(null);
    }

    public void save(ZonaComercial zona) {
        // Validar antes de guardar
        if (zona == null) {
            throw new IllegalArgumentException("La zona comercial no puede ser nula");
        }
        if (zona.getId() == null || zona.getId() <= 0) {
            throw new IllegalArgumentException("El ID de zona debe ser un número positivo");
        }
        if (zona.getNombre() == null || zona.getNombre().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre de zona no puede estar vacío");
        }
        if (zona.getObjetivoFacturacionAnual() < 0) {
            throw new IllegalArgumentException("El objetivo de facturación no puede ser negativo");
        }
        
        // Si ya existe, la reemplaza; si no, la añade
        zonasComerciales.removeIf(z -> z.getId().equals(zona.getId()));
        zonasComerciales.add(zona);
        guardarEnFichero();
    }

    public boolean delete(String id) {
        Integer idParseado = parseId(id);
        if (idParseado == null) {
            return false;
        }
        boolean eliminado = zonasComerciales.removeIf(z -> idParseado.equals(z.getId()));
        if (eliminado) guardarEnFichero();
        return eliminado;
    }

    // Lectura / Escritura JSON 

    private List<ZonaComercial> cargarDesdeFichero() {
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
            List<ZonaComercial> leidas = mapper.readValue(FILE_PATH.toFile(), new TypeReference<List<ZonaComercial>>() {});
            // Eliminar duplicados por ID manteniendo el primero encontrado
            java.util.Map<Integer, ZonaComercial> porId = new java.util.LinkedHashMap<>();
            for (ZonaComercial z : leidas) {
                if (z == null || z.getId() == null) continue;
                porId.putIfAbsent(z.getId(), z);
            }
            return new ArrayList<>(porId.values());
        } catch (IOException e) {
            System.err.println("Error al leer zonas.json: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    private void guardarEnFichero() {
        try {
            Files.createDirectories(DATA_DIRECTORY);
            ordenarPorId();
            mapper.writerWithDefaultPrettyPrinter().writeValue(FILE_PATH.toFile(), zonasComerciales);
        } catch (IOException e) {
            System.err.println("Error al guardar zonas.json: " + e.getMessage());
        }
    }

    private void ordenarPorId() {
        zonasComerciales.sort(Comparator.comparing(ZonaComercial::getId, Comparator.nullsLast(Integer::compareTo)));
    }

    private Integer parseId(String id) {
        try {
            return Integer.valueOf(id);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
