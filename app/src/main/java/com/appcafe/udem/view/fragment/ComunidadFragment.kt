package com.appcafe.udem.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.appcafe.udem.adapter.EventosComunidadAdapter
import com.appcafe.udem.databinding.FragmentComunidadBinding
import com.appcafe.udem.viewmodel.ComunidadViewModel

class ComunidadFragment : Fragment() {

    private var _binding: FragmentComunidadBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ComunidadViewModel by viewModels()

    private val adapter = EventosComunidadAdapter(
        onToggleUnirse = { index -> viewModel.toggleUnirse(index) }
    )

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentComunidadBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.recyclerEventos.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerEventos.adapter = adapter

        viewModel.eventos.observe(viewLifecycleOwner) { lista ->
            adapter.submitList(lista)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
