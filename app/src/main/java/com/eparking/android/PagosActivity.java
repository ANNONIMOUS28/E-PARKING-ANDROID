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

public class PagosActivity extends AppCompatActivity {

    private TextView tvListaPagos;

    private static final String URL =
            ApiConfig.url("Pagos");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_pagos);

        tvListaPagos =
                findViewById(R.id.tvListaPagos);

        Button btnRegistrarPago =
                findViewById(R.id.btnRegistrarPago);

        btnRegistrarPago.setOnClickListener(v -> {

            Intent intent =
                    new Intent(
                            PagosActivity.this,
                            RegistrarPagoActivity.class
                    );

            startActivity(intent);
        });

        cargarPagos();
    }

    private void cargarPagos() {

        Log.d(
                "EPARKING_PAGOS",
                "Consultando pagos: " + URL
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
                                    "EPARKING_PAGOS",
                                    "Pagos recibidos: " + response
                            );

                            mostrarPagos(response);
                        },

                        error -> {

                            Log.e(
                                    "EPARKING_PAGOS",
                                    "Error consultando pagos",
                                    error
                            );

                            tvListaPagos.setText(
                                    "No se pudieron cargar los pagos."
                            );

                            Toast.makeText(
                                    this,
                                    "Error conectando con pagos",
                                    Toast.LENGTH_LONG
                            ).show();
                        }
                );

        queue.add(request);
    }

    private void mostrarPagos(JSONArray pagos) {

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

        if (pagos.length() == 0) {

            tvListaPagos.setText(
                    "No hay pagos registrados."
            );

            return;
        }

        for (int i = 0; i < pagos.length(); i++) {

            try {

                JSONObject pago =
                        pagos.getJSONObject(i);

                int id =
                        pago.getInt("id");

                int reservaId =
                        pago.getInt("reservaId");

                String metodoPago =
                        pago.optString(
                                "metodoPago",
                                ""
                        );

                double total =
                        pago.optDouble(
                                "total",
                                0
                        );

                String estado =
                        pago.optString(
                                "estado",
                                ""
                        );

                String fechaPago =
                        pago.optString(
                                "fechaPago",
                                ""
                        );

                texto.append("💳 Pago #")
                        .append(id)
                        .append("\n");

                texto.append("📅 Reserva ID: ")
                        .append(reservaId)
                        .append("\n");

                texto.append("💰 Total: $")
                        .append(total)
                        .append("\n");

                texto.append("💵 Método de pago: ")
                        .append(metodoPago)
                        .append("\n");

                texto.append("Estado: ")
                        .append(estado)
                        .append("\n");

                texto.append("📆 Fecha: ")
                        .append(fechaPago)
                        .append("\n");

                texto.append("-------------------------\n\n");

                cantidad++;

            } catch (Exception e) {

                Log.e(
                        "EPARKING_PAGOS",
                        "Error leyendo pago",
                        e
                );
            }
        }

        if (cantidad == 0) {

            tvListaPagos.setText(
                    "No tienes pagos registrados."
            );

        } else {

            tvListaPagos.setText(
                    texto.toString()
            );
        }
    }

    @Override
    protected void onResume() {

        super.onResume();

        cargarPagos();
    }
}