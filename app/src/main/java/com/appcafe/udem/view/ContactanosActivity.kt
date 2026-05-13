package com.appcafe.udem.view

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.appcafe.udem.databinding.ActivityContactanosBinding

class ContactanosActivity : AppCompatActivity() {

    private lateinit var binding: ActivityContactanosBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityContactanosBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.botonRegresar.setOnClickListener {
            finish()
        }

        binding.botonEnviarMensaje.setOnClickListener {
            Toast.makeText(this, "Mensaje enviado", Toast.LENGTH_SHORT).show()
        }
    }
}
