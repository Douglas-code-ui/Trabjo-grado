package com.grupo2.prygrados.Modelo;

import com.google.gson.annotations.SerializedName;

public class Reporte {

    @SerializedName("idReporte")
    private int idReporte;

    @SerializedName("tipo")
    private String tipo;

    @SerializedName("fechaGeneracion")
    private String fechaGeneracion;

    @SerializedName("usuarioId")
    private int usuarioId;

    @SerializedName("descripcion")
    private String descripcion;

    public Reporte() {
    }

    public Reporte(String tipo, int usuarioId, String descripcion) {
        this.tipo = tipo;
        this.usuarioId = usuarioId;
        this.descripcion = descripcion;
    }

    public int getIdReporte() {
        return idReporte;
    }

    public void setIdReporte(int idReporte) {
        this.idReporte = idReporte;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getFechaGeneracion() {
        return fechaGeneracion;
    }

    public void setFechaGeneracion(String fechaGeneracion) {
        this.fechaGeneracion = fechaGeneracion;
    }

    public int getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(int usuarioId) {
        this.usuarioId = usuarioId;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
}