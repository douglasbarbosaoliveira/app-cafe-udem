package com.appcafe.udem

import androidx.test.espresso.Espresso.closeSoftKeyboard
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.isEnabled
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.appcafe.udem.view.LoginActivity
import org.junit.Rule
import org.junit.Test

// Pruebas instrumentales de la pantalla de Login.
// Se ejecutan en un emulador o dispositivo real.
class RegistrationUtilInstrumentedTest {

    @get:Rule
    val activityRule = ActivityScenarioRule(LoginActivity::class.java)

    // Verifica que el campo de correo esté visible en la pantalla de login
    @Test
    fun loginCampoCorreoVisible() {
        onView(withId(R.id.emailInput))
            .check(matches(isDisplayed()))
    }

    // Verifica que el campo de contraseña esté visible en la pantalla de login
    @Test
    fun loginCampoContrasenaVisible() {
        onView(withId(R.id.passwordInput))
            .check(matches(isDisplayed()))
    }

    // Verifica que el botón de login esté visible y habilitado
    @Test
    fun loginBotonVisibleYHabilitado() {
        onView(withId(R.id.btnLogin))
            .check(matches(isDisplayed()))
            .check(matches(isEnabled()))
    }

    // Escribir en el campo de correo funciona correctamente
    @Test
    fun loginEscribirCorreo() {
        onView(withId(R.id.emailInput))
            .perform(typeText("test@cafe.mx"))
        closeSoftKeyboard()
        onView(withId(R.id.emailInput))
            .check(matches(withText("test@cafe.mx")))
    }

    // Escribir en ambos campos y presionar login funciona sin crash
    @Test
    fun loginEscribirYPresionarBoton() {
        onView(withId(R.id.emailInput))
            .perform(typeText("test@cafe.mx"))
        onView(withId(R.id.passwordInput))
            .perform(typeText("123456"))
        closeSoftKeyboard()
        onView(withId(R.id.btnLogin))
            .perform(click())
    }
}