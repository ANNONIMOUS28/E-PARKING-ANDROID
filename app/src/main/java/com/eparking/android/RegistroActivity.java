package com.eparking.android;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
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

public class RegistroActivity extends AppCompatActivity {

    private static final String TAG = "EPARKING_REGISTRO";

    private static final String URL =
            ApiConfig.url("Usuarios");

    private EditText etNombre;
    private EditText etDocumento;
    private EditText etCorreoRegistro;
    private EditText etPasswordRegistro;
    private Button btnRegistrar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        etNombre = findViewById(R.id.etNombre);
        etDocumento = findViewById(R.id.etDocumento);
        etCorreoRegistro = findViewById(R.id.etCorreoRegistro);
        etPasswordRegistro = findViewById(R.id.etPasswordRegistro);
        btnRegistrar = findViewById(R.id.btnRegistrar);

        btnRegistrar.setOnClickListener(v -> registrar());
    }

    private void registrar() {

        String nombre = etNombre.getText().toString().trim();
        String documento = etDocumento.getText().toString().trim();
        String correo = etCorreoRegistro.getText().toString().trim();
        String password = etPasswordRegistro.getText().toString().trim();

        if (nombre.isEmpty()) {
            etNombre.setError("Ingrese el nombre completo");
            etNombre.requestFocus();
            avisar("Ingrese el nombre completo");
            return;
        }

        if (documento.isEmpty()) {
            etDocumento.setError("Ingrese el documento");
            etDocumento.requestFocus();
            avisar("Ingrese el documento");
            return;
        }

        if (correo.isEmpty()) {
            etCorreoRegistro.setError("Ingrese el correo electrónico");
            etCorreoRegistro.requestFocus();
            avisar("Ingrese el correo electrónico");
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(correo).matches()) {
            etCorreoRegistro.setError("El correo no tiene un formato válido");
            etCorreoRegistro.requestFocus();
            avisar("El correo no tiene un formato válido");
            return;
        }

        if (password.isEmpty()) {
            etPasswordRegistro.setError("Ingrese la contraseña");
            etPasswordRegistro.requestFocus();
            avisar("Ingrese la contraseña");
            return;
        }

        if (password.length() < 4) {
            etPasswordRegistro.setError("La contraseña debe tener al menos 4 caracteres");
            etPasswordRegistro.requestFocus();
            avisar("La contraseña debe tener al menos 4 caracteres");
            return;
        }

        btnRegistrar.setEnabled(false);

        try {

            JSONObject datosUsuario = new JSONObject();

            datosUsuario.put("nombre", nombre);
            datosUsuario.put("identificacion", documento);
            datosUsuario.put("correo", correo);
            datosUsuario.put("password", password);
            datosUsuario.put("telefono", "");
            datosUsuario.put("rol", "USUARIO");

            Log.d(TAG, "Enviando POST: " + datosUsuario);

            RequestQueue queue = Volley.newRequestQueue(this);

            JsonObjectRequest request =
                    new JsonObjectRequest(
                            Request.Method.POST,
                            URL,
                            datosUsuario,

                            response -> {

                                Log.d(TAG, "Registro exitoso: " + response);

                                Toast.makeText(
                                        this,
                                        "Cuenta creada. Ya puedes iniciar sesión",
                                        Toast.LENGTH_LONG
                                ).show();

                                irAlLogin();

                            },

                            error -> {

                                Log.e(TAG, "Error en registro", error);

                                btnRegistrar.setEnabled(true);

                                if (error.networkResponse != null) {

                                    int codigo = error.networkResponse.statusCode;

                                    String respuesta = "";

                                    if (error.networkResponse.data != null) {
                                        respuesta = new String(
                                                error.networkResponse.data
                                        );
                                    }

                                    Log.e(TAG, "Código HTTP: " + codigo);
                                    Log.e(TAG, "Respuesta: " + respuesta);

                                    Toast.makeText(
                                            this,
                                            mensajeDeError(codigo, respuesta),
                                            Toast.LENGTH_LONG
                                    ).show();

                                } else {

                                    Toast.makeText(
                                            this,
                                            "No hubo respuesta del servidor. "
                                                    + "Verifique su conexión",
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

            Log.e(TAG, "Error preparando la petición", e);

            btnRegistrar.setEnabled(true);

            Toast.makeText(
                    this,
                    "Ocurrió un error al enviar los datos",
                    Toast.LENGTH_LONG
            ).show();
        }
    }

    /**
     * Muestra un aviso visible. El setError por sí solo deja un icono
     * pequeño que el usuario no ve, y la app parece no responder.
     */
    private void avisar(String mensaje) {

        Toast.makeText(this, mensaje, Toast.LENGTH_LONG).show();
    }

    /**
     * Intenta tomar el mensaje que devuelve el servidor para no
     * mostrar un código HTTP pelado al usuario.
     */
    private String mensajeDeError(int codigo, String respuesta) {

        if (respuesta.contains("Duplicate") || respuesta.contains("duplicate")) {
            return "Ese correo ya está registrado";
        }

        try {
            JSONObject json = new JSONObject(respuesta);

            if (json.has("mensaje")) {
                return json.getString("mensaje");
            }
        } catch (Exception ignored) {
        }

        return "Error HTTP: " + codigo;
    }

    private void irAlLogin() {

        Intent intent = new Intent(this, MainActivity.class);

        intent.setFlags(
                Intent.FLAG_ACTIVITY_NEW_TASK
                        | Intent.FLAG_ACTIVITY_CLEAR_TASK
        );

        startActivity(intent);

        finish();
    }
}
