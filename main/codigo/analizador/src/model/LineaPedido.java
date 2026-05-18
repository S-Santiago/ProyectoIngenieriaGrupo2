package model;

import java.math.BigDecimal;

public class LineaPedido {

    private Integer idLinea;
    private Integer idPedido;
    private String referenciaProduto;
    private String descripcionProducto;
    private String categoria;
    private BigDecimal costeUnitario;
    private BigDecimal precioVentaUnitario;
    private Integer unidades;
    private String fechaPedido;
    private Integer zonaComercial;
    private EstadoPedido estado;

    public LineaPedido(Integer idLinea, Integer idPedido, String referenciaProduto, String descripcionProducto,
            String categoria, BigDecimal costeUnitario, BigDecimal precioVentaUnitario,
            Integer unidades, String fechaPedido, Integer zonaComercial, EstadoPedido estado) {
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

    public Integer getIdLinea() {
        return idLinea;
    }

    public void setIdLinea(Integer idLinea) {
        this.idLinea = idLinea;
    }

    public Integer getIdPedido() {
        return idPedido;
    }

    public void setIdPedido(Integer idPedido) {
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

    public BigDecimal getCosteUnitario() {
        return costeUnitario;
    }

    public void setCosteUnitario(BigDecimal costeUnitario) {
        this.costeUnitario = costeUnitario;
    }

    public BigDecimal getPrecioVentaUnitario() {
        return precioVentaUnitario;
    }

    public void setPrecioVentaUnitario(BigDecimal precioVentaUnitario) {
        this.precioVentaUnitario = precioVentaUnitario;
    }

    public Integer getUnidades() {
        return unidades;
    }

    public void setUnidades(Integer unidades) {
        this.unidades = unidades;
    }

    public String getFechaPedido() {
        return fechaPedido;
    }

    public void setFechaPedido(String fechaPedido) {
        this.fechaPedido = fechaPedido;
    }

    public Integer getZonaComercial() {
        return zonaComercial;
    }

    public void setZonaComercial(Integer zonaComercial) {
        this.zonaComercial = zonaComercial;
    }

    public EstadoPedido getEstado() {
        return estado;
    }

    public void setEstado(EstadoPedido estado) {
        this.estado = estado;
    }
}
