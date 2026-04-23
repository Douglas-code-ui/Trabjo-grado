package com.grupo2.prygrados.Modelo;

public class Venta {

    private int usuarioId;
    private double total;
    private String metodoPago;

    public Venta(int usuarioId, double total, String metodoPago) {
        this.usuarioId = usuarioId;
        this.total = total;
        this.metodoPago = metodoPago;
    }

    public int getUsuarioId() {
        return usuarioId;
    }

    public double getTotal() {
        return total;
    }

    public String getMetodoPago() {
        return metodoPago;
    }
}