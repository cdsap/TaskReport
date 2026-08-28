package io.github.cdsap.taskreport.output

import io.github.cdsap.taskreport.report.tasks.SingleTaskDuration
import java.io.File

class CsvOutput(private val rows: List<SingleTaskDuration>) {

    fun write(taskPath: String) {
        if (rows.isEmpty()) {
            return
        }
        writeCsv(taskPath, rows)
    }

    private fun writeCsv(
        taskPath: String,
        rows: List<SingleTaskDuration>,
    ) {
        val csv = "duration${taskPath.replace(":", "_")}-${System.currentTimeMillis()}.csv"
        val headers = "BuildId,Date,Duration\n"
        var values = ""
        rows.forEach {
            values += "${it.buildId},${it.buildStartTime},${it.duration}\n"
        }
        File(csv).writeText("""$headers$values""".trimIndent())
        println("File $csv created")
    }
}
