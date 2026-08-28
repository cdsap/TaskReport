package io.github.cdsap.taskreport.report


import io.github.cdsap.geapi.client.domain.impl.GetBuildsFromQueryWithAttributesRequest
import io.github.cdsap.geapi.client.domain.impl.GetBuildsWithCachePerformanceRequest
import io.github.cdsap.geapi.client.model.Filter
import io.github.cdsap.geapi.client.repository.GradleEnterpriseRepository
import io.github.cdsap.taskreport.output.CsvOutput
import io.github.cdsap.taskreport.output.ImageOutput
import io.github.cdsap.taskreport.report.tasks.SingleTaskDurations
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

            val singleTaskDurations = SingleTaskDurations.fromBuilds(outcome, taskPath)

            if (singleTaskDurations.isNotEmpty()) {
                val durations = singleTaskDurations.map { it.duration }
                TaskDurationView(durations).print(filter, taskPath)
                CsvOutput(singleTaskDurations).write(taskPath)
                ImageOutput(durations).write(taskPath)
            }
        }
    }
}
