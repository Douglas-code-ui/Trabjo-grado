package com.grupo2.prygrados;

import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.firebase.firestore.FirebaseFirestore;
import com.grupo2.prygrados.Api.ApiClient;
import com.grupo2.prygrados.Api.ApiService;
import com.grupo2.prygrados.Modelo.VentaTabla;

import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Venta extends AppCompatActivity {

    private TableLayout tableVentas;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ventas);

        tableVentas = findViewById(R.id.tableVentas);

        db = FirebaseFirestore.getInstance();

        logEvento("SUCCESS", "APP_VENTAS", "Entró a pantalla ventas");
    }

    // Se recarga cada vez que se vuelve a esta pantalla,
    // así una venta nueva aparece sin reabrirla
    @Override
    protected void onResume() {
        super.onResume();
        cargarVentas();
    }

    private void cargarVentas() {

        ApiService api = ApiClient
                .getClient()
                .create(ApiService.class);

        api.obtenerVentasTabla().enqueue(new Callback<List<VentaTabla>>() {

            @Override
            public void onResponse(Call<List<VentaTabla>> call,
                                   Response<List<VentaTabla>> response) {

                if (!response.isSuccessful() || response.body() == null) {

                    logEvento("ERROR", "CARGA_VENTAS", "Respuesta no exitosa");

                    Toast.makeText(Venta.this,
                            "No hay ventas",
                            Toast.LENGTH_SHORT).show();

                    return;
                }

                logEvento("SUCCESS", "CARGA_VENTAS", "Total: " + response.body().size());

                mostrarVentas(response.body());
            }

            @Override
            public void onFailure(Call<List<VentaTabla>> call,
                                  Throwable t) {

                logEvento("ERROR", "ERROR_CONEXION_VENTAS", String.valueOf(t.getMessage()));

                Toast.makeText(Venta.this,
                        "Error: " + t.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    private void mostrarVentas(List<VentaTabla> lista) {

        // Elimina todas las filas excepto el encabezado
        while (tableVentas.getChildCount() > 1) {
            tableVentas.removeViewAt(1);
        }

        int blanco = ContextCompat.getColor(this, R.color.kemba_white);
        int dorado = ContextCompat.getColor(this, R.color.kemba_gold);
        int hint = ContextCompat.getColor(this, R.color.kemba_hint);
        int linea = ContextCompat.getColor(this, R.color.kemba_stroke);

        for (VentaTabla venta : lista) {

            TableRow fila = new TableRow(this);

            fila.addView(crearCelda(venta.getEmpleado(), blanco, false));
            fila.addView(crearCelda(venta.getPrendas(), blanco, false));
            fila.addView(crearCelda(venta.getFecha(), hint, false));
            fila.addView(crearCelda("$ " + venta.getTotal(), dorado, true));

            tableVentas.addView(fila);

            // Línea fina entre filas
            View separador = new View(this);
            separador.setBackgroundColor(linea);
            separador.setLayoutParams(new TableLayout.LayoutParams(
                    TableLayout.LayoutParams.MATCH_PARENT, dp(1)));

            tableVentas.addView(separador);
        }
    }

    // Crea una celda con el color y el padding del diseño
    private TextView crearCelda(Object texto, int color, boolean negrita) {

        TextView tv = new TextView(this);

        int pad = dp(12);
        tv.setPadding(pad, pad, pad, pad);
        tv.setText(texto == null ? "" : String.valueOf(texto));
        tv.setTextColor(color);
        tv.setTextSize(14);

        if (negrita) {
            tv.setTypeface(tv.getTypeface(), Typeface.BOLD);
        }

        return tv;
    }

    private int dp(int valor) {
        return Math.round(valor * getResources().getDisplayMetrics().density);
    }

    // =========================
    // FIREBASE LOG
    // =========================
    private void logEvento(String tipo, String evento, String detalle) {

        HashMap<String, Object> log = new HashMap<>();

        log.put("tipo", tipo);
        log.put("evento", evento);
        log.put("detalle", detalle);
        log.put("fecha", System.currentTimeMillis());

        db.collection("logs_ventas").add(log);
    }
}