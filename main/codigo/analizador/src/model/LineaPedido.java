package model;

public class LineaPedido {

    private int idLinea;
    private int idPedido;
    private String referenciaProduto;
    private String descripcionProducto;
    private String categoria;
    private double costeUnitario;
    private double precioVentaUnitario;
    private int unidades;
    private String fechaPedido;
    private ZonaComercial zonaComercial;
    private EstadoPedido estado;

    public LineaPedido() {
    }

    public LineaPedido(int idLinea, int idPedido, String referenciaProduto, String descripcionProducto,
            String categoria, double costeUnitario, double precioVentaUnitario,
            int unidades, String fechaPedido, ZonaComercial zonaComercial, EstadoPedido estado) {
        this.idLinea = idLinea;
        this.idPedido = idPedido;
        this.referenciaProduto = referenciaProduto;
        this.descripcionProducto = descripcionProducto;
        this.categoria = categoria;
        this.costeUnitario = costeUnitario;
        this.precioVentaUnitario = precioVentaUnitario;
        this.unidades = unidades;
        this.fechaPedido = fechaPedido;
        this.zonaComercial = zonaComercial;
        this.estado = estado;
    }

    public int getIdLinea() {
        return idLinea;
    }

    public void setIdLinea(int idLinea) {
        this.idLinea = idLinea;
    }

    public int getIdPedido() {
        return idPedido;
    }

    public void setIdPedido(int idPedido) {
        this.idPedido = idPedido;
    }

    public String getReferenciaProduto() {
        return referenciaProduto;
    }

    public void setReferenciaProduto(String referenciaProduto) {
        this.referenciaProduto = referenciaProduto;
    }

    public String getDescripcionProducto() {
        return descripcionProducto;
    }

    public void setDescripcionProducto(String descripcionProducto) {
        this.descripcionProducto = descripcionProducto;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public double getCosteUnitario() {
        return costeUnitario;
    }

    public void setCosteUnitario(double costeUnitario) {
        this.costeUnitario = costeUnitario;
    }

    public double getPrecioVentaUnitario() {
        return precioVentaUnitario;
    }

    public void setPrecioVentaUnitario(double precioVentaUnitario) {
        this.precioVentaUnitario = precioVentaUnitario;
    }

    public int getUnidades() {
        return unidades;
    }

    public void setUnidades(int unidades) {
        this.unidades = unidades;
    }

    public String getFechaPedido() {
        return fechaPedido;
    }

    public void setFechaPedido(String fechaPedido) {
        this.fechaPedido = fechaPedido;
    }

    public ZonaComercial getZonaComercial() {
        return zonaComercial;
    }

    public void setZonaComercial(ZonaComercial zonaComercial) {
        this.zonaComercial = zonaComercial;
    }

    public EstadoPedido getEstado() {
        return estado;
    }

    public void setEstado(EstadoPedido estado) {
        this.estado = estado;
    }
}
