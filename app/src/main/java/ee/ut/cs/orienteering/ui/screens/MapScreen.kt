package ee.ut.cs.orienteering.ui.screens

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.core.graphics.createBitmap
import androidx.core.graphics.drawable.toDrawable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import ee.ut.cs.orienteering.R
import ee.ut.cs.orienteering.data.AppDatabase
import ee.ut.cs.orienteering.data.Quest
import ee.ut.cs.orienteering.data.network.RetrofitInstance
import ee.ut.cs.orienteering.data.network.WeatherRepository
import ee.ut.cs.orienteering.ui.components.QuestionRow
import ee.ut.cs.orienteering.ui.viewmodels.QuestionsViewModel
import ee.ut.cs.orienteering.ui.viewmodels.WeatherViewModel
import ee.ut.cs.orienteering.ui.viewmodels.WeatherViewModelFactory
import kotlinx.coroutines.launch
import org.osmdroid.config.Configuration
import org.osmdroid.events.MapEventsReceiver
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.CustomZoomButtonsController
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.MapEventsOverlay
import org.osmdroid.views.overlay.Marker

/**
 * Displays an interactive OSMDroid map inside a Compose UI and renders question markers
 * associated with a specific quest.
 *
 * Features:
 * - **Single tap:** triggers a weather lookup via [onWeatherTrigger].
 * - **Long press (editable mode only):** opens a dialog to add a new question at the pressed location.
 * - **Markers:** each question is shown as a numbered marker on the map.
 * - **Marker click:** scrolls the question list (via [listState]) to the corresponding item.
 *
 * Dialog behavior:
 * - When the user long‑presses in editable mode, a dialog appears allowing them to enter
 *   question text and an answer. Submitting the dialog calls
 *   [QuestionsViewModel.addQuestion] with the selected coordinates.
 *
 * @param onWeatherTrigger Callback invoked when the user taps the map. Receives the tapped [GeoPoint].
 * @param questionsViewModel The [QuestionsViewModel] providing question data and add‑question logic.
 * @param listState The [LazyListState] used to scroll the question list when a marker is tapped.
 * @param questId The ID of the quest whose questions should be displayed on the map.
 * @param editable Whether long‑pressing the map should allow adding new questions.
 */
@Composable
fun OSMDroidMapView(
    onWeatherTrigger: (GeoPoint) -> Unit,
    questionsViewModel: QuestionsViewModel,
    listState: LazyListState,
    questId: Int,
    editable: Boolean = false
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()


    val questions by questionsViewModel
        .questionsForQuest(questId)
        .collectAsState(initial = emptyList())

    val showAddDialog = remember { mutableStateOf(false) }
    val addingPoint = remember { mutableStateOf("") }
    val isDark = isSystemInDarkTheme()

    // Dialog for adding a new question
    if (showAddDialog.value) {
        var text by remember { mutableStateOf("") }
        var answer by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showAddDialog.value = false },
            title = { Text("Add question") },
            text = {
                Column {
                    OutlinedTextField(
                        value = text,
                        onValueChange = { text = it },
                        label = { Text("Question text") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = if (isSystemInDarkTheme()) {
                            OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.onPrimary,
                                unfocusedBorderColor = MaterialTheme.colorScheme.onSurface,
                                focusedLabelColor = MaterialTheme.colorScheme.onPrimary,
                                focusedTextColor = MaterialTheme.colorScheme.onSurface,
                                unfocusedTextColor = MaterialTheme.colorScheme.onSurface
                            )
                        } else {
                            OutlinedTextFieldDefaults.colors()
                        }
                    )
                    OutlinedTextField(
                        value = answer,
                        onValueChange = { answer = it },
                        label = { Text("Question answer") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = if (isSystemInDarkTheme()) {
                            OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.onPrimary,
                                unfocusedBorderColor = MaterialTheme.colorScheme.onSurface,
                                focusedLabelColor = MaterialTheme.colorScheme.onPrimary,
                                focusedTextColor = MaterialTheme.colorScheme.onSurface,
                                unfocusedTextColor = MaterialTheme.colorScheme.onSurface
                            )
                        } else {
                            OutlinedTextFieldDefaults.colors()
                        }
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        questionsViewModel.addQuestion(text.trim(), questId, addingPoint.value)
                        showAddDialog.value = false
                    },
                    enabled = text.isNotBlank(),
                    colors = if (isDark) ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.onSecondary) else ButtonDefaults.textButtonColors()
                ) { Text("Add") }
            },
            dismissButton = {
                TextButton(
                    onClick = { showAddDialog.value = false },
                    colors = if (isDark) ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.onSecondary) else ButtonDefaults.textButtonColors()
                ) { Text("Cancel") }
            }
        )
    }

    val colors = MaterialTheme.colorScheme

    AndroidView(
        factory = {
            Configuration.getInstance().load(context, androidx.preference.PreferenceManager.getDefaultSharedPreferences(context))
            val mapView = MapView(context)
            mapView.zoomController.setVisibility(CustomZoomButtonsController.Visibility.NEVER)
            mapView.setTileSource(TileSourceFactory.MAPNIK)
            mapView.setMultiTouchControls(true)
            mapView.controller.setZoom(15.0)
            mapView.controller.setCenter(GeoPoint(58.3776, 26.7290)) // Tartu center

            val mapEventsReceiver = object : MapEventsReceiver {
                override fun singleTapConfirmedHelper(p: GeoPoint): Boolean {

                    onWeatherTrigger(p) // Fetch weather data
                    return true
                }

                override fun longPressHelper(p: GeoPoint): Boolean {
                    if (editable) {
                        addingPoint.value = (p.latitude.toString() + "," + p.longitude.toString())
                        showAddDialog.value = true
                    }

                    return true
                }

            }

            val mapEventsOverlay = MapEventsOverlay(mapEventsReceiver)
            mapView.overlays.add(mapEventsOverlay)

            mapView
        },
        update = { mapView ->
            // add question markers
            questions.forEachIndexed { index, question ->
                val geoPoint = when {
                    question.location.isNotEmpty() -> {
                        val (lat, lon) = question.location.split(",").map { it.toDouble() }
                        GeoPoint(lat, lon)
                    }
                    else -> null
                }

                geoPoint?.let {
                    val icon = createNumberMarkerDrawable(context, question.id, colors.secondary,colors.onSecondary)

                    val marker = Marker(mapView).apply {
                        position = it
                        title = context.getString(R.string.map_marker_question_title, question.id)
                        this.icon = icon
                        setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)

                        // scrolling to the corresponding question
                        // doesn't scroll if there are not enough items in the list
                        setOnMarkerClickListener { _, _ ->
                            coroutineScope.launch {
                                listState.animateScrollToItem(index, 0)
                            }
                            true
                        }
                    }
                    mapView.overlays.add(marker)
                }
            }

            // refresh
            mapView.invalidate()
        },
        modifier = Modifier.fillMaxSize()
    )
}

/**
 * Displays the main map screen for a specific quest, combining:
 * - An interactive OSMDroid map
 * - A bottom sheet containing the list of questions
 * - Weather information for tapped map locations
 * - QR code generation for sharing the quest
 * - A leave‑lobby confirmation dialog
 *
 * Key behaviors:
 * - **Map interactions:**
 *   - Single tap triggers a weather lookup via [WeatherViewModel.loadWeather].
 *   - Long press (when `editable` is true) allows adding new questions.
 *
 * - **Bottom sheet:**
 *   - Shows the list of questions for the quest.
 *   - Collapsible, with a configurable peek height.
 *
 * - **QR code dialog:**
 *   - Generates a QR code for the quest using [QuestionsViewModel.generateQuestQrBitmap].
 *   - Allows saving the QR code to the device gallery.
 *
 * - **Leave dialog:**
 *   - Confirms whether the user wants to exit the lobby.
 *   - On confirmation, navigates back using [NavController.popBackStack].
 *
 * - **Weather display:**
 *   - Weather text is shown in the top‑right corner of the map.
 *
 * @param navController The [NavController] used for navigation actions.
 * @param questionsViewModel The [QuestionsViewModel] providing question data and QR generation.
 * @param questId The ID of the quest whose map and questions should be displayed.
 * @param editable Whether the user is allowed to add new questions via long‑press on the map.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(
    navController: NavController,
    questionsViewModel: QuestionsViewModel = viewModel(),
    questId: Int,
    editable: Boolean = false
) {
    val context = LocalContext.current
    val questDao = remember { AppDatabase.getDatabase(context).questDao() }
    val quest: Quest? by questDao.getQuestById(questId).collectAsState(initial = null)
    val weatherRepo = WeatherRepository(RetrofitInstance.api)
    val weatherViewModel: WeatherViewModel = viewModel(factory = WeatherViewModelFactory(weatherRepo))
    val weatherText by weatherViewModel.weatherText

    // UI state
    val listState = rememberLazyListState()
    val sheetState = rememberBottomSheetScaffoldState()
    val screenPadding = dimensionResource(R.dimen.screen_padding)

    val colors = MaterialTheme.colorScheme

    val showLeaveDialog = remember { mutableStateOf(false) }
    val showQrDialog = remember { mutableStateOf(false) }

    val isDark = isSystemInDarkTheme()

    val sheetHeight = dimensionResource(id = R.dimen.sheet_height_collapsed)
    val iconPadding = dimensionResource(id = R.dimen.icon_padding)
    val roundedCorners = dimensionResource(id = R.dimen.rounded_corners)
    val qrSize = dimensionResource(id = R.dimen.qr_size)
    val rowSpacing = dimensionResource(id = R.dimen.column_row_spacing)
    val columnPadding = dimensionResource(id = R.dimen.column_padding)

    // Disable back navigation
    BackHandler {}

    BottomSheetScaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(quest?.title ?: stringResource(R.string.quest_title, questId), style = MaterialTheme.typography.titleLarge)
                        quest?.code?.takeIf { it.isNotBlank() }?.let {
                            Text(stringResource(R.string.code, it), style = MaterialTheme.typography.labelMedium)
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { showLeaveDialog.value = true }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.btn_back)
                        )
                    }
                },
                actions = {
                    TextButton(onClick = { showLeaveDialog.value = true }) {
                        Text(stringResource(R.string.btn_leave), color = MaterialTheme.colorScheme.onPrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = colors.primary,
                    titleContentColor = colors.onPrimary,
                    actionIconContentColor = colors.onPrimary
                )
            )
        },

        scaffoldState = sheetState,
        sheetContent = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.8f)
                    .padding(screenPadding)
            ) {
                QuestionsList(viewModel = questionsViewModel, listState = listState, questId = questId)

            }
        },
        sheetPeekHeight = sheetHeight, // visible when collapsed
        sheetDragHandle = {
            BottomSheetDefaults.DragHandle()
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            OSMDroidMapView(
                questionsViewModel = questionsViewModel,
                onWeatherTrigger = { geoPoint ->
                    weatherViewModel.loadWeather(geoPoint.latitude, geoPoint.longitude)
                },
                listState = listState,
                questId = questId,
                editable = editable
            )

            // Display weather in top-right corner
            Text(
                text = weatherText ?: "",
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(screenPadding),
                style = MaterialTheme.typography.bodyMedium.copy(color = Color.Black)
            )

            IconButton(
                onClick = { showQrDialog.value = true },
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(iconPadding)
                    .background(colors.primary)
            ) {
                Icon(Icons.Default.QrCode,
                    contentDescription = "Share quest",
                    modifier = Modifier.background(colors.background))
            }
        }
    }
    if (showLeaveDialog.value) {
        AlertDialog(
            onDismissRequest = { showLeaveDialog.value = false },
            title = { Text("Leave lobby?") },
            text = { Text("You can come back later. Leave this lobby now?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showLeaveDialog.value = false
                        navController.popBackStack()
                    },
                    colors = if (isDark) ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.tertiary) else ButtonDefaults.textButtonColors()
                ) { Text("Leave") }
            },
            dismissButton = {
                TextButton(
                    onClick = { showLeaveDialog.value = false },
                    colors = if (isDark) ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.tertiary) else ButtonDefaults.textButtonColors()
                ) { Text("Cancel") }
            }
        )
    }
    if (showQrDialog.value) {
        Dialog(onDismissRequest = { showQrDialog.value = false }) {
            Surface(
                shape = RoundedCornerShape(roundedCorners),
                tonalElevation = 4.dp,
                color = MaterialTheme.colorScheme.surface
            ) {
                Column(
                    modifier = Modifier.padding(columnPadding),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Quest QR Code", style = MaterialTheme.typography.titleLarge)

                    Spacer(Modifier.height(rowSpacing))

                    val qrBitmap by produceState<Bitmap?>(initialValue = null, key1 = quest?.id) {
                        quest?.let { value = questionsViewModel.generateQuestQrBitmap(it) }
                    }

                    if (qrBitmap != null) {
                        Image(
                            bitmap = qrBitmap!!.asImageBitmap(),
                            contentDescription = "Quest QR",
                            modifier = Modifier.size(qrSize)
                        )

                        Spacer(Modifier.height(rowSpacing))

                        Button(onClick = {
                            questionsViewModel.saveQrToGallery(qrBitmap!!)
                        }) {
                            Text("Download code")
                        }
                    }
                }
            }
        }
    }


}

/**
 * Displays the list of questions for a given quest.
 *
 * Behavior:
 * - Observes the question list, checked states, and answer states from [QuestionsViewModel].
 * - If no questions exist, shows a placeholder text.
 * - Otherwise displays a scrollable list of [QuestionRow] items.
 * - Each row:
 *   - Shows the question text
 *   - Allows toggling the checked state
 *   - Allows editing the answer text
 * - Clicking a marker on the map scrolls this list using [listState].
 *
 * @param viewModel The [QuestionsViewModel] providing questions, checked states, and answers.
 * @param listState The [LazyListState] used for programmatic scrolling from the map.
 * @param questId The ID of the quest whose questions should be displayed.
 */
@Composable
fun QuestionsList(
    viewModel: QuestionsViewModel,
    listState: LazyListState,
    questId: Int
)

 {

     val questions by viewModel
         .questionsForQuest(questId)
         .collectAsState(initial = emptyList())

     val checked by viewModel.checked.collectAsState()
     val answers by viewModel.answers.collectAsState()

    if (questions.isEmpty()) {
        Text(stringResource(R.string.no_questions), modifier = Modifier.fillMaxWidth())
    } else {
        LazyColumn(state = listState) {
            itemsIndexed(
                questions,
                key = { _, q -> q.id }
            ) { index, q ->

                val isChecked = checked.contains(q.id)
                val answer = answers[q.id] ?: ""


                QuestionRow(
                    question = q,
                    isChecked = isChecked,
                    answerText = answer,
                    onCheckedToggle = { viewModel.toggleChecked(q.id) },
                    onAnswerChanged = { viewModel.updateAnswer(q.id, it) },
                    modifier = Modifier
                        .padding(5.dp)
                )

            }
            item {
                Spacer(modifier = Modifier.height(300.dp)) // to allow scrolling
            }
        }
    }
}


/**
 * Creates a circular numbered marker drawable used for map question markers.
 *
 * The marker consists of:
 * - A filled circle using [primaryColor]
 * - A centered bold number using [onPrimaryColor]
 *
 * @param context The Android context used to access resources.
 * @param number The number to display inside the marker (e.g., question ID).
 * @param primaryColor The background circle color.
 * @param onPrimaryColor The text color used for the number.
 * @param size The width/height of the drawable in pixels (default: 64).
 *
 * @return A [Drawable] representing the numbered marker.
 */
fun createNumberMarkerDrawable(
    context: Context,
    number: Int,
    primaryColor: Color,
    onPrimaryColor: Color,
    size: Int = 64
): Drawable {
    val bitmap = createBitmap(size, size)
    val canvas = Canvas(bitmap)

    val circlePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = primaryColor.toArgb()
        style = Paint.Style.FILL
    }

    val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = onPrimaryColor.toArgb()
        textSize = size * 0.5f
        textAlign = Paint.Align.CENTER
        typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
    }

    val cx = size / 2f
    val cy = size / 2f
    canvas.drawCircle(cx, cy, size / 2.3f, circlePaint)

    val yPos = cy - (textPaint.descent() + textPaint.ascent()) / 2
    canvas.drawText(number.toString(), cx, yPos, textPaint)

    return bitmap.toDrawable(context.resources)
}
