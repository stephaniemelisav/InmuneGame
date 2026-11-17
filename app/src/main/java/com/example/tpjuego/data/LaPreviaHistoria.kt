package com.example.tpjuego.data

/** Un dato útil que la persona se lleva al final, con su fuente. */
data class Recurso(
    val texto: String,
    val fuente: String
)

/**
 * Una de las respuestas posibles en una escena.
 *
 * @param texto lo que se lee en el botón.
 * @param resumen cómo aparece en "Tu recorrido" al final.
 * @param titulo título de la pantalla de consecuencia.
 * @param consecuencia qué pasa después de elegirla.
 * @param recurso dato útil que suma al final (puede no tener).
 * @param siguiente id de la próxima escena, o null si la historia termina.
 */
data class Opcion(
    val texto: String,
    val resumen: String,
    val titulo: String,
    val consecuencia: String,
    val recurso: Recurso?,
    val siguiente: String?
)

/**
 * Una escena de la historia.
 *
 * @param numero en qué momento de la noche está (1 a [LaPreviaHistoria.TOTAL_ESCENAS]).
 *  Todos los caminos pasan por la misma cantidad de escenas, así la barra de progreso
 *  avanza igual sin importar lo que se elija.
 */
data class Escena(
    val id: String,
    val numero: Int,
    val titulo: String,
    val situacion: String,
    val opciones: List<Opcion>
)

/**
 * La Previa: una noche contada en escenas. Cada decisión lleva a una consecuencia
 * distinta y, en algunos momentos, a escenas distintas. Los caminos se vuelven a
 * juntar (escena 4 y escena 6), así la historia no se multiplica sin control.
 *
 * Mapa:
 *   llegada → oferta ─┬─ (no / excusa) → insisten ──┐
 *                     └─ (pruebo) ────→ malestar ───┴→ amigo ─┬─ (lo cuida / llama) → ayuda ─┐
 *                                                             └─ (lo deja dormir) ──→ peor ──┴→ vuelta → fin
 *
 * Criterios (relato.md): no estigmatiza, no moraliza, no hay "final malo",
 * y todos los caminos terminan con la información correcta.
 */
object LaPreviaHistoria {

    const val INICIO = "llegada"
    const val TOTAL_ESCENAS = 6

    // Fuentes verificadas: los enlaces están en DOCUMENTACION.md (Fuentes de La Previa).
    private const val SEDRONAR = "SEDRONAR"
    private const val AAP = "Academia Americana de Pediatría (AAP)"
    private const val SAMHSA = "SAMHSA, agencia de salud de EE. UU."
    private const val MSAL = "Ministerio de Salud de la Nación (primeros auxilios)"
    private const val EMERGENCIAS = "SEDRONAR y Gobierno de la Ciudad de Buenos Aires"
    private const val ANSV = "Agencia Nacional de Seguridad Vial"

    /** Se suma siempre al final, elija lo que elija la persona. */
    val recursoFinal = Recurso(
        texto = "Línea 141 de SEDRONAR: gratuita, anónima y atiende las 24 horas.",
        fuente = SEDRONAR
    )

    val escenas: Map<String, Escena> = listOf(
        Escena(
            id = "llegada",
            numero = 1,
            titulo = "LLEGÁS A LA PREVIA",
            situacion = "Sábado a la noche. Llegás a lo de Cami: música fuerte, gente que no " +
                "conocés y una ronda en el patio.",
            opciones = listOf(
                Opcion(
                    texto = "Me quedo cerca de Cami.",
                    resumen = "Te quedaste cerca de Cami.",
                    titulo = "ARRANCÁS CON TU GENTE",
                    consecuencia = "Cami te presenta a sus amigos. Tener a alguien de confianza " +
                        "cerca hace más fácil cualquier situación de la noche.",
                    recurso = null,
                    siguiente = "oferta"
                ),
                Opcion(
                    texto = "Me sumo a la ronda del patio.",
                    resumen = "Te sumaste a la ronda del patio.",
                    titulo = "CONOCÉS GENTE NUEVA",
                    consecuencia = "La ronda es buena onda y te reís un montón. Igual, conviene " +
                        "saber dónde está tu gente durante la noche.",
                    recurso = null,
                    siguiente = "oferta"
                )
            )
        ),
        Escena(
            id = "oferta",
            numero = 2,
            titulo = "TE OFRECEN ALGO",
            situacion = "En la ronda, alguien que recién conocés saca unas pastillas: " +
                "«Probá, es re suave. Con esto la pasás mejor».",
            opciones = listOf(
                Opcion(
                    texto = "«No, gracias. Estoy bien así.»",
                    resumen = "Dijiste que no.",
                    titulo = "DIJISTE QUE NO",
                    consecuencia = "Lo dijiste sin vueltas. Un «no» corto y tranquilo suele " +
                        "alcanzar, aunque no siempre termina ahí.",
                    recurso = Recurso("Un «no, gracias» firme y amable alcanza: no hace falta dar explicaciones.", AAP),
                    siguiente = "insisten"
                ),
                Opcion(
                    texto = "Pongo una excusa: «Mañana tengo partido».",
                    resumen = "Pusiste una excusa.",
                    titulo = "SALISTE DEL MOMENTO",
                    consecuencia = "«Uh, qué bajón», dicen, y la ronda sigue. Una excusa " +
                        "preparada te da tiempo para salir de una situación incómoda.",
                    recurso = Recurso("Tener una excusa lista ayuda a salir del momento: «Mañana tengo partido», «Hoy no».", AAP),
                    siguiente = "insisten"
                ),
                Opcion(
                    texto = "«Dale, pruebo.»",
                    resumen = "Probaste la pastilla.",
                    titulo = "PROBASTE",
                    consecuencia = "Nadie en la ronda sabe qué tiene esa pastilla: a simple vista " +
                        "no se puede saber qué sustancia es ni cuánta cantidad trae.",
                    recurso = Recurso("No se puede saber a simple vista qué contiene una pastilla: muchas veces no es lo que dicen.", SEDRONAR),
                    siguiente = "malestar"
                )
            )
        ),
        Escena(
            id = "insisten",
            numero = 3,
            titulo = "INSISTEN",
            situacion = "«Dale, no cortes la onda, una vez no pasa nada». Un par se ríe y " +
                "todos te miran.",
            opciones = listOf(
                Opcion(
                    texto = "Repito que no y cambio de tema.",
                    resumen = "Repetiste que no y cambiaste de tema.",
                    titulo = "SOSTUVISTE TU DECISIÓN",
                    consecuencia = "Alguno te carga un rato, pero la charla sigue por otro lado. " +
                        "Repetir el «no», sin enojarte, vale las veces que haga falta.",
                    recurso = Recurso("Podés repetir el «no» las veces que haga falta, o cambiar de tema.", AAP),
                    siguiente = "amigo"
                ),
                Opcion(
                    texto = "Me alejo y voy a buscar a Cami.",
                    resumen = "Te alejaste del grupo que insistía.",
                    titulo = "TE ALEJASTE",
                    consecuencia = "Cami te hace un lugar en la cocina. Salir del lugar donde te " +
                        "presionan también es una forma de responder.",
                    recurso = Recurso("Alejarte de la situación también es una forma de decir que no.", SAMHSA),
                    siguiente = "amigo"
                )
            )
        ),
        Escena(
            id = "malestar",
            numero = 3,
            titulo = "ALGO NO ANDA BIEN",
            situacion = "Media hora después, el corazón te late muy rápido y te mareás. " +
                "No sabés qué tenía esa pastilla.",
            opciones = listOf(
                Opcion(
                    texto = "Le cuento a Cami cómo me siento.",
                    resumen = "Le contaste a Cami cómo te sentías.",
                    titulo = "PEDISTE AYUDA",
                    consecuencia = "Cami te lleva afuera, te da agua y se queda con vos hasta que " +
                        "te sentís mejor. Pedir ayuda a tiempo es lo más importante.",
                    recurso = Recurso("Si te sentís mal, no te quedes sin compañía: avisale a alguien y buscá a un adulto responsable.", SEDRONAR),
                    siguiente = "amigo"
                ),
                Opcion(
                    texto = "Me encierro en el baño hasta que se pase.",
                    resumen = "Te encerraste en el baño.",
                    titulo = "TE AISLASTE",
                    consecuencia = "Estar sin nadie cuando te sentís mal es riesgoso: si empeorás, " +
                        "nadie se entera. Por suerte, Cami te busca y te acompaña afuera.",
                    recurso = Recurso("Si te sentís mal, no te quedes sin compañía: avisale a alguien y buscá a un adulto responsable.", SEDRONAR),
                    siguiente = "amigo"
                )
            )
        ),
        Escena(
            id = "amigo",
            numero = 4,
            titulo = "NICO NO RESPONDE BIEN",
            situacion = "Ya es tarde. Encontrás a Nico tirado en el sillón, muy dormido. " +
                "Le hablás y apenas abre los ojos.",
            opciones = listOf(
                Opcion(
                    texto = "Lo pongo de costado y aviso a un adulto.",
                    resumen = "Pusiste a Nico de costado y avisaste a un adulto.",
                    titulo = "LO CUIDASTE",
                    consecuencia = "De costado, si vomita no se ahoga. La mamá de Cami viene " +
                        "enseguida.",
                    recurso = Recurso("Acostar de costado a alguien que no responde bien evita que se ahogue si vomita.", MSAL),
                    siguiente = "ayuda"
                ),
                Opcion(
                    texto = "Lo dejo dormir, ya se le va a pasar.",
                    resumen = "Dejaste dormir a Nico.",
                    titulo = "LO DEJASTE DORMIR",
                    consecuencia = "Parece lo más tranquilo, pero dormido boca arriba y sin nadie " +
                        "al lado, si empeora o vomita nadie lo va a notar.",
                    recurso = Recurso("A una persona intoxicada no hay que dejarla sola: hay que buscar a un adulto responsable.", SEDRONAR),
                    siguiente = "peor"
                ),
                Opcion(
                    texto = "Llamo a emergencias (107 o 911).",
                    resumen = "Llamaste a emergencias.",
                    titulo = "LLAMASTE A EMERGENCIAS",
                    consecuencia = "Del otro lado te explican qué hacer mientras llega la " +
                        "ambulancia: ponerlo de costado y quedarte con él.",
                    recurso = Recurso("Ante signos de intoxicación, llamar a emergencias: 107 (ambulancias del SAME en CABA) o 911.", EMERGENCIAS),
                    siguiente = "ayuda"
                )
            )
        ),
        Escena(
            id = "ayuda",
            numero = 5,
            titulo = "LLEGA AYUDA",
            situacion = "Llegan la mamá de Cami y la ambulancia. Revisan a Nico, que de a " +
                "poco empieza a responder.",
            opciones = listOf(
                Opcion(
                    texto = "Me quedo con Nico hasta que lo atienden.",
                    resumen = "Acompañaste a Nico hasta que lo atendieron.",
                    titulo = "ACOMPAÑASTE",
                    consecuencia = "Nico va a estar bien. Lo que hiciste a tiempo hizo la diferencia.",
                    recurso = null,
                    siguiente = "vuelta"
                )
            )
        ),
        Escena(
            id = "peor",
            numero = 5,
            titulo = "NICO ESTÁ PEOR",
            situacion = "Veinte minutos después, Nico respira raro y no se despierta cuando " +
                "lo movés.",
            opciones = listOf(
                Opcion(
                    texto = "Lo pongo de costado y pido ayuda ya.",
                    resumen = "Pusiste a Nico de costado y pediste ayuda.",
                    titulo = "ACTUASTE",
                    consecuencia = "Llega la ambulancia y Nico empieza a responder. Ante alguien " +
                        "que no se despierta no hay que esperar: primero, pedir ayuda.",
                    recurso = Recurso("Si alguien no se despierta: de costado, no dejarlo solo y llamar a emergencias (107 o 911).", EMERGENCIAS),
                    siguiente = "vuelta"
                )
            )
        ),
        Escena(
            id = "vuelta",
            numero = 6,
            titulo = "¿CÓMO VOLVÉS?",
            situacion = "Son las 3 de la mañana y la previa termina. Hay que volver a casa.",
            opciones = listOf(
                Opcion(
                    texto = "Con Cami, en el remis que pidieron sus viejos.",
                    resumen = "Volviste con Cami en remis.",
                    titulo = "VOLVISTE CON CAMI",
                    consecuencia = "Llegás a tu casa sin problemas. Volver con alguien de " +
                        "confianza y en un transporte seguro también es cuidarse.",
                    recurso = null,
                    siguiente = null
                ),
                Opcion(
                    texto = "Me subo al auto del primo de Tomi, que tomó toda la noche.",
                    resumen = "Subiste al auto de alguien que había tomado.",
                    titulo = "SUBISTE AL AUTO",
                    consecuencia = "Llegás, pero manejar después de tomar alcohol aumenta mucho " +
                        "el riesgo de choques. Siempre hay otra forma de volver, aunque tarde más.",
                    recurso = Recurso("Manejar después de tomar alcohol, aunque sea poco, aumenta mucho el riesgo de siniestros, también para quien viaja.", ANSV),
                    siguiente = null
                ),
                Opcion(
                    texto = "Aviso en casa y me vienen a buscar.",
                    resumen = "Avisaste en casa y te vinieron a buscar.",
                    titulo = "TE VINIERON A BUSCAR",
                    consecuencia = "Nadie te reta: les alegra que hayas avisado. Pedir que te " +
                        "busquen siempre es una opción.",
                    recurso = null,
                    siguiente = null
                )
            )
        )
    ).associateBy { it.id }
}
