# FitTrack

Base Android de la Entrega 1. La app usa Kotlin, Jetpack Compose y dos `Fragment` reales para una interfaz de dos paneles.

## Estructura

- `MainActivity`: aloja ambos paneles y coordina la selección.
- `MenuFragment`: panel izquierdo con Perfil, Fotos, Video, Web y Botones.
- `ContentFragment`: panel derecho que muestra el contenido inicial de la opción activa.
- `model/FitTrackSection.kt`: catálogo único de las cinco secciones.

## Abrir y ejecutar

1. Abrir esta carpeta con Android Studio.
2. Esperar la sincronización de Gradle.
3. Ejecutar la configuración `app` en un emulador o dispositivo Android.

## Pendientes

- Repositorio público: [JulianOrtiz84/fittrack-android](https://github.com/JulianOrtiz84/fittrack-android). Falta completar el primer `push` desde el repositorio local.
- Los mockups visuales de Perfil, Fotos y Video están en `docs/mockups/`; el wireframe de estructura está en `docs/wireframe.md`.
