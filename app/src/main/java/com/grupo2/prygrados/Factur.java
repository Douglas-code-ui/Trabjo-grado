package com.grupo2.prygrados;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.grupo2.prygrados.Api.ApiClient;
import com.grupo2.prygrados.Api.ApiService;
import com.grupo2.prygrados.Modelo.Factura;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Factur extends AppCompatActivity {

    private TextView txtNumeroFactura;
    private TextView txtFecha;
    private TextView txtTotal;
    private TextView txtVentaId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.factura);

        txtNumeroFactura =
                findViewById(R.id.txtNumeroFactura);

        txtFecha =
                findViewById(R.id.txtFecha);

        txtTotal =
                findViewById(R.id.txtTotal);

        txtVentaId =
                findViewById(R.id.txtVentaId);

        // =========================
        // RECIBIR ID VENTA
        // =========================

        int ventaId =
                getIntent().getIntExtra(
                        "idVenta",
                        0
                );

        cargarFactura(ventaId);
    }

    // =========================
    // CARGAR FACTURA
    // =========================

    private void cargarFactura(int ventaId){

        ApiService api =
                ApiClient.getClient()
                        .create(ApiService.class);

        api.obtenerFacturaPorVenta(ventaId)
                .enqueue(new Callback<Factura>() {

                    @Override
                    public void onResponse(
                            Call<Factura> call,
                            Response<Factura> response) {

                        if(response.isSuccessful()
                                && response.body() != null){

                            Factura factura =
                                    response.body();

                            txtNumeroFactura.setText(
                                    factura.getNumeroFactura());

                            txtFecha.setText(
                                    factura.getFecha());

                            txtTotal.setText(
                                    "$ " + factura.getTotal());

                            txtVentaId.setText(
                                    String.valueOf(
                                            factura.getVentaId()));

                        } else {

                            txtNumeroFactura.setText(
                                    "Factura no encontrada");
                        }
                    }

                    @Override
                    public void onFailure(
                            Call<Factura> call,
                            Throwable t) {

                        txtNumeroFactura.setText(
                                "Error conexión");
                    }
                });
    }
}