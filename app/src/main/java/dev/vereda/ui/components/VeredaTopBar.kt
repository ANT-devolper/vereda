package dev.vereda.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/**
 * App bar with a title and a back button on the left.
 *
 * The back affordance is the standard Android back arrow (`Icons.AutoMirrored.Filled.ArrowBack`,
 * from `material-icons-core`, so no `material-icons-extended` dependency is needed).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VeredaTopBar(
    title: String,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    TopAppBar(
        title = { Text(title) },
        navigationIcon = {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Voltar",
                )
            }
        },
        modifier = modifier,
    )
}
