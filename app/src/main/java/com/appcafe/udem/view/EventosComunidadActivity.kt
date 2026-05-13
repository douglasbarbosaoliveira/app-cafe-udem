package com.appcafe.udem.view

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.appcafe.udem.adapter.EventosComunidadAdapter
import com.appcafe.udem.databinding.ActivityEventosComunidadBinding
import com.appcafe.udem.viewmodel.EventosComunidadViewModel

class EventosComunidadActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEventosComunidadBinding
    private val eventosViewModel: EventosComunidadViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEventosComunidadBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.botonRegresar.setOnClickListener {
            finish()
        }

        eventosViewModel.listaEventos.observe(this) { listaEventos ->
            binding.recyclerEventos.layoutManager = LinearLayoutManager(this)
            binding.recyclerEventos.adapter = EventosComunidadAdapter().also { it.submitList(listaEventos) }
        }
    }
}
