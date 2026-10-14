package com.example.tpjuego.data

/**
 * Banco de afirmaciones. Cada partida toma [PREGUNTAS_POR_PARTIDA] al azar,
 * así "Jugar de nuevo" no repite siempre el mismo orden.
 *
 * Criterios (relato.md): no estigmatiza, no moraliza, se basa en fuentes.
 * Para sumar afirmaciones, agregarlas a la lista [todas].
 */
object AfirmacionesRepositorio {

    const val PREGUNTAS_POR_PARTIDA = 5

    private const val SEDRONAR_OAD = "SEDRONAR · Observatorio Argentino de Drogas"
    private const val SEDRONAR = "SEDRONAR"
    private const val SAP = "Sociedad Argentina de Pediatría"

    val todas: List<Afirmacion> = listOf(
        Afirmacion(
            texto = "Como la marihuana es una planta, no puede causar daños importantes en la salud.",
            esRealidad = false,
            explicacion = "Que venga de una planta no la hace inofensiva. Puede afectar la memoria, " +
                "la atención y la coordinación, y generar dependencia. En la adolescencia el " +
                "cerebro todavía se está desarrollando. Según el OAD, 1 de cada 5 estudiantes " +
                "secundarios cree que es inocua.",
            fuente = SEDRONAR_OAD
        ),
        Afirmacion(
            texto = "Probar una sola vez siempre es seguro.",
            esRealidad = false,
            explicacion = "Una sola vez también tiene riesgos: no se sabe cómo va a reaccionar el " +
                "cuerpo ni qué contiene exactamente la sustancia. Puede haber intoxicaciones o " +
                "accidentes desde la primera vez.",
            fuente = SEDRONAR
        ),
        Afirmacion(
            texto = "La presión del grupo puede influir en la decisión de consumir.",
            esRealidad = true,
            explicacion = "Lo que dicen y hacen las amistades influye, sobre todo en la " +
                "adolescencia. Tener información y animarse a decidir por cuenta propia ayuda " +
                "a enfrentar esas situaciones.",
            fuente = SAP
        ),
        Afirmacion(
            texto = "Mezclar alcohol con otras sustancias aumenta los riesgos.",
            esRealidad = true,
            explicacion = "Al combinarse, los efectos se suman de formas difíciles de prever. " +
                "Eso aumenta el riesgo de intoxicación y de accidentes.",
            fuente = SEDRONAR
        ),
        Afirmacion(
            texto = "Si alguien se siente mal después de consumir, lo mejor es dejarlo dormir solo para que se le pase.",
            esRealidad = false,
            explicacion = "No hay que dejar sola a esa persona. Lo mejor es quedarse con ella, " +
                "acostarla de costado si está muy dormida y pedir ayuda a un adulto o llamar " +
                "al 107 (emergencias).",
            fuente = SEDRONAR
        ),
        Afirmacion(
            texto = "Las pastillas que circulan en fiestas siempre tienen lo que dicen tener.",
            esRealidad = false,
            explicacion = "A simple vista no hay forma de saber qué contiene una pastilla o un " +
                "polvo. Pueden tener otras sustancias o cantidades distintas a las que se " +
                "dicen, y eso aumenta los riesgos.",
            fuente = SEDRONAR
        ),
        Afirmacion(
            texto = "Si alguien tiene un problema de consumo, puede pedir ayuda gratis.",
            esRealidad = true,
            explicacion = "Existen espacios gratuitos y confidenciales. Por ejemplo, la Línea 141 " +
                "de SEDRONAR atiende las 24 horas, es gratuita y anónima.",
            fuente = SEDRONAR
        )
    )

    fun paraPartida(cantidad: Int = PREGUNTAS_POR_PARTIDA): List<Afirmacion> =
        todas.shuffled().take(cantidad)
}
