package com.grupo2.prygrados.Api;

import com.grupo2.prygrados.Modelo.Producto;
import com.grupo2.prygrados.Modelo.Usuario;
import com.grupo2.prygrados.Modelo.Venta;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.*;

public interface ApiService {

    // =========================
    // 👤 USUARIOS
    // =========================

    @GET("usuarios")
    Call<List<Usuario>> listarUsuarios();

    @POST("usuarios")
    Call<Usuario> crearUsuario(@Body Usuario usuario);

    @GET("usuarios/buscar")
    Call<Usuario> obtenerUsuario(@Query("correo") String correo);

    @POST("usuarios/login")
    Call<Usuario> login(@Body Usuario usuario);


    // =========================
    // 🛒 PRODUCTOS
    // =========================

    @GET("producto/listar")
    Call<List<Producto>> listarProductos();

    @GET("producto/categoria")
    Call<List<Producto>> porCategoria(@Query("categoria") String categoria);

    @GET("producto/buscar")
    Call<List<Producto>> buscarProducto(@Query("nombre") String nombre);
    // =========================
// 📦 OBTENER PRODUCTOS
// =========================

    @GET("producto/listar")
    Call<List<Producto>> obtenerProductos();

// =========================
// ⚠ STOCK BAJO
// =========================

    @GET("producto/stock-bajo")
    Call<List<Producto>> obtenerStockBajo();

// =========================
// 💰 OBTENER VENTAS
// =========================

    @GET("venta/listar")
    Call<List<Venta>> obtenerVentas();


    // =========================
    // 💰 VENTA (FLUJO COMPLETO)
    // =========================

    @FormUrlEncoded
    @POST("venta/guardar")
    Call<String> registrarVenta(
            @Field("productoId") int productoId,
            @Field("cantidad") int cantidad,
            @Field("metodoPago") String metodoPago
    );

}