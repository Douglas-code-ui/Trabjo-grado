package com.grupo2.prygrados.Api;

import com.grupo2.prygrados.Modelo.Usuario;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ApiService {

    @GET("usuarios")
    Call<List<Usuario>> listarUsuarios();

    @POST("usuarios")
    Call<Usuario> crearUsuario(@Body Usuario usuario);

    @GET("usuarios/buscar")
    Call<Usuario> obtenerUsuario(@Query("correo") String correo);


    @POST("usuarios/login")
    Call<Usuario> login(@Body Usuario usuario);
}
