package controller;

public final class SesionAplicacion {

    private static SesionUsuario sesionActual;

    private SesionAplicacion() {
    }

    public static void establecer(SesionUsuario sesionUsuario) {
        sesionActual = sesionUsuario;
    }

    public static SesionUsuario obtener() {
        return sesionActual;
    }

    public static void limpiar() {
        sesionActual = null;
    }
}