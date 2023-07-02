package io.github.cdsap.taskreport.output

import io.github.cdsap.taskreport.model.PostStats
import io.github.cdsap.taskreport.model.Statistics
import java.io.File

class GeneralCsvOutput(private val outcome: List<PostStats>) {

    fun write(requestedTask: String?) {
        val csv = "task_report-$requestedTask-${System.currentTimeMillis()}.csv"
        val headers =
            "task,module,executions,outcome_executed,outcome_up_to_date,outcome_from_cache,outcome_from_cache_local," +
                "outcome_from_cache_remote,${headersDuration("duration_executed")},${headersDuration("duration_up_to_date")}," +
                "${headersDuration("durations_from_cache")},${headersDuration("durations_from_cache_local")},${
                    headersDuration(
                        "durations_from_cache_local"
                    )
                }," +
                "${headersDuration("fingerprinting_executed")},${headersDuration("fingerprinting_up_to_date")}," +
                "${headersDuration("fingerprinting_from_cache")},${headersDuration("fingerprinting_from_cache_local")},${
                    headersDuration(
                        "fingerprinting_from_cache_remote"
                    )
                }\n"

        var values = ""
        outcome.forEach {
            values += "${it.name},${it.module},${it.executions.executions},${it.executions.executionsExecuted},${it.executions.executionsUpToDate},${it.executions.executionsFromCache},${it.executions.executionsFromCacheLocal},${it.executions.executionsFromCacheRemote}," +
                "${valuesDuration(it.durationsExecuted)},${valuesDuration(it.durationsFromUpToDate)},${valuesDuration(it.durationsFromCache)},${valuesDuration(it.durationsFromCacheLocal)},${valuesDuration(it.durationsFromCacheRemote)},"+
                "${valuesDuration(it.fingerprintingExecuted)},${valuesDuration(it.fingerprintingFromUpToDate)},${valuesDuration(it.fingerprintingFromCache)},${valuesDuration(it.fingerprintingFromCacheLocal)},${valuesDuration(it.fingerprintingFromCacheRemote)}\n"
        }
        File(csv).writeText("""$headers$values""".trimIndent())
        println("File $csv created")
    }

    private fun headersDuration(name: String) = "${name}_mean,${name}_p25,${name}_p50,${name}_75,${name}_p90,${name}_p99"

    private fun valuesDuration(stat: Statistics) = "${stat.mean},${stat.p25},${stat.p50},${stat.p75},${stat.p90},${stat.p99}"
}
