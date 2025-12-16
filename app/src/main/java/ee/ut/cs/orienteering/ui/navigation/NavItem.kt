package ee.ut.cs.orienteering.ui.navigation

import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Represents a single navigation item used in the app's bottom or side navigation UI.
 *
 * Each item defines:
 * - A navigation route
 * - A user‑visible label
 * - An icon to display in the navigation bar
 *
 * @param route The navigation route string used by the NavController.
 * @param label The text label shown to the user.
 * @param icon The icon displayed for this navigation item.
 */
data class NavItem(
    val route: String,
    val label: String,
    val icon: ImageVector
)
