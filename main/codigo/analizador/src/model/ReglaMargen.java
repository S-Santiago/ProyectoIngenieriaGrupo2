package model;

public class ReglaMargen {

    private int id;
    private String categoriaProductoAfectada;
    private double margenMinimoPortcentaje;
    private boolean activa;
    private String descripcion;

    public ReglaMargen() {
    }

    public ReglaMargen(int id, String categoriaProductoAfectada, double margenMinimoPortcentaje, boolean activa,
            String descripcion) {
        this.id = id;
        this.categoriaProductoAfectada = categoriaProductoAfectada;
        this.margenMinimoPortcentaje = margenMinimoPortcentaje;
        this.activa = activa;
        this.descripcion = descripcion;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCategoriaProductoAfectada() {
        return categoriaProductoAfectada;
    }

    public void setCategoriaProductoAfectada(String categoriaProductoAfectada) {
        this.categoriaProductoAfectada = categoriaProductoAfectada;
    }

    public double getMargenMinimoPortcentaje() {
        return margenMinimoPortcentaje;
    }

    public void setMargenMinimoPortcentaje(double margenMinimoPortcentaje) {
        this.margenMinimoPortcentaje = margenMinimoPortcentaje;
    }

    public boolean isActiva() {
        return activa;
    }

    public void setActiva(boolean activa) {
        this.activa = activa;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
}