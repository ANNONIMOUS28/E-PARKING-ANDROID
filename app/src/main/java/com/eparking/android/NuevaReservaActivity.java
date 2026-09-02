package com.eparking.android;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

public class NuevaReservaActivity extends AppCompatActivity {

    private EditText etVehiculoId;
    private EditText etCupo;
    private EditText etFecha;
    private EditText etHora;

    private static final String URL =
            "http://192.168.1.6:8081/eparking/api/reservas";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_nueva_reserva);

        etVehiculoId =
                findViewById(R.id.etVehiculoId);

        etCupo =
                findViewById(R.id.etCupo);

        etFecha =
                findViewById(R.id.etFecha);

        etHora =
                findViewById(R.id.etHora);

        Button btnReservar =
                findViewById(R.id.btnReservar);

        Button btnCancelarReserva =
                findViewById(R.id.btnCancelarReserva);

        btnReservar.setOnClickListener(v ->
                registrarReserva()
        );

        btnCancelarReserva.setOnClickListener(v ->
                finish()
        );
    }

    private void registrarReserva() {

        String vehiculoTexto =
                etVehiculoId.getText()
                        .toString()
                        .trim();

        String cupo =
                etCupo.getText()
                        .toString()
                        .trim();

        String fecha =
                etFecha.getText()
                        .toString()
                        .trim();

        String hora =
                etHora.getText()
                        .toString()
                        .trim();

        // Validar campos
        if (vehiculoTexto.isEmpty()) {

            etVehiculoId.setError(
                    "Ingrese el ID del vehículo"
            );

            etVehiculoId.requestFocus();

            return;
        }

        if (cupo.isEmpty()) {

            etCupo.setError(
                    "Ingrese el cupo"
            );

            etCupo.requestFocus();

            return;
        }

        if (fecha.isEmpty()) {

            etFecha.setError(
                    "Ingrese la fecha"
            );

            etFecha.requestFocus();

            return;
        }

        if (hora.isEmpty()) {

            etHora.setError(
                    "Ingrese la hora"
            );

            etHora.requestFocus();

            return;
        }

        int vehiculoId;

        try {

            vehiculoId =
                    Integer.parseInt(vehiculoTexto);

        } catch (NumberFormatException e) {

            etVehiculoId.setError(
                    "El ID debe ser numérico"
            );

            etVehiculoId.requestFocus();

            return;
        }

        /*
         * Obtener el usuario que inició sesión.
         */
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

        if (usuarioId == -1) {

            Toast.makeText(
                    this,
                    "No se encontró el usuario de la sesión",
                    Toast.LENGTH_LONG
            ).show();

            return;
        }

        try {

            /*
             * Crear el JSON que espera
             * ReservaApiServlet.
             *
             * Los nombres deben coincidir
             * con la clase modelo Reservas.
             */
            JSONObject json =
                    new JSONObject();

            json.put(
                    "usuarioId",
                    usuarioId
            );

            json.put(
                    "vehiculoId",
                    vehiculoId
            );

            json.put(
                    "cupo",
                    cupo
            );

            /*
             * Estado inicial de la reserva.
             */
            json.put(
                    "estado",
                    "Activa"
            );

            json.put(
                    "fecha",
                    fecha
            );

            json.put(
                    "hora",
                    hora
            );

            Log.d(
                    "EPARKING_RESERVA",
                    "JSON enviado: " + json
            );

            RequestQueue queue =
                    Volley.newRequestQueue(this);

            JsonObjectRequest request =
                    new JsonObjectRequest(
                            Request.Method.POST,
                            URL,
                            json,

                            response -> {

                                Log.d(
                                        "EPARKING_RESERVA",
                                        "Reserva registrada: "
                                                + response
                                );

                                Toast.makeText(
                                        this,
                                        "Reserva registrada correctamente",
                                        Toast.LENGTH_LONG
                                ).show();

                                /*
                                 * Regresar a ReservasActivity.
                                 * onResume() volverá a cargar
                                 * la lista.
                                 */
                                finish();
                            },

                            error -> {

                                Log.e(
                                        "EPARKING_RESERVA",
                                        "Error registrando reserva",
                                        error
                                );

                                String mensaje =
                                        "No se pudo registrar la reserva";

                                if (error.networkResponse != null) {

                                    mensaje +=
                                            "\nCódigo: "
                                                    + error.networkResponse.statusCode;
                                }

                                Toast.makeText(
                                        this,
                                        mensaje,
                                        Toast.LENGTH_LONG
                                ).show();
                            }
                    );

            queue.add(request);

        } catch (Exception e) {

            Log.e(
                    "EPARKING_RESERVA",
                    "Error creando JSON",
                    e
            );

            Toast.makeText(
                    this,
                    "Error preparando la reserva",
                    Toast.LENGTH_LONG
            ).show();
        }
    }
}