package com.example.myfittrack.ui

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import com.example.myfittrack.model.FitTrackSection
import com.example.myfittrack.ui.theme.MyFitTrackTheme

class MenuFragment : Fragment() {
    interface SectionSelectionListener {
        fun onSectionSelected(section: FitTrackSection)
    }

    private var listener: SectionSelectionListener? = null
    private var selected by mutableStateOf(FitTrackSection.PROFILE)

    override fun onAttach(context: Context) {
        super.onAttach(context)
        listener = context as? SectionSelectionListener
            ?: error("La actividad debe implementar SectionSelectionListener")
    }

    override fun onCreateView(
        inflater: android.view.LayoutInflater,
        container: android.view.ViewGroup?,
        savedInstanceState: Bundle?
    ): View = ComposeView(requireContext()).apply {
        setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
        setContent {
            MyFitTrackTheme(dynamicColor = false) {
                val onSelected: (FitTrackSection) -> Unit = { section ->
                    selected = section
                    listener?.onSectionSelected(section)
                }
                if (LocalConfiguration.current.screenWidthDp < 600) {
                    CompactMenuPanel(selected, onSelected)
                } else {
                    MenuPanel(selected, onSelected)
                }
            }
        }
    }

    override fun onDetach() {
        listener = null
        super.onDetach()
    }
}

@Composable
private fun CompactMenuPanel(selected: FitTrackSection, onSelected: (FitTrackSection) -> Unit) {
    val navy = Color(0xFF14213D)
    Column(
        modifier = Modifier.fillMaxSize()
            .background(navy)
            .padding(start = 8.dp, end = 8.dp, top = 30.dp, bottom = 10.dp),
        verticalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        Text("FITTRACK", color = Color.White, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
        Text("Bienestar", color = Color(0xFFB9C6DE), style = MaterialTheme.typography.labelSmall)
        Spacer(Modifier.height(14.dp))
        FitTrackSection.entries.forEach { section ->
            val active = section == selected
            Row(
                modifier = Modifier.fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (active) Color(0xFF2EC4B6) else Color.Transparent)
                    .clickable { onSelected(section) }
                    .padding(horizontal = 7.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(section.emoji, color = Color.White, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelMedium)
                Text(section.label, color = Color.White, fontWeight = if (active) FontWeight.Bold else FontWeight.Medium, style = MaterialTheme.typography.labelMedium, maxLines = 1)
            }
        }
        Spacer(Modifier.weight(1f))
        Text("Entrega 2", color = Color(0xFF8EA1C1), style = MaterialTheme.typography.labelSmall)
    }
}

@Composable
private fun MenuPanel(selected: FitTrackSection, onSelected: (FitTrackSection) -> Unit) {
    val navy = Color(0xFF14213D)
    Column(
        modifier = Modifier
            .fillMaxHeight()
            .background(navy)
            .padding(horizontal = 18.dp, vertical = 30.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text("FITTRACK", color = Color.White, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.headlineSmall)
        Text("Tu bienestar, en movimiento", color = Color(0xFFB9C6DE), style = MaterialTheme.typography.bodySmall)
        Spacer(Modifier.height(28.dp))
        FitTrackSection.entries.forEach { section ->
            val active = section == selected
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(if (active) Color(0xFF2EC4B6) else Color.Transparent)
                    .clickable { onSelected(section) }
                    .padding(horizontal = 14.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(section.emoji, color = Color.White, fontWeight = FontWeight.Bold)
                Spacer(Modifier.width(12.dp))
                Text(section.label, color = Color.White, fontWeight = if (active) FontWeight.Bold else FontWeight.Medium)
            }
        }
        Spacer(Modifier.weight(1f))
        Text("Entrega 2 · Perfil y bienestar", color = Color(0xFF8EA1C1), style = MaterialTheme.typography.bodySmall)
    }
}
