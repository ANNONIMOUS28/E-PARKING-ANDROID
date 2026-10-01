package com.eparking.android;

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
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

public class AgregarVehiculoActivity extends AppCompatActivity {

    private EditText etPlaca;
    private EditText etTipo;
    private EditText etColor;
    private EditText etPropietario;
    private EditText etUsuarioId;

    private static final String URL =
            ApiConfig.url("Vehiculos");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_agregar_vehiculo);

        etPlaca = findViewById(R.id.etPlaca);
        etTipo = findViewById(R.id.etTipo);
        etColor = findViewById(R.id.etColor);
        etPropietario = findViewById(R.id.etPropietario);
        etUsuarioId = findViewById(R.id.etUsuarioId);

        Button btnGuardarVehiculo =
                findViewById(R.id.btnGuardarVehiculo);

        btnGuardarVehiculo.setOnClickListener(
                v -> guardarVehiculo()
        );
    }

    private void guardarVehiculo() {

        String placa = etPlaca.getText().toString().trim();
        String tipo = etTipo.getText().toString().trim();
        String color = etColor.getText().toString().trim();
        String propietario = etPropietario.getText().toString().trim();
        String usuarioIdTexto =
                etUsuarioId.getText().toString().trim();

        if (placa.isEmpty()) {
            etPlaca.setError("Ingrese la placa");
            etPlaca.requestFocus();
            return;
        }

        if (tipo.isEmpty()) {
            etTipo.setError("Ingrese el tipo de vehículo");
            etTipo.requestFocus();
            return;
        }

        if (color.isEmpty()) {
            etColor.setError("Ingrese el color");
            etColor.requestFocus();
            return;
        }

        if (propietario.isEmpty()) {
            etPropietario.setError("Ingrese el propietario");
            etPropietario.requestFocus();
            return;
        }

        if (usuarioIdTexto.isEmpty()) {
            etUsuarioId.setError("Ingrese el ID del usuario");
            etUsuarioId.requestFocus();
            return;
        }

        int usuarioId;

        try {
            usuarioId = Integer.parseInt(usuarioIdTexto);
        } catch (NumberFormatException e) {
            etUsuarioId.setError("El ID debe ser un número");
            etUsuarioId.requestFocus();
            return;
        }

        try {

            JSONObject datosVehiculo = new JSONObject();

            datosVehiculo.put("placa", placa);
            datosVehiculo.put("tipo", tipo);
            datosVehiculo.put("color", color);
            datosVehiculo.put("propietario", propietario);
            datosVehiculo.put("usuarioId", usuarioId);

            Log.d(
                    "EPARKING_API",
                    "Enviando POST: " + datosVehiculo
            );

            RequestQueue queue =
                    Volley.newRequestQueue(this);

            JsonObjectRequest request =
                    new JsonObjectRequest(
                            Request.Method.POST,
                            URL,
                            datosVehiculo,

                            response -> {

                                Log.d(
                                        "EPARKING_API",
                                        "POST exitoso: "
                                                + response
                                );

                                Toast.makeText(
                                        this,
                                        "Vehículo registrado correctamente",
                                        Toast.LENGTH_LONG
                                ).show();

                                limpiarFormulario();
                            },

                            error -> {

                                Log.e(
                                        "EPARKING_API",
                                        "Error en POST",
                                        error
                                );

                                if (error.networkResponse != null) {

                                    int codigo =
                                            error.networkResponse.statusCode;

                                    String respuesta = "";

                                    if (error.networkResponse.data != null) {
                                        respuesta = new String(
                                                error.networkResponse.data
                                        );
                                    }

                                    Log.e(
                                            "EPARKING_API",
                                            "Código HTTP: " + codigo
                                    );

                                    Log.e(
                                            "EPARKING_API",
                                            "Respuesta: " + respuesta
                                    );

                                    Toast.makeText(
                                            this,
                                            "Error HTTP: " + codigo,
                                            Toast.LENGTH_LONG
                                    ).show();

                                } else {

                                    Toast.makeText(
                                            this,
                                            "No hubo respuesta del servidor",
                                            Toast.LENGTH_LONG
                                    ).show();
                                }
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

        } catch (Exception e) {

            Log.e(
                    "EPARKING_API",
                    "Error preparando JSON",
                    e
            );

            Toast.makeText(
                    this,
                    "Error al preparar los datos",
                    Toast.LENGTH_LONG
            ).show();
        }
    }

    private void limpiarFormulario() {

        etPlaca.setText("");
        etTipo.setText("");
        etColor.setText("");
        etPropietario.setText("");
        etUsuarioId.setText("");
    }
}