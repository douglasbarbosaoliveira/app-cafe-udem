package com.appcafe.udem

// Utilidad de validación de la app Café.
// Contiene lógica pura de validación separada del ViewModel,
// lo que permite probarla fácilmente con tests unitarios en la JVM.
object RegistrationUtil {

    // Correos que ya existen en el sistema (simulados para pruebas)
    private val correosExistentes = listOf("ana@cafe.mx", "carlos@cafe.mx")

    /**
     * Valida los campos de registro de un nuevo usuario.
     * Retorna true solo si todos los campos son válidos.
     *
     * Condiciones que retornan false:
     * - Nombre, correo o contraseña vacíos
     * - El correo ya está registrado
     * - La contraseña tiene menos de 6 caracteres
     * - La contraseña y su confirmación no coinciden
     */
    fun validateRegistrationInput(
        nombre: String,
        correo: String,
        contrasena: String,
        confirmarContrasena: String
    ): Boolean {
        // Verificar que ningún campo esté vacío
        if (nombre.isBlank() || correo.isBlank() ||
            contrasena.isBlank() || confirmarContrasena.isBlank()) return false
        // Verificar que el correo no esté ya registrado
        if (correo in correosExistentes) return false
        // Verificar longitud mínima de contraseña
        if (contrasena.length < 6) return false
        // Verificar que las contraseñas coincidan
        if (contrasena != confirmarContrasena) return false
        return true
    }

    /**
     * Valida los campos de inicio de sesión.
     * Retorna true solo si correo y contraseña no están vacíos.
     */
    fun validateLoginInput(correo: String, contrasena: String): Boolean {
        if (correo.isBlank() || contrasena.isBlank()) return false
        return true
    }

    /**
     * Valida los datos de una reseña antes de guardarla.
     * Retorna true solo si el rating es válido (entre 1 y 5).
     *
     * Condiciones que retornan false:
     * - Rating menor o igual a 0
     * - Rating mayor a 5
     * - CafeteriaId vacío
     */
    fun validateResenaInput(cafeteriaId: String, rating: Float): Boolean {
        if (cafeteriaId.isBlank()) return false
        if (rating <= 0f || rating > 5f) return false
        return true
    }
}