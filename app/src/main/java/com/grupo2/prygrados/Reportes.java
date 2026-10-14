package com.grupo2.prygrados;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.grupo2.prygrados.Api.ApiClient;
import com.grupo2.prygrados.Api.ApiService;
import com.grupo2.prygrados.Modelo.Producto;
import com.grupo2.prygrados.Modelo.Reporte;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Reportes extends AppCompatActivity {

    // Stock igual o menor a este número se considera "bajo"
    private static final int STOCK_BAJO = 5;

    // Tipos de reporte. Deben coincidir con el ENUM de la columna "tipo"
    // en MySQL: enum('VENTAS','INVENTARIO')
    private static final String TIPO_VENTAS = "VENTAS";
    private static final String TIPO_STOCK = "INVENTARIO";

    private EditText txtReporte;
    private MaterialButton btnEnviar;
    private MaterialButton btnReportarStock;
    private TextView txtEstadoStock;
    private LinearLayout layoutStockBajo;

    private ApiService api;
    private FirebaseFirestore db;

    private int idUsuario;

    private String tipoReporte = TIPO_VENTAS;
    private final List<Producto> productosBajos = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.reporte);

        txtReporte = findViewById(R.id.txtReporte);
        btnEnviar = findViewById(R.id.btnEnviarReporte);
        btnReportarStock = findViewById(R.id.btnReportarStock);
        txtEstadoStock = findViewById(R.id.txtEstadoStock);
        layoutStockBajo = findViewById(R.id.layoutStockBajo);

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

        logEvento("SUCCESS", "APP_REPORTES", "Entró a reportes");

        btnReportarStock.setOnClickListener(v -> prepararReporteTodas());
        btnEnviar.setOnClickListener(v -> enviarReporte());
    }

    // Se consulta el stock cada vez que se abre o se vuelve a la pantalla
    @Override
    protected void onResume() {
        super.onResume();
        cargarStockBajo();
    }

    // =========================
    // QUERY: PRENDAS CON STOCK BAJO
    // =========================
    private void cargarStockBajo() {

        txtEstadoStock.setVisibility(View.VISIBLE);
        txtEstadoStock.setText("Cargando...");

        api.listarProductos().enqueue(new Callback<List<Producto>>() {

            @Override
            public void onResponse(Call<List<Producto>> call,
                                   Response<List<Producto>> response) {

                if (response.isSuccessful() && response.body() != null) {

                    productosBajos.clear();

                    for (Producto p : response.body()) {
                        if (p.getStockActual() <= STOCK_BAJO) {
                            productosBajos.add(p);
                        }
                    }

                    logEvento("SUCCESS", "CONSULTA_STOCK_BAJO",
                            "Prendas con stock bajo: " + productosBajos.size());

                    mostrarStockBajo();

                } else {

                    txtEstadoStock.setText("No se pudo consultar el stock");

                    logEvento("ERROR", "CONSULTA_STOCK_BAJO",
                            "Código: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<Producto>> call, Throwable t) {

                txtEstadoStock.setText("Error de conexión");

                logEvento("ERROR", "ERROR_CONEXION_STOCK",
                        String.valueOf(t.getMessage()));
            }
        });
    }

    private void mostrarStockBajo() {

        layoutStockBajo.removeAllViews();

        if (productosBajos.isEmpty()) {
            txtEstadoStock.setVisibility(View.VISIBLE);
            txtEstadoStock.setText("No hay prendas con stock bajo");
            btnReportarStock.setVisibility(View.GONE);
            return;
        }

        txtEstadoStock.setVisibility(View.GONE);
        btnReportarStock.setVisibility(View.VISIBLE);

        for (Producto p : productosBajos) {

            View fila = getLayoutInflater()
                    .inflate(R.layout.item_stock_bajo, layoutStockBajo, false);

            TextView nombre = fila.findViewById(R.id.txtItemNombre);
            TextView stock = fila.findViewById(R.id.txtItemStock);

            nombre.setText(p.getNombre());
            stock.setText(p.getStockActual() == 0
                    ? "Agotado"
                    : "Stock: " + p.getStockActual());

            // Al tocar una prenda, se llena el reporte con sus datos
            fila.setOnClickListener(v -> prepararReportePrenda(p));

            layoutStockBajo.addView(fila);
        }
    }

    // =========================
    // PREPARAR EL TEXTO DEL REPORTE
    // =========================
    private void prepararReportePrenda(Producto p) {

        String texto = "Stock bajo: " + p.getNombre() + " - "
                + (p.getStockActual() == 0
                ? "agotado."
                : "quedan " + p.getStockActual() + " unidades.");

        ponerTextoReporte(texto);

        logEvento("SUCCESS", "SELECCION_PRENDA_STOCK", p.getNombre());
    }

    private void prepararReporteTodas() {

        StringBuilder sb = new StringBuilder("Prendas con stock bajo:\n");

        for (Producto p : productosBajos) {
            sb.append("- ")
                    .append(p.getNombre())
                    .append(": ")
                    .append(p.getStockActual() == 0
                            ? "agotado"
                            : p.getStockActual() + " unidades")
                    .append("\n");
        }

        ponerTextoReporte(sb.toString().trim());

        logEvento("SUCCESS", "SELECCION_TODAS_STOCK",
                "Total: " + productosBajos.size());
    }

    private void ponerTextoReporte(String texto) {

        tipoReporte = TIPO_STOCK;

        txtReporte.setText(texto);
        txtReporte.setSelection(txtReporte.getText().length());
        txtReporte.requestFocus();
    }

    // =========================
    // ENVIAR REPORTE
    // =========================
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
        reporte.setTipo(tipoReporte);
        reporte.setUsuarioId(idUsuario);

        // Evita enviar dos veces si tocan el botón varias veces
        btnEnviar.setEnabled(false);

        // Se vacía el mensaje apenas se toca "Enviar reporte".
        // Si el envío falla, se restaura para no perder lo escrito.
        final String tipoEnviado = tipoReporte;
        txtReporte.setText("");
        tipoReporte = TIPO_VENTAS;

        api.guardarReporte(reporte).enqueue(new Callback<Reporte>() {

            @Override
            public void onResponse(Call<Reporte> call,
                                   Response<Reporte> response) {

                btnEnviar.setEnabled(true);

                if (response.isSuccessful()) {

                    Toast.makeText(
                            Reportes.this,
                            "Reporte enviado correctamente",
                            Toast.LENGTH_LONG
                    ).show();

                    logEvento(
                            "SUCCESS",
                            "REPORTE_ENVIADO",
                            descripcion
                    );

                } else {

                    // Falló: se devuelve el mensaje para poder reenviarlo
                    txtReporte.setText(descripcion);
                    txtReporte.setSelection(txtReporte.getText().length());
                    tipoReporte = tipoEnviado;

                    Toast.makeText(
                            Reportes.this,
                            "Error " + response.code() + ". El mensaje no se envió",
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

                btnEnviar.setEnabled(true);

                // Falló: se devuelve el mensaje para poder reenviarlo
                txtReporte.setText(descripcion);
                txtReporte.setSelection(txtReporte.getText().length());
                tipoReporte = tipoEnviado;

                Toast.makeText(
                        Reportes.this,
                        "Error: " + t.getMessage(),
                        Toast.LENGTH_LONG
                ).show();

                logEvento(
                        "ERROR",
                        "ERROR_CONEXION",
                        String.valueOf(t.getMessage())
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