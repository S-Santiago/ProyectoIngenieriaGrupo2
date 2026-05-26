package controller;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public final class LoginCredentialsValidator {

    private record Credenciales(String contrasena, RolUsuario rol, Integer zonaComercial) {
    }

    private static final Map<String, Credenciales> CREDENTIALS = new HashMap<>();

    static {
        CREDENTIALS.put("sfsanchez", new Credenciales("Agapito", RolUsuario.DIRECTOR_FINANCIERO, null));
        CREDENTIALS.put("hlopez", new Credenciales("Mondongo", RolUsuario.COMERCIAL, 2));
        CREDENTIALS.put("dfielding", new Credenciales("pichabrava3", RolUsuario.COMERCIAL, 1));
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

        return Optional.of(new SesionUsuario(usuario.trim(), credenciales.rol(), credenciales.zonaComercial()));
    }
}