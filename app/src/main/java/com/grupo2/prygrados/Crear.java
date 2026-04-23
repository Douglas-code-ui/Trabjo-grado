package com.grupo2.prygrados;

import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.grupo2.prygrados.Api.ApiClient;
import com.grupo2.prygrados.Api.ApiService;
import com.grupo2.prygrados.Database.UsuarioDAO;
import com.grupo2.prygrados.Modelo.Usuario;
import com.grupo2.prygrados.Util.NetworkUtil;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Crear extends AppCompatActivity {

    private EditText txtNombre, txtCorreo, txtContrasena;
    private RadioGroup radioTipoUsuario;
    private Button btnCrear;

    private ApiService apiService;
    private UsuarioDAO usuarioDAO;

    private static final int MAX_NOMBRE = 100;
    private static final int MAX_CORREO = 100;
    private static final int MIN_CONTRASENA = 6;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.crear);

        txtNombre = findViewById(R.id.txtNombre);
        txtCorreo = findViewById(R.id.txtCorreo);
        txtContrasena = findViewById(R.id.txtContrasena);
        radioTipoUsuario = findViewById(R.id.radioTipoUsuario);
        btnCrear = findViewById(R.id.btnCrear);

        apiService = ApiClient.getClient().create(ApiService.class);
        usuarioDAO = new UsuarioDAO(this);

        btnCrear.setOnClickListener(v -> {
            if (validarCampos()) {
                enviarRegistro();
            }
        });
    }

    private boolean validarCampos() {

        String nombre = txtNombre.getText().toString().trim();
        String correo = txtCorreo.getText().toString().trim();
        String contrasena = txtContrasena.getText().toString();

        if (nombre.isEmpty()) {
            txtNombre.setError("El nombre es obligatorio");
            return false;
        }

        if (nombre.length() > MAX_NOMBRE) {
            txtNombre.setError("Máximo " + MAX_NOMBRE + " caracteres");
            return false;
        }

        if (correo.isEmpty()) {
            txtCorreo.setError("El correo es obligatorio");
            return false;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(correo).matches()) {
            txtCorreo.setError("Correo inválido");
            return false;
        }

        if (contrasena.length() < MIN_CONTRASENA) {
            txtContrasena.setError("Mínimo " + MIN_CONTRASENA + " caracteres");
            return false;
        }

        if (radioTipoUsuario.getCheckedRadioButtonId() == -1) {
            Toast.makeText(this, "Selecciona un rol", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private void enviarRegistro() {

        String nombre = txtNombre.getText().toString().trim();
        String correo = txtCorreo.getText().toString().trim();
        String contrasena = txtContrasena.getText().toString();

        int checkedId = radioTipoUsuario.getCheckedRadioButtonId();
        RadioButton rb = findViewById(checkedId);

        String rolTexto = rb.getText().toString();

        String rolBackend =
                rolTexto.equalsIgnoreCase("Administrador")
                        ? "ADMIN"
                        : "EMPLEADO";

        Usuario usuario =
                new Usuario(nombre, correo, contrasena, rolBackend);

        // 🟢 SI HAY INTERNET
        if (NetworkUtil.hayInternet(this)) {

            btnCrear.setEnabled(false);

            Call<Usuario> call =
                    apiService.crearUsuario(usuario);

            call.enqueue(new Callback<Usuario>() {

                @Override
                public void onResponse(Call<Usuario> call,
                                       Response<Usuario> response) {

                    btnCrear.setEnabled(true);

                    if (response.isSuccessful()) {

                        // 🟢 Guardar en tabla ONLINE
                        usuarioDAO.insertarUsuarioOnline(usuario);

                        Toast.makeText(Crear.this,
                                "✅ Guardado ONLINE",
                                Toast.LENGTH_LONG).show();

                    } else {

                        // 🔴 Guardar OFFLINE
                        usuarioDAO.insertarUsuarioOffline(usuario);

                        Toast.makeText(Crear.this,
                                "⚠ Error servidor, guardado OFFLINE",
                                Toast.LENGTH_LONG).show();
                    }

                    limpiarCampos();
                }

                @Override
                public void onFailure(Call<Usuario> call,
                                      Throwable t) {

                    btnCrear.setEnabled(true);

                    // 🔴 Guardar OFFLINE
                    usuarioDAO.insertarUsuarioOffline(usuario);

                    Toast.makeText(Crear.this,
                            "📴 Sin conexión, guardado OFFLINE",
                            Toast.LENGTH_LONG).show();

                    limpiarCampos();
                }
            });

        }

        // 🔴 SIN INTERNET
        else {

            usuarioDAO.insertarUsuarioOffline(usuario);

            Toast.makeText(this,
                    "📴 Guardado OFFLINE",
                    Toast.LENGTH_LONG).show();

            limpiarCampos();
        }
    }

    private void limpiarCampos() {

        txtNombre.setText("");
        txtCorreo.setText("");
        txtContrasena.setText("");
        radioTipoUsuario.clearCheck();
    }
}