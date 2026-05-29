package com.grupo2.prygrados;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;

public class Empleado extends AppCompatActivity {

    TextView txtNombreEmpleado, txtRolEmpleado;
    EditText txtBuscar;
    ImageView btnConfig;


    Button btnBuzos, btnPantalones, btncierre, btnRegistrarVenta;

    FirebaseFirestore db;

    private int idUsuario;
    private String nombre;
    private String rol;

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
        btncierre = findViewById(R.id.btncierre);

        db = FirebaseFirestore.getInstance();

        // =========================
        // RECIBIR DATOS DEL LOGIN
        // =========================

        idUsuario = getIntent().getIntExtra("idUsuario", 0);
        nombre = getIntent().getStringExtra("nombre");
        rol = getIntent().getStringExtra("rol");

        txtNombreEmpleado.setText(nombre != null ? nombre : "");
        txtRolEmpleado.setText(rol != null ? rol : "");

        // =========================
        // CONFIGURACIÓN
        // =========================

        btnConfig.setOnClickListener(v -> {

            logEvento("SUCCESS",
                    "CLICK_CONFIG",
                    "Empleado abrió configuración");

            // startActivity(new Intent(this, Configuracion.class));

        });

        // =========================
        // BUZOS
        // =========================

        btnBuzos.setOnClickListener(v -> {

            logEvento("SUCCESS",
                    "CLICK_BUZOS",
                    "Entró a productos buzos");

            startActivity(new Intent(
                    Empleado.this,
                    Buzos.class));

        });

        // =========================
        // PANTALONES
        // =========================

        btnPantalones.setOnClickListener(v -> {

            logEvento("SUCCESS",
                    "CLICK_PANTALONES",
                    "Entró a productos pantalones");

            startActivity(new Intent(
                    Empleado.this,
                    Pantalones.class));

        });

        // =========================
        // REGISTRAR VENTA
        // =========================

        btnRegistrarVenta.setOnClickListener(v -> {

            logEvento("SUCCESS",
                    "CLICK_REGISTRAR_VENTA",
                    "Abrió registro de venta");

            Intent intent = new Intent(
                    Empleado.this,
                    RegistroVenta.class);

            intent.putExtra("idUsuario", idUsuario);
            intent.putExtra("nombre", nombre);
            intent.putExtra("rol", rol);

            startActivity(intent);

        });
        // =========================
// CERRAR SESIÓN
// =========================

        btncierre.setOnClickListener(v -> {

            logEvento("SUCCESS",
                    "CERRAR_SESION",
                    "El empleado cerró sesión");

            Intent intent = new Intent(
                    Empleado.this,
                    CerrarSesion.class);

            startActivity(intent);
            finish();

        });

    }

    // =========================
    // FIRESTORE LOG
    // =========================

    private void logEvento(String tipo,
                           String evento,
                           String detalle) {

        java.util.HashMap<String, Object> log =
                new java.util.HashMap<>();

        log.put("tipo", tipo);
        log.put("evento", evento);
        log.put("detalle", detalle);
        log.put("fecha", System.currentTimeMillis());

        db.collection("logs_empleado").add(log);
    }
}