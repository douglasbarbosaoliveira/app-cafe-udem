package com.appcafe.udem.view

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.appcafe.udem.databinding.ActivityResenaBinding

class OlvidoContrasenaActivity : AppCompatActivity() {

    private lateinit var binding: ActivityResenaBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResenaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnEnviar.setOnClickListener {
            val email = binding.emailInput.text.toString().trim()
            if (email.isBlank()) {
                Toast.makeText(this, "Ingresa tu correo", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            Toast.makeText(this, "Instrucciones enviadas a $email", Toast.LENGTH_LONG).show()
            finish()
        }
    }
}
