package android.waterreminder.ui.core.utils

import android.content.Context
import java.io.BufferedReader
import java.io.InputStreamReader

/** TODO: Append the license into About page
 * Reads a text file from the app's assets folder safely.
 */
fun readAssetLicenseText(context: Context, fileName: String): String {
    return try {
        context.assets.open(fileName).use { inputStream ->
            BufferedReader(InputStreamReader(inputStream)).use { reader ->
                reader.readText()
            }
        }
    } catch (_: Exception) {
        "License text unavailable."
    }
}