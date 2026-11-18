# Conectar InmuneGame con Firebase (Cloud Firestore)

La app sube cada partida terminada de los tres juegos a la colección `partidas` de Cloud Firestore. Con esos datos arma el **Ranking** compartido.
Hasta que se haga esta configuración, la app funciona igual y guarda las partidas solo en el celular (Room).

> **Credenciales:** `google-services.json` identifica al proyecto de Firebase y **no se sube al repositorio** (está en `.gitignore`). Cada integrante o docente descarga el suyo siguiendo estos pasos.

## 1. Crear el proyecto
1. Entrar a https://console.firebase.google.com con una cuenta de Google.
2. **Crear un proyecto** (por ejemplo `InmuneGame`). Google Analytics se puede desactivar.

## 2. Registrar la app Android
1. En el proyecto, tocar el ícono de **Android**.
2. Nombre del paquete: `com.example.tpjuego` (tiene que ser exactamente este).
3. **Registrar app** y **descargar `google-services.json`**.
4. Copiar ese archivo dentro de la carpeta `app/` del proyecto (al lado de `build.gradle.kts`).
5. En Android Studio: **Sync Project with Gradle Files** y correr la app.

## 3. Crear la base de datos
1. En el menú de Firebase: **Compilación → Firestore Database → Crear base de datos**.
2. Ubicación: `southamerica-east1` (San Pablo) o la que sugiera.
3. Elegir **modo de prueba** para empezar.
4. Reglas de seguridad (pegar en la pestaña **Reglas** y **Publicar**):

```
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    match /partidas/{id} {
      // El ranking es público: cualquiera puede leerlo.
      allow read: if true;

      // Solo se pueden crear partidas con el formato de la app.
      allow create: if request.resource.data.keys().hasOnly(['usuario', 'juego', 'titulo', 'resultado', 'fechaCelular', 'fecha'])
                    && request.resource.data.keys().hasAll(['usuario', 'juego', 'resultado', 'fecha'])
                    && request.resource.data.usuario is string
                    && request.resource.data.usuario.size() > 0
                    && request.resource.data.usuario.size() <= 40
                    && request.resource.data.juego in ['MITO_O_REALIDAD', 'MEMOTEST', 'LA_PREVIA']
                    && request.resource.data.resultado is int
                    && request.resource.data.resultado >= 0
                    && request.resource.data.resultado <= 1000;

      // Al reintentar la subida de una partida solo puede cambiar la fecha del servidor:
      // el resultado no se puede modificar.
      allow update: if request.resource.data.diff(resource.data).affectedKeys().hasOnly(['fecha']);

      // Nadie puede borrar partidas.
      allow delete: if false;
    }
  }
}
```

Con estas reglas cualquiera puede leer el ranking y sumar partidas con el formato correcto, pero nadie puede cambiar un resultado ni borrar partidas.

## 4. Probar
1. Jugar una partida de cualquier juego hasta el final.
2. En la consola de Firebase → Firestore → colección `partidas` aparece un documento nuevo.
3. En la app, **Mi resumen → EN LA NUBE** muestra las últimas partidas del usuario.
4. En el menú, **Ranking** muestra el top 10 de ¿Mito o Realidad? y del Memotest.
5. Para probar el error de conexión: activar el modo avión y abrir el Ranking (aparece el aviso o el botón **Reintentar**).

## Formato de cada partida

| Campo | Ejemplo | Qué es |
|---|---|---|
| `usuario` | `gonza` | Usuario con el que se ingresó a la app |
| `juego` | `MITO_O_REALIDAD` | `MITO_O_REALIDAD`, `MEMOTEST` o `LA_PREVIA` |
| `titulo` | `¿Mito o Realidad?` | Nombre del juego |
| `resultado` | `4` | Aciertos (Mito), intentos (Memotest) o decisiones (La Previa) |
| `fechaCelular` | `1763035200000` | Fecha del celular en milisegundos |
| `fecha` | timestamp | Fecha del servidor de Firebase |
