package com.grupo2.prygrados.Modelo;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

public class Venta {

    @SerializedName("idVenta")
    private int idVenta;

    @SerializedName("usuarioId")
    private int usuarioId;

    @SerializedName("total")
    private double total;

    @SerializedName("metodoPago")
    private String metodoPago;

    @SerializedName("fecha")
    private Date fecha;

    // =========================
    // GETTERS
    // =========================

    public int getIdVenta() {
        return idVenta;
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

    public Date getFecha() {
        return fecha;
    }
}