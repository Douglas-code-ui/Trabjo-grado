package com.grupo2.prygrados.Modelo;

import com.google.gson.annotations.SerializedName;

public class Factura {

    @SerializedName("idFactura")
    private int idFactura;

    @SerializedName("numeroFactura")
    private String numeroFactura;

    @SerializedName("fecha")
    private String fecha;

    @SerializedName("ventaId")
    private int ventaId;

    @SerializedName("total")
    private double total;

    // =========================
    // GETTERS
    // =========================

    public int getIdFactura() {
        return idFactura;
    }

    public String getNumeroFactura() {
        return numeroFactura;
    }

    public String getFecha() {
        return fecha;
    }

    public int getVentaId() {
        return ventaId;
    }

    public double getTotal() {
        return total;
    }
}