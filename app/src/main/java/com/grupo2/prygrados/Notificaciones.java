package com.grupo2.prygrados;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.grupo2.prygrados.Api.ApiClient;
import com.grupo2.prygrados.Api.ApiService;
import com.grupo2.prygrados.Modelo.Reporte;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Notificaciones extends AppCompatActivity {

    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notificaciones);

        listView = findViewById(R.id.listReportes);

        cargarReportes();
    }

    private void cargarReportes() {

        ApiService api =
                ApiClient.getClient().create(ApiService.class);

        api.listarReportes().enqueue(new Callback<List<Reporte>>() {

            @Override
            public void onResponse(Call<List<Reporte>> call,
                                   Response<List<Reporte>> response) {

                if (response.isSuccessful() && response.body() != null) {

                    ArrayList<String> lista = new ArrayList<>();

                    for (Reporte r : response.body()) {

                        String fechaBonita = r.getFechaGeneracion();

                        try {

                            SimpleDateFormat entrada =
                                    new SimpleDateFormat(
                                            "yyyy-MM-dd'T'HH:mm:ss.SSSX",
                                            Locale.getDefault());

                            SimpleDateFormat salida =
                                    new SimpleDateFormat(
                                            "dd/MM/yyyy HH:mm",
                                            Locale.getDefault());

                            Date fecha =
                                    entrada.parse(r.getFechaGeneracion());

                            fechaBonita = salida.format(fecha);

                        } catch (Exception e) {

                            // Si no puede convertir la fecha,
                            // muestra la original.
                        }

                        lista.add(
                                "📅 " + fechaBonita
                                        + "\n\n📢 "
                                        + r.getDescripcion()
                        );
                    }

                    ArrayAdapter<String> adapter =
                            new ArrayAdapter<>(
                                    Notificaciones.this,
                                    android.R.layout.simple_list_item_1,
                                    lista
                            );

                    listView.setAdapter(adapter);
                }

            }

            @Override
            public void onFailure(Call<List<Reporte>> call,
                                  Throwable t) {

            }
        });

    }

}