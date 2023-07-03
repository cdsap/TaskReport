package io.github.cdsap.taskreport.view

import com.jakewharton.picnic.TableSectionDsl
import com.jakewharton.picnic.TextAlignment
import com.jakewharton.picnic.table
import io.github.cdsap.taskreport.model.*
import kotlin.math.roundToLong
import kotlin.time.DurationUnit
import kotlin.time.toDuration

class AllTaskStateView(private val postStats: List<PostStats>) {

    fun print() {
        val typeOfWorkUnit = if(postStats.first().type == "gradle") "Tasks" else "Goals"
        printTasks(typeOfWorkUnit, postStats)
    }

    private fun printTasks(title: String, postStats: List<PostStats>) {


        val meanExecution = postStats.toList().sortedByDescending { it.durationsExecuted.mean }.take(5).flatMap {
            listOf(PrintedStat(it.name, it.durationsExecuted.mean))
        }

        val p90Execution = postStats.toList().sortedByDescending { it.durationsExecuted.p90 }.take(5).flatMap {
            listOf(PrintedStat(it.name, it.durationsExecuted.p90.roundToLong()))
        }

        val cacheMeanDuration = postStats.toList().sortedByDescending { it.durationsFromCache.mean }.take(5).flatMap {
            listOf(PrintedStat(it.name, it.durationsFromCache.mean))
        }

        val cacheP90Duration = postStats.toList().sortedByDescending { it.durationsFromCache.p90 }.take(5).flatMap {
            listOf(PrintedStat(it.name, it.durationsFromCache.p90.roundToLong()))
        }

        val upToDateMeanDuration =
            postStats.toList().sortedByDescending { it.durationsFromUpToDate.mean }.take(5).flatMap {
                listOf(PrintedStat(it.name, it.durationsFromUpToDate.mean))
            }

        val upToDate90Duration =
            postStats.toList().sortedByDescending { it.durationsFromUpToDate.p90 }.take(5).flatMap {
                listOf(PrintedStat(it.name, it.durationsFromUpToDate.p90.roundToLong()))
            }


        val fingerExecutedMean =
            postStats.toList().sortedByDescending { it.fingerprintingExecuted.mean }.take(5).flatMap {
                listOf(PrintedStat(it.name, it.fingerprintingExecuted.mean))
            }

        val fingerExecutedP90 =
            postStats.toList().sortedByDescending { it.fingerprintingExecuted.p90 }.take(5).flatMap {
                listOf(PrintedStat(it.name, it.fingerprintingExecuted.p90.roundToLong()))
            }


        val fingerCacheMean =
            postStats.toList().sortedByDescending { it.fingerprintingFromCache.mean }.take(5).flatMap {
                listOf(PrintedStat(it.name, it.fingerprintingFromCache.mean))
            }

        val fingerCacheP90 = postStats.toList().sortedByDescending { it.fingerprintingFromCache.p90 }.take(5).flatMap {
            listOf(PrintedStat(it.name, it.fingerprintingFromCache.p90.roundToLong()))
        }

        val fingerUpToDateMean =
            postStats.toList().sortedByDescending { it.fingerprintingFromUpToDate.mean }.take(5).flatMap {
                listOf(PrintedStat(it.name, it.fingerprintingFromUpToDate.mean))
            }


        val fingerUpToDateP90 =
            postStats.toList().sortedByDescending { it.fingerprintingFromUpToDate.p90 }.take(5).flatMap {
                listOf(PrintedStat(it.name, it.fingerprintingFromUpToDate.p90.roundToLong()))
            }

        printReport(
            fingerExecutedMean,
            fingerExecutedP90,
            fingerCacheMean,
            fingerCacheP90,
            fingerUpToDateMean,
            fingerUpToDateP90,
            "$title - Top 5 Fingerprinting"
        )

        printReport(
            meanExecution,
            p90Execution,
            cacheMeanDuration,
            cacheP90Duration,
            upToDateMeanDuration,
            upToDate90Duration,
            "$title - Top 5 Duration"
        )

    }

    private fun printReport(
        executedMean: List<PrintedStat>,
        executedP90: List<PrintedStat>,
        cacheMean: List<PrintedStat>,
        cacheP90: List<PrintedStat>,
        upToDateMean: List<PrintedStat>,
        upToDateP90: List<PrintedStat>,
        title: String
    ) {
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
                        cell(title) {
                            columnSpan = 4
                            padding = 3
                            alignment = TextAlignment.MiddleCenter
                        }
                    }
                    if(title != "Goals") {
                        row {
                            cell("UP TO DATE") {
                                columnSpan = 4
                                alignment = TextAlignment.MiddleCenter
                            }
                        }
                    }
                    row {
                        cell("Mean") {
                            columnSpan = 2
                            alignment = TextAlignment.MiddleCenter
                        }
                        cell("P90") {
                            columnSpan = 2
                            alignment = TextAlignment.MiddleCenter
                        }
                    }
                    if(title != "Goals") {
                        header()
                        printedElement(upToDateMean, upToDateP90)
                    }
                    row {
                        cell("FROM-CACHE") {
                            columnSpan = 4
                            alignment = TextAlignment.MiddleCenter
                        }
                    }
                    row {
                        cell("Mean") {
                            columnSpan = 2
                            alignment = TextAlignment.MiddleCenter
                        }
                        cell("P90") {
                            columnSpan = 2
                            alignment = TextAlignment.MiddleCenter
                        }
                    }
                    header()
                    printedElement(cacheMean, cacheP90)
                    row {
                        cell("Executed") {
                            columnSpan = 4
                            alignment = TextAlignment.MiddleCenter
                        }
                    }
                    row {
                        cell("Mean") {
                            columnSpan = 2
                            alignment = TextAlignment.MiddleCenter
                        }
                        cell("P90") {
                            columnSpan = 2
                            alignment = TextAlignment.MiddleCenter
                        }
                    }
                    header()
                    printedElement(executedMean, executedP90)
                }
            }
        )
    }

}
fun TableSectionDsl.header() {
    row(
        "Task",
        "Duration",
        "Task",
        "Duration"
    )
}

fun TableSectionDsl.printedElement(
    firstList: List<PrintedStat>,
    secondList: List<PrintedStat>
) {
    for (i in 0..4) {

        this.row {
            cell(firstList[i].name)
            cell(firstList[i].elements.toDuration(DurationUnit.MILLISECONDS)) {
                alignment = TextAlignment.MiddleRight
            }

            cell(secondList[i].name)
            cell(secondList[i].elements.toDuration(DurationUnit.MILLISECONDS)) {
                alignment = TextAlignment.MiddleRight
            }
        }
    }
}
