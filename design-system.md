# Design System — InmuneGame

Grupo 7 · Temática: Sustancias ilegales
Unidad de medida: **px** (pantalla de referencia 360 × 800 px).

---

## 1. Persona usuaria

**Valentina, 16 años**: cursa 4.º año en una escuela secundaria pública del conurbano bonaerense.

- **Edad y contexto.** Vive con su familia y viaja a la escuela en colectivo, unos 25 minutos. Va a previas y cumpleaños los fines de semana, que es donde aparecen las primeras ofertas de consumo entre conocidos. Jugaría al juego **en clase, en un taller guiado por la docente**, y quizás lo retome en el colectivo o en un recreo.
- **Uso del celular.** Lo usa casi siempre **con una sola mano**, porque con la otra sostiene la mochila, se agarra en el colectivo o sostiene la carpeta. Lo usa en el aula, en el patio (a pleno sol) y en el transporte. Sus sesiones son cortas: entre **5 y 10 minutos** seguidos.
- **Preferencias.** TikTok e Instagram (contenido corto y visual), juegos casuales de partidas rápidas como Preguntados y Subway Surfers, y juegos con amigos como Among Us.
- **Necesidades y dificultades.**
  - Le aburren los textos largos y los mensajes "de sermón": si siente que la están retando, cierra la app.
  - Le molestan los botones chicos o pegados arriba de la pantalla, que con una mano no alcanza.
  - No quiere registrarse ni dar datos para jugar.
  - En el patio, con sol, no lee bien los textos claros sobre fondos claros.
  - Necesita saber en todo momento cuánto le falta para terminar.

> **Por qué esta persona:** sale directo de la investigación de la Etapa 1. El público es de 15 a 17 años, el contexto es escolar con docente y, según la encuesta del OAD a estudiantes de nivel medio, el 21 % considera "inocua" la marihuana. Valentina representa a quien todavía no tiene una opinión formada y escucha mitos de sus pares.

---

## 2. Paleta de colores

Cada color se probó con la regla de contraste de las pautas de accesibilidad web (WCAG). El mínimo es **4,5:1 para texto normal** y **3:1 para títulos grandes**.

| Color | Función | RGB | HEX | Dónde se usa |
|---|---|---|---|---|
| Turquesa | Marca, fondo (inicio del degradé) | 10, 128, 138 | #0A808A | Parte superior del fondo general; comillas de la afirmación |
| Azul noche | Fondo (fin del degradé) y texto principal | 27, 42, 74 | #1B2A4A | Parte inferior del fondo; texto sobre tarjetas blancas y sobre botón amarillo |
| Amarillo | Acción principal / destacado | 255, 197, 61 | #FFC53D | Botones primarios (Comencemos, Siguiente, Jugar de nuevo), barra de progreso, etiqueta "JUEGO DE PREGUNTAS" |
| Blanco | Superficie y texto sobre fondos oscuros | 255, 255, 255 | #FFFFFF | Tarjetas, títulos y textos sobre el degradé |
| Verde | Opción "Verdadero" | 27, 127, 74 | #1B7F4A | Botón Verdadero |
| Rojo | Opción "Falso" | 208, 58, 63 | #D03A3F | Botón Falso |
| Verde estado | Feedback "respuesta correcta" | 14, 127, 107 → 11, 61, 58 | #0E7F6B → #0B3D3A | Degradé de fondo de la pantalla de acierto |
| Naranja estado | Feedback "respuesta incorrecta" | 200, 80, 46 → 74, 23, 48 | #C8502E → #4A1730 | Degradé de fondo de la pantalla de error |
| Blanco translúcido | Elementos secundarios | 255, 255, 255 al 20 % | — | Riel de la barra de progreso, etiqueta "ES UN MITO / ES REALIDAD" |

### Contrastes verificados

| Combinación | Contraste | Cumple |
|---|---|---|
| Azul noche sobre tarjeta blanca | 14,2:1 | ✔ texto normal |
| Azul noche sobre amarillo (botón primario) | 9,0:1 | ✔ texto normal |
| Blanco sobre turquesa #0A808A | 4,7:1 | ✔ texto normal |
| Blanco sobre verde #1B7F4A | 5,0:1 | ✔ texto normal |
| Blanco sobre rojo #D03A3F | 4,8:1 | ✔ texto normal |
| Blanco sobre verde estado #0E7F6B | 4,9:1 | ✔ texto normal |
| Blanco sobre naranja estado #C8502E | 4,5:1 | ✔ texto normal |

### Ajuste respecto del prototipo en Figma

Al medir el primer mockup encontramos combinaciones que no llegaban al mínimo, así que oscurecimos esos tonos y mantuvimos la misma identidad:

| Antes | Contraste con blanco | Ahora | Contraste |
|---|---|---|---|
| Verde #2FB36B (botón Verdadero) | 2,7:1 ✘ | #1B7F4A | 5,0:1 ✔ |
| Rojo #E5484D (botón Falso) | 3,9:1 ✘ | #D03A3F | 4,8:1 ✔ |
| Turquesa #0E9AA7 (fondo) | 3,4:1 ✘ en texto chico | #0A808A | 4,7:1 ✔ |
| Verde #12B886 (fondo acierto) | 2,6:1 ✘ | #0E7F6B | 4,9:1 ✔ |
| Naranja #F0643E (fondo error) | 3,2:1 ✘ en texto chico | #C8502E | 4,5:1 ✔ |

**Justificación de la paleta**
- **Fondo oscuro con texto claro:** reduce el brillo en el aula y en el colectivo, y el contraste alto se sigue leyendo al sol en el patio.
- **Amarillo para "lo que hay que tocar":** siempre que Valentina vea amarillo sabe que es el siguiente paso. Esa consistencia evita que tenga que pensar.
- **Verde y rojo nunca van solos:** cada opción y cada resultado tienen además **texto** (Verdadero / Falso), **ícono** (✓ / ✕) y la **etiqueta** "ES UN MITO · FALSO" o "ES REALIDAD · VERDADERO". Así el juego funciona también para personas con daltonismo.
- **Error en naranja-bordó y no en rojo intenso:** equivocarse no se vive como un castigo. El tono es más cálido, y el foco está en la explicación, no en la falla.

---

## 3. Tipografías

| Uso | Tipografía | Peso | Tamaño | Ejemplo |
|---|---|---|---|---|
| Título de la app | **Staatliches** | Regular | 68 px | ¿MITO O REALIDAD? |
| Título de pantalla | Staatliches | Regular | 40 px | ¿VERDADERO O FALSO? / ¡RESPUESTA CORRECTA! |
| Etiquetas y encabezados chicos | Staatliches | Regular | 18 px | JUEGO DE PREGUNTAS / ES UN MITO · FALSO |
| Indicador de progreso | Staatliches | Regular | 16 px | PREGUNTA 1 DE 5 |
| Afirmación a evaluar | **Inter** | Semibold | 22 px | "Como la marihuana es una planta…" |
| Subtítulo | Inter | Semibold | 20 px | ¿Cuánto sabés realmente? |
| Texto de cuerpo / explicaciones | Inter | Regular | 17–18 px | Explicación después de responder |
| Texto de botones | Inter | Bold | 18 px | Verdadero / Falso / Siguiente |

**Justificación**
- **Staatliches** es una tipografía condensada y en mayúsculas, con estilo de póster o videojuego. Le da al juego una identidad que conecta con Valentina sin parecer un material escolar. Solo se usa en textos **cortos y grandes**, donde se lee bien.
- **Inter** fue diseñada para pantallas: tiene letras abiertas y se distingue bien incluso en tamaños chicos y con sol. Se usa en todo texto de más de una línea.
- **Ningún texto baja de 16 px**, y la afirmación, que es lo más importante, va a 22 px. Así se lee sin acercarse el celular, aun en el colectivo.
- El interlineado es amplio (1,35 a 1,5) para que las explicaciones de 3 a 4 líneas no se vean como un bloque pesado.

---

## 4. Botones

| Tipo | Forma | Tamaño | Color de fondo | Texto | Ubicación |
|---|---|---|---|---|---|
| **Primario** | Rectángulo con esquinas redondeadas (radio 16 px) y sombra suave | 312 × 58 px | Amarillo #FFC53D | Azul noche, Inter Bold 18 px ("Comencemos", "Siguiente", "Jugar de nuevo") | Borde inferior de la pantalla, a 32 px del final |
| **Verdadero** | Igual al primario | 312 × 58 px | Verde #1B7F4A | Blanco, Inter Bold 18 px ("Verdadero") | Tercio inferior, arriba del botón Falso |
| **Falso** | Igual al primario | 312 × 58 px | Rojo #D03A3F | Blanco, Inter Bold 18 px ("Falso") | Tercio inferior, debajo de Verdadero, separados 12 px |
| **Secundario** | Mismo radio, sin relleno, borde blanco al 60 % de 2 px | 312 × 62 px | Transparente | Blanco, Inter Bold 18 px ("Volver al inicio", "Volver al menú") | Debajo del primario |
| **Tarjeta de juego** (menú) | Tarjeta blanca, radio 24 px, toda la tarjeta es tocable | 312 × ~120 px | Blanco | Nombre del juego + descripción breve | Lista vertical en el centro y abajo del menú |

**Justificación (ergonomía del pulgar)**
- **Ancho completo (312 px, con márgenes de 24 px):** Valentina usa una sola mano. Un botón que ocupa todo el ancho se alcanza con el pulgar derecho o izquierdo sin estirarse, y por la **Ley de Fitts** un objetivo más grande es más rápido y seguro de tocar.
- **Siempre en el tercio inferior:** es la zona natural del pulgar. Arriba solo va información (progreso, títulos), nunca algo que haya que tocar.
- **58 px de alto:** supera con margen el mínimo de 48 dp recomendado por Android para áreas táctiles. Con el colectivo en movimiento, un dedo impreciso igual acierta.
- **Verdadero y Falso apilados, con 12 px entre sí y de colores distintos:** reduce el riesgo de tocar la opción equivocada sin querer, que en un juego de respuestas sería muy frustrante.
- **Un solo botón primario por pantalla:** en cada momento hay una acción principal clara, sin menús ni opciones escondidas.
- **Sombra en los botones rellenos:** la clase marca que los elementos interactivos se tienen que distinguir como "tocables". La sombra los separa del fondo.

---

## 5. Otros elementos de interfaz

- **Tarjeta:** fondo blanco, radio 24 px, padding 24 px y sombra suave. Contiene la afirmación o la explicación. Es la "zona de lectura" y siempre tiene el mayor contraste de la pantalla.
- **Barra de progreso:** 8 px de alto, riel blanco al 20 % y avance amarillo, con el texto "PREGUNTA X DE 5". Responde a la necesidad de Valentina de saber cuánto falta.
- **Ícono de resultado:** círculo blanco de 96 px con ✓ (acierto) o ✕ (error). Refuerza el resultado sin depender del color.
- **Márgenes:** 24 px laterales en todas las pantallas, y 56 px arriba para no quedar debajo de la barra de estado del celular.
