# Registro de cambios

## 1.0.2 — 30 de septiembre de 2026

Correcciones encontradas al probar la aplicación contra el backend real.
Todos los cambios de esta versión se verificaron sobre el emulador de
Android con el servidor corriendo de forma local.

### Correcciones

**El registro de usuarios no funcionaba.**

`RegistroActivity` solo inflaba el layout y no tenía ninguna lógica
asociada. El formulario se veía completo, con sus cuatro campos y su
botón, pero al presionar REGISTRAR no se ejecutaba nada: no se enviaba
la petición, no se mostraba ningún mensaje y el usuario no se guardaba
en la base de datos.

Se implementó la llamada al alta de usuarios, con validación de los
cuatro campos, comprobación del formato del correo, aviso visible cuando
falta un dato, bloqueo del botón durante el envío para evitar registros
duplicados por doble toque, y lectura del mensaje que devuelve el
servidor en lugar de mostrar un código HTTP suelto.

Al terminar correctamente, la aplicación vuelve a la pantalla de inicio
de sesión limpiando el historial de navegación.

**El campo de vehículo en la nueva reserva solo aceptaba números.**

El campo de identificación del vehículo tenía `android:inputType="number"`
en el layout. Eso no es una validación: es un filtro que descarta las
letras sin avisar. Al escribir una placa como `ABC123`, el campo terminaba
guardando solamente `123`, y las letras desaparecían sin que el usuario
se enterara.

El campo ahora admite las dos formas. Si se escribe un número, se usa
directo como identificador. Si se escribe texto, la aplicación consulta el
listado de vehículos, busca la placa sin distinguir mayúsculas de
minúsculas y envía el identificador que le corresponde. La búsqueda
resulta necesaria porque la tabla de reservas solo admite un entero en el
campo de vehículo: enviar la placa directamente produce un error 500 en
el servidor.

Si la placa no está registrada, la aplicación lo indica y sugiere
registrarla primero, en lugar de dejar fallar la reserva sin explicación.

**Las rutas de la API estaban en minúsculas.**

Los ocho puntos de la aplicación llamaban a rutas como `/api/cupos`,
`/api/vehiculos` o `/api/pagos`. El servidor distingue mayúsculas en el
mapeo de los servlets, así que todas esas peticiones devolvían 404 y
ninguna pantalla cargaba datos. Se corrigieron a mayúscula inicial:
`/api/Cupos`, `/api/Vehiculos`, `/api/Pagos`.

Este error no era evidente al leer el código, porque la ruta escrita y la
ruta esperada se parecían mucho.

**Las validaciones no mostraban aviso al usuario.**

En varios formularios se usaba únicamente el método `setError` sobre el
campo. Ese método deja un icono pequeño al borde del campo que el usuario
no alcanza a percibirse, así que la aplicación parecía no responder cuando
una validación fallaba. Se agregó un mensaje visible en cada caso, con el
texto concreto de lo que falta.

**Los errores del servidor se mostraban como códigos.**

Cuando una operación fallaba, la aplicación mostraba `Error HTTP: 400` o
`Error HTTP: 500` sin más contexto. Ahora se traducen a mensajes que
indican qué revisar: si el vehículo o el cupo no existen, o si hubo un
error al guardar el registro.

### Mejoras

**La dirección del servidor ya no está fija en el código.**

Las once llamadas usaban una dirección escrita directamente en cada
archivo, lo que obligaba a recompilar para cambiarla. Ahora todas pasan
por una clase única y la dirección base se define fuera del código, en la
clave `eparking.baseUrl`.

El valor predeterminado está en `gradle.properties`, que sí se versiona,
para que el proyecto compile en cualquier equipo sin pasos adicionales.
Cada equipo puede sobrescribirlo en `local.properties`, que no se
versiona, y así apuntar a su propio servidor sin modificar archivos del
repositorio.

**Se corrigió la contraseña mínima de forma accidental.**

La primera implementación del registro exigía seis caracteres, lo que
impedía crear cuentas con contraseñas más cortas sin avisar al usuario.
Se bajó el mínimo a cuatro caracteres, en línea con las cuentas ya
existentes en el sistema.

### Estado de la versión

| | |
|---|---|
| `versionName` | 1.0.2 |
| `versionCode` | 2 |
| Módulo | `app` |
| Paquete | `com.eparking.android` |

### Cómo se verificó

Las correcciones se probaron una por una sobre el emulador, no solo
revisando el código:

- Alta de usuario y luego inicio de sesión con la cuenta recién creada.
- Reserva de cupo escribiendo la placa del vehículo y comprobando que se
  guardó el identificador correcto en la base de datos.
- Reserva de cupo escribiendo el identificador numérico, para confirmar
  que el camino anterior sigue funcionando.
- Placa inexistente: se comprobó que no se envía nada y que se avisa.
- Formulario vacío, correo con formato inválido y contraseña demasiado
  corta: en los tres casos se bloquea el envío y se muestra el motivo.
- Correo ya registrado: el servidor lo rechaza y la aplicación informa
  en lugar de mostrar un código de error.

Para comprobar los datos guardados se consultó directamente la base de
datos, cruzando el identificador del vehículo con su placa.
