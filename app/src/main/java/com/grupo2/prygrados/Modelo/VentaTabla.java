package com.grupo2.prygrados.Modelo;

import com.google.gson.annotations.SerializedName;

public class VentaTabla {

    @SerializedName("idVenta")
    private int idVenta;

    @SerializedName("empleado")
    private String empleado;

    @SerializedName("prendas")
    private String prendas;

    @SerializedName("fecha")
    private String fecha;

    @SerializedName("total")
    private double total;

    public int getIdVenta() {
        return idVenta;
    }

    public String getEmpleado() {
        return empleado;
    }

    public String getPrendas() {
        return prendas;
    }

    public String getFecha() {
        return fecha;
    }

    public double getTotal() {
        return total;
    }
}