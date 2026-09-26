package com.example.myfittrack.ui

import android.os.Bundle
import android.content.Intent
import android.net.Uri
import android.view.View
import android.widget.ImageView
import android.widget.MediaController
import android.widget.VideoView
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Surface
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import androidx.compose.runtime.remember
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import com.example.myfittrack.model.FitTrackSection
import com.example.myfittrack.R
import com.example.myfittrack.ui.theme.MyFitTrackTheme

class ContentFragment : Fragment() {
    private var currentSection by mutableStateOf(FitTrackSection.PROFILE)
    private var photoUris by mutableStateOf<List<String>>(emptyList())
    private var videoUri by mutableStateOf<String?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val preferences = requireContext().getSharedPreferences(PREFERENCES_NAME, android.content.Context.MODE_PRIVATE)
        photoUris = savedInstanceState?.getStringArrayList(PHOTO_URIS_KEY)?.toList()
            ?: preferences.getString(PHOTO_URIS_KEY, null)?.split('\n')?.filter(String::isNotBlank).orEmpty()
        videoUri = savedInstanceState?.getString(VIDEO_URI_KEY) ?: preferences.getString(VIDEO_URI_KEY, null)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putStringArrayList(PHOTO_URIS_KEY, ArrayList(photoUris))
        outState.putString(VIDEO_URI_KEY, videoUri)
        super.onSaveInstanceState(outState)
    }

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
            MyFitTrackTheme(dynamicColor = false) {
                ContentPanel(
                    currentSection,
                    photoUris,
                    videoUri,
                    onPhotosChanged = { photos ->
                        photoUris = photos
                        requireContext().getSharedPreferences(PREFERENCES_NAME, android.content.Context.MODE_PRIVATE)
                            .edit().putString(PHOTO_URIS_KEY, photos.joinToString("\n")).apply()
                    },
                    onVideoChanged = { video ->
                        videoUri = video
                        val editor = requireContext().getSharedPreferences(PREFERENCES_NAME, android.content.Context.MODE_PRIVATE).edit()
                        if (video == null) editor.remove(VIDEO_URI_KEY) else editor.putString(VIDEO_URI_KEY, video)
                        editor.apply()
                    }
                )
            }
        }
    }

    private companion object {
        const val PREFERENCES_NAME = "fittrack_media"
        const val PHOTO_URIS_KEY = "fittrack_photo_uris"
        const val VIDEO_URI_KEY = "fittrack_video_uri"
    }
}

@Composable
private fun ContentPanel(
    section: FitTrackSection,
    photoUris: List<String>,
    videoUri: String?,
    onPhotosChanged: (List<String>) -> Unit,
    onVideoChanged: (String?) -> Unit
) {
    val stateHolder = rememberSaveableStateHolder()
    when (section) {
        FitTrackSection.PROFILE -> stateHolder.SaveableStateProvider(section.name) { ProfileScreen() }
        FitTrackSection.PHOTOS -> PhotoGallery(photoUris, onPhotosChanged)
        FitTrackSection.VIDEO -> VideoScreen(videoUri, onVideoChanged)
        FitTrackSection.WEB -> WebResourcesScreen()
        FitTrackSection.BUTTONS -> stateHolder.SaveableStateProvider(section.name) { ButtonsScreen() }
    }
}

@Composable
private fun ButtonsScreen() {
    val navy = Color(0xFF14213D)
    val muted = Color(0xFF617087)
    val accent = Color(0xFF2EC4B6)
    val compact = LocalConfiguration.current.screenWidthDp < 600
    var draft by rememberSaveable { mutableStateOf("") }
    var savedMessage by rememberSaveable { mutableStateOf<String?>(null) }
    var feedback by rememberSaveable { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier.fillMaxSize()
            .background(Color(0xFFF7F8FA))
            .heightIn(max = 2000.dp)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = if (compact) 12.dp else 28.dp, vertical = if (compact) 16.dp else 24.dp),
        verticalArrangement = Arrangement.spacedBy(if (compact) 12.dp else 18.dp)
    ) {
        Text("Botones", color = navy, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Text("Acciones rápidas para registrar tus hábitos", color = muted, style = MaterialTheme.typography.bodyLarge)
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(Modifier.fillMaxWidth().padding(if (compact) 12.dp else 24.dp), verticalArrangement = Arrangement.spacedBy(if (compact) 10.dp else 16.dp)) {
                Text("Nota de bienestar", color = navy, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)
                Text("Registra un objetivo o una observación para tu próxima sesión.", color = muted)
                OutlinedTextField(
                    value = draft,
                    onValueChange = { draft = it; feedback = null },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Escribe tu nota") },
                    minLines = 3
                )
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Button(
                        modifier = Modifier.weight(1f),
                        onClick = {
                            if (draft.isBlank()) {
                                feedback = "Escribe una nota antes de guardarla."
                            } else {
                                savedMessage = draft.trim()
                                draft = ""
                                feedback = "Nota guardada."
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = accent)
                    ) { Text("Guardar") }
                    OutlinedButton(modifier = Modifier.weight(1f), onClick = {
                        draft = ""
                        feedback = "Edición cancelada."
                    }) { Text("Cancelar") }
                }
                feedback?.let { Text(it, color = if (it == "Nota guardada.") accent else muted, style = MaterialTheme.typography.bodyMedium) }
            }
        }
        savedMessage?.let { message ->
            Surface(color = Color(0xFFE9F8F6), shape = RoundedCornerShape(16.dp)) {
                Column(Modifier.fillMaxWidth().padding(20.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Nota guardada", color = navy, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                    Text(message, color = muted)
                }
            }
        }
    }
}

@Composable
private fun WebResourcesScreen() {
    val navy = Color(0xFF14213D)
    val muted = Color(0xFF617087)
    val compact = LocalConfiguration.current.screenWidthDp < 600
    var webView by remember { mutableStateOf<WebView?>(null) }
    var address by rememberSaveable { mutableStateOf("https://www.who.int/es/news-room/fact-sheets/detail/physical-activity") }
    var isLoading by remember { mutableStateOf(true) }
    var loadError by remember { mutableStateOf<String?>(null) }

    DisposableEffect(Unit) {
        onDispose {
            webView?.stopLoading()
            webView?.destroy()
            webView = null
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().background(Color(0xFFF7F8FA)).padding(horizontal = if (compact) 12.dp else 28.dp, vertical = if (compact) 16.dp else 24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Web", color = navy, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Text("Recursos de bienestar y actividad física", color = muted, style = MaterialTheme.typography.bodyLarge)
        Text("Página web", color = navy, style = if (compact) MaterialTheme.typography.titleSmall else MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            OutlinedTextField(
                value = address,
                onValueChange = { address = it },
                modifier = Modifier.weight(1f),
                singleLine = true,
                label = { Text("URL") }
            )
            Button(onClick = {
                val target = if (address.startsWith("http://") || address.startsWith("https://")) address else "https://$address"
                address = target
                loadError = null
                isLoading = true
                webView?.loadUrl(target)
            }, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2EC4B6))) { Text("Ir") }
        }
        Card(
            modifier = Modifier.fillMaxWidth().weight(1f),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Box(Modifier.fillMaxSize()) {
                AndroidView(
                    factory = { context ->
                        WebView(context).apply {
                            webView = this
                            settings.javaScriptEnabled = false
                            settings.domStorageEnabled = false
                            settings.allowFileAccess = false
                            settings.allowContentAccess = false
                            settings.setSupportMultipleWindows(false)
                            webViewClient = object : WebViewClient() {
                                override fun shouldOverrideUrlLoading(view: WebView, request: android.webkit.WebResourceRequest): Boolean =
                                    openExternalUrl(context, request.url)

                                @Suppress("DEPRECATION")
                                override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean =
                                    openExternalUrl(context, Uri.parse(url))

                                override fun onPageStarted(view: WebView, url: String, favicon: android.graphics.Bitmap?) {
                                    isLoading = true
                                    loadError = null
                                }

                                override fun onPageFinished(view: WebView, url: String) {
                                    isLoading = false
                                }

                                override fun onReceivedError(
                                    view: WebView,
                                    request: android.webkit.WebResourceRequest,
                                    error: android.webkit.WebResourceError
                                ) {
                                    if (request.isForMainFrame) {
                                        isLoading = false
                                        loadError = error.description.toString()
                                    }
                                }
                            }
                            loadUrl(address)
                        }
                    },
                    modifier = Modifier.fillMaxSize()
                )
                if (isLoading) {
                    Column(
                        modifier = Modifier.fillMaxSize().background(Color.White),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        CircularProgressIndicator(color = Color(0xFF2EC4B6))
                        Spacer(Modifier.height(12.dp))
                        Text("Cargando contenido de la OMS…", color = muted)
                    }
                }
                loadError?.let { error ->
                    Column(
                        modifier = Modifier.fillMaxSize().background(Color.White).padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text("No se pudo cargar el recurso web", color = navy, style = MaterialTheme.typography.titleMedium)
                        Spacer(Modifier.height(8.dp))
                        Text(error, color = muted, style = MaterialTheme.typography.bodySmall)
                        Spacer(Modifier.height(12.dp))
                        Button(onClick = {
                            loadError = null
                            isLoading = true
                            webView?.reload()
                        }, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2EC4B6))) {
                            Text("Intentar de nuevo")
                        }
                    }
                }
            }
        }
    }
}

private fun openExternalUrl(context: android.content.Context, target: Uri): Boolean {
    if (target.scheme !in setOf("http", "https")) return true
    val host = target.host.orEmpty()
    if (host == "who.int" || host.endsWith(".who.int")) return false
    return try {
        context.startActivity(Intent(Intent.ACTION_VIEW, target))
        true
    } catch (_: android.content.ActivityNotFoundException) {
        true
    }
}

@Composable
private fun VideoScreen(videoUri: String?, onVideoChanged: (String?) -> Unit) {
    val navy = Color(0xFF14213D)
    val muted = Color(0xFF617087)
    val accent = Color(0xFF2EC4B6)
    val compact = LocalConfiguration.current.screenWidthDp < 600
    val context = androidx.compose.ui.platform.LocalContext.current
    val picker = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { selected ->
        if (selected != null) {
            try {
                context.contentResolver.takePersistableUriPermission(selected, Intent.FLAG_GRANT_READ_URI_PERMISSION)
            } catch (_: SecurityException) {
                // The player can still use the temporary URI grant for this app session.
            }
            onVideoChanged(selected.toString())
        }
    }
    val chooseVideo = { picker.launch(arrayOf("video/*")) }

    Column(
        modifier = Modifier.fillMaxSize()
            .background(Color(0xFFF7F8FA))
            .heightIn(max = 2000.dp)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = if (compact) 12.dp else 28.dp, vertical = if (compact) 16.dp else 24.dp),
        verticalArrangement = Arrangement.spacedBy(if (compact) 12.dp else 18.dp)
    ) {
        Text("Video", color = navy, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Text("Rutinas y contenido audiovisual", color = muted, style = MaterialTheme.typography.bodyLarge)

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(if (compact) 12.dp else 16.dp), modifier = Modifier.padding(if (compact) 12.dp else 20.dp)) {
                if (videoUri == null) {
                    Column(
                        modifier = Modifier.fillMaxWidth().height(if (compact) 170.dp else 260.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(navy)
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text("▶", color = accent, style = MaterialTheme.typography.displayMedium)
                        Spacer(Modifier.height(12.dp))
                        Text("ATHLEAN-X Español", color = Color.White, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                    }
                } else {
                    AndroidView(
                        factory = { viewContext ->
                            VideoView(viewContext).apply {
                                val controller = MediaController(viewContext)
                                controller.setAnchorView(this)
                                setMediaController(controller)
                            }
                        },
                        update = { player ->
                            if (player.tag != videoUri) {
                                player.tag = videoUri
                                player.setVideoURI(Uri.parse(videoUri))
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(if (compact) 170.dp else 260.dp)
                            .clip(RoundedCornerShape(16.dp))
                    )
                }
                Text(
                    if (videoUri == null) "Rutina perfecta de cuerpo completo" else Uri.parse(videoUri).lastPathSegment ?: "Video seleccionado",
                    color = navy,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    if (videoUri == null) "Video de ATHLEAN-X Español. Ábrelo para reproducirlo con sus controles." else "Usa los controles del reproductor para iniciar, pausar o recorrer el video.",
                    color = muted,
                    style = MaterialTheme.typography.bodyMedium
                )
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    if (videoUri == null) {
                        Button(
                            onClick = { openExternalUrl(context, Uri.parse(ATHLEAN_VIDEO_URL)) },
                            colors = ButtonDefaults.buttonColors(containerColor = accent)
                        ) { Text("Ver video en YouTube") }
                    }
                    Button(onClick = chooseVideo, colors = ButtonDefaults.buttonColors(containerColor = if (videoUri == null) navy else accent)) {
                        Text(if (videoUri == null) "Elegir video del dispositivo" else "Cambiar video")
                    }
                    if (videoUri != null) {
                        TextButton(onClick = {
                            val uri = Uri.parse(videoUri)
                            try {
                                context.contentResolver.releasePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION)
                            } catch (_: SecurityException) {
                                // Temporary grants do not need to be released.
                            }
                            onVideoChanged(null)
                        }) { Text("Quitar", color = muted) }
                    }
                }
            }
        }
    }
}

private const val ATHLEAN_VIDEO_URL = "https://www.youtube.com/watch?v=8V57EbWrDzI"

private data class ExerciseItem(
    val imageRes: Int,
    val name: String,
    val muscleGroup: String,
    val description: String
)

private val EXERCISES = listOf(
    ExerciseItem(R.drawable.exercise_pullups, "Dominadas", "Espalda", "Cuelga de la barra con agarre firme y eleva el pecho hasta acercar la barbilla. Desciende con control."),
    ExerciseItem(R.drawable.exercise_incline_press, "Press inclinado", "Pecho", "En banco inclinado, baja la barra hacia la parte alta del pecho y empuja sin perder el apoyo de los pies."),
    ExerciseItem(R.drawable.exercise_squat, "Sentadilla", "Piernas", "Lleva la cadera atrás y flexiona las rodillas manteniendo la espalda neutra y las rodillas alineadas con los pies."),
    ExerciseItem(R.drawable.exercise_dumbbell_row, "Remo con mancuerna", "Espalda", "Apoya una mano en el banco y lleva la mancuerna hacia la cadera con el torso estable."),
    ExerciseItem(R.drawable.exercise_plank, "Plancha", "Core", "Apoya antebrazos y puntas de los pies; mantén el cuerpo alineado y el abdomen activo.")
)

@Composable
private fun PhotoGallery(photoUris: List<String>, onPhotosChanged: (List<String>) -> Unit) {
    val navy = Color(0xFF14213D)
    val muted = Color(0xFF617087)
    val accent = Color(0xFF2EC4B6)
    val compact = LocalConfiguration.current.screenWidthDp < 600
    var selectedPhoto by rememberSaveable { mutableStateOf<String?>(null) }
    var selectedExercise by rememberSaveable { mutableStateOf<String?>(null) }
    val context = androidx.compose.ui.platform.LocalContext.current
    val picker = rememberLauncherForActivityResult(ActivityResultContracts.OpenMultipleDocuments()) { selected ->
        selected.forEach { uri ->
            try {
                context.contentResolver.takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION)
            } catch (_: SecurityException) {
                // The gallery can still use the temporary URI grant for this app session.
            }
        }
        onPhotosChanged((photoUris + selected.map(Uri::toString)).distinct())
    }

    Column(
        modifier = Modifier.fillMaxSize()
            .background(Color(0xFFF7F8FA))
            .heightIn(max = 2000.dp)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = if (compact) 12.dp else 28.dp, vertical = if (compact) 16.dp else 24.dp),
        verticalArrangement = Arrangement.spacedBy(if (compact) 12.dp else 18.dp)
    ) {
        Text("Fotos", color = navy, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Text("Registra tu progreso visual", color = muted, style = MaterialTheme.typography.bodyLarge)
        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text("Ejercicios básicos", color = navy, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)
            Text("${EXERCISES.size} ejercicios", color = muted, style = MaterialTheme.typography.bodySmall)
        }
        Text("Toca una imagen para ver cómo se realiza", color = muted, style = MaterialTheme.typography.bodySmall)
        EXERCISES.forEach { exercise ->
            Card(
                modifier = Modifier.fillMaxWidth().clickable { selectedExercise = exercise.name },
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = if (selectedExercise == exercise.name) BorderStroke(2.dp, accent) else null
            ) {
                Column {
                    Image(
                        painter = painterResource(exercise.imageRes),
                        contentDescription = "${exercise.name}, ejercicio para ${exercise.muscleGroup.lowercase()}",
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.fillMaxWidth().height(if (compact) 170.dp else 230.dp).background(Color(0xFFE9EDF2))
                    )
                    Column(Modifier.fillMaxWidth().padding(if (compact) 12.dp else 16.dp), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                        Text(exercise.name, color = navy, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                        Text(exercise.muscleGroup, color = muted, style = MaterialTheme.typography.bodySmall)
                    }
                    if (selectedExercise == exercise.name) {
                        Surface(color = Color(0xFFE9F8F6)) {
                            Column(Modifier.fillMaxWidth().padding(if (compact) 12.dp else 16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                Text("Descripción del ejercicio", color = navy, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                                Text(exercise.description, color = muted, style = MaterialTheme.typography.bodySmall)
                            }
                        }
                    }
                }
            }
        }
        HorizontalDivider(color = Color(0xFFE1E5EA))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text("Mis fotos de progreso", color = navy, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)
            Text("${photoUris.size} ${if (photoUris.size == 1) "foto" else "fotos"}", color = muted)
        }
        if (photoUris.isEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, Color(0xFFDCE4EA))
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(if (compact) 14.dp else 32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text("▧", color = muted, style = MaterialTheme.typography.displayMedium)
                    Text("Aún no hay fotos", color = navy, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)
                    Text("Elige imágenes de tu dispositivo para empezar tu bitácora visual.", color = muted)
                    Button(onClick = { picker.launch(arrayOf("image/*")) }, colors = ButtonDefaults.buttonColors(containerColor = accent)) {
                        Text("Agregar fotos")
                    }
                }
            }
        } else {
            Button(onClick = { picker.launch(arrayOf("image/*")) }, colors = ButtonDefaults.buttonColors(containerColor = accent)) {
                Text("Agregar fotos")
            }
            photoUris.chunked(if (compact) 1 else 2).forEach { rowPhotos ->
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                    rowPhotos.forEach { uriString ->
                        Card(
                            modifier = Modifier.weight(1f).clickable { selectedPhoto = uriString },
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White)
                        ) {
                            Column {
                                AndroidView(
                                    factory = { viewContext -> ImageView(viewContext).apply { scaleType = ImageView.ScaleType.CENTER_CROP } },
                                    update = { imageView -> imageView.setImageURI(Uri.parse(uriString)) },
                                    modifier = Modifier.fillMaxWidth().height(if (compact) 170.dp else 190.dp)
                                )
                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(horizontal = 10.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("Progreso", color = navy, style = MaterialTheme.typography.labelLarge)
                                    TextButton(onClick = {
                                        val uri = Uri.parse(uriString)
                                        try {
                                            context.contentResolver.releasePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                        } catch (_: SecurityException) {
                                            // Temporary grants do not need to be released.
                                        }
                                        onPhotosChanged(photoUris - uriString)
                                    }) { Text("Quitar", color = muted) }
                                }
                            }
                        }
                    }
                    if (rowPhotos.size == 1) Spacer(Modifier.weight(1f))
                }
            }
            selectedPhoto?.let { uriString ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(Modifier.fillMaxWidth().padding(if (compact) 12.dp else 18.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Descripción de la imagen", color = navy, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                        Text("Imagen de progreso seleccionada desde el dispositivo.", color = muted)
                        Text(Uri.parse(uriString).lastPathSegment ?: "Foto de progreso", color = muted, style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }
    }
}

@Composable
private fun ProfileScreen() {
    val navy = Color(0xFF14213D)
    val muted = Color(0xFF617087)
    val accent = Color(0xFF2EC4B6)
    val compact = LocalConfiguration.current.screenWidthDp < 600
    var editing by rememberSaveable { mutableStateOf(false) }
    var name by rememberSaveable { mutableStateOf("Alex García") }
    var degree by rememberSaveable { mutableStateOf("Ingeniería de Sistemas") }
    var institution by rememberSaveable { mutableStateOf("Universidad Nacional") }
    var experience by rememberSaveable { mutableStateOf("Desarrollo de aplicaciones móviles · 1 año") }
    var draftName by rememberSaveable { mutableStateOf(name) }
    var draftDegree by rememberSaveable { mutableStateOf(degree) }
    var draftInstitution by rememberSaveable { mutableStateOf(institution) }
    var draftExperience by rememberSaveable { mutableStateOf(experience) }

    if (editing) {
        AlertDialog(
            onDismissRequest = { editing = false },
            title = { Text("Editar perfil", color = navy) },
            text = {
                Column(
                    modifier = Modifier.verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(draftName, { draftName = it }, label = { Text("Nombre") }, singleLine = true)
                    OutlinedTextField(draftDegree, { draftDegree = it }, label = { Text("Formación académica") })
                    OutlinedTextField(draftInstitution, { draftInstitution = it }, label = { Text("Institución") })
                    OutlinedTextField(draftExperience, { draftExperience = it }, label = { Text("Experiencia") })
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    name = draftName.ifBlank { name }
                    degree = draftDegree.ifBlank { degree }
                    institution = draftInstitution.ifBlank { institution }
                    experience = draftExperience.ifBlank { experience }
                    editing = false
                }) { Text("Guardar", color = accent) }
            },
            dismissButton = { TextButton(onClick = { editing = false }) { Text("Cancelar") } }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF7F8FA))
            .heightIn(max = 2000.dp)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = if (compact) 12.dp else 28.dp, vertical = if (compact) 16.dp else 24.dp),
        verticalArrangement = Arrangement.spacedBy(if (compact) 12.dp else 18.dp)
    ) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Top) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text("Perfil", color = navy, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                Text("Datos personales, formación y experiencia", color = muted, style = MaterialTheme.typography.bodyLarge)
            }
            if (!compact) Text("¡Hola, Alex!\nUn día mejor, siempre.", color = muted, style = MaterialTheme.typography.bodySmall)
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(if (compact) 12.dp else 24.dp), verticalArrangement = Arrangement.spacedBy(if (compact) 14.dp else 20.dp)) {
                val openEditor = {
                    draftName = name
                    draftDegree = degree
                    draftInstitution = institution
                    draftExperience = experience
                    editing = true
                }
                if (compact) {
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = painterResource(R.drawable.fittrack_profile_avatar),
                            contentDescription = "Ilustración de perfil de $name",
                            modifier = Modifier.size(56.dp).clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(name, color = navy, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                            Text("Disciplina hoy, resultados mañana.", color = muted, style = MaterialTheme.typography.bodySmall)
                            Text("22 años · Bogotá, Colombia", color = muted, style = MaterialTheme.typography.bodySmall)
                        }
                    }
                    Button(onClick = openEditor, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = accent)) {
                        Text("Editar perfil")
                    }
                } else {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(20.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            painter = painterResource(R.drawable.fittrack_profile_avatar),
                            contentDescription = "Ilustración de perfil de $name",
                            modifier = Modifier.size(112.dp).clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(name, color = navy, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                            Text("Disciplina hoy, resultados mañana.", color = muted, style = MaterialTheme.typography.bodyMedium)
                            Text("22 años  ·  Bogotá, Colombia", color = muted, style = MaterialTheme.typography.bodyMedium)
                        }
                        Button(onClick = openEditor, colors = ButtonDefaults.buttonColors(containerColor = accent)) {
                            Text("Editar perfil")
                        }
                    }
                }
                HorizontalDivider(color = Color(0xFFE7EBF0))
                Text("Datos académicos", color = navy, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                InfoCard("🎓", "Formación", degree, institution)
                InfoCard("📚", "En curso", "Séptimo semestre", "Interés en desarrollo móvil y tecnología para la salud")
                Text("Experiencia", color = navy, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                InfoCard("💼", "Experiencia profesional", experience, "Participación en proyectos académicos y trabajo colaborativo")
                InfoCard("🏃", "Actividad", "Hábitos saludables", "Entrenamiento de fuerza y seguimiento de objetivos personales")
                Surface(color = Color(0xFFE9F8F6), shape = RoundedCornerShape(16.dp)) {
                    Column(Modifier.fillMaxWidth().padding(18.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text("Mi objetivo", color = navy, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                        Text("Mantener el equilibrio entre estudio, experiencia profesional y bienestar.", color = muted)
                    }
                }
            }
        }
    }
}

@Composable
private fun InfoCard(icon: String, title: String, primary: String, secondary: String) {
    Row(
        modifier = Modifier.fillMaxWidth().background(Color(0xFFF7F9FB), RoundedCornerShape(14.dp)).padding(if (LocalConfiguration.current.screenWidthDp < 600) 10.dp else 16.dp),
        horizontalArrangement = Arrangement.spacedBy(if (LocalConfiguration.current.screenWidthDp < 600) 8.dp else 14.dp),
        verticalAlignment = Alignment.Top
    ) {
        Text(icon, style = MaterialTheme.typography.titleLarge)
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(title, color = Color(0xFF617087), style = MaterialTheme.typography.labelLarge)
            Text(primary, color = Color(0xFF14213D), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            Text(secondary, color = Color(0xFF617087), style = MaterialTheme.typography.bodyMedium)
        }
    }
}
