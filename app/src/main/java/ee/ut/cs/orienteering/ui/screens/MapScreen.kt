package ee.ut.cs.orienteering.ui.screens

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.graphics.createBitmap
import androidx.core.graphics.drawable.toDrawable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import ee.ut.cs.orienteering.R
import ee.ut.cs.orienteering.data.network.RetrofitInstance
import ee.ut.cs.orienteering.data.network.WeatherRepository
import ee.ut.cs.orienteering.ui.components.QuestionRow
import ee.ut.cs.orienteering.ui.viewmodels.JoinLobbyViewModel
import ee.ut.cs.orienteering.ui.viewmodels.MapViewModel
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
    listState: LazyListState
    ) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val questions by questionsViewModel.questions.collectAsState()

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

                    return false
                }

                override fun longPressHelper(p: GeoPoint): Boolean {
                    onWeatherTrigger(p) // Fetch weather data

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
                        title = "Question ${question.id}"
                        this.icon = icon
                        setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)

                        // scrolling to the corresponding question
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
    viewModel: MapViewModel = viewModel(),
    questionsViewModel: QuestionsViewModel = viewModel(),
    questsViewModel: JoinLobbyViewModel = viewModel(),
    questId: Int
) {
    val weatherRepo = WeatherRepository(RetrofitInstance.api)
    val weatherViewModel: WeatherViewModel = viewModel(factory = WeatherViewModelFactory(weatherRepo))
    val weatherText by weatherViewModel.weatherText

    val listState = rememberLazyListState()

    val colors = MaterialTheme.colorScheme

    // Disable back navigation
//    BackHandler {}


    val screenPadding = dimensionResource(R.dimen.screen_padding)

    val sheetState = rememberBottomSheetScaffoldState()

    BottomSheetScaffold(
        topBar = {
            TopAppBar(
            title = { Text("Quest nr $questId", style = MaterialTheme.typography.titleLarge) },
            navigationIcon = {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "About",
                        tint = colors.onPrimary
                    )
                }
            },
                actions = {
                // button to submit the answers?
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
                QuestionsList(questionsViewModel, listState)
            }
        },
        sheetPeekHeight = 150.dp, // visible when collapsed
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
                weatherViewModel.loadWeather(geoPoint.latitude, geoPoint.longitude)},
                listState = listState
            )

            // Display weather in top-right corner
            Text(
                text = weatherText ?: "",
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(screenPadding),
                style = MaterialTheme.typography.bodyMedium.copy(color = Color.Black)
            )
        }
    }
}

@Composable
fun QuestionsList(viewModel: QuestionsViewModel, listState: LazyListState) {

    val questions by viewModel.questions.collectAsState()
    val checked by viewModel.checked.collectAsState()
    val answers by viewModel.answers.collectAsState()

    if (questions.isEmpty()) {
        Text("No questions yet for this lobby.", modifier = Modifier.fillMaxWidth())
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
