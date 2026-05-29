package com.grupo2.prygrados;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.grupo2.prygrados.Api.ApiClient;
import com.grupo2.prygrados.Api.ApiService;
import com.grupo2.prygrados.Modelo.Usuario;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class login extends AppCompatActivity {

    EditText txtCorreo, txtContrasena;
    MaterialButton btnIngresar;
    RadioGroup radioTipoUsuario;

    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.iniciar);

        txtCorreo = findViewById(R.id.txtCorreo);
        txtContrasena = findViewById(R.id.txtContrasena);
        btnIngresar = findViewById(R.id.btnIniciarSesion);
        radioTipoUsuario = findViewById(R.id.radioTipoUsuario);

        db = FirebaseFirestore.getInstance();

        btnIngresar.setOnClickListener(v -> validarLogin());
    }

    private void logEvento(String evento, String detalle) {

        db.collection("logs_login").add(
                new java.util.HashMap<String, Object>() {{
                    put("evento", evento);
                    put("detalle", detalle);
                    put("fecha", System.currentTimeMillis());
                }}
        );
    }

    private void validarLogin() {

        String correo = txtCorreo.getText().toString().trim();
        String contrasena = txtContrasena.getText().toString().trim();

        if (correo.isEmpty()) {
            txtCorreo.setError("Ingrese su correo");
            txtCorreo.requestFocus();
            return;
        }

        if (contrasena.isEmpty()) {
            txtContrasena.setError("Ingrese su contraseña");
            txtContrasena.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(correo).matches()) {
            txtCorreo.setError("Correo inválido");
            txtCorreo.requestFocus();
            return;
        }

        int selectedId = radioTipoUsuario.getCheckedRadioButtonId();

        if (selectedId == -1) {
            Toast.makeText(this,
                    "Seleccione un rol",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        RadioButton radioSeleccionado = findViewById(selectedId);

        String rolSeleccionado =
                radioSeleccionado.getText().toString()
                        .equalsIgnoreCase("Administrador")
                        ? "ADMIN"
                        : "EMPLEADO";

        ApiService api =
                ApiClient.getClient().create(ApiService.class);

        Usuario userLogin = new Usuario();
        userLogin.setCorreo(correo);
        userLogin.setContrasena(contrasena);

        api.login(userLogin).enqueue(new Callback<Usuario>() {

            @Override
            public void onResponse(Call<Usuario> call,
                                   Response<Usuario> response) {

                if (!response.isSuccessful() || response.body() == null) {

                    Toast.makeText(login.this,
                            "Usuario o contraseña incorrectos",
                            Toast.LENGTH_SHORT).show();

                    logEvento("LOGIN_FALLIDO", correo);

                    return;
                }

                Usuario user = response.body();

                if (user.getRol() == null) {

                    Toast.makeText(login.this,
                            "Usuario sin rol",
                            Toast.LENGTH_SHORT).show();

                    return;
                }

                String rolBackend =
                        user.getRol().trim().toUpperCase();

                if (!rolBackend.equals(rolSeleccionado)) {

                    Toast.makeText(login.this,
                            "Rol incorrecto",
                            Toast.LENGTH_SHORT).show();

                    return;
                }

                Toast.makeText(login.this,
                        "Bienvenido " + user.getNombre(),
                        Toast.LENGTH_LONG).show();

                Intent intent;

                if (rolBackend.equals("ADMIN")) {

                    intent = new Intent(
                            login.this,
                            Admin.class
                    );

                } else {

                    intent = new Intent(
                            login.this,
                            Empleado.class
                    );
                }

                // =============================
                // ENVIAR DATOS DEL USUARIO
                // =============================

                intent.putExtra(
                        "idUsuario",
                        user.getIdUsuario()
                );

                intent.putExtra(
                        "nombre",
                        user.getNombre()
                );

                intent.putExtra(
                        "rol",
                        rolBackend
                );

                startActivity(intent);
                finish();
            }

            @Override
            public void onFailure(Call<Usuario> call,
                                  Throwable t) {

                Toast.makeText(login.this,
                        "Error conexión: " + t.getMessage(),
                        Toast.LENGTH_LONG).show();

                logEvento("ERROR_CONEXION",
                        t.getMessage());
            }
        });
    }
}