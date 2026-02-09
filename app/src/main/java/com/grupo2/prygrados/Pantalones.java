package com.grupo2.prygrados;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

public class Pantalones extends AppCompatActivity {

    // Carrusel 1
    private int[] carrusel1 = {
            R.drawable.pan1,
            R.drawable.pan2,
    };
    private int index1 = 0;

    // Carrusel 2
    private int[] carrusel2 = {
            R.drawable.pan2,
            R.drawable.pan1,
    };
    private int index2 = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pantalones); // <-- CORREGIDO

        // ------------ Referencias CARRUSEL 1 ----------------
        ImageView img1 = findViewById(R.id.imgCarrusel1);
        ImageView prev1 = findViewById(R.id.btnPrev1);
        ImageView next1 = findViewById(R.id.btnNext1);

        img1.setImageResource(carrusel1[index1]);

        prev1.setOnClickListener(v -> {
            index1--;
            if (index1 < 0) index1 = carrusel1.length - 1;
            img1.setImageResource(carrusel1[index1]);
        });

        next1.setOnClickListener(v -> {
            index1++;
            if (index1 >= carrusel1.length) index1 = 0;
            img1.setImageResource(carrusel1[index1]);
        });

        // ------------ Referencias CARRUSEL 2 ----------------
        ImageView img2 = findViewById(R.id.imgCarrusel2);
        ImageView prev2 = findViewById(R.id.btnPrev2);
        ImageView next2 = findViewById(R.id.btnNext2);

        img2.setImageResource(carrusel2[index2]);

        prev2.setOnClickListener(v -> {
            index2--;
            if (index2 < 0) index2 = carrusel2.length - 1;
            img2.setImageResource(carrusel2[index2]);
        });

        next2.setOnClickListener(v -> {
            index2++;
            if (index2 >= carrusel2.length) index2 = 0;
            img2.setImageResource(carrusel2[index2]);
        });
    }
}
