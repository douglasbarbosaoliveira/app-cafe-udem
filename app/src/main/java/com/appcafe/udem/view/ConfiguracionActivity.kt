package com.appcafe.udem.view

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.appcafe.udem.databinding.ActivityConfiguracionBinding

class ConfiguracionActivity : AppCompatActivity() {

    private lateinit var binding: ActivityConfiguracionBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityConfiguracionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.botonAccesibilidad.setOnClickListener {
            startActivity(Intent(this, AccesibilidadActivity::class.java))
        }

        binding.botonCambiarContrasena.setOnClickListener {
            startActivity(Intent(this, CambiarContrasenaActivity::class.java))
        }

        binding.botonContactanos.setOnClickListener {
            startActivity(Intent(this, ContactanosActivity::class.java))
        }

        binding.botonRegresar.setOnClickListener {
            finish()
        }
    }
}
