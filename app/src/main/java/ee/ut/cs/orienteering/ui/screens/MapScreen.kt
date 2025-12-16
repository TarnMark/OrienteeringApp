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
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = answer,
                        onValueChange = { answer = it },
                        label = { Text("Question answer") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        questionsViewModel.addQuestion(text.trim(), questId, addingPoint.value)
                        showAddDialog.value = false
                    },
                    enabled = text.isNotBlank()
                ) { Text("Add") }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog.value = false }) { Text("Cancel") }
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


// function for drawing the question number icons
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
