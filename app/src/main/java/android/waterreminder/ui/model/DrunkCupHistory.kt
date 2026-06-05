package android.waterreminder.ui.model

/**
 * Data representation matching the grouped preset structure identified in the Figma spec.
 */
data class DrunkCupHistory(
    val amountMl: Int,
    val count: Int
)