package com.grupo2.prygrados;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.firebase.firestore.FirebaseFirestore;
import com.grupo2.prygrados.Api.ApiClient;
import com.grupo2.prygrados.Api.ApiService;
import com.grupo2.prygrados.Modelo.Producto;

import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Inventari extends AppCompatActivity {

    private TableLayout tableInventario;
    private ImageButton btnConfig;
    private com.google.android.material.button.MaterialButton btnCrear;

    private ApiService apiService;
    private FirebaseFirestore db;

    // Stock igual o menor a este número se resalta en rojo
    private static final int STOCK_BAJO = 5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.inventario);

        tableInventario = findViewById(R.id.tableInventario);
        btnConfig = findViewById(R.id.btnConfig);
        btnCrear = findViewById(R.id.btnCrear);

        apiService = ApiClient.getClient().create(ApiService.class);
        db = FirebaseFirestore.getInstance();

        logEvento("SUCCESS", "APP_INVENTARIO", "Entró a pantalla inventario");

        btnCrear.setOnClickListener(v -> {

            logEvento("SUCCESS", "CLICK_REGISTRAR_PRENDA", "Abrió registrar prenda");

            Intent intent = new Intent(Inventari.this, RegistrarPrenda.class);
            startActivity(intent);
        });
    }

    // Se recarga cada vez que se vuelve a esta pantalla,
    // así la prenda recién registrada aparece sin reabrir la app
    @Override
    protected void onResume() {
        super.onResume();
        cargarProductos();
    }

    private void cargarProductos() {

        apiService.obtenerProductos().enqueue(new Callback<List<Producto>>() {

            @Override
            public void onResponse(Call<List<Producto>> call,
                                   Response<List<Producto>> response) {

                if (response.isSuccessful() && response.body() != null) {

                    List<Producto> productos = response.body();

                    logEvento("SUCCESS", "CARGA_INVENTARIO", "Total: " + productos.size());

                    mostrarProductos(productos);

                } else {

                    logEvento("ERROR", "CARGA_INVENTARIO", "Respuesta no exitosa");

                    Toast.makeText(
                            Inventari.this,
                            "No se encontraron productos",
                            Toast.LENGTH_LONG
                    ).show();
                }
            }

            @Override
            public void onFailure(Call<List<Producto>> call,
                                  Throwable t) {

                logEvento("ERROR", "ERROR_CONEXION_INVENTARIO", String.valueOf(t.getMessage()));

                Toast.makeText(
                        Inventari.this,
                        "Error: " + t.getMessage(),
                        Toast.LENGTH_LONG
                ).show();
            }
        });
    }

    // Crea una celda de la tabla con el color y el padding del diseño
    private TextView crearCelda(String texto, int color, boolean negrita) {

        TextView tv = new TextView(this);

        int pad = dp(12);
        tv.setPadding(pad, pad, pad, pad);
        tv.setText(texto);
        tv.setTextColor(color);
        tv.setTextSize(14);

        if (negrita) {
            tv.setTypeface(tv.getTypeface(), android.graphics.Typeface.BOLD);
        }

        return tv;
    }

    private int dp(int valor) {
        return Math.round(valor * getResources().getDisplayMetrics().density);
    }

    private void mostrarProductos(List<Producto> productos) {

        // Deja solo el encabezado (fila 0)
        while (tableInventario.getChildCount() > 1) {
            tableInventario.removeViewAt(1);
        }

        int blanco = ContextCompat.getColor(this, R.color.kemba_white);
        int dorado = ContextCompat.getColor(this, R.color.kemba_gold);
        int linea = ContextCompat.getColor(this, R.color.kemba_stroke);
        int rojo = Color.parseColor("#E24B4A");

        for (Producto producto : productos) {

            TableRow fila = new TableRow(this);

            int stock = producto.getStockActual();
            int colorStock = (stock <= STOCK_BAJO) ? rojo : blanco;

            fila.addView(crearCelda(
                    String.valueOf(producto.getIdProducto()), blanco, false));

            fila.addView(crearCelda(
                    producto.getNombre(), blanco, false));

            fila.addView(crearCelda(
                    String.valueOf(stock), colorStock, stock <= STOCK_BAJO));

            fila.addView(crearCelda(
                    "$ " + producto.getPrecioVenta(), dorado, true));

            tableInventario.addView(fila);

            // Línea fina entre filas
            View separador = new View(this);
            separador.setBackgroundColor(linea);
            separador.setLayoutParams(new TableLayout.LayoutParams(
                    TableLayout.LayoutParams.MATCH_PARENT, dp(1)));

            tableInventario.addView(separador);
        }
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

        db.collection("logs_inventario").add(log);
    }
}