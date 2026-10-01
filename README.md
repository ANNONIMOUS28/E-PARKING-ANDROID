# E-Parking — Aplicación Android

Aplicación móvil del sistema de parqueadero E-Parking. Permite iniciar
sesión, registrar vehículos, consultar la disponibilidad de cupos, crear
reservas y revisar pagos e historial.

La aplicación es el cliente de la API REST del backend
[E-Parking](https://github.com/ANNONIMOUS28/E-Parking), que corre en un
servidor Tomcat y guarda los datos en MySQL.

| | |
|---|---|
| Paquete | `com.eparking.android` |
| Versión | 1.0.2 (`versionCode` 2) |
| SDK mínimo | 24 (Android 7.0) |
| SDK objetivo | 37 |
| Permisos | `INTERNET` |
| Librería de red | Volley 1.2.1 |

---

## 1. Requisitos

| Herramienta | Versión |
|---|---|
| JDK | 17 |
| Android SDK | Compilación 37 |
| Gradle | 9.5.0 (incluido en el proyecto) |
| Backend | API REST del proyecto E-Parking, accesible en la red |

El JDK 17 es el que necesita Gradle para compilar. El backend es aparte y
va con JDK 21, porque su `pom.xml` genera bytecode 19.

---

## 2. Puesta en marcha

### 2.1 Apuntar a un servidor

La dirección del backend **no está escrita en el código**. Se define en la
clave `eparking.baseUrl`.

El valor predeterminado está en `gradle.properties`, que sí se versiona:

```properties
eparking.baseUrl=http://10.0.2.2:8081/eparking
```

`10.0.2.2` es la dirección que usa el emulador para alcanzar la máquina
anfitriona. Si pruebas en un teléfono físico, o si el servidor está en
otra máquina, redefine la clave en `local.properties`, que no se versiona y
tiene prioridad sobre el archivo anterior:

```properties
# local.properties
eparking.baseUrl=http://192.168.0.10:8081/eparking
```

No hace falta recompilar el proyecto para cambiar la dirección, pero sí
reconstruir el APK.

> El puerto **8081** es el que usa el backend en las pruebas. Si corres
> Tomcat con su puerto predeterminado, cambia el 8081 por el 8080.

### 2.2 Compilar

```bash
./gradlew :app:assembleDebug
```

El APK queda en `app/build/outputs/apk/debug/app-debug.apk`.

Para instalarlo en un dispositivo o emulador conectado:

```bash
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

### 2.3 Requisitos de red

La aplicación se comunica por HTTP sin cifrar, y el manifiesto lo permite
de forma explícita con `android:usesCleartextTraffic="true"`. Eso es
necesario para hablar con un servidor local durante las pruebas, pero en
una instalación real habría que servir el backend por HTTPS y quitar esa
opción.

---

## 3. Pantallas

| Pantalla | Activity | Qué hace |
|---|---|---|
| Inicio de sesión | `MainActivity` | Valida las credenciales contra el servidor. |
| Crear cuenta | `RegistroActivity` | Alta de usuario nuevo. |
| Menú principal | `MenuActivity` | Punto de entrada a las demás secciones. |
| Mis vehículos | `VehiculosActivity` | Lista los vehículos del usuario. |
| Agregar vehículo | `AgregarVehiculoActivity` | Registra un vehículo nuevo. |
| Disponibilidad | `DisponibilidadActivity` | Muestra el estado de cada cupo. |
| Mis reservas | `ReservasActivity` | Lista las reservas registradas. |
| Nueva reserva | `NuevaReservaActivity` | Crea una reserva para un cupo. |
| Pagos | `PagosActivity` | Lista los pagos realizados. |
| Registrar pago | `RegistrarPagoActivity` | Asocia un pago a una reserva. |
| Historial | `HistorialActivity` | Lista el historial de movimientos. |

La sesión se guarda en `SharedPreferences`, bajo el nombre
`EPARKING_SESSION`, con el identificador y el rol del usuario.

---

## 4. Endpoints que consume

Todas las llamadas se arman a través de la clase `ApiConfig`, que
concatena la dirección base con la ruta del recurso:

```java
ApiConfig.url("Cupos")   // →  http://<servidor>/eparking/api/Cupos
```

| Método | Ruta | Para qué la usa |
|---|---|---|
| POST | `/api/auth` | Inicio de sesión. |
| POST | `/api/Usuarios` | Alta de usuario. |
| GET | `/api/Vehiculos` | Listar vehículos. |
| POST | `/api/Vehiculos` | Registrar vehículo. |
| GET | `/api/Cupos` | Consultar disponibilidad. |
| GET | `/api/Reservas` | Listar reservas. |
| POST | `/api/Reservas` | Crear reserva. |
| GET | `/api/Pagos` | Listar pagos. |
| POST | `/api/Pagos` | Registrar pago. |
| GET | `/api/Historial` | Consultar historial. |

> **El backend distingue mayúsculas en las rutas.** `/api/Cupos` responde y
> `/api/cupos` devuelve 404. Es un detalle fácil de pasar por alto y ya
> costó una tanda de pruebas completa.

Los nombres de los campos que viajan en el cuerpo de las peticiones deben
coincidir con los de las clases del paquete `modelo` del backend. Van en
Java, no en `snake_case`: `usuarioId`, `vehiculoId`, `reservaId`.

---

## 5. Estructura del proyecto

```
app/src/main/java/com/eparking/android/
├── MainActivity.java          Inicio de sesión
├── MenuActivity.java          Menú principal
├── RegistroActivity.java      Alta de usuario
├── VehiculosActivity.java     Listado de vehículos
├── AgregarVehiculoActivity.java
├── DisponibilidadActivity.java
├── ReservasActivity.java
├── NuevaReservaActivity.java
├── PagosActivity.java
├── RegistrarPagoActivity.java
├── HistorialActivity.java
└── ApiConfig.java             Arma las direcciones de la API

app/src/main/res/layout/       Un layout por pantalla
gradle.properties              Dirección base predeterminada
local.properties               Dirección base local (no se versiona)
CHANGELOG.md                   Registro de cambios
```

No hay una capa de separación entre Activities y acceso a datos: cada
pantalla arma su propia petición con Volley. Es un punto conocido de mejora
para versiones futuras.

---

## 6. Problemas conocidos

| # | Problema | Impacto |
|---|---|---|
| 1 | El campo de vehículo acepta el identificador o la placa, y si se escribe la placa hace una consulta extra para resolverlo | Un poco más de tráfico al crear reservas |
| 2 | No hay caché de datos | Cada entrada a una pantalla vuelve a consultar al servidor |
| 3 | La contraseña se envía y se guarda en texto plano en el backend | Seguridad, en el lado del servidor |
| 4 | El tráfico va sin cifrar (`usesCleartextTraffic`) | Solo aceptable en pruebas locales |

---

## 7. Registro de cambios

El detalle de lo corregido en cada versión está en
[CHANGELOG.md](CHANGELOG.md).
