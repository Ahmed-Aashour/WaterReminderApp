package android.waterreminder.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cups_catalog")
data class CupsCatalogEntity(
    @PrimaryKey
    val amountMl: Int // Using the capacity volume as the primary key since duplicates aren't allowed
) {
    companion object {
        val DEFAULT_PRESET_CUPS = listOf(250, 350, 500)
    }
}