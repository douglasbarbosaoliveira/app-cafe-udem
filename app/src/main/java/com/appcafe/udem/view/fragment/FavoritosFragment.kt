package com.appcafe.udem.view.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.appcafe.udem.CafeApplication
import com.appcafe.udem.adapter.FavoritoCafeteriaAdapter
import com.appcafe.udem.databinding.FragmentFavoritosBinding
import com.appcafe.udem.view.ConfiguracionActivity
import com.appcafe.udem.view.DetailCafeteriaActivity
import com.appcafe.udem.view.MainContainerActivity
import com.appcafe.udem.viewmodel.FavoritesViewModel

class FavoritosFragment : Fragment() {

    private var _binding: FragmentFavoritosBinding? = null
    private val binding get() = _binding!!

    private val viewModel: FavoritesViewModel by viewModels {
        FavoritesViewModel.Factory((requireActivity().application as CafeApplication).favoriteRepository)
    }

    private lateinit var adapter: FavoritoCafeteriaAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentFavoritosBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = FavoritoCafeteriaAdapter(
            onClick = { cafeteria ->
                val intent = Intent(requireContext(), DetailCafeteriaActivity::class.java)
                intent.putExtra(DetailCafeteriaActivity.EXTRA_CAFETERIA_ID, cafeteria.id)
                startActivity(intent)
            },
            onRemove = { cafeteria -> viewModel.eliminarFavorito(cafeteria.id) }
        )

        binding.recyclerFavorites.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerFavorites.adapter = adapter

        val prefs = requireActivity().getSharedPreferences("cafe_prefs", android.content.Context.MODE_PRIVATE)
        val usuarioId = prefs.getInt("usuario_id", -1)
        if (usuarioId != -1) viewModel.setUsuarioId(usuarioId)

        viewModel.cafeteriasFavoritas.observe(viewLifecycleOwner) { lista ->
            adapter.submitList(lista)
        }

        viewModel.accionExitosa.observe(viewLifecycleOwner) { msg ->
            if (!msg.isNullOrEmpty()) {
                Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
                viewModel.limpiarAccion()
            }
        }

        binding.cardPerfil.setOnClickListener {
            (requireActivity() as? MainContainerActivity)?.selectTab(HomeFragment.TAB_PERFIL)
        }

        binding.btnSeeAll.setOnClickListener {
            binding.scrollView.post {
                binding.scrollView.smoothScrollTo(0, binding.recyclerFavorites.top)
            }
        }

        binding.btnSettings.setOnClickListener {
            startActivity(Intent(requireContext(), ConfiguracionActivity::class.java))
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
