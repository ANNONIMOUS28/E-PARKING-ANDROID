package com.eparking.android;

import android.content.Intent;
import android.content.SharedPreferences;
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

public class ReservasActivity extends AppCompatActivity {

    private TextView tvListaReservas;

    private static final String URL =
            "http://192.168.1.6:8081/eparking/api/reservas";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_reservas);

        tvListaReservas =
                findViewById(R.id.tvListaReservas);

        Button btnNuevaReserva =
                findViewById(R.id.btnNuevaReserva);

        btnNuevaReserva.setOnClickListener(v -> {

            Intent intent =
                    new Intent(
                            ReservasActivity.this,
                            NuevaReservaActivity.class
                    );

            startActivity(intent);
        });

        cargarReservas();
    }

    private void cargarReservas() {

        Log.d(
                "EPARKING_RESERVAS",
                "Consultando reservas: " + URL
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
                                    "EPARKING_RESERVAS",
                                    "Reservas recibidas: " + response
                            );

                            mostrarReservas(response);
                        },

                        error -> {

                            Log.e(
                                    "EPARKING_RESERVAS",
                                    "Error consultando reservas",
                                    error
                            );

                            tvListaReservas.setText(
                                    "No se pudieron cargar las reservas."
                            );

                            Toast.makeText(
                                    this,
                                    "Error conectando con reservas",
                                    Toast.LENGTH_LONG
                            ).show();
                        }
                );

        queue.add(request);
    }

    private void mostrarReservas(JSONArray reservas) {

        SharedPreferences preferences =
                getSharedPreferences(
                        "EPARKING_SESSION",
                        MODE_PRIVATE
                );

        int usuarioId =
                preferences.getInt(
                        "usuarioId",
                        -1
                );

        StringBuilder texto =
                new StringBuilder();

        int cantidad = 0;

        for (int i = 0; i < reservas.length(); i++) {

            try {

                JSONObject reserva =
                        reservas.getJSONObject(i);

                int id =
                        reserva.optInt("id", 0);

                int idUsuario =
                        reserva.optInt("usuarioId", -1);

                /*
                 * Mostrar solamente las reservas
                 * del usuario que inició sesión.
                 */
                if (usuarioId != -1 &&
                        idUsuario != usuarioId) {

                    continue;
                }

                int vehiculoId =
                        reserva.optInt("vehiculoId", 0);

                String cupo =
                        reserva.optString(
                                "cupo",
                                ""
                        );

                String estado =
                        reserva.optString(
                                "estado",
                                ""
                        );

                String fecha =
                        reserva.optString(
                                "fecha",
                                ""
                        );

                String hora =
                        reserva.optString(
                                "hora",
                                ""
                        );

                texto.append("📅 Reserva #")
                        .append(id)
                        .append("\n");

                texto.append("🚗 Vehículo ID: ")
                        .append(vehiculoId)
                        .append("\n");

                texto.append("🅿️ Cupo: ")
                        .append(cupo)
                        .append("\n");

                texto.append("📆 Fecha: ")
                        .append(fecha)
                        .append("\n");

                texto.append("🕐 Hora: ")
                        .append(hora)
                        .append("\n");

                texto.append("Estado: ")
                        .append(estado)
                        .append("\n");

                texto.append("-------------------------\n\n");

                cantidad++;

            } catch (Exception e) {

                Log.e(
                        "EPARKING_RESERVAS",
                        "Error leyendo reserva",
                        e
                );
            }
        }

        if (cantidad == 0) {

            tvListaReservas.setText(
                    "No tienes reservas registradas."
            );

        } else {

            tvListaReservas.setText(
                    texto.toString()
            );
        }
    }

    @Override
    protected void onResume() {

        super.onResume();

        cargarReservas();
    }
}