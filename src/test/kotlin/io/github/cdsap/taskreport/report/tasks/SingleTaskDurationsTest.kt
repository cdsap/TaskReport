package io.github.cdsap.taskreport.report.tasks

import io.github.cdsap.geapi.client.model.AvoidanceSavingsSummary
import io.github.cdsap.geapi.client.model.Build
import io.github.cdsap.geapi.client.model.Task
import kotlin.test.Test
import kotlin.test.assertEquals

class SingleTaskDurationsTest {

    @Test
    fun `extracts ordered executed durations for matching task path`() {
        val builds = listOf(
            build(
                id = "later",
                startTime = 200L,
                tasks = arrayOf(
                    task(path = ":app:compileKotlin", outcome = "executed_cacheable", duration = 40),
                    task(path = ":app:compileKotlin", outcome = "avoided_from_cache", duration = 5),
                ),
            ),
            build(
                id = "earlier",
                startTime = 100L,
                tasks = arrayOf(
                    task(path = ":lib:compileKotlin", outcome = "executed_not_cacheable", duration = 10),
                    task(path = ":app:compileKotlin", outcome = "executed_cacheable", duration = 25),
                    task(path = ":app:compileKotlin", outcome = "executed_cacheable", duration = 15),
                ),
            ),
            build(
                id = "skipped",
                startTime = 50L,
                tasks = arrayOf(
                    task(path = ":app:compileKotlin", outcome = "avoided_from_cache", duration = 3),
                ),
            ),
        )

        val rows = SingleTaskDurations.fromBuilds(builds, ":app:compileKotlin")

        assertEquals(
            listOf(
                SingleTaskDuration(buildId = "earlier", buildStartTime = 100L, duration = 40L),
                SingleTaskDuration(buildId = "later", buildStartTime = 200L, duration = 40L),
            ),
            rows,
        )
    }

    private fun build(
        id: String,
        startTime: Long,
        tasks: Array<Task>,
    ): Build {
        return Build(
            builtTool = "gradle",
            taskExecution = tasks,
            id = id,
            avoidanceSavingsSummary = AvoidanceSavingsSummary("0", "0", "0"),
            buildStartTime = startTime,
            goalExecution = emptyArray(),
        )
    }

    private fun task(
        path: String,
        outcome: String,
        duration: Long,
    ): Task {
        return Task(
            taskType = "org.jetbrains.kotlin.gradle.tasks.KotlinCompile",
            taskPath = path,
            avoidanceOutcome = outcome,
            duration = duration,
            fingerprintingDuration = 0L,
        )
    }
}
