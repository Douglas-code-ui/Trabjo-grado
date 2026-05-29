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

    @SerializedName("categoria")
    private String categoria;

    @SerializedName("talla")
    private String talla;

    // GETTERS

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

    public String getTalla() {
        return talla;
    }

    // SETTERS

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public void setTalla(String talla) {
        this.talla = talla;
    }

    public void setPrecioVenta(double precioVenta) {
        this.precioVenta = precioVenta;
    }

    public void setStockActual(int stockActual) {
        this.stockActual = stockActual;
    }

    @Override
    public String toString() {
        return nombre;
    }
}