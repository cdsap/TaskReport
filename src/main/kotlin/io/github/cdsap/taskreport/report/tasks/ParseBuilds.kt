package io.github.cdsap.taskreport.report.tasks

import io.github.cdsap.geapi.client.model.Build
import io.github.cdsap.taskreport.model.PostStats


class ParseBuilds(private val builds: List<Build>) {

    fun getPostStatsByPath(): List<PostStats> {
        val statsByTaskPath = Stats(builds, false).get()
        return PostStats(statsByTaskPath).get()
    }

    fun getPostStatsByType(): List<PostStats> {
        val statsByTaskType = Stats(builds, true).get()
        return PostStats(statsByTaskType).get()
    }


}
