package com.eparking.android;

/**
 * Fuente unica de las direcciones del backend.
 *
 * La URL base se define fuera del codigo, en la clave
 * {@code eparking.baseUrl} de {@code local.properties} o, si no
 * existe, de {@code gradle.properties}. Asi el proyecto se puede
 * apuntar a otro servidor sin recompilar cambios en cada activity.
 *
 * La ruta se pasa sin la barra inicial:
 * {@code ApiConfig.url("Cupos")}.
 *
 * Ojo: el backend distingue mayusculas. {@code /api/cupos} devuelve
 * 404 y {@code /api/Cupos} responde.
 */
public final class ApiConfig {

    private ApiConfig() {
    }

    /**
     * @param ruta recurso del servlet, por ejemplo {@code Cupos}
     *             o {@code auth}
     * @return URL completa del endpoint
     */
    public static String url(String ruta) {
        return BuildConfig.EPARKING_BASE_URL + "/api/" + ruta;
    }
}
