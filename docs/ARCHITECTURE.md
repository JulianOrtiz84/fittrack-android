# Arquitectura inicial

FitTrack usa una estructura simple y escalable para la Entrega 1.

`MainActivity` aloja dos contenedores de fragments. `MenuFragment` conserva la opción seleccionada y notifica a la actividad. La actividad pasa la selección a `ContentFragment`, que representa el contenido mediante Jetpack Compose.

Cada panel es un `Fragment` real, de modo que puede ampliarse sin cambiar la estructura principal. Las cinco opciones están centralizadas en `FitTrackSection`, para evitar textos y rutas duplicadas.

Para la Entrega 2, cada sección podrá evolucionar a datos reales, selección de imágenes, reproducción de video, WebView controlado y acciones conectadas a ViewModels.
