package com.appcafe.udem.view

import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.appcafe.udem.BuildConfig
import com.appcafe.udem.CafeApplication
import com.appcafe.udem.R
import com.appcafe.udem.databinding.ActivityHomeBinding
import com.appcafe.udem.viewmodel.CoffeeViewModel
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

// Activity principal de la app. Muestra el mapa con cafeterías cercanas,
// lanza una notificación de bienvenida y detecta luz ambiental para sugerir Dark Mode.
class HomeActivity : AppCompatActivity(), SensorEventListener {

    private lateinit var binding: ActivityHomeBinding
    private var googleMap: GoogleMap? = null

    // Componentes del sensor de luz ambiental
    private lateinit var sensorManager: SensorManager
    private var lightSensor: Sensor? = null

    // Evita que la sugerencia de Dark Mode aparezca más de una vez por sesión
    private var darkModeSuggested = false

    // Umbral de luz en lux: por debajo de este valor se considera ambiente oscuro
    private val LIGHT_THRESHOLD = 3f

    // ViewModel que gestiona la lógica de cafeterías
    private val coffeeViewModel: CoffeeViewModel by viewModels {
        CoffeeViewModel.Factory((application as CafeApplication).coffeeRepository)
    }

    // Coordenadas de la UDEM como ubicación por defecto
    private val UDEM_LAT = 25.6499
    private val UDEM_LNG = -100.4058

    // Canal de notificaciones compartido con el resto de la app
    private val CHANNEL_ID = "canal_eventos_cafe"

    // Lanzador de permisos de ubicación
    private val locationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permisos ->
        if (permisos[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
            permisos[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        ) {
            obtenerUbicacionYBuscar()
        } else {
            buscarCafeterias(UDEM_LAT, UDEM_LNG)
        }
    }

    // Lanzador de permiso de notificaciones (requerido en Android 13+)
    private val notificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { concedido ->
        // Si el permiso fue concedido, mostrar la notificación
        if (concedido) mostrarNotificacionBienvenida()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inicializar el gestor de sensores y obtener el sensor de luz
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT)

        // Solicitar permiso y mostrar notificación de bienvenida
        solicitarPermisoYNotificar()

        // Inicializar el MapView
        binding.mapView.onCreate(savedInstanceState)
        binding.mapView.getMapAsync { map ->
            googleMap = map
            // Deshabilitar gestos para que el mapa sea solo visual en esta pantalla
            map.uiSettings.setAllGesturesEnabled(false)
            map.uiSettings.isZoomControlsEnabled = false
            map.uiSettings.isMapToolbarEnabled = false
        }

        // Solicitar permisos de ubicación y cargar cafeterías
        pedirUbicacionYBuscarCafeterias()

        // Mostrar errores del ViewModel si ocurren
        coffeeViewModel.error.observe(this) { msg ->
            if (!msg.isNullOrEmpty()) {
                Toast.makeText(this, "Error al cargar cafeterías: $msg", Toast.LENGTH_SHORT).show()
            }
        }

        // Actualizar el mapa cuando cambia la lista de cafeterías
        coffeeViewModel.cafeterias.observe(this) { cafeterias ->
            if (cafeterias.isNullOrEmpty()) return@observe
            val center = cafeterias.first()
            val centerLatLng = LatLng(center.latitud, center.longitud)
            googleMap?.let { map ->
                map.clear()
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(centerLatLng, 14f))
                cafeterias.forEach { c ->
                    map.addMarker(MarkerOptions().position(LatLng(c.latitud, c.longitud)).title(c.nombre))
                }
            } ?: binding.mapView.getMapAsync { map ->
                // Si el mapa aún no estaba listo, configurarlo y agregar marcadores
                googleMap = map
                map.uiSettings.setAllGesturesEnabled(false)
                map.uiSettings.isZoomControlsEnabled = false
                map.uiSettings.isMapToolbarEnabled = false
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(centerLatLng, 14f))
                cafeterias.forEach { c ->
                    map.addMarker(MarkerOptions().position(LatLng(c.latitud, c.longitud)).title(c.nombre))
                }
            }
        }

        // Navegación entre pantallas
        binding.cardPerfil.setOnClickListener {
            startActivity(Intent(this, PerfilActivity::class.java))
        }
        binding.btnSettings.setOnClickListener {
            startActivity(Intent(this, ConfiguracionActivity::class.java))
        }
        binding.btnFavoritos.setOnClickListener {
            startActivity(Intent(this, FavoritosActivity::class.java))
        }
        binding.btnPerfilHome.setOnClickListener {
            startActivity(Intent(this, PerfilActivity::class.java))
        }
        binding.btnAccesibilidad.setOnClickListener {
            startActivity(Intent(this, AccesibilidadActivity::class.java))
        }
        binding.btnAjustes.setOnClickListener {
            startActivity(Intent(this, ConfiguracionActivity::class.java))
        }
        binding.btnBuscar.setOnClickListener {
            startActivity(Intent(this, SearchActivity::class.java))
        }
        binding.cardUsuarios.setOnClickListener {
            startActivity(Intent(this, UsuariosActivity::class.java))
        }
        binding.cardComunidad.setOnClickListener {
            startActivity(Intent(this, ComunidadActivity::class.java))
        }

        // Navegación del footer
        binding.footer.navHome.setOnClickListener { /* ya estamos en Home */ }
        binding.footer.navSearch.setOnClickListener {
            startActivity(Intent(this, SearchActivity::class.java))
        }
        binding.footer.navWeb.setOnClickListener {
            startActivity(Intent(this, ComunidadActivity::class.java))
        }
        binding.footer.navFav.setOnClickListener {
            startActivity(Intent(this, FavoritosActivity::class.java))
        }
        binding.footer.navUser.setOnClickListener {
            startActivity(Intent(this, PerfilActivity::class.java))
        }
    }

    // ── Sensor de luz ──────────────────────────────────────────────────────────

    // Se llama cada vez que el sensor detecta un cambio en la luz ambiental
    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type != Sensor.TYPE_LIGHT) return

        val lux = event.values[0]
        val prefs = getSharedPreferences("cafe_prefs", MODE_PRIVATE)
        val darkModeActivo = prefs.getBoolean("dark_mode", false)

        // Sugerir Dark Mode solo si el ambiente está oscuro, el modo no está
        // activado y aún no se ha sugerido en esta sesión
        if (lux < LIGHT_THRESHOLD && !darkModeActivo && !darkModeSuggested) {
            darkModeSuggested = true
            com.google.android.material.snackbar.Snackbar.make(
                binding.root,
                "Ambiente oscuro detectado. ¿Activar Dark Mode?",
                10000
            ).setAction("Activar") {
                // Guardar preferencia y aplicar el tema oscuro inmediatamente
                prefs.edit().putBoolean("dark_mode", true).apply()
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                recreate()
            }.show()
        }
    }

    // Requerido por SensorEventListener, no se utiliza en este caso
    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    // ── Notificaciones ─────────────────────────────────────────────────────────

    // Verifica el permiso de notificaciones y lo solicita si es necesario (Android 13+)
    private fun solicitarPermisoYNotificar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val tienePermiso = ContextCompat.checkSelfPermission(
                this, Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED

            if (tienePermiso) {
                mostrarNotificacionBienvenida()
            } else {
                // Solicitar permiso al usuario
                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        } else {
            // En versiones anteriores a Android 13 no se requiere permiso explícito
            mostrarNotificacionBienvenida()
        }
    }

    // Crea el canal y muestra la notificación de bienvenida
    @SuppressLint("MissingPermission")
    private fun mostrarNotificacionBienvenida() {
        // Crear el canal de notificación (requerido en Android 8+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val canal = NotificationChannel(
                CHANNEL_ID,
                "Eventos de la comunidad",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Notificaciones sobre eventos y reuniones en cafeterías"
            }
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(canal)
        }

        // Construir la notificación
        val notificacion = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notifications)
            .setContentTitle("☕ ¡Bienvenido a Café!")
            .setContentText("Revisa los eventos de hoy en tu comunidad.")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()

        // Mostrar la notificación
        NotificationManagerCompat.from(this).notify(1, notificacion)
    }

    // ── Ciclo de vida ──────────────────────────────────────────────────────────

    override fun onResume() {
        super.onResume()
        binding.mapView.onResume()
        // Registrar el sensor solo cuando la Activity está visible para ahorrar batería
        lightSensor?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
        }
    }

    override fun onPause() {
        super.onPause()
        binding.mapView.onPause()
        // Liberar el sensor al salir de la pantalla para no consumir recursos en segundo plano
        sensorManager.unregisterListener(this)
    }

    override fun onStop() { super.onStop(); binding.mapView.onStop() }
    override fun onDestroy() { super.onDestroy(); binding.mapView.onDestroy() }
    override fun onLowMemory() { super.onLowMemory(); binding.mapView.onLowMemory() }
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        binding.mapView.onSaveInstanceState(outState)
    }

    // ── Localización ──────────────────────────────────────────────────────────

    // Verifica si ya se tienen permisos de ubicación; si no, los solicita
    private fun pedirUbicacionYBuscarCafeterias() {
        val tienePermiso = ContextCompat.checkSelfPermission(
            this, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (tienePermiso) {
            obtenerUbicacionYBuscar()
        } else {
            locationPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    // Obtiene la ubicación actual del dispositivo con alta precisión
    @SuppressLint("MissingPermission")
    private fun obtenerUbicacionYBuscar() {
        val fusedClient = LocationServices.getFusedLocationProviderClient(this)
        fusedClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
            .addOnSuccessListener { location ->
                if (location != null && !esEmulador(location.latitude, location.longitude)) {
                    buscarCafeterias(location.latitude, location.longitude)
                } else {
                    // Si la ubicación es nula o corresponde a un emulador, usar UDEM
                    buscarCafeterias(UDEM_LAT, UDEM_LNG)
                }
            }
            .addOnFailureListener {
                // En caso de fallo, usar coordenadas por defecto
                buscarCafeterias(UDEM_LAT, UDEM_LNG)
            }
    }

    // Detecta si el dispositivo es un emulador comparando con coordenadas típicas
    private fun esEmulador(lat: Double, lng: Double) = lat in 37.0..38.0 && lng in -123.0..-121.0

    // Llama al ViewModel para buscar cafeterías cercanas a las coordenadas dadas
    private fun buscarCafeterias(lat: Double, lng: Double) {
        coffeeViewModel.buscarCafeteriasNearby(
            latitud = lat,
            longitud = lng,
            apiKey = BuildConfig.PLACES_API_KEY
        )
    }
}