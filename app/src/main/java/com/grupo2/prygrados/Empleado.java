package com.grupo2.prygrados;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class Empleado extends AppCompatActivity {

    TextView txtNombreEmpleado, txtRolEmpleado;
    EditText txtBuscar;
    ImageView btnConfig;
    Button btnBuzos, btnPantalones, btnRegistrarVenta;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.empleado);

        txtNombreEmpleado = findViewById(R.id.txtNombreEmpleado);
        txtRolEmpleado = findViewById(R.id.txtRolEmpleado);
        txtBuscar = findViewById(R.id.txtBuscar);
        btnConfig = findViewById(R.id.btnConfig);

        btnBuzos = findViewById(R.id.btnBuzos);
        btnPantalones = findViewById(R.id.btnPantalones);
        btnRegistrarVenta = findViewById(R.id.btnRegistrarVenta);

        // 👉 Recibir datos del login
        String nombre = getIntent().getStringExtra("nombre");
        String rol = getIntent().getStringExtra("rol");

        // 👉 Mostrar datos reales del usuario
        txtNombreEmpleado.setText(nombre);
        txtRolEmpleado.setText(rol);

        // 👉 Botón BUZOS
        btnBuzos.setOnClickListener(v -> {
            Intent intent = new Intent(Empleado.this, Buzos.class);
            startActivity(intent);
        });

        // 👉 Botón PANTALONES
        btnPantalones.setOnClickListener(v -> {
            Intent intent = new Intent(Empleado.this, Pantalones.class);
            startActivity(intent);
        });
    }
}
