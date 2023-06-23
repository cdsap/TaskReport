package io.github.cdsap.taskreport.view

import com.jakewharton.picnic.TextAlignment
import com.jakewharton.picnic.table
import io.github.cdsap.geapi.client.model.Build
import io.github.cdsap.geapi.client.model.Filter

class TaskStateView(private val outcome: List<Build>) {

    fun print(filter: Filter, taskPath: String) {

        val outcomes = mutableListOf<String>()
        outcome.forEach {
            it.taskExecution.filter { it.taskPath == taskPath }.groupBy {
                it.avoidanceOutcome
            }.keys.forEach {
                if (!outcomes.contains(it)) {
                    outcomes.add(it)
                }
            }
        }

        val outcomesCounte = mutableMapOf<String, Int>()
        outcomes.forEach { outcom ->
            var aux_cont = 0
            outcome.forEach {
                it.taskExecution.forEach {
                    if (it.taskPath == taskPath && it.avoidanceOutcome == outcom) {
                        aux_cont++
                    }
                }
                outcomesCounte[outcom] = aux_cont
            }
        }

        if (outcomesCounte.isNotEmpty()) {
            printOutcomesReport(filter, taskPath, outcomesCounte)
        }


    }

    private fun printOutcomesReport(filter: Filter, taskPath: String, outcomesCounte: Map<String, Int>) {
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
                        cell("Task Cache State Report") {
                            columnSpan = 2
                            alignment = TextAlignment.MiddleCenter
                        }
                    }
                    row {
                        cell("${filter.requestedTask} > $taskPath") {
                            columnSpan = 2
                            alignment = TextAlignment.MiddleCenter
                        }
                    }
                    row {
                        cell("tags: ${filter.tags}") {
                            columnSpan = 2
                            alignment = TextAlignment.MiddleCenter
                        }
                    }
                    outcomesCounte.forEach {
                        row {
                            cell(it.key) {
                                alignment = TextAlignment.MiddleLeft
                            }
                            cell(it.value) {
                                alignment = TextAlignment.MiddleRight
                            }
                        }
                    }
                }
            })
    }
}
