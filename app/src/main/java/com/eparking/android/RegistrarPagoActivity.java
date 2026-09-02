package com.eparking.android;

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

public class RegistrarPagoActivity extends AppCompatActivity {

    private EditText etReservaIdPago;
    private EditText etMetodoPago;
    private EditText etTotalPago;
    private EditText etEstadoPago;
    private EditText etFechaPago;

    private static final String URL =
            "http://192.168.1.6:8081/eparking/api/pagos";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(
                R.layout.activity_registrar_pago
        );

        etReservaIdPago =
                findViewById(R.id.etReservaIdPago);

        etMetodoPago =
                findViewById(R.id.etMetodoPago);

        etTotalPago =
                findViewById(R.id.etTotalPago);

        etEstadoPago =
                findViewById(R.id.etEstadoPago);

        etFechaPago =
                findViewById(R.id.etFechaPago);

        Button btnGuardarPago =
                findViewById(R.id.btnGuardarPago);

        Button btnCancelarPago =
                findViewById(R.id.btnCancelarPago);

        btnGuardarPago.setOnClickListener(
                v -> registrarPago()
        );

        btnCancelarPago.setOnClickListener(
                v -> finish()
        );
    }

    private void registrarPago() {

        String reservaIdTexto =
                etReservaIdPago.getText()
                        .toString()
                        .trim();

        String metodoPago =
                etMetodoPago.getText()
                        .toString()
                        .trim();

        String totalTexto =
                etTotalPago.getText()
                        .toString()
                        .trim();

        String estado =
                etEstadoPago.getText()
                        .toString()
                        .trim();

        String fechaPago =
                etFechaPago.getText()
                        .toString()
                        .trim();

        if (reservaIdTexto.isEmpty() ||
                metodoPago.isEmpty() ||
                totalTexto.isEmpty() ||
                estado.isEmpty() ||
                fechaPago.isEmpty()) {

            Toast.makeText(
                    this,
                    "Complete todos los campos",
                    Toast.LENGTH_LONG
            ).show();

            return;
        }

        int reservaId;

        double total;

        try {

            reservaId =
                    Integer.parseInt(
                            reservaIdTexto
                    );

            total =
                    Double.parseDouble(
                            totalTexto
                    );

        } catch (NumberFormatException e) {

            Toast.makeText(
                    this,
                    "Ingrese valores numéricos válidos",
                    Toast.LENGTH_LONG
            ).show();

            return;
        }

        try {

            JSONObject datos =
                    new JSONObject();

            datos.put(
                    "reservaId",
                    reservaId
            );

            datos.put(
                    "metodoPago",
                    metodoPago
            );

            datos.put(
                    "total",
                    total
            );

            datos.put(
                    "estado",
                    estado
            );

            datos.put(
                    "fechaPago",
                    fechaPago
            );

            Log.d(
                    "EPARKING_PAGOS",
                    "Enviando pago: " + datos
            );

            RequestQueue queue =
                    Volley.newRequestQueue(this);

            JsonObjectRequest request =
                    new JsonObjectRequest(
                            Request.Method.POST,
                            URL,
                            datos,

                            response -> {

                                Log.d(
                                        "EPARKING_PAGOS",
                                        "Pago registrado: "
                                                + response
                                );

                                Toast.makeText(
                                        this,
                                        "Pago registrado correctamente",
                                        Toast.LENGTH_LONG
                                ).show();

                                finish();
                            },

                            error -> {

                                Log.e(
                                        "EPARKING_PAGOS",
                                        "Error registrando pago",
                                        error
                                );

                                Toast.makeText(
                                        this,
                                        "No se pudo registrar el pago",
                                        Toast.LENGTH_LONG
                                ).show();
                            }
                    );

            queue.add(request);

        } catch (Exception e) {

            Log.e(
                    "EPARKING_PAGOS",
                    "Error creando JSON",
                    e
            );

            Toast.makeText(
                    this,
                    "Error al preparar el pago",
                    Toast.LENGTH_LONG
            ).show();
        }
    }
}