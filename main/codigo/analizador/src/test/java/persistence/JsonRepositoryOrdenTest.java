package persistence;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import model.ReglaMargen;
import model.ZonaComercial;

class JsonRepositoryOrdenTest {

    @TempDir
    Path tempDir;

    @Test
    void guardaZonasOrdenadasPorId() {
        withWorkingDirectory(() -> {
            JsonRepositoryZonaComercial repository = new JsonRepositoryZonaComercial();

            repository.save(new ZonaComercial(20, "Zona C", "España", "Responsable C", 3000));
            repository.save(new ZonaComercial(5, "Zona A", "España", "Responsable A", 1000));
            repository.save(new ZonaComercial(12, "Zona B", "España", "Responsable B", 2000));

            List<Integer> ids = repository.findAll().stream()
                    .map(ZonaComercial::getId)
                    .toList();

            assertTrue(estaOrdenadaAscendente(ids));
            assertTrue(ids.indexOf(5) < ids.indexOf(12));
            assertTrue(ids.indexOf(12) < ids.indexOf(20));
        });
    }

    @Test
    void guardaReglasOrdenadasPorId() {
        withWorkingDirectory(() -> {
            JsonRepositoryReglaMargen repository = new JsonRepositoryReglaMargen();

            repository.save(new ReglaMargen(20, "Categoria C", 30, true, "Regla C"));
            repository.save(new ReglaMargen(5, "Categoria A", 10, true, "Regla A"));
            repository.save(new ReglaMargen(12, "Categoria B", 20, false, "Regla B"));

            List<Integer> ids = repository.findAll().stream()
                    .map(ReglaMargen::getId)
                    .toList();

            assertTrue(estaOrdenadaAscendente(ids));
            assertTrue(ids.indexOf(5) < ids.indexOf(12));
            assertTrue(ids.indexOf(12) < ids.indexOf(20));
        });
    }

    private void withWorkingDirectory(Runnable action) {
        String originalUserDir = System.getProperty("user.dir");
        try {
            System.setProperty("user.dir", tempDir.toAbsolutePath().toString());
            action.run();
        } finally {
            if (originalUserDir != null) {
                System.setProperty("user.dir", originalUserDir);
            }
        }
    }

    private boolean estaOrdenadaAscendente(List<Integer> ids) {
        for (int i = 1; i < ids.size(); i++) {
            if (ids.get(i - 1) > ids.get(i)) {
                return false;
            }
        }
        return true;
    }
}