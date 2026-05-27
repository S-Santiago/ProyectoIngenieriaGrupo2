package view;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.prefs.Preferences;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

class ConsolaErroresDialogTest {

    private final Preferences preferencias = Preferences.userNodeForPackage(ConsolaErroresDialog.class);

    @AfterEach
    void limpiarPreferencias() {
        ConsolaErroresDialog.reiniciarNoVolverAMostrarAnalisis();
    }

    @Test
    void reiniciarNoVolverAMostrarAnalisisEliminaLasDosClaves() {
        preferencias.putBoolean(clave(ConsolaErroresDialog.CLAVE_ANALISIS_MARGEN), true);
        preferencias.putBoolean(clave(ConsolaErroresDialog.CLAVE_DESVIACIONES_ZONAS), true);

        ConsolaErroresDialog.reiniciarNoVolverAMostrarAnalisis();

        assertFalse(preferencias.getBoolean(clave(ConsolaErroresDialog.CLAVE_ANALISIS_MARGEN), false));
        assertFalse(preferencias.getBoolean(clave(ConsolaErroresDialog.CLAVE_DESVIACIONES_ZONAS), false));
    }

    @Test
    void reiniciarNoVolverAMostrarEliminaUnaClaveConcreta() {
        preferencias.putBoolean(clave(ConsolaErroresDialog.CLAVE_ANALISIS_MARGEN), true);
        preferencias.putBoolean(clave(ConsolaErroresDialog.CLAVE_DESVIACIONES_ZONAS), true);

        ConsolaErroresDialog.reiniciarNoVolverAMostrar(ConsolaErroresDialog.CLAVE_ANALISIS_MARGEN);

        assertFalse(preferencias.getBoolean(clave(ConsolaErroresDialog.CLAVE_ANALISIS_MARGEN), false));
        assertTrue(preferencias.getBoolean(clave(ConsolaErroresDialog.CLAVE_DESVIACIONES_ZONAS), false));
    }

    private String clave(String sufijo) {
        return "no_mostrar_" + sufijo;
    }
}
