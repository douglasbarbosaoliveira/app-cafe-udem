package com.appcafe.udem.view

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.appcafe.udem.databinding.ActivityAccesibilidadBinding

// Activity de accesibilidad. Permite al usuario controlar:
// - Modo oscuro
// - Alto contraste (modo oscuro con colores vibrantes)
// - Texto grande
// - Reducir animaciones
class AccesibilidadActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAccesibilidadBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAccesibilidadBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val prefs = getSharedPreferences("cafe_prefs", MODE_PRIVATE)

        // Restaurar el estado actual de cada switch según las preferencias guardadas
        binding.switchDarkMode.isChecked = prefs.getBoolean("dark_mode", false)
        binding.switchAltoContraste.isChecked = prefs.getBoolean("alto_contraste", false)
        binding.switchTextoGrande.isChecked = prefs.getBoolean("texto_grande", false)
        binding.switchReducirAnimaciones.isChecked = prefs.getBoolean("reducir_animaciones", false)

        // Modo Oscuro: activa el tema oscuro estándar
        binding.switchDarkMode.setOnCheckedChangeListener { _, isChecked ->
            // Si se activa Dark Mode, desactivar Alto Contraste para evitar conflicto
            if (isChecked) {
                binding.switchAltoContraste.isChecked = false
                prefs.edit().putBoolean("alto_contraste", false).apply()
            }
            prefs.edit().putBoolean("dark_mode", isChecked).apply()
            AppCompatDelegate.setDefaultNightMode(
                if (isChecked) AppCompatDelegate.MODE_NIGHT_YES
                else AppCompatDelegate.MODE_NIGHT_NO
            )
            reiniciarApp()
        }

        // Alto contraste: activa modo oscuro con indicador especial para colores vibrantes
        binding.switchAltoContraste.setOnCheckedChangeListener { _, isChecked ->
            // Si se activa Alto Contraste, desactivar Dark Mode simple para evitar conflicto
            if (isChecked) {
                binding.switchDarkMode.isChecked = false
                prefs.edit().putBoolean("dark_mode", false).apply()
            }
            prefs.edit().putBoolean("alto_contraste", isChecked).apply()
            AppCompatDelegate.setDefaultNightMode(
                if (isChecked) AppCompatDelegate.MODE_NIGHT_YES
                else AppCompatDelegate.MODE_NIGHT_NO
            )
            reiniciarApp()
        }

        // Texto grande: cambia la escala de fuente en toda la app
        binding.switchTextoGrande.setOnCheckedChangeListener { _, isChecked ->
            prefs.edit().putBoolean("texto_grande", isChecked).apply()
            // Reiniciar app para que el nuevo fontScale se aplique en todas las pantallas
            reiniciarApp()
        }

        // Reducir animaciones: desactiva las animaciones de transición entre pantallas
        binding.switchReducirAnimaciones.setOnCheckedChangeListener { _, isChecked ->
            prefs.edit().putBoolean("reducir_animaciones", isChecked).apply()
            if (isChecked) {
                // Eliminar animación de la ventana actual
                window.setWindowAnimations(0)
            }
        }

        // Regresar a la pantalla anterior
        binding.botonRegresar.setOnClickListener {
            finish()
        }
    }

    // Reinicia la app desde el inicio para aplicar cambios globales (tema, fuente)
    private fun reiniciarApp() {
        val intent = packageManager.getLaunchIntentForPackage(packageName)
        intent?.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(intent)
        // Deshabilitar animación de transición para que el reinicio sea inmediato
        overridePendingTransition(0, 0)
    }
}