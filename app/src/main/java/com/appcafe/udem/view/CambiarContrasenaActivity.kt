package com.appcafe.udem.view

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.appcafe.udem.databinding.ActivityCambiarContrasenaBinding

class CambiarContrasenaActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCambiarContrasenaBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCambiarContrasenaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.botonRegresar.setOnClickListener {
            finish()
        }

        binding.botonGuardarContrasena.setOnClickListener {
            Toast.makeText(this, "Contraseña actualizada", Toast.LENGTH_SHORT).show()
        }
    }
}
