package dev.vereda.ui.components

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/**
 * App bar with a title and a back button on the left.
 *
 * The back affordance is a text button ("Voltar") to keep the project free of `material-icons-extended`.
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
            TextButton(onClick = onBack) { Text("Voltar") }
        },
        modifier = modifier,
    )
}
