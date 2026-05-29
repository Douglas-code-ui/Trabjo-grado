package com.grupo2.prygrados;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.grupo2.prygrados.Api.ApiClient;
import com.grupo2.prygrados.Api.ApiService;
import com.grupo2.prygrados.Modelo.VentaTabla;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Venta extends AppCompatActivity {

    private TableLayout tableVentas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ventas);

        tableVentas = findViewById(R.id.tableVentas);

        cargarVentas();
    }

    private void cargarVentas() {

        ApiService api = ApiClient
                .getClient()
                .create(ApiService.class);

        api.obtenerVentasTabla().enqueue(new Callback<List<VentaTabla>>() {

            @Override
            public void onResponse(Call<List<VentaTabla>> call,
                                   Response<List<VentaTabla>> response) {

                if (!response.isSuccessful() || response.body() == null) {

                    Toast.makeText(Venta.this,
                            "No hay ventas",
                            Toast.LENGTH_SHORT).show();

                    return;
                }

                List<VentaTabla> lista = response.body();

                // Elimina todas las filas excepto el encabezado
                while (tableVentas.getChildCount() > 1) {
                    tableVentas.removeViewAt(1);
                }

                for (VentaTabla venta : lista) {

                    TableRow fila = new TableRow(Venta.this);

                    fila.setPadding(10,10,10,10);

                    TextView empleado = new TextView(Venta.this);
                    empleado.setText(venta.getEmpleado());
                    empleado.setTextColor(Color.BLACK);

                    TextView prendas = new TextView(Venta.this);
                    prendas.setText(venta.getPrendas());
                    prendas.setTextColor(Color.BLACK);

                    TextView fecha = new TextView(Venta.this);
                    fecha.setText(venta.getFecha());
                    fecha.setTextColor(Color.BLACK);

                    TextView total = new TextView(Venta.this);
                    total.setText("$ " + venta.getTotal());
                    total.setTextColor(Color.BLACK);

                    fila.addView(empleado);
                    fila.addView(prendas);
                    fila.addView(fecha);
                    fila.addView(total);

                    tableVentas.addView(fila);
                }
            }

            @Override
            public void onFailure(Call<List<VentaTabla>> call,
                                  Throwable t) {

                Toast.makeText(Venta.this,
                        t.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        });
    }
}