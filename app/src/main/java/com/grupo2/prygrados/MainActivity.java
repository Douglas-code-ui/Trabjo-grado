package com.grupo2.prygrados;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.grupo2.prygrados.Sync.Sync;

public class MainActivity extends AppCompatActivity {

    private Button btnLogin, btnCrearCuenta;

    // 🔥 Handler para sincronización automática
    private Handler handler = new Handler();

    // 🔥 Cada 10 segundos
    private Runnable runnableSync = new Runnable() {
        @Override
        public void run() {

            // SINCRONIZAR
            Sync.sincronizarUsuarios(MainActivity.this);

            // REPETIR CADA 10 SEGUNDOS
            handler.postDelayed(this, 10000);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnLogin = findViewById(R.id.btnLogin);
        btnCrearCuenta = findViewById(R.id.btnCrearCuenta);

        // 🔥 INICIAR SINCRONIZACIÓN AUTOMÁTICA
        handler.post(runnableSync);

        // =========================
        // CREAR CUENTA
        // =========================

        btnCrearCuenta.setOnClickListener(v -> {

            Intent intent =
                    new Intent(MainActivity.this,
                            Crear.class);

            startActivity(intent);

        });

        // =========================
        // LOGIN
        // =========================

        btnLogin.setOnClickListener(v -> {

            Intent intent =
                    new Intent(MainActivity.this,
                            login.class);

            startActivity(intent);

        });
    }

    // 🔥 DETENER HANDLER
    @Override
    protected void onDestroy() {
        super.onDestroy();

        handler.removeCallbacks(runnableSync);
    }
}