package com.appcafe.udem.view

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.appcafe.udem.CafeApplication
import com.appcafe.udem.databinding.ActivityMainBinding
import com.appcafe.udem.viewmodel.AuthViewModel

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val viewModel: AuthViewModel by viewModels {
        AuthViewModel.Factory((application as CafeApplication).userRepository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnRegister.setOnClickListener {
            val correo = binding.emailInput.text.toString().trim()
            val nombre = binding.passwordInput.text.toString().trim()
            val contrasena = binding.contrasenaInput.text.toString().trim()
            viewModel.registrar(nombre, correo, contrasena)
        }

        binding.loginText.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        viewModel.registroExitoso.observe(this) { exito ->
            if (exito == true) {
                Toast.makeText(this, "Cuenta creada exitosamente", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }
        }

        viewModel.error.observe(this) { mensaje ->
            if (!mensaje.isNullOrEmpty()) {
                Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show()
            }
        }
    }
}
