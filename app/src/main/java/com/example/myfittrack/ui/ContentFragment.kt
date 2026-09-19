package com.example.myfittrack.ui

import android.os.Bundle
import android.view.View
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import com.example.myfittrack.model.FitTrackSection
import com.example.myfittrack.ui.theme.MyFitTrackTheme

class ContentFragment : Fragment() {
    private var currentSection by mutableStateOf(FitTrackSection.PROFILE)

    fun show(section: FitTrackSection) {
        currentSection = section
    }

    override fun onCreateView(
        inflater: android.view.LayoutInflater,
        container: android.view.ViewGroup?,
        savedInstanceState: Bundle?
    ): View = ComposeView(requireContext()).apply {
        setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
        setContent {
            MyFitTrackTheme(dynamicColor = false) { ContentPanel(currentSection) }
        }
    }
}

@Composable
private fun ContentPanel(section: FitTrackSection) {
    val accent = Color(0xFF2EC4B6)
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF7F8FA))
            .padding(32.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        Text(section.label, color = Color(0xFF14213D), style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Text(section.description, color = Color(0xFF617087), style = MaterialTheme.typography.bodyLarge)
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(modifier = Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(section.emoji, color = accent, style = MaterialTheme.typography.displaySmall, fontWeight = FontWeight.Bold)
                Text(contentTitle(section), color = Color(0xFF14213D), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)
                Text(contentDescription(section), color = Color(0xFF617087), style = MaterialTheme.typography.bodyMedium)
                Spacer(Modifier.height(8.dp))
                SectionAction(section, accent)
            }
        }
    }
}

@Composable
private fun SectionAction(section: FitTrackSection, accent: Color) {
    when (section) {
        FitTrackSection.BUTTONS -> Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Button(onClick = {}, colors = ButtonDefaults.buttonColors(containerColor = accent)) { Text("Guardar") }
            OutlinedButton(onClick = {}) { Text("Cancelar") }
        }
        else -> Button(onClick = {}, colors = ButtonDefaults.buttonColors(containerColor = accent)) {
            Text(actionLabel(section))
        }
    }
}

private fun contentTitle(section: FitTrackSection) = when (section) {
    FitTrackSection.PROFILE -> "Tu perfil de bienestar"
    FitTrackSection.PHOTOS -> "Bitácora visual"
    FitTrackSection.VIDEO -> "Rutina recomendada"
    FitTrackSection.WEB -> "Recursos seleccionados"
    FitTrackSection.BUTTONS -> "Componentes interactivos"
}

private fun contentDescription(section: FitTrackSection) = when (section) {
    FitTrackSection.PROFILE -> "Aquí se mostrarán nombre, objetivo y progreso semanal."
    FitTrackSection.PHOTOS -> "Prepara esta sección para cargar fotos y comparar avances."
    FitTrackSection.VIDEO -> "Espacio inicial para enlazar o reproducir videos de entrenamiento."
    FitTrackSection.WEB -> "Área preparada para abrir contenido web relacionado con salud y actividad."
    FitTrackSection.BUTTONS -> "Ejemplos de acciones primarias y secundarias para futuras pantallas."
}

private fun actionLabel(section: FitTrackSection) = when (section) {
    FitTrackSection.PROFILE -> "Editar perfil"
    FitTrackSection.PHOTOS -> "Agregar foto"
    FitTrackSection.VIDEO -> "Ver video"
    FitTrackSection.WEB -> "Abrir recurso"
    FitTrackSection.BUTTONS -> ""
}
