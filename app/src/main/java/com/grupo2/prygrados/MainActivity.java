package com.grupo2.prygrados;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;
import com.grupo2.prygrados.Sync.Sync;

public class MainActivity extends AppCompatActivity {

    private Button btnLogin, btnCrearCuenta;

    private Handler handler = new Handler();
    private Runnable runnableSync;

    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnLogin = findViewById(R.id.btnLogin);
        btnCrearCuenta = findViewById(R.id.btnCrearCuenta);

        db = FirebaseFirestore.getInstance();

        // 🔥 LOG: cuando abre la app
        logEvento("SUCCESS", "APP_INICIO", "Usuario abrió la aplicación");

        // =========================
        // SINCRONIZACIÓN
        // =========================
        runnableSync = new Runnable() {
            @Override
            public void run() {

                Sync.sincronizarUsuarios(MainActivity.this);

                handler.postDelayed(this, 10000);
            }
        };

        handler.post(runnableSync);

        // =========================
        // BOTÓN LOGIN
        // =========================
        btnLogin.setOnClickListener(v -> {

            logEvento(
                    "SUCCESS",
                    "CLICK_LOGIN",
                    "Usuario presionó botón login"
            );

            startActivity(new Intent(MainActivity.this, login.class));
        });

        // =========================
        // BOTÓN CREAR CUENTA
        // =========================
        btnCrearCuenta.setOnClickListener(v -> {

            logEvento(
                    "SUCCESS",
                    "CLICK_CREAR_CUENTA",
                    "Usuario presionó botón crear cuenta"
            );

            startActivity(new Intent(MainActivity.this, Crear.class));
        });
    }

    // =========================
    // FUNCIÓN LOG SIMPLE
    // =========================
    private void logEvento(String tipo, String evento, String detalle) {

        java.util.HashMap<String, Object> log = new java.util.HashMap<>();

        log.put("tipo", tipo);      // SUCCESS / ERROR
        log.put("evento", evento);  // CLICK_LOGIN, CLICK_CREAR, etc
        log.put("detalle", detalle);
        log.put("fecha", System.currentTimeMillis());

        db.collection("logs").add(log);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        handler.removeCallbacks(runnableSync);

        logEvento("SUCCESS", "APP_CERRADA", "Usuario cerró la aplicación");
    }
}