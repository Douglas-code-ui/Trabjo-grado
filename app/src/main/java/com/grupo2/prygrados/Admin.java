package com.grupo2.prygrados;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class Admin extends AppCompatActivity {

    TextView txtNombreAdmin, txtRolAdmin;

    MaterialButton btnDashboard, btnResumen, btnVentas, btnReportes, btncierre, btnInventario;

    private int idUsuario;
    private String nombre;
    private String rol;

    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin);

        txtNombreAdmin = findViewById(R.id.txtNombreAdmin);
        txtRolAdmin = findViewById(R.id.txtRolAdmin);

        btnDashboard = findViewById(R.id.btnDashboard);
        btnResumen = findViewById(R.id.btnResumen);
        btnInventario = findViewById(R.id.btnInventario);
        btnVentas = findViewById(R.id.btnVentas);
        btnReportes = findViewById(R.id.btnReportes);
        btncierre = findViewById(R.id.btncierre);

        // RECIBIR DATOS DEL LOGIN
        idUsuario = getIntent().getIntExtra("idUsuario",0);
        nombre = getIntent().getStringExtra("nombre");
        rol = getIntent().getStringExtra("rol");

        txtNombreAdmin.setText(nombre);
        txtRolAdmin.setText(rol);

        db = FirebaseFirestore.getInstance();

        logEvento("SUCCESS", "APP_ADMIN", "Entró el administrador: " + nombre + " (id " + idUsuario + ")");

        btnDashboard.setOnClickListener(v -> {

            logEvento("SUCCESS", "CLICK_DASHBOARD", "Abrió Dashboard");

            Intent intent = new Intent(Admin.this, Dashboard.class);

            intent.putExtra("idUsuario", idUsuario);
            intent.putExtra("nombre", nombre);
            intent.putExtra("rol", rol);

            startActivity(intent);

        });

        btnResumen.setOnClickListener(v -> {

            logEvento("SUCCESS", "CLICK_RESUMEN", "Abrió Resumen");

            Intent intent = new Intent(Admin.this, Resumen.class);

            intent.putExtra("idUsuario", idUsuario);

            startActivity(intent);

        });

        btnInventario.setOnClickListener(v -> {

            logEvento("SUCCESS", "CLICK_INVENTARIO", "Abrió Inventario");

            Intent intent = new Intent(Admin.this, Inventari.class);

            intent.putExtra("idUsuario", idUsuario);

            startActivity(intent);

        });

        btnVentas.setOnClickListener(v -> {

            logEvento("SUCCESS", "CLICK_VENTAS", "Abrió Ventas");

            Intent intent = new Intent(Admin.this, Venta.class);

            intent.putExtra("idUsuario", idUsuario);

            startActivity(intent);

        });

        btnReportes.setOnClickListener(v -> {

            logEvento("SUCCESS", "CLICK_REPORTES", "Abrió Reportes");

            Intent intent = new Intent(Admin.this, Reportes.class);

            intent.putExtra("idUsuario", idUsuario);
            intent.putExtra("nombre", nombre);
            intent.putExtra("rol", rol);

            startActivity(intent);

        });

        btncierre.setOnClickListener(v -> {

            logEvento("SUCCESS", "CLICK_CERRAR_SESION", "Cerró sesión");

            Intent intent = new Intent(Admin.this, CerrarSesion.class);

            startActivity(intent);
            finish();

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

        db.collection("logs_admin").add(log);
    }

}