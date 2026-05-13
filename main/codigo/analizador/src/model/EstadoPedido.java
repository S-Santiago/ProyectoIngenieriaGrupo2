package model;

public enum EstadoPedido {
    COMPLETADO("completado"),
    CANCELADO("cancelado"),
    PENDIENTE("pendiente");

    private final String valor;

    EstadoPedido(String valor) {
        this.valor = valor;
    }

    public String getValor() {
        return valor;
    }

    public static EstadoPedido fromString(String texto) {
        for (EstadoPedido estado : EstadoPedido.values()) {
            if (estado.valor.equalsIgnoreCase(texto)) {
                return estado;
            }
        }
        throw new IllegalArgumentException("Estado de pedido no reconocido: " + texto);
    }
}