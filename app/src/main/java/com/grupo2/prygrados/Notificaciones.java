package com.grupo2.prygrados;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

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
    }

    // Se recarga cada vez que se abre o se vuelve a la pantalla,
    // así los reportes nuevos aparecen sin reabrir la app
    @Override
    protected void onResume() {
        super.onResume();
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

                        if (fechaBonita == null) {
                            fechaBonita = "Sin fecha";
                        }

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

                        String descripcion = r.getDescripcion() != null
                                ? r.getDescripcion()
                                : "";

                        lista.add(
                                "📅 " + fechaBonita
                                        + "\n\n📢 "
                                        + descripcion
                        );
                    }

                    if (lista.isEmpty()) {
                        lista.add("No hay reportes por ahora");
                    }

                    // 🎨 Layout propio: texto blanco sobre fondo oscuro
                    ArrayAdapter<String> adapter =
                            new ArrayAdapter<>(
                                    Notificaciones.this,
                                    R.layout.item_lista,
                                    lista
                            );

                    listView.setAdapter(adapter);

                } else {

                    Toast.makeText(
                            Notificaciones.this,
                            "No se pudieron cargar los reportes",
                            Toast.LENGTH_LONG
                    ).show();
                }

            }

            @Override
            public void onFailure(Call<List<Reporte>> call,
                                  Throwable t) {

                Toast.makeText(
                        Notificaciones.this,
                        "Error de conexión: " + t.getMessage(),
                        Toast.LENGTH_LONG
                ).show();
            }
        });

    }

}