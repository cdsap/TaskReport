package io.github.cdsap.taskreport.report.tasks

import io.github.cdsap.geapi.client.model.Build
import io.github.cdsap.geapi.domain.model.Goal
import io.github.cdsap.geapi.domain.model.Task
import io.github.cdsap.taskreport.model.Stats

class Stats(
    private val builds: List<Build>,
    private val filterByTaskType: Boolean
) {

    fun get(): Map<String, Stats> {
        val statsByTaskPath = mutableMapOf<String, Stats>()
        builds.forEach {
            if (it.builtTool == "gradle") {
                it.taskExecution.filter { it.avoidanceOutcome.contains("cache") || it.avoidanceOutcome.contains("up_to_date") }
                    .filter { it.taskType != "org.gradle.api.tasks.Delete" }
                    .forEach {
                        if (filterByTaskType) {
                            if (statsByTaskPath.containsKey(it.taskType)) {
                                statsByTaskPath[it.taskType] = stats(it, statsByTaskPath[it.taskType])
                            } else {
                                statsByTaskPath[it.taskType] = stats(it, null)
                            }
                        } else {
                            if (statsByTaskPath.containsKey(it.taskPath)) {
                                statsByTaskPath[it.taskPath] = stats(it, statsByTaskPath[it.taskPath])
                            } else {
                                statsByTaskPath[it.taskPath] = stats(it, null)
                            }
                        }
                    }
            } else {
                it.goalExecution.filter { it.avoidanceOutcome.contains("cache") || it.avoidanceOutcome.contains("up_to_date") }
                    .forEach {
                        if (filterByTaskType) {
                            if (statsByTaskPath.containsKey(it.mojoType)) {
                                statsByTaskPath[it.mojoType] =
                                    statsMaven(
                                        it,
                                        statsByTaskPath[it.mojoType]
                                    )
                            } else {
                                statsByTaskPath[it.mojoType] =
                                    statsMaven(it, null)
                            }
                        } else {


                            if (statsByTaskPath.containsKey("${it.goalName}-${it.goalExecutionId}-${it.goalProjectName}")) {
                                statsByTaskPath["${it.goalName}-${it.goalExecutionId}-${it.goalProjectName}"] =
                                    statsMaven(
                                        it,
                                        statsByTaskPath["${it.goalName}-${it.goalExecutionId}-${it.goalProjectName}"]
                                    )
                            } else {
                                statsByTaskPath["${it.goalName}-${it.goalExecutionId}-${it.goalProjectName}"] =
                                    statsMaven(it, null)
                            }
                        }
                    }
            }
        }
        return statsByTaskPath
    }

    private fun stats(it: Task, preStat: Stats?): Stats {
        val avoidance = it.avoidanceOutcome
        var durationsExecuted = -1L
        var durationsFromCache = -1L
        var durationsFromUpToDate = -1L
        var durationsFromCacheLocal = -1L
        var durationsFromCacheRemote = -1L
        var fingerprintingExecuted = -1L
        var fingerprintingFromCache = -1L
        var fingerprintingFromUpToDate = -1L
        var fingerprintingFromCacheLocal = -1L
        var fingerprintingFromCacheRemote = -1L
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
                durationsExecuted = if (durationsExecuted == -1L) mutableListOf() else mutableListOf(durationsExecuted),
                durationsFromCache = if (durationsFromCache == -1L) mutableListOf() else mutableListOf(
                    durationsFromCache
                ),
                durationsFromUpToDate = if (durationsFromUpToDate == -1L) mutableListOf() else mutableListOf(
                    durationsFromUpToDate
                ),
                durationsFromCacheLocal = if (durationsFromCacheLocal == -1L) mutableListOf() else mutableListOf(
                    durationsFromCacheLocal
                ),
                durationsFromCacheRemote = if (durationsFromCacheRemote == -1L) mutableListOf() else mutableListOf(
                    durationsFromCacheRemote
                ),
                fingerprintingExecuted = if (fingerprintingExecuted == -1L) mutableListOf() else mutableListOf(
                    fingerprintingExecuted
                ),
                fingerprintingFromUpToDate = if (fingerprintingFromUpToDate == -1L) mutableListOf() else mutableListOf(
                    fingerprintingFromUpToDate
                ),
                fingerprintingFromCache = if (fingerprintingFromCache == -1L) mutableListOf() else mutableListOf(
                    fingerprintingFromCache
                ),
                fingerprintingFromCacheLocal = if (fingerprintingFromCacheLocal == -1L) mutableListOf() else mutableListOf(
                    fingerprintingFromCacheLocal
                ),
                fingerprintingFromCacheRemote = if (fingerprintingFromCacheRemote == -1L) mutableListOf() else mutableListOf(
                    fingerprintingFromCacheRemote
                ),
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
            if (durationsExecuted != -1L) preStat.durationsExecuted.add(durationsExecuted)
            if (durationsFromCache != -1L) preStat.durationsFromCache.add(durationsFromCache)
            if (durationsFromUpToDate != -1L) preStat.durationsFromUpToDate.add(durationsFromUpToDate)
            if (durationsFromCacheLocal != -1L) preStat.durationsFromCacheLocal.add(durationsFromCacheLocal)
            if (durationsFromCacheRemote != -1L) preStat.durationsFromCacheRemote.add(durationsFromCacheRemote)
            if (fingerprintingExecuted != -1L) preStat.fingerprintingExecuted.add(fingerprintingExecuted)
            if (fingerprintingFromUpToDate != -1L) preStat.fingerprintingFromUpToDate.add(fingerprintingFromUpToDate)
            if (fingerprintingFromCache != -1L) preStat.fingerprintingFromCache.add(fingerprintingFromCache)
            if (fingerprintingFromCacheLocal != -1L) preStat.fingerprintingFromCacheLocal.add(
                fingerprintingFromCacheLocal
            )
            if (fingerprintingFromCacheRemote != -1L) preStat.fingerprintingFromCacheRemote.add(
                fingerprintingFromCacheRemote
            )
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
                durationsExecuted = if (durationsExecuted == -1L) mutableListOf() else mutableListOf(
                    durationsExecuted
                ),
                durationsFromCache = if (durationsFromCache == -1L) mutableListOf() else mutableListOf(
                    durationsFromCache
                ),
                durationsFromUpToDate = if (durationsFromUpToDate == -1L) mutableListOf() else mutableListOf(
                    durationsFromUpToDate
                ),
                durationsFromCacheLocal = if (durationsFromCacheLocal == -1L) mutableListOf() else mutableListOf(
                    durationsFromCacheLocal
                ),
                durationsFromCacheRemote = if (durationsFromCacheRemote == -1L) mutableListOf() else mutableListOf(
                    durationsFromCacheRemote
                ),
                fingerprintingExecuted = if (fingerprintingExecuted == -1L) mutableListOf() else mutableListOf(
                    fingerprintingExecuted
                ),
                fingerprintingFromUpToDate = if (fingerprintingFromUpToDate == -1L) mutableListOf() else mutableListOf(
                    fingerprintingFromUpToDate
                ),
                fingerprintingFromCache = if (fingerprintingFromCache == -1L) mutableListOf() else mutableListOf(
                    fingerprintingFromCache
                ),
                fingerprintingFromCacheLocal = if (fingerprintingFromCacheLocal == -1L) mutableListOf() else mutableListOf(
                    fingerprintingFromCacheLocal
                ),
                fingerprintingFromCacheRemote = if (fingerprintingFromCacheRemote == -1L) mutableListOf() else mutableListOf(
                    fingerprintingFromCacheRemote
                ),
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
            if (durationsExecuted != -1L) preStat.durationsExecuted.add(durationsExecuted)
            if (durationsFromCache != -1L) preStat.durationsFromCache.add(durationsFromCache)
            if (durationsFromUpToDate != -1L) preStat.durationsFromUpToDate.add(durationsFromUpToDate)
            if (durationsFromCacheLocal != -1L) preStat.durationsFromCacheLocal.add(durationsFromCacheLocal)
            if (durationsFromCacheRemote != -1L) preStat.durationsFromCacheRemote.add(durationsFromCacheRemote)
            if (fingerprintingExecuted != -1L) preStat.fingerprintingExecuted.add(fingerprintingExecuted)
            if (fingerprintingFromUpToDate != -1L) preStat.fingerprintingFromUpToDate.add(fingerprintingFromUpToDate)
            if (fingerprintingFromCache != -1L) preStat.fingerprintingFromCache.add(fingerprintingFromCache)
            if (fingerprintingFromCacheLocal != -1L) preStat.fingerprintingFromCacheLocal.add(
                fingerprintingFromCacheLocal
            )
            if (fingerprintingFromCacheRemote != -1L) preStat.fingerprintingFromCacheRemote.add(
                fingerprintingFromCacheRemote
            )
            return preStat
        }
    }

}
