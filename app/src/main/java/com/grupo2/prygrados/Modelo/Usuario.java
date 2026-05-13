package com.grupo2.prygrados.Modelo;

public class Usuario {

    // 🔥 IMPORTANTE
    // SQLite usa este ID
    // Retrofit NO lo enviará al backend
    private transient Integer id;

    private String nombre;
    private String correo;
    private String contrasena;
    private String rol;

    // =========================
    // CONSTRUCTORES
    // =========================

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
    // GETTERS Y SETTERS
    // =========================

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

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