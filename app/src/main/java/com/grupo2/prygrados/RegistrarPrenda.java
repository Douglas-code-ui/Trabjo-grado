package com.grupo2.prygrados;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.grupo2.prygrados.Api.ApiClient;
import com.grupo2.prygrados.Api.ApiService;
import com.grupo2.prygrados.Modelo.Producto;

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

        String[] categorias = {
                "BUZOS",
                "PANTALONES"
        };

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                categorias
        );

        adapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );

        spCategoria.setAdapter(adapter);

        btnGuardar.setOnClickListener(v -> guardarProducto());
    }

    private void guardarProducto() {

        String nombre = txtNombre.getText().toString().trim();
        String categoria = spCategoria.getSelectedItem().toString();
        String talla = txtTalla.getText().toString().trim();
        String precio = txtPrecio.getText().toString().trim();
        String stock = txtStock.getText().toString().trim();

        if(nombre.isEmpty()){
            txtNombre.setError("Ingrese el nombre");
            return;
        }

        if(talla.isEmpty()){
            txtTalla.setError("Ingrese la talla");
            return;
        }

        if(precio.isEmpty()){
            txtPrecio.setError("Ingrese el precio");
            return;
        }

        if(stock.isEmpty()){
            txtStock.setError("Ingrese el stock");
            return;
        }

        Producto producto = new Producto();

        producto.setNombre(nombre);
        producto.setCategoria(categoria);
        producto.setTalla(talla);
        producto.setPrecioVenta(
                Double.parseDouble(precio)
        );
        producto.setStockActual(
                Integer.parseInt(stock)
        );

        ApiService api =
                ApiClient.getClient().create(ApiService.class);

        api.guardarProducto(producto)
                .enqueue(new Callback<Producto>() {

                    @Override
                    public void onResponse(Call<Producto> call,
                                           Response<Producto> response) {

                        if(response.isSuccessful()){

                            Toast.makeText(
                                    RegistrarPrenda.this,
                                    "Prenda registrada",
                                    Toast.LENGTH_LONG
                            ).show();

                            finish();

                        }else{

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

                        Toast.makeText(
                                RegistrarPrenda.this,
                                t.getMessage(),
                                Toast.LENGTH_LONG
                        ).show();
                    }
                });
    }
}