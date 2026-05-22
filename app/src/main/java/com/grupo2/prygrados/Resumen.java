package com.grupo2.prygrados;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.grupo2.prygrados.Api.ApiClient;
import com.grupo2.prygrados.Api.ApiService;
import com.grupo2.prygrados.Modelo.Producto;
import com.grupo2.prygrados.Modelo.Venta;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Resumen extends AppCompatActivity {

    private TextView txtBuzos;
    private TextView txtPantalones;

    private TextView tabProductos;
    private TextView tabVentas;
    private TextView tabStock;

    private LinearLayout layoutProductos;
    private LinearLayout layoutVentas;
    private LinearLayout layoutStock;

    private ListView listVentas;
    private ListView listStock;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.resumen);

        // =========================
        // TEXTOS
        // =========================

        txtBuzos = findViewById(R.id.txtBuzos);
        txtPantalones = findViewById(R.id.txtPantalones);

        tabProductos = findViewById(R.id.tabProductos);
        tabVentas = findViewById(R.id.tabVentas);
        tabStock = findViewById(R.id.tabStock);

        // =========================
        // LAYOUTS
        // =========================

        layoutProductos = findViewById(R.id.layoutProductos);
        layoutVentas = findViewById(R.id.layoutVentas);
        layoutStock = findViewById(R.id.layoutStock);

        // =========================
        // LISTAS
        // =========================

        listVentas = findViewById(R.id.listVentas);
        listStock = findViewById(R.id.listStock);

        // =========================
        // CARGAR DATOS
        // =========================

        cargarProductos();
        cargarVentasHoy();
        cargarStockBajo();

        // =========================
        // CLICK PRODUCTOS
        // =========================

        tabProductos.setOnClickListener(v -> {

            layoutProductos.setVisibility(View.VISIBLE);
            layoutVentas.setVisibility(View.GONE);
            layoutStock.setVisibility(View.GONE);

        });

        // =========================
        // CLICK VENTAS
        // =========================

        tabVentas.setOnClickListener(v -> {

            layoutProductos.setVisibility(View.GONE);
            layoutVentas.setVisibility(View.VISIBLE);
            layoutStock.setVisibility(View.GONE);

        });

        // =========================
        // CLICK STOCK
        // =========================

        tabStock.setOnClickListener(v -> {

            layoutProductos.setVisibility(View.GONE);
            layoutVentas.setVisibility(View.GONE);
            layoutStock.setVisibility(View.VISIBLE);

        });
    }

    // =========================
    // PRODUCTOS
    // =========================

    private void cargarProductos() {

        ApiService api =
                ApiClient.getClient()
                        .create(ApiService.class);

        api.listarProductos().enqueue(
                new Callback<List<Producto>>() {

                    @Override
                    public void onResponse(
                            Call<List<Producto>> call,
                            Response<List<Producto>> response) {

                        if (response.isSuccessful()
                                && response.body() != null) {

                            int buzos = 0;
                            int pantalones = 0;

                            for (Producto p : response.body()) {

                                if (p.getCategoria()
                                        .equalsIgnoreCase("BUZOS")) {

                                    buzos += p.getStockActual();
                                }

                                if (p.getCategoria()
                                        .equalsIgnoreCase("PANTALONES")) {

                                    pantalones += p.getStockActual();
                                }
                            }

                            txtBuzos.setText(
                                    String.valueOf(buzos));

                            txtPantalones.setText(
                                    String.valueOf(pantalones));
                        }
                    }

                    @Override
                    public void onFailure(
                            Call<List<Producto>> call,
                            Throwable t) {

                    }
                });
    }

    // =========================
    // VENTAS DE HOY
    // =========================

    private void cargarVentasHoy() {

        ApiService api =
                ApiClient.getClient()
                        .create(ApiService.class);

        api.obtenerVentas().enqueue(
                new Callback<List<Venta>>() {

                    @Override
                    public void onResponse(
                            Call<List<Venta>> call,
                            Response<List<Venta>> response) {

                        if (response.isSuccessful()
                                && response.body() != null) {

                            List<String> lista =
                                    new ArrayList<>();

                            String fechaHoy =
                                    new SimpleDateFormat(
                                            "yyyy-MM-dd",
                                            Locale.getDefault()
                                    ).format(new Date());

                            for (Venta venta : response.body()) {

                                if (venta.getFecha() != null) {

                                    SimpleDateFormat formato =
                                            new SimpleDateFormat(
                                                    "yyyy-MM-dd",
                                                    Locale.getDefault()
                                            );

                                    String fechaVenta =
                                            formato.format(
                                                    venta.getFecha()
                                            );

                                    if (fechaVenta.equals(fechaHoy)) {

                                        lista.add(
                                                "Total: $" +
                                                        venta.getTotal()
                                                        + "\nMétodo: "
                                                        + venta.getMetodoPago()
                                        );
                                    }
                                }
                            }

                            ArrayAdapter<String> adapter =
                                    new ArrayAdapter<>(
                                            Resumen.this,
                                            android.R.layout.simple_list_item_1,
                                            lista
                                    );

                            listVentas.setAdapter(adapter);
                        }
                    }

                    @Override
                    public void onFailure(
                            Call<List<Venta>> call,
                            Throwable t) {

                    }
                });
    }

    // =========================
    // STOCK BAJO
    // =========================

    private void cargarStockBajo() {

        ApiService api =
                ApiClient.getClient()
                        .create(ApiService.class);

        api.listarProductos().enqueue(
                new Callback<List<Producto>>() {

                    @Override
                    public void onResponse(
                            Call<List<Producto>> call,
                            Response<List<Producto>> response) {

                        if (response.isSuccessful()
                                && response.body() != null) {

                            List<String> lista =
                                    new ArrayList<>();

                            for (Producto p : response.body()) {

                                if (p.getStockActual() <= 5) {

                                    lista.add(
                                            p.getNombre()
                                                    + " - Stock: "
                                                    + p.getStockActual()
                                    );
                                }
                            }

                            ArrayAdapter<String> adapter =
                                    new ArrayAdapter<>(
                                            Resumen.this,
                                            android.R.layout.simple_list_item_1,
                                            lista
                                    );

                            listStock.setAdapter(adapter);
                        }
                    }

                    @Override
                    public void onFailure(
                            Call<List<Producto>> call,
                            Throwable t) {

                    }
                });
    }
}