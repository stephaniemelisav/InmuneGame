package com.example.tpjuego.data

import android.content.Context
import android.util.Log
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

/** Una partida guardada en la base de datos externa. */
data class PartidaRemota(
    val juego: Juego,
    val resultado: Int,
    val fechaMillis: Long
)

/** Lo que muestra "Mi resumen" sobre la base de datos en la nube. */
sealed class EstadoNube {
    /** Falta app/google-services.json: la app guarda solo en el celular. */
    data object NoConfigurada : EstadoNube()
    data object Cargando : EstadoNube()
    data class Lista(val partidas: List<PartidaRemota>) : EstadoNube()
    data class Error(val mensaje: String) : EstadoNube()
}

/**
 * Base de datos externa: Cloud Firestore (Firebase).
 *
 * Cada partida terminada de los tres juegos se guarda como un documento en la colección
 * "partidas", con el usuario, el juego, el resultado y la fecha.
 *
 * - Sin internet, Firestore guarda la partida en el celular y la sube sola cuando vuelve la conexión.
 * - Si Firebase no está configurado (falta google-services.json), no se hace nada:
 *   las estadísticas locales (SharedPreferences) siguen funcionando igual.
 */
object PartidasRemotas {

    const val COLECCION = "partidas"
    private const val TAG = "PartidasRemotas"

    /** true si el proyecto tiene Firebase configurado. */
    fun disponible(context: Context): Boolean =
        FirebaseApp.getApps(context.applicationContext).isNotEmpty()

    fun guardar(context: Context, usuario: String, juego: Juego, resultado: Int) {
        if (!disponible(context)) return
        val partida = hashMapOf(
            "usuario" to usuario,
            "juego" to juego.name,
            "titulo" to juego.titulo,
            "resultado" to resultado,
            "fechaCelular" to System.currentTimeMillis(),
            "fecha" to FieldValue.serverTimestamp()
        )
        FirebaseFirestore.getInstance()
            .collection(COLECCION)
            .add(partida)
            .addOnSuccessListener { Log.d(TAG, "Partida guardada en Firestore: ${it.id}") }
            .addOnFailureListener { Log.w(TAG, "No se pudo guardar la partida", it) }
    }

    /** Lee las últimas partidas del usuario desde Firestore (las más nuevas primero). */
    fun ultimas(context: Context, usuario: String, cantidad: Int = 30, alTerminar: (EstadoNube) -> Unit) {
        if (!disponible(context)) {
            alTerminar(EstadoNube.NoConfigurada)
            return
        }
        FirebaseFirestore.getInstance()
            .collection(COLECCION)
            .whereEqualTo("usuario", usuario)
            .get()
            .addOnSuccessListener { resultado ->
                val partidas = resultado.documents.mapNotNull { doc ->
                    val juego = doc.getString("juego")
                        ?.let { nombre -> Juego.entries.firstOrNull { it.name == nombre } }
                        ?: return@mapNotNull null
                    PartidaRemota(
                        juego = juego,
                        resultado = doc.getLong("resultado")?.toInt() ?: 0,
                        fechaMillis = doc.getLong("fechaCelular") ?: 0L
                    )
                }
                    // Se ordena acá y no en la consulta, así no hace falta crear un índice en Firestore.
                    .sortedByDescending { it.fechaMillis }
                    .take(cantidad)
                alTerminar(EstadoNube.Lista(partidas))
            }
            .addOnFailureListener {
                Log.w(TAG, "No se pudieron leer las partidas", it)
                alTerminar(EstadoNube.Error("No se pudo conectar con la base de datos."))
            }
    }
}
