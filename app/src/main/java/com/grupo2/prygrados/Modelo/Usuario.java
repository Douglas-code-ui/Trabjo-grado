package com.grupo2.prygrados.Modelo;

import com.google.gson.annotations.SerializedName;

public class Usuario {

    // Solo para SQLite
    private transient Integer id;

    @SerializedName("idUsuario")
    private Integer idUsuario;

    @SerializedName("nombre")
    private String nombre;

    @SerializedName("correo")
    private String correo;

    @SerializedName("contrasena")
    private String contrasena;

    @SerializedName("rol")
    private String rol;

    public Usuario() {
    }

    public Usuario(String nombre,
                   String correo,
                   String contrasena,
                   String rol) {

        this.nombre = nombre;
        this.correo = correo;
        this.contrasena = contrasena;
        this.rol = rol;
    }

    // =========================
    // SQLITE
    // =========================

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    // =========================
    // BACKEND
    // =========================

    public Integer getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Integer idUsuario) {
        this.idUsuario = idUsuario;
    }

    // =========================
    // DATOS
    // =========================

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getContrasena() {
        return contrasena;
    }

    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }
}