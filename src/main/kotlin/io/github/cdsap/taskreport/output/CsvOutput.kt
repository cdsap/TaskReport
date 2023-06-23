package io.github.cdsap.taskreport.output

import io.github.cdsap.geapi.client.model.Build
import java.io.File

class CsvOutput(private val outcome: List<Build>) {

    fun write(taskPath: String) {
        val csvEntries = mutableListOf<CsvFormat>()

        outcome.forEach {
            val duration = it.taskExecution.filter {
                it.taskPath.contains(taskPath)
                    && it.avoidanceOutcome.contains("executed")
            }.sumOf { it.duration }
            csvEntries.add(
                CsvFormat(
                    id = it.id,
                    date = it.buildStartTime,
                    duration = duration
                )
            )
        }

        if (csvEntries.isNotEmpty()) {
            writeCsv(taskPath, csvEntries)
        }

    }

    private fun writeCsv(
        taskPath: String,
        csvEntries: MutableList<CsvFormat>
    ) {
        val csv = "duration${taskPath.replace(":","_")}-${System.currentTimeMillis()}.csv"
        val headers = "BuildId,Date,Duration\n"
        var values = ""
        csvEntries.forEach {
            values += "${it.id},${it.date},${it.duration}\n"
        }
        File(csv).writeText("""$headers$values""".trimIndent())
        println("File $csv created")
    }


}

data class CsvFormat(
    val id: String,
    val date: Long,
    val duration: Long
)
