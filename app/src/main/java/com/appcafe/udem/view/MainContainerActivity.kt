package com.appcafe.udem.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.appcafe.udem.R
import com.appcafe.udem.databinding.ActivityMainContainerBinding
import com.appcafe.udem.view.fragment.ComunidadFragment
import com.appcafe.udem.view.fragment.FavoritosFragment
import com.appcafe.udem.view.fragment.HomeFragment
import com.appcafe.udem.view.fragment.PerfilFragment
import com.appcafe.udem.view.fragment.SearchFragment

class MainContainerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainContainerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainContainerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (savedInstanceState == null) {
            selectTab(HomeFragment.TAB_HOME)
        }

        binding.footer.navHome.setOnClickListener { selectTab(HomeFragment.TAB_HOME) }
        binding.footer.navSearch.setOnClickListener { selectTab(HomeFragment.TAB_BUSCAR) }
        binding.footer.navWeb.setOnClickListener { selectTab(HomeFragment.TAB_COMUNIDAD) }
        binding.footer.navFav.setOnClickListener { selectTab(HomeFragment.TAB_FAVORITOS) }
        binding.footer.navUser.setOnClickListener { selectTab(HomeFragment.TAB_PERFIL) }
    }

    fun selectTab(tab: Int) {
        val fragment: Fragment = when (tab) {
            HomeFragment.TAB_HOME -> HomeFragment()
            HomeFragment.TAB_BUSCAR -> SearchFragment()
            HomeFragment.TAB_COMUNIDAD -> ComunidadFragment()
            HomeFragment.TAB_FAVORITOS -> FavoritosFragment()
            HomeFragment.TAB_PERFIL -> PerfilFragment()
            else -> HomeFragment()
        }
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit()
    }
}
