package com.appcafe.udem.view.fragment

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.appcafe.udem.CafeApplication
import com.appcafe.udem.adapter.CafeteriaAdapter
import com.appcafe.udem.adapter.UsuarioAdapter
import com.appcafe.udem.data.local.entities.Cafeteria
import com.appcafe.udem.data.local.entities.Usuario
import com.appcafe.udem.databinding.FragmentSearchBinding
import com.appcafe.udem.view.DetailCafeteriaActivity
import com.appcafe.udem.view.UserProfileActivity
import com.appcafe.udem.viewmodel.CoffeeViewModel
import com.appcafe.udem.viewmodel.UsuariosViewModel

class SearchFragment : Fragment() {

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    private val coffeeViewModel: CoffeeViewModel by viewModels {
        CoffeeViewModel.Factory((requireActivity().application as CafeApplication).coffeeRepository)
    }
    private val usuariosViewModel: UsuariosViewModel by viewModels {
        UsuariosViewModel.Factory((requireActivity().application as CafeApplication).userRepository)
    }

    private var searchMode = MODE_CAFETERIAS
    private var allCafeterias: List<Cafeteria> = emptyList()
    private var allUsuarios: List<Usuario> = emptyList()
    private var usuarioActualId: Int = -1

    private val cafeteriaAdapter = CafeteriaAdapter { cafeteria ->
        val intent = Intent(requireContext(), DetailCafeteriaActivity::class.java)
        intent.putExtra(DetailCafeteriaActivity.EXTRA_CAFETERIA_ID, cafeteria.id)
        startActivity(intent)
    }

    private lateinit var usuarioAdapter: UsuarioAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val prefs = requireActivity().getSharedPreferences("cafe_prefs", android.content.Context.MODE_PRIVATE)
        usuarioActualId = prefs.getInt("usuario_id", -1)

        val amigosGuardados = prefs.getStringSet("amigos_$usuarioActualId", emptySet())!!
            .mapNotNull { it.toIntOrNull() }.toSet()

        // Inicializar el adapter con los tres parámetros requeridos
        usuarioAdapter = UsuarioAdapter(
            amigosIds = amigosGuardados,
            onToggleAmigo = { usuario, esAmigo ->
                // Guardar o quitar amigo en SharedPreferences
                val amigos = prefs.getStringSet(
                    "amigos_$usuarioActualId", mutableSetOf()
                )!!.toMutableSet()
                if (esAmigo) amigos.add(usuario.id.toString())
                else amigos.remove(usuario.id.toString())
                prefs.edit().putStringSet("amigos_$usuarioActualId", amigos).apply()
            },
            onVerPerfil = { usuario ->
                // Abrir perfil público del usuario seleccionado
                val intent = Intent(requireContext(), UserProfileActivity::class.java)
                intent.putExtra(UserProfileActivity.EXTRA_USUARIO_ID, usuario.id)
                startActivity(intent)
            }
        )

        binding.recyclerSearch.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerSearch.adapter = cafeteriaAdapter

        setupTabs()

        coffeeViewModel.cafeterias.observe(viewLifecycleOwner) { lista ->
            allCafeterias = lista
            if (searchMode == MODE_CAFETERIAS) filterCafeterias(binding.inputSearch.text.toString())
        }

        usuariosViewModel.usuarios.observe(viewLifecycleOwner) { lista ->
            allUsuarios = lista.filter { it.id != usuarioActualId }
            if (searchMode == MODE_USUARIOS) filterUsuarios(binding.inputSearch.text.toString())
        }

        binding.inputSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (searchMode == MODE_CAFETERIAS) filterCafeterias(s.toString())
                else filterUsuarios(s.toString())
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        binding.inputSearch.setOnEditorActionListener { v, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                if (searchMode == MODE_CAFETERIAS) filterCafeterias(v.text.toString())
                else filterUsuarios(v.text.toString())
                val imm = requireContext().getSystemService(android.content.Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(v.windowToken, 0)
                true
            } else false
        }
    }

    private fun setupTabs() {
        binding.tabCafeterias.setOnClickListener {
            searchMode = MODE_CAFETERIAS
            binding.tabCafeterias.setBackgroundColor(requireContext().getColor(com.appcafe.udem.R.color.Red))
            binding.tabCafeterias.setTextColor(requireContext().getColor(com.appcafe.udem.R.color.white))
            binding.tabUsuarios.setBackgroundResource(com.appcafe.udem.R.drawable.inputs)
            binding.tabUsuarios.setTextColor(requireContext().getColor(com.appcafe.udem.R.color.GrayText))
            binding.inputSearch.hint = "Buscar cafetería..."
            binding.recyclerSearch.adapter = cafeteriaAdapter
            filterCafeterias(binding.inputSearch.text.toString())
        }
        binding.tabUsuarios.setOnClickListener {
            searchMode = MODE_USUARIOS
            binding.tabUsuarios.setBackgroundColor(requireContext().getColor(com.appcafe.udem.R.color.Red))
            binding.tabUsuarios.setTextColor(requireContext().getColor(com.appcafe.udem.R.color.white))
            binding.tabCafeterias.setBackgroundResource(com.appcafe.udem.R.drawable.inputs)
            binding.tabCafeterias.setTextColor(requireContext().getColor(com.appcafe.udem.R.color.GrayText))
            binding.inputSearch.hint = "Buscar usuario..."
            binding.recyclerSearch.adapter = usuarioAdapter
            filterUsuarios(binding.inputSearch.text.toString())
        }
    }

    private fun filterCafeterias(query: String) {
        val filtered = if (query.isBlank()) allCafeterias
        else allCafeterias.filter { it.nombre.contains(query, ignoreCase = true) }
        cafeteriaAdapter.submitList(filtered)
    }

    private fun filterUsuarios(query: String) {
        val filtered = if (query.isBlank()) allUsuarios
        else allUsuarios.filter {
            it.nombre.contains(query, ignoreCase = true) ||
                    it.correo.contains(query, ignoreCase = true)
        }
        usuarioAdapter.submitList(filtered)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val MODE_CAFETERIAS = 0
        private const val MODE_USUARIOS = 1
    }
}