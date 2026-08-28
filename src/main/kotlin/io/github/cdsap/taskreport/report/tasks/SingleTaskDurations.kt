package io.github.cdsap.taskreport.report.tasks

import io.github.cdsap.geapi.client.model.Build
import io.github.cdsap.geapi.client.model.Task

data class SingleTaskDuration(
    val buildId: String,
    val buildStartTime: Long,
    val duration: Long,
)

object SingleTaskDurations {
    fun fromBuilds(builds: List<Build>, taskPath: String): List<SingleTaskDuration> {
        return builds
            .filter { build -> build.taskExecution.any { matchesExecutedTask(it, taskPath) } }
            .sortedBy { it.buildStartTime }
            .map { build ->
                SingleTaskDuration(
                    buildId = build.id,
                    buildStartTime = build.buildStartTime,
                    duration = build.taskExecution
                        .filter { matchesExecutedTask(it, taskPath) }
                        .sumOf { it.duration },
                )
            }
    }

    private fun matchesExecutedTask(task: Task, taskPath: String): Boolean {
        return task.taskPath.contains(taskPath) && task.avoidanceOutcome.contains("executed")
    }
}
