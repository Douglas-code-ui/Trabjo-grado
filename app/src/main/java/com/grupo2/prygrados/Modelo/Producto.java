package com.grupo2.prygrados.Modelo;

import com.google.gson.annotations.SerializedName;

public class Producto {

    @SerializedName("idProducto")
    private int idProducto;

    @SerializedName("nombre")
    private String nombre;

    @SerializedName("precioVenta")
    private double precioVenta;

    @SerializedName("stockActual")
    private int stockActual;

    // 🔥 FALTABA ESTO
    @SerializedName("categoria")
    private String categoria;

    public int getIdProducto() {
        return idProducto;
    }

    public String getNombre() {
        return nombre;
    }

    public double getPrecioVenta() {
        return precioVenta;
    }

    public int getStockActual() {
        return stockActual;
    }

    public String getCategoria() {
        return categoria;
    }

    @Override
    public String toString() {
        return nombre;
    }
}