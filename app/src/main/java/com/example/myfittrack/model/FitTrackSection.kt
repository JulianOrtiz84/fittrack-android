package com.example.myfittrack.model

enum class FitTrackSection(
    val label: String,
    val emoji: String,
    val description: String
) {
    PROFILE("Perfil", "●", "Datos y objetivos personales"),
    PHOTOS("Fotos", "▣", "Registra tu progreso visual"),
    VIDEO("Video", "▶", "Rutinas y contenido audiovisual"),
    WEB("Web", "⌘", "Recursos de bienestar en línea"),
    BUTTONS("Botones", "↗", "Acciones rápidas de la app")
}
