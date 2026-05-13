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

public class Pantalones extends AppCompatActivity {

    private List<Producto> listaPantalones = new ArrayList<>();

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
        setContentView(R.layout.pantalones);

        txtBuscar = findViewById(R.id.txtBuscar);

        // 🔥 CONECTAR LUPA
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

        cargarPantalones();

        // 🔎 ENTER
        txtBuscar.setOnKeyListener((v, keyCode, event) -> {
            if (event.getAction() == KeyEvent.ACTION_DOWN &&
                    keyCode == KeyEvent.KEYCODE_ENTER) {

                ejecutarBusqueda();
                return true;
            }
            return false;
        });

        // 🔥 CLICK LUPA
        btnBuscar.setOnClickListener(v -> ejecutarBusqueda());

        prev1.setOnClickListener(v -> moverCarrusel1(-1));
        next1.setOnClickListener(v -> moverCarrusel1(1));
        prev2.setOnClickListener(v -> moverCarrusel2(-1));
        next2.setOnClickListener(v -> moverCarrusel2(1));
    }

    // 🔥 MÉTODO CENTRAL
    private void ejecutarBusqueda() {
        String texto = txtBuscar.getText().toString().trim();

        if (texto.isEmpty()) {
            cargarPantalones();
        } else {
            buscarProducto(texto);
        }
    }

    private void cargarPantalones() {

        ApiService api = ApiClient.getClient().create(ApiService.class);

        api.listarProductos().enqueue(new Callback<List<Producto>>() {
            @Override
            public void onResponse(Call<List<Producto>> call, Response<List<Producto>> response) {

                if (response.isSuccessful() && response.body() != null) {

                    listaPantalones.clear();

                    for (Producto p : response.body()) {
                        if (p.getCategoria() != null &&
                                p.getCategoria().equalsIgnoreCase("PANTALONES")) {

                            listaPantalones.add(p);
                        }
                    }

                    Log.d("PANTALONES", "Total: " + listaPantalones.size());

                    if (listaPantalones.isEmpty()) {
                        txtNombre1.setText("No hay pantalones");
                        return;
                    }

                    index1 = 0;
                    index2 = (listaPantalones.size() > 1) ? 1 : 0;

                    mostrarProductos();
                }
            }

            @Override
            public void onFailure(Call<List<Producto>> call, Throwable t) {
                txtNombre1.setText("Error conexión");
                t.printStackTrace();
            }
        });
    }

    private void buscarProducto(String nombre) {

        ApiService api = ApiClient.getClient().create(ApiService.class);

        api.buscarProducto(nombre).enqueue(new Callback<List<Producto>>() {
            @Override
            public void onResponse(Call<List<Producto>> call, Response<List<Producto>> response) {

                if (response.isSuccessful() && response.body() != null) {

                    listaPantalones.clear();

                    for (Producto p : response.body()) {
                        if (p.getCategoria() != null &&
                                p.getCategoria().equalsIgnoreCase("PANTALONES")) {

                            listaPantalones.add(p);
                        }
                    }

                    if (listaPantalones.isEmpty()) {
                        txtNombre1.setText("No encontrado");
                        txtPrecio1.setText("");
                        txtStock1.setText("");
                        return;
                    }

                    index1 = 0;
                    index2 = (listaPantalones.size() > 1) ? 1 : 0;

                    mostrarProductos();
                }
            }

            @Override
            public void onFailure(Call<List<Producto>> call, Throwable t) {
                txtNombre1.setText("Error búsqueda");
                t.printStackTrace();
            }
        });
    }

    private void mostrarProductos() {

        if (listaPantalones.isEmpty()) return;

        Producto p1 = listaPantalones.get(index1);

        txtNombre1.setText(p1.getNombre());
        txtPrecio1.setText("$ " + p1.getPrecioVenta());
        txtStock1.setText("Stock: " + p1.getStockActual());

        img1.setImageResource(R.drawable.pan1);

        if (listaPantalones.size() > 1) {

            Producto p2 = listaPantalones.get(index2);

            txtNombre2.setText(p2.getNombre());
            txtPrecio2.setText("$ " + p2.getPrecioVenta());
            txtStock2.setText("Stock: " + p2.getStockActual());

            img2.setImageResource(R.drawable.pan2);
        }
    }

    private void moverCarrusel1(int d) {
        index1 = (index1 + d + listaPantalones.size()) % listaPantalones.size();
        mostrarProductos();
    }

    private void moverCarrusel2(int d) {
        index2 = (index2 + d + listaPantalones.size()) % listaPantalones.size();
        mostrarProductos();
    }
}