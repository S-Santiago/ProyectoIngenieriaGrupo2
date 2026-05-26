package controller;

public enum RolUsuario {
    COMERCIAL,
    DIRECTOR_FINANCIERO;

    public boolean esComercial() {
        return this == COMERCIAL;
    }

    public boolean esDirectorFinanciero() {
        return this == DIRECTOR_FINANCIERO;
    }
}