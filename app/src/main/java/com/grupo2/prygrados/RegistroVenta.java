package com.grupo2.prygrados;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;

import com.grupo2.prygrados.Api.ApiClient;
import com.grupo2.prygrados.Api.ApiService;
import com.grupo2.prygrados.Modelo.Producto;

import java.util.ArrayList;
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

        String nombre = getIntent().getStringExtra("nombre");
        String rol = getIntent().getStringExtra("rol");

        txtNombreEmpleado.setText(nombre != null ? nombre : "");
        txtRolEmpleado.setText(rol != null ? rol : "");

        adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item,
                listaProductos);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPrenda.setAdapter(adapter);

        cargarProductos();

        spinnerPrenda.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, android.view.View view, int position, long id) {
                calcularTotal();
            }
            @Override public void onNothingSelected(AdapterView<?> parent) {}
        });

        etCantidad.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override public void afterTextChanged(Editable s) {
                calcularTotal();
            }
        });

        btnRegistrar.setOnClickListener(v -> {
            btnRegistrar.setEnabled(false);
            registrarVenta();
        });
    }

    private void cargarProductos() {
        api.listarProductos().enqueue(new Callback<List<Producto>>() {
            @Override
            public void onResponse(Call<List<Producto>> call, Response<List<Producto>> response) {

                if (response.isSuccessful() && response.body() != null) {
                    listaProductos.clear();
                    listaProductos.addAll(response.body());
                    adapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(RegistroVenta.this, "No hay productos", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Producto>> call, Throwable t) {
                Toast.makeText(RegistroVenta.this,
                        "Error conexión: " + t.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        });
    }

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
            etTotal.setText(String.valueOf(total));
        } catch (Exception e) {
            etTotal.setText("0");
        }
    }

    private void registrarVenta() {

        if (spinnerPrenda.getSelectedItem() == null) {
            Toast.makeText(this, "Selecciona un producto", Toast.LENGTH_SHORT).show();
            btnRegistrar.setEnabled(true);
            return;
        }

        String cantidadStr = etCantidad.getText().toString().trim();

        if (cantidadStr.isEmpty()) {
            Toast.makeText(this, "Ingrese cantidad", Toast.LENGTH_SHORT).show();
            btnRegistrar.setEnabled(true);
            return;
        }

        int selectedId = radioGroupPago.getCheckedRadioButtonId();

        if (selectedId == -1) {
            Toast.makeText(this, "Selecciona método de pago", Toast.LENGTH_SHORT).show();
            btnRegistrar.setEnabled(true);
            return;
        }

        RadioButton radioSeleccionado = findViewById(selectedId);
        String metodoPago = radioSeleccionado.getText().toString();

        int cantidad = Integer.parseInt(cantidadStr);
        Producto producto = (Producto) spinnerPrenda.getSelectedItem();

        api.registrarVenta(producto.getIdProducto(), cantidad, metodoPago)
                .enqueue(new Callback<String>() {

                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {

                        btnRegistrar.setEnabled(true);

                        if (response.isSuccessful()) {

                            String mensaje = response.body();

                            if (mensaje == null || mensaje.isEmpty()) {
                                mensaje = "Venta registrada correctamente";
                            }

                            Toast.makeText(RegistroVenta.this,
                                    mensaje,
                                    Toast.LENGTH_LONG).show();

                            etCantidad.setText("");
                            etTotal.setText("0");

                        } else {
                            Toast.makeText(RegistroVenta.this,
                                    "Error servidor: " + response.code(),
                                    Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {

                        btnRegistrar.setEnabled(true);

                        Toast.makeText(RegistroVenta.this,
                                "Error conexión: " + t.getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                });
    }
}