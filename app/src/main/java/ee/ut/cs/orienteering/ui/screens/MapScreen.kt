package ee.ut.cs.orienteering.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
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
import org.osmdroid.config.Configuration
import org.osmdroid.events.MapEventsReceiver
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.CustomZoomButtonsController
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.MapEventsOverlay
import org.osmdroid.views.overlay.Marker

@Composable
fun OSMDroidMapView(onLongPress: (GeoPoint) -> Unit) {
    val context = LocalContext.current

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
                    // Single tap function
                    return false
                }

                override fun longPressHelper(p: GeoPoint): Boolean {
                    val marker = Marker(mapView).apply {
                        position = p
                        title = "New Quest"
                        setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                    }
                    mapView.overlays.add(marker)
                    mapView.invalidate()
                    onLongPress(p) // Fetch weather data

                    return true
                }
            }

            val mapEventsOverlay = MapEventsOverlay(mapEventsReceiver)
            mapView.overlays.add(mapEventsOverlay)

            mapView
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
                    .fillMaxHeight(0.95f)
                    .padding(screenPadding)
            ) {
                QuestionsList(questionsViewModel)
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
            OSMDroidMapView(onLongPress = { geoPoint ->
                weatherViewModel.loadWeather(geoPoint.latitude, geoPoint.longitude)
            })

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
fun QuestionsList(viewModel: QuestionsViewModel) {

    val questions by viewModel.questions.collectAsState()
    val checked by viewModel.checked.collectAsState()
    val answers by viewModel.answers.collectAsState()

    LazyColumn {
        items(
            items = questions,
            key = { it.id }
        ) { q ->

            val isChecked = checked.contains(q.id)
            val answer = answers[q.id] ?: ""


            QuestionRow(
                question = q,
                isChecked = isChecked,
                answerText = answer,
                onCheckedToggle = { viewModel.toggleChecked(q.id) },
                onAnswerChanged = { viewModel.updateAnswer(q.id, it) },
                modifier = Modifier
                    .padding(5.dp))

        }
    }
}