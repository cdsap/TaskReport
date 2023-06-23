package io.github.cdsap.taskreport.view

import com.jakewharton.picnic.TextAlignment
import com.jakewharton.picnic.table
import io.github.cdsap.geapi.client.model.Filter
import kotlin.time.DurationUnit
import kotlin.time.toDuration
import org.nield.kotlinstatistics.percentile

class TaskDurationView(private val durations: List<Long>) {
    fun print(filter: Filter, taskPath: String) {

        println(
            table {
                cellStyle {
                    border = true
                    alignment = TextAlignment.MiddleLeft
                    paddingLeft = 1
                    paddingRight = 1
                }
                body {
                    row {
                        cell("Task Duration Report with outcome executed") {
                            columnSpan = 7
                            alignment = TextAlignment.MiddleCenter
                        }
                    }
                    row {
                        cell("${filter.requestedTask} > $taskPath") {
                            columnSpan = 7
                            alignment = TextAlignment.MiddleCenter
                        }
                    }
                    row {
                        cell("tags: ${filter.tags}") {
                            columnSpan = 7
                            alignment = TextAlignment.MiddleCenter
                        }
                    }
                    row {
                        cell("Builds") {
                            alignment = TextAlignment.MiddleCenter
                        }
                        cell("Mean") {
                            alignment = TextAlignment.MiddleCenter
                        }
                        cell("P25") {
                            alignment = TextAlignment.MiddleCenter
                        }
                        cell("P50") {
                            alignment = TextAlignment.MiddleCenter
                        }
                        cell("P75") {
                            alignment = TextAlignment.MiddleCenter
                        }
                        cell("P90") {
                            alignment = TextAlignment.MiddleCenter
                        }
                        cell("P99") {
                            alignment = TextAlignment.MiddleCenter
                        }
                    }
                    row {
                        cell(durations.count()) {
                            alignment = TextAlignment.MiddleCenter
                        }
                        cell((durations.sumOf { it } / durations.size).toDuration(DurationUnit.MILLISECONDS)) {
                            alignment = TextAlignment.MiddleCenter
                        }
                        cell(durations.percentile(25.0).toDuration(DurationUnit.MILLISECONDS)) {
                            alignment = TextAlignment.MiddleCenter
                        }
                        cell(durations.percentile(50.0).toDuration(DurationUnit.MILLISECONDS)) {
                            alignment = TextAlignment.MiddleCenter
                        }
                        cell(durations.percentile(75.0).toDuration(DurationUnit.MILLISECONDS)) {
                            alignment = TextAlignment.MiddleCenter
                        }
                        cell(durations.percentile(90.0).toDuration(DurationUnit.MILLISECONDS)) {
                            alignment = TextAlignment.MiddleCenter
                        }
                        cell(durations.percentile(99.0).toDuration(DurationUnit.MILLISECONDS)) {
                            alignment = TextAlignment.MiddleCenter
                        }
                    }
                }
            })
    }
}
