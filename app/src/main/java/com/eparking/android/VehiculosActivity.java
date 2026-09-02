package com.eparking.android;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

public class VehiculosActivity extends AppCompatActivity {

    private TextView tvListaVehiculos;

    private static final String URL =
            "http://192.168.1.6:8081/eparking/api/vehiculos";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_vehiculos);

        Button btnAgregarVehiculo =
                findViewById(R.id.btnAgregarVehiculo);

        tvListaVehiculos =
                findViewById(R.id.tvListaVehiculos);

        btnAgregarVehiculo.setOnClickListener(v -> {

            Intent intent = new Intent(
                    VehiculosActivity.this,
                    AgregarVehiculoActivity.class
            );

            startActivity(intent);
        });

        cargarVehiculos();
    }

    private void cargarVehiculos() {

        Log.d(
                "EPARKING_API",
                "Consultando vehículos: " + URL
        );

        RequestQueue queue =
                Volley.newRequestQueue(this);

        JsonArrayRequest request =
                new JsonArrayRequest(
                        Request.Method.GET,
                        URL,
                        null,

                        response -> {

                            Log.d(
                                    "EPARKING_API",
                                    "Vehículos recibidos: "
                                            + response
                            );

                            mostrarVehiculos(response);
                        },

                        error -> {

                            Log.e(
                                    "EPARKING_API",
                                    "Error consultando vehículos",
                                    error
                            );

                            Toast.makeText(
                                    this,
                                    "No se pudieron cargar los vehículos",
                                    Toast.LENGTH_LONG
                            ).show();
                        }
                );

        queue.add(request);
    }

    private void mostrarVehiculos(JSONArray vehiculos) {

        if (vehiculos.length() == 0) {

            tvListaVehiculos.setText(
                    "No hay vehículos para mostrar"
            );

            return;
        }

        StringBuilder texto =
                new StringBuilder();

        for (int i = 0; i < vehiculos.length(); i++) {

            try {

                JSONObject vehiculo =
                        vehiculos.getJSONObject(i);

                String placa =
                        vehiculo.getString("placa");

                String tipo =
                        vehiculo.getString("tipo");

                String color =
                        vehiculo.getString("color");

                String propietario =
                        vehiculo.getString("propietario");

                int usuarioId =
                        vehiculo.getInt("usuarioId");

                texto.append("🚗 Vehículo ")
                        .append(i + 1)
                        .append("\n");

                texto.append("Placa: ")
                        .append(placa)
                        .append("\n");

                texto.append("Tipo: ")
                        .append(tipo)
                        .append("\n");

                texto.append("Color: ")
                        .append(color)
                        .append("\n");

                texto.append("Propietario: ")
                        .append(propietario)
                        .append("\n");

                texto.append("Usuario ID: ")
                        .append(usuarioId)
                        .append("\n\n");

            } catch (Exception e) {

                Log.e(
                        "EPARKING_API",
                        "Error leyendo vehículo",
                        e
                );
            }
        }

        tvListaVehiculos.setText(
                texto.toString()
        );
    }

    @Override
    protected void onResume() {
        super.onResume();

        cargarVehiculos();
    }
}