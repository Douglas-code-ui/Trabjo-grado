package com.grupo2.prygrados;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.grupo2.prygrados.Api.ApiClient;
import com.grupo2.prygrados.Api.ApiService;
import com.grupo2.prygrados.Modelo.Reporte;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Reportes extends AppCompatActivity {

    private EditText txtReporte;
    private MaterialButton btnEnviar;

    private ApiService api;
    private FirebaseFirestore db;

    private int idUsuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.reporte);

        txtReporte = findViewById(R.id.txtReporte);
        btnEnviar = findViewById(R.id.btnEnviarReporte);

        api = ApiClient.getClient().create(ApiService.class);
        db = FirebaseFirestore.getInstance();

        // Recibir el id del administrador
        idUsuario = getIntent().getIntExtra("idUsuario", 0);

        // Verificar que llegó
        Toast.makeText(
                this,
                "ID Usuario: " + idUsuario,
                Toast.LENGTH_LONG
        ).show();

        btnEnviar.setOnClickListener(v -> enviarReporte());
    }

    private void enviarReporte() {

        String descripcion = txtReporte.getText().toString().trim();

        if (descripcion.isEmpty()) {
            txtReporte.setError("Ingrese un reporte");
            txtReporte.requestFocus();
            return;
        }

        // Mostrar nuevamente el id antes de enviar
        Toast.makeText(
                this,
                "Enviando con usuario: " + idUsuario,
                Toast.LENGTH_LONG
        ).show();

        Reporte reporte = new Reporte();
        reporte.setDescripcion(descripcion);
        reporte.setTipo("VENTAS");
        reporte.setUsuarioId(idUsuario);

        api.guardarReporte(reporte).enqueue(new Callback<Reporte>() {

            @Override
            public void onResponse(Call<Reporte> call,
                                   Response<Reporte> response) {

                if (response.isSuccessful()) {

                    Toast.makeText(
                            Reportes.this,
                            "Reporte enviado correctamente",
                            Toast.LENGTH_LONG
                    ).show();

                    txtReporte.setText("");

                    logEvento(
                            "SUCCESS",
                            "REPORTE_ENVIADO",
                            descripcion
                    );

                } else {

                    Toast.makeText(
                            Reportes.this,
                            "Error " + response.code(),
                            Toast.LENGTH_LONG
                    ).show();

                    logEvento(
                            "ERROR",
                            "REPORTE_ERROR",
                            "Código: " + response.code()
                    );
                }
            }

            @Override
            public void onFailure(Call<Reporte> call,
                                  Throwable t) {

                Toast.makeText(
                        Reportes.this,
                        "Error: " + t.getMessage(),
                        Toast.LENGTH_LONG
                ).show();

                logEvento(
                        "ERROR",
                        "ERROR_CONEXION",
                        t.getMessage()
                );
            }
        });
    }

    private void logEvento(String tipo,
                           String evento,
                           String detalle) {

        java.util.HashMap<String, Object> log = new java.util.HashMap<>();

        log.put("tipo", tipo);
        log.put("evento", evento);
        log.put("detalle", detalle);
        log.put("fecha", System.currentTimeMillis());

        db.collection("logs_reportes").add(log);
    }
}