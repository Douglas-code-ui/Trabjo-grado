package com.grupo2.prygrados;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.grupo2.prygrados.Api.ApiClient;
import com.grupo2.prygrados.Api.ApiService;
import com.grupo2.prygrados.Modelo.Producto;
import android.content.Intent;
import com.grupo2.prygrados.Modelo.Reporte;
import android.view.View;

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
    ImageView btnBuscar;

    TextView txtNombre1, txtPrecio1, txtStock1;
    TextView txtNombre2, txtPrecio2, txtStock2;
    EditText txtBuscar;
    ImageView imgNotificacion;
    TextView txtContador;

    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.buzos);

        txtBuscar = findViewById(R.id.txtBuscar);
        btnBuscar = findViewById(R.id.btnBuscar);
        imgNotificacion = findViewById(R.id.imgNotificacion);
        txtContador = findViewById(R.id.txtContador);

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

        db = FirebaseFirestore.getInstance();

        // 🔥 LOG: entrada pantalla
        logEvento("SUCCESS", "APP_BUZOS", "Entró a pantalla buzos");

        cargarBuzos();

        txtBuscar.setOnKeyListener((v, keyCode, event) -> {
            if (event.getAction() == KeyEvent.ACTION_DOWN &&
                    keyCode == KeyEvent.KEYCODE_ENTER) {

                ejecutarBusqueda();
                return true;
            }
            return false;
        });
        cargarNotificaciones();

        imgNotificacion.setOnClickListener(v -> {

            Intent intent = new Intent(
                    Buzos.this,
                    Notificaciones.class
            );

            startActivity(intent);
        });

        btnBuscar.setOnClickListener(v -> ejecutarBusqueda());


        prev1.setOnClickListener(v -> moverCarrusel1(-1));
        next1.setOnClickListener(v -> moverCarrusel1(1));
        prev2.setOnClickListener(v -> moverCarrusel2(-1));
        next2.setOnClickListener(v -> moverCarrusel2(1));
    }

    // =========================
    // BUSQUEDA CENTRAL
    // =========================
    private void ejecutarBusqueda() {

        String texto = txtBuscar.getText().toString().trim();

        if (texto.isEmpty()) {

            logEvento("SUCCESS", "CLICK_BUSCAR_VACIO", "Recargando buzos");

            cargarBuzos();
        } else {

            logEvento("SUCCESS", "BUSQUEDA", "Buscando: " + texto);

            buscarProducto(texto);
        }
    }

    // =========================
    // CARGAR BUZOS
    // =========================
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

                    logEvento("SUCCESS", "CARGA_BUZOS", "Total: " + listaBuzos.size());

                    if (listaBuzos.isEmpty()) {
                        txtNombre1.setText("No hay buzos");
                        return;
                    }

                    index1 = 0;
                    index2 = (listaBuzos.size() > 1) ? 1 : 0;

                    mostrarProductos();

                } else {

                    logEvento("ERROR", "API_BUZOS", "Respuesta no exitosa");
                }
            }

            @Override
            public void onFailure(Call<List<Producto>> call, Throwable t) {

                logEvento("ERROR", "ERROR_CONEXION", t.getMessage());

                txtNombre1.setText("Error conexión");
            }
        });
    }

    // =========================
    // BUSCAR PRODUCTO
    // =========================
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

                        logEvento("ERROR", "SIN_RESULTADOS", nombre);

                        txtNombre1.setText("No hay resultados");
                        return;
                    }

                    logEvento("SUCCESS", "RESULTADO_BUSQUEDA", nombre);

                    index1 = 0;
                    index2 = (listaBuzos.size() > 1) ? 1 : 0;

                    mostrarProductos();
                }
            }

            @Override
            public void onFailure(Call<List<Producto>> call, Throwable t) {

                logEvento("ERROR", "ERROR_BUSQUEDA", t.getMessage());

                txtNombre1.setText("Error búsqueda");
            }
        });
    }

    // =========================
    // MOSTRAR
    // =========================
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
    private void cargarNotificaciones(){

        ApiService api =
                ApiClient.getClient().create(ApiService.class);

        api.listarReportes().enqueue(new Callback<List<Reporte>>() {

            @Override
            public void onResponse(Call<List<Reporte>> call,
                                   Response<List<Reporte>> response) {

                if(response.isSuccessful() && response.body()!=null){

                    int cantidad = response.body().size();

                    if(cantidad==0){

                        txtContador.setVisibility(View.GONE);

                    }else{

                        txtContador.setVisibility(View.VISIBLE);
                        txtContador.setText(String.valueOf(cantidad));
                    }

                }
            }

            @Override
            public void onFailure(Call<List<Reporte>> call,
                                  Throwable t) {

            }
        });

    }

    private void moverCarrusel1(int d) {
        index1 = (index1 + d + listaBuzos.size()) % listaBuzos.size();
        mostrarProductos();
    }

    private void moverCarrusel2(int d) {
        index2 = (index2 + d + listaBuzos.size()) % listaBuzos.size();
        mostrarProductos();
    }

    // =========================
    // FIREBASE LOG
    // =========================
    private void logEvento(String tipo, String evento, String detalle) {

        java.util.HashMap<String, Object> log = new java.util.HashMap<>();

        log.put("tipo", tipo);
        log.put("evento", evento);
        log.put("detalle", detalle);
        log.put("fecha", System.currentTimeMillis());

        db.collection("logs_buzos").add(log);
    }
}