package controller;

public record SesionUsuario(String usuario, RolUsuario rol, Integer zonaComercial) {

    public boolean esComercial() {
        return rol != null && rol.esComercial();
    }

    public Integer getZonaId() {
        return zonaComercial;
    }

    public boolean esDirectorFinanciero() {
        return rol != null && rol.esDirectorFinanciero();
    }
}