package com.grupo2.prygrados;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;

public class Admin extends AppCompatActivity {

    TextView txtNombreAdmin, txtRolAdmin;

    MaterialButton btnResumen, btnVentas, btnReportes, btncierre, btnInventario;

    private int idUsuario;
    private String nombre;
    private String rol;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin);

        txtNombreAdmin = findViewById(R.id.txtNombreAdmin);
        txtRolAdmin = findViewById(R.id.txtRolAdmin);

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

        btnResumen.setOnClickListener(v -> {

            Intent intent = new Intent(Admin.this, Resumen.class);

            intent.putExtra("idUsuario", idUsuario);

            startActivity(intent);

        });

        btnInventario.setOnClickListener(v -> {

            Intent intent = new Intent(Admin.this, Inventari.class);

            intent.putExtra("idUsuario", idUsuario);

            startActivity(intent);

        });

        btnVentas.setOnClickListener(v -> {

            Intent intent = new Intent(Admin.this, Venta.class);

            intent.putExtra("idUsuario", idUsuario);

            startActivity(intent);

        });

        btnReportes.setOnClickListener(v -> {

            Intent intent = new Intent(Admin.this, Reportes.class);

            intent.putExtra("idUsuario", idUsuario);
            intent.putExtra("nombre", nombre);
            intent.putExtra("rol", rol);

            startActivity(intent);

        });

        btncierre.setOnClickListener(v -> {

            Intent intent = new Intent(Admin.this, CerrarSesion.class);

            startActivity(intent);
            finish();

        });


    }

}