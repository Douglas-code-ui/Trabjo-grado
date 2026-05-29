package com.grupo2.prygrados;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;

public class CerrarSesion extends AppCompatActivity {

    private MaterialButton btnVolver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cierresesion); //

        btnVolver = findViewById(R.id.btnVolver);

        btnVolver.setOnClickListener(v -> {

            Intent intent = new Intent(CerrarSesion.this, MainActivity.class);

            // Elimina todas las actividades anteriores para que no pueda volver atrás
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

            startActivity(intent);
            finish();

        });
    }
}