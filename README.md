# InmuneGame

**Grupo:** 7
**Materia:** Desarrollo de Aplicaciones para Dispositivos Móviles · IFTS N.º 18
**Temática:** Sustancias ilegales

App de juegos educativos para Android pensada para adolescentes de 15 a 17 años, para usar en clase con la guía de una docente. El objetivo es entrenar el hábito de **dudar antes de creer** lo que circula entre pares sobre el consumo de sustancias, con información de fuentes confiables (SEDRONAR / Observatorio Argentino de Drogas, Sociedad Argentina de Pediatría).

La documentación completa del desarrollo está en [DOCUMENTACION.md](DOCUMENTACION.md).

## Integrantes, roles y tareas

| Integrante | Rol | Tareas realizadas |
|---|---|---|
| Gonzalo Giri Petray | Desarrollo e integración | ¿Mito o Realidad? (lógica, pantallas, cronómetro y sensor), base de datos local con **Room**, **Ranking** con Firebase e integración de los tres juegos en la app. |
| Stephanie Velarde | Diseño y estructura | **Design System** y relato de la partida, repositorio y estructura del proyecto, y el juego **La Previa** (historia, lógica y pantallas). |
| Enzo Páez | Desarrollo del Memotest | Prototipo en **Figma**, el **Memotest** completo (cartas, sustancias, notas, animaciones y pantalla final) y la música de fondo de los tres juegos. |

## Los juegos

| Juego | Mecánica | Puntaje y final |
|---|---|---|
| **¿Mito o Realidad?** (continúa el juego de la primera entrega) | Se leen 5 afirmaciones al azar y se decide si son *Realidad* o *Mito*, con **10 segundos por frase**. Se responde con los botones o **inclinando el celular** (derecha = Verdadero, izquierda = Falso). Después de cada respuesta aparece una explicación con su fuente. | Aciertos sobre 5, contador de aciertos y errores, resumen pregunta por pregunta y últimas partidas. "Jugar de nuevo" o "Terminar y volver al menú". |
| **Memotest** (mecánica diferente) | Dar vuelta cartas de a dos para encontrar las 6 parejas. Cada pareja muestra una nota con un dato para cuidarse. | Cantidad de intentos (menos es mejor) y récord personal. **Sacudir el celular** arranca otra partida. |
| **La Previa** (extra) | Historia ramificada de 6 escenas sobre decisiones bajo presión social. Cada decisión muestra una consecuencia y un recurso. | Recorrido de decisiones y recursos de ayuda. |

### Mejoras respecto de la primera entrega

- Segundo juego (Memotest) y tercero (La Previa), integrados en un menú de juegos.
- Ingreso con usuario y contraseña (hash PBKDF2, sin datos personales).
- Intro multimedia de concientización con animación y voz narradora.
- Cronómetro de 10 segundos con tic, sonidos de acierto y error, vibración y música.
- Control por **acelerómetro** (inclinar para responder, sacudir para volver a jugar).
- **Base de datos local con Room/SQLite** para el historial, "Mi resumen" y el récord.
- **Ranking compartido con Firebase (Cloud Firestore)**.

## Base de datos local: Room / SQLite

- **Qué se guarda:** cada partida terminada de los tres juegos, en la tabla `partidas` del archivo `inmunegame.db`: usuario, juego, resultado, total de preguntas, fecha y si ya se subió al ranking.
- **Entidad:** `data/local/PartidaEntity.kt` · **DAO:** `data/local/PartidaDao.kt` · **Base:** `data/local/InmuneDatabase.kt`.
- **Operaciones:** insertar una partida; consultar las partidas del usuario, las últimas de un juego, el récord del Memotest y las pendientes de subir; marcar una partida como subida (update); borrar el historial del usuario (delete).
- **Cómo se usa:** los ViewModels guardan y leen a través de `PartidasRepository`. "Mi resumen" (partidas, mejor resultado y última vez por juego), el historial de la pantalla final de ¿Mito o Realidad? y el récord del Memotest salen de Room. Las consultas devuelven `Flow`, así que la pantalla se actualiza sola.

## Ranking compartido: Firebase (Cloud Firestore)

- Cada partida se sube a la colección `partidas` con el **nombre de usuario** (seudónimo con el que se ingresa), el juego, el resultado real y la fecha.
- La pantalla **Ranking** (menú → Ranking) muestra el top 10 de cada juego con el mejor resultado de cada jugador: **más aciertos** primero en ¿Mito o Realidad? y **menos intentos** primero en el Memotest. Tu fila se marca con "(vos)".
- **Errores de conexión:** si no hay internet se muestran los últimos datos guardados con un aviso, o un mensaje de error con el botón **Reintentar**. Las partidas que no se pudieron subir quedan pendientes en Room y se suben más tarde.
- **Reglas de seguridad:** se puede leer el ranking y crear partidas con el formato correcto, pero no modificarlas ni borrarlas. Están en [FIREBASE.md](FIREBASE.md).
- **Credenciales:** `app/google-services.json` **no está en el repositorio** (está en `.gitignore`). Cada uno usa el suyo (ver "Cómo ejecutar").

## Sensor: acelerómetro

- En **¿Mito o Realidad?**, inclinar el celular a la derecha responde *Verdadero* y a la izquierda *Falso*. Hay que volver a ponerlo derecho para que cuente otra inclinación.
- En el final del **Memotest**, sacudir el celular arranca otra partida.
- `game/DetectorMovimiento.kt` convierte las lecturas en gestos (Kotlin puro, con tests). `ui/multimedia/Sensores.kt` registra el sensor en `onResume` y lo libera en `onPause`, así no gasta batería en segundo plano.

## Arquitectura MVVM

| Capa | Dónde | Qué hace |
|---|---|---|
| **Model** | `data/`, `data/local/`, `game/` | Reglas de cada juego en Kotlin puro (`Partida`, `Memotest`, `Historia`), Room (entidad, DAO, base), Firestore (`PartidasRemotas`) y `PartidasRepository`, que une las dos. |
| **ViewModel** | `game/*ViewModel.kt` | Estado de cada pantalla como `StateFlow`: los tres juegos, login, Mi resumen y Ranking. Guardan y leen datos solo a través del repositorio. |
| **View** | `ui/screens/` | Pantallas en Jetpack Compose. Dibujan el estado y avisan al ViewModel lo que toca el jugador. |

**Ciclo de vida:** la música y la voz se pausan en `onPause` y se liberan al salir; el sensor se libera en `onPause`; el cronómetro se frena en segundo plano; el estado de la partida sobrevive a la rotación porque vive en el ViewModel; las escrituras en Room y Firebase terminan aunque se cierre la pantalla.

## Diseño

- **Prototipo en Figma:** [Mito o Realidad — Rediseño v2](https://www.figma.com/design/UzpNnKOHeR5DY6m8oeSEID/Mito-o-Realidad---Redise%25C3%25B1o-v2?node-id=2-77)
- **Design System:** [design-system.md](design-system.md), con imágenes en [`img/`](img/) (persona, colores, tipografías y botones)
- **Relato de una partida:** [relato.md](relato.md)

## Cómo ejecutar la app

1. Clonar el repositorio y abrir la carpeta en **Android Studio** (Ladybug o más nuevo).
2. **Firebase (opcional, para el ranking):** crear un proyecto en Firebase, registrar la app Android con el paquete `com.example.tpjuego`, descargar `google-services.json` y copiarlo en `app/`. Los pasos y las reglas están en [FIREBASE.md](FIREBASE.md). Sin ese archivo la app funciona igual, pero el Ranking muestra "no configurado".
3. **File → Sync Project with Gradle Files**.
4. Elegir un emulador o un celular (Android 7.0 o más nuevo) y tocar **Run ▶**.
5. Crear un usuario en la pantalla de ingreso y jugar.

En el emulador, el acelerómetro se prueba desde **Extended controls (⋯) → Virtual sensors → Device Pose**.

**Tests unitarios:** `./gradlew test` (lógica de los juegos, sensor, usuarios, resumen y ranking).

## Tecnología

Kotlin 2.1 · Jetpack Compose (Material 3) · Navigation Compose · ViewModel + StateFlow · Room 2.6 (KSP) · Firebase Cloud Firestore · SensorManager · SoundPool · MediaPlayer · TextToSpeech · Vibrator. Tipografías Staatliches e Inter (licencia OFL, ver [`licencias/`](licencias/)).
