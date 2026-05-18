package controller;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import model.ReglaMargen;
import model.ZonaComercial;
import persistence.CsvImporter;
import persistence.JsonRepositoryReglaMargen;
import persistence.JsonRepositoryZonaComercial;

public class RentabilidadController {
    private final JsonRepositoryZonaComercial repoZonas = new JsonRepositoryZonaComercial();
    private final JsonRepositoryReglaMargen repoReglas = new JsonRepositoryReglaMargen();
    private final CsvImporter csvImporter = new CsvImporter();

    public void cargarDatos() {
        //carga zona
        List<ZonaComercial>zonas=repoZonas.findAll();
        if(zonas.isEmpty()){
            System.out.println(" JSON de Zonas vacío  Cargando desde CSV...");
            if (Files.exists(Paths.get("data", "zonas.csv"))) {
                try {
                    List<ZonaComercial>zonaComercialsCSv=csvImporter.importCSVZonasComerciales("data/zonas.csv");
                    for(ZonaComercial z:zonaComercialsCSv){
                        //save a repozonas
                        repoZonas.save(z);
                    }
                } catch (Exception e) {
                    System.out.println("Aviso: no se pudo cargar data/zonas.csv. Se continuará con la lista vacía. Detalle: " + e.getMessage());
                }
            } else {
                System.out.println("Aviso: no existe data/zonas.csv. Se continuará con la lista vacía.");
            }
        }else {
            System.out.println("Zonas cargadas desde JSON correctamente.");
        }
    List<ReglaMargen> reglas = repoReglas.findAll();
        
        if (reglas.isEmpty()) {
            System.out.println("JSON de Reglas vacío. Cargando desde CSV...");
            if (Files.exists(Paths.get("data", "reglas.csv"))) {
                try {
                    List<ReglaMargen> reglasCSV = csvImporter.importCSVReglasMargen("data/reglas.csv");
                    for (ReglaMargen r : reglasCSV) {
                        repoReglas.save(r);
                    }
                } catch (Exception e) {
                    System.out.println("Aviso: no se pudo cargar data/reglas.csv. Se continuará con la lista vacía. Detalle: " + e.getMessage());
                }
            } else {
                System.out.println("Aviso: no existe data/reglas.csv. Se continuará con la lista vacía.");
            }
        } else {
            System.out.println("Reglas cargadas desde JSON correctamente.");
        }
    }
    //C
    public void addZona(Object obj) {
        if (!(obj instanceof ZonaComercial)) {
            System.out.println("Error: El objeto no es de la clase ZonaComercial");
            return;
        }
        ZonaComercial nuevaZona = (ZonaComercial) obj;
        repoZonas.save(nuevaZona);
        System.out.println("Zona guardada correctamente.");
    }

    public void addMarginRule(ReglaMargen nuevaRegla) {
        repoReglas.save(nuevaRegla);
        System.out.println("Regla de margen guardada correctamente.");
    }
    //R
    public List<ZonaComercial> readAllZones() {
        return repoZonas.findAll();
    }
    public List<ReglaMargen> readAllMarginRules() {
        return repoReglas.findAll();
    }
    // eleminar

    public void deleteZona(String id) {
        boolean exito = repoZonas.delete(id);
        
        if (exito) {
            System.out.println(" Zona comercial con ID " + id + " eliminada correctamente.");
        } else {
            System.out.println(" Error: No encontró la zona con ID " + id + ".");
        }
    }

    public void deleteReglaMargen(String id) {
        boolean exito = repoReglas.delete(id);
        
        if (exito) {
            System.out.println("Regla de margen con ID " + id + " eliminada correctamente.");
        } else {
            System.out.println("Error: No se encontró la regla con ID " + id + ".");
        }
    }
    //U
    public void updateZona(String id, String nuevoNombre, String nuevoPais, String nuevoResponsable, double nuevoObjetivo) {
        ZonaComercial zonaExistente = repoZonas.findById(id);

        if (zonaExistente == null) {
            System.out.println("Error: No  encontró la zona con ID " + id);
            return;
        }
        zonaExistente.setNombre(nuevoNombre);
        zonaExistente.setPais(nuevoPais);
        zonaExistente.setObjetivoFacturacionAnual(nuevoObjetivo);
        zonaExistente.setResponsableComercial(nuevoResponsable);

        repoZonas.save(zonaExistente);
        System.out.println("Zona actualizada correctamente.");
    }

    public void updateReglaMargen(String id, String nuevaCategoria, double nuevoMargen, boolean nuevaActiva, String nuevaDescripcion) {
        ReglaMargen reglaExistente = repoReglas.findById(id);

        if (reglaExistente == null) {
            System.out.println(" Error: No  encontró la regla con ID " + id);
            return;
        }
        reglaExistente.setMargenMinimoPortcentaje(nuevoMargen);
        reglaExistente.setCategoriaProductoAfectada(nuevaCategoria);
        reglaExistente.setActiva(nuevaActiva);
        reglaExistente.setDescripcion(nuevaDescripcion);

        repoReglas.save(reglaExistente);
        System.out.println("Regla de margen actualizada correctamente.");
    }

}

