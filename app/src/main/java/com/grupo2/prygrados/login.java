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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.iniciar);

        txtCorreo = findViewById(R.id.txtCorreo);
        txtContrasena = findViewById(R.id.txtContrasena);
        btnIngresar = findViewById(R.id.btnIniciarSesion);
        radioTipoUsuario = findViewById(R.id.radioTipoUsuario);

        btnIngresar.setOnClickListener(v -> validarLogin());
    }

    private void validarLogin() {

        String correo = txtCorreo.getText().toString().trim();
        String contrasena = txtContrasena.getText().toString().trim();

        // VALIDACIONES
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

        // RADIO BUTTON
        int selectedId = radioTipoUsuario.getCheckedRadioButtonId();

        if (selectedId == -1) {
            Toast.makeText(this, "Seleccione un rol", Toast.LENGTH_SHORT).show();
            return;
        }

        RadioButton radioSeleccionado = findViewById(selectedId);
        String rolTemp = radioSeleccionado.getText().toString().trim().toUpperCase();

        // 🔥 NORMALIZAR
        final String rolSeleccionado;

        if (rolTemp.contains("ADMIN")) {
            rolSeleccionado = "ADMIN";
        } else {
            rolSeleccionado = "EMPLEADO";
        }

        // API
        ApiService api = ApiClient.getClient().create(ApiService.class);

        Usuario userLogin = new Usuario();
        userLogin.setCorreo(correo);
        userLogin.setContrasena(contrasena);

        api.login(userLogin).enqueue(new Callback<Usuario>() {

            @Override
            public void onResponse(Call<Usuario> call, Response<Usuario> response) {

                if (!response.isSuccessful() || response.body() == null) {
                    Toast.makeText(login.this,
                            "Usuario o contraseña incorrectos",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                Usuario user = response.body();
                String rolBackend = user.getRol();

                if (rolBackend == null) {
                    Toast.makeText(login.this,
                            "Usuario sin rol",
                            Toast.LENGTH_LONG).show();
                    return;
                }

                rolBackend = rolBackend.trim().toUpperCase();

                //  VALIDACIÓN
                if (!rolBackend.equals(rolSeleccionado)) {
                    Toast.makeText(login.this,
                            "Rol incorrecto\nSeleccionaste: " + rolSeleccionado +
                                    "\nPero eres: " + rolBackend,
                            Toast.LENGTH_LONG).show();
                    return;
                }

                // BIENVENIDA
                Toast.makeText(login.this,
                        "Bienvenido " + user.getNombre(),
                        Toast.LENGTH_LONG).show();

                Intent intent;

                if (rolBackend.equals("ADMIN")) {
                    intent = new Intent(login.this, Admin.class);
                } else {
                    intent = new Intent(login.this, Empleado.class);
                }

                intent.putExtra("nombre", user.getNombre());
                intent.putExtra("rol", rolBackend);

                startActivity(intent);
                finish();
            }

            @Override
            public void onFailure(Call<Usuario> call, Throwable t) {

                Toast.makeText(login.this,
                        "Error conexión: " + t.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        });
    }
}