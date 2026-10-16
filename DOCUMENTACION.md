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
| **Mecánica principal** | **¿Mito o Realidad?**: se leen 5 afirmaciones elegidas al azar de un banco y se decide si cada una es *Realidad (Verdadero)* o *Mito (Falso)*. Después de cada respuesta, acierte o no, aparece una explicación con su fuente. **Memotest**: hay que dar vuelta cartas de a dos para unir cada sustancia con un dato sobre sus efectos (6 pares). **La Previa**: una historia en 6 escenas (una noche de previa) donde se elige cómo reaccionar ante la presión del grupo; cada decisión muestra una consecuencia y un recurso, y algunas llevan a escenas distintas. No hay puntaje ni "final malo". |
| **Interacción** | Solo toques, con una mano. Botones grandes (58 dp) en el tercio inferior de la pantalla. No hay reloj ni vidas, no hay que registrarse y no se piden datos personales. |
| **Recorrido de la partida** | Intro de concientización → menú de juegos → elige un juego → juega (con contador de aciertos y errores, o de intentos y pares) → pantalla final con resumen de resultados → **"Jugar de nuevo"** o **"Terminar y volver al menú"**. |

El detalle narrativo de una partida está en [relato.md](relato.md).

---

## B. Estructura de la aplicación

```
                 ┌──────────────────────┐
  Abrir app ───► │ Intro multimedia     │  (se puede saltear; se vuelve a ver desde el menú)
                 └──────────┬───────────┘
                            ▼
                 ┌──────────────────────┐
                 │ Menú de juegos       │ ◄─────────────────────────────┐
                 └─────┬──────────┬─────┘                               │
                       │          │                                     │
   ¿Mito o Realidad?   ▼          ▼   Memotest                          │
        ┌──────────────────┐   ┌────────────────────────┐               │
        │ Portada          │   │ Tablero de 12 cartas   │               │
        └────────┬─────────┘   │ (intentos y pares)     │               │
                 ▼             └───────────┬────────────┘               │
        ┌──────────────────┐               ▼                            │
        │ Cómo se juega    │   ┌────────────────────────┐               │
        └────────┬─────────┘   │ Final: intentos +      │── Terminar ───┤
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

**Resumen del flujo:** Intro → Menú → Portada → Cómo se juega → Pregunta → Resultado → (×5) → Final → Menú

| Pantalla | Archivo | Qué hace |
|---|---|---|
| Intro | `ui/screens/IntroScreen.kt` | Animación de concientización (o video, si existe `res/raw/intro.mp4`). Botón "Saltar". |
| Menú de juegos | `ui/screens/HomeScreen.kt` | Dos tarjetas, una por juego, y un enlace para volver a ver la intro. |
| Portada | `ui/screens/PortadaScreen.kt` | Título, "¿Cuánto sabés realmente?" y botón "Comencemos". |
| Cómo se juega | `ui/screens/ComoSeJuegaScreen.kt` | Tres pasos cortos antes de jugar. |
| Pregunta / Resultado / Final | `ui/screens/GameScreen.kt` | Una sola pantalla que cambia de fase según el estado de la partida. |
| Memotest | `ui/screens/MemotestScreen.kt` | Tablero, contador y pantalla final. |
| La Previa | `ui/screens/LaPreviaScreen.kt` | Portada, escena con opciones, consecuencia con recurso y final con recorrido. Una sola pantalla que cambia de fase según el estado de la historia. |

La app tiene **cuatro Activities**, una para el menú y una por juego:

| Activity | Pantallas que contiene | Cómo se llega |
|---|---|---|
| `MainActivity` | Intro y menú de juegos | Al abrir la app |
| `MitoORealidadActivity` | Portada, cómo se juega, pregunta, resultado y final | Desde el menú, con un `Intent` |
| `MemotestActivity` | Memotest (tablero y final) | Desde el menú, con un `Intent` |
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
| **Model** | `data/` y `game/Partida.kt` | `Afirmacion` y el banco de afirmaciones (`AfirmacionesRepositorio`). `Partida` contiene las reglas del juego en Kotlin puro: responder, avanzar y contar aciertos y errores. `HistorialPartidas` guarda los resultados. |
| **ViewModel** | `game/MitoORealidadViewModel.kt` | Expone el estado de la partida como `StateFlow<Partida>` y recibe las acciones de la vista: `responder()`, `siguiente()` y `jugarDeNuevo()`. Al terminar, guarda el resultado en el historial. |
| **View** | `ui/screens/` | Pantallas en Jetpack Compose. Solo dibujan el estado (`collectAsState()`) y avisan al ViewModel cuando el jugador toca algo. No tienen lógica del juego. |

Como `Partida` no depende de Android, sus reglas se prueban con **tests unitarios** (`app/src/test/.../PartidaTest.kt`).

### Administración del estado del juego

- `Partida` es una **clase inmutable** (`data class`). Cada acción devuelve un estado nuevo con `copy()` en lugar de modificar el actual.
- La fase de la pantalla sale del estado: si todavía no respondió se muestra la **pregunta**, si ya respondió se muestra el **resultado** y si terminó se muestra el **final**.
- Se guarda la lista de respuestas dadas, y de ahí salen el contador de aciertos y errores y el resumen pregunta por pregunta.
- Los toques repetidos se ignoran: solo cuenta la primera respuesta de cada pregunta.
- **La Previa:** la historia es un grafo de escenas (`data/LaPreviaHistoria.kt`): cada opción indica el `id` de la escena siguiente, y los caminos se separan y se vuelven a juntar. `Historia` (Kotlin puro e inmutable, como `Partida`) guarda la escena actual, la opción elegida y el recorrido; `LaPreviaViewModel` la expone como `StateFlow`. Los tests (`HistoriaTest`) verifican que todas las opciones lleven a escenas que existen y que todos los caminos terminen.
- **Memotest:** maneja su estado dentro de la pantalla con `remember` y `mutableStateOf` (cartas, selección actual e intentos), y usa `LaunchedEffect` para comparar las dos cartas dadas vuelta.

### Ciclo de vida de las Activities

- La app tiene **cuatro Activities**: `MainActivity` (intro y menú), `MitoORealidadActivity`, `MemotestActivity` y `LaPreviaActivity` (una por juego). Todas heredan de `BaseActivity`.
- **Abrir un juego:** el menú lanza la Activity del juego con un `Intent` (`startActivity`). `MainActivity` pasa a `onPause` y `onStop`, pero queda en la pila.
- **"Terminar y volver al menú":** la Activity del juego llama a `finish()`, pasa por `onPause`, `onStop` y `onDestroy`, y Android vuelve a mostrar `MainActivity` (`onRestart`, `onStart`, `onResume`) en el mismo estado en que estaba.
- **Registro del ciclo de vida:** `BaseActivity` escribe en el **Logcat** cada callback (`onCreate`, `onStart`, `onResume`, `onPause`, `onStop`, `onRestart`, `onDestroy`), con el filtro `CicloDeVida`. Así se puede ver en vivo qué pasa con cada Activity al entrar y salir de los juegos.
- **Configuración común:** en `onCreate`, `BaseActivity` configura `enableEdgeToEdge()` con íconos claros en la barra de estado. Cada Activity carga su interfaz con `setContent`.
- **Rotación y cambios de configuración:** Android destruye y recrea la Activity (en el log aparece `onDestroy` y `onCreate (recreada)`), pero el `ViewModel` sobrevive, así que la partida de *¿Mito o Realidad?* no se pierde al girar el celular.
- **Persistencia:** el historial de partidas se guarda en `SharedPreferences`, así que se conserva aunque se cierre la app.
- **Liberación de recursos:** si la intro reproduce un video, el `VideoView` se detiene al salir de la pantalla (`onRelease` → `stopPlayback()`). Si el video falla, la app pasa al menú en lugar de trabarse.
- **Pila de navegación:** la intro se quita de la pila (`popUpTo`), así que "Atrás" desde el menú cierra la app.
- *Mejora pendiente:* el estado del Memotest vive en la pantalla (`remember`), así que se reinicia si se rota el celular. Se podría pasar a un ViewModel, igual que el otro juego.

### Recursos multimedia

- **Intro de concientización animada**, hecha con animaciones de Compose: mensajes que aparecen y se desvanecen (`Animatable`), partículas girando dibujadas en `Canvas` (`rememberInfiniteTransition`) y el logo con efecto rebote (`spring`).
- **Soporte para video:** si se agrega `res/raw/intro.mp4`, la intro lo reproduce con `VideoView`.
- **Transiciones** entre las fases del juego (`Crossfade`) y barra de progreso animada.
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
| SharedPreferences | — | Historial de partidas |
| VideoView (`AndroidView`) | — | Video de la intro |
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

| Integrante | Rol en el equipo | Tareas realizadas |
|---|---|---|
| **Stephanie Velarde** | Diseño y estructura del proyecto | Participó en el desarrollo colaborativo de **¿Mito o Realidad?**: definió la experiencia del juego en el **relato de la partida** (`relato.md`) y diseñó su **Design System** (`design-system.md`: persona usuaria, paleta con contrastes verificados, tipografías y botones, con sus imágenes en `img/`). Creó el repositorio, la estructura inicial del proyecto Android (Gradle, navegación y pantallas base) y el README. Desarrolló el tercer juego, **La Previa** (rama `dev-meli`): la historia ramificada con sus escenas, consecuencias y recursos, la lógica (`Historia`, ViewModel y tests) y sus pantallas. |
| **Gonzalo Giri Petray** | Desarrollo e integración de la app | Participó en el desarrollo colaborativo de **¿Mito o Realidad?**: programó la lógica (`Partida`, ViewModel y tests) y las pantallas de portada, cómo se juega, pregunta, resultado y final. Hizo la **intro multimedia**, el contador de aciertos y errores, el resumen y el historial de partidas. Aplicó el Design System en código (tema, tipografías y componentes) e integró el Memotest al menú de la app. |
| **Enzo Páez** | Desarrollo del Memotest | Participó en el desarrollo colaborativo de **¿Mito o Realidad?**, colaborando en el **prototipo en Figma**. Desarrolló el segundo juego, el **Memotest** (rama `dev-enzo`): cartas, parejas sustancia/dato, lógica de comparación de cartas y contador de intentos. |

El historial de commits del repositorio (ramas `main`, `dev-gonzalo` y `dev-enzo`) muestra el aporte de cada integrante.
