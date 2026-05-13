package com.appcafe.udem

import android.app.Application
import android.content.Context
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatDelegate
import coil.Coil
import coil.ImageLoader
import com.appcafe.udem.data.local.AppDatabase
import com.appcafe.udem.data.local.entities.Usuario
import com.appcafe.udem.data.remote.api.RetrofitClient
import com.appcafe.udem.data.repository.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient

class CafeApplication : Application() {

    val database: AppDatabase by lazy { AppDatabase.getInstance(this) }

    val userRepository: UserRepository by lazy { UserRepository(database.userDao()) }
    val coffeeRepository: CoffeeRepository by lazy {
        CoffeeRepository(database.coffeeDao(), RetrofitClient.apiService)
    }
    val reviewRepository: ReviewRepository by lazy { ReviewRepository(database.reviewDao()) }
    val favoriteRepository: FavoriteRepository by lazy { FavoriteRepository(database.favoriteDao()) }
    val newsRepository: NewsRepository by lazy { NewsRepository(database.newsDao()) }
    val recommendationRepository: RecommendationRepository by lazy {
        RecommendationRepository(database.coffeeDao())
    }
    val visitaRepository: VisitaRepository by lazy { VisitaRepository(database.visitaDao()) }

    override fun onCreate() {
        super.onCreate()
        aplicarPreferenciasDeAccesibilidad()
        configurarCoil()
        seedUsuarios()
    }

    override fun attachBaseContext(base: Context) {
        val prefs = base.getSharedPreferences("cafe_prefs", MODE_PRIVATE)
        val textoGrande = prefs.getBoolean("texto_grande", false)
        val config = Configuration(base.resources.configuration)
        config.fontScale = if (textoGrande) 1.3f else 1.0f
        val contextoAjustado = base.createConfigurationContext(config)
        super.attachBaseContext(contextoAjustado)
    }

    private fun aplicarPreferenciasDeAccesibilidad() {
        val prefs = getSharedPreferences("cafe_prefs", MODE_PRIVATE)
        val darkMode = prefs.getBoolean("dark_mode", false)
        val altoContraste = prefs.getBoolean("alto_contraste", false)
        when {
            altoContraste -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            darkMode -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            else -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
    }

    private fun configurarCoil() {
        val okHttp = OkHttpClient.Builder()
            .addInterceptor { chain ->
                val req = chain.request()
                val newReq = if (req.url.host.contains("googleapis.com")) {
                    req.newBuilder()
                        .addHeader("X-Goog-Api-Key", BuildConfig.PLACES_API_KEY)
                        .build()
                } else req
                chain.proceed(newReq)
            }
            .followRedirects(true)
            .followSslRedirects(true)
            .build()
        Coil.setImageLoader(ImageLoader.Builder(this).okHttpClient(okHttp).build())
    }

    private fun seedUsuarios() {
        CoroutineScope(Dispatchers.IO).launch {
            val dao = database.userDao()

            // Usuarios de la app — fotos genéricas via pravatar.cc
            // Substituir foto por "https://github.com/USERNAME.png" cuando estén disponibles
            val semilla = listOf(
                Usuario(
                    nombre = "Anna Carolina",
                    correo = "anna.carolina@cafe.mx",
                    contrasena = "Anna123",
                    foto = "https://i.pravatar.cc/150?u=anna.carolina@cafe.mx"
                ),
                Usuario(
                    nombre = "Pedro Soria",
                    correo = "pedro.soria@cafe.mx",
                    contrasena = "Pedro123",
                    foto = "https://i.pravatar.cc/150?u=pedro.soria@cafe.mx"
                ),
                Usuario(
                    nombre = "Raquel Garza",
                    correo = "raquel.garza@cafe.mx",
                    contrasena = "Raquel23",
                    foto = "https://i.pravatar.cc/150?u=raquel.garza@cafe.mx"
                ),
                Usuario(
                    nombre = "Douglas Oliveira",
                    correo = "douglas.oliveira@cafe.mx",
                    contrasena = "Douglas123",
                    foto = "https://i.pravatar.cc/150?u=douglas.oliveira@cafe.mx"
                ),
                Usuario(
                    nombre = "Jorge Alanis",
                    correo = "jorge.alanis@cafe.mx",
                    contrasena = "Jorge123",
                    foto = "https://i.pravatar.cc/150?u=jorge.alanis@cafe.mx"
                )
            )

            semilla.forEach { u ->
                if (dao.getUsuarioByCorreo(u.correo) == null) dao.insertUsuario(u)
            }
        }
    }
}