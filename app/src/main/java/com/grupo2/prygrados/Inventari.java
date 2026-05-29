package com.grupo2.prygrados;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.grupo2.prygrados.Api.ApiClient;
import com.grupo2.prygrados.Api.ApiService;
import com.grupo2.prygrados.Modelo.Producto;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Inventari extends AppCompatActivity {

    private TableLayout tableInventario;
    private ImageButton btnConfig;
    private com.google.android.material.button.MaterialButton btnCrear;

    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.inventario);

        tableInventario = findViewById(R.id.tableInventario);
        btnConfig = findViewById(R.id.btnConfig);
        btnCrear = findViewById(R.id.btnCrear);

        apiService = ApiClient.getClient().create(ApiService.class);

        cargarProductos();

        btnCrear.setOnClickListener(v -> {

        Intent intent =
        new Intent(Inventari.this,
        RegistrarPrenda.class);

        startActivity(intent);
        });

    }

    private void cargarProductos() {

        apiService.obtenerProductos().enqueue(new Callback<List<Producto>>() {

            @Override
            public void onResponse(Call<List<Producto>> call,
                                   Response<List<Producto>> response) {

                if(response.isSuccessful()
                        && response.body() != null){

                    List<Producto> productos =
                            response.body();

                    mostrarProductos(productos);

                }else{

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

                Toast.makeText(
                        Inventari.this,
                        "Error: " + t.getMessage(),
                        Toast.LENGTH_LONG
                ).show();
            }
        });
    }

    private void mostrarProductos(List<Producto> productos){

        while(tableInventario.getChildCount() > 1){
            tableInventario.removeViewAt(1);
        }

        for(Producto producto : productos){

            TableRow fila = new TableRow(this);

            TextView txtId = new TextView(this);
            txtId.setPadding(10,10,10,10);
            txtId.setText(String.valueOf(
                    producto.getIdProducto()));

            TextView txtNombre = new TextView(this);
            txtNombre.setPadding(10,10,10,10);
            txtNombre.setText(
                    producto.getNombre());

            TextView txtStock = new TextView(this);
            txtStock.setPadding(10,10,10,10);
            txtStock.setText(String.valueOf(
                    producto.getStockActual()));

            TextView txtPrecio = new TextView(this);
            txtPrecio.setPadding(10,10,10,10);
            txtPrecio.setText(
                    "$ " + producto.getPrecioVenta());

            fila.addView(txtId);
            fila.addView(txtNombre);
            fila.addView(txtStock);
            fila.addView(txtPrecio);

            tableInventario.addView(fila);
        }
    }
}