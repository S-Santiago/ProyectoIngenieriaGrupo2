package controller;

import java.util.List;

public record SesionUsuario(String usuario, RolUsuario rol, List<Integer> zonasComerciales) {

    public boolean esComercial() {
        return rol != null && rol.esComercial();
    }

    /**
     * Compatibilidad: devuelve la primera zona asignada (si existe).
     */
    public Integer zonaComercial() {
        return zonasComerciales == null || zonasComerciales.isEmpty() ? null : zonasComerciales.get(0);
    }

    public List<Integer> zonasComerciales() {
        return zonasComerciales == null ? List.of() : List.copyOf(zonasComerciales);
    }

    public boolean tieneZona(Integer idZona) {
        return idZona != null && zonasComerciales != null && zonasComerciales.contains(idZona);
    }

    public boolean esDirectorFinanciero() {
        return rol != null && rol.esDirectorFinanciero();
    }

    @Override
    public String toString() {
        return "SesionUsuario[usuario=" + usuario + ", rol=" + rol + ", zonas=" + (zonasComerciales == null ? "[]" : zonasComerciales.toString()) + "]";
    }
}