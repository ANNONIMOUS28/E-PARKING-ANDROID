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

public class HistorialActivity extends AppCompatActivity {

    private TextView tvResumenHistorial;
    private TextView tvListaHistorial;

    private static final String URL =
            "http://192.168.1.6:8081/eparking/api/historial";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_historial);

        tvResumenHistorial =
                findViewById(R.id.tvResumenHistorial);

        tvListaHistorial =
                findViewById(R.id.tvListaHistorial);

        cargarHistorial();
    }

    private void cargarHistorial() {

        Log.d(
                "EPARKING_HISTORIAL",
                "Consultando historial: " + URL
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
                                    "EPARKING_HISTORIAL",
                                    "Historial recibido: "
                                            + response
                            );

                            mostrarHistorial(response);
                        },

                        error -> {

                            Log.e(
                                    "EPARKING_HISTORIAL",
                                    "Error consultando historial",
                                    error
                            );

                            tvResumenHistorial.setText(
                                    "No se pudo consultar el historial"
                            );

                            tvListaHistorial.setText(
                                    "Verifique la conexión con el servidor."
                            );

                            Toast.makeText(
                                    this,
                                    "No se pudo cargar el historial",
                                    Toast.LENGTH_LONG
                            ).show();
                        }
                );

        queue.add(request);
    }

    private void mostrarHistorial(JSONArray historial) {

        StringBuilder texto =
                new StringBuilder();

        int cantidad = 0;
        int activas = 0;
        int finalizadas = 0;

        if (historial.length() == 0) {

            tvResumenHistorial.setText(
                    "No hay registros en el historial"
            );

            tvListaHistorial.setText(
                    "No tienes registros para mostrar."
            );

            return;
        }

        for (int i = 0; i < historial.length(); i++) {

            try {

                JSONObject registro =
                        historial.getJSONObject(i);

                int id =
                        registro.optInt("id", 0);

                int historialId =
                        registro.optInt("historialId", 0);

                String fecha =
                        registro.optString(
                                "fecha",
                                ""
                        );

                String vehiculo =
                        registro.optString(
                                "vehiculo",
                                ""
                        );

                String cupo =
                        registro.optString(
                                "cupo",
                                ""
                        );

                String estado =
                        registro.optString(
                                "estado",
                                ""
                        );

                texto.append("📋 Registro #")
                        .append(
                                historialId != 0
                                        ? historialId
                                        : id
                        )
                        .append("\n");

                texto.append("📆 Fecha: ")
                        .append(fecha)
                        .append("\n");

                texto.append("🚗 Vehículo: ")
                        .append(vehiculo)
                        .append("\n");

                texto.append("🅿️ Cupo: ")
                        .append(cupo)
                        .append("\n");

                texto.append("Estado: ")
                        .append(estado)
                        .append("\n");

                texto.append("-------------------------")
                        .append("\n\n");

                cantidad++;

                if ("Activa".equalsIgnoreCase(estado)) {
                    activas++;
                }

                if ("Finalizada".equalsIgnoreCase(estado)) {
                    finalizadas++;
                }

            } catch (Exception e) {

                Log.e(
                        "EPARKING_HISTORIAL",
                        "Error leyendo registro",
                        e
                );
            }
        }

        if (cantidad == 0) {

            tvResumenHistorial.setText(
                    "No hay registros para mostrar"
            );

            tvListaHistorial.setText(
                    "No tienes registros en el historial."
            );

        } else {

            tvResumenHistorial.setText(
                    "Total: " + cantidad +
                            "    Activas: " + activas +
                            "    Finalizadas: " + finalizadas
            );

            tvListaHistorial.setText(
                    texto.toString()
            );
        }
    }

    @Override
    protected void onResume() {

        super.onResume();

        cargarHistorial();
    }
}