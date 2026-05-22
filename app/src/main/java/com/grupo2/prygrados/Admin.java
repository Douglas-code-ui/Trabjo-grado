package com.grupo2.prygrados;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;

public class Admin extends AppCompatActivity {

    TextView txtNombreAdmin, txtRolAdmin;

    MaterialButton btnResumen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin);

        txtNombreAdmin = findViewById(R.id.txtNombreAdmin);
        txtRolAdmin = findViewById(R.id.txtRolAdmin);

        btnResumen = findViewById(R.id.btnResumen);

        // RECIBIR DATOS
        String nombre =
                getIntent().getStringExtra("nombre");

        String rol =
                getIntent().getStringExtra("rol");

        txtNombreAdmin.setText(
                nombre != null ? nombre : "Admin");

        txtRolAdmin.setText(
                rol != null ? rol : "Administrador");

        // 🔥 IR A RESUMEN
        btnResumen.setOnClickListener(v -> {

            Intent intent =
                    new Intent(
                            Admin.this,
                            Resumen.class
                    );

            startActivity(intent);

        });
    }
}