package controller;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public final class LoginCredentialsValidator {

    private record Credenciales(String contrasena, RolUsuario rol, List<Integer> zonasComerciales) {
    }

    private static final Map<String, Credenciales> CREDENTIALS = new HashMap<>();

    static {
        // Intentar cargar usuarios desde resources/data/users.json
        ObjectMapper mapper = new ObjectMapper();
        try (InputStream is = LoginCredentialsValidator.class.getResourceAsStream("/data/users.json")) {
            if (is != null) {
                JsonNode root = mapper.readTree(is);
                if (root.isArray()) {
                    for (JsonNode n : root) {
                        String usuario = n.path("usuario").asText(null);
                        String contrasena = n.path("contrasena").asText(null);
                        String rolStr = n.path("rol").asText(null);

                        List<Integer> zonas = new ArrayList<>();
                        // Soportar nueva propiedad zonasComerciales (array) o la antigua zonaComercial (int)
                        if (n.hasNonNull("zonasComerciales") && n.get("zonasComerciales").isArray()) {
                            for (JsonNode z : n.get("zonasComerciales")) {
                                if (z != null && z.isInt()) {
                                    zonas.add(z.asInt());
                                }
                            }
                        } else if (n.hasNonNull("zonaComercial") && n.get("zonaComercial").isInt()) {
                            zonas.add(n.get("zonaComercial").asInt());
                        }

                        if (usuario != null && contrasena != null && rolStr != null) {
                            try {
                                RolUsuario rol = RolUsuario.valueOf(rolStr);
                                CREDENTIALS.put(usuario, new Credenciales(contrasena, rol, zonas));
                            } catch (IllegalArgumentException ex) {
                                // rol no válido, ignorar entrada
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Si no se cargaron credenciales desde JSON, usar valores por defecto (compatibilidad)
        if (CREDENTIALS.isEmpty()) {
            CREDENTIALS.put("sfsanchez", new Credenciales("Agapito", RolUsuario.DIRECTOR_FINANCIERO, List.of()));
            CREDENTIALS.put("hlopez", new Credenciales("Mondongo", RolUsuario.COMERCIAL, List.of(2)));
            CREDENTIALS.put("dfielding", new Credenciales("pichabrava3", RolUsuario.COMERCIAL, List.of(1)));
        }
    }

    private LoginCredentialsValidator() {
    }

    public static boolean validar(String usuario, String contrasena) {
        return autenticar(usuario, contrasena).isPresent();
    }

    public static Optional<SesionUsuario> autenticar(String usuario, String contrasena) {
        if (usuario == null || contrasena == null) {
            return Optional.empty();
        }

        Credenciales credenciales = CREDENTIALS.get(usuario.trim());
        if (credenciales == null || !contrasena.equals(credenciales.contrasena())) {
            return Optional.empty();
        }

        return Optional.of(new SesionUsuario(usuario.trim(), credenciales.rol(), credenciales.zonasComerciales()));
    }
}