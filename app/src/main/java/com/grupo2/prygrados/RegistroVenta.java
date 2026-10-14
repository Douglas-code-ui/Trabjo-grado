package com.grupo2.prygrados;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;
import com.grupo2.prygrados.Api.ApiClient;
import com.grupo2.prygrados.Api.ApiService;
import com.grupo2.prygrados.Modelo.Producto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegistroVenta extends AppCompatActivity {

    Spinner spinnerPrenda;
    EditText etCantidad, etTotal;
    TextView txtNombreEmpleado, txtRolEmpleado;
    Button btnRegistrar;
    RadioGroup radioGroupPago;

    ArrayList<Producto> listaProductos = new ArrayList<>();
    ArrayAdapter<Producto> adapter;

    ApiService api;
    FirebaseFirestore db;
    private int idUsuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.registroventa);

        spinnerPrenda = findViewById(R.id.spinnerPrenda);
        etCantidad = findViewById(R.id.etCantidad);
        etTotal = findViewById(R.id.etTotal);
        btnRegistrar = findViewById(R.id.btnRegistrar);
        radioGroupPago = findViewById(R.id.rgPago);

        txtNombreEmpleado = findViewById(R.id.txtNombreEmpleado);
        txtRolEmpleado = findViewById(R.id.txtRolEmpleado);

        api = ApiClient.getClient().create(ApiService.class);
        db = FirebaseFirestore.getInstance();

        logEvento("SUCCESS", "APP_REGISTRO_VENTA", "Entró a registro de venta");

        idUsuario = getIntent().getIntExtra("idUsuario", 0);

        String nombre = getIntent().getStringExtra("nombre");
        String rol = getIntent().getStringExtra("rol");

        txtNombreEmpleado.setText(nombre != null ? nombre : "");
        txtRolEmpleado.setText(rol != null ? rol : "");

        // 🎨 Layouts oscuros propios: texto blanco en el Spinner
        adapter = new ArrayAdapter<>(
                this,
                R.layout.item_spinner,
                listaProductos
        );
        adapter.setDropDownViewResource(R.layout.item_spinner_dropdown);

        spinnerPrenda.setAdapter(adapter);

        cargarProductos();

        spinnerPrenda.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                calcularTotal();
                logEvento("SUCCESS", "SELECCION_PRODUCTO", "Producto seleccionado");
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        etCantidad.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override public void afterTextChanged(Editable s) {
                calcularTotal();
            }
        });

        btnRegistrar.setOnClickListener(v -> {

            logEvento("SUCCESS", "CLICK_REGISTRAR_VENTA", "Intento de registro");

            btnRegistrar.setEnabled(false);
            registrarVenta();
        });
    }

    // =========================
    // CARGAR PRODUCTOS
    // =========================
    private void cargarProductos() {

        api.listarProductos().enqueue(new Callback<List<Producto>>() {

            @Override
            public void onResponse(Call<List<Producto>> call, Response<List<Producto>> response) {

                if (response.isSuccessful() && response.body() != null) {

                    listaProductos.clear();
                    listaProductos.addAll(response.body());
                    adapter.notifyDataSetChanged();

                    logEvento("SUCCESS", "CARGA_PRODUCTOS", "Total: " + listaProductos.size());

                } else {

                    logEvento("ERROR", "CARGA_PRODUCTOS", "Respuesta no exitosa");

                    Toast.makeText(RegistroVenta.this,
                            "No hay productos",
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Producto>> call, Throwable t) {

                logEvento("ERROR", "ERROR_CONEXION_PRODUCTOS", String.valueOf(t.getMessage()));

                Toast.makeText(RegistroVenta.this,
                        "Error conexión: " + t.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    // =========================
    // CALCULAR TOTAL
    // =========================
    private void calcularTotal() {

        if (spinnerPrenda.getSelectedItem() == null) return;

        Producto producto = (Producto) spinnerPrenda.getSelectedItem();

        String cantidadStr = etCantidad.getText().toString().trim();

        if (cantidadStr.isEmpty()) {
            etTotal.setText("0");
            return;
        }

        try {
            int cantidad = Integer.parseInt(cantidadStr);
            double total = cantidad * producto.getPrecioVenta();

            // Sin ".0" cuando el total es un número entero
            if (total == Math.floor(total)) {
                etTotal.setText(String.valueOf((long) total));
            } else {
                etTotal.setText(String.valueOf(total));
            }

        } catch (Exception e) {

            etTotal.setText("0");

            logEvento("ERROR", "CALCULO_TOTAL", "Cantidad inválida");
        }
    }

    // =========================
    // REGISTRAR VENTA
    // =========================
    private void registrarVenta() {

        if (spinnerPrenda.getSelectedItem() == null) {

            logEvento("ERROR", "VALIDACION_PRODUCTO", "No seleccionó producto");

            Toast.makeText(this, "Selecciona una prenda", Toast.LENGTH_SHORT).show();
            btnRegistrar.setEnabled(true);
            return;
        }

        String cantidadStr = etCantidad.getText().toString().trim();

        if (cantidadStr.isEmpty()) {

            logEvento("ERROR", "VALIDACION_CANTIDAD", "Cantidad vacía");

            Toast.makeText(this, "Ingresa la cantidad", Toast.LENGTH_SHORT).show();
            btnRegistrar.setEnabled(true);
            return;
        }

        int cantidad;
        try {
            cantidad = Integer.parseInt(cantidadStr);
        } catch (NumberFormatException e) {
            cantidad = 0;
        }

        if (cantidad <= 0) {

            logEvento("ERROR", "VALIDACION_CANTIDAD", "Cantidad menor o igual a 0");

            Toast.makeText(this, "La cantidad debe ser mayor a 0", Toast.LENGTH_SHORT).show();
            btnRegistrar.setEnabled(true);
            return;
        }

        int selectedId = radioGroupPago.getCheckedRadioButtonId();

        if (selectedId == -1) {

            logEvento("ERROR", "VALIDACION_PAGO", "No seleccionó método de pago");

            Toast.makeText(this, "Selecciona el tipo de pago", Toast.LENGTH_SHORT).show();
            btnRegistrar.setEnabled(true);
            return;
        }

        RadioButton radioSeleccionado = findViewById(selectedId);
        String metodoPago = radioSeleccionado.getText().toString();

        Producto producto = (Producto) spinnerPrenda.getSelectedItem();

        api.registrarVenta(
                producto.getIdProducto(),
                cantidad,
                metodoPago,
                idUsuario
        ).enqueue(new Callback<String>() {

            @Override
            public void onResponse(Call<String> call, Response<String> response) {

                btnRegistrar.setEnabled(true);

                if (response.isSuccessful() && response.body() != null) {

                    String idVenta = response.body();

                    logEvento("SUCCESS", "VENTA_REGISTRADA", "ID: " + idVenta);

                    int idVentaNum;
                    try {
                        idVentaNum = Integer.parseInt(idVenta.trim());
                    } catch (NumberFormatException e) {
                        // El servidor respondió algo que no es un número
                        logEvento("ERROR", "ID_VENTA_INVALIDO", idVenta);
                        Toast.makeText(RegistroVenta.this,
                                "Venta registrada, pero no se pudo abrir la factura",
                                Toast.LENGTH_LONG).show();
                        return;
                    }

                    Toast.makeText(RegistroVenta.this,
                            "Venta registrada correctamente",
                            Toast.LENGTH_LONG).show();

                    Intent intent = new Intent(RegistroVenta.this, Factur.class);
                    intent.putExtra("idVenta", idVentaNum);
                    startActivity(intent);
                    finish();

                } else {

                    logEvento("ERROR", "VENTA_FALLIDA", "Código: " + response.code());

                    Toast.makeText(RegistroVenta.this,
                            "Error servidor: " + response.code(),
                            Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {

                btnRegistrar.setEnabled(true);

                logEvento("ERROR", "ERROR_CONEXION_VENTA", String.valueOf(t.getMessage()));

                Toast.makeText(RegistroVenta.this,
                        "Error conexión: " + t.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    // =========================
    // FIRESTORE LOG
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