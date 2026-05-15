package controller;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import model.ReglaMargen;
import model.ZonaComercial;
import persistence.CsvImporter;
import persistence.JsonRepository;
import persistence.JsonRepositoryReglaMargen;
import persistence.JsonRepositoryZonaComercial;

public class RentabilidadController {
    private JsonRepositoryZonaComercial repoZonas = new JsonRepositoryZonaComercial();
    private JsonRepositoryReglaMargen repoReglas = new JsonRepositoryReglaMargen();
    private CsvImporter csvImporter = new CsvImporter();

    public void cargarDatos() {
        //carga zona
        List<ZonaComercial>zonas=repoZonas.findAll();
        if(zonas.isEmpty()){
            System.out.println(" JSON de Zonas vacío  Cargando desde CSV...");
            //si no exite cargamos desde csv
            List<ZonaComercial>zonaComercialsCSv=csvImporter.importCSVZonasComerciales("data/zonas.csv");
            for(ZonaComercial z:zonaComercialsCSv){
                //save a repozonas
                repoZonas.save(z);
            }
        }else {
            System.out.println("Zonas cargadas desde JSON correctamente.");
        }
    List<ReglaMargen> reglas = repoReglas.findAll();
        
        if (reglas.isEmpty()) {
            System.out.println("JSON de Reglas vacío. Cargando desde CSV...");
            List<ReglaMargen> reglasCSV = csvImporter.importCSVReglasMargen("data/reglas.csv");
            for (ReglaMargen r : reglasCSV) {
                repoReglas.save(r);
            }
        } else {
            System.out.println("Reglas cargadas desde JSON correctamente.");
        }
    }
    //C
 public void addzona(Object obj) {
        if (!(obj instanceof ZonaComercial)) {
            System.out.println("Error: El objeto no es de la clase ZonaComercial");
            return;
        }
        ZonaComercial nuevaZona = (ZonaComercial) obj;
        repoZonas.save(nuevaZona);
        System.out.println("Zona guardada correctamente.");
    }

    public void addreglademargen(ReglaMargen nuevaRegla) {
        repoReglas.save(nuevaRegla);
        System.out.println("Regla de margen guardada correctamente.");
    }
    //R
    public List<ZonaComercial> ReadTodaszona() {
        return repoZonas.findAll();
    }
    public  List<ReglaMargen>ReadTOdosmargen(){
        return  repoReglas.findAll();
    }
    // eleminar

    public void deleteZona(String id) {
        boolean exito = repoZonas.delete(id);
        
        if (exito) {
            System.out.println(" Zona comercial con ID " + id + " eliminada correctamente.");
        } else {
            System.out.println(" Error: No se encontró la zona con ID " + id + ".");
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
            System.out.println("Error: No se encontró la zona con ID " + id);
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
            System.out.println(" Error: No se encontró la regla con ID " + id);
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

