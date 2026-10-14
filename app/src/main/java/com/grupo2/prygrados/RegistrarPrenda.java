package com.grupo2.prygrados;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.grupo2.prygrados.Api.ApiClient;
import com.grupo2.prygrados.Api.ApiService;
import com.grupo2.prygrados.Modelo.Producto;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegistrarPrenda extends AppCompatActivity {

    private EditText txtNombre;
    private EditText txtTalla;
    private EditText txtPrecio;
    private EditText txtStock;

    private Spinner spCategoria;

    private MaterialButton btnGuardar;

    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registroprenda);

        txtNombre = findViewById(R.id.txtNombre);
        txtTalla = findViewById(R.id.txtTalla);
        txtPrecio = findViewById(R.id.txtPrecio);
        txtStock = findViewById(R.id.txtStock);

        spCategoria = findViewById(R.id.spCategoria);

        btnGuardar = findViewById(R.id.btnGuardar);

        db = FirebaseFirestore.getInstance();

        logEvento("SUCCESS", "APP_REGISTRAR_PRENDA", "Entró a registrar prenda");

        String[] categorias = {
                "BUZOS",
                "PANTALONES"
        };

        // 🎨 Layouts oscuros propios: texto blanco en el Spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                R.layout.item_spinner,
                categorias
        );

        adapter.setDropDownViewResource(R.layout.item_spinner_dropdown);

        spCategoria.setAdapter(adapter);

        btnGuardar.setOnClickListener(v -> guardarProducto());
    }

    private void guardarProducto() {

        String nombre = txtNombre.getText().toString().trim();
        String categoria = spCategoria.getSelectedItem().toString();
        String talla = txtTalla.getText().toString().trim();
        String precioStr = txtPrecio.getText().toString().trim();
        String stockStr = txtStock.getText().toString().trim();

        if (nombre.isEmpty()) {
            logEvento("ERROR", "VALIDACION_NOMBRE", "Nombre vacío");
            txtNombre.setError("Ingrese el nombre");
            return;
        }

        if (talla.isEmpty()) {
            logEvento("ERROR", "VALIDACION_TALLA", "Talla vacía");
            txtTalla.setError("Ingrese la talla");
            return;
        }

        if (precioStr.isEmpty()) {
            logEvento("ERROR", "VALIDACION_PRECIO", "Precio vacío");
            txtPrecio.setError("Ingrese el precio");
            return;
        }

        if (stockStr.isEmpty()) {
            logEvento("ERROR", "VALIDACION_STOCK", "Stock vacío");
            txtStock.setError("Ingrese el stock");
            return;
        }

        double precio;
        int stock;

        try {
            precio = Double.parseDouble(precioStr);
        } catch (NumberFormatException e) {
            logEvento("ERROR", "VALIDACION_PRECIO", "Precio inválido");
            txtPrecio.setError("Precio inválido");
            return;
        }

        try {
            stock = Integer.parseInt(stockStr);
        } catch (NumberFormatException e) {
            logEvento("ERROR", "VALIDACION_STOCK", "Stock inválido");
            txtStock.setError("Stock inválido");
            return;
        }

        if (precio <= 0) {
            logEvento("ERROR", "VALIDACION_PRECIO", "Precio menor o igual a 0");
            txtPrecio.setError("El precio debe ser mayor a 0");
            return;
        }

        if (stock < 0) {
            logEvento("ERROR", "VALIDACION_STOCK", "Stock negativo");
            txtStock.setError("El stock no puede ser negativo");
            return;
        }

        Producto producto = new Producto();

        producto.setNombre(nombre);
        producto.setCategoria(categoria);
        producto.setTalla(talla);
        producto.setPrecioVenta(precio);
        producto.setStockActual(stock);

        logEvento("SUCCESS", "CLICK_GUARDAR_PRENDA", "Intento de guardar: " + nombre + " / " + categoria);

        // Evita guardar dos veces si tocan el botón varias veces
        btnGuardar.setEnabled(false);

        ApiService api =
                ApiClient.getClient().create(ApiService.class);

        api.guardarProducto(producto)
                .enqueue(new Callback<Producto>() {

                    @Override
                    public void onResponse(Call<Producto> call,
                                           Response<Producto> response) {

                        btnGuardar.setEnabled(true);

                        if (response.isSuccessful()) {

                            logEvento("SUCCESS", "PRENDA_REGISTRADA", nombre + " / " + categoria);

                            Toast.makeText(
                                    RegistrarPrenda.this,
                                    "Prenda registrada",
                                    Toast.LENGTH_LONG
                            ).show();

                            finish();

                        } else {

                            logEvento("ERROR", "GUARDAR_FALLIDO", "Código: " + response.code());

                            Toast.makeText(
                                    RegistrarPrenda.this,
                                    "Error al guardar",
                                    Toast.LENGTH_LONG
                            ).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Producto> call,
                                          Throwable t) {

                        btnGuardar.setEnabled(true);

                        logEvento("ERROR", "ERROR_CONEXION_GUARDAR", String.valueOf(t.getMessage()));

                        Toast.makeText(
                                RegistrarPrenda.this,
                                "Error de conexión: " + t.getMessage(),
                                Toast.LENGTH_LONG
                        ).show();
                    }
                });
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

        db.collection("logs_registro_prenda").add(log);
    }
}