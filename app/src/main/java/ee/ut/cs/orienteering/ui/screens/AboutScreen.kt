package ee.ut.cs.orienteering.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import ee.ut.cs.orienteering.R
import ee.ut.cs.orienteering.ui.viewmodels.AboutViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutScreen(
    navController: NavController,
    viewModel: AboutViewModel = viewModel()
) {
    val colors = MaterialTheme.colorScheme

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.about))},
                navigationIcon = {
                    IconButton(onClick = { viewModel.onBackClicked(navController) }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.btn_back)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = colors.primary,
                    titleContentColor = colors.onPrimary,
                    actionIconContentColor = colors.onPrimary
                )
            )
        }
    ) { innerPadding ->
        val screenPadding = dimensionResource(id = R.dimen.screen_padding)
        val textBlockSpacing = dimensionResource(id = R.dimen.text_block_spacing)
        val textRowPadding = dimensionResource(id = R.dimen.text_row_padding)
        val textRowPaddingSmall = dimensionResource(id = R.dimen.text_row_padding_small)

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .padding(innerPadding).padding(screenPadding)
                    .fillMaxSize(),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = "Our Team",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(bottom = textRowPadding)
                )

                val teamMembers = listOf(
                    "Mark Tarnovski",
                    "Liselle Velner",
                    "Angelina Zhumadilova",
                    "Erik Kippus"
                )

                teamMembers.forEach { member ->
                    Text(
                        text = "• $member",
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(start = textRowPadding, bottom = textRowPaddingSmall)
                    )
                }

                Spacer(modifier = Modifier.height(textBlockSpacing))

                Text(
                    text = "Core App Features",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(bottom = textRowPadding)
                )

                val descriptionLines = listOf(
                    "Creating maps with quests/questions tied to chosen location",
                    "Creating and managing lobbies for orienteering events",
                    "Signing up and participating in organized orienteering events"
                )

                descriptionLines.forEach { line ->
                    Text(
                        text = "• $line",
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(start = textRowPadding, bottom = textRowPaddingSmall)
                    )
                }
            }
        }
    }
}
