# Arquitectura de FitTrack

FitTrack usa una actividad con dos contenedores de fragments lado a lado en teléfonos y pantallas anchas. `MainActivity` aloja el menú lateral izquierdo y el panel derecho de contenido; `MenuFragment` comunica la selección de sección y `ContentFragment` presenta esa sección con Jetpack Compose. En teléfonos, ambos paneles se compactan y el contenido derecho permite desplazamiento vertical.

## Secciones de la segunda entrega

- **Perfil:** formulario de edición y datos académicos y profesionales. `SaveableStateHolder` conserva los campos al cambiar de sección.
- **Fotos:** la galería incluye cinco ilustraciones educativas de ejercicios, cada una con descripción al seleccionarla. `OpenMultipleDocuments` permite agregar imágenes personales sin permiso general; sus URI se conservan en preferencias locales.
- **Video:** enlaza la rutina de cuerpo completo de ATHLEAN-X Español en YouTube. También `OpenDocument` permite elegir un archivo local; `VideoView` y `MediaController` lo reproducen dentro de la app y el URI se conserva en preferencias locales.
- **Web:** un campo permite digitar una URL y cargarla en el `WebView`; inicia con la página en español de actividad física de la OMS. JavaScript, acceso a archivos y almacenamiento DOM están desactivados; los enlaces del dominio de la OMS permanecen en la vista y los enlaces web externos se abren fuera de la app. La vista necesita el permiso `INTERNET`.
- **Botones:** formulario demostrativo con validación, guardado visible y cancelación de la edición.

Las cinco secciones siguen centralizadas en `model/FitTrackSection.kt`. Para el flujo visual, consulta `wireframe.md` y los mockups de `mockups/`.
