# InmuneGame

**Grupo:** 7
**Materia:** Desarrollo de Aplicaciones para Dispositivos Móviles
**Temática:** Sustancias ilegales

## Integrantes y roles

| Integrante | Rol |
|---|---|
| Stephanie Velarde | Diseño y estructura del proyecto |
| Gonzalo Giri Petray | Desarrollo e integración de la app |
| Enzo Páez | Desarrollo del Memotest |

El juego **¿Mito o Realidad?** se desarrolló de manera colaborativa entre los tres integrantes. El detalle del aporte de cada uno está en [DOCUMENTACION.md](DOCUMENTACION.md#e-participación-de-los-integrantes).

## Descripción del juego

**InmuneGame** es una app de juegos educativos para Android pensada para adolescentes de 15 a 17 años, para usar en clase con la guía de una docente. Su primer juego es **¿Mito o Realidad?**: en cada partida, la persona lee **5 afirmaciones** que circulan entre adolescentes sobre el consumo de sustancias (por ejemplo, *"como la marihuana es una planta, no puede causar daños"*) y decide si son **realidad (Verdadero)** o un **mito (Falso)**.

Después de cada respuesta, acierte o no, aparece una explicación breve basada en fuentes confiables (SEDRONAR / Observatorio Argentino de Drogas, Sociedad Argentina de Pediatría). Al final ve su puntaje y un mensaje que invita a informarse en fuentes confiables. El juego no busca retar ni moralizar, sino entrenar el hábito de **dudar antes de creer** lo que se dice entre pares.

La app abre con un **menú de juegos** con tres juegos: **¿Mito o Realidad?**, el **Memotest** y **La Previa**, una historia de **decisiones bajo presión social** (narrativa ramificada), el formato propuesto en la investigación.

## Documentación

- **Documentación del desarrollo** (descripción, estructura, Design System, implementación técnica y participación): [DOCUMENTACION.md](DOCUMENTACION.md)

## Diseño

- **Figma (prototipo y Design System):** [Rediseño v2](https://www.figma.com/design/UzpNnKOHeR5DY6m8oeSEID/Mito-o-Realidad---Redise%25C3%25B1o-v2?node-id=2-77)
- **Design System documentado:** [design-system.md](design-system.md)
- **Relato de una partida:** [relato.md](relato.md)

### Imágenes

| Contenido | Archivo |
|---|---|
| Persona usuaria | [img/persona.png](img/persona.png) |
| Design System completo | [img/design-system.png](img/design-system.png) |
| Colores y códigos RGB | [img/colores.png](img/colores.png) |
| Tipografías | [img/tipografias.png](img/tipografias.png) |
| Botones | [img/botones.png](img/botones.png) |

## Tecnología

- Android Studio · Kotlin · Jetpack Compose (Material 3) · Navigation Compose · ViewModel
- Tipografías Staatliches e Inter incluidas en `app/src/main/res/font/` (licencia OFL, ver [`licencias/`](licencias/))

### Estructura del código (`app/src/main/java/com/example/tpjuego/`)

| Carpeta | Qué tiene |
|---|---|
| `data/` | `Afirmacion`, el banco de afirmaciones (`AfirmacionesRepositorio`) y el historial de partidas guardado en el celular |
| `game/` | `Partida`: la lógica del juego en Kotlin puro (responder, avanzar, puntaje) y su `ViewModel` |
| `ui/theme/` | Colores, tipografías y tema del Design System |
| `ui/components/` | Botones primario, secundario y de opción, tarjeta, barra de progreso, etiqueta y tarjeta de juego |
| `ui/screens/` | Intro → Menú de juegos → Portada → Cómo se juega → Partida (pregunta, resultado y pantalla final) |

Para sumar afirmaciones alcanza con agregarlas a la lista de `AfirmacionesRepositorio`: cada partida elige 5 al azar.
La lógica tiene tests unitarios en `app/src/test/` (se corren con `./gradlew test`).

### Flujo de la app

1. **Intro multimedia de concientización** al abrir la app (se puede saltear y volver a ver desde el menú).
2. **Menú de juegos**: ¿Mito o Realidad?, **Memotest** (juego de Enzo: unir cada sustancia con su dato, con contador de intentos y resumen final) y **La Previa** (juego de Stephanie: modo historia en 6 escenas donde cada decisión lleva a su camino, con recorrido y recursos al final).
3. **¿Mito o Realidad?**: portada, cómo se juega y 5 preguntas, con contador de aciertos y errores arriba.
4. **Pantalla final**: puntaje, contador de aciertos y errores, resumen pregunta por pregunta e historial de las últimas partidas. **"Terminar y volver al menú"** cierra el juego y vuelve a la app.

### Video de la intro

Si se guarda un video en `app/src/main/res/raw/intro.mp4`, la intro lo reproduce automáticamente. Si no hay video, se muestra una animación de concientización hecha en Compose. El nombre del archivo tiene que ser exactamente `intro.mp4` (minúsculas, sin espacios). Usar un video con permiso de uso y que pese poco (idealmente menos de 10 MB).
