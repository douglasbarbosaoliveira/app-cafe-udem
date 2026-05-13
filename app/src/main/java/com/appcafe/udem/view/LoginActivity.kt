package com.appcafe.udem.view

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.appcafe.udem.CafeApplication
import com.appcafe.udem.databinding.ActivityLoginBinding
import com.appcafe.udem.viewmodel.AuthViewModel

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    private val viewModel: AuthViewModel by viewModels {
        AuthViewModel.Factory((application as CafeApplication).userRepository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnLogin.setOnClickListener {
            val correo = binding.emailInput.text.toString().trim()
            val contrasena = binding.passwordInput.text.toString().trim()
            viewModel.login(correo, contrasena)
        }

        binding.btnContrasena.setOnClickListener {
            startActivity(Intent(this, OlvidoContrasenaActivity::class.java))
        }

        binding.loginText.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
            finish()
        }

        viewModel.usuarioActual.observe(this) { usuario ->
            if (usuario != null) {
                val prefs = getSharedPreferences("cafe_prefs", MODE_PRIVATE)
                prefs.edit().putInt("usuario_id", usuario.id).apply()
                startActivity(Intent(this, HomeActivity::class.java))
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
