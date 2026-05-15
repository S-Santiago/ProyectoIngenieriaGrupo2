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

public class RentabilidadController {
    List<ZonaComercial>listZonaComercials;
    List<ReglaMargen>listadReglaMargens;
       private final String PATH_ZONAS = "data/zonas.json";
        private final String PATH_REGLAS = "data/reglas.json";
    private JsonRepository jsonRepo = new JsonRepository();
    private  CsvImporter csvImporter=new CsvImporter();

    public void cargarDatos() {
        try {
            File file = new File(PATH_ZONAS);
            if (file.exists()) {
               ZonaComercial[]arrylisaComercials=jsonRepo.loadFromJson(PATH_ZONAS, ZonaComercial[].class);
                this.listZonaComercials=new ArrayList<>(Arrays.asList(arrylisaComercials));
            } else {
                // si no hay json,cargar csv
                this.listZonaComercials=csvImporter.importCSVZonasComerciales("data/zonas.csv");
            }
            File fileRegarademargen=new File(PATH_REGLAS);
            if (fileRegarademargen.exists()) {
               ReglaMargen[]arryreglademargens=jsonRepo.loadFromJson(PATH_REGLAS, ReglaMargen[].class);
                this.listadReglaMargens=new ArrayList<>(Arrays.asList(arryreglademargens));
            } else {
                // si no hay json,cargar csv
                this.listadReglaMargens=csvImporter.importCSVReglasMargen("data/regla.csv");
            }
        } catch (IOException e) {
            System.out.println("....");
        }
    }
    //1.Crear zona, Crear regla de margen
    public void addzona(Object obj){
        if(!(obj instanceof ZonaComercial)){
            System.out.println("el objeto no es un clase de zona zonacomercial");
            return;
        }
        ZonaComercial nuevZonaComercial=(ZonaComercial)obj;
        if(this.listZonaComercials.contains(nuevZonaComercial)){
            System.out.println("");
        }else try{
            listZonaComercials.add(nuevZonaComercial);
            jsonRepo.saveToJson(PATH_ZONAS,this.listZonaComercials);
        }catch(IOException e){
            System.out.println("No se puede guardar"+e.getMessage());
        }
    }
    public void addreglademargen(ReglaMargen nuevaregReglaMargen){
        this.listadReglaMargens.add(nuevaregReglaMargen);
        try{
            jsonRepo.saveToJson(PATH_REGLAS,this.listadReglaMargens);
        }catch(IOException e){
            System.out.println("No se puede guardar"+e.getMessage());
        }
    }
    public List<ZonaComercial> ReadTodaszona(){
        return listZonaComercials;
    }
}
