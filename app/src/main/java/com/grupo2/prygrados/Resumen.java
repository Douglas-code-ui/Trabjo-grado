package com.grupo2.prygrados;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

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

    // Stock igual o menor a este número se considera "bajo"
    private static final int STOCK_BAJO = 5;

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
        // CLICK EN LAS PESTAÑAS
        // =========================

        tabProductos.setOnClickListener(v -> seleccionarTab(tabProductos));
        tabVentas.setOnClickListener(v -> seleccionarTab(tabVentas));
        tabStock.setOnClickListener(v -> seleccionarTab(tabStock));

        // Al abrir, la pestaña activa es "Total de Productos"
        seleccionarTab(tabProductos);
    }

    // Se recargan los datos cada vez que se vuelve a esta pantalla
    @Override
    protected void onResume() {
        super.onResume();

        cargarProductos();
        cargarVentasHoy();
        cargarStockBajo();
    }

    // =========================
    // PESTAÑAS
    // =========================

    private void seleccionarTab(TextView tabActiva) {

        // Muestra solo el contenido de la pestaña elegida
        layoutProductos.setVisibility(tabActiva == tabProductos ? View.VISIBLE : View.GONE);
        layoutVentas.setVisibility(tabActiva == tabVentas ? View.VISIBLE : View.GONE);
        layoutStock.setVisibility(tabActiva == tabStock ? View.VISIBLE : View.GONE);

        // Activa en dorado, las demás en gris claro
        int activo = ContextCompat.getColor(this, R.color.kemba_gold);
        int inactivo = ContextCompat.getColor(this, R.color.kemba_hint);

        tabProductos.setTextColor(tabActiva == tabProductos ? activo : inactivo);
        tabVentas.setTextColor(tabActiva == tabVentas ? activo : inactivo);
        tabStock.setTextColor(tabActiva == tabStock ? activo : inactivo);
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

                                // Si el producto no trae categoría, se salta
                                if (p.getCategoria() == null) continue;

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

                        avisarError(t);
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

                            SimpleDateFormat formato =
                                    new SimpleDateFormat(
                                            "yyyy-MM-dd",
                                            Locale.getDefault()
                                    );

                            String fechaHoy = formato.format(new Date());

                            for (Venta venta : response.body()) {

                                if (venta.getFecha() != null) {

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

                            if (lista.isEmpty()) {
                                lista.add("No hay ventas registradas hoy");
                            }

                            // 🎨 Layout propio: texto blanco sobre fondo oscuro
                            ArrayAdapter<String> adapter =
                                    new ArrayAdapter<>(
                                            Resumen.this,
                                            R.layout.item_lista,
                                            lista
                                    );

                            listVentas.setAdapter(adapter);
                        }
                    }

                    @Override
                    public void onFailure(
                            Call<List<Venta>> call,
                            Throwable t) {

                        avisarError(t);
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

                                if (p.getStockActual() <= STOCK_BAJO) {

                                    lista.add(
                                            p.getNombre()
                                                    + " - Stock: "
                                                    + p.getStockActual()
                                    );
                                }
                            }

                            if (lista.isEmpty()) {
                                lista.add("No hay productos con stock bajo");
                            }

                            // 🎨 Layout propio: texto blanco sobre fondo oscuro
                            ArrayAdapter<String> adapter =
                                    new ArrayAdapter<>(
                                            Resumen.this,
                                            R.layout.item_lista,
                                            lista
                                    );

                            listStock.setAdapter(adapter);
                        }
                    }

                    @Override
                    public void onFailure(
                            Call<List<Producto>> call,
                            Throwable t) {

                        avisarError(t);
                    }
                });
    }

    // Un solo aviso de error, aunque fallen las tres consultas a la vez
    private long ultimoAviso = 0;

    private void avisarError(Throwable t) {

        long ahora = System.currentTimeMillis();

        if (ahora - ultimoAviso < 3000) return;
        ultimoAviso = ahora;

        Toast.makeText(
                this,
                "Error de conexión: " + t.getMessage(),
                Toast.LENGTH_LONG
        ).show();
    }
}