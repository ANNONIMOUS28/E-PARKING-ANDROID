package com.eparking.android;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private EditText etCorreo;
    private EditText etPassword;

    private static final String URL =
            ApiConfig.url("auth");

    private static final String TAG = "EPARKING_LOGIN";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);

        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(
                findViewById(R.id.main),
                (v, insets) -> {

                    Insets systemBars = insets.getInsets(
                            WindowInsetsCompat.Type.systemBars()
                    );

                    v.setPadding(
                            systemBars.left,
                            systemBars.top,
                            systemBars.right,
                            systemBars.bottom
                    );

                    return insets;
                });

        // Campos del login
        etCorreo = findViewById(R.id.etCorreo);
        etPassword = findViewById(R.id.etPassword);

        // Botón para ir al registro
        TextView tvRegistro = findViewById(R.id.tvRegistro);

        tvRegistro.setOnClickListener(v -> {

            Intent intent = new Intent(
                    MainActivity.this,
                    RegistroActivity.class
            );

            startActivity(intent);
        });

        // Botón ingresar
        Button btnIngresar = findViewById(R.id.btnIngresar);

        btnIngresar.setOnClickListener(v -> {

            iniciarSesion();
        });
    }

    private void iniciarSesion() {

        String correo =
                etCorreo.getText().toString().trim();

        String password =
                etPassword.getText().toString().trim();

        // Validar correo
        if (correo.isEmpty()) {

            etCorreo.setError("Ingrese su correo");
            etCorreo.requestFocus();
            return;
        }

        // Validar contraseña
        if (password.isEmpty()) {

            etPassword.setError("Ingrese su contraseña");
            etPassword.requestFocus();
            return;
        }

        Log.d(
                TAG,
                "Intentando iniciar sesión con: " + correo
        );

        RequestQueue queue =
                Volley.newRequestQueue(this);

        StringRequest request =
                new StringRequest(
                        Request.Method.POST,
                        URL,

                        response -> {

                            Log.d(
                                    TAG,
                                    "Respuesta login: " + response
                            );

                            try {

                                JSONObject json =
                                        new JSONObject(response);

                                String status =
                                        json.getString("status");

                                if (status.equals("success")) {

                                    JSONObject data =
                                            json.getJSONObject("data");

                                    int usuarioId =
                                            data.getInt("id");

                                    String nombre =
                                            data.getString("nombre");

                                    String correoUsuario =
                                            data.getString("correo");

                                    String rol =
                                            data.getString("rol");

                                    // Guardar información del usuario
                                    SharedPreferences preferences =
                                            getSharedPreferences(
                                                    "EPARKING_SESSION",
                                                    MODE_PRIVATE
                                            );

                                    preferences.edit()
                                            .putInt(
                                                    "usuarioId",
                                                    usuarioId
                                            )
                                            .putString(
                                                    "nombre",
                                                    nombre
                                            )
                                            .putString(
                                                    "correo",
                                                    correoUsuario
                                            )
                                            .putString(
                                                    "rol",
                                                    rol
                                            )
                                            .apply();

                                    Log.d(
                                            TAG,
                                            "Usuario ID guardado: "
                                                    + usuarioId
                                    );

                                    Toast.makeText(
                                            this,
                                            "Bienvenido " + nombre,
                                            Toast.LENGTH_SHORT
                                    ).show();

                                    // Ir al menú principal
                                    Intent intent =
                                            new Intent(
                                                    MainActivity.this,
                                                    MenuActivity.class
                                            );

                                    startActivity(intent);

                                    finish();

                                } else {

                                    String mensaje =
                                            json.optString(
                                                    "message",
                                                    "Error en la autenticación"
                                            );

                                    Toast.makeText(
                                            this,
                                            mensaje,
                                            Toast.LENGTH_LONG
                                    ).show();
                                }

                            } catch (Exception e) {

                                Log.e(
                                        TAG,
                                        "Error procesando respuesta",
                                        e
                                );

                                Toast.makeText(
                                        this,
                                        "Error procesando la respuesta del servidor",
                                        Toast.LENGTH_LONG
                                ).show();
                            }
                        },

                        error -> {

                            Log.e(
                                    TAG,
                                    "Error en login",
                                    error
                            );

                            if (error.networkResponse != null) {

                                int codigo =
                                        error.networkResponse.statusCode;

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
                ) {

                    @Override
                    protected Map<String, String> getParams() {

                        Map<String, String> params =
                                new HashMap<>();

                        params.put(
                                "action",
                                "login"
                        );

                        params.put(
                                "usuario",
                                correo
                        );

                        params.put(
                                "password",
                                password
                        );

                        return params;
                    }
                };

        queue.add(request);
    }
}