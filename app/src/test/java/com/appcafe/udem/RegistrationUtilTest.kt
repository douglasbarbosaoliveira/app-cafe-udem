package com.appcafe.udem

import com.google.common.truth.Truth.assertThat
import org.junit.Test

// Pruebas unitarias de RegistrationUtil.
// Se ejecutan en la JVM local, sin necesidad de emulador o dispositivo.
class RegistrationUtilTest {

    // ── Validación de registro ─────────────────────────────────────────────────

    // Retorna falso si el nombre está vacío
    @Test
    fun emptyNombre() {
        val result = RegistrationUtil.validateRegistrationInput(
            nombre = "",
            correo = "usuario@cafe.mx",
            contrasena = "123456",
            confirmarContrasena = "123456"
        )
        assertThat(result).isFalse()
    }

    // Retorna falso si el correo está vacío
    @Test
    fun emptyCorreo() {
        val result = RegistrationUtil.validateRegistrationInput(
            nombre = "Usuario",
            correo = "",
            contrasena = "123456",
            confirmarContrasena = "123456"
        )
        assertThat(result).isFalse()
    }

    // Retorna falso si la contraseña está vacía
    @Test
    fun emptyContrasena() {
        val result = RegistrationUtil.validateRegistrationInput(
            nombre = "Usuario",
            correo = "usuario@cafe.mx",
            contrasena = "",
            confirmarContrasena = "123456"
        )
        assertThat(result).isFalse()
    }

    // Retorna falso si la confirmación de contraseña está vacía
    @Test
    fun emptyConfirmarContrasena() {
        val result = RegistrationUtil.validateRegistrationInput(
            nombre = "Usuario",
            correo = "usuario@cafe.mx",
            contrasena = "123456",
            confirmarContrasena = ""
        )
        assertThat(result).isFalse()
    }

    // Retorna falso si el correo ya está registrado (ana@cafe.mx)
    @Test
    fun correoExistenteAna() {
        val result = RegistrationUtil.validateRegistrationInput(
            nombre = "Usuario",
            correo = "ana@cafe.mx",
            contrasena = "123456",
            confirmarContrasena = "123456"
        )
        assertThat(result).isFalse()
    }

    // Retorna falso si el correo ya está registrado (carlos@cafe.mx)
    @Test
    fun correoExistenteCarlos() {
        val result = RegistrationUtil.validateRegistrationInput(
            nombre = "Usuario",
            correo = "carlos@cafe.mx",
            contrasena = "123456",
            confirmarContrasena = "123456"
        )
        assertThat(result).isFalse()
    }

    // Retorna falso si la contraseña tiene menos de 6 caracteres
    @Test
    fun contrasenaMenorDeSeis() {
        val result = RegistrationUtil.validateRegistrationInput(
            nombre = "Usuario",
            correo = "usuario@cafe.mx",
            contrasena = "12345",
            confirmarContrasena = "12345"
        )
        assertThat(result).isFalse()
    }

    // Retorna verdadero en el límite exacto de la contraseña (6 caracteres)
    @Test
    fun contrasenaExactamenteSeis() {
        val result = RegistrationUtil.validateRegistrationInput(
            nombre = "Usuario",
            correo = "usuario@cafe.mx",
            contrasena = "123456",
            confirmarContrasena = "123456"
        )
        assertThat(result).isTrue()
    }

    // Retorna falso si las contraseñas no coinciden
    @Test
    fun contrasenasNoCoinciden() {
        val result = RegistrationUtil.validateRegistrationInput(
            nombre = "Usuario",
            correo = "usuario@cafe.mx",
            contrasena = "123456",
            confirmarContrasena = "123446"
        )
        assertThat(result).isFalse()
    }

    // Retorna verdadero con todos los datos completamente válidos
    @Test
    fun datosValidos() {
        val result = RegistrationUtil.validateRegistrationInput(
            nombre = "Nuevo Usuario",
            correo = "nuevo@cafe.mx",
            contrasena = "123456",
            confirmarContrasena = "123456"
        )
        assertThat(result).isTrue()
    }

    // Retorna falso si todos los campos están vacíos
    @Test
    fun todosCamposVacios() {
        val result = RegistrationUtil.validateRegistrationInput(
            nombre = "",
            correo = "",
            contrasena = "",
            confirmarContrasena = ""
        )
        assertThat(result).isFalse()
    }

    // ── Validación de login ────────────────────────────────────────────────────

    // Retorna falso si el correo de login está vacío
    @Test
    fun loginCorreoVacio() {
        val result = RegistrationUtil.validateLoginInput(
            correo = "",
            contrasena = "123456"
        )
        assertThat(result).isFalse()
    }

    // Retorna falso si la contraseña de login está vacía
    @Test
    fun loginContrasenaVacia() {
        val result = RegistrationUtil.validateLoginInput(
            correo = "usuario@cafe.mx",
            contrasena = ""
        )
        assertThat(result).isFalse()
    }

    // Retorna falso si ambos campos de login están vacíos
    @Test
    fun loginAmbosVacios() {
        val result = RegistrationUtil.validateLoginInput(
            correo = "",
            contrasena = ""
        )
        assertThat(result).isFalse()
    }

    // Retorna verdadero con correo y contraseña válidos
    @Test
    fun loginDatosValidos() {
        val result = RegistrationUtil.validateLoginInput(
            correo = "usuario@cafe.mx",
            contrasena = "123456"
        )
        assertThat(result).isTrue()
    }

    // ── Validación de reseña ───────────────────────────────────────────────────

    // Retorna falso si el id de la cafetería está vacío
    @Test
    fun resenaCafeteriaIdVacio() {
        val result = RegistrationUtil.validateResenaInput(
            cafeteriaId = "",
            rating = 4f
        )
        assertThat(result).isFalse()
    }

    // Retorna falso si el rating es 0
    @Test
    fun resenaRatingCero() {
        val result = RegistrationUtil.validateResenaInput(
            cafeteriaId = "cafe_01",
            rating = 0f
        )
        assertThat(result).isFalse()
    }

    // Retorna falso si el rating es negativo
    @Test
    fun resenaRatingNegativo() {
        val result = RegistrationUtil.validateResenaInput(
            cafeteriaId = "cafe_01",
            rating = -1f
        )
        assertThat(result).isFalse()
    }

    // Retorna falso si el rating es mayor a 5
    @Test
    fun resenaRatingMayorACinco() {
        val result = RegistrationUtil.validateResenaInput(
            cafeteriaId = "cafe_01",
            rating = 6f
        )
        assertThat(result).isFalse()
    }

    // Retorna verdadero con rating válido (5 estrellas)
    @Test
    fun resenaRatingMaximoValido() {
        val result = RegistrationUtil.validateResenaInput(
            cafeteriaId = "cafe_01",
            rating = 5f
        )
        assertThat(result).isTrue()
    }

    // Retorna verdadero con rating mínimo válido (1 estrella)
    @Test
    fun resenaRatingMinimoValido() {
        val result = RegistrationUtil.validateResenaInput(
            cafeteriaId = "cafe_01",
            rating = 1f
        )
        assertThat(result).isTrue()
    }
}