// Archivo de build raíz: solo declara los plugins, cada módulo los aplica.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.ksp) apply false               // Room genera su código con KSP
    alias(libs.plugins.google.services) apply false   // Firebase (base de datos en la nube)
}
