package com.grupo2.prygrados;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.grupo2.prygrados.Sync.Sync; // 🔥 IMPORTANTE

public class MainActivity extends AppCompatActivity {

    private Button btnLogin, btnCrearCuenta;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnLogin = findViewById(R.id.btnLogin);
        btnCrearCuenta = findViewById(R.id.btnCrearCuenta);

        // SINCRONIZAR CUANDO ABRE LA APP
        Sync.sincronizarUsuarios(this);

        // Ir a crear cuenta
        btnCrearCuenta.setOnClickListener(v -> {

            Intent intent =
                    new Intent(MainActivity.this,
                            Crear.class);

            startActivity(intent);

        });

        // Ir a login
        btnLogin.setOnClickListener(v -> {

            Intent intent =
                    new Intent(MainActivity.this,
                            login.class);

            startActivity(intent);

        });
    }
}