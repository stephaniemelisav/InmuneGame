# Documentación del desarrollo — InmuneGame

**Grupo 7** · Desarrollo de Aplicaciones para Dispositivos Móviles · Temática: sustancias ilegales
Repositorio: https://github.com/stephaniemelisav/InmuneGame

---

## A. Descripción del juego

| | |
|---|---|
| **Nombre** | **InmuneGame**, una app con tres juegos: **¿Mito o Realidad?**, **Memotest** y **La Previa** |
| **Objetivo** | Entrenar el hábito de *dudar antes de creer* lo que circula entre pares sobre el consumo de sustancias, con información de fuentes confiables (SEDRONAR / Observatorio Argentino de Drogas, Sociedad Argentina de Pediatría). No busca retar ni moralizar. |
| **Público destinatario** | Adolescentes de 15 a 17 años, en clase y con la guía de una docente. Persona usuaria: Valentina, 16 años (ver [design-system.md](design-system.md)). |
| **Mecánica principal** | **¿Mito o Realidad?**: se leen 5 afirmaciones elegidas al azar de un banco y se decide si cada una es *Realidad (Verdadero)* o *Mito (Falso)*, con **10 segundos por frase** (si se acaba el tiempo, cuenta como error). Después de cada respuesta, acierte o no, aparece una explicación con su fuente. **Memotest**: hay que dar vuelta cartas de a dos para encontrar las 6 parejas (cada sustancia aparece dos veces); al encontrar una pareja se muestra una nota con un dato para cuidarse, que se puede volver a leer. **La Previa**: una historia en 6 escenas (una noche de previa) donde se elige cómo reaccionar ante la presión del grupo; cada decisión muestra una consecuencia y un recurso, y algunas llevan a escenas distintas. No hay puntaje ni "final malo". |
| **Interacción** | Toques con una mano: botones grandes (58 dp) en el tercio inferior de la pantalla. En ¿Mito o Realidad? también se responde **inclinando el celular** (acelerómetro) y en el Memotest se **sacude** para volver a jugar. Solo se pide un usuario y una contraseña, sin datos personales. |
| **Recorrido de la partida** | Ingreso con usuario y contraseña → intro de concientización → menú de juegos → elige un juego → juega (con contador de aciertos y errores, o de intentos y pares) → pantalla final con resumen de resultados → **"Jugar de nuevo"** o **"Terminar y volver al menú"**. |

El detalle narrativo de una partida está en [relato.md](relato.md).

---

## B. Estructura de la aplicación

```
                 ┌──────────────────────┐
  Abrir app ───► │ Ingreso: usuario y   │  (si ya hay sesión iniciada, se saltea)
                 │ contraseña           │
                 └──────────┬───────────┘
                            ▼
                 ┌──────────────────────┐
                 │ Intro multimedia     │  (se puede saltear; se vuelve a ver desde el menú)
                 └──────────┬───────────┘
                            ▼
                 ┌──────────────────────┐
                 │ Menú de juegos       │ ◄─────────────────────────────┐
                 └─────┬──────────┬─────┘                               │
                       │          │                                     │
   ¿Mito o Realidad?   ▼          ▼   Memotest                          │
        ┌──────────────────┐   ┌────────────────────────┐               │
        │ Portada con las  │   │ Tablero de 12 cartas   │               │
        │ reglas: "Cómo se │   │ (intentos y pares)     │               │
        │ juega" (3 pasos) │   └───────────┬────────────┘               │
        └────────┬─────────┘               ▼                            │
                 │             ┌────────────────────────┐               │
                 │             │ Final: intentos +      │── Terminar ───┤
                 ▼             │ "Lo que aprendiste"    │               │
        ┌──────────────────┐   └────────────────────────┘               │
        │ Pregunta (V / F) │ ◄──────────┐                               │
        └────────┬─────────┘            │ Siguiente (5 preguntas)       │
                 ▼                      │                               │
        ┌──────────────────┐            │                               │
        │ Resultado +      │ ───────────┘                               │
        │ explicación      │                                            │
        └────────┬─────────┘                                            │
                 ▼                                                      │
        ┌──────────────────────────────┐                                │
        │ Final: puntaje, aciertos y   │──── Terminar y volver al menú ─┘
        │ errores, resumen e historial │
        └──────────────────────────────┘
```

**Resumen del flujo:** Ingreso → Intro → Menú → Portada (con cómo se juega) → Pregunta → Resultado → (×5) → Final → Menú

| Pantalla | Archivo | Qué hace |
|---|---|---|
| Ingreso | `ui/screens/LoginScreen.kt` | Usuario y contraseña. Permite entrar o crear un usuario nuevo. |
| Intro | `ui/screens/IntroScreen.kt` | Animación de concientización con voz que lee cada mensaje (o video, si existe `res/raw/intro.mp4`). Botón "Saltar". |
| Menú de juegos | `ui/screens/HomeScreen.kt` | Saludo al usuario, una tarjeta por juego y accesos a "Mi resumen", "Ranking", "Ver intro" y "Salir" (cierra la sesión). |
| Mi resumen | `ui/screens/ResumenScreen.kt` | Pestaña **MIS JUEGOS** (desde Room): partidas jugadas, mejor resultado y última vez de cada juego, con opción de borrar el historial del celular. Pestaña **EN LA NUBE** (desde Firestore): últimas partidas subidas. |
| Ranking | `ui/screens/RankingScreen.kt` | Ranking compartido (Firestore) de ¿Mito o Realidad? y del Memotest: top 10 con el mejor resultado de cada jugador. Avisa si no hay conexión y tiene botón Reintentar. |
| Portada | `ui/screens/PortadaScreen.kt` | Título, "¿Cuánto sabés realmente?", tarjeta "Cómo se juega" con tres pasos, música de portada y botón "Comencemos". |
| Pregunta / Resultado / Final | `ui/screens/GameScreen.kt` | Una sola pantalla que cambia de fase según el estado de la partida. |
| Portada del Memotest | `ui/screens/PortadaMemotestScreen.kt` | Título, explicación corta y música de introducción. |
| Memotest | `ui/screens/MemotestScreen.kt` | Tablero con cartas animadas, notas de cada pareja, música de fondo y pantalla final con récord. |
| La Previa | `ui/screens/LaPreviaScreen.kt` | Portada, escena con opciones, consecuencia con recurso y final con recorrido. Una sola pantalla que cambia de fase según el estado de la historia. |

La app tiene **cinco Activities**: una para el ingreso, una para el menú y una por juego:

| Activity | Pantallas que contiene | Cómo se llega |
|---|---|---|
| `LoginActivity` | Ingreso con usuario y contraseña | Al abrir la app (es la Activity `LAUNCHER`) |
| `MainActivity` | Intro, menú de juegos, Mi resumen y Ranking | Después de ingresar |
| `MitoORealidadActivity` | Portada (con cómo se juega), pregunta, resultado y final | Desde el menú, con un `Intent` |
| `MemotestActivity` | Memotest (portada, tablero y final) | Desde el menú, con un `Intent` |
| `LaPreviaActivity` | La Previa (portada, escenas, consecuencias y final) | Desde el menú, con un `Intent` |

Dentro de cada Activity, la navegación entre pantallas se define en `ui/AppNavigation.kt` (`AppNavigation()` para el menú y `MitoORealidadNavigation()` para ¿Mito o Realidad?).

---

## C. Prototipo y Design System

- **Prototipo en Figma:** [Mito o Realidad — Rediseño v2](https://www.figma.com/design/UzpNnKOHeR5DY6m8oeSEID/Mito-o-Realidad---Redise%25C3%25B1o-v2?node-id=2-77)
- **Design System documentado:** [design-system.md](design-system.md), con imágenes en [`img/`](img/)
- **Design System en código:** `ui/theme/` (colores, tipografías y tema) y `ui/components/Componentes.kt` (componentes reutilizables)

**Decisiones visuales:**
- **Fondo oscuro con degradé** (turquesa → azul noche) y texto claro: reduce el brillo en el aula y se lee bien al sol.
- **Amarillo = lo que hay que tocar.** Hay un solo botón primario por pantalla y siempre está en el tercio inferior, al alcance del pulgar.
- **Contraste WCAG verificado:** todos los colores cumplen 4,5:1 en texto normal. Algunos tonos del primer prototipo se oscurecieron para llegar a ese mínimo.
- **El color nunca va solo:** Verdadero/Falso y acierto/error llevan además texto e ícono (✓ / ✕). El juego funciona también para personas con daltonismo.
- **El error en naranja-bordó y no en rojo intenso:** equivocarse no se vive como castigo, el foco está en la explicación.
- **Tipografías:** *Staatliches* para títulos cortos, con estilo de videojuego, e *Inter* para textos largos, con buena legibilidad en pantalla. Ningún texto baja de 16 sp.

---

## D. Implementación técnica

### Arquitectura MVVM

| Capa | Dónde | Responsabilidad |
|---|---|---|
| **Model** | `data/`, `data/local/` y `game/` | `Partida`, `Memotest` e `Historia` tienen las reglas de cada juego en Kotlin puro. `DetectorMovimiento` interpreta el acelerómetro. En `data/`: banco de afirmaciones, temas del Memotest, historia de La Previa, usuarios (`Usuarios`, `Credencial`, `Sesion`), juegos y criterio de récord (`Juego`, `Resumen`), ranking (`Ranking`). En `data/local/`: la base **Room** (`PartidaEntity`, `PartidaDao`, `InmuneDatabase`). `PartidasRemotas` habla con Firestore y `PartidasRepository` une Room y Firestore. |
| **ViewModel** | `game/*ViewModel.kt` | `MitoORealidadViewModel`, `MemotestViewModel`, `LaPreviaViewModel`, `LoginViewModel`, `ResumenViewModel` y `RankingViewModel`. Exponen el estado como `StateFlow` y reciben las acciones de la vista. Leen y guardan partidas **solo a través de `PartidasRepository`**: no saben si los datos vienen de Room o de Firebase. |
| **View** | `ui/screens/` | Pantallas en Jetpack Compose. Solo dibujan el estado (`collectAsState()`) y avisan al ViewModel cuando el jugador toca algo. No tienen lógica del juego. |

Como esas clases no dependen de Android, se prueban con **tests unitarios** (`app/src/test/`): `PartidaTest`, `HistoriaTest`, `MemotestTest`, `DetectorMovimientoTest`, `UsuariosTest` y `ResumenYRankingTest` (resumen armado desde las filas de Room, criterio de récord y orden del ranking).

### Administración del estado del juego

- `Partida` es una **clase inmutable** (`data class`). Cada acción devuelve un estado nuevo con `copy()` en lugar de modificar el actual.
- La fase de la pantalla sale del estado: si todavía no respondió se muestra la **pregunta**, si ya respondió se muestra el **resultado** y si terminó se muestra el **final**.
- Se guarda la lista de respuestas dadas, y de ahí salen el contador de aciertos y errores y el resumen pregunta por pregunta.
- Los toques repetidos se ignoran: solo cuenta la primera respuesta de cada pregunta.
- **La Previa:** la historia es un grafo de escenas (`data/LaPreviaHistoria.kt`): cada opción indica el `id` de la escena siguiente, y los caminos se separan y se vuelven a juntar. `Historia` (Kotlin puro e inmutable, como `Partida`) guarda la escena actual, la opción elegida y el recorrido; `LaPreviaViewModel` la expone como `StateFlow`. Los tests (`HistoriaTest`) verifican que todas las opciones lleven a escenas que existen y que todos los caminos terminen.
- **Memotest (versión final de Enzo):** las reglas que programó Enzo están en `Memotest` (Kotlin puro e inmutable): cartas, selección, intentos, notas encontradas, nota abierta y pantalla final. `MemotestViewModel` la expone como `StateFlow`, espera 1 segundo antes de tapar dos cartas que no coinciden y, al cerrar la nota de la última pareja, guarda la partida (Room y Firestore) **una sola vez** y actualiza el récord. Como el estado vive en el ViewModel, **la partida no se reinicia al rotar el celular**.
- El sonido de acierto, la vibración y el sonido de "juego completado" llegan como **eventos** del ViewModel (`SharedFlow`), así no se repiten al rotar la pantalla. El diseño de la pantalla (colores, cartas con giro, notas y `Dialog`) es el de Enzo.

### Usuarios y resumen por usuario

- Al abrir la app se pide **usuario y contraseña**. Se puede entrar o crear un usuario nuevo. Solo se piden esos dos datos (sin mail ni datos personales).
- La contraseña **no se guarda**: se guarda un hash **PBKDF2** con una sal aleatoria (`Credencial`). Al ingresar se calcula el hash otra vez y se compara. Si el usuario o la contraseña no coinciden, el mensaje es el mismo para los dos casos.
- Los usuarios y la sesión quedan en el celular (`SharedPreferences`); las partidas, en la base **Room**. El nombre de usuario es el seudónimo que aparece en el ranking.
- Cada usuario tiene su propio historial y récord. **Mi resumen** muestra por juego las partidas jugadas, el mejor resultado (aciertos en Mito, menos intentos en Memotest, decisiones en La Previa) y la fecha de la última partida.
- La sesión queda iniciada hasta tocar **Salir** en el menú.
- *Nota de diseño:* la persona del proyecto "no quiere registrarse". Por eso el registro es lo más corto posible: solo usuario y contraseña, y se crea desde la misma pantalla.

### Base de datos local (Room / SQLite)

- **Qué se guarda:** cada partida terminada de los tres juegos, en la tabla `partidas` del archivo `inmunegame.db` (SQLite, manejado con **Room**).

| Columna | Tipo | Qué es |
|---|---|---|
| `id` | Long (clave, autoincremental) | Identificador de la partida |
| `usuario` | String (índice) | Usuario con el que se jugó |
| `juego` | String (índice) | `MITO_O_REALIDAD`, `MEMOTEST` o `LA_PREVIA` |
| `resultado` | Int | Aciertos (Mito), intentos (Memotest) o decisiones (La Previa) |
| `total` | Int? | Cantidad de preguntas (solo Mito) |
| `fechaMillis` | Long | Fecha de la partida |
| `enLaNube` | Boolean | Si ya se subió al ranking de Firebase |

- **Entidad:** `data/local/PartidaEntity.kt` (`@Entity`). **DAO:** `data/local/PartidaDao.kt` (`@Dao`). **Base:** `data/local/InmuneDatabase.kt` (`@Database`, una sola instancia para toda la app).
- **Operaciones del DAO:**
  - `insertar` (insert): guarda la partida al terminar.
  - `partidasDe(usuario)` (query, `Flow`): alimenta **Mi resumen**; la pantalla se actualiza sola cuando se agrega una partida.
  - `ultimas(usuario, juego, cantidad)` (query, `Flow`): historial de la pantalla final de ¿Mito o Realidad?.
  - `menorResultado(usuario, juego)` (query): récord del Memotest.
  - `pendientesDeSubir()` (query) y `marcarEnLaNube(id)` (update): sincronización con Firebase.
  - `borrarDe(usuario)` (delete): botón "Borrar historial del celular" en Mi resumen.
- **Integración con MVVM:** las pantallas nunca tocan la base. Los ViewModels llaman a `PartidasRepository`, que usa el DAO dentro de corrutinas (`suspend` y `Flow`), así la base no se consulta en el hilo principal.
- **Room se genera con KSP** (`androidx.room:room-compiler`), que crea en tiempo de compilación el código SQL de las consultas.

### Ranking compartido (Cloud Firestore)

- **Registro de resultados reales:** al terminar una partida, `PartidasRepository` la guarda primero en Room y después la sube a Firestore (`data/PartidasRemotas.kt`), en la colección `partidas`, con el **usuario** (seudónimo), el juego, el resultado y la fecha. El id del documento es `usuario_fecha`, así una partida reintentada no se duplica.
- **Pantalla Ranking:** `RankingViewModel` pide las partidas de un juego y `Ranking.armar()` (Kotlin puro, con tests) se queda con el **mejor resultado de cada jugador** y las ordena según el criterio del juego: **más aciertos** primero en ¿Mito o Realidad? y **menos intentos** primero en el Memotest (empates por nombre). Se muestra el top 10, con los tres primeros destacados en amarillo y "(vos)" en la fila del usuario.
- **Errores de conexión:**
  - Sin internet, Firestore devuelve lo último que tenía guardado y la pantalla avisa "Sin conexión: se muestran los últimos datos guardados".
  - Si no se puede leer nada, muestra un mensaje de error y el botón **Reintentar**.
  - Si una partida no se pudo subir, queda en Room con `enLaNube = false` y se vuelve a subir al abrir **Mi resumen** (`sincronizarPendientes()`).
- **Reglas de seguridad:** cualquiera puede leer el ranking y crear partidas con el formato correcto (campos obligatorios, juego válido, resultado entero), pero nadie puede modificar el resultado ni borrar partidas. Las reglas completas están en [FIREBASE.md](FIREBASE.md).
- **Credenciales:** el archivo `app/google-services.json` **no se sube al repositorio** (está en `.gitignore`). Si falta, la app compila y funciona igual con Room, y el ranking muestra "no configurado".

### Sensores y vibración

- **Acelerómetro** (`SensorManager`, `TYPE_ACCELEROMETER`). `DetectorMovimiento` convierte las lecturas en gestos:
  - En **¿Mito o Realidad?**, inclinar el celular a la **derecha** responde *Verdadero* y a la **izquierda** *Falso*. Los botones siguen funcionando. Hay que volver a poner el celular derecho para que cuente otra inclinación.
  - En el final del **Memotest**, **sacudir** el celular arranca otra partida.
- El sensor se registra en `onResume` y se libera en `onPause` (`LifecycleEventObserver` en `ui/multimedia/Sensores.kt`), así no gasta batería con la app en segundo plano. Solo escucha en las pantallas donde se usa.
- **Vibración:** en ¿Mito o Realidad?, si la respuesta es incorrecta (o se acaba el tiempo) el celular vibra (`Vibrator` / `VibrationEffect`, permiso `VIBRATE`).

### Cronómetro de ¿Mito o Realidad?

- Cada frase tiene **10 segundos**. La cuenta regresiva se ve en un círculo junto a la pregunta y se pone naranja en los últimos 3 segundos.
- Cada segundo suena un **tic** (más agudo al final). Si llega a 0, la pregunta cuenta como **error**: suena, vibra y se muestra "¡SE ACABÓ EL TIEMPO!" con la explicación. En el resumen figura como "SIN TIEMPO".
- El reloj vive en el `MitoORealidadViewModel` (corrutina en `viewModelScope`), así que **sigue donde estaba al rotar** el celular. Se **frena en `onPause`** y se retoma en `onResume`: si la app pasa a segundo plano, no se pierde tiempo.
- En `Partida`, `sinTiempo()` registra la pregunta sin respuesta (Kotlin puro, probado en `PartidaTest`).

### Ciclo de vida de las Activities

- La app tiene **cinco Activities**: `LoginActivity` (ingreso), `MainActivity` (intro, menú y resumen), `MitoORealidadActivity`, `MemotestActivity` y `LaPreviaActivity` (una por juego). Todas heredan de `BaseActivity`.
- **Ingreso:** `LoginActivity` abre `MainActivity` y llama a `finish()`, así "Atrás" desde el menú no vuelve al login. Al tocar **Salir**, `MainActivity` cierra la sesión, abre `LoginActivity` y se cierra.
- **Abrir un juego:** el menú lanza la Activity del juego con un `Intent` (`startActivity`). `MainActivity` pasa a `onPause` y `onStop`, pero queda en la pila.
- **"Terminar y volver al menú":** la Activity del juego llama a `finish()`, pasa por `onPause`, `onStop` y `onDestroy`, y Android vuelve a mostrar `MainActivity` (`onRestart`, `onStart`, `onResume`) en el mismo estado en que estaba.
- **Registro del ciclo de vida:** `BaseActivity` escribe en el **Logcat** cada callback (`onCreate`, `onStart`, `onResume`, `onPause`, `onStop`, `onRestart`, `onDestroy`), con el filtro `CicloDeVida`. Así se puede ver en vivo qué pasa con cada Activity al entrar y salir de los juegos.
- **Configuración común:** en `onCreate`, `BaseActivity` configura `enableEdgeToEdge()` con íconos claros en la barra de estado. Cada Activity carga su interfaz con `setContent`.
- **Rotación y cambios de configuración:** Android destruye y recrea la Activity (en el log aparece `onDestroy` y `onCreate (recreada)`), pero el `ViewModel` sobrevive, así que la partida de *¿Mito o Realidad?* no se pierde al girar el celular.
- **Persistencia y almacenamiento:** las partidas se guardan en **Room**, así que se conservan aunque se cierre la app. La escritura arranca al terminar la partida y la subida a Firebase y la marca `enLaNube` se completan en un scope propio del repositorio, así no se cortan si se cierra la pantalla. La lectura de Mi resumen (`Flow`) se corta 5 segundos después de que la pantalla deja de verse (`WhileSubscribed`).
- **Voz de la intro:** el `TextToSpeech` se calla en `onPause` y se libera (`shutdown()`) cuando la intro deja de mostrarse.
- **Liberación de recursos:** si la intro reproduce un video local, el `VideoView` se detiene al salir de la pantalla (`onRelease` → `stopPlayback()`). Si el video falla, la app pasa al menú en lugar de trabarse.
- **Pila de navegación:** la intro se quita de la pila (`popUpTo`), así que "Atrás" desde el menú cierra la app.
- **Música de fondo:** `MusicaDeFondo` (`ui/components/MusicaDeFondo.kt`) crea un `MediaPlayer` en bucle que se pausa en `onPause`, sigue en `onResume` y se libera (`release()`) cuando la pantalla deja de mostrarse. Lo usan las portadas y las partidas de los tres juegos. El tablero del Memotest tiene su propio `MediaPlayer` con el mismo manejo.
- **Sonidos y sensor:** el `SoundPool` se libera cuando la pantalla deja de mostrarse (`DisposableEffect` → `release()`), y el acelerómetro se libera en `onPause`.

### Recursos multimedia

- **Intro de concientización animada**, hecha con animaciones de Compose: mensajes que aparecen y se desvanecen (`Animatable`), partículas girando dibujadas en `Canvas` (`rememberInfiniteTransition`) y el logo con efecto rebote (`spring`).
- **Voz narradora:** mientras aparece cada mensaje de la animación, el celular lo lee en voz alta con el lector de texto de Android (`TextToSpeech`, español). Cada mensaje queda en pantalla hasta que termina de leerse. No necesita internet ni archivos de audio; si el celular no tiene voz en español, la animación sigue sin sonido (`ui/multimedia/Narrador.kt`).
- **Soporte para video:** si se agrega `res/raw/intro.mp4`, la intro lo reproduce con `VideoView` en lugar de la animación.
- **Efectos de sonido** propios (`res/raw/sonido_acierto.wav`, `sonido_error.wav`, `sonido_fin.wav`, `sonido_tic.wav`) con `SoundPool`: suenan al acertar, al equivocarse y al terminar en ¿Mito o Realidad?, y el tic del cronómetro en cada segundo.
- **Transiciones** entre las fases del juego (`Crossfade`) y barra de progreso animada.
- **Música de fondo** con `MediaPlayer`: `introduccion1.mp3` en las portadas, `music3.mp3` durante ¿Mito o Realidad?, `music4.mp3` durante La Previa y `music5.mp3` en el tablero del Memotest.
- **Sonidos del Memotest** (`acierto1.mp3`, `juegocompletado1.mp3`).
- **Tipografías propias** incluidas en `res/font/` (Staatliches e Inter, licencia OFL en [`licencias/`](licencias/)).

### Librerías y componentes de Android

| Librería / componente | Versión | Uso |
|---|---|---|
| Kotlin | 2.1.0 | Lenguaje |
| Android Gradle Plugin | 8.7.3 | Build (compileSdk 35, minSdk 24) |
| Jetpack Compose (BOM) | 2024.12.01 | Interfaz declarativa |
| Material 3 | (BOM) | Botones, superficies y tema |
| Navigation Compose | 2.8.5 | Navegación entre pantallas dentro de cada Activity |
| Lifecycle ViewModel Compose | 2.8.7 | `ViewModel` / `AndroidViewModel` |
| Activity Compose | 1.9.3 | `ComponentActivity`, `setContent`, `enableEdgeToEdge` |
| Intents | — | Abrir la Activity de cada juego desde el menú |
| Log (Logcat) | — | Registro del ciclo de vida de las Activities |
| Kotlin Coroutines (`StateFlow`) | — | Estado observable del ViewModel |
| SharedPreferences | — | Usuarios y sesión |
| Room (runtime, ktx, compiler) | 2.6.1 | Base de datos local SQLite: entidad, DAO y consultas con `Flow` |
| KSP | 2.1.0-1.0.29 | Genera el código de Room |
| TextToSpeech | — | Voz que lee los mensajes de la intro |
| VideoView (`AndroidView`) | — | Video de la intro (opcional) |
| Lifecycle Runtime Compose | 2.8.7 | `LocalLifecycleOwner` para registrar el sensor en `onResume` y liberarlo en `onPause` |
| SensorManager (acelerómetro) | — | Inclinar para responder y sacudir para jugar de nuevo |
| Vibrator / VibrationEffect | — | Vibración al equivocarse |
| SoundPool | — | Efectos de sonido |
| PBKDF2 (`javax.crypto`) | — | Hash de contraseñas |
| Firebase BOM / Cloud Firestore | 33.7.0 | Ranking compartido y copia de las partidas en la nube |
| Google Services (plugin) | 4.4.2 | Lee `google-services.json` |
| JUnit | 4.13.2 | Tests unitarios |

### Fuentes de La Previa

Cada recurso que aparece en La Previa muestra su fuente en pantalla. Estos son los documentos de los que sale cada uno:

| Recurso del juego | Fuente |
|---|---|
| Decir «no, gracias» de forma firme, poner una excusa, repetir el no o cambiar de tema | Academia Americana de Pediatría (AAP), *Helping Teens Resist Pressure to Try Drugs*: https://www.healthychildren.org/English/ages-stages/teen/substance-abuse/Pages/Helping-Teens-Resist-Pressure-to-Try-Drugs.aspx |
| Alejarse de la situación | SAMHSA (EE. UU.), campaña *Talk. They Hear You.*: https://library.samhsa.gov/sites/default/files/pep23-03-01-006.pdf |
| No se puede saber qué contiene una pastilla | SEDRONAR, «Cada vez es más difícil asegurar que cuando una persona está consumiendo una pastilla eso sea éxtasis» (2017): https://www.argentina.gob.ar/noticias/cada-vez-es-mas-dificil-asegurar-que-cuando-una-persona-esta-consumiendo-una-pastilla-eso-0 |
| No dejar sola a una persona intoxicada, buscar a un adulto responsable y llamar a emergencias | SEDRONAR, recomendaciones por el Día de las y los Estudiantes: https://www.argentina.gob.ar/noticias/recomendaciones-de-la-sedronar-por-los-festejos-en-el-pais-del-dia-de-las-y-los-estudiantes |
| Acostar de costado (posición de recuperación) para evitar que se ahogue con el vómito | Ministerio de Salud de la Nación, primeros auxilios: https://www.argentina.gob.ar/salud/primerosauxilios/posiciondeseguridad |
| 107 (ambulancias del SAME, CABA) y 911 | Gobierno de la Ciudad de Buenos Aires, teléfonos útiles: https://buenosaires.gob.ar/gcaba_historico/jefaturadegabinete/telefonos-utiles |
| Alcohol al volante y riesgo de siniestros | Agencia Nacional de Seguridad Vial, Observatorio Vial: https://www.argentina.gob.ar/seguridadvial/observatoriovialnacional/el-consumo-de-alcohol-y-la-seguridad-vial |
| Línea 141: gratuita, anónima, las 24 horas | SEDRONAR: https://www.argentina.gob.ar/noticias/se-modifico-la-leyenda-de-la-linea-141-de-la-sedronar-para-los-medios-de-comunicacion y https://x.com/Sedronar/status/1357052903232393216 |

Las estrategias para decir que no salen de fuentes de pediatría y salud de EE. UU. (AAP y SAMHSA), porque las fuentes argentinas consultadas no las detallan. El resto de los datos son de organismos argentinos.

---

## E. Participación de los integrantes

El juego **¿Mito o Realidad?** se desarrolló de manera **colaborativa** entre los tres integrantes. Cada uno aportó desde su rol, y además Enzo desarrolló el segundo juego, el **Memotest**, y Stephanie el tercero, **La Previa**.

| Integrante | Rol | Tareas realizadas |
|---|---|---|
| **Gonzalo Giri Petray** | Desarrollo e integración | ¿Mito o Realidad? (lógica, pantallas, cronómetro y sensor), base de datos local con **Room**, **Ranking** con Firebase e integración de los tres juegos en la app. |
| **Stephanie Velarde** | Diseño y estructura | **Design System** y relato de la partida, repositorio y estructura del proyecto, y el juego **La Previa** (historia, lógica y pantallas). |
| **Enzo Páez** | Desarrollo del Memotest | Prototipo en **Figma**, el **Memotest** completo (cartas, sustancias, notas, animaciones y pantalla final) y la música de fondo de los tres juegos. |

El historial de commits del repositorio (ramas `main`, `dev-gonzalo` y `dev-enzo`) muestra el aporte de cada integrante.
