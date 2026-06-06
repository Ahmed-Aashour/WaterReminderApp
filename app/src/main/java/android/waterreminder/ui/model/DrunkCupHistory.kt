package android.waterreminder.ui.model

/**
 * Data representation matching the grouped preset structure identified in the Figma spec.
 */
data class DrunkCupHistory(
    val id: Int,
    val amountMl: Int,
    val timeLogged: String,
)