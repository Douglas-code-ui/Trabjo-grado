package com.grupo2.prygrados;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class Admin extends AppCompatActivity {

    TextView txtNombreAdmin, txtRolAdmin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin); //

        txtNombreAdmin = findViewById(R.id.txtNombreAdmin);
        txtRolAdmin = findViewById(R.id.txtRolAdmin);

        // RECIBIR DATOS
        String nombre = getIntent().getStringExtra("nombre");
        String rol = getIntent().getStringExtra("rol");

        txtNombreAdmin.setText(nombre != null ? nombre : "Admin");
        txtRolAdmin.setText(rol != null ? rol : "Administrador");
    }
}