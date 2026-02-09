package com.grupo2.prygrados;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private Button btnLogin, btnCrearCuenta;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); // asegúrate que el XML se llame exactamente así

        btnLogin = findViewById(R.id.btnLogin);
        btnCrearCuenta = findViewById(R.id.btnCrearCuenta);

        // Ir a la actividad Crear (crear cuenta)
        btnCrearCuenta.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, Crear.class);
            startActivity(intent);
        });

        // Si luego agregas pantalla de login, aquí irá la navegación
        btnLogin.setOnClickListener(v -> {
            // Ejemplo futuro:
            Intent intent = new Intent(MainActivity.this, login.class);
             startActivity(intent);
        });
    }
}
