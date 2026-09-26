# FitTrack

Aplicación Android desarrollada con Kotlin, Jetpack Compose y dos `Fragment` para una interfaz de dos paneles. La segunda entrega amplía la base inicial con secciones funcionales de perfil, fotos, video, recursos web y acciones.

## Secciones

- **Perfil:** ilustración, información académica y experiencia; permite editar los datos.
- **Fotos:** consulta una biblioteca ilustrada de ejercicios, toca una para leer su descripción y agrega tus propias imágenes de progreso.
- **Video:** abre la rutina de cuerpo completo de ATHLEAN-X Español en YouTube o elige un archivo local para reproducirlo en la app con controles multimedia.
- **Web:** digita una URL y consúltala en una vista integrada; inicia con información en español de la OMS sobre actividad física.
- **Botones:** registra una nota de bienestar y prueba las acciones Guardar y Cancelar.

## Estructura

- `MainActivity`: aloja los paneles y coordina la selección.
- `MenuFragment`: menú lateral para navegar por las cinco secciones.
- `ContentFragment`: interfaz y estado de las secciones.
- `model/FitTrackSection.kt`: catálogo de secciones.

## Abrir y ejecutar

1. Abrir esta carpeta con Android Studio.
2. Esperar la sincronización de Gradle.
3. Ejecutar la configuración `app` en un emulador o dispositivo Android.

La sección Web requiere conexión a Internet. Fotos y Video usan el selector de documentos de Android y no solicitan acceso general al almacenamiento.

## Diseño

Los mockups visuales están en `docs/mockups/`; la estructura de navegación se describe en `docs/wireframe.md`.

Repositorio: [JulianOrtiz84/fittrack-android](https://github.com/JulianOrtiz84/fittrack-android).
