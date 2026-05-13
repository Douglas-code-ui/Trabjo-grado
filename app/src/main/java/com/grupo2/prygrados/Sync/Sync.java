package com.grupo2.prygrados.Sync;

import android.content.Context;
import android.util.Log;

import com.grupo2.prygrados.Api.ApiClient;
import com.grupo2.prygrados.Api.ApiService;
import com.grupo2.prygrados.Database.UsuarioDAO;
import com.grupo2.prygrados.Modelo.Usuario;
import com.grupo2.prygrados.Util.NetworkUtil;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Sync {

    public static void sincronizarUsuarios(Context context) {

        // VERIFICAR INTERNET
        if (!NetworkUtil.hayInternet(context)) {

            Log.d("SYNC", "Sin internet");
            return;
        }

        UsuarioDAO usuarioDAO =
                new UsuarioDAO(context);

        ApiService apiService =
                ApiClient.getClient()
                        .create(ApiService.class);

        // OBTENER OFFLINE
        List<Usuario> lista =
                usuarioDAO.obtenerUsuariosOffline();

        Log.d("SYNC",
                "Usuarios offline: "
                        + lista.size());

        // RECORRER LISTA
        for (Usuario usuario : lista) {

            // CREAR OBJETO SIN ID
            Usuario usuarioEnviar =
                    new Usuario(
                            usuario.getNombre(),
                            usuario.getCorreo(),
                            usuario.getContrasena(),
                            usuario.getRol()
                    );

            // ENVIAR
            Call<Usuario> call =
                    apiService.crearUsuario(usuarioEnviar);

            call.enqueue(new Callback<Usuario>() {

                @Override
                public void onResponse(
                        Call<Usuario> call,
                        Response<Usuario> response) {

                    // ✅ GUARDADO CORRECTO
                    if (response.isSuccessful()) {

                        Log.d("SYNC",
                                "Sincronizado: "
                                        + usuario.getCorreo());

                        usuarioDAO.insertarUsuarioOnline(usuario);

                        usuarioDAO.eliminarUsuarioOffline(
                                usuario.getId());

                    }

                    // ⚠ CORREO YA EXISTE
                    else if (response.code() == 409) {

                        Log.d("SYNC",
                                "Correo ya existe: "
                                        + usuario.getCorreo());

                        // 🔥 BORRAR OFFLINE
                        usuarioDAO.eliminarUsuarioOffline(
                                usuario.getId());
                    }

                    // ❌ OTRO ERROR
                    else {

                        Log.e("SYNC",
                                "Error servidor: "
                                        + response.code());
                    }
                }

                @Override
                public void onFailure(
                        Call<Usuario> call,
                        Throwable t) {

                    Log.e("SYNC",
                            "Error: "
                                    + t.getMessage());
                }
            });
        }
    }
}