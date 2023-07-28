package io.github.cdsap.taskreport.report.tasks

import io.github.cdsap.taskreport.model.Outcomes
import io.github.cdsap.taskreport.model.PostStats
import io.github.cdsap.taskreport.model.Statistics
import io.github.cdsap.taskreport.model.Stats
import org.nield.kotlinstatistics.percentile

class PostStats(val stats: Map<String, Stats>) {

    fun get(): List<PostStats> {
        if (stats.isNotEmpty()) {
            return stats.flatMap {
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
        } else {
            return emptyList()
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
}
