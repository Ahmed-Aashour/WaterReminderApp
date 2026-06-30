package android.waterreminder.ui.dashboard.components

import android.waterreminder.R
import android.waterreminder.data.entity.AppUnit
import android.waterreminder.ui.dashboard.DrinkButtonsState
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import android.waterreminder.ui.core.components.CustomAddButton
import android.waterreminder.ui.core.components.DashboardSection // Your newly created slot component
import android.waterreminder.ui.core.utils.dynamicFadingEdges
import android.waterreminder.ui.theme.ErtawyTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun DrinkButtonsSection(
    state: DrinkButtonsState,
    onPresetClick: (Int) -> Unit,
    onCustomAddClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    DashboardSection(
        title = stringResource(R.string.dashboard_section_drink_cups),
        modifier = modifier
    ) {
        // Everything inside this lambda block fills the "content" slot seamlessly!
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 16.dp)
                    .dynamicFadingEdges(state = scrollState, fadeWidth = 32.dp)
                    .horizontalScroll(scrollState),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                state.presetAmountsMl.forEach { amount ->
                    PresetCupButton(
                        amountMl = amount,
                        unit = state.unit,
                        onClick = onPresetClick
                    )
                }
            }

            CustomAddButton(onClick = onCustomAddClick)
        }
    }
}

@Preview(name = "Full Input Buttons Section Viewport", showBackground = true)
@Composable
fun DrinkButtonsSectionPreview() {
    ErtawyTheme {
        Box(modifier = Modifier.padding(16.dp)) {
            DrinkButtonsSection(
                state = DrinkButtonsState(
                    unit = AppUnit.ML,
                    presetAmountsMl = listOf(250, 350, 500)
                ),
                onPresetClick = {},
                onCustomAddClick = {}
            )
        }
    }
}
