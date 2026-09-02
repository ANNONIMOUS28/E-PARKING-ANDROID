package com.eparking.android;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

public class DisponibilidadActivity extends AppCompatActivity {

    private TextView tvResumenDisponibilidad;
    private TextView tvListaCupos;

    private static final String URL =
            "http://192.168.1.6:8081/eparking/api/cupos";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_disponibilidad);

        tvResumenDisponibilidad =
                findViewById(R.id.tvResumenDisponibilidad);

        tvListaCupos =
                findViewById(R.id.tvListaCupos);

        cargarCupos();
    }

    private void cargarCupos() {

        Log.d(
                "EPARKING_API",
                "Consultando cupos: " + URL
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
                                    "Cupos recibidos: " + response
                            );

                            mostrarDisponibilidad(response);
                        },

                        error -> {

                            Log.e(
                                    "EPARKING_API",
                                    "Error consultando cupos",
                                    error
                            );

                            tvResumenDisponibilidad.setText(
                                    "No se pudo consultar la disponibilidad"
                            );

                            tvListaCupos.setText(
                                    "Verifique la conexión con el servidor"
                            );

                            Toast.makeText(
                                    this,
                                    "No se pudieron cargar los cupos",
                                    Toast.LENGTH_LONG
                            ).show();
                        }
                );

        queue.add(request);
    }

    private void mostrarDisponibilidad(JSONArray cupos) {

        int disponibles = 0;
        int ocupados = 0;

        StringBuilder texto =
                new StringBuilder();

        if (cupos.length() == 0) {

            tvResumenDisponibilidad.setText(
                    "No hay cupos registrados"
            );

            tvListaCupos.setText(
                    "No hay cupos para mostrar"
            );

            return;
        }

        for (int i = 0; i < cupos.length(); i++) {

            try {

                JSONObject cupo =
                        cupos.getJSONObject(i);

                int id =
                        cupo.getInt("id");

                String codigo =
                        cupo.getString("codigo");

                String estado =
                        cupo.getString("estado");

                if ("DISPONIBLE".equalsIgnoreCase(estado)) {

                    disponibles++;

                } else if ("OCUPADO".equalsIgnoreCase(estado)) {

                    ocupados++;
                }

                texto.append("🚗 Cupo ")
                        .append(codigo)
                        .append("\n");

                texto.append("Estado: ")
                        .append(estado)
                        .append("\n");

                texto.append("ID: ")
                        .append(id)
                        .append("\n\n");

            } catch (Exception e) {

                Log.e(
                        "EPARKING_API",
                        "Error leyendo cupo",
                        e
                );
            }
        }

        tvResumenDisponibilidad.setText(
                "Disponibles: " + disponibles +
                        "    Ocupados: " + ocupados
        );

        tvListaCupos.setText(
                texto.toString()
        );
    }

    @Override
    protected void onResume() {

        super.onResume();

        cargarCupos();
    }
}