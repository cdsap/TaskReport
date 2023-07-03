package io.github.cdsap.taskreport.report

import io.github.cdsap.geapi.client.domain.impl.GetBuildScansWithQueryImpl
import io.github.cdsap.geapi.client.domain.impl.GetCachePerformanceImpl
import io.github.cdsap.geapi.client.model.Filter
import io.github.cdsap.geapi.client.repository.GradleEnterpriseRepository
import io.github.cdsap.geapi.domain.model.Goal
import io.github.cdsap.geapi.domain.model.Task
import io.github.cdsap.taskreport.model.Outcomes
import io.github.cdsap.taskreport.model.PostStats
import io.github.cdsap.taskreport.model.Statistics
import io.github.cdsap.taskreport.model.Stats
import io.github.cdsap.taskreport.output.GeneralCsvOutput
import io.github.cdsap.taskreport.view.*
import org.nield.kotlinstatistics.percentile

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

            val stats = mutableMapOf<String, Stats>()
            var typeReport = ""
            outcome.forEach {
                if (it.builtTool == "gradle") {
                    typeReport = "gradle"
                    it.taskExecution.filter { it.avoidanceOutcome.contains("cache") || it.avoidanceOutcome.contains("up_to_date") }
                        .filter { it.taskType != "org.gradle.api.tasks.Delete" }
                        .forEach {
                            if (stats.containsKey(it.taskPath)) {
                                stats[it.taskPath] = stats(it, stats[it.taskPath])
                            } else {
                                stats[it.taskPath] = stats(it, null)
                            }
                        }
                } else {
                    typeReport = "maven"
                    it.goalExecution.filter { it.avoidanceOutcome.contains("cache") || it.avoidanceOutcome.contains("up_to_date") }
                        .forEach {
                            if (stats.containsKey("${it.goalName}-${it.goalExecutionId}-${it.goalProjectName}")) {
                                stats["${it.goalName}-${it.goalExecutionId}-${it.goalProjectName}"] =
                                    statsMaven(it, stats["${it.goalName}-${it.goalExecutionId}-${it.goalProjectName}"])
                            } else {
                                stats["${it.goalName}-${it.goalExecutionId}-${it.goalProjectName}"] =
                                    statsMaven(it, null)
                            }
                        }
                }
            }

            if (stats.isNotEmpty()) {
                val postStats = stats.flatMap {
                    listOf(
                        PostStats(
                            durationsExecuted = statitics(it.value.durationsExecuted),
                            durationsFromCache = statitics(it.value.durationsFromCache),
                            durationsFromUpToDate = statitics(it.value.durationsFromUpToDate),
                            durationsFromCacheLocal = statitics(it.value.durationsFromCacheLocal),
                            durationsFromCacheRemote = statitics(it.value.durationsFromCacheRemote),
                            fingerprintingExecuted = statitics(it.value.fingerprintingExecuted),
                            fingerprintingFromCache = statitics(it.value.fingerprintingFromCache),
                            fingerprintingFromUpToDate = statitics(it.value.fingerprintingFromUpToDate),
                            fingerprintingFromCacheLocal = statitics(it.value.fingerprintingFromCacheLocal),
                            fingerprintingFromCacheRemote = statitics(it.value.fingerprintingFromCacheRemote),
                            executions = statiticsCache(it.value),
                            name = it.key,
                            module = it.key.split(":").dropLast(1).joinToString(":"),
                            type = it.value.type
                        )
                    )
                }

                AllTaskStateView(postStats).print()
                GeneralCsvOutput(postStats).write(filter.requestedTask,typeReport)
            }

        }
    }
}


private fun stats(it: Task, preStat: Stats?): Stats {
    val avoidance = it.avoidanceOutcome
    var durationsExecuted = 0L
    var durationsFromCache = 0L
    var durationsFromUpToDate = 0L
    var durationsFromCacheLocal = 0L
    var durationsFromCacheRemote = 0L
    var fingerprintingExecuted = 0L
    var fingerprintingFromCache = 0L
    var fingerprintingFromUpToDate = 0L
    var fingerprintingFromCacheLocal = 0L
    var fingerprintingFromCacheRemote = 0L
    val executions = 1
    var executionsExecuted = 0
    var executionsUpToDate = 0
    var executionsFromCache = 0
    var executionsFromCacheLocal = 0
    var executionsFromCacheRemote = 0

    if (avoidance.contains("executed")) {
        durationsExecuted = it.duration
        fingerprintingExecuted = it.fingerprintingDuration
        executionsExecuted = 1

    } else if (avoidance.contains("avoided_from")) {
        durationsFromCache = it.duration
        executionsFromCache = 1
        fingerprintingFromCache = it.fingerprintingDuration
        if (avoidance.contains("local")) {
            executionsFromCacheLocal = 1
            durationsFromCacheLocal = it.duration
            fingerprintingFromCacheLocal = it.fingerprintingDuration
        } else if (avoidance.contains("remote")) {
            executionsFromCacheRemote = 1
            durationsFromCacheRemote = it.duration
            fingerprintingFromCacheRemote = it.fingerprintingDuration
        }
    } else if (avoidance.contains("up_to_date")) {
        durationsFromUpToDate = it.duration
        fingerprintingFromUpToDate = it.fingerprintingDuration
        executionsUpToDate = 1
    } else {

    }
    if (preStat == null) {
        val stats = Stats(
            executions = executions,
            executionsExecuted = executionsExecuted,
            executionsUpToDate = executionsUpToDate,
            executionsFromCache = executionsFromCache,
            executionsFromCacheLocal = executionsFromCacheLocal,
            executionsFromCacheRemote = executionsFromCacheRemote,
            durationsExecuted = mutableListOf(durationsExecuted),
            durationsFromCache = mutableListOf(durationsFromCache),
            durationsFromUpToDate = mutableListOf(durationsFromUpToDate),
            durationsFromCacheLocal = mutableListOf(durationsFromCacheLocal),
            durationsFromCacheRemote = mutableListOf(durationsFromCacheRemote),
            fingerprintingExecuted = mutableListOf(fingerprintingExecuted),
            fingerprintingFromUpToDate = mutableListOf(fingerprintingFromUpToDate),
            fingerprintingFromCache = mutableListOf(fingerprintingFromCache),
            fingerprintingFromCacheLocal = mutableListOf(fingerprintingFromCacheLocal),
            fingerprintingFromCacheRemote = mutableListOf(fingerprintingFromCacheRemote),
            type = "gradle"
        )
        return stats
    } else {

        preStat.executions = preStat.executions + executions
        preStat.executionsExecuted = preStat.executionsExecuted + executionsExecuted
        preStat.executionsUpToDate = preStat.executionsUpToDate + executionsUpToDate
        preStat.executionsFromCache = preStat.executionsFromCache + executionsFromCache
        preStat.executionsFromCacheLocal = preStat.executionsFromCacheLocal + executionsFromCacheLocal
        preStat.executionsFromCacheRemote = preStat.executionsFromCacheRemote + executionsFromCacheRemote
        preStat.durationsExecuted.add(durationsExecuted)
        preStat.durationsFromCache.add(durationsFromCache)
        preStat.durationsFromUpToDate.add(durationsFromUpToDate)
        preStat.durationsFromCacheLocal.add(durationsFromCacheLocal)
        preStat.durationsFromCacheRemote.add(durationsFromCacheRemote)
        preStat.fingerprintingExecuted.add(fingerprintingExecuted)
        preStat.fingerprintingFromUpToDate.add(fingerprintingFromUpToDate)
        preStat.fingerprintingFromCache.add(fingerprintingFromCache)
        preStat.fingerprintingFromCacheLocal.add(fingerprintingFromCacheLocal)
        preStat.fingerprintingFromCacheRemote.add(fingerprintingFromCacheRemote)
        return preStat
    }
}

private fun statsMaven(it: Goal, preStat: Stats?): Stats {
    val avoidance = it.avoidanceOutcome
    var durationsExecuted = 0L
    var durationsFromCache = 0L
    var durationsFromUpToDate = 0L
    var durationsFromCacheLocal = 0L
    var durationsFromCacheRemote = 0L
    var fingerprintingExecuted = 0L
    var fingerprintingFromCache = 0L
    var fingerprintingFromUpToDate = 0L
    var fingerprintingFromCacheLocal = 0L
    var fingerprintingFromCacheRemote = 0L
    val executions = 1
    var executionsExecuted = 0
    var executionsUpToDate = 0
    var executionsFromCache = 0
    var executionsFromCacheLocal = 0
    var executionsFromCacheRemote = 0

    if (avoidance.contains("executed")) {
        durationsExecuted = it.duration
        fingerprintingExecuted = it.fingerprintingDuration
        executionsExecuted = 1

    } else if (avoidance.contains("avoided_from")) {
        durationsFromCache = it.duration
        executionsFromCache = 1
        fingerprintingFromCache = it.fingerprintingDuration
        if (avoidance.contains("local")) {
            executionsFromCacheLocal = 1
            durationsFromCacheLocal = it.duration
            fingerprintingFromCacheLocal = it.fingerprintingDuration
        } else if (avoidance.contains("remote")) {
            executionsFromCacheRemote = 1
            durationsFromCacheRemote = it.duration
            fingerprintingFromCacheRemote = it.fingerprintingDuration
        }
    } else if (avoidance.contains("up_to_date")) {
        durationsFromUpToDate = it.duration
        fingerprintingFromUpToDate = it.fingerprintingDuration
        executionsUpToDate = 1
    } else {

    }
    if (preStat == null) {
        val stats = Stats(
            executions = executions,
            executionsExecuted = executionsExecuted,
            executionsUpToDate = executionsUpToDate,
            executionsFromCache = executionsFromCache,
            executionsFromCacheLocal = executionsFromCacheLocal,
            executionsFromCacheRemote = executionsFromCacheRemote,
            durationsExecuted = mutableListOf(durationsExecuted),
            durationsFromCache = mutableListOf(durationsFromCache),
            durationsFromUpToDate = mutableListOf(durationsFromUpToDate),
            durationsFromCacheLocal = mutableListOf(durationsFromCacheLocal),
            durationsFromCacheRemote = mutableListOf(durationsFromCacheRemote),
            fingerprintingExecuted = mutableListOf(fingerprintingExecuted),
            fingerprintingFromUpToDate = mutableListOf(fingerprintingFromUpToDate),
            fingerprintingFromCache = mutableListOf(fingerprintingFromCache),
            fingerprintingFromCacheLocal = mutableListOf(fingerprintingFromCacheLocal),
            fingerprintingFromCacheRemote = mutableListOf(fingerprintingFromCacheRemote),
            type = "maven"
        )
        return stats
    } else {

        preStat.executions = preStat.executions + executions
        preStat.executionsExecuted = preStat.executionsExecuted + executionsExecuted
        preStat.executionsUpToDate = preStat.executionsUpToDate + executionsUpToDate
        preStat.executionsFromCache = preStat.executionsFromCache + executionsFromCache
        preStat.executionsFromCacheLocal = preStat.executionsFromCacheLocal + executionsFromCacheLocal
        preStat.executionsFromCacheRemote = preStat.executionsFromCacheRemote + executionsFromCacheRemote
        preStat.durationsExecuted.add(durationsExecuted)
        preStat.durationsFromCache.add(durationsFromCache)
        preStat.durationsFromUpToDate.add(durationsFromUpToDate)
        preStat.durationsFromCacheLocal.add(durationsFromCacheLocal)
        preStat.durationsFromCacheRemote.add(durationsFromCacheRemote)
        preStat.fingerprintingExecuted.add(fingerprintingExecuted)
        preStat.fingerprintingFromUpToDate.add(fingerprintingFromUpToDate)
        preStat.fingerprintingFromCache.add(fingerprintingFromCache)
        preStat.fingerprintingFromCacheLocal.add(fingerprintingFromCacheLocal)
        preStat.fingerprintingFromCacheRemote.add(fingerprintingFromCacheRemote)
        return preStat
    }
}


fun statiticsCache(stat: Stats): Outcomes {
    if (stat.executions == 0) {
        return Outcomes()
    } else {


        return Outcomes(
            executions = stat.executions,
            executionsExecuted = stat.executionsExecuted,
            executionsFromCache = stat.executionsFromCache,
            executionsFromCacheLocal = stat.executionsFromCacheLocal,
            executionsFromCacheRemote = stat.executionsFromCacheRemote,
            executionsUpToDate = stat.executionsUpToDate,
            percentageCaching = if (stat.executionsFromCache == 0) 0f else (stat.executionsFromCache.toFloat() / ((stat.executionsFromCache + stat.executionsUpToDate + stat.executionsExecuted))) * 100,
            percentageExecution = if (stat.executionsExecuted == 0) 0f else (stat.executionsExecuted.toFloat() / ((stat.executionsFromCache + stat.executionsUpToDate + stat.executionsExecuted))) * 100,
            percentageUpToDate = if (stat.executionsUpToDate == 0) 0f else (stat.executionsUpToDate.toFloat() / ((stat.executionsFromCache + stat.executionsUpToDate + stat.executionsExecuted))) * 100,
            percentageLocalCaching = if (stat.executionsFromCacheLocal == 0) 0f else (stat.executionsFromCacheLocal.toFloat() / ((stat.executionsFromCache + stat.executionsUpToDate + stat.executionsExecuted))) * 100,
            percentageRemoteCaching = if (stat.executionsFromCacheRemote == 0) 0f else (stat.executionsFromCacheRemote.toFloat() / ((stat.executionsFromCache + stat.executionsUpToDate + stat.executionsExecuted))) * 100
        )
    }
}


fun statitics(stat: List<Long>): Statistics {

    if (stat.isEmpty()) {
        return Statistics()
    } else {
        val durationsExecutedMean = stat.sum() / stat.size
        val durationExecutedP25 = stat.percentile(25.0)
        val durationExecutedP50 = stat.percentile(50.0)
        val durationExecutedP75 = stat.percentile(70.0)
        val durationExecutedP90 = stat.percentile(90.0)
        val durationExecutedP99 = stat.percentile(99.0)


        return Statistics(
            mean = durationsExecutedMean,
            p25 = durationExecutedP25,
            p50 = durationExecutedP50,
            p75 = durationExecutedP75,
            p90 = durationExecutedP90,
            p99 = durationExecutedP99
        )
    }
}
