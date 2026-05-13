package com.grupo2.prygrados;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.grupo2.prygrados.Api.ApiClient;
import com.grupo2.prygrados.Api.ApiService;
import com.grupo2.prygrados.Modelo.Producto;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Buzos extends AppCompatActivity {

    private List<Producto> listaBuzos = new ArrayList<>();

    private int index1 = 0;
    private int index2 = 1;

    ImageView img1, img2, prev1, next1, prev2, next2;

    // 🔥 NUEVO (LUPA)
    ImageView btnBuscar;

    TextView txtNombre1, txtPrecio1, txtStock1;
    TextView txtNombre2, txtPrecio2, txtStock2;
    EditText txtBuscar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.buzos);

        txtBuscar = findViewById(R.id.txtBuscar);

        // 🔥 NUEVO
        btnBuscar = findViewById(R.id.btnBuscar);

        img1 = findViewById(R.id.imgCarrusel1);
        img2 = findViewById(R.id.imgCarrusel2);

        prev1 = findViewById(R.id.btnPrev1);
        next1 = findViewById(R.id.btnNext1);

        prev2 = findViewById(R.id.btnPrev2);
        next2 = findViewById(R.id.btnNext2);

        txtNombre1 = findViewById(R.id.txtNombre1);
        txtPrecio1 = findViewById(R.id.txtPrecio1);
        txtStock1 = findViewById(R.id.txtStock1);

        txtNombre2 = findViewById(R.id.txtNombre2);
        txtPrecio2 = findViewById(R.id.txtPrecio2);
        txtStock2 = findViewById(R.id.txtStock2);

        cargarBuzos();

        // 🔎 ENTER (YA LO TENÍAS)
        txtBuscar.setOnKeyListener((v, keyCode, event) -> {
            if (event.getAction() == KeyEvent.ACTION_DOWN &&
                    keyCode == KeyEvent.KEYCODE_ENTER) {

                ejecutarBusqueda();
                return true;
            }
            return false;
        });

        // 🔥 CLICK EN LA LUPA (LO NUEVO)
        btnBuscar.setOnClickListener(v -> ejecutarBusqueda());

        prev1.setOnClickListener(v -> moverCarrusel1(-1));
        next1.setOnClickListener(v -> moverCarrusel1(1));
        prev2.setOnClickListener(v -> moverCarrusel2(-1));
        next2.setOnClickListener(v -> moverCarrusel2(1));
    }

    // 🔥 MÉTODO CENTRALIZADO (EVITA REPETIR CÓDIGO)
    private void ejecutarBusqueda() {
        String texto = txtBuscar.getText().toString().trim();

        if (texto.isEmpty()) {
            cargarBuzos();
        } else {
            buscarProducto(texto);
        }
    }

    private void cargarBuzos() {

        ApiService api = ApiClient.getClient().create(ApiService.class);

        api.listarProductos().enqueue(new Callback<List<Producto>>() {
            @Override
            public void onResponse(Call<List<Producto>> call, Response<List<Producto>> response) {

                if (response.isSuccessful() && response.body() != null) {

                    listaBuzos.clear();

                    for (Producto p : response.body()) {
                        if (p.getCategoria() != null &&
                                p.getCategoria().equalsIgnoreCase("BUZOS")) {

                            listaBuzos.add(p);
                        }
                    }

                    Log.d("BUZOS", "Total: " + listaBuzos.size());

                    if (listaBuzos.isEmpty()) {
                        txtNombre1.setText("No hay buzos");
                        return;
                    }

                    index1 = 0;
                    index2 = (listaBuzos.size() > 1) ? 1 : 0;

                    mostrarProductos();
                }
            }

            @Override
            public void onFailure(Call<List<Producto>> call, Throwable t) {
                txtNombre1.setText("Error conexión");
            }
        });
    }

    private void buscarProducto(String nombre) {

        ApiService api = ApiClient.getClient().create(ApiService.class);

        api.buscarProducto(nombre).enqueue(new Callback<List<Producto>>() {
            @Override
            public void onResponse(Call<List<Producto>> call, Response<List<Producto>> response) {

                if (response.isSuccessful() && response.body() != null) {

                    listaBuzos.clear();

                    for (Producto p : response.body()) {

                        if (p.getCategoria() != null &&
                                p.getCategoria().equalsIgnoreCase("BUZOS")) {

                            listaBuzos.add(p);
                        }
                    }

                    if (listaBuzos.isEmpty()) {
                        txtNombre1.setText("No hay resultados");
                        txtPrecio1.setText("");
                        txtStock1.setText("");
                        return;
                    }

                    index1 = 0;
                    index2 = (listaBuzos.size() > 1) ? 1 : 0;

                    mostrarProductos();
                }
            }

            @Override
            public void onFailure(Call<List<Producto>> call, Throwable t) {
                txtNombre1.setText("Error búsqueda");
            }
        });
    }

    private void mostrarProductos() {

        if (listaBuzos.isEmpty()) return;

        Producto p1 = listaBuzos.get(index1);

        txtNombre1.setText(p1.getNombre());
        txtPrecio1.setText("$ " + p1.getPrecioVenta());
        txtStock1.setText("Stock: " + p1.getStockActual());

        img1.setImageResource(R.drawable.buzo1);

        if (listaBuzos.size() > 1) {

            Producto p2 = listaBuzos.get(index2);

            txtNombre2.setText(p2.getNombre());
            txtPrecio2.setText("$ " + p2.getPrecioVenta());
            txtStock2.setText("Stock: " + p2.getStockActual());

            img2.setImageResource(R.drawable.buzo2);
        }
    }

    private void moverCarrusel1(int d) {
        index1 = (index1 + d + listaBuzos.size()) % listaBuzos.size();
        mostrarProductos();
    }

    private void moverCarrusel2(int d) {
        index2 = (index2 + d + listaBuzos.size()) % listaBuzos.size();
        mostrarProductos();
    }
}