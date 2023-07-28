package io.github.cdsap.taskreport.report

import io.github.cdsap.geapi.client.domain.impl.GetBuildScansWithQueryImpl
import io.github.cdsap.geapi.client.domain.impl.GetCachePerformanceImpl
import io.github.cdsap.geapi.client.model.Filter
import io.github.cdsap.geapi.client.repository.GradleEnterpriseRepository
import io.github.cdsap.taskreport.output.GeneralCsvOutput
import io.github.cdsap.taskreport.report.tasks.ParseBuilds
import io.github.cdsap.taskreport.view.AllTaskStateView

class GeneralReport(
    private val filter: Filter,
    private val repository: GradleEnterpriseRepository,
    private val cacheRepository: GradleEnterpriseRepository
) {

    suspend fun process() {
        val getBuildScans = GetBuildScansWithQueryImpl(repository)
        val getOutcome = GetCachePerformanceImpl(cacheRepository)
        val buildScansFiltered = getBuildScans.get(filter)
        val outcome = getOutcome.get(buildScansFiltered, filter).sortedBy { it.buildStartTime }

        if (outcome.isNotEmpty()) {
            val postStatsByPath = ParseBuilds(outcome).getPostStatsByPath()
            val postStatsByType = ParseBuilds(outcome).getPostStatsByType()
            val typeReport = outcome.first().builtTool
            if (postStatsByPath.isNotEmpty() && postStatsByType.isNotEmpty()) {
                AllTaskStateView(postStatsByPath, false).print()
                AllTaskStateView(postStatsByType, true).print()
                GeneralCsvOutput(postStatsByPath, false).write(filter.requestedTask, "$typeReport-path")
                GeneralCsvOutput(postStatsByType, true).write(filter.requestedTask, "$typeReport-type")
            }
        }
    }

}
