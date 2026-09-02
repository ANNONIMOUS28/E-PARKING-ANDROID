package com.eparking.android;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MenuActivity extends AppCompatActivity {

    private TextView tvBienvenida;

    private Button btnVehiculos;
    private Button btnDisponibilidad;
    private Button btnReservas;
    private Button btnHistorial;
    private Button btnPagos;
    private Button btnCerrarSesion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_menu);

        tvBienvenida = findViewById(R.id.tvBienvenidaMenu);

        btnVehiculos = findViewById(R.id.btnVehiculos);
        btnDisponibilidad = findViewById(R.id.btnDisponibilidad);
        btnReservas = findViewById(R.id.btnReservas);
        btnHistorial = findViewById(R.id.btnHistorial);
        btnPagos = findViewById(R.id.btnPagos);
        btnCerrarSesion = findViewById(R.id.btnCerrarSesion);

        mostrarUsuario();
        configurarBotones();
    }

    private void mostrarUsuario() {

        SharedPreferences preferences =
                getSharedPreferences(
                        "EPARKING_SESSION",
                        MODE_PRIVATE
                );

        String nombre =
                preferences.getString("nombre", "");

        if (nombre.isEmpty()) {

            tvBienvenida.setText(
                    "Bienvenido a E-Parking"
            );

        } else {

            tvBienvenida.setText(
                    "Bienvenido, " + nombre
            );
        }
    }

    private void configurarBotones() {

        btnVehiculos.setOnClickListener(v -> {

            Intent intent =
                    new Intent(
                            MenuActivity.this,
                            VehiculosActivity.class
                    );

            startActivity(intent);
        });

        btnDisponibilidad.setOnClickListener(v -> {

            Intent intent =
                    new Intent(
                            MenuActivity.this,
                            DisponibilidadActivity.class
                    );

            startActivity(intent);
        });

        btnReservas.setOnClickListener(v -> {

            Intent intent =
                    new Intent(
                            MenuActivity.this,
                            ReservasActivity.class
                    );

            startActivity(intent);
        });

        btnHistorial.setOnClickListener(v -> {

            Intent intent =
                    new Intent(
                            MenuActivity.this,
                            HistorialActivity.class
                    );

            startActivity(intent);
        });

        btnPagos.setOnClickListener(v -> {

            Intent intent =
                    new Intent(
                            MenuActivity.this,
                            PagosActivity.class
                    );

            startActivity(intent);
        });

        btnCerrarSesion.setOnClickListener(v -> {

            cerrarSesion();
        });
    }

    private void cerrarSesion() {

        SharedPreferences preferences =
                getSharedPreferences(
                        "EPARKING_SESSION",
                        MODE_PRIVATE
                );

        preferences.edit()
                .clear()
                .apply();

        Toast.makeText(
                this,
                "Sesión cerrada correctamente",
                Toast.LENGTH_SHORT
        ).show();

        Intent intent =
                new Intent(
                        MenuActivity.this,
                        MainActivity.class
                );

        intent.setFlags(
                Intent.FLAG_ACTIVITY_NEW_TASK |
                        Intent.FLAG_ACTIVITY_CLEAR_TASK
        );

        startActivity(intent);

        finish();
    }
}