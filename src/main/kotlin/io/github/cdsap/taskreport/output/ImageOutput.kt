package io.github.cdsap.taskreport.output

import org.jetbrains.letsPlot.export.ggsave
import org.jetbrains.letsPlot.geom.geomLine
import org.jetbrains.letsPlot.geom.geomPoint
import org.jetbrains.letsPlot.ggplot
import org.jetbrains.letsPlot.label.ggtitle
import org.jetbrains.letsPlot.label.labs
import java.io.File
import kotlin.time.DurationUnit
import kotlin.time.toDuration

class ImageOutput(private val durations: List<Long>) {

    fun write(taskPath: String) {
        if (durations.isNotEmpty()) {
            writeImage(taskPath, durations)
        }

    }


    private fun writeImage(taskPath: String, durations: List<Long>) {
        val ka = mapOf(
            "aa" to IntArray(durations.size),
            "base" to IntArray(durations.size) { i -> i + 1 },
            "data" to durations
        )
        val fig1 = ggplot(ka) + ggtitle("$taskPath") + labs(x = "", y = "Duration (milliseconds)") +
            geomLine { x = "base"; y = "data"; } + geomPoint { y = "data";x = "base" }
        val imageName = "duration${taskPath.replace(":", "_")}-${System.currentTimeMillis()}.png"
        ggsave(plot = fig1, filename = imageName, path = System.getProperty("user.dir"))
        if (File(imageName).exists()) {
            println("Image $imageName created")
        }
    }


}
