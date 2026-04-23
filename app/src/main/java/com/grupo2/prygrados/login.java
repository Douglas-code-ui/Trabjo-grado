package com.grupo2.prygrados;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.grupo2.prygrados.Api.ApiClient;
import com.grupo2.prygrados.Api.ApiService;
import com.grupo2.prygrados.Modelo.Usuario;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class login extends AppCompatActivity {

    EditText txtCorreo, txtContrasena;
    Button btnIngresar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.iniciar);

        txtCorreo = findViewById(R.id.txtCorreo);
        txtContrasena = findViewById(R.id.txtContrasena);
        btnIngresar = findViewById(R.id.btnIniciarSesion);

        btnIngresar.setOnClickListener(v -> validarLogin());
    }

    private void validarLogin() {

        String correo = txtCorreo.getText().toString().trim();
        String contrasena = txtContrasena.getText().toString().trim();

        // Validar campos vacíos
        if (correo.isEmpty()) {
            txtCorreo.setError("Ingrese su correo electrónico");
            txtCorreo.requestFocus();
            return;
        }

        if (contrasena.isEmpty()) {
            txtContrasena.setError("Ingrese su contraseña");
            txtContrasena.requestFocus();
            return;
        }

        // Validar formato de correo
        if (!Patterns.EMAIL_ADDRESS.matcher(correo).matches()) {
            txtCorreo.setError("Ingrese un correo electrónico válido");
            txtCorreo.requestFocus();
            return;
        }

        ApiService api = ApiClient.getClient().create(ApiService.class);

        Usuario userLogin = new Usuario();
        userLogin.setCorreo(correo);
        userLogin.setContrasena(contrasena);

        Call<Usuario> call = api.login(userLogin);

        call.enqueue(new Callback<Usuario>() {

            @Override
            public void onResponse(Call<Usuario> call, Response<Usuario> response) {

                if (!response.isSuccessful() || response.body() == null) {
                    Toast.makeText(login.this,
                            "Usuario o contraseña incorrectos",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                Usuario user = response.body();

                Toast.makeText(login.this,
                        "Bienvenido " + user.getNombre(),
                        Toast.LENGTH_SHORT).show();

                Intent intent;

                if (user.getRol().equalsIgnoreCase("Administrador")) {

                    intent = new Intent(login.this, Admin.class);

                    intent.putExtra("nombre", user.getNombre());
                    intent.putExtra("rol", user.getRol());

                } else {

                    intent = new Intent(login.this, Empleado.class);

                    intent.putExtra("nombre", user.getNombre());
                    intent.putExtra("rol", user.getRol());
                }

                startActivity(intent);
                finish();
            }

            @Override
            public void onFailure(Call<Usuario> call, Throwable t) {

                Toast.makeText(login.this,
                        "Error de conexión: " + t.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        });
    }
}