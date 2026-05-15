package model;

public class ZonaComercial {

    private int id;
    private String nombre;
    private String pais;
    private String responsableComercial;
    private double objetivoFacturacionAnual;

    public ZonaComercial() {
    }

    public ZonaComercial(int id, String nombre, String pais, String responsableComercial,
            double objetivoFacturacionAnual) {
        this.id = id;
        this.nombre = nombre;
        this.pais = pais;
        this.responsableComercial = responsableComercial;
        this.objetivoFacturacionAnual = objetivoFacturacionAnual;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getPais() {
        return pais;
    }

    public void setPais(String pais) {
        this.pais = pais;
    }

    public String getResponsableComercial() {
        return responsableComercial;
    }

    public void setResponsableComercial(String responsableComercial) {
        this.responsableComercial = responsableComercial;
    }

    public double getObjetivoFacturacionAnual() {
        return objetivoFacturacionAnual;
    }

    public void setObjetivoFacturacionAnual(double objetivoFacturacionAnual) {
        this.objetivoFacturacionAnual = objetivoFacturacionAnual;
    }
}