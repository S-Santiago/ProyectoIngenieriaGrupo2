package controller;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

class LoginCredentialsValidatorTest {

    @Test
    void aceptaCredencialesValidas() {
        assertTrue(LoginCredentialsValidator.validar("sfsanchez", "Agapito"));
        assertTrue(LoginCredentialsValidator.validar("hlopez", "Mondongo"));
        assertTrue(LoginCredentialsValidator.validar("dfielding", "pichabrava3"));

        SesionUsuario sesionComercial = LoginCredentialsValidator.autenticar("sfsanchez", "Agapito").orElseThrow();
        assertEquals(RolUsuario.COMERCIAL, sesionComercial.rol());
        assertEquals(1, sesionComercial.zonaComercial());

        SesionUsuario sesionComercialZonaDos = LoginCredentialsValidator.autenticar("hlopez", "Mondongo").orElseThrow();
        assertEquals(RolUsuario.COMERCIAL, sesionComercialZonaDos.rol());
        assertEquals(2, sesionComercialZonaDos.zonaComercial());

        SesionUsuario sesionDirector = LoginCredentialsValidator.autenticar("dfielding", "pichabrava3").orElseThrow();
        assertEquals(RolUsuario.DIRECTOR_FINANCIERO, sesionDirector.rol());
        assertNull(sesionDirector.zonaComercial());
    }

    @Test
    void rechazaCredencialesInvalidas() {
        assertFalse(LoginCredentialsValidator.validar("sfsanchez", "otra"));
        assertFalse(LoginCredentialsValidator.validar("desconocido", "Agapito"));
        assertFalse(LoginCredentialsValidator.validar(null, "Agapito"));
        assertFalse(LoginCredentialsValidator.validar("sfsanchez", null));
    }
}