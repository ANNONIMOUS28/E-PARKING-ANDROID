package com.eparking.android;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

public class NuevaReservaActivity extends AppCompatActivity {

    private EditText etVehiculoId;
    private EditText etCupo;
    private EditText etFecha;
    private EditText etHora;

    private static final String URL =
            ApiConfig.url("Reservas");

    private static final String URL_VEHICULOS =
            ApiConfig.url("Vehiculos");

    /**
     * Cache de la lista de vehículos para no consultar el
     * servidor en cada intento de reserva.
     */
    private JSONArray vehiculosCache;

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
                    "Ingrese el ID o la placa del vehículo"
            );

            etVehiculoId.requestFocus();

            avisar("Ingrese el ID o la placa del vehículo");

            return;
        }

        if (cupo.isEmpty()) {

            etCupo.setError(
                    "Ingrese el cupo"
            );

            etCupo.requestFocus();

            avisar("Ingrese el cupo");

            return;
        }

        if (fecha.isEmpty()) {

            etFecha.setError(
                    "Ingrese la fecha"
            );

            etFecha.requestFocus();

            avisar("Ingrese la fecha");

            return;
        }

        if (hora.isEmpty()) {

            etHora.setError(
                    "Ingrese la hora"
            );

            etHora.requestFocus();

            avisar("Ingrese la hora");

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

        resolverVehiculo(
                vehiculoTexto,
                cupo,
                fecha,
                hora,
                usuarioId
        );
    }

    /**
     * El usuario puede escribir el ID numérico del vehículo
     * o su placa. Si escribe la placa, se busca en el servidor
     * el ID que le corresponde, porque la tabla reservas solo
     * acepta un entero en vehiculo_id.
     */
    private void resolverVehiculo(
            String vehiculoTexto,
            String cupo,
            String fecha,
            String hora,
            int usuarioId
    ) {

        // Si es puramente numérico, ya es el ID.
        if (vehiculoTexto.matches("\\d+")) {

            enviarReserva(
                    Integer.parseInt(vehiculoTexto),
                    cupo,
                    fecha,
                    hora,
                    usuarioId
            );

            return;
        }

        // Si es texto, se busca por placa.
        buscarIdPorPlaca(
                vehiculoTexto,
                cupo,
                fecha,
                hora,
                usuarioId
        );
    }

    private void buscarIdPorPlaca(
            String placa,
            String cupo,
            String fecha,
            String hora,
            int usuarioId
    ) {

        if (vehiculosCache != null) {

            aplicarVehiculoPorPlaca(
                    vehiculosCache,
                    placa,
                    cupo,
                    fecha,
                    hora,
                    usuarioId
            );

            return;
        }

        RequestQueue queue =
                Volley.newRequestQueue(this);

        StringRequest request =
                new StringRequest(
                        Request.Method.GET,
                        URL_VEHICULOS,

                        response -> {

                            try {

                                vehiculosCache =
                                        new JSONArray(response);

                            } catch (Exception e) {

                                Log.e(
                                        "EPARKING_RESERVA",
                                        "Respuesta de vehículos ilegible",
                                        e
                                );

                                avisar("No se pudo leer la lista de vehículos");

                                return;
                            }

                            aplicarVehiculoPorPlaca(
                                    vehiculosCache,
                                    placa,
                                    cupo,
                                    fecha,
                                    hora,
                                    usuarioId
                            );

                        },

                        error -> {

                            Log.e(
                                    "EPARKING_RESERVA",
                                    "Error al consultar vehículos",
                                    error
                            );

                            avisar(
                                    "No se pudo consultar la lista de vehículos. "
                                            + "Verifique su conexión"
                            );
                        }
                );

        request.setRetryPolicy(
                new DefaultRetryPolicy(
                        15000,
                        0,
                        1.0f
                )
        );

        queue.add(request);
    }

    private void aplicarVehiculoPorPlaca(
            JSONArray vehiculos,
            String placa,
            String cupo,
            String fecha,
            String hora,
            int usuarioId
    ) {

        for (int i = 0; i < vehiculos.length(); i++) {

            JSONObject vehiculo = vehiculos.optJSONObject(i);

            if (vehiculo == null) {
                continue;
            }

            String placaRegistrada =
                    vehiculo.optString("placa", "");

            if (placaRegistrada.equalsIgnoreCase(placa)) {

                int id = vehiculo.optInt("id", -1);

                if (id == -1) {
                    avisar("El vehículo no tiene un ID válido");
                    return;
                }

                Log.d(
                        "EPARKING_RESERVA",
                        "Placa " + placa
                                + " corresponde al ID " + id
                );

                enviarReserva(
                        id,
                        cupo,
                        fecha,
                        hora,
                        usuarioId
                );

                return;
            }
        }

        etVehiculoId.setError("No hay un vehículo con esa placa");

        avisar(
                "No se encontró un vehículo con la placa "
                        + placa
                        + ". Regístrala primero en Mis vehículos"
        );
    }

    private void enviarReserva(
            int vehiculoId,
            String cupo,
            String fecha,
            String hora,
            int usuarioId
    ) {

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

                                    int codigo =
                                            error.networkResponse.statusCode;

                                    if (codigo == 400) {

                                        mensaje =
                                                "El vehículo o el cupo no existen. "
                                                        + "Revisa los datos e intenta otra vez";

                                    } else if (codigo == 500) {

                                        mensaje =
                                                "Error interno del servidor al guardar la reserva";

                                    } else {

                                        mensaje +=
                                                " (código " + codigo + ")";
                                    }
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

    /**
     * Muestra un aviso visible. El setError por sí solo deja un
     * icono pequeño que el usuario no ve, y la app parece no
     * responder.
     */
    private void avisar(String mensaje) {

        Toast.makeText(
                this,
                mensaje,
                Toast.LENGTH_LONG
        ).show();
    }
}