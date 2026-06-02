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

import model.ZonaComercial;

public class JsonRepositoryZonaComercial {
    private static final Path DATA_DIRECTORY = Paths.get("src", "resources", "data");
    private static final Path FILE_PATH = DATA_DIRECTORY.resolve("zonas.json");
        private final ObjectMapper mapper = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
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
            System.err.println("No se pudo crear el directorio src/resources/data: " + e.getMessage());
            return new ArrayList<>();
        }

        // Si existe el fichero en disco, leerlo
        if (Files.exists(FILE_PATH)) {
            try {
                List<ZonaComercial> leidas = mapper.readValue(FILE_PATH.toFile(), new TypeReference<List<ZonaComercial>>() {});
                // Si la lectura directa produjo objetos sin ID (por ejemplo porque el JSON usa "idZona"),
                // intentaremos una conversión alternativa leyendo como mapas.
                boolean anyIdPresent = false;
                for (ZonaComercial z : leidas) {
                    if (z != null && z.getId() != null) { anyIdPresent = true; break; }
                }

                if (!anyIdPresent && !leidas.isEmpty()) {
                    // Intentar conversión alternativa desde el JSON crudo
                    try {
                        List<java.util.Map<String, Object>> raw = mapper.readValue(FILE_PATH.toFile(), new TypeReference<List<java.util.Map<String, Object>>>() {});
                        List<ZonaComercial> conv = new ArrayList<>();
                        for (java.util.Map<String, Object> m : raw) {
                            if (m == null) continue;
                            Integer id = null;
                            Object idObj = m.getOrDefault("id", m.get("idZona"));
                            if (idObj instanceof Number) id = ((Number) idObj).intValue();
                            else if (idObj instanceof String) {
                                try { id = Integer.valueOf((String) idObj); } catch (NumberFormatException ex) {}
                            }

                            String nombre = (String) m.getOrDefault("nombre", m.get("nombreZona"));
                            String pais = (String) m.getOrDefault("pais", "");
                            String responsable = (String) m.getOrDefault("responsableComercial", m.get("responsable"));
                            Double objetivo = null;
                            Object objObj = m.getOrDefault("objetivoFacturacionAnual", m.get("objetivo"));
                            if (objObj instanceof Number) objetivo = ((Number) objObj).doubleValue();
                            else if (objObj instanceof String) {
                                try { objetivo = Double.valueOf((String) objObj); } catch (NumberFormatException ex) {}
                            }

                            ZonaComercial z = new ZonaComercial();
                            z.setId(id);
                            z.setNombre(nombre);
                            z.setPais(pais != null ? pais : "");
                            z.setResponsableComercial(responsable != null ? responsable : "");
                            z.setObjetivoFacturacionAnual(objetivo != null ? objetivo : 0);
                            conv.add(z);
                        }
                        try {
                            mapper.writerWithDefaultPrettyPrinter().writeValue(FILE_PATH.toFile(), conv);
                        } catch (IOException ex) {
                            System.err.println("No se pudo escribir zonas.json convertido en src/resources/data: " + ex.getMessage());
                        }
                        System.out.println("[JsonRepositoryZonaComercial] Convertidas y leídas " + conv.size() + " zonas desde disco (formato alternativo)");
                        return conv;
                    } catch (IOException ex2) {
                        System.err.println("Error al convertir zonas.json desde disco (fase alternativa): " + ex2.getMessage());
                        return new ArrayList<>();
                    }
                }

                // Eliminar duplicados por ID manteniendo el primero encontrado
                java.util.Map<Integer, ZonaComercial> porId = new java.util.LinkedHashMap<>();
                for (ZonaComercial z : leidas) {
                    if (z == null || z.getId() == null) continue;
                    porId.putIfAbsent(z.getId(), z);
                }
                List<ZonaComercial> resultado = new ArrayList<>(porId.values());
                System.out.println("[JsonRepositoryZonaComercial] Leídas " + resultado.size() + " zonas desde disco: " + FILE_PATH.toAbsolutePath());
                return resultado;
            } catch (IOException e) {
                System.err.println("Error al leer zonas.json desde disco (lectura directa): " + e.getMessage());
                // Intentar leer como lista de mapas y convertir campos alternativos
                try {
                    List<java.util.Map<String, Object>> raw = mapper.readValue(FILE_PATH.toFile(), new TypeReference<List<java.util.Map<String, Object>>>() {});
                    List<ZonaComercial> conv = new ArrayList<>();
                    for (java.util.Map<String, Object> m : raw) {
                        if (m == null) continue;
                        Integer id = null;
                        Object idObj = m.getOrDefault("id", m.get("idZona"));
                        if (idObj instanceof Number) id = ((Number) idObj).intValue();
                        else if (idObj instanceof String) {
                            try { id = Integer.valueOf((String) idObj); } catch (NumberFormatException ex) {}
                        }

                        String nombre = (String) m.getOrDefault("nombre", m.get("nombreZona"));
                        String pais = (String) m.getOrDefault("pais", "");
                        String responsable = (String) m.getOrDefault("responsableComercial", m.get("responsable"));
                        Double objetivo = null;
                        Object objObj = m.getOrDefault("objetivoFacturacionAnual", m.get("objetivo"));
                        if (objObj instanceof Number) objetivo = ((Number) objObj).doubleValue();
                        else if (objObj instanceof String) {
                            try { objetivo = Double.valueOf((String) objObj); } catch (NumberFormatException ex) {}
                        }

                        ZonaComercial z = new ZonaComercial();
                        z.setId(id);
                        z.setNombre(nombre);
                        z.setPais(pais != null ? pais : "");
                        z.setResponsableComercial(responsable != null ? responsable : "");
                        z.setObjetivoFacturacionAnual(objetivo != null ? objetivo : 0);
                        conv.add(z);
                    }
                    try {
                        mapper.writerWithDefaultPrettyPrinter().writeValue(FILE_PATH.toFile(), conv);
                    } catch (IOException ex) {
                        System.err.println("No se pudo escribir zonas.json convertido en src/resources/data: " + ex.getMessage());
                    }
                    System.out.println("[JsonRepositoryZonaComercial] Convertidas y leídas " + conv.size() + " zonas desde disco (formato alternativo)");
                    return conv;
                } catch (IOException ex2) {
                    System.err.println("Error al convertir zonas.json desde disco: " + ex2.getMessage());
                    return new ArrayList<>();
                }
            }
        }
        // Si no existe en disco, devolver vacío (no hacemos fallback a classpath en V2)
        System.out.println("Aviso: el JSON de zonas está vacío o no existe.");
        return new ArrayList<>();
    }

    private void guardarEnFichero() {
        try {
            Files.createDirectories(DATA_DIRECTORY);
            ordenarPorId();
            mapper.writerWithDefaultPrettyPrinter().writeValue(FILE_PATH.toFile(), zonasComerciales);
        } catch (IOException e) {
            System.err.println("Error al guardar zonas.json en src/resources/data: " + e.getMessage());
        }
    }

    private void ordenarPorId() {
        zonasComerciales.sort(Comparator.comparing(ZonaComercial::getId, Comparator.nullsLast(Comparator.naturalOrder())));
    }

    private Integer parseId(String id) {
        try {
            return Integer.valueOf(id);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
