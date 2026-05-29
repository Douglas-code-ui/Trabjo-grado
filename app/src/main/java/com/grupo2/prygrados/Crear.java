package com.grupo2.prygrados;

import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;
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

    private FirebaseFirestore db;

    private static final int MAX_NOMBRE = 100;
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

        db = FirebaseFirestore.getInstance();

        // 🔥 LOG: pantalla abierta
        logEvento("SUCCESS", "APP_CREAR", "Pantalla crear cuenta abierta");

        btnCrear.setOnClickListener(v -> {

            logEvento("SUCCESS", "CLICK_CREAR", "Usuario presionó botón crear cuenta");

            if (validarCampos()) {
                enviarRegistro();
            }
        });
    }

    // =========================
    // VALIDACIONES
    // =========================
    private boolean validarCampos() {

        String nombre = txtNombre.getText().toString().trim();
        String correo = txtCorreo.getText().toString().trim();
        String contrasena = txtContrasena.getText().toString();

        if (nombre.isEmpty()) {

            txtNombre.setError("El nombre es obligatorio");
            logEvento("ERROR", "VALIDACION_NOMBRE", "Nombre vacío");
            return false;
        }

        if (nombre.length() > MAX_NOMBRE) {

            txtNombre.setError("Máximo " + MAX_NOMBRE + " caracteres");
            logEvento("ERROR", "VALIDACION_NOMBRE_LARGO", "Nombre muy largo");
            return false;
        }

        if (correo.isEmpty()) {

            txtCorreo.setError("El correo es obligatorio");
            logEvento("ERROR", "VALIDACION_CORREO", "Correo vacío");
            return false;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(correo).matches()) {

            txtCorreo.setError("Correo inválido");
            logEvento("ERROR", "VALIDACION_CORREO_INVALIDO", correo);
            return false;
        }

        if (contrasena.length() < MIN_CONTRASENA) {

            txtContrasena.setError("Mínimo " + MIN_CONTRASENA + " caracteres");
            logEvento("ERROR", "VALIDACION_PASSWORD", "Contraseña corta");
            return false;
        }

        if (radioTipoUsuario.getCheckedRadioButtonId() == -1) {

            logEvento("ERROR", "VALIDACION_ROL", "No seleccionó rol");

            Toast.makeText(this, "Selecciona un rol", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    // =========================
    // REGISTRO
    // =========================
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

        Usuario usuario = new Usuario(nombre, correo, contrasena, rolBackend);

        if (NetworkUtil.hayInternet(this)) {

            btnCrear.setEnabled(false);

            Call<Usuario> call = apiService.crearUsuario(usuario);

            call.enqueue(new Callback<Usuario>() {

                @Override
                public void onResponse(Call<Usuario> call,
                                       Response<Usuario> response) {

                    btnCrear.setEnabled(true);

                    if (response.isSuccessful()) {

                        usuarioDAO.insertarUsuarioOnline(usuario);

                        logEvento("SUCCESS", "REGISTRO_ONLINE", correo);

                        Toast.makeText(Crear.this,
                                "✅ Guardado ONLINE",
                                Toast.LENGTH_LONG).show();

                    } else {

                        usuarioDAO.insertarUsuarioOffline(usuario);

                        logEvento("ERROR", "REGISTRO_FALLIDO_SERVIDOR", correo);

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

                    usuarioDAO.insertarUsuarioOffline(usuario);

                    logEvento("ERROR", "ERROR_CONEXION", t.getMessage());

                    Toast.makeText(Crear.this,
                            "📴 Sin conexión, guardado OFFLINE",
                            Toast.LENGTH_LONG).show();

                    limpiarCampos();
                }
            });

        } else {

            usuarioDAO.insertarUsuarioOffline(usuario);

            logEvento("ERROR", "SIN_INTERNET", "Guardado offline directo");

            Toast.makeText(this,
                    "📴 Guardado OFFLINE",
                    Toast.LENGTH_LONG).show();

            limpiarCampos();
        }
    }

    // =========================
    // FIRESTORE LOG
    // =========================
    private void logEvento(String tipo, String evento, String detalle) {

        java.util.HashMap<String, Object> log = new java.util.HashMap<>();

        log.put("tipo", tipo);
        log.put("evento", evento);
        log.put("detalle", detalle);
        log.put("fecha", System.currentTimeMillis());

        db.collection("logs_crear_cuenta").add(log);
    }

    // =========================
    // LIMPIAR CAMPOS
    // =========================
    private void limpiarCampos() {

        txtNombre.setText("");
        txtCorreo.setText("");
        txtContrasena.setText("");
        radioTipoUsuario.clearCheck();
    }
}