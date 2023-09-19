package io.github.cdsap.taskreport.report


import io.github.cdsap.geapi.client.domain.impl.GetBuildsFromQueryWithAttributesRequest
import io.github.cdsap.geapi.client.domain.impl.GetBuildsWithAttributesRequest
import io.github.cdsap.geapi.client.domain.impl.GetBuildsWithCachePerformanceRequest
import io.github.cdsap.geapi.client.model.Build
import io.github.cdsap.geapi.client.model.Filter
import io.github.cdsap.geapi.client.repository.GradleEnterpriseRepository
import io.github.cdsap.taskreport.output.CsvOutput
import io.github.cdsap.taskreport.output.ImageOutput
import io.github.cdsap.taskreport.view.AllTaskStateView
import io.github.cdsap.taskreport.view.TaskDurationView
import io.github.cdsap.taskreport.view.TaskStateView


class TaskReport(
    private val filter: Filter,
    private val repository: GradleEnterpriseRepository,
    private val cacheRepository: GradleEnterpriseRepository,
    private val taskPath: String

) {

    suspend fun process() {
        val getBuildScans = GetBuildsFromQueryWithAttributesRequest(repository)
        val getOutcome = GetBuildsWithCachePerformanceRequest(cacheRepository)
        val buildScansFiltered = getBuildScans.get(filter)
        if (buildScansFiltered.isNotEmpty() && buildScansFiltered.first().buildTool == "maven") {
            throw IllegalArgumentException("Single Tasks reports for Maven builds not supported")
        }

        val outcome = getOutcome.get(buildScansFiltered, filter).sortedBy { it.buildStartTime }

        if (outcome.isNotEmpty()) {
            TaskStateView(outcome).print(filter, taskPath)

            val buildsWithTaskAndExecution = filterBuildsByTaskAndOutcome(outcome)

            if (buildsWithTaskAndExecution.isNotEmpty()) {

                val durations = durations(buildsWithTaskAndExecution)
                TaskDurationView(durations).print(filter, taskPath)
                CsvOutput(buildsWithTaskAndExecution).write(taskPath)
                ImageOutput(durations).write(taskPath)

            }
        }
    }

    private fun filterBuildsByTaskAndOutcome(outcome: List<Build>): List<Build> {
        return outcome.filter {
            it.taskExecution.any {
                it.taskPath.contains(taskPath)
                    && it.avoidanceOutcome.contains("executed")
            }
        }
    }

    private fun durations(builds: List<Build>): List<Long> {
        val durations = mutableListOf<Long>()
        builds.sortedBy { it.buildStartTime }.forEach {

            val duration = it.taskExecution.filter {
                it.taskPath.contains(taskPath)
                    && it.avoidanceOutcome.contains("executed")
            }.sumOf { it.duration }
            durations.add(duration)
        }
        return durations
    }
}

