<p align="center">
  <img src="app/src/main/res/mipmap-xxxhdpi/logo_cafe.webp" width="150px" alt="Café Logo">
</p>

<h1 align="center">☕ Café — Guía de Cafeterías en Monterrey | Coffee Shop Guide | Guia de Cafeterias</h1>

<p align="center">
  <img src="https://img.shields.io/badge/Android-Kotlin-7F52FF.svg?logo=kotlin" alt="Kotlin">
  <img src="https://img.shields.io/badge/Architecture-MVVM-brightgreen.svg" alt="MVVM">
  <img src="https://img.shields.io/badge/Database-Room-blue.svg" alt="Room">
  <img src="https://img.shields.io/badge/API-Google%20Places-4285F4.svg?logo=googlemaps" alt="Places API">
  <img src="https://img.shields.io/badge/Maps-Google%20Maps%20SDK-34A853.svg?logo=googlemaps" alt="Google Maps">
  <img src="https://img.shields.io/badge/University-UDEM-FBBE21.svg" alt="UDEM">
</p>

<p align="center">
  <img src="https://img.shields.io/badge/Tests-Unit%20%26%20Espresso-success.svg" alt="Tests">
  <img src="https://img.shields.io/badge/Sensor-Light%20Sensor-lightgrey.svg" alt="Sensor">
  <img src="https://img.shields.io/badge/Notifications-FCM-orange.svg" alt="Notifications">
  <img src="https://img.shields.io/badge/Min%20SDK-24-informational.svg" alt="Min SDK">
</p>

---

🌐 **Idioma / Language / Idioma:**
[🇲🇽 Español](#-español) · [🇺🇸 English](#-english) · [🇧🇷 Português](#-português)

---

## 🇲🇽 Español

### Equipo — MTYSP

| Nombre | Rol |
|---|---|
| Anna Carolina De Azevedo Leite | Arquitecto de la app |
| Raquel de la Garza von Rossum | Diseñador de UI/UX |
| Douglas Barbosa de Oliveira | Líder de proyecto |
| Pedro Elidio Soria González | Responsable de datos |

### Descripción

Café es una aplicación Android que funciona como guía de cafeterías en el área metropolitana de Monterrey. Permite encontrar cafeterías cercanas en el mapa, consultar información detallada de cada lugar, dejar reseñas, guardar favoritos y conectarse con una comunidad de amantes del café.

### Funcionalidades implementadas

**🗺️ Mapa y ubicación**
Muestra un mapa interactivo con marcadores de cafeterías cercanas a la ubicación del usuario, usando Google Maps SDK y Places API (New).

**🔍 Búsqueda**
Permite buscar cafeterías por nombre desde una pantalla dedicada.

**☕ Detalle de cafetería**
Muestra nombre, dirección, calificación, fotos, horario, teléfono y sitio web obtenidos desde la Places API.

**⭐ Reseñas**
Los usuarios pueden escribir reseñas con calificación de estrellas y comentario. Se guardan localmente con Room.

**❤️ Favoritos**
El usuario puede marcar cafeterías como favoritas y acceder a ellas rápidamente.

**👤 Perfil de usuario**
Pantalla de perfil con nombre, correo electrónico y foto tomada desde la cámara del dispositivo.

**👥 Comunidad**
Sección donde se muestran eventos y reuniones para que los amantes del café se encuentren.

**🔔 Notificaciones**
Al abrir la app, el usuario recibe una notificación de bienvenida invitándolo a revisar los eventos del día.

**🌙 Sensor de luz**
La app detecta ambientes oscuros usando el sensor de luz del dispositivo. Si la luminosidad es muy baja, sugiere activar el Modo Oscuro mediante un Snackbar.

**♿ Accesibilidad**
Pantalla de accesibilidad con opciones funcionales: Modo Oscuro, Alto Contraste, Texto Grande y Reducir Animaciones.

**🔐 Autenticación**
Registro e inicio de sesión con correo y contraseña. Los datos se almacenan localmente con Room.

### Tecnologías utilizadas

| Tecnología | Uso en la app |
|---|---|
| Activities | Pantalla contenedora de navegación (`MainContainerActivity`) y pantallas secundarias (Login, Detalle, Configuración, etc.) |
| Fragments | Las 5 pestañas principales de navegación: Home, Buscar, Comunidad, Favoritos y Perfil, alojadas en `MainContainerActivity` |
| ViewBinding | Acceso seguro a vistas (sin `findViewById`) |
| RecyclerView | Listas de cafeterías, reseñas, favoritos y eventos |
| Room / SQLite | Persistencia local de usuarios, reseñas y favoritos |
| LiveData | Comunicación reactiva entre ViewModel y UI |
| MVVM | Arquitectura: View, ViewModel y Model separados |
| Retrofit | Consumo de la Places API (New) de Google |
| Google Maps SDK | Mapa interactivo |
| Places API (New) | Búsqueda y detalle de cafeterías reales |
| Coil | Carga de imágenes desde URLs |
| SharedPreferences | Persistencia de preferencias de accesibilidad |
| Cámara | Captura de foto de perfil |
| Sensor de luz | Detección de ambiente oscuro |
| Notificaciones | Notificación de bienvenida al abrir la app |
| Coroutines | Operaciones asíncronas |

### Pruebas

**Unitarias — `RegistrationUtilTest.kt`**
`app/src/test/java/com/appcafe/udem/`
- 11 pruebas de validación de registro
- 4 pruebas de validación de login
- 6 pruebas de validación de reseñas
- **Total: 21 pruebas**

**Instrumentales — `RegistrationUtilInstrumentedTest.kt`**
`app/src/androidTest/java/com/appcafe/udem/`
- 5 pruebas de UI en `LoginActivity` con Espresso

### Arquitectura MVVM

```
View
 ├── Activities (MainContainerActivity, Login, Detalle, Configuración…)
 │       └── Fragments (Home, Buscar, Comunidad, Favoritos, Perfil)
 │
 ▼
ViewModel (lógica + LiveData)
 │
 ▼
Repository
 ├── Room (base de datos local)
 └── Retrofit (Places API)
```

```
com.appcafe.udem/
├── view/
│   ├── fragment/   → Los 5 Fragments de navegación principal
│   └── ...         → Activities secundarias
├── viewmodel/      → ViewModels con LiveData
├── data/
│   ├── local/      → Room: entities, DAOs, AppDatabase
│   ├── remote/     → Retrofit: ApiService, modelos
│   └── repository/ → Repositories
├── adapter/        → RecyclerView Adapters
└── model/          → Modelos de datos de UI
```

### Cómo ejecutar el proyecto

**Requisitos**
- Android Studio Hedgehog o superior
- Android SDK 24+
- Conexión a internet

**1. Clonar el repositorio**
```bash
git clone https://github.com/douglasbarbosaoliveira/app-cafe-udem.git
```

**2. Configurar la API Key**

Crear `local.properties` en la raíz del proyecto:
```
PLACES_API_KEY=TU_API_KEY_AQUI
```
> ⚠️ La API Key debe tener habilitadas: **Maps SDK for Android** y **Places API (New)**.

**3. Sincronizar Gradle y ejecutar**
```
Build → Sync Project with Gradle Files → ▶ Run
```

**Credenciales de prueba**
```
Correo:     ana@cafe.mx
Contraseña: Ana123
```

---

## 🇺🇸 English

### Team — MTYSP

| Name | Role |
|---|---|
| Anna Carolina De Azevedo Leite | App Architect |
| Raquel de la Garza von Rossum | UI/UX Designer |
| Douglas Barbosa de Oliveira | Project Lead |
| Pedro Elidio Soria González | Data Manager |

### Description

Café is an Android application that serves as a coffee shop guide for the Monterrey metropolitan area. It allows users to find nearby coffee shops on a map, view detailed information, leave reviews, save favorites, and connect with a community of coffee lovers.

### Implemented Features

**🗺️ Map & Location**
Displays an interactive map with markers for nearby coffee shops based on the user's location, using Google Maps SDK and Places API (New).

**🔍 Search**
Allows searching for coffee shops by name from a dedicated screen.

**☕ Coffee Shop Detail**
Shows the name, address, rating, photos, schedule, phone number, and website fetched from the Places API.

**⭐ Reviews**
Users can write reviews with a star rating and comment, stored locally using Room.

**❤️ Favorites**
Users can mark coffee shops as favorites for quick access.

**👤 User Profile**
Profile screen with name, email, and a photo taken directly from the device camera.

**👥 Community**
Community section displaying events and meetups for coffee lovers to connect.

**🔔 Notifications**
A welcome notification is shown when the app opens, inviting users to check the day's events.

**🌙 Light Sensor**
The app detects dark environments using the device's light sensor and suggests enabling Dark Mode via a Snackbar.

**♿ Accessibility**
Accessibility screen with functional options: Dark Mode, High Contrast, Large Text, and Reduce Animations.

**🔐 Authentication**
Registration and login with email and password, stored locally using Room.

### Technologies Used

| Technology | Usage |
|---|---|
| Activities | Navigation container (`MainContainerActivity`) and secondary screens (Login, Detail, Settings, etc.) |
| Fragments | The 5 main navigation tabs: Home, Search, Community, Favorites, and Profile, hosted inside `MainContainerActivity` |
| ViewBinding | Safe view access (no `findViewById`) |
| RecyclerView | Lists of coffee shops, reviews, favorites, events |
| Room / SQLite | Local persistence of users, reviews, favorites |
| LiveData | Reactive communication between ViewModel and UI |
| MVVM | Architecture pattern: View, ViewModel, Model |
| Retrofit | Google Places API (New) consumption |
| Google Maps SDK | Interactive map rendering |
| Places API (New) | Real coffee shop search and details |
| Coil | Image loading from URLs |
| SharedPreferences | Accessibility preferences persistence |
| Camera | Profile photo capture |
| Light Sensor | Dark environment detection |
| Notifications | Welcome notification on app launch |
| Coroutines | Async operations |

### Tests

**Unit Tests — `RegistrationUtilTest.kt`**
`app/src/test/java/com/appcafe/udem/`
- 11 registration validation tests
- 4 login validation tests
- 6 review validation tests
- **Total: 21 unit tests**

**Instrumented Tests — `RegistrationUtilInstrumentedTest.kt`**
`app/src/androidTest/java/com/appcafe/udem/`
- 5 UI tests on `LoginActivity` with Espresso

### MVVM Architecture

```
View
 ├── Activities (MainContainerActivity, Login, Detail, Settings…)
 │       └── Fragments (Home, Search, Community, Favorites, Profile)
 │
 ▼
ViewModel (logic + LiveData)
 │
 ▼
Repository
 ├── Room (local database)
 └── Retrofit (Places API)
```

```
com.appcafe.udem/
├── view/
│   ├── fragment/   → The 5 main navigation Fragments
│   └── ...         → Secondary Activities
├── viewmodel/      → ViewModels with LiveData
├── data/
│   ├── local/      → Room: entities, DAOs, AppDatabase
│   ├── remote/     → Retrofit: ApiService, response models
│   └── repository/ → Repositories
├── adapter/        → RecyclerView Adapters
└── model/          → UI data models
```

### How to Run the Project

**Requirements**
- Android Studio Hedgehog or newer
- Android SDK 24+
- Internet connection

**1. Clone the repository**
```bash
git clone https://github.com/douglasbarbosaoliveira/app-cafe-udem.git
```

**2. Configure the API Key**

Create `local.properties` at the project root:
```
PLACES_API_KEY=YOUR_API_KEY_HERE
```
> ⚠️ The API Key must have **Maps SDK for Android** and **Places API (New)** enabled.

**3. Sync Gradle and Run**
```
Build → Sync Project with Gradle Files → ▶ Run
```

**Test Credentials**
```
Email:    ana@cafe.mx
Password: Ana123
```

---

## 🇧🇷 Português

### Equipe — MTYSP

| Nome | Função |
|---|---|
| Anna Carolina De Azevedo Leite | Arquiteta do app |
| Raquel de la Garza von Rossum | Designer de UI/UX |
| Douglas Barbosa de Oliveira | Líder de projeto |
| Pedro Elidio Soria González | Responsável por dados |

### Descrição

Café é um aplicativo Android que funciona como um guia de cafeterias na região metropolitana de Monterrey. Permite encontrar cafeterias próximas no mapa, consultar informações detalhadas, deixar avaliações, salvar favoritos e se conectar com uma comunidade de amantes de café.

### Funcionalidades implementadas

**🗺️ Mapa e localização**
Exibe um mapa interativo com marcadores das cafeterias mais próximas da localização do usuário, usando o Google Maps SDK e a Places API (New).

**🔍 Busca**
Permite buscar cafeterias por nome em uma tela dedicada.

**☕ Detalhe da cafeteria**
Exibe nome, endereço, avaliação, fotos, horário, telefone e site obtidos da Places API.

**⭐ Avaliações**
Os usuários podem escrever avaliações com nota em estrelas e comentário, salvas localmente com Room.

**❤️ Favoritos**
O usuário pode marcar cafeterias como favoritas para acesso rápido.

**👤 Perfil do usuário**
Tela de perfil com nome, e-mail e foto tirada diretamente pela câmera do dispositivo.

**👥 Comunidade**
Seção com eventos e encontros para que os amantes de café se reúnam.

**🔔 Notificações**
Ao abrir o app, o usuário recebe uma notificação de boas-vindas convidando a conferir os eventos do dia.

**🌙 Sensor de luz**
O app detecta ambientes escuros pelo sensor de luz do dispositivo e sugere ativar o Modo Escuro via Snackbar.

**♿ Acessibilidade**
Tela de acessibilidade com opções funcionais: Modo Escuro, Alto Contraste, Texto Grande e Reduzir Animações.

**🔐 Autenticação**
Cadastro e login com e-mail e senha, armazenados localmente com Room.

### Tecnologias utilizadas

| Tecnologia | Uso no app |
|---|---|
| Activities | Tela contêiner de navegação (`MainContainerActivity`) e telas secundárias (Login, Detalhe, Configurações, etc.) |
| Fragments | As 5 abas principais de navegação: Home, Busca, Comunidade, Favoritos e Perfil, hospedadas na `MainContainerActivity` |
| ViewBinding | Acesso seguro às views (sem `findViewById`) |
| RecyclerView | Listas de cafeterias, avaliações, favoritos e eventos |
| Room / SQLite | Persistência local de usuários, avaliações e favoritos |
| LiveData | Comunicação reativa entre ViewModel e UI |
| MVVM | Arquitetura: View, ViewModel e Model separados |
| Retrofit | Consumo da Places API (New) do Google |
| Google Maps SDK | Mapa interativo |
| Places API (New) | Busca e detalhes de cafeterias reais |
| Coil | Carregamento de imagens por URL |
| SharedPreferences | Persistência de preferências de acessibilidade |
| Câmera | Captura de foto de perfil |
| Sensor de luz | Detecção de ambiente escuro |
| Notificações | Notificação de boas-vindas ao abrir o app |
| Coroutines | Operações assíncronas |

### Testes

**Unitários — `RegistrationUtilTest.kt`**
`app/src/test/java/com/appcafe/udem/`
- 11 testes de validação de cadastro
- 4 testes de validação de login
- 6 testes de validação de avaliação
- **Total: 21 testes unitários**

**Instrumentais — `RegistrationUtilInstrumentedTest.kt`**
`app/src/androidTest/java/com/appcafe/udem/`
- 5 testes de UI na `LoginActivity` com Espresso

### Arquitetura MVVM

```
View
 ├── Activities (MainContainerActivity, Login, Detalhe, Configurações…)
 │       └── Fragments (Home, Busca, Comunidade, Favoritos, Perfil)
 │
 ▼
ViewModel (lógica + LiveData)
 │
 ▼
Repository
 ├── Room (banco de dados local)
 └── Retrofit (Places API)
```

```
com.appcafe.udem/
├── view/
│   ├── fragment/   → Os 5 Fragments de navegação principal
│   └── ...         → Activities secundárias
├── viewmodel/      → ViewModels com LiveData
├── data/
│   ├── local/      → Room: entities, DAOs, AppDatabase
│   ├── remote/     → Retrofit: ApiService, modelos
│   └── repository/ → Repositories
├── adapter/        → Adapters do RecyclerView
└── model/          → Modelos de dados de UI
```

### Como executar o projeto

**Requisitos**
- Android Studio Hedgehog ou superior
- Android SDK 24+
- Conexão com a internet

**1. Clonar o repositório**
```bash
git clone https://github.com/douglasbarbosaoliveira/app-cafe-udem.git
```

**2. Configurar a API Key**

Criar o arquivo `local.properties` na raiz do projeto:
```
PLACES_API_KEY=SUA_API_KEY_AQUI
```
> ⚠️ A API Key deve ter habilitadas: **Maps SDK for Android** e **Places API (New)**.

**3. Sincronizar o Gradle e executar**
```
Build → Sync Project with Gradle Files → ▶ Run
```

**Credenciais de teste**
```
E-mail: ana@cafe.mx
Senha:  Ana123
```

---

<p align="center">
  <i>Universidad de Monterrey — Vicerrectoría de Ingeniería y Tecnologías</i><br>
  <i>Desarrollo de Aplicaciones Móviles</i>
</p>
